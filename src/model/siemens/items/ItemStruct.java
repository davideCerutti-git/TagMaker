package model.siemens.items;

import java.util.*;
import java.util.regex.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import model.siemens.*;

public class ItemStruct extends Item {

	private List<Item> listStructsList;
	private int sizeByte;
	private int sizeBit;
	private String titleUDT = "";

	public ItemStruct(String titleUdt, String dbName, String structName, String structComment, Address addressGlobal,
			ItemStruct _parent, int sizeByte, int sizeBit) {
		this.dbName = dbName;
		this.name = structName;
		this.comment = structComment;
		this.address = addressGlobal;
		this.listStructsList = new ArrayList<Item>();
		this.parent = _parent;
		this.sizeByte = sizeByte;
		this.sizeBit = sizeBit;
		this.titleUDT = titleUdt;
		super.toStringExtended();
	}

	public List<Item> getStructlist() {
		return getListStructsList();
	}

	public void addItem(Item struct) {
		if (this.getListStructsList() == null) {
			listStructsList = new ArrayList<Item>();
		}
		getListStructsList().add(struct);
	}

	public void allToString() {
		for (Object struct : getListStructsList()) {
			if (struct instanceof ItemStruct) {
				System.out.println(struct.toString());
				((ItemStruct) struct).allToString();
			} else if (struct instanceof ItemBool) {
				System.out.println("	" + struct.toString());
			} else if (struct instanceof ItemString) {
				System.out.println("	" + struct.toString());
			} else if (struct instanceof ItemInt) {
				System.out.println("	" + struct.toString());
			} else if (struct instanceof ItemDint) {
				System.out.println("	" + struct.toString());
			} else if (struct instanceof ItemReal) {
				System.out.println("	" + struct.toString());
			}
		}
	}

	@Override
	public String toString() {
		if (!comment.equals(""))
			return name + " (" + comment + ") " + " " + address;
		return name + " " + address;
	}

	public String toStringExtended() {
		if (comment.equals(""))
			return "Struct [UDTName=" + titleUDT + ",name=" + name + ", address=" + address + "]";
		return "Struct [UDTName=" + titleUDT + ",name=" + name + ", comment=" + comment + ", address=" + address + "]";
	}

	public static String toAlphabetic(int i) {
		if (i < 0) {
			return "-" + toAlphabetic(-i - 1);
		}
		int quot = i / 26;
		int rem = i % 26;
		char letter = (char) ((int) 'A' + rem);
		if (quot == 0) {
			return "" + letter;
		} else {
			return toAlphabetic(quot - 1) + letter;
		}
	}

	public int generateXlsx(XSSFWorkbook wb, Sheet sheet, int ind, String strName, CellStyle style, boolean flagFirstLineInStruct,boolean enableFlagFirstLineInStruct) {
		Font fontAlarms = makefontAlarms(wb);
		Font fontReads = makefontReads(wb);
		Font fontWrites = makefontWrites(wb);
		CellStyle styleAlarms = makeStyleAlarms(wb);
		CellStyle styleReads = makeStyleReads(wb);
		CellStyle styleWrites = makeStyleWrites(wb);
		CellStyle stylePlates = makeStylePlates(wb);

		CellStyle styleAlarms_InitialRow = makeStyleAlarms_InitialRow(wb);
		CellStyle styleReads_InitialRow = makeStyleReads_InitialRow(wb);
		CellStyle styleWrites_InitialRow = makeStyleWrites_InitialRow(wb);
		CellStyle stylePlates_InitialRow = makeStylePlates_InitialRow(wb);

		styleAlarms.setFont(fontAlarms);
		styleReads.setFont(fontReads);
		styleWrites.setFont(fontWrites);
		
		styleAlarms_InitialRow.setFont(fontAlarms);
		styleReads_InitialRow.setFont(fontReads);
		styleWrites_InitialRow.setFont(fontWrites);

		for (Item item : getListStructsList()) {
			if (item instanceof ItemStruct) {
				flagFirstLineInStruct = true;
				ind = ((ItemStruct) item).generateXlsx(wb, sheet, ind, strName, style,flagFirstLineInStruct,enableFlagFirstLineInStruct);
				flagFirstLineInStruct = false;
			} else {
				Row rowGen = sheet.createRow(ind);
				ind++;
				rowGen.createCell(0).setCellValue(item.getAddress().getDB()); // Numero della DB
				rowGen.createCell(1).setCellValue(item.getAddress().gByte());// Numero della word
				rowGen.createCell(2).setCellValue("");
				rowGen.createCell(3).setCellValue("");
				rowGen.createCell(4).setCellValue("");
				rowGen.createCell(5).setCellValue("");
				rowGen.createCell(6).setCellValue(getStringFromStruct(item));
				rowGen.createCell(7).setCellValue("");
				rowGen.createCell(8).setCellValue("");
				rowGen.createCell(9).setCellValue(getLongComment(item.getComment()));
				rowGen.createCell(10)
						.setCellFormula("CONCATENATE(" + toAlphabetic(8) + ind + "," + toAlphabetic(9) + ind + ")");
				rowGen.createCell(11).setCellValue(getLowLimit(item.getComment()));
				rowGen.createCell(12).setCellValue(getHightLimit(item.getComment()));
				rowGen.createCell(13).setCellValue(getUM(item.getComment()));
				rowGen.createCell(14).setCellValue("");
				rowGen.createCell(15).setCellValue("");
				rowGen.createCell(16).setCellValue(getLevel(item.getComment()));
				rowGen.createCell(17).setCellValue("");
				item.insertItem(item, rowGen);
				applyStyle(wb, styleAlarms, styleReads, styleWrites, stylePlates, styleAlarms_InitialRow,
						styleReads_InitialRow, styleWrites_InitialRow, stylePlates_InitialRow, flagFirstLineInStruct,enableFlagFirstLineInStruct, item,
						rowGen);
					flagFirstLineInStruct = false;
			}
		}
		return ind;
	}

	private String getStringFromStruct(Item item) {
		if (item.getType() == TAG_TYPE.ALARM_BIT)
			return "bit_Anomalies<DA_BIT>";
		if (item.getType() == TAG_TYPE.ALARM_BYTE)
			return "byte_Anomalies<DA_BYTE>";
		if (item.getType() == TAG_TYPE.ALARM_WORD)
			return "word_Anomalies<DA_WORD>";
		if (item.getType() == TAG_TYPE.ALARM_DWORD)
			return "dword_Anomalies<DA_DWORD>";
		if (item.getType() == TAG_TYPE.READ_BIT)
			return "bit_read<DI_BIT>";
		if (item.getType() == TAG_TYPE.READ_BYTE)
			return "byte_read<DI_BYTE>";
		if (item.getType() == TAG_TYPE.READ_INT || item.getType() == TAG_TYPE.PLATE)
			return "int_read<AI_INT>";
		if (item.getType() == TAG_TYPE.READ_DINT)
			return "dint_read<AI_DINT>";
		if (item.getType() == TAG_TYPE.READ_REAL)
			return "real_read<AI_REAL>";
		if (item.getType() == TAG_TYPE.READ_STRING)
			return "string_read<TX_STRING>";
		if (item.getType() == TAG_TYPE.WRITE_BIT)
			return "bit_write<WDI_BIT>";
		if (item.getType() == TAG_TYPE.WRITE_BYTE)
			return "byte_write<WDI_BYTE>";
		if (item.getType() == TAG_TYPE.WRITE_INT)
			return "int_write<WAI_INT>";
		if (item.getType() == TAG_TYPE.WRITE_DINT)
			return "dint_write<WAI_DINT>";
		if (item.getType() == TAG_TYPE.WRITE_REAL)
			return "real_write<WAI_REAL>";
		if (item.getType() == TAG_TYPE.WRITE_STRING)
			return "string_write<WTX_STRING>";
		ModelSiemens.logSiem.error("Errore tipo: " + item.toStringExtended());
		return "null type";
	}

	private void applyStyle(XSSFWorkbook wb, CellStyle styleAlarms, CellStyle styleReads, CellStyle styleWrites,
			CellStyle stylePlates, CellStyle styleAlarms_InitialRow, CellStyle styleReads_InitialRow,
			CellStyle styleWrites_InitialRow, CellStyle stylePlates_InitialRow, boolean flagFirstLineInStruct,boolean enableFlagFirstLineInStruct, Item item, Row rowGen) {
		for (int i = 0; i < rowGen.getLastCellNum(); i++) {
			if (rowGen.getCell(i) != null) {
				if (item.getType() == Item.TAG_TYPE.ALARM_BIT) {
					if (flagFirstLineInStruct&&enableFlagFirstLineInStruct) {
						rowGen.getCell(i).setCellStyle(styleAlarms_InitialRow);
					} else {
						rowGen.getCell(i).setCellStyle(styleAlarms);
					}
				}
				if (item.getType() == Item.TAG_TYPE.READ_BIT || item.getType() == Item.TAG_TYPE.READ_INT
						|| item.getType() == Item.TAG_TYPE.READ_DINT || item.getType() == Item.TAG_TYPE.READ_REAL
						|| item.getType() == Item.TAG_TYPE.READ_STRING || item.getType() == Item.TAG_TYPE.READ_BYTE) {

					if (flagFirstLineInStruct&&enableFlagFirstLineInStruct) {
						rowGen.getCell(i).setCellStyle(styleReads_InitialRow);
					} else {
						rowGen.getCell(i).setCellStyle(styleReads);
					}
				}
				if (item.getType() == Item.TAG_TYPE.WRITE_BIT || item.getType() == Item.TAG_TYPE.WRITE_INT
						|| item.getType() == Item.TAG_TYPE.WRITE_DINT || item.getType() == Item.TAG_TYPE.WRITE_REAL
						|| item.getType() == Item.TAG_TYPE.WRITE_STRING || item.getType() == Item.TAG_TYPE.WRITE_BYTE) {
					if (flagFirstLineInStruct&&enableFlagFirstLineInStruct) {
						rowGen.getCell(i).setCellStyle(styleWrites_InitialRow);
					} else {
						rowGen.getCell(i).setCellStyle(styleWrites);
					}
				}
				if (item.getType() == Item.TAG_TYPE.PLATE) {
					if (flagFirstLineInStruct&&enableFlagFirstLineInStruct) {
						rowGen.getCell(i).setCellStyle(stylePlates_InitialRow);
					} else {
						rowGen.getCell(i).setCellStyle(stylePlates);
					}
				}
			}
		}
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
	
	private CellStyle makeStyleWrites_InitialRow(Workbook wb) {
		CellStyle styleWrites = wb.createCellStyle();
		styleWrites.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		styleWrites.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWrites.setBorderBottom(BorderStyle.THIN);
		styleWrites.setBorderTop(BorderStyle.MEDIUM);
		styleWrites.setBorderRight(BorderStyle.THIN);
		styleWrites.setBorderLeft(BorderStyle.THIN);
		return styleWrites;
	}

	/**
	 * Crea lo stile per le celle relative ai tag tipo plates nel file excel
	 * 
	 * @param wb
	 * @return
	 */
	private CellStyle makeStylePlates(Workbook wb) {
		CellStyle styleWrites = wb.createCellStyle();
		styleWrites.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styleWrites.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWrites.setBorderBottom(BorderStyle.THIN);
		styleWrites.setBorderTop(BorderStyle.THIN);
		styleWrites.setBorderRight(BorderStyle.THIN);
		styleWrites.setBorderLeft(BorderStyle.THIN);
		return styleWrites;
	}
	
	private CellStyle makeStylePlates_InitialRow(Workbook wb) {
		CellStyle styleWrites = wb.createCellStyle();
		styleWrites.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styleWrites.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWrites.setBorderBottom(BorderStyle.THIN);
		styleWrites.setBorderTop(BorderStyle.MEDIUM);
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
	
	private CellStyle makeStyleReads_InitialRow(Workbook wb) {
		CellStyle styleReads = wb.createCellStyle();
		styleReads.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		styleReads.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleReads.setBorderBottom(BorderStyle.THIN);
		styleReads.setBorderTop(BorderStyle.MEDIUM);
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
	
	private CellStyle makeStyleAlarms_InitialRow(Workbook wb) {
		CellStyle styleAlarms = wb.createCellStyle();
		styleAlarms.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		styleAlarms.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleAlarms.setBorderBottom(BorderStyle.THIN);
		styleAlarms.setBorderTop(BorderStyle.MEDIUM);
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

	private String getLevel(String comment) {
		if (comment.isBlank() || comment.isEmpty())
			return "LineOps";
		Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(comment);
		while (m.find()) {
			comment = m.group(1);
		}
		Pattern pattern = Pattern.compile("[^|]*|");
		Matcher matcher = pattern.matcher(comment);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		if (count == 4) {
			if (comment.split("\\|")[4].equals("A") || comment.split("\\|")[4].equals("a"))
				return "Administrators";
		}
		return "LineOps";
	}

	private String getUM(String comment) {
		String str1, str2;
		comment = "" + comment.trim();
		if (comment.contains("[")) {
			comment = comment.split("\\[")[1];
			if (comment.endsWith("]"))
				comment = "" + comment.substring(0, comment.length() - 1);
			return "[" + comment + "]";
		} else {
			if (comment.contains("{")) {
				comment = comment.split("\\{")[1];
				if (comment.contains("}"))
					comment = comment.split("\\}")[0];
				if (comment.split("\\|")[0].equals("X") || comment.split("\\|")[0].equals("x"))
					str1 = "";
				else
					str1 = "[" + comment.split("\\|")[0].trim() + "]";
				if (comment.split("\\|")[1].equals("X") || comment.split("\\|")[1].equals("x"))
					str2 = "";
				else
					str2 = " [" + comment.split("\\|")[1].trim() + "]";
				return str1 + str2;
			}
		}
		return "";
	}

	private String getHightLimit(String comment) {
		if (comment.isBlank() || comment.isEmpty())
			return "99999999";
		Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(comment);
		while (m.find()) {
			comment = m.group(1);
		}
		Pattern pattern = Pattern.compile("[^|]*|");
		Matcher matcher = pattern.matcher(comment);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		if (count == 4) {
			if (comment.split("\\|")[3].equals("X") || comment.split("\\|")[3].equals("x"))
				return "99999999";
			return comment.split("\\|")[3];
		}
		return "99999999";
	}

	private String getLowLimit(String comment) {
		if (comment.isBlank() || comment.isEmpty())
			return "-99999999";
		Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(comment);
		while (m.find()) {
			comment = m.group(1);
		}
		Pattern pattern = Pattern.compile("[^|]*|");
		Matcher matcher = pattern.matcher(comment);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		if (count == 4) {
			if (comment.split("\\|")[2].equals("X") || comment.split("\\|")[2].equals("x"))
				return "-99999999";
			return comment.split("\\|")[2];
		}
		return "-99999999";
	}

	private String getLongComment(String comment) {
		return comment;
	}

	private String getShortComment(String comment) {
		comment = "" + comment.trim();
		if (comment.startsWith("(NU)"))
			comment = "" + comment.substring("(NU)".length());
		if (comment.startsWith("(IU)"))
			comment = "" + comment.substring("(IU)".length());
		if (comment.contains("["))
			comment = comment.split("\\[")[0];
		if (comment.contains("{"))
			comment = comment.split("\\{")[0];
		if (comment.contains("("))
			comment = comment.split("\\(")[0];
		comment = "" + comment.trim();
		String[] tokens = comment.split("\\.");
		int index = tokens.length - 1;
		return tokens[index].trim();
	}

	private String getAbbrev(String comment) {
		if (comment.startsWith("(NU)"))
			comment = "" + comment.substring("(NU)".length());
		String string;
		if (comment.contains("{")) {
			string = comment.split("\\{")[0];
		} else {
			string = "" + comment;
		}
		String[] tokens = string.split("\\.");
		int length = tokens.length;
		length = length - 2;
		StringBuffer resultStringBuffer = new StringBuffer();
		for (int j = 0; j <= length; j++) {
			if (j > 0)
				resultStringBuffer.append(".");
			resultStringBuffer.append(tokens[j]);
		}
		return resultStringBuffer.toString().trim();
	}

	public static String intToStringFormatted(int db) {
		if (db < 10000 && db >= 1000)
			return "" + db;
		if (db < 1000 && db >= 100)
			return "0" + db;
		if (db < 100 && db >= 10)
			return "00" + db;
		if (db < 10 && db >= 0)
			return "000" + db;
		return null;
	}

	public String getName() {
		return this.name;
	}

	public String getNameStruct() {
		return ((ItemStruct) this).name;
	}

	public int counterBit() {
		int cnt = 0;
		for (Object struct : getListStructsList()) {
			if (struct instanceof ItemBool) {
				cnt++;
			} else if (struct instanceof ItemStruct && ((ItemStruct) struct).isSelected()) {
				cnt = cnt + ((ItemStruct) struct).counterBit();
			} else {
				;
			}
		}
		return cnt;
	}

	@Override
	public void printSimbolicName() {
		System.out.println(this.getSimbolicName());
	}

	public void setAddressOffcet(Address addressGlobal) {
		for (Item item : getListStructsList()) {
			if (item instanceof ItemStruct) {
				item.addAddress(addressGlobal);
				((ItemStruct) item).setAddressOffcet(addressGlobal);
			} else {
				item.addAddress(addressGlobal);
			}
		}
	}

	public void printToConsolle() {
		for (Item item : getListStructsList()) {
			if (item instanceof ItemStruct) {
				item.printSimbolicName();
				((ItemStruct) item).printToConsolle();
			} else {
				item.printSimbolicName();
			}
		}
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		ItemStruct st = new ItemStruct(this.getTitleUDT(), this.getDbName(), this.getName(), this.getComment(),
				this.getAddress().clone(), this.getParent(), this.getByteSize(), this.getBitSize());
		st.getStructlist().clear();
		for (Item item : this.getStructlist()) {
			Item i = item.clone();
			i.updateParent(st);
			st.getStructlist().add(i);
		}
		return st;
	}

	public ItemStruct getParent() {
		return this.parent;
	}

	public void setListStructsList(List<Item> listStructsList) {
		this.listStructsList = listStructsList;
	}

	public void uploadAddress(Address addressGlobal) throws CloneNotSupportedException {
		for (Item item : this.getStructlist()) {
			if (item instanceof ItemStruct) {
				((ItemStruct) item).setAddress(addressGlobal.clone());
				((ItemStruct) item).uploadAddress(addressGlobal);
			} else if (item instanceof ItemBool) {
				((ItemBool) item).setAddress(addressGlobal.clone());
				addressGlobal.addBit();
			} else if (item instanceof ItemInt) {
				((ItemInt) item).setAddress(addressGlobal.clone());
				addressGlobal.addByte(2);
			} else if (item instanceof ItemDint) {
				((ItemDint) item).setAddress(addressGlobal.clone());
				addressGlobal.addByte(4);
			} else if (item instanceof ItemReal) {
				((ItemReal) item).setAddress(addressGlobal.clone());
				addressGlobal.addByte(4);
			} else if (item instanceof ItemString) {
				((ItemString) item).setAddress(addressGlobal.clone());
				int numChar = ((ItemString) item).getNumChar();
				if (numChar % 2 == 1)
					addressGlobal.addByte(numChar + 3);// TODO +3?? dispari , verificare
				else
					addressGlobal.addByte(numChar + 2);// TODO +3?? pari, verificare
			}
		}
	}

	public void setAddress(Address clone) {
		this.address = clone;
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String tmp_str) {
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		ItemStruct itemStruct = new ItemStruct(tmp_str, workingStruct.getDbName(), tmp_str, "",
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), workingStruct, 0,
				0);
		ModelSiemens.getgAddr().incrementAddress(0, 0);
		return itemStruct;
	}

	public static Item makeItemArrayFromString(ItemStruct workingStruct, String lineReaded) {
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		ItemStruct itemStruct = new ItemStruct("Struct generic", workingStruct.getDbName(),
				ModelSiemens.getNameItemFromStringLine(lineReaded), "",
				new Address(workingStruct.getAddress().getDB(), 0, 0), workingStruct, 0, 0);
		return itemStruct;
	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	public void setSize(int gByte, int gBit) {
		this.sizeByte = gByte;
		this.sizeBit = gBit;
	}

	public int getByteSize() {
		return this.sizeByte;
	}

	public int getBitSize() {
		return this.sizeBit;
	}

	@Override
	public void addAddresRec(Address gAddr) {
		this.address.add(gAddr);
		this.address.setDB_fromAddress(gAddr);
		for (Item item : getListStructsList()) {
			item.addAddresRec(gAddr);
		}
	}

	public void updateName(String nameItem) {
		this.name = nameItem;
	}

	@Override
	public void updateDbName(String nameDbItem) {
		this.dbName = nameDbItem;
		for (Item item : getListStructsList()) {
			item.updateDbName(nameDbItem);
		}
	}

	@Override
	public StringBuffer getSimbolicName() {
		if (parent != null)
			return parent.getSimbolicName().append("." + this.getName());
		else
			return new StringBuffer().append(this.getName());
	}

	public void setParent(ItemStruct parent) {
		this.parent = parent;
	}

	public String getTitleUDT() {
		return this.titleUDT;
	}

	public void setTitleUDT(String str) {
		this.titleUDT = str;
	}

	public void updateParent(ItemStruct workingStruct) {
		this.parent = workingStruct;
	}

	public void updateTitleUdt(String string) {
		this.titleUDT = string;
	}

	@Override
	public Address getByteOccupation() {
		Address addr = new Address(0, 0, 0);
		for (Item item : getListStructsList()) {
			addr.add(item.getByteOccupation());
		}
		return addr;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	public List<Item> getListStructsList() {
		return listStructsList;
	}

	@Override
	protected void insertItem(Item item, Row rowGen) {
		ModelSiemens.logSiem.error("ERROR: " + item.getName());
	}

}
