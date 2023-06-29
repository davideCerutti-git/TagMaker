package model.siemens.items;

import org.apache.poi.ss.usermodel.Row;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemBool extends Item {

	private boolean value;

	public ItemBool(String dbName, String stringName, String stringComment, Address addressGlobal, ItemStruct _parent) {
		this.dbName = dbName;
		this.value = false;
		this.name = stringName;
		this.comment = stringComment;
		this.address = addressGlobal;
		this.selected = false;
		this.parent = _parent;
		this.setUpType();
		super.toStringExtended();
	}

	@Override
	public String toString() {
		return name + " " + address + " " + comment;
	}

	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemBool [value=" + value + ", name=" + name + ", address=" + address + ", simbolicName="
					+ this.getSimbolicName() + "]";
		return "ItemBool [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+ ", simbolicName=" + this.getSimbolicName() + "]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemBool(this.getDbName(), this.getName(), this.getComment(), this.getAddress().clone(),
				this.getParent());
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String str, boolean typeChanged) {
		String name, comment = "";
		name = str.split(":")[0].trim();
		if (str.split("//").length > 1) {
			comment = str.split("//")[1].trim();
		}
		ItemBool itemBool = new ItemBool(workingStruct.getDbName(), name, comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(),
						ModelSiemens.getgAddr().gBit()),
				workingStruct);
		ModelSiemens.getgAddr().incrementAddress(0, 1);
//		ModelSiemens.logSiem.info(itemBool.toStringExtended());
		return itemBool;
	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	public void addAddresRec(Address gAddr) {
		this.address.add(gAddr);
		this.address.setDB_fromAddress(gAddr);
//		ModelSiemens.getgAddr().incrementAddress(0, 1);
	}

	@Override
	public StringBuffer getSimbolicName() {
//		ModelSiemens.logSiem.error("###"+parent.getName());
		return parent.getSimbolicName().append("." + this.getName());
	}

	public void updateParent(ItemStruct workingStruct) {
		this.parent=workingStruct;
	}

	@Override
	public Address getByteOccupation() {
		return new Address(0,0,1);
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
	public  void insertItem(Item item, Row rowGen) {
		String strFormula;
		rowGen.createCell(2).setCellValue(item.getAddress().gBit());
		rowGen.createCell(3).setCellValue(item.getSimbolicName().toString());
		rowGen.createCell(4).setCellValue(
				"DB" + item.getAddress().getDB() + ".DBX" + item.getAddress().gByte() + "." + item.getAddress().gBit());
		if (false) {
//		if (struct.getSimbolicName().toString().contains("ALARM")) {
//			strFormula = struct.getDbName() + "_DB" + intToStringFormatted(struct.getAddress().getDB()) + "X"
//					+ intToStringFormatted(struct.getAddress().gByte()) + "_" + struct.getAddress().gBit() + "_ALM";
//			rowGen.createCell(5).setCellValue(strFormula);
//			rowGen.createCell(6).setCellValue("bit_Anomalies<DA_BIT>");
//			for (int i = 0; i < 6; i++) {
//				if (rowGen.getCell(i) != null) {
//					rowGen.getCell(i).setCellStyle(style);
//				}
//			}
		} else {
			strFormula = item.getDbName() +Item.getStringTypeForSCADATag(item)+ "_DB" + ItemStruct.intToStringFormatted(item.getAddress().getDB()) + "X"
					+ ItemStruct.intToStringFormatted(item.getAddress().gByte()) + "_" + item.getAddress().gBit();
			rowGen.createCell(5).setCellValue(strFormula);
			if (item.getSimbolicName().toString().contains(".W.ManCmd"))
				rowGen.createCell(6).setCellValue("bit_manual_cmd<WDI_BIT>");
			if (item.getSimbolicName().toString().contains(".R.ManCmd"))
				rowGen.createCell(6).setCellValue("bit_manual_cmd<DI_BIT>");
			if (item.getSimbolicName().toString().contains(".R._bool.W"))
				rowGen.createCell(6).setCellValue("bit_read<DI_BIT>");
			if (item.getSimbolicName().toString().contains(".W._bool.W"))
				rowGen.createCell(6).setCellValue("bit_Write<WDI_BIT>");
		}
	}

}
