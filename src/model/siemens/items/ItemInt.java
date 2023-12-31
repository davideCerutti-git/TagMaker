package model.siemens.items;

import org.apache.poi.ss.usermodel.Row;
import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemInt extends Item {

	private int value;

	public ItemInt(String dbName, String stringName, String stringComment, Address addressGlobal, ItemStruct _parent) {
		this.dbName = dbName;
		this.value = 0;
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
		return name + " " + address;
	}

	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemInt [value=" + value + ", name=" + name + ", address=" + address + ", simbolicName="
					+ this.getSimbolicName() + "]";
		return "ItemInt [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+ ", simbolicName=" + this.getSimbolicName() + "]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemInt(this.getDbName(), this.getName(), this.getComment(), this.getAddress().clone(),
				this.getParent());
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
		ItemInt itemInt = new ItemInt(workingStruct.getDbName(), ModelSiemens.getNameString(str), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), workingStruct);
		ModelSiemens.getgAddr().incrementAddress(2, 0);
		return itemInt;
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
	};
	
    public void updateParent(ItemStruct workingStruct) {
		this.parent = workingStruct;
	}
	
	@Override
	public Address getByteOccupation() {
		return new Address(0,2,0);
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
		String strFormula = item.getDbName() + Item.getStringTypeForSCADATag(item)+"_DB" + ItemStruct.intToStringFormatted(item.getAddress().getDB()) + "INT"
				+ ItemStruct.intToStringFormatted(item.getAddress().gByte());
		rowGen.createCell(5).setCellValue(strFormula);
		rowGen.createCell(4).setCellValue("DB" + item.getAddress().getDB() + ".DBW" + item.getAddress().gByte());
		rowGen.createCell(3).setCellValue(item.getSimbolicName().toString());

		if (item.getSimbolicName().toString().contains(".R."))
			rowGen.createCell(6).setCellValue("Int_read<AI_INT>");
		if (item.getSimbolicName().toString().contains(".W."))
			rowGen.createCell(6).setCellValue("Int_write<WAI_INT>");
	}
	
}
