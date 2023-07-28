package model.rockwell;

import java.awt.Desktop;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import controller.MainViewControllerRockwell;
import javafx.stage.Stage;
import model.CsvGenerator;
import settings.Settings;

public class ModelRockwell {
	private static final int NUM_BITS_IN_DINT = 32;
	private Stage primaryStage;// View
	public static final Logger logRock = Logger.getLogger("rockwellLogger");
	private static final int MAX_NUM_COL_XLS = 15;
	private static final int NUM_BITS_IN_INT = 16;
	private static Settings properties;
	private ArrayList<DataType> listOfDataTypes = new ArrayList<DataType>();
	private ArrayList<Tag> listOfTags = new ArrayList<Tag>();
	private ArrayList<Machine> listOfMachines = new ArrayList<Machine>();
	private ArrayList<String> listOfStringProgram = new ArrayList<String>();
	private MainViewControllerRockwell controller;
	private CsvGenerator csvGenerator;

	public ModelRockwell(Stage _primaryStage) {
		primaryStage = _primaryStage;
		readProperties();
		// TODO Attenzione! il costruttore viene richiamato due volte
	}

	/**
	 * Ricerca 'ignorante' se il tag è utilizzato nel programma plc
	 * 
	 * @param tag
	 * @return
	 */
	private boolean isUsedInProgram(String tag) {
		for (String s : listOfStringProgram) {
			if (s.contains(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Carico il programma plc leggendo riga per riga
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void loadProgramStrings(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		boolean flag = false;
		while ((line = reader.readLine()) != null) {
			if (line.contains("<Programs>")) {
				flag = true;
			}
			if (line.contains("</Programs>") && flag) {
				listOfStringProgram.add(line);
				flag = false;
			}
			if (flag) {
				listOfStringProgram.add(line);
			}
		}
		reader.close();
	}

	/**
	 * Lettura iniziale del file xml generato dal progetto plc
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void readL5X(String path) throws IOException {
		XmlParser xmlParser = new XmlParser(properties);
		xmlParser.parse(listOfDataTypes, listOfTags, path);
		loadProgramStrings(path);
	}

	/**
	 * Lettura iniziale del file xml generato dal progetto plc
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void readXmlRaw(String path) throws IOException {
		XmlParser xmlParserRaw = new XmlParser(properties);
		xmlParserRaw.parseRaw(listOfTags, path);
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	public void generateXlsx(String path) throws IOException {
		findMachineNames();
		makeEntryes();
		readCommentsInTags();
		makeXml(path);
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	public void generateXmlRaw(String path) throws IOException {
		findMachineNamesRaw();
		makeEntryesRaw();
		readCommentsInTags();
		makeXml(path);
	}

	/**
	 * Leggo la lista delle macchine presenti nei tag plc ogni macchina è
	 * identificata dalla stringa precedente ai suffissi, i suffissi sono salvati
	 * nel file settings
	 */
	private void findMachineNames() {
		ArrayList<String> suffixesList = getListSuffixes();
		for (Tag tag : listOfTags) {
			String tagName = tag.getfName();
			for (String suffix : suffixesList) {
				if (tagName.contains(suffix.trim())) {
					boolean newName = true;
					String nameMachineTmpString = tagName.replace(suffix, "");
					for (Machine m : listOfMachines) {
						if (m.getfName().equals(nameMachineTmpString))
							newName = false;
					}
					if (newName) {
						listOfMachines.add(new Machine(nameMachineTmpString));
					}
				}
			}
		}
	}

	/**
	 * Leggo la lista delle macchine presenti nei tag plc ogni macchina è
	 * identificata dalla stringa precedente ai suffissi, i suffissi sono salvati
	 * nel file settings
	 */
	private void findMachineNamesRaw() {
		for (Tag tag : listOfTags) {
			String tagName = tag.getfName();
			listOfMachines.add(new Machine(tagName));
		}
	}

	/**
	 * 
	 */
	private void makeEntryes() {
		for (Machine machine : listOfMachines) {
			for (Tag tag : listOfTags) {
				if (removeSuffix(tag.getfName()).equals(machine.getfName())) {
					if (tag.getChannel(properties) == Tag.ALARMSs) {
						generateEntries(machine, tag, Tag.ALARMSs);
					} else if (tag.getChannel(properties) == Tag.READs) {
						generateEntries(machine, tag, Tag.READs);
					} else if (tag.getChannel(properties) == Tag.WRITEs) {
						generateEntries(machine, tag, Tag.WRITEs);
					} else {
						ModelRockwell.logRock
								.error("Unable to reconize the channel of the tag --- " + tag.getChannel(properties));
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void makeEntryesRaw() {
		for (Machine machine : listOfMachines) {
			for (Tag tag : listOfTags) {
				if (tag.getfName().equals(machine.getfName())) {
					generateEntries(machine, tag, Tag.RAWs);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void readCommentsInTags() {
		for (Machine machine : listOfMachines) {
			for (Tag tag : listOfTags) {
				for (Comment comment : tag.getfComments()) {
					findOperand(machine, (tag.getfName() + comment.getfOperand()).toUpperCase(),
							comment.getfDescription());
				}
			}
		}
	}

	private void makeXml(String path) {
		Workbook wb = new XSSFWorkbook();
		boolean flagImport = Boolean.parseBoolean(properties.getProperty("importAllsTags"));
		for (Machine machine : listOfMachines) {
			int index = 1;
			int indexXml = 0;
			Sheet sheet = wb.createSheet(machine.getfName());
			sheet.createFreezePane(3, 1);
			Row headerRow = sheet.createRow(0);
			makeHeaderSheetXML(headerRow);
			CellStyle styleHeader = makeStyleHeader(wb);
			for (int i = 0; i < MAX_NUM_COL_XLS; i++) {
				if (headerRow.getCell(i) != null) {
					headerRow.getCell(i).setCellStyle(styleHeader);
				}
			}
			CellStyle styleAlarms = makeStyleAlarms(wb);
			CellStyle styleReads = makeStyleReads(wb);
			CellStyle styleWrites = makeStyleWrites(wb);
			Font fontAlarms = makefontAlarms(wb);
			Font fontReads = makefontReads(wb);
			Font fontWrites = makefontWrites(wb);
			String tagSCADA = "";
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsBit()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsInt()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsDint()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsReal()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsString()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupAlarms().getAlarmsManCmd()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_Anomalies"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleAlarms, fontAlarms, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadBit()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_read"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadInt()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_read"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadDint()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Dint_read"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadReal()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Real_read"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadString()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_String_read"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupReads().getReadManCmd()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_manual_cmd"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleReads, fontReads, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteBit()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_Write"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteInt()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Int_write"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteDint()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Dint_write"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteReal()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_Real_write"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteString()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_String_write"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			indexXml = 0;
			for (EntryRockwellXls entry : machine.getFgroupWrites().getWriteManCmd()) {
				entry.setfUM(getUM(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMAX(getLimiteMax(entry.getfDescrizioneEstesa()));
				entry.setfLimiteMIN(getLimiteMin(entry.getfDescrizioneEstesa()));
				entry.setfSigla(getSigla(entry.getfDescrizioneEstesa()));
				entry.setfDescrizione(getDescription(entry.getfDescrizioneEstesa()));
				entry.setfIndice(addZeros(indexXml));
				entry.setfTipo(properties.getProperty("type_bit_manual_cmd"));
				tagSCADA = machine.getfName() + properties.getProperty(entry.getfTipo()) + addZeros(indexXml++);
				entry.setfTagNameSCADA(tagSCADA);
				if (flagImport || entry.isFlg_print())
					makeCells(index++, sheet, styleWrites, fontWrites, entry);
			}
			sheet.setColumnWidth(0, convertChars(6));// Indice
			sheet.setColumnWidth(1, convertChars(30));// Tag Scada
			sheet.setColumnWidth(2, convertChars(50));// Indirizzo Plc
			sheet.setColumnWidth(3, convertChars(25));// Tipo
			sheet.setColumnWidth(4, convertChars(6));// Sviluppo
			sheet.setColumnWidth(5, convertChars(20));// Sigla
			sheet.setColumnWidth(6, convertChars(40));// Descrizione
			sheet.setColumnWidth(7, convertChars(50));// Descrizione Estesa
			sheet.setColumnWidth(8, convertChars(10));// Limite MIN
			sheet.setColumnWidth(9, convertChars(10));// Limite MAX
			sheet.setColumnWidth(10, convertChars(6));// UM
			sheet.setColumnWidth(11, convertChars(5));// Uso del bit
			sheet.setColumnWidth(12, convertChars(10));// Commento
			sheet.setColumnWidth(13, convertChars(10));// Livello
			sheet.setColumnWidth(14, convertChars(10));// UPDATED
		}
		try {
			saveFileXls(path, wb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param _machine
	 * @param string
	 * @param sub
	 */
	private void findOperand(Machine _machine, String string, String sub) {
		boolean flagSearchIfIsUsed = Boolean.parseBoolean(properties.getProperty("flagSearchIfTagIsUsed"));
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsBit()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string)) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsInt()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsDint()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsReal()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsString()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupAlarms().getAlarmsManCmd()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadBit()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadInt()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadDint()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadReal()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadString()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupReads().getReadManCmd()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteBit()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteInt()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteDint()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteReal()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteString()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
		for (EntryRockwellXls entry : _machine.getFgroupWrites().getWriteManCmd()) {
			if (entry.getfAddrPlc().toUpperCase().equals(string.toUpperCase())) {
				entry.setfDescrizioneEstesa(sub);
				entry.setFlg_print(true);
				return;
			}
			if (flagSearchIfIsUsed && !isUsedInProgram(entry.getfAddrPlc())) {
				entry.setFlg_print(false);
			}
		}
	}

	/**
	 * @param path
	 * @param wb
	 * @throws IOException
	 */
	private void saveFileXls(String path, Workbook wb) throws IOException {
		File file = new File(getProperties().getProperty("filePath") + "\\" + path);
		if (file != null) {
			try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
				wb.write(outputStream);
			} catch (IOException ex) {
				logRock.error("Unable to save file: " + file.getAbsolutePath());
			} finally {
				wb.close();
			}
			try {
				if (properties.getProperty("openXlsAfterImport").equals("true"))
					Desktop.getDesktop().open(new File(file.getAbsolutePath()));
			} catch (Exception e) {
				logRock.error("Unable to get property: openXlsAfterImport");
			}
		}
	}

	/**
	 * @param headerRow
	 */
	private void makeHeaderSheetXML(Row headerRow) {
		headerRow.createCell(0).setCellValue("Indice");
		headerRow.createCell(1).setCellValue("Tag Scada");
		headerRow.createCell(2).setCellValue("Indirizzo Plc");
		headerRow.createCell(3).setCellValue("Tipo");
		headerRow.createCell(4).setCellValue("Sviluppo");
		headerRow.createCell(5).setCellValue("Sigla");
		headerRow.createCell(6).setCellValue("Descrizione");
		headerRow.createCell(7).setCellValue("Descrizione Estesa");
		headerRow.createCell(8).setCellValue("Limite MIN");
		headerRow.createCell(9).setCellValue("Limite MAX");
		headerRow.createCell(10).setCellValue("UM");
		headerRow.createCell(11).setCellValue("Uso del bit");
		headerRow.createCell(12).setCellValue("Commento");
		headerRow.createCell(13).setCellValue("Livello");
		headerRow.createCell(14).setCellValue("UPDATED");
	}

	/**
	 * Legge il file settings
	 */
	public static void readProperties() {
		try {
			properties = new Settings();
			properties.load(new File("properties/rockwellImporterSettings.cfg"));
		} catch (IOException e) {
			logRock.error("Unable to open properties file: properties/rockwellImporterSettings.cfg");
		}
	}

	/**
	 * @return
	 */
	public Settings getProperties() {
		return properties;
	}

	/**
	 * Lista dei suffissi presenti nel file settings sottoforma di ArrayList<String>
	 * 
	 * @return
	 */
	public static ArrayList<String> getListSuffixes() {
		ArrayList<String> stringList = new ArrayList<String>();
		String[] stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_ALARMs").trim().split(";");
		} catch (Exception e) {
			logRock.error("Unable to get property: suffixList_ALARMs");
			return null;
		}
		for (String suffix : stringsArray) {
			stringList.add(suffix.trim());
		}
		stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_READs").trim().split(";");
		} catch (Exception e) {
			logRock.error("Unable to get property: suffixList_READs");
			return null;
		}
		for (String suffix : stringsArray) {
			stringList.add(suffix.trim());
		}
		stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_WRITEs").trim().split(";");
		} catch (Exception e) {
			logRock.error("Unable to get property: suffixList_WRITEs");
			return null;
		}
		for (String suffix : stringsArray) {
			stringList.add(suffix.trim());
		}
		return stringList;
	}

	/**
	 * Ad ogni import dei tag è necessario pulire le liste presenti in memoria
	 */
	public void clearLists() {
		listOfMachines.clear();
		listOfDataTypes.clear();
		listOfTags.clear();
		listOfStringProgram.clear();
	}

	/**
	 * @param machine
	 * @param tag
	 * @param _channel
	 */
	private void generateEntries(Machine machine, Tag tag, String _channel) {
		StringBuffer sbTagNamePlc = new StringBuffer(tag.getfName());
		String dataType = tag.getDataType();
		DataType dt = null;
		for (DataType tmp_dt : listOfDataTypes) {
			if (tmp_dt.getfName().equals(dataType)) {
				dt = tmp_dt;
				break;
			}
		}
		if (dt == null) {
			logRock.error("Unable to find dataType: " + dataType);
			return;
		}

		// per ogni elemento da cui è composto il tag
		String dimensionTagString = tag.getfDimensions();
		int dimensionTagInt = 1;
		if (!dimensionTagString.equals("")) {
			dimensionTagInt = Integer.parseInt(dimensionTagString);
			if (dimensionTagInt == 0) {
				dimensionTagInt = 1;
			}
		}
		for (int indexDimensionsTag = 0; indexDimensionsTag < dimensionTagInt; indexDimensionsTag++) {
			// per ogni membro da cui è composto il tipo di dato
			for (Member m : dt.getfMembers()) {
				sbTagNamePlc.delete(0, sbTagNamePlc.length());
				if (!dimensionTagString.equals("")) {
					sbTagNamePlc.append(tag.getfName() + "[" + indexDimensionsTag + "]");
				} else {
					sbTagNamePlc.append(tag.getfName());
				}
				if (m.getfDataType().equals("BIT")) {
					generateEntryXls(machine, sbTagNamePlc, m, "BIT", _channel);
				} else if (m.getfDataType().equals("INT")) {
					generateEntryXls(machine, sbTagNamePlc, m, "INT", _channel);
				} else if (m.getfDataType().equals("SINT")) {
					// generateEntryXls(machine, sbTagNamePlc, m, "INT", _channel);
					// TODO ma va bene cosi???
//					 logRock.info("Type SINT ignored");
				} else if (m.getfDataType().equals("DINT")) {
					generateEntryXls(machine, sbTagNamePlc, m, "DINT", _channel);
				} else if (m.getfDataType().equals("REAL")) {
					generateEntryXls(machine, sbTagNamePlc, m, "REAL", _channel);
				} else if (m.getfDataType().equals("STRING")) {
					generateEntryXls(machine, sbTagNamePlc, m, "STRING", _channel);
				} else {
					// se il membro non è composto da titpi di dato semplici
					generateMember(machine, m, sbTagNamePlc, _channel);
				}
			}
		}
	}

	/**
	 * @param machine
	 * @param sbTagNamePlc
	 * @param m
	 * @param _type
	 * @param _channel
	 */
	private void generateEntryXls(Machine machine, StringBuffer sbTagNamePlc, Member m, String _type, String _channel) {
		String descriptionTagPlc;
		EntryRockwellXls entry;
		int dimension = m.getfDimension();
		if (dimension == 0) {
			StringBuffer _sb = new StringBuffer();
			_sb = new StringBuffer(sbTagNamePlc);
			_sb.append("." + m.getfName());
//			ModelRockwell.logRock.info("_sb: "+_sb);
//			//TODO Attenzione!!
//			if (_sb.toString().contains("ZZZZ") )return;
			descriptionTagPlc = m.getfDescription();
			boolean flag_print = true;
			if (_channel.contentEquals(Tag.ALARMSs))
				flag_print = false;
			if (!checkifVector(machine, _sb, m, _type, _channel)) {
				entry = new EntryRockwellXls("", "", _sb.toString(), _type, 0, "", "", descriptionTagPlc, "", "", "",
						"", "", "", "", flag_print);
				insertEntryXls(machine, entry, _type, _channel);
			}
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < dimension; i++) {
				sb = new StringBuffer(sbTagNamePlc);
				sb.append("." + m.getfName() + "[" + i + "]");
//				//TODO Attenzione!!
//				if (sb.toString().contains("ZZZZ") )return;
				descriptionTagPlc = m.getfDescription();
				boolean flag_print = true;
				if (_channel.contentEquals(Tag.ALARMSs))
					flag_print = false;
				if (!checkifVector(machine, sb, m, _type, _channel)) {
					entry = new EntryRockwellXls("", "", sb.toString(), _type, 0, "", "", descriptionTagPlc, "", "", "",
							"", "", "", "", flag_print);
					insertEntryXls(machine, entry, _type, _channel);
				}
				sb.delete(0, sb.length());
			}
		}
	}

	/**
	 * @param machine
	 * @param sbTagNamePlc
	 * @param m
	 * @param _type
	 * @param _channel
	 * @return
	 */
	private boolean checkifVector(Machine machine, StringBuffer sbTagNamePlc, Member m, String _type, String _channel) {
		String descriptionTagPlc;
		EntryRockwellXls entry;
		if ((m.getfName().startsWith("rgx") || m.getfName().startsWith("x")) && (_type.equals("DINT"))) {
			for (int i = 0; i < NUM_BITS_IN_DINT; i++) {
				StringBuffer _sb = new StringBuffer();
				_sb = new StringBuffer(sbTagNamePlc);
				_sb.append("." + i);
				descriptionTagPlc = m.getfDescription();
				boolean flag_print = true;
				if (((m.getfName().startsWith("rgx") || m.getfName().startsWith("x"))
						&& m.getfDataType().equals("DINT")) || _channel.contentEquals(Tag.ALARMSs))
					flag_print = false;
				entry = new EntryRockwellXls("", "", _sb.toString(), "BIT", 0, "", "", descriptionTagPlc, "", "", "",
						"", "", "", "", flag_print);
				insertEntryXls(machine, entry, "BIT", _channel);
			}
			return true;
		} else if ((m.getfName().startsWith("rgx") || m.getfName().startsWith("x")) && (_type.equals("INT"))) {
			for (int i = 0; i < NUM_BITS_IN_INT; i++) {
				StringBuffer _sb = new StringBuffer();
				_sb = new StringBuffer(sbTagNamePlc);
				_sb.append("." + i);
				descriptionTagPlc = m.getfDescription();
				boolean flag_print = true;
				if (((m.getfName().startsWith("rgx") || m.getfName().startsWith("x"))
						&& m.getfDataType().equals("DINT")) || _channel.contentEquals(Tag.ALARMSs))
					flag_print = false;
				entry = new EntryRockwellXls("", "", _sb.toString(), "BIT", 0, "", "", descriptionTagPlc, "", "", "",
						"", "", "", "", flag_print);
				insertEntryXls(machine, entry, "BIT", _channel);
				// TODO COSA VUOL DIRE????
			}
			return true;// TODO COSA VUOL DIRE????
		}
		return false;
	}

	/**
	 * @param machine
	 * @param entry
	 * @param _type
	 * @param _channel
	 */
	private void insertEntryXls(Machine machine, EntryRockwellXls entry, String _type, String _channel) {
		if (entry.getfAddrPlc().contains("ZZZZ"))
			return;
		if (_channel.contentEquals(Tag.ALARMSs)) {
			if (_type.equals("BIT")) {
				machine.getFgroupAlarms().getAlarmsBit().add(entry);
			} else if (_type.equals("SINT")) {
				machine.getFgroupAlarms().getAlarmsInt().add(entry);
			} else if (_type.equals("INT")) {
				machine.getFgroupAlarms().getAlarmsInt().add(entry);
			} else if (_type.equals("DINT")) {
				machine.getFgroupAlarms().getAlarmsDint().add(entry);
			} else if (_type.equals("REAL")) {
				machine.getFgroupAlarms().getAlarmsReal().add(entry);
			} else if (_type.equals("STRING")) {
				machine.getFgroupAlarms().getAlarmsString().add(entry);
			}
		} else if (_channel.contentEquals(Tag.READs)) {
			if (_type.equals("BIT")) {
				machine.getFgroupReads().getReadBit().add(entry);
			} else if (_type.equals("SINT")) {
				machine.getFgroupReads().getReadInt().add(entry);
			} else if (_type.equals("INT")) {
				machine.getFgroupReads().getReadInt().add(entry);
			} else if (_type.equals("DINT")) {
				machine.getFgroupReads().getReadDint().add(entry);
			} else if (_type.equals("REAL")) {
				machine.getFgroupReads().getReadReal().add(entry);
			} else if (_type.equals("STRING")) {
				machine.getFgroupReads().getReadString().add(entry);
			}
		} else if (_channel.contentEquals(Tag.WRITEs)) {
			if (_type.equals("BIT")) {
				machine.getFgroupWrites().getWriteBit().add(entry);
			} else if (_type.equals("SINT")) {
				machine.getFgroupWrites().getWriteInt().add(entry);
			} else if (_type.equals("INT")) {
				machine.getFgroupWrites().getWriteInt().add(entry);
			} else if (_type.equals("DINT")) {
				machine.getFgroupWrites().getWriteDint().add(entry);
			} else if (_type.equals("REAL")) {
				machine.getFgroupWrites().getWriteReal().add(entry);
			} else if (_type.equals("STRING")) {
				machine.getFgroupWrites().getWriteString().add(entry);
			}
		}
	}

	/**
	 * @param machine
	 * @param _m
	 * @param sbTagName
	 * @param _channel
	 */
	private void generateMember(Machine machine, Member _m, StringBuffer sbTagName, String _channel) {
		String dataType = _m.getfDataType();
		sbTagName.append("." + _m.getfName());
		DataType dt = null;
		// siccome il membro è composto da un dataType, cerco il tipo di dato nella
		// lista DataType
		for (DataType tmp_dt : listOfDataTypes) {
			if (tmp_dt.getfName().equals(dataType)) {
				dt = tmp_dt;
				break;
			}
		}
		if (dt == null)
			return;

		String orig = sbTagName.toString();
		int dimensionTagInt = _m.getfDimension();
		if (dimensionTagInt == 0)
			dimensionTagInt = 1;
		// per ogni elemento da cui è composto il membro (potrebbe essere un vettore)
		for (int dimensionsTag = 0; dimensionsTag < dimensionTagInt; dimensionsTag++) {
			sbTagName.delete(0, sbTagName.length());
			sbTagName.append(orig);
			if (_m.getfDimension() != 0)
				sbTagName.append("[" + dimensionsTag + "]");
			// per ogni membro da cui è composto il tipo di dato su cui sto ricorrendo
			for (Member m : dt.getfMembers()) {
				if (m.getfDataType().equals("BIT")) {
					generateEntryXls(machine, sbTagName, m, "BIT", _channel);
				} else if (m.getfDataType().equals("INT")) {
					generateEntryXls(machine, sbTagName, m, "INT", _channel);
				} else if (m.getfDataType().equals("SINT")) {
					generateEntryXls(machine, sbTagName, m, "INT", _channel);
				} else if (m.getfDataType().equals("DINT")) {
					generateEntryXls(machine, sbTagName, m, "DINT", _channel);
				} else if (m.getfDataType().equals("REAL")) {
					generateEntryXls(machine, sbTagName, m, "REAL", _channel);
				} else if (m.getfDataType().equals("STRING")) {
					generateEntryXls(machine, sbTagName, m, "STRING", _channel);
				} else {
					// "ricorro"
					generateMember(machine, m, sbTagName, _channel);
				}
			}
		}
	}

	/**
	 * @param getfName
	 * @return
	 */
	private String removeSuffix(String getfName) {
		String string;
		ArrayList<String> suffixesList = getListSuffixes();
		for (String suffix : suffixesList) {
			if (getfName.contains(suffix)) {
				string = getfName.replace(suffix, "");
				return string;
			}
		}
		return null;
	}

	/**
	 * @param comment
	 * @return
	 */
	private String getUM(String comment) {
		if (comment != null) {
			String str1, str2;
			comment = "" + comment.trim();
			if (comment.contains("{")) {
				comment = comment.split("\\{")[1];
				if (comment.contains("}")) {
					comment = comment.split("\\}")[0];
				}
				if (comment.split("\\|")[0].equals("X") || comment.split("\\|")[0].equals("x")) {
					str1 = "";
				} else {
					str1 = "[" + comment.split("\\|")[0].trim() + "]";
				}
				if (comment.split("\\|")[1].equals("X") || comment.split("\\|")[1].equals("x")) {
					str2 = "";
				} else {
					str2 = " [" + comment.split("\\|")[1].trim() + "]";
				}
				return str1 + str2;
			} else if (comment.contains("[")) {
				comment = comment.split("\\[")[1];
				if (comment.endsWith("]")) {
					comment = "" + comment.substring(0, comment.length() - 1);
				}
				return "[" + comment + "]";
			}
		}
		return "";
	}

	/**
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getLimiteMax(String getfDescrizioneEstesa) {
		if (getfDescrizioneEstesa != null) {
			if (getfDescrizioneEstesa.contains("{")) {
				getfDescrizioneEstesa = getfDescrizioneEstesa.split("\\{")[1].trim();
				if (getfDescrizioneEstesa.contains("}")) {
					getfDescrizioneEstesa = getfDescrizioneEstesa.split("\\}")[0].trim();
				}
				if (getfDescrizioneEstesa.split("\\|")[3].equals("X")
						|| getfDescrizioneEstesa.split("\\|")[3].equals("x")) {
					return "99999999";
				}
				return getfDescrizioneEstesa.split("\\|")[3];
			}
		}
		return "99999999";
	}

	/**
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getLimiteMin(String getfDescrizioneEstesa) {
		if (getfDescrizioneEstesa != null) {
			if (getfDescrizioneEstesa.contains("{")) {
				getfDescrizioneEstesa = getfDescrizioneEstesa.split("\\{")[1];
				if (getfDescrizioneEstesa.contains("}")) {
					getfDescrizioneEstesa = getfDescrizioneEstesa.split("\\}")[0];
				}
				if (getfDescrizioneEstesa.split("\\|")[2].equals("X")
						|| getfDescrizioneEstesa.split("\\|")[2].equals("x")) {
					return "-99999999";
				}
				return getfDescrizioneEstesa.split("\\|")[2];
			}
		}
		return "-99999999";
	}

	/**
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getSigla(String getfDescrizioneEstesa) {
		if (getfDescrizioneEstesa != null) {
			String[] tokens = getfDescrizioneEstesa.trim().split("\\.");
			int index = tokens.length - 1;
			return removeSuffix(removeSuffix(getfDescrizioneEstesa, tokens[index].trim()).trim(), ".");
		}
		return "";
	}

	/**
	 * @param s
	 * @param suffix
	 * @return
	 */
	public static String removeSuffix(final String s, final String suffix) {
		if (s != null && suffix != null && s.endsWith(suffix)) {
			return s.substring(0, s.length() - suffix.length());
		}
		return s;
	}

	/**
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getDescription(String getfDescrizioneEstesa) {
		if (getfDescrizioneEstesa != null) {
			String[] tokens = getfDescrizioneEstesa.trim().split("\\.");
			int index = tokens.length - 1;
			return tokens[index].trim();
		}
		return "";
	}

	/**
	 * @param i
	 * @return
	 */
	private String addZeros(int i) {
		if (i < 10) {
			return "000" + i;
		}
		if (i < 100) {
			return "00" + i;
		}
		if (i < 1000) {
			return "0" + i;
		}
		return "" + i;
	}

	/**
	 * @param chars
	 * @return
	 */
	private int convertChars(int chars) {
		return chars * 256;
	}

	/**
	 * @param _index
	 * @param _sheet
	 * @param _style
	 * @param _font
	 * @param _entry
	 */
	private void makeCells(int _index, Sheet _sheet, CellStyle _style, Font _font, EntryRockwellXls _entry) {
		Row rowGen = populateCells(_index, _sheet, _entry);
		_style.setFont(_font);
		for (int i = 0; i < MAX_NUM_COL_XLS; i++) {
			if (rowGen.getCell(i) != null) {
				rowGen.getCell(i).setCellStyle(_style);
			}
		}
	}

	/**
	 * @param index
	 * @param sheet
	 * @param entry
	 * @return
	 */
	private Row populateCells(int index, Sheet sheet, EntryRockwellXls entry) {
		Row rowGen = sheet.createRow(index);
		rowGen.createCell(0).setCellValue(entry.getfIndice());
		rowGen.createCell(1).setCellValue(entry.getfTagNameSCADA());
		rowGen.createCell(2).setCellValue(entry.getfAddrPlc());
		rowGen.createCell(3).setCellValue(entry.getfTipo());
		rowGen.createCell(4).setCellValue(entry.getfSviluppo());
		rowGen.createCell(5).setCellValue(entry.getfSigla());
		rowGen.createCell(6).setCellValue(entry.getfDescrizione());
		rowGen.createCell(7).setCellValue(entry.getfDescrizioneEstesa());
		rowGen.createCell(8).setCellValue(entry.getfLimiteMIN());
		rowGen.createCell(9).setCellValue(entry.getfLimiteMAX());
		rowGen.createCell(10).setCellValue(entry.getfUM());
		rowGen.createCell(11).setCellValue(entry.getfUsoDelBit());
		rowGen.createCell(12).setCellValue(entry.getfCommento());
		rowGen.createCell(13).setCellValue(entry.getfLivello());
		rowGen.createCell(14).setCellValue(entry.getfUpdated());
		return rowGen;
	}

	/**
	 * @param wb
	 * @return
	 */
	private CellStyle makeStyleHeader(Workbook wb) {
		CellStyle styleHeader = wb.createCellStyle();
		styleHeader.setBorderBottom(BorderStyle.MEDIUM);
		styleHeader.setBorderTop(BorderStyle.THIN);
		styleHeader.setBorderRight(BorderStyle.THIN);
		styleHeader.setBorderLeft(BorderStyle.THIN);
		return styleHeader;
	}

	/**
	 * Crea lo stile per le celle relative ai tag tipo write nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private CellStyle makeStyleWrites(Workbook wb) {
		CellStyle styleWrites = wb.createCellStyle();
		styleWrites.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		styleWrites.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWrites.setBorderBottom(BorderStyle.THIN);
		styleWrites.setBorderTop(BorderStyle.THIN);
		styleWrites.setBorderRight(BorderStyle.THIN);
		styleWrites.setBorderLeft(BorderStyle.THIN);
		return styleWrites;
	}

	/**
	 * Crea lo stile per le celle relative ai tag tipo read nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private CellStyle makeStyleReads(Workbook wb) {
		CellStyle styleReads = wb.createCellStyle();
		styleReads.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		styleReads.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleReads.setBorderBottom(BorderStyle.THIN);
		styleReads.setBorderTop(BorderStyle.THIN);
		styleReads.setBorderRight(BorderStyle.THIN);
		styleReads.setBorderLeft(BorderStyle.THIN);
		return styleReads;
	}

	/**
	 * Crea lo stile per le celle relative ai tag tipo alarm nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private CellStyle makeStyleAlarms(Workbook wb) {
		CellStyle styleAlarms = wb.createCellStyle();
		styleAlarms.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		styleAlarms.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleAlarms.setBorderBottom(BorderStyle.THIN);
		styleAlarms.setBorderTop(BorderStyle.THIN);
		styleAlarms.setBorderRight(BorderStyle.THIN);
		styleAlarms.setBorderLeft(BorderStyle.THIN);
		return styleAlarms;
	}

	/**
	 * Crea il font per le celle relative ai tag tipo write nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private Font makefontWrites(Workbook wb) {
		Font fontWrites = wb.createFont();
		fontWrites.setFontHeightInPoints((short) 10);
		fontWrites.setFontName("Arial");
		fontWrites.setColor(IndexedColors.BLACK.getIndex());
		fontWrites.setBold(false);
		fontWrites.setItalic(false);
		return fontWrites;
	}

	/**
	 * Crea il font per le celle relative ai tag tipo read nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private Font makefontReads(Workbook wb) {
		Font fontReads = wb.createFont();
		fontReads.setFontHeightInPoints((short) 10);
		fontReads.setFontName("Arial");
		fontReads.setColor(IndexedColors.BLACK.getIndex());
		fontReads.setBold(false);
		fontReads.setItalic(false);
		return fontReads;
	}

	/**
	 * Crea il font per le celle relative ai tag tipo alarm nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private Font makefontAlarms(Workbook wb) {
		Font fontAlarms = wb.createFont();
		fontAlarms.setFontHeightInPoints((short) 10);
		fontAlarms.setFontName("Arial");
		fontAlarms.setColor(IndexedColors.RED.getIndex());
		fontAlarms.setBold(false);
		fontAlarms.setItalic(false);
		return fontAlarms;
	}

	/**
	 * @param stringArray
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String csvWriterOneByOne(List<String[]> stringArray, String path) throws Exception {
		CSVWriter writer = new CSVWriter(new FileWriter(path));
		for (String[] array : stringArray) {
			writer.writeNext(array);
		}
		writer.close();
		return "";
	}

	/**
	 * @param absolutePath
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 */
	public void readXsl(String absolutePath) throws EncryptedDocumentException, IOException {
		Workbook workbook = null;
		Iterator<Sheet> sheetIterator = null;
		workbook = WorkbookFactory.create(new File(absolutePath));
		sheetIterator = workbook.sheetIterator();
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			if (!sheet.getSheetName().equals("Parametri") && !sheet.getSheetName().equals("Intestazione")
					&& !sheet.getSheetName().equals("Sommario") && !sheet.getSheetName().equals("ZONA_MACCHINA")) {
				DataFormatter dataFormatter = new DataFormatter();
				Iterator<Row> rowIterator = sheet.rowIterator();
				Row row = rowIterator.next();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();// salto l'intestazione
					Iterator<Cell> cellIterator = row.cellIterator();
					String[] a = new String[15];
					int i = 0;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (cell == null) {
							a[i++] = "";
						} else {
							a[i++] = dataFormatter.formatCellValue(cell);
						}
					}
					EntryRockwellXls entry = new EntryRockwellXls(a[0].trim(), a[1].trim(), a[2].trim(), a[3].trim(),
							Integer.parseInt(a[4].trim()), a[5].trim(), a[6].trim(), a[7].trim(), a[8].trim(),
							a[9].trim(), a[10].trim(), a[11].trim(), a[12].trim(), a[13].trim(), a[14].trim(),
							Boolean.parseBoolean(a[14].trim()));
					if (entry.getfTipo().trim().equals("bit_Anomalies<DA_BIT>")) {
						getCsvGenerator().getListEntry_DA().add(entry);
					} else if (entry.getfTipo().trim().equals("Int_Anomalies<AA_INT>")) {
						getCsvGenerator().getListEntry_AI().add(entry);// per ora li metto in AI e non AA
					} else if (entry.getfTipo().trim().equals("bit_read<DI_BIT>")
							|| entry.getfTipo().trim().equals("bit_manual_cmd<WDI_BIT>")
							|| entry.getfTipo().trim().equals("bit_manual_cmd<DI_BIT>")
							|| entry.getfTipo().trim().equals("bit_Write<WDI_BIT>")) {
						getCsvGenerator().getListEntry_DI().add(entry);
					} else if (entry.getfTipo().trim().equals("String_write<WTX_STRING>")
							|| entry.getfTipo().trim().equals("Dint_read<AI_DINT>")
							|| entry.getfTipo().trim().equals("Int_write<WAI_INT>")
							|| entry.getfTipo().trim().equals("String_read<TX_STRING>")
							|| entry.getfTipo().trim().equals("Real_write<WAI_REAL>")
							|| entry.getfTipo().trim().equals("Dint_write<WAI_DINT>")
							|| entry.getfTipo().trim().equals("Int_read<AI_INT>")
							|| entry.getfTipo().trim().equals("Real_read<AI_REAL>")) {
						getCsvGenerator().getListEntry_AI().add(entry);
					}
					// TODO aggiungere gli altri casi, e controllare questi
				}
			}
		}
	}

	public void setController(MainViewControllerRockwell mainController) {
		controller = mainController;
	}

	/**
	 * @return the csvGenerator
	 */
	public CsvGenerator getCsvGenerator() {
		if (csvGenerator == null) {
			csvGenerator = new CsvGenerator(properties, logRock, controller);
		}
		return csvGenerator;
	}

}
