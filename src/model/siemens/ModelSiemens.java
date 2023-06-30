package model.siemens;

import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import controller.MainViewControllerSiemens;
import javafx.stage.Stage;
import model.CsvGenerator;
import model.EntryXlsx;
import model.siemens.items.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import settings.Settings;

public class ModelSiemens {

	/** ###### FIELDS ###### */
	private static List<DB> dblist;
	private static List<ItemStruct> udtlist;
	private static DB tmpDbBlock;
	public static final Logger logSiem = Logger.getLogger("siemensLogger");
	static BufferedReader br;
	private static final int MAX_NUM_COL_XLS = 15;
	private static int tmp_num_db = 0;

	private static Address gAddr = new Address();
	static Settings properties;

	List<String> listOfScaleMul_1000000;
	List<String> listOfScaleMul_100000;
	List<String> listOfScaleMul_10000;
	List<String> listOfScaleMul_1000;
	List<String> listOfScaleMul_100;
	List<String> listOfScaleMul_10;
	List<String> listOfScaleDiv_1000000;
	List<String> listOfScaleDiv_100000;
	List<String> listOfScaleDiv_10000;
	List<String> listOfScaleDiv_1000;
	List<String> listOfScaleDiv_100;
	List<String> listOfScaleDiv_10;

	protected CellStyle style;
	protected Font font;
	private MainViewControllerSiemens controller;
	private CsvGenerator csvGenerator;
	private static HashMap<String, String> PLCTags;
	private static SimpleSiemensType lastSimpleType;

	/** ###### CONSTRUCTORS ###### */
	public ModelSiemens(Stage _primaryStage) {
		this.dblist = new ArrayList<DB>();
		this.udtlist = new ArrayList<ItemStruct>();
		this.PLCTags = new HashMap<String, String>();
		this.udtlist = new ArrayList<ItemStruct>();
		readProperties();

		// TODO Attenzione! il costruttore viene richiamato due volte
	}

	public static boolean readDBFile(File f) throws IOException, CloneNotSupportedException {
//		ModelSiemens.logSiem.info("readDBFile" + f.getName());
		readProperties();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = br.readLine();
		Settings dbSettings = new Settings();

		if (!readDBIndex(f, dbSettings, "indexDBs.cfg"))
			return false;

		readPLCTags(f, "PLCTags.xlsx");

		while (line != null) {
			if (line.contains("DATA_BLOCK") && !line.contains("END_DATA_BLOCK") && line != null) {
//				ModelSiemens.logSiem.info("#### DATA BLOCK ####" + " Data Block parsing: " + line);
//				ModelSiemens.logSiem.info("Data Block parsing: " + line);
				if (!parseDataBlock(line, dbSettings)) {
					return false;
				}
			}

			if (line.contains("TYPE") && !line.contains("END_TYPE") && line != null) {
//				ModelSiemens.logSiem.info("#### TYPE ####" + " Type parsing: " + line);
				if (!parseType(line)) {
					return false;
				}
			}
			line = br.readLine();
		}
		br.close();
		return true;
	}

	/**
	 * @param f
	 * @param filePLCTagsName
	 */
	private static void readPLCTags(File f, String filePLCTagsName) {
		File file2 = new File(f.getParentFile().getAbsolutePath() + "/" + filePLCTagsName);
		if (file2.exists() && !file2.isDirectory()) {
			loadPlcTags(f.getParentFile().getAbsolutePath() + "/" + filePLCTagsName);
		} else {
			ModelSiemens.logSiem.error("File " + filePLCTagsName + " NON trovato! " + f.getName());
		}
	}

	/**
	 * @param f
	 * @param dbSettings
	 * @param fileIndexesDBs
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static boolean readDBIndex(File f, Settings dbSettings, String fileIndexesDBs)
			throws FileNotFoundException, IOException {
		File file = new File(f.getParentFile().getAbsolutePath() + "/" + fileIndexesDBs);
		if (file.exists() && !file.isDirectory()) {
			dbSettings.load(file);
		} else {
			ModelSiemens.logSiem.error("File " + fileIndexesDBs + " NON trovato! " + f.getName());
			br.close();
			return false;
		}
		return true;
	}

	/**
	 * @param line
	 * @return
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	private static boolean parseType(String line) throws IOException, CloneNotSupportedException {
		getgAddr().resetAddress();
		ItemStruct tmp_type = new ItemStruct(line.split("TYPE")[1].trim(), "", line.split("TYPE")[1].trim(), "",
				new Address(0, ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()), null, 0, 0);
//		ModelSiemens.logSiem.info("Type title: " + tmp_type.getName());
		while (!line.contains("STRUCT")) {
			line = br.readLine();
		}
//		ModelSiemens.logSiem.info("Linea letta TYPE: " + line);
		readLineRecursive(br, tmp_type, true);
		tmp_type.setSize(getgAddr().gByte(), getgAddr().gBit());
		ModelSiemens.udtlist.add(tmp_type);
		return true;
	}

	/**
	 * @param line
	 * @param dbSettings
	 * @return
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	private static boolean parseDataBlock(String line, Settings dbSettings)
			throws IOException, CloneNotSupportedException {

		line = line.split("DATA_BLOCK")[1].replace("\"", " ").trim();
		if (isNumeric(dbSettings.getProperty(line))) {
			tmp_num_db = Integer.parseInt(dbSettings.getProperty(line));
		} else {
			ModelSiemens.logSiem.error("DB non trovata!");
			return false;
		}
		tmpDbBlock = new DB(tmp_num_db, "", line, 0.0f);
		ModelSiemens.getgAddr().resetAddress();
		ModelSiemens.getgAddr().setDB(tmp_num_db);
		dblist.add(tmpDbBlock);
		while ((!line.contains("STRUCT") && !line.contains("VAR")) && line != null) {
			line = br.readLine();
		}
		readLineRecursive(br, tmpDbBlock.getMainStruct(), false);
		return true;
	}

	private static void loadPlcTags(String f) {
//		ModelSiemens.logSiem.info("start method: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		Workbook workbook = null;
		Iterator<Sheet> sheetIterator = null;

		try {
			workbook = WorkbookFactory.create(new File(f));
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sheetIterator = workbook.sheetIterator();

		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			if (sheet.getSheetName().equals("Constants")) {
//				ModelSiemens.logSiem.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": constants");
				DataFormatter dataFormatter = new DataFormatter();
				Iterator<Row> rowIterator = sheet.rowIterator();
				Row row = rowIterator.next();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					String[] cells = new String[10];
					int i = 0;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (cell == null) {
							cells[i] = "";
							i++;
						} else {
							cells[i] = dataFormatter.formatCellValue(cell);
							i++;
						}
					}
					ModelSiemens.getPLCTags().put(cells[0], cells[3]);
				}
			}
		}
		return;
	}

	public static void iterateUsingEntrySet(Map<String, String> map) {
		ModelSiemens.logSiem.info("start method: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		int k = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			ModelSiemens.logSiem.info(k + ": " + entry.getKey() + ":" + entry.getValue());
			k++;
		}
	}

	private static void readLineRecursive(BufferedReader br, ItemStruct workingStruct, boolean flag)
			throws IOException, CloneNotSupportedException {

		String lineReaded = "";

		while (lineReaded != null) {
			lineReaded = br.readLine();
			lineReaded = removeInitialBrackets(lineReaded);
			if (lineReaded == null || lineReaded.contains("END_TYPE") || lineReaded.contains("END_DATA_BLOCK")
					|| lineReaded.contains("END_STRUCT") || lineReaded.contains("END_VAR")) {
//				logSiem.debug("end recursion.");
				return;
			}
			String line = removeInitialBrackets(lineReaded);
			line = removeComment(line);
			if (line.split(":").length > 1 && line.split(":")[1].contains("Array[")) {
				manageArray(workingStruct, lineReaded, flag);
			} else if (line.toLowerCase().contains("struct")) {
				manageStructSimple(br, workingStruct, lineReaded, flag);
			} else {
				manageTypeSimple(workingStruct, lineReaded, flag);
			}
		}
	}

	private static void manageTypeSimple(ItemStruct workingStruct, String lineReaded, boolean flag)
			throws CloneNotSupportedException {

		String str = lineReaded.split("//")[0].replaceAll("\\{.*?\\}", "").trim().replaceAll(" +", " ");
		if (str.split(":")[1].contains("Bool;")) {
			manageBool(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.BOOL);
			lastSimpleType = SimpleSiemensType.BOOL;
		} else if (str.split(":")[1].contains("Int;") && !str.split(":")[1].contains("DInt;")
				&& !str.split(":")[1].contains("LInt;") && !str.split(":")[1].contains("SInt;")
				&& !str.split(":")[1].contains("UInt;")) {
			manageInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.INT);
			lastSimpleType = SimpleSiemensType.INT;
		} else if (str.split(":")[1].contains("UInt;")) {
			manageUInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.UINT);
			lastSimpleType = SimpleSiemensType.UINT;
		} else if (str.split(":")[1].contains("S5Time;")) {
			manageS5Time(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.S5TIME);
			lastSimpleType = SimpleSiemensType.S5TIME;
		} else if (str.split(":")[1].contains("Date_And_Time;")) {
			manageDateAndTime(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.DATEANDTIME);
			lastSimpleType = SimpleSiemensType.DATEANDTIME;
		} else if (str.split(":")[1].contains("Time_Of_Day;")) {
			manageTimeOfDay(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.TIMEOFDAY);
			lastSimpleType = SimpleSiemensType.TIMEOFDAY;
		} else if (str.split(":")[1].contains("Date;")) {
			manageDate(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.DATE);
			lastSimpleType = SimpleSiemensType.DATE;
		} else if (str.split(":")[1].contains("ULInt;")) {
			manageULInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.ULINT);
			lastSimpleType = SimpleSiemensType.ULINT;
		} else if (str.split(":")[1].contains("LInt;")) {
			manageLInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.LINT);
			lastSimpleType = SimpleSiemensType.LINT;
		} else if (str.split(":")[1].contains("USInt;")) {
			manageUSInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.USINT);
			lastSimpleType = SimpleSiemensType.USINT;
		} else if (str.split(":")[1].contains("SInt;")) {
			manageSInt(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.SINT);
			lastSimpleType = SimpleSiemensType.SINT;
		} else if (str.split(":")[1].contains("LReal;")) {
			manageLReal(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.LREAL);
			lastSimpleType = SimpleSiemensType.LREAL;
		} else if (str.split(":")[1].contains("LTime;")) {
			manageLTime(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.LTIME);
			lastSimpleType = SimpleSiemensType.LTIME;
		} else if (str.split(":")[1].contains("LWord;")) {
			manageLWord(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.LWORD);
			lastSimpleType = SimpleSiemensType.LWORD;
		} else if (str.split(":")[1].contains("UDInt;")) {
			manageUDint(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.UDINT);
			lastSimpleType = SimpleSiemensType.UDINT;
		} else if (str.split(":")[1].contains("DInt;")) {
			manageDint(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.DINT);
			lastSimpleType = SimpleSiemensType.DINT;
		} else if (str.split(":")[1].contains("Real;")) {
			manageReal(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.REAL);
			lastSimpleType = SimpleSiemensType.REAL;
		} else if (str.split(":")[1].contains("Byte;")) {
			manageByte(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.BYTE);
			lastSimpleType = SimpleSiemensType.BYTE;
		} else if (str.split(":")[1].contains("Char;")) {
			manageChar(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.BYTE);
			lastSimpleType = SimpleSiemensType.BYTE;
		} else if (str.split(":")[1].contains("DWord;")) {
			manageDWord(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.DWORD);
			lastSimpleType = SimpleSiemensType.DWORD;
		} else if (str.split(":")[1].contains("Time;")) {
			manageTime(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.TIME);
			lastSimpleType = SimpleSiemensType.TIME;
		} else if (str.split(":")[1].contains("Word;") && !str.split(":")[1].contains("DWord;")) {
			manageWord(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.WORD);
			lastSimpleType = SimpleSiemensType.WORD;
		} else if (str.split(":")[1].contains("IEC_TIMER;")) {
			manageIEC_TIMER(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.IEC_TIMER);
			lastSimpleType = SimpleSiemensType.IEC_TIMER;
		} else if (str.split(":")[1].contains("String;")) {
			manageString(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.STRING);
			lastSimpleType = SimpleSiemensType.STRING;
		} else if (str.split(":")[1].contains("String[")) {
			manageStringInArrayForm(workingStruct, lineReaded, lastSimpleType != SimpleSiemensType.STRING);
			lastSimpleType = SimpleSiemensType.STRING;
		} else if (typeExist(lineReaded.split(":")[1].replace(";", "").trim())) {
			if (flag)
				manageUDT_Type(workingStruct, lineReaded);
			else {
				manageUDT_DataBlock(workingStruct, lineReaded);
			}
		} else {
			logSiem.error("Tipo non riconosciuto! " + lineReaded + " -> " + lineReaded.split(":")[1]);
		}
	}

	private static boolean typeExist(String string) {
		for (ItemStruct t : ModelSiemens.udtlist) {
			if ((t.getTitleUDT().trim()).equals(string.trim())) {
				return true;
			}
		}
		return false;
	}

	private static void manageDWord(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageDWord");
		ItemDWord item = (ItemDWord) ItemDWord.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageTime(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageTime");
		ItemTime item = (ItemTime) ItemTime.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageWord(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageWordi");
		ItemWord item = (ItemWord) ItemWord.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageUDT_Type(ItemStruct workingStruct, String lineReaded) throws CloneNotSupportedException {
//		logSiem.info("manageUDT_Type");
		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		String str = removeInitialBrackets(lineReaded);
		str = removeComment(str);
		str = getTypeString(str);

		ItemStruct itemUDT = (ItemStruct) getUDTFromName(str, getNameItemFromStringLine(lineReaded), workingStruct)
				.clone();
		itemUDT.updateName(getNameItemFromStringLine(lineReaded));
		itemUDT.setUpType();
		itemUDT.addAddresRec(gAddr);
		ModelSiemens.getgAddr().incrementAddress(itemUDT.getByteSize(), itemUDT.getBitSize());
		workingStruct.addItem(itemUDT);
	}

	private static void manageUDT_DataBlock(ItemStruct workingStruct, String lineReaded)
			throws CloneNotSupportedException {
//		logSiem.info("manageUDT_DataBlock");
		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		String str = removeInitialBrackets(lineReaded);
		str = removeComment(str);
		str = getTypeString(str);
		ItemStruct itemUDT = (ItemStruct) getUDTFromName(str, getNameItemFromStringLine(lineReaded), workingStruct)
				.clone();
		itemUDT.updateName(getNameItemFromStringLine(lineReaded));
		itemUDT.updateParent(workingStruct);

		itemUDT.setUpType();
		itemUDT.addAddresRec(gAddr);
		ModelSiemens.getgAddr().incrementAddress(itemUDT.getByteSize(), itemUDT.getBitSize());
		workingStruct.addItem(itemUDT);
	}

	private static ItemStruct getUDTFromName(String str, String string, ItemStruct parent) {
//		logSiem.info("getUDTFromName");
		for (ItemStruct item : udtlist) {
			if (item.getTitleUDT().equals(str)) {
				if (parent != null)
					item.setParent(parent);
				else
					ModelSiemens.logSiem.error("Fatal error.");
				return item;
			}
		}
		return null;
	}

	private static String getTypeString(String str) {
		str = removeComment(str);
		if (str.contains(Character.toString(':'))) {
			str = str.split(":")[1].trim();
			if (str.contains(Character.toString(';'))) {
				str = str.replaceAll(";", "").trim();
				return str;
			} else {
				return str;
			}
		} else {
			logSiem.error("Fatal error");
			return null;
		}
	}

	public static String removeInitialBrackets(String lineReaded) {
//		ModelSiemens.logSiem.info(lineReaded);
		String tmp_str1 = "", tmp_str2 = "";
		if (lineReaded.contains("//")) {
			// la linea letta ha un commento, quindi lo rimuovo temporaneamnete
			tmp_str1 = lineReaded.split("//")[0];
			tmp_str2 = " //" + lineReaded.split("//")[1];
			// rimuovo il contenuto delle parentesi graffe se ci sono le parentesi graffe
			if (lineReaded.contains(Character.toString('{')) && lineReaded.contains(Character.toString('}'))) {
				tmp_str1 = tmp_str1.replaceAll("\\{.*?\\}", "").trim();
			}
		} else if (lineReaded.contains(Character.toString('{')) && lineReaded.contains(Character.toString('}'))) {

			// rimuovo il contenuto delle parentesi graffe
			tmp_str1 = lineReaded.replaceAll("\\{.*?\\}", "");
		} else {
			// La riga NON contiene un commento e NON ha graffe
			tmp_str1 = lineReaded;
		}
		tmp_str1 = tmp_str1 + tmp_str2;
		return tmp_str1;
	}

	public static String removeComment(String replace) {
		if (replace.contains("//")) {
			replace = replace.trim().split("//")[0].trim();
		}
		return replace.trim();
	}

	private static void manageIEC_TIMER(ItemStruct workingStruct, String lineReaded, boolean typeChanged)
			throws CloneNotSupportedException {
//		logSiem.info("manageIEC_TIMER");
		ItemIEC_TIMER itemIEC_TIMER = (ItemIEC_TIMER) ItemIEC_TIMER.makeItemFromString(workingStruct, lineReaded,
				typeChanged);
		itemIEC_TIMER.setUpType();
		workingStruct.addItem(itemIEC_TIMER);
	}

	private static void manageByte(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		logSiem.info("manageByte");
		ItemByte item = (ItemByte) ItemByte.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageChar(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		logSiem.info("manageChar");
		ItemChar item = (ItemChar) ItemChar.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageReal(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageReal");
		ItemReal item = (ItemReal) ItemReal.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageString(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageString");
		ItemString item = (ItemString) ItemString.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageStringInArrayForm(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageString");
		ItemString item = (ItemString) ItemString.makeItemFromStringInArrayForm(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageDint(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageDint");
		ItemDint item = (ItemDint) ItemDint.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageUDint(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageUDint");
		ItemUDint item = (ItemUDint) ItemUDint.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageTimeOfDay(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
		// logSiem.info("manageTimeOfDay");
		ItemTimeOfDay item = (ItemTimeOfDay) ItemTimeOfDay.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageInt");
		ItemInt item = (ItemInt) ItemInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageUInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageUInt");
		ItemUInt item = (ItemUInt) ItemUInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageSInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageSInt");
		ItemSInt item = (ItemSInt) ItemSInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageUSInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageUSInt");
		ItemUSInt item = (ItemUSInt) ItemUSInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageS5Time(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageS5Time");
		ItemS5Time item = (ItemS5Time) ItemS5Time.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageDate(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageDate");
		ItemDate item = (ItemDate) ItemDate.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageDateAndTime(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageDatAndTimee");
		ItemDateAndTime item = (ItemDateAndTime) ItemDateAndTime.makeItemFromString(workingStruct, lineReaded,
				typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageLInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageLInt");
		ItemLInt item = (ItemLInt) ItemLInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageULInt(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageULInt");
		ItemULInt item = (ItemULInt) ItemULInt.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageLReal(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageLReal");
		ItemLReal item = (ItemLReal) ItemLReal.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageLTime(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageLTime");
		ItemLTime item = (ItemLTime) ItemLTime.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageLWord(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageLWord");
		ItemLWord item = (ItemLWord) ItemLWord.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		workingStruct.addItem(item);
	}

	private static void manageBool(ItemStruct workingStruct, String lineReaded, boolean typeChanged) {
//		 logSiem.info("manageBool: "+lineReaded);
		ItemBool item = (ItemBool) ItemBool.makeItemFromString(workingStruct, lineReaded, typeChanged);
		item.setUpType();
		item.updateParent(workingStruct);
		workingStruct.addItem(item);
	}

	private static void manageStructSimple(BufferedReader br, ItemStruct workingStruct, String lineReaded, boolean flag)
			throws IOException, CloneNotSupportedException {
		// logSiem.info("manageStruct");
		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		String nameStruct = "";
		if (flag) {
			nameStruct = getNameItemFromStringLine(lineReaded);
		} else {
			nameStruct = getNameItemFromStringLine(lineReaded);
		}
		Item struct = ItemStruct.makeItemFromString(workingStruct, nameStruct);
		workingStruct.addItem(struct);
		readLineRecursive(br, (ItemStruct) struct, flag);
	}

	public static String getNameItemFromStringLine(String lineReaded) {
		String str = removeInitialBrackets(lineReaded);
		str = removeComment(str);
		str = getNameString(str);
		return str;
	}

	public static String getNameString(String str) {
		if (str.contains(Character.toString(':'))) {
			str = str.split(":")[0].trim();
			return str;
		} else {
			logSiem.error("Fatal error");
			return null;
		}
	}

	private static String getNameUDTFromStringLine(String lineReaded) {
		String str = removeInitialBrackets(lineReaded);
		str = removeComment(str);
		str = getTypeString(str);
		return str;
	}

	private static void manageArray(ItemStruct workingStruct, String lineReaded, boolean flag)
			throws IOException, CloneNotSupportedException {
//		logSiem.info("manageArray");
		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		int lowerArrayIndex = 0, upperArrayIndex = 0;
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(lineReaded.split(":")[1]);

		m.find();

		if (m.group(1).contains("..")) {
			if (isNumeric(m.group(1).split("\\.\\.")[0])) {
				lowerArrayIndex = Integer.parseInt(m.group(1).split("\\.\\.")[0]);
			} else {
				String s = m.group(1).split("\\.\\.")[0].replace("\"", "");
				ModelSiemens.getPLCTags().get(s);
				if (ModelSiemens.getPLCTags().get(s) != null) {
					lowerArrayIndex = Integer.parseInt(ModelSiemens.getPLCTags().get(s));
				} else {
					logSiem.error("L'indice letto non è stato trovato!");
				}
			}

			if (isNumeric(m.group(1).split("\\.\\.")[1])) {
				upperArrayIndex = Integer.parseInt(m.group(1).split("\\.\\.")[1]);
			} else {
				String s = m.group(1).split("\\.\\.")[1].replace("\"", "");
				ModelSiemens.getPLCTags().get(s);
				if (ModelSiemens.getPLCTags().get(s) != null) {
					upperArrayIndex = Integer.parseInt(ModelSiemens.getPLCTags().get(s));
				} else {
					logSiem.error("L'indice letto non è stato trovato!");
				}
			}
		} else {
			logSiem.error("fatal error!");
			return;
		}
		String str = lineReaded.split(":")[1].split(" of ")[1];
		str = removeComment(str);

		if (str.contains("Bool;")) {
//			ModelSiemens.logSiem.info("Array of bool");
			manageArrayOfBool(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("UDInt;")) {
//			ModelSiemens.logSiem.info("Array of UDint");
			manageArrayOfUDint(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("DInt;")) {
//			ModelSiemens.logSiem.info("Array of Dint");
			manageArrayOfDint(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Byte;")) {
//			ModelSiemens.logSiem.info("Array of Byte");
			manageArrayOfByte(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Char;")) {
//			ModelSiemens.logSiem.info("Array of Char");
			manageArrayOfChar(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Word;") && !str.contains("LWord;") && !str.contains("DWord;")) {
//			ModelSiemens.logSiem.info("Array of Word");
			manageArrayOfWord(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("DWord;")) {
//			ModelSiemens.logSiem.info("Array of DWord");
			manageArrayOfDword(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("ULInt;")) {
//			ModelSiemens.logSiem.info("Array of ULInt");
			manageArrayOfULInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("UInt;")) {
//			ModelSiemens.logSiem.info("Array of UInt");
			manageArrayOfUInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Int;") && !str.contains("LInt;") && !str.contains("SInt;") && !str.contains("DInt;")
				&& !str.contains("UInt;")) {
//			ModelSiemens.logSiem.info("Array of Int");
			manageArrayOfInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("S5Time;") && !str.contains("LTime;")) {
//			ModelSiemens.logSiem.info("Array of S5Time");
			manageArrayOfS5Time(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Date;") && !str.contains("Date_And_Time;") && !str.contains("Time_Of_Day;")) {
//			ModelSiemens.logSiem.info("Array of Date");
			manageArrayOfDate(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Date_And_Time;")) {
//			ModelSiemens.logSiem.info("Array of DateAndTime");
			manageArrayOfDateAndTime(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Time_Of_Day;")) {
//			ModelSiemens.logSiem.info("Array of TimeOfDay");
			manageArrayOfTimeOfDay(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Time;") && !str.contains("LTime;") && !str.contains("S5Time;")) {
//			ModelSiemens.logSiem.info("Array of Time");
			manageArrayOfTime(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("LInt;")) {
//			ModelSiemens.logSiem.info("Array of LInt");
			manageArrayOfLInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("USInt;")) {
//			ModelSiemens.logSiem.info("Array of USInt");
			manageArrayOfUSInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("SInt;")) {
//			ModelSiemens.logSiem.info("Array of SInt");
			manageArrayOfSInt(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("LReal;")) {
//			ModelSiemens.logSiem.info("Array of LReal");
			manageArrayOfLReal(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("LTime;")) {
//			ModelSiemens.logSiem.info("Array of LTime");
			manageArrayOfLTime(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("LWord;")) {
//			ModelSiemens.logSiem.info("Array of LWord");
			manageArrayOfLWord(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Real;")) {
//			ModelSiemens.logSiem.info("Array of Real");
			manageArrayOfReal(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("String;")) {
//			ModelSiemens.logSiem.info("Array of String");
			manageArrayOfString(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("IEC_TIMER;")) {
//			ModelSiemens.logSiem.info("Array of IEC_TIMER");
			manageArrayOf_IEC_TIMER(workingStruct, lineReaded, lowerArrayIndex, upperArrayIndex);
		} else if (str.contains("Struct")) {
//			ModelSiemens.logSiem.info("Array of Struct");
			manageArrayOfStruct(workingStruct, lineReaded, flag, lowerArrayIndex, upperArrayIndex);
		} else if (UDTexist(str.replace(";", ""))) {
//			ModelSiemens.logSiem.info("Array of UDT");
			manageArrayOfUDT(workingStruct, lowerArrayIndex, upperArrayIndex, lineReaded);
		} else {
			logSiem.error("Tipo (Array) non riconosciuto! " + str + " -> " + lineReaded);
		}

		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
	}

	private static void manageArrayOfUDT(ItemStruct workingStruct, int lowerArrayIndex, int upperArrayIndex,
			String lineReaded) throws CloneNotSupportedException {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			String str = removeInitialBrackets(lineReaded);
			str = removeComment(str);
			str = getTypeString(str);
			str = getTypeArrayString(str);

			ItemStruct itemUDT = (ItemStruct) getUDTFromName(str, getNameItemFromStringLine(lineReaded) + "[" + i + "]",
					workingStruct).clone();
			itemUDT.updateDbName(workingStruct.getDbName());
			itemUDT.updateName(getNameItemFromStringLine(lineReaded) + "[" + i + "]");
			itemUDT.updateParent(workingStruct);
			itemUDT.setUpType();
			itemUDT.addAddresRec(gAddr);
			ModelSiemens.getgAddr().incrementAddress(itemUDT.getByteSize(), itemUDT.getBitSize());
			workingStruct.addItem(itemUDT);
		}
	}

	private static String getTypeArrayString(String str) {
		if (str.contains("of")) {
			str = str.split("of")[1].trim();
			if (str.contains(Character.toString(';'))) {
				str = str.replaceAll(";", "");
			}
			return str;
		}
		ModelSiemens.logSiem.error("Fatal error.");
		return null;
	}

	private static void manageArrayOfStruct(ItemStruct workingStruct, String lineReaded, boolean flag,
			int lowerArrayIndex, int upperArrayIndex) throws IOException, CloneNotSupportedException {
		int i = 0;

		Item struct = ItemStruct.makeItemArrayFromString(workingStruct, lineReaded);
		Address oldGlobalAddress = ModelSiemens.getgAddr().clone();
		ModelSiemens.getgAddr().resetAddress();
		readLineRecursive(br, (ItemStruct) struct, flag);
		Address sizeArray = ModelSiemens.getgAddr().clone();
		ModelSiemens.setgAddr(oldGlobalAddress.clone());
		for (i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			ItemStruct struct_tmp = (ItemStruct) struct.clone();
			struct_tmp.updateName(getNameItemFromStringLine(lineReaded) + "[" + i + "]");
			struct_tmp.addAddresRec(gAddr);
			ModelSiemens.getgAddr().add(sizeArray);
			workingStruct.addItem(struct_tmp);
		}
	}

	private static void manageArrayOf_IEC_TIMER(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) throws CloneNotSupportedException {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			ModelSiemens.getgAddr().incrementAddress(4, 0);
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemIEC_TIMER(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
		}
	}

	private static void manageArrayOfReal(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemReal(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfString(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemString(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					0, workingStruct));
			ModelSiemens.getgAddr().incrementAddress(256, 0);
		}
	}

	private static void manageArrayOfInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(2, 0);
		}
	}

	private static void manageArrayOfUInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemUInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(2, 0);
		}
	}

	private static void manageArrayOfSInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			workingStruct.addItem(new ItemSInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(1, 0);
		}
	}

	private static void manageArrayOfUSInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			workingStruct.addItem(new ItemUSInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(1, 0);
		}
	}

	private static void manageArrayOfS5Time(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemS5Time(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(2, 0);
		}
	}

	private static void manageArrayOfDate(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemDate(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(2, 0);
		}
	}

	private static void manageArrayOfDateAndTime(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemDateAndTime(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfLInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemLInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfULInt(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemULInt(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfLReal(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemLReal(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfLTime(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemLTime(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfLWord(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemLWord(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(8, 0);
		}
	}

	private static void manageArrayOfDword(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemDWord(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfTimeOfDay(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemTimeOfDay(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfTime(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemTime(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfWord(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemWord(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(2, 0);
		}
	}

	private static void manageArrayOfByte(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			workingStruct.addItem(new ItemByte(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(1, 0);
		}
	}

	private static void manageArrayOfChar(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			workingStruct.addItem(new ItemChar(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(1, 0);
		}
	}

	private static void manageArrayOfDint(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemDint(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfUDint(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
			workingStruct.addItem(new ItemUDint(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(4, 0);
		}
	}

	private static void manageArrayOfBool(ItemStruct workingStruct, String lineReaded, int lowerArrayIndex,
			int upperArrayIndex) {
		for (int i = lowerArrayIndex; i <= upperArrayIndex; i++) {
			workingStruct.addItem(new ItemBool(workingStruct.getDbName(),
					lineReaded.split(":")[0].trim() + "[" + i + "]", "", new Address(workingStruct.getAddress().getDB(),
							ModelSiemens.getgAddr().gByte(), ModelSiemens.getgAddr().gBit()),
					workingStruct));
			ModelSiemens.getgAddr().incrementAddress(0, 1);
		}
	}

	private static boolean UDTexist(String replace) {
		for (ItemStruct itemUDT : udtlist) {
			if (itemUDT.getName().equals(replace)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void generateXlsx(String stringNameFile) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		style = wb.createCellStyle();
		font = wb.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (DB db : dblist) {
			int ind = 1;
			int index = 1;
			String nameSheet = db.getName();// + index++;
			if (nameSheet.equals(""))
				nameSheet = "noName_" + index++;
			XSSFSheet sheet = wb.createSheet(nameSheet);
			sheet.createFreezePane(3, 1);
			resizeColoumns(sheet);
			Row headerRow = sheet.createRow(0);
			makeHeaderSheetXls(headerRow);
			CellStyle styleHeader = makeStyleHeader(wb);
			for (int i = 0; i < MAX_NUM_COL_XLS; i++) {
				if (headerRow.getCell(i) != null) {
					headerRow.getCell(i).setCellStyle(styleHeader);
				}
			}
			db.generateXlsx(wb, sheet, ind, style);
		}

		File file = new File(stringNameFile);
		if (file != null) {
			try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
				wb.write(outputStream);
			} catch (IOException ex) {
				logSiem.error("Impossibile salvare file Excel");
			} finally {
				wb.close();
			}
		}
		wb.close();
		try {
			if (properties.getProperty("openXlsAfterImport").equals("true"))
				if (new File(stringNameFile).isFile()) {
					Desktop.getDesktop().open(new File(stringNameFile));
				}
		} catch (Exception e) {
			logSiem.error("Unable to get property: openXlsAfterImport");
		}
	}

	private void resizeColoumns(Sheet sheet) {
		sheet.setColumnWidth(0, convertChars("DB".length()));
		sheet.setColumnWidth(1, convertChars("word".length()));
		sheet.setColumnWidth(2, convertChars("bit".length()));
		sheet.setColumnWidth(3, convertChars("Indirizzo Plc simbolico".length()));
		sheet.setColumnWidth(4, convertChars("Indirizzo Plc assoluto".length()));
		sheet.setColumnWidth(5, convertChars("Tag Scada             ".length()));
		sheet.setColumnWidth(6, convertChars("Tipo".length()));
		sheet.setColumnWidth(7, convertChars("Sviluppo".length()));
		sheet.setColumnWidth(8, convertChars("Sigla".length()));
		sheet.setColumnWidth(9, convertChars("Descrizione".length()));
		sheet.setColumnWidth(10, convertChars("Descrizione Estesa".length()));
		sheet.setColumnWidth(11, convertChars("Limite MIN".length()));
		sheet.setColumnWidth(12, convertChars("Limite MAX".length()));
		sheet.setColumnWidth(13, convertChars("UM".length()));
		sheet.setColumnWidth(14, convertChars("Uso del bit".length()));
		sheet.setColumnWidth(15, convertChars("Commento".length()));
		sheet.setColumnWidth(16, convertChars("Livello".length()));
		sheet.setColumnWidth(17, convertChars("UPDATED".length()));
	}

	/**
	 * @param chars
	 * @return
	 */
	private int convertChars(int chars) {
		return (chars * 256) + 1500;
	}

	/**
	 * @param headerRow
	 */
	private void makeHeaderSheetXls(Row headerRow) {
		headerRow.createCell(0).setCellValue("DB");
		headerRow.createCell(1).setCellValue("word");
		headerRow.createCell(2).setCellValue("bit");
		headerRow.createCell(3).setCellValue("Indirizzo Plc simbolico");
		headerRow.createCell(4).setCellValue("Indirizzo Plc assoluto");
		headerRow.createCell(5).setCellValue("Tag Scada             ");
		headerRow.createCell(6).setCellValue("Tipo");
		headerRow.createCell(7).setCellValue("Sviluppo");
		headerRow.createCell(8).setCellValue("Sigla");
		headerRow.createCell(9).setCellValue("Descrizione");
		headerRow.createCell(10).setCellValue("Descrizione Estesa");
		headerRow.createCell(11).setCellValue("Limite MIN");
		headerRow.createCell(12).setCellValue("Limite MAX");
		headerRow.createCell(13).setCellValue("UM");
		headerRow.createCell(14).setCellValue("Uso del bit");
		headerRow.createCell(15).setCellValue("Commento");
		headerRow.createCell(16).setCellValue("Livello");
		headerRow.createCell(17).setCellValue("UPDATED");
	}

	public boolean readXlsxFile(File f) {
		ZipSecureFile.setMinInflateRatio(0);
		Workbook workbook = null;
		Iterator<Sheet> sheetIterator = null;
		try {
			workbook = WorkbookFactory.create(f);
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		sheetIterator = workbook.sheetIterator();
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			// Solitamete questi sheet non ci sono (alemo nelle nuove versioni dei file
			// excel) ma èmeglio controllare...
			if (!sheet.getSheetName().equals("Parametri") && !sheet.getSheetName().equals("Intestazione")
					&& !sheet.getSheetName().equals("Sommario") && !sheet.getSheetName().equals("ZONA_MACCHINA")) {
				DataFormatter dataFormatter = new DataFormatter();
				Iterator<Row> rowIterator = sheet.rowIterator();
				Row row = rowIterator.next();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					String[] cells = new String[18];
					int i = 0;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (cell == null) {
							cells[i] = "";
							i++;
						} else {
							if (cell.getCellType() == CellType.FORMULA) {
								switch (evaluator.evaluateFormulaCell(cell)) {
								case BOOLEAN:
									cells[i] = "" + cell.getBooleanCellValue();
									break;
								case NUMERIC:
									cells[i] = "" + cell.getNumericCellValue();
									break;
								case STRING:
									cells[i] = "" + cell.getStringCellValue();
									break;
								default:
									break;
								}
							} else {
								cells[i] = dataFormatter.formatCellValue(cell);
							}
							i++;
						}
					}

					EntryXlsx entry = new EntrySiemensXls(cells[0].trim(), cells[1].trim(), cells[2].trim(),
							cells[3].trim(), cells[4].trim(), cells[5].trim(), cells[6].trim(), 0, cells[8].trim(),
							cells[9].trim(), cells[10].trim(), cells[11].trim(), cells[12].trim(), cells[13].trim(),
							cells[14].trim(), cells[15].trim(), cells[16].trim(), cells[17].trim(), false);

					if (!entry.getfDescrizioneEstesa().isBlank() && !entry.getfDescrizioneEstesa().isEmpty()) {
						if (entry.getfTipo().trim().equals("bit_Anomalies<DA_BIT>")) {
							this.getCsvGenerator().getListEntry_DA().add(entry);
						} else if (entry.getfTipo().trim().equals("int_Anomalies<AA_INT>")) {
							this.getCsvGenerator().getListEntry_AI().add(entry);// TODO per ora li metto in AI e non AA
						} else if (entry.getfTipo().trim().equals("bit_read<DI_BIT>")
								|| entry.getfTipo().trim().equals("bit_manual_cmd<WDI_BIT>")
								|| entry.getfTipo().trim().equals("bit_manual_cmd<DI_BIT>")
								|| entry.getfTipo().trim().equals("bit_write<WDI_BIT>")) {
							this.getCsvGenerator().getListEntry_DI().add(entry);
						} else if (entry.getfTipo().trim().equals("string_write<WTX_STRING>")
								|| entry.getfTipo().trim().equals("dint_read<AI_DINT>")
								|| entry.getfTipo().trim().equals("int_write<WAI_INT>")
								|| entry.getfTipo().trim().equals("string_read<TX_STRING>")
								|| entry.getfTipo().trim().equals("real_write<WAI_REAL>")
								|| entry.getfTipo().trim().equals("dint_write<WAI_DINT>")
								|| entry.getfTipo().trim().equals("int_read<AI_INT>")
								|| entry.getfTipo().trim().equals("real_read<AI_REAL>")
								|| entry.getfTipo().trim().equals("byte_write<WDI_BYTE>")
								|| entry.getfTipo().trim().equals("byte_read<DI_BYTE>")) {
							this.getCsvGenerator().getListEntry_AI().add(entry);
						} else {
							ModelSiemens.logSiem.error("Errore. Tipo non riconosciuto: " + entry.getfTipo().trim());
							return false;
						}
						// TODO aggiungere gli altri casi, e controllare questi
					}
				}
			}
		}
		return true;

	}

	public static Settings getProp() {
		return properties;
	}

	public static void readProperties() {
		try {
			properties = new Settings();
			properties.load(new File("properties/siemensImporterSettings.cfg"));
		} catch (IOException e) {
			e.printStackTrace();
			ModelSiemens.logSiem.error("IOException - " + e);
		}
	}

	enum Category {
		DA_ALARM, DI_BOOL, AI_NUMBER
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

	public void clearDbList() {
		dblist.clear();
	}

	public void clearListEntry() {
		this.getCsvGenerator().getListEntry_DA().clear();
		this.getCsvGenerator().getListEntry_AA().clear();
		this.getCsvGenerator().getListEntry_DI().clear();
		this.getCsvGenerator().getListEntry_AI().clear();
	}

	public void setController(MainViewControllerSiemens mainController) {
		controller = mainController;

	}

	/**
	 * @return the csvGenerator
	 */
	public CsvGenerator getCsvGenerator() {
		if (csvGenerator == null) {
			csvGenerator = new CsvGenerator(properties, logSiem, controller);
		}
		return csvGenerator;
	}

	public static Address getgAddr() {
		return gAddr;
	}

	public static void setgAddr(Address gAddr) {
		ModelSiemens.gAddr = gAddr;
	}

	public static HashMap<String, String> getPLCTags() {
		return PLCTags;
	}

}