package model.siemens.items;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemWord extends Item {

	private int value;

	public ItemWord(String dbName, String stringName, String stringComment, Address addressGlobal, ItemStruct _parent) {
		this.dbName = dbName;
		this.value = 0;
		this.name = stringName;
		this.comment = stringComment;
		this.address = addressGlobal;
		this.selected = false;
//		this.simbolicName=_simbolicName;
		this.parent = _parent;
		this.setUpType();
//		ModelSiemens.logSiem.info(this.toStringExtended());
	}

	@Override
	public String toString() {
		return name + " " + address;
	}

	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemWord [value=" + value + ", name=" + name + ", address=" + address + ", simbolicName="
					+ this.getSimbolicName() + "]";
		return "ItemWord [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+ ", simbolicName=" + this.getSimbolicName() + "]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemWord(this.getDbName(), this.getName(), this.getComment(), this.getAddress().clone(),
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
		ItemWord itemWord = new ItemWord(workingStruct.getDbName(), str.split(":")[0].trim(), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), workingStruct);
		ModelSiemens.getgAddr().incrementAddress(2, 0);
//		ModelSiemens.logSiem.info(itemWord.toStringExtended());
		return itemWord;
	}

//	private static void incrementAddress(int[] tmp_num_word, int[] tmp_num_bit) {
//		if(tmp_num_bit[0]>0) {
//			tmp_num_word[0]++;
//			tmp_num_bit[0]=0;
//		}
//
//		tmp_num_word[0]=tmp_num_word[0]+2;
//	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	public void addAddresRec(Address gAddr) {
		this.address.setDB_fromAddress(gAddr);
		this.address.add(gAddr);
//		ModelSiemens.getgAddr().incrementAddress(2, 0);
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
}
