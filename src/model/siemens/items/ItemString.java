package model.siemens.items;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Row;
import model.siemens.*;

public class ItemString extends Item {

	private String value;
	private int numChar;

	public ItemString(String dbName, String stringName, String stringComment, Address addressGlobal, int numChar,
			ItemStruct _parent) {
		this.dbName = dbName;
		this.value = "";
		this.name = stringName;
		this.comment = stringComment;
		this.address = addressGlobal;
		this.selected = false;
		this.numChar = numChar;
		this.parent = _parent;
		this.setUpType();
		super.toStringExtended();
	}

	@Override
	public String toString() {
		return name + " " + address;
	}

	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemString [value=" + value + ", name=" + name + ", address=" + address + ", simbolicName="
					+ this.getSimbolicName() + "]";
		return "ItemString [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+ ", simbolicName=" + this.getSimbolicName() + "]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemString(this.getDbName(), this.getName(), this.getComment(), this.getAddress().clone(),
				this.getNumChar(), this.getParent());
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String str, boolean typeChanged) {
		String comment = "";
		if (str.split("//").length > 1) {
			comment = str.split("//")[1].trim();
		}
		if (typeChanged) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
		}
		ItemString itemString = new ItemString(workingStruct.getDbName(), str.split(":")[0].trim(), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), 0, workingStruct);
		ModelSiemens.getgAddr().incrementAddress(256, 0);
		return itemString;
	}

	public static ItemString makeItemFromStringInArrayForm(ItemStruct workingStruct, String str, boolean typeChanged) {
		String comment = "";
		if (str.split("//").length > 1) {
			comment = str.split("//")[1].trim();
		}
		if (typeChanged) {
			if (ModelSiemens.getgAddr().gBit() > 0) {
				ModelSiemens.getgAddr().incrementAddress(1, 0);
				ModelSiemens.getgAddr().setBit(0);
			}
			if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
				ModelSiemens.getgAddr().incrByte(1);
			}
		}
		String indexString = ModelSiemens.removeInitialBrackets(str);
		indexString = ModelSiemens.removeComment(indexString);
		indexString = indexString.split(":")[1].trim();
		Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(indexString);
		while (m.find()) {
			indexString = m.group(1);
		}
		int index = 0;
		if (isNumeric(indexString)) {
			index = Integer.parseInt(indexString);
		} else {
			if (ModelSiemens.getPLCTags().get(indexString) != null) {
				index = Integer.parseInt(ModelSiemens.getPLCTags().get(indexString));
			} else {
				ModelSiemens.logSiem.error("L'indice letto non è stato trovato!");
			}
		}
		ItemString itemString = new ItemString(workingStruct.getDbName(), str.split(":")[0].trim(), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), 0, workingStruct);
		ModelSiemens.getgAddr().incrementAddress(index + 2, 0);
		return itemString;
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public int getNumChar() {
		return numChar;
	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	public void addAddresRec(Address gAddr) {
		this.address.setDB_fromAddress(gAddr);
		this.address.add(gAddr);
	}

	@Override
	public StringBuffer getSimbolicName() {
		return parent.getSimbolicName().append("." + this.getName());
	}

	public void updateParent(ItemStruct workingStruct) {
		this.parent = workingStruct;
	}

	@Override
	public Address getByteOccupation() {
		return new Address(0, 256, 0);
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public void updateDbName(String nameDbItem) {
		this.dbName = nameDbItem;
	}

	@Override
	protected void insertItem(Item item, Row rowGen) {
		String strFormula = item.getDbName() + Item.getStringTypeForSCADATag(item) + "_DB"
				+ ItemStruct.intToStringFormatted(item.getAddress().getDB()) + "TX"
				+ ItemStruct.intToStringFormatted(item.getAddress().gByte());
		rowGen.createCell(5).setCellValue(strFormula);
		rowGen.createCell(4).setCellValue("DB" + item.getAddress().getDB() + ".DBS" + item.getAddress().gByte());
		rowGen.createCell(3).setCellValue(item.getSimbolicName().toString());
		if (item.getSimbolicName().toString().contains(".R."))
			rowGen.createCell(6).setCellValue("String_read<AI_STRING>");
		if (item.getSimbolicName().toString().contains(".W."))
			rowGen.createCell(6).setCellValue("String_write<WAI_STRING>");
	}

}
