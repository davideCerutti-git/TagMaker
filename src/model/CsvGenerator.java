package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import controller.MainViewControllerRockwell;
import controller.MainViewControllerSiemens;
import controller.ViewController;
import settings.Settings;

public class CsvGenerator {

	/*
	 * ================================================================== 
	 * Liste di "righe" prese dal file Excel
	 * DA = digital alarm
	 * ==================================================================
	 */
	private ArrayList<EntryXlsx> listEntry_DA = new ArrayList<EntryXlsx>();
	private ArrayList<EntryXlsx> listEntry_DI = new ArrayList<EntryXlsx>();
	private ArrayList<EntryXlsx> listEntry_AI = new ArrayList<EntryXlsx>();
	private ArrayList<EntryXlsx> listEntry_AA = new ArrayList<EntryXlsx>();

	private List<String> listOfScaleMul_1000000;
	private List<String> listOfScaleMul_100000;
	private List<String> listOfScaleMul_10000;
	private List<String> listOfScaleMul_1000;
	private List<String> listOfScaleMul_100;
	private List<String> listOfScaleMul_10;
	private List<String> listOfScaleDiv_1000000;
	private List<String> listOfScaleDiv_100000;
	private List<String> listOfScaleDiv_10000;
	private List<String> listOfScaleDiv_1000;
	private List<String> listOfScaleDiv_100;
	private List<String> listOfScaleDiv_10;
	private String fileNameNoExtension;

	private static Settings properties;
	private static Logger logger;
	private static ViewController controller;

	public CsvGenerator(Settings settings, Logger logger, ViewController controller) {
		CsvGenerator.properties = settings;
		CsvGenerator.logger = logger;
		CsvGenerator.controller = controller;
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	public void generateCSV(String path) throws IOException {

		String[] arrayMul1000000 = properties.getProperty("scaleMul_1000000").split(";");
		for (String s : arrayMul1000000) {
			s = s.trim();
		}

		String[] arrayMul100000 = properties.getProperty("scaleMul_100000").split(";");
		for (String s : arrayMul100000) {
			s = s.trim();
		}

		String[] arrayMul10000 = properties.getProperty("scaleMul_10000").split(";");
		for (String s : arrayMul10000) {
			s = s.trim();
		}

		String[] arrayMul1000 = properties.getProperty("scaleMul_1000").split(";");
		for (String s : arrayMul1000) {
			s = s.trim();
		}

		String[] arrayMul100 = properties.getProperty("scaleMul_100").split(";");
		for (String s : arrayMul100) {
			s = s.trim();
		}

		String[] arrayMul10 = properties.getProperty("scaleMul_10").split(";");
		for (String s : arrayMul10) {
			s = s.trim();
		}

		String[] arrayDiv1000000 = properties.getProperty("scaleDiv_1000000").split(";");
		for (String s : arrayDiv1000000) {
			s = s.trim();
		}

		String[] arrayDiv100000 = properties.getProperty("scaleDiv_100000").split(";");
		for (String s : arrayDiv100000) {
			s = s.trim();
		}

		String[] arrayDiv10000 = properties.getProperty("scaleDiv_10000").split(";");
		for (String s : arrayDiv10000) {
			s = s.trim();
		}

		String[] arrayDiv1000 = properties.getProperty("scaleDiv_1000").split(";");
		for (String s : arrayDiv1000) {
			s = s.trim();
		}

		String[] arrayDiv100 = properties.getProperty("scaleDiv_100").split(";");
		for (String s : arrayDiv100) {
			s = s.trim();
		}

		String[] arrayDiv10 = properties.getProperty("scaleDiv_10").split(";");
		for (String s : arrayDiv10) {
			s = s.trim();
		}

		listOfScaleMul_1000000 = Arrays.asList(arrayMul1000000);
		listOfScaleMul_100000 = Arrays.asList(arrayMul100000);
		listOfScaleMul_10000 = Arrays.asList(arrayMul10000);
		listOfScaleMul_1000 = Arrays.asList(arrayMul1000);
		listOfScaleMul_100 = Arrays.asList(arrayMul100);
		listOfScaleMul_10 = Arrays.asList(arrayMul10);

		listOfScaleDiv_1000000 = Arrays.asList(arrayDiv1000000);
		listOfScaleDiv_100000 = Arrays.asList(arrayDiv100000);
		listOfScaleDiv_10000 = Arrays.asList(arrayDiv10000);
		listOfScaleDiv_1000 = Arrays.asList(arrayDiv1000);
		listOfScaleDiv_100 = Arrays.asList(arrayDiv100);
		listOfScaleDiv_10 = Arrays.asList(arrayDiv10);

//		System.out.println("‡‡‡‡");
		File f=new File(path);
		fileNameNoExtension=FilenameUtils.removeExtension(f.getName());

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(properties.getProperty("csvHeaderMain_1") + "\n\n");
		writer.write(properties.getProperty("csvHeaderMain_2") + "\n");

		if (!listEntry_DA.isEmpty()) {
			writer.write(properties.getProperty("csvHeader1_DA") + "\n");
			writer.write(properties.getProperty("csvHeader2_DA") + "\n");
			List<String[]> listRows_DA = new ArrayList<String[]>();
			for (EntryXlsx entry : listEntry_DA) {
				if (entry.fDescrizioneEstesa.isEmpty()) {
					;
				} else {
					listRows_DA.add(generateEntryVectString_DA(entry));
				}
			}
			write(writer, listRows_DA);
		}

		if (!listEntry_AA.isEmpty()) {
			writer.write(properties.getProperty("csvHeader1_AA") + "\n");
			writer.write(properties.getProperty("csvHeader2_AA") + "\n");
			List<String[]> listRows_AA = new ArrayList<String[]>();
			for (EntryXlsx entry : listEntry_AA) {
				if (entry.fDescrizioneEstesa.isEmpty()) {
					;
				} else {
					listRows_AA.add(generateEntryVectString_AA(entry));
				}
			}
			write(writer, listRows_AA);
		}

		if (!listEntry_DI.isEmpty()) {
			writer.write("\n" + properties.getProperty("header1_DI") + "\n");
			writer.write(properties.getProperty("header2_DI") + "\n");
			List<String[]> listRows_DI = new ArrayList<String[]>();
			for (EntryXlsx entry : listEntry_DI) {
				if (entry.fDescrizioneEstesa.isEmpty()) {
					;
				} else {
					listRows_DI.add(generateEntryVectString_DI(entry));
				}
			}
			write(writer, listRows_DI);
		}

		if (!listEntry_AI.isEmpty()) {
			writer.write("\n" + properties.getProperty("header1_AI") + "\n");
			writer.write(properties.getProperty("header2_AI") + "\n");
			List<String[]> listRows_AI = new ArrayList<String[]>();
			for (EntryXlsx entry : listEntry_AI) {
				if (entry.fDescrizioneEstesa.isEmpty()) {
					;
				} else {
					listRows_AI.add(generateEntryVectString_AI(entry));
				}
			}
			write(writer, listRows_AI);
		}

		writer.write("\n" + properties.getProperty("csvFileEndFileString"));
		writer.flush();
		writer.close();
	}

	/**
	 * @param writer
	 * @param listRows
	 */
	private void write(BufferedWriter writer, List<String[]> listRows) {
		listRows.forEach(vect -> {
			try {
				writeEntry(writer, vect);
			} catch (IOException e) {
				logger.error("Threw a BadException, full stack trace follows:", e);
			}
		});
	}

	/**
	 * @param writer
	 * @param vect
	 * @throws IOException
	 */
	private void writeEntry(BufferedWriter writer, String[] vect) throws IOException {

		writer.write("\"");
		for (String s : vect) {
			writer.write(s + "\";\"");
		}
		writer.write("\"\n");

	}

	private String[] generateEntryVectString_AA(EntryXlsx entry) {
		ArrayList<String> array = new ArrayList<String>();
		array.add(0, "AA");
		array.add(1, entry.getfTagNameSCADA());
		array.add(2, "");
		array.add(3, getContentNoBraces(entry.getfDescrizioneEstesa()));
		// TODO finire
		array.add(4, (false ? "IGS" : "S7A"));
		array.add(5, "");
		// TODO finire
		array.add(6, getDriverPrefix() + (false ? entry.getfAddrPlc() : entry.getfAddrAbsPlc()));
		array.add(7, "AUTO");
		array.add(8, "ON");
		array.add(9, "1");
		array.add(10, "NO");
		array.add(11, "OPEN");
		array.add(12, "CLOSE");
		array.add(13, "ENABLE");
		array.add(14, "NONE");
		array.add(15, "HIGH");
		array.add(16, "CLOSE");
		array.add(17, "NO");
		array.add(18, "NONE");
		array.add(19, "NONE");
		array.add(20, "NONE");
		array.add(21, "ALL");
		array.add(22, "");
		array.add(23, "");
		array.add(24, "");
		array.add(25, "");
		array.add(26, "");
		array.add(27, "");
		array.add(28, "");
		array.add(29, "");
		array.add(30, "");
		array.add(31, "");
		array.add(32, "");
		array.add(33, "");
		array.add(34, "");
		array.add(35, "");
		array.add(36, cleanNewLines(entry));
		array.add(37, cleanNewLines(entry));
		array.add(38, "");
		array.add(39, "");
		array.add(40, "");
		array.add(41, "");
		array.add(42, "00:00:00:00");
		array.add(43, "00:00:00:00");
		array.add(44, "NONE");
		array.add(45, "YES");
		array.add(46, "NO");
		array.add(47, "REJECT");
		array.add(48, "NO");
		array.add(49, "300");
		array.add(50, "0");
		array.add(51, "0");
		array.add(52, "NO");
		array.add(53, "NO");
		array.add(54, "0");
		array.add(55, "NO");
		array.add(56, "NO");
		array.add(57, "5.000,0");
		array.add(58, "0.0");
		array.add(59, "Milliseconds");
		array.add(60, "DISABLE");
		array.add(61, "0.0000");
		array.add(62, "Absolute");
		array.add(63, "0");
		array.add(64, "DISABLE");
		array.add(65, "DISABLE");
		array.add(66, "DISABLE");
		array.add(67, "DISABLE");
		array.add(68, "");

		String[] arr = new String[array.size()];
		arr = array.toArray(arr);
		return arr;
	}

	/**
	 * @param entry
	 * @return
	 */
	private String[] generateEntryVectString_DA(EntryXlsx entry) {
		ArrayList<String> array = new ArrayList<String>();
		array.add(0, "DA");
		array.add(1, entry.getfTagNameSCADA());
		array.add(2, "");
		//array.add(3, getContentNoBraces(entry.getfDescrizioneEstesa()));
		array.add(3, fileNameNoExtension+"."+cleanNewLines(entry));
		// TODO finire
		array.add(4, (false ? "IGS" : "S7A"));
		array.add(5, "");
		// TODO finire
		array.add(6, getDriverPrefix() + (false ? entry.getfAddrPlc() : entry.getfAddrAbsPlc()));
		array.add(7, "AUTO");
		array.add(8, "ON");
		array.add(9, "1");
		array.add(10, "NO");
		array.add(11, "OPEN");
		array.add(12, "CLOSE");
		array.add(13, "ENABLE");
		array.add(14, "NONE");
		array.add(15, "HIGH");
		array.add(16, "CLOSE");
		array.add(17, "NO");
		array.add(18, "NONE");
		array.add(19, "NONE");
		array.add(20, "NONE");
		array.add(21, "ALL");
		array.add(22, "");
		array.add(23, "");
		array.add(24, "");
		array.add(25, "");
		array.add(26, "");
		array.add(27, "");
		array.add(28, "");
		array.add(29, "");
		array.add(30, "");
		array.add(31, "");
		array.add(32, "");
		array.add(33, "");
		array.add(34, "");
		array.add(35, "");
		array.add(36, fileNameNoExtension+"."+cleanNewLines(entry));
		array.add(37, fileNameNoExtension+"."+cleanNewLines(entry));
		array.add(38, "");
		array.add(39, "");
		array.add(40, "");
		array.add(41, "");
		array.add(42, "00:00:00:00");
		array.add(43, "00:00:00:00");
		array.add(44, "NONE");
		array.add(45, "YES");
		array.add(46, "NO");
		array.add(47, "REJECT");
		array.add(48, "NO");
		array.add(49, "300");
		array.add(50, "0");
		array.add(51, "0");
		array.add(52, "NO");
		array.add(53, "NO");
		array.add(54, "0");
		array.add(55, "NO");
		array.add(56, "NO");
		array.add(57, "5.000,0");
		array.add(58, "0.0");
		array.add(59, "Milliseconds");
		array.add(60, "DISABLE");
		array.add(61, "0.0000");
		array.add(62, "Absolute");
		array.add(63, "0");
		array.add(64, "DISABLE");
		array.add(65, "DISABLE");
		array.add(66, "DISABLE");
		array.add(67, "DISABLE");
		array.add(68, "");

		String[] arr = new String[array.size()];
		arr = array.toArray(arr);
		return arr;
	}

	/**
	 * @param entry
	 * @return
	 */
	private String[] generateEntryVectString_DI(EntryXlsx entry) {
		ArrayList<String> array = new ArrayList<String>();
		array.add(0, "DI");
		array.add(1, entry.getfTagNameSCADA());
		array.add(2, "");
		array.add(3, getContentNoBraces(entry.getfDescrizioneEstesa()));
		// TODO il false Ë da correggere
		array.add(4, (false ? "IGS" : "S7A"));
		array.add(5, "");
		// TODO il false Ë da correggere
		array.add(6, getDriverPrefix() + (false ? entry.getfAddrPlc() : entry.getfAddrAbsPlc()));
		array.add(7, "AUTO");
		array.add(8, "ON");
		array.add(9, "0,10");
		array.add(10, "NO");
		array.add(11, "OPEN");
		array.add(12, "CLOSE");
		array.add(13, "DISABLE");
		array.add(14, "NONE");
		array.add(15, "LOW");
		array.add(16, "CLOSE");
		array.add(17, "DISABLE");
		array.add(18, "NONE");
		array.add(19, "NONE");
		array.add(20, "NONE");
		array.add(21, isEnableOutput(entry));// enable output
		array.add(22, "ALL");
		array.add(23, "");
		array.add(24, "");
		array.add(25, "");
		array.add(26, "");
		array.add(27, "");
		array.add(28, "");
		array.add(29, "");
		array.add(30, "");
		array.add(31, "");
		array.add(32, "");
		array.add(33, "");
		array.add(34, "");
		array.add(35, "");
		array.add(36, "");
		array.add(37, cleanNewLines(entry));
		array.add(38, cleanNewLines(entry));
		array.add(39, "NONE");
		array.add(40, "YES");
		array.add(41, "NO");
		array.add(42, "REJECT");
		array.add(43, "NO");
		array.add(44, "1");
		array.add(45, "300");
		array.add(46, "0");
		array.add(47, "NO");
		array.add(48, "NO");
		array.add(49, "0");
		array.add(50, "");
		array.add(51, "NO");
		array.add(52, "5.000,0");
		array.add(53, "0.0");
		array.add(54, "Milliseconds");
		array.add(55, "DISABLE");
		array.add(56, "0.0000");
		array.add(57, "Absolute");
		array.add(58, "0");
		array.add(59, "DISABLE");
		array.add(60, "");

		String[] arr = new String[array.size()];
		arr = array.toArray(arr);
		return arr;
	}

	/**
	 * @param entry
	 * @return
	 */
	private String[] generateEntryVectString_AI(EntryXlsx entry) {
		ArrayList<String> array = new ArrayList<String>();
		array.add(0, "AI");
		array.add(1, entry.getfTagNameSCADA());
		array.add(2, "");
		array.add(3, getContentNoBraces(entry.getfDescrizioneEstesa()));
		array.add(4, "ON");
		array.add(5, "0,10");// Scan time
		array.add(6, "0");
		array.add(7, getDriverName());// I/O Device
		array.add(8, "");
		array.add(9, getDriverPrefix() + (false ? entry.getfAddrPlc() : entry.getfAddrAbsPlc()));
		array.add(10, "None");
		array.add(11, "-999999");
		array.add(12, "999999");
		array.add(13, "");
		array.add(14, "AUTO");
//		array.add(15, "DISABLE");
//		array.add(16, "NONE");
//		array.add(17, "-999,999,999");
//		array.add(18, "-999,999,999");
//		array.add(19, "999,999,999");
//		array.add(20, "999,999,999");
//		array.add(21, "0");
//		array.add(22, "100,000,000");
//		array.add(23, "LOW");
		array.add(15, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "ENABLE" : "DISABLE");
		array.add(16, "NONE");
		array.add(17, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "0" : "-100");
		array.add(18, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "0" : "-100");
		array.add(19, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "0" : "100");
		array.add(20, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "100" : "100");
		array.add(21, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "0" : "0");
		array.add(22, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "0" : "100");
		array.add(23, entry.getfTipo().equals("Int_Anomalies<AA_INT>") ? "HIGH" : "LOW");
		array.add(24, isEnableOutput(entry));// enable output
		array.add(25, "NONE");
		array.add(26, "NONE");
		array.add(27, "NONE");
		array.add(28, "ALL");
		array.add(29, "");
		array.add(30, "");
		array.add(31, "");
		array.add(32, "");
		array.add(33, "");
		array.add(34, "");
		array.add(35, "");
		array.add(36, "");
		array.add(37, "");
		array.add(38, "");
		array.add(39, "");
		array.add(40, "");
		array.add(41, "");
		array.add(42, "");
		array.add(43, cleanNewLines(entry));// descrizione pulita da ritorni a capo e ; e con = modificati in '='
		array.add(44, cleanNewLines(entry));// descrizione pulita da ritorni a capo e ; e con = modificati in '='
		array.add(45, "NONE");
		array.add(46, "YES");
		array.add(47, "NO");
		array.add(48, "REJECT");
		array.add(49, "NO");
		array.add(50, "1");
		array.add(51, "300");
		array.add(52, "0");
		array.add(53, "NO");
		array.add(54, "NO");
		array.add(55, "0");
		array.add(56, "");
		array.add(57, "NO");
		array.add(58, "5.000,0");
		array.add(59, "0.0");
		array.add(60, "Milliseconds");
		array.add(61, "DISABLE");
		array.add(62, "0.0000");
		array.add(63, "Absolute");
		array.add(64, "0");
		array.add(65, isScaleEnabled(entry));// scale enabled
		array.add(66, "NO");// scale clamping
		array.add(67, "NO");
		array.add(68, getScaleRawLow(entry));// scale raw low
		array.add(69, getScaleRawHigh(entry));// scale raw high
		array.add(70, getScaleLow(entry));// scale low
		array.add(71, getScaleHigh(entry));// scale high
		array.add(72, "DISABLE");
		array.add(73, "");

		String[] arr = new String[array.size()];
		arr = array.toArray(arr);
		return arr;
	}

	/**
	 * A seconda di quale plc Ë selezionato dal file settingsRockwell restituisco il
	 * prefisso corretto da aggiungere all'indirizzo plc
	 */
	private String getDriverPrefix() {
		if (controller instanceof MainViewControllerRockwell)
			return ((MainViewControllerRockwell) controller).getPrefixSelected() + ":";
		else {
			return ((MainViewControllerSiemens) controller).getPrefixSelected() + ":";
		}
	}

	/**
	 * @return
	 */
	private String getDriverName() {
		// TODO Finire
		return "S7A";
	}

	/**
	 * Restituisce il valore di scalatura alto leggendo dalla descrizione cio che si
	 * trova tra { e }
	 * 
	 * @param entry
	 * @return
	 */
	private String getScaleHigh(EntryXlsx entry) {
		// TODO aggiungere test
		String contentBracesString = getContentOfBraces(entry.getfDescrizioneEstesa());
		if (!contentBracesString.equals("NO PARAMETERS")) {
			// mm/100|mm|-50|+50|M
			String[] strings = contentBracesString.split("\\|");
			String umPlc = strings[0].trim();
			String umScada = strings[1].trim();
			String umConversion = umPlc + "|" + umScada;
			// mm/100|mm
			if (listOfScaleDiv_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 1000.000
			} else if (listOfScaleDiv_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 100.000
			} else if (listOfScaleDiv_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 10.000
			} else if (listOfScaleDiv_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 1000
			} else if (listOfScaleDiv_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 100
			} else if (listOfScaleDiv_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// dividere per 10
			} else if (listOfScaleMul_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1000000";// moltiplicare per 1000.000
			} else if (listOfScaleMul_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "100000";// moltiplicare per 100.000
			} else if (listOfScaleMul_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "10000";// moltiplicare per 10.000
			} else if (listOfScaleMul_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1000";// moltiplicare per 1000
			} else if (listOfScaleMul_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "100";// moltiplicare per 100
			} else if (listOfScaleMul_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "10";// moltiplicare per 10
			} else if (umPlc.equals(umScada)) {
				return "1";// per 1
			}

			logger.error("Conversion rule not found -> " + umConversion);
		}
		return "1";
	}

	/**
	 * Restituisce il valore di scalatura basso leggendo dalla descrizione cio che
	 * si trova tra { e }
	 * 
	 * @param entry
	 * @return
	 */
	private String getScaleLow(EntryXlsx entry) {
		// TODO aggiungere test
		String contentBracesString = getContentOfBraces(entry.getfDescrizioneEstesa());
		if (!contentBracesString.equals("NO PARAMETERS")) {
			// mm/100|mm|-50|+50|M
			String[] strings = contentBracesString.split("\\|");
			String umPlc = strings[0].trim();
			String umScada = strings[1].trim();
			String umConversion = umPlc + "|" + umScada;
			// mm/100|mm
			if (listOfScaleDiv_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 1000.000
			} else if (listOfScaleDiv_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 100.000
			} else if (listOfScaleDiv_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 10.000
			} else if (listOfScaleDiv_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 1000
			} else if (listOfScaleDiv_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 100
			} else if (listOfScaleDiv_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 10
			} else if (listOfScaleMul_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1000000";// dividere per 1000.000
			} else if (listOfScaleMul_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-100000";// dividere per 100.000
			} else if (listOfScaleMul_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-10000";// dividere per 10.000
			} else if (listOfScaleMul_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1000";// dividere per 1000
			} else if (listOfScaleMul_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-100";// dividere per 100
			} else if (listOfScaleMul_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-10";// dividere per 10
			} else if (umPlc.equals(umScada)) {
				return "-1";// per 1
			}
			logger.error("Conversion rule not found -> " + umConversion);
		}
		return "-1";
	}

	/**
	 * Restituisce il valore di scalatura alto leggendo dalla descrizione cio che si
	 * trova tra { e } (restituisce il valore Raw)
	 * 
	 * @param entry
	 * @return
	 */
	private String getScaleRawHigh(EntryXlsx entry) {
		// TODO aggiungere test
		String contentBracesString = getContentOfBraces(entry.getfDescrizioneEstesa());
		if (!contentBracesString.equals("NO PARAMETERS")) {
			// mm/100|mm|-50|+50|M
			String[] strings = contentBracesString.split("\\|");
			String umPlc = strings[0].trim();
			String umScada = strings[1].trim();
			String umConversion = umPlc + "|" + umScada;
			// mm/100|mm
			if (listOfScaleMul_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 1000.000
			} else if (listOfScaleMul_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 100.000
			} else if (listOfScaleMul_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 10.000
			} else if (listOfScaleMul_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 1000
			} else if (listOfScaleMul_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 100
			} else if (listOfScaleMul_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1";// moltiplicare per 10
			} else if (listOfScaleDiv_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1000000";// dividere per 1000.000
			} else if (listOfScaleDiv_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "100000";// dividere per 100.000
			} else if (listOfScaleDiv_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "10000";// dividere per 10.000
			} else if (listOfScaleDiv_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "1000";// dividere per 1000
			} else if (listOfScaleDiv_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "100";// dividere per 100
			} else if (listOfScaleDiv_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "10";// dividere per 10
			} else if (umPlc.equals(umScada)) {
				return "1";// per 1
			}
			logger.error("Conversion rule not found -> " + umConversion);
		}
		return "1";
	}

	/**
	 * Restituisce il valore di scalatura basso leggendo dalla descrizione cio che
	 * si trova tra { e } (restituisce il valore Raw)
	 * 
	 * @param entry
	 * @return
	 */
	private String getScaleRawLow(EntryXlsx entry) {
		// TODO aggiungere test
		String contentBracesString = getContentOfBraces(entry.getfDescrizioneEstesa());
		if (!contentBracesString.equals("NO PARAMETERS")) {
			// mm/100|mm|-50|+50|M
			String[] strings = contentBracesString.split("\\|");
			String umPlc = strings[0].trim();
			String umScada = strings[1].trim();
			String umConversion = umPlc + "|" + umScada;
			// mm/100|mm
			if (listOfScaleMul_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 1000.0000
			} else if (listOfScaleMul_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 100.000
			} else if (listOfScaleMul_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 10.000
			} else if (listOfScaleMul_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 1000
			} else if (listOfScaleMul_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 100
			} else if (listOfScaleMul_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1";// moltiplicare per 10
			} else if (listOfScaleDiv_1000000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1000000";// dividere per 1000.000
			} else if (listOfScaleDiv_100000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-100000";// dividere per 100.000
			} else if (listOfScaleDiv_10000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-10000";// dividere per 10.000
			} else if (listOfScaleDiv_1000.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-1000";// dividere per 1000
			} else if (listOfScaleDiv_100.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-100";// dividere per 100
			} else if (listOfScaleDiv_10.stream().anyMatch(str -> str.trim().equals(umConversion.trim()))) {
				return "-10";// dividere per 10
			} else if (umPlc.equals(umScada)) {
				return "-1";// per 1
			}
			logger.error("Conversion rule not found -> " + umConversion);
		}
		return "-1";
	}

	/**
	 * Verifico se il tag ha una scalatura o meno, leggendo da cio che Ë contenuto
	 * tra { e }
	 * 
	 * @param entry
	 * @return
	 */
	private String isScaleEnabled(EntryXlsx entry) {
		// TODO aggiungere test
		// TODO rimuovere virgole e punti e virgola
		String string = getContentOfBraces(entry.getfDescrizioneEstesa());
		if (!string.equals("NO PARAMETERS")) {
			String[] stringsArray = string.split("\\|");
			if (stringsArray.length == 5) {
				String umPLC = stringsArray[0];
				String umSCADA = stringsArray[1];
				if ((!umPLC.equals("X") || !umPLC.equals("x")) || (!umSCADA.equals("X") || !umSCADA.equals("x"))) {
					return "YES";
				}
			}
		}
		return "NO";
	}

	/**
	 * Verifico se il tag Ë un solo in lettura (NO) o Ë anche in scrittura (YES)
	 * 
	 * @param entry
	 * @return
	 */
	private String isEnableOutput(EntryXlsx entry) {
		// TODO aggiungere test
		if (entry.getfTipo().equals("bit_manual_cmd<WDI_BIT>") || entry.getfTipo().equals("string_write<WTX_STRING>")
				|| entry.getfTipo().equals("int_write<WAI_INT>") || entry.getfTipo().equals("real_write<WAI_REAL>")
				|| entry.getfTipo().equals("dint_write<WAI_DINT>") || entry.getfTipo().equals("bit_write<WDI_BIT>")) {
			return "YES";
		}
		return "NO";
	}

	/**
	 * Nella descrizione di alcuni tag per qualche motivo ci sono dei ritorni a capo
	 * a cazzo (colpa del plc) li rimuovo per evitare pasticci
	 * 
	 * @param entry
	 * @return
	 */
	private String cleanNewLines(EntryXlsx entry) {
		String s;
		if (entry.getfDescrizioneEstesa().trim().contains("\n")) {
			s = entry.getfDescrizioneEstesa().trim().replace("\n", " ");
		} else if (entry.getfDescrizioneEstesa().trim().contains("\r\n")) {
			s = entry.getfDescrizioneEstesa().trim().replace("\r\n", " ");
		} else if (entry.getfDescrizioneEstesa().trim().contains("\n\r")) {
			s = entry.getfDescrizioneEstesa().trim().replace("\n\r", " ");
		} else if (entry.getfDescrizioneEstesa().trim().contains("\r")) {
			s = entry.getfDescrizioneEstesa().trim().replace("\r", " ");
		} else {
			s = entry.getfDescrizioneEstesa().trim();
		}
		String s2 = s;
		if (s.contains("=")) {
			s2 = s.replace("=", "\'=\'");
		}
		String s3 = s2;
		if (s2.contains(";")) {
			s3 = s2.replace(";", " ");
		}

		return s3;
	}

	/**
	 * Isolo cio che si trova tra { e } leggendo dalla descrizione estesa
	 * 
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getContentOfBraces(String string) {

//		System.out.println(string);
		// TODO aggiungere test
		if (string.contains("{") && string.contains("}")) {
			String s1 = string.split("\\{")[1].trim();
			String s2 = s1.split("\\}")[0].trim();
			return s2;
		}

		return "NO PARAMETERS";
	}

	/**
	 * Isolo cio che si trova fuori dalle parentesi
	 * 
	 * @param getfDescrizioneEstesa
	 * @return
	 */
	private String getContentNoBraces(String string) {
		String s2 = "";
//		String s = string.replaceAll("\\{.*?\\}", "").trim().replaceAll("\\(.*?\\)", "");
		if (string.contains("=")) {
			s2 = string.replace("=", "\'=\'");
		}
		String s3 = s2.replaceAll(";", "");
		String s4 = s3.replaceAll("\"", "");
		System.out.println(s4);
		return s4;
	}

	/**
	 * @return the listEntry_DA
	 */
	public ArrayList<EntryXlsx> getListEntry_DA() {
		return listEntry_DA;
	}

	/**
	 * @return the listEntry_DI
	 */
	public ArrayList<EntryXlsx> getListEntry_DI() {
		return listEntry_DI;
	}

	/**
	 * @return the listEntry_AI
	 */
	public ArrayList<EntryXlsx> getListEntry_AI() {
		return listEntry_AI;
	}

	/**
	 * @return the listEntry_AA
	 */
	public ArrayList<EntryXlsx> getListEntry_AA() {
		return listEntry_AA;
	}

}
