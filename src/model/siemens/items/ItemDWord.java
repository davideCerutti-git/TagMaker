package model.siemens.items;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemDWord extends Item{

	private int value;
	
	public ItemDWord(String dbName,String stringName, String stringComment, Address addressGlobal, ItemStruct _parent) {
		this.dbName=dbName;
		this.value = 0;
		this.name = stringName;
		this.comment = stringComment;
		this.address = addressGlobal;
		this.selected = false;
//		this.simbolicName=_simbolicName;
		this.parent = _parent;
		this.setUpType();
		ModelSiemens.logSiem.info(this.toStringExtended());
	}

	@Override
	public String toString() {
		return name + " " + address;
	}
	
	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemDWord [value=" + value + ", name=" + name + ", address=" + address +", simbolicName=" + this.getSimbolicName() +"]";
		return "ItemDWord [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+", simbolicName=" + this.getSimbolicName() +"]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemDWord(this.getDbName(),this.getName(), this.getComment(), this.getAddress().clone(), this.getParent());
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String str, boolean typeChanged) {
		
		String comment = "";
		if(str.split("//").length>1) {
			comment=str.split("//")[1].trim();
		}
		if((ModelSiemens.getgAddr().gByte()%2)!=0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		ItemDWord itemWord=new ItemDWord(workingStruct.getDbName(),str.split(":")[0].trim(),comment, new Address(workingStruct.getAddress().getDB(),ModelSiemens.getgAddr().gByte(),0),workingStruct);
		ModelSiemens.getgAddr().incrementAddress(4,0);
//		ModelSiemens.logSiem.info(itemWord.toStringExtended());
		return itemWord;
	}

//	private static void incrementAddress(int num) {
//		if(ModelSiemens.getgAddr().gBit()>0) {
//			ModelSiemens.getgAddr().incrByte(1);
//			ModelSiemens.getgAddr().setBit(0);
//		}
//		ModelSiemens.getgAddr().incrByte(num);
//	}

	@Override
	public Address getAddress() {
		return this.address;
	}
	
	public void addAddresRec(Address gAddr) {
		this.address.add(gAddr);
//		ModelSiemens.getgAddr().incrementAddress(4, 0);
	}

	@Override
	public StringBuffer getSimbolicName() {
		return parent.getSimbolicName().append("."+this.getName());
	};
	public void updateParent(ItemStruct workingStruct) {
		this.parent = workingStruct;
	}
	
	@Override
	public Address getByteOccupation() {
		return new Address(0,4,0);
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
