package model.siemens.items;

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
//		ModelSiemens.logSiem.info(this.toStringExtended());
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

}
