package model.siemens.items;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemIEC_TIMER extends ItemStruct {

	public ItemIEC_TIMER(String dbName, String stringName, String stringComment, Address addressGlobal,ItemStruct _parent) throws CloneNotSupportedException {
		
		super("IEC_TIMER", dbName, stringName, stringComment, addressGlobal, _parent, 16, 0);
		
		this.dbName = dbName;
		this.name = stringName;
		this.comment = stringComment;
		this.address = addressGlobal;
		this.selected = false;
		this.parent = _parent;
		this.setUpType();
		
		
		if (ModelSiemens.getgAddr().gBit() > 0) {
			ModelSiemens.getgAddr().incrementAddress(1, 0);
			ModelSiemens.getgAddr().setBit(0);
		}
		if ((ModelSiemens.getgAddr().gByte() % 2) != 0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		
		this.getListStructsList().add(new ItemTime(dbName, "PT", "",  ModelSiemens.getgAddr().clone(), this));
		ModelSiemens.getgAddr().incrementAddress(4, 0);
		this.getListStructsList().add(new ItemTime(dbName, "ET", "", ModelSiemens.getgAddr().clone(), this));
		ModelSiemens.getgAddr().incrementAddress(4, 1);//ci sono indirizzi non visibili ma utilizzati in tia portal
		this.getListStructsList().add(new ItemBool(dbName, "IN", "", ModelSiemens.getgAddr().clone(), this));
		ModelSiemens.getgAddr().incrementAddress(0, 1);
		this.getListStructsList().add(new ItemBool(dbName, "Q", "", ModelSiemens.getgAddr().clone(), this));
		ModelSiemens.getgAddr().incrementAddress(3, 1); //ci sono indirizzi non visibili ma utilizzati in tia portal
		super.toStringExtended();
	}

	@Override
	public String toString() {
		return name + " " + address;
	}

	@Override
	public String toStringExtended() {
		if (comment.equals(""))
			return "ItemIEC_TIMER [name=" + name + ", address=" + address + ", simbolicName=" + this.getSimbolicName()
					+ "]";
		return "ItemIEC_TIMER [name=" + name + ", comment=" + comment + ", address=" + address + ", simbolicName="
				+ this.getSimbolicName() + "]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemIEC_TIMER(this.getDbName(), this.getName(), this.getComment(), this.getAddress().clone(),
				this.getParent());
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String str, boolean typeChanged) throws CloneNotSupportedException {

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
		ModelSiemens.getgAddr().incrementAddress(4, 0);
		
		ItemIEC_TIMER itemIEC_TIMER = new ItemIEC_TIMER(workingStruct.getDbName(), str.split(":")[0].trim(), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0), workingStruct);
//		ModelSiemens.getgAddr().incrementAddress(16, 0);
//		ModelSiemens.logSiem.info(itemIEC_TIMER.toStringExtended());
		return itemIEC_TIMER;
	}

//	private static void incrementAddress(int[] tmp_num_word, int[] tmp_num_bit) {
//		if(tmp_num_bit[0]>0) {
//			tmp_num_word[0]++;
//
//			tmp_num_bit[0]=0;
//		}
//
//		tmp_num_word[0]=tmp_num_word[0]+16;
//	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	public void addAddresRec(Address gAddr) {
		this.address.add(gAddr);
//		ModelSiemens.getgAddr().incrementAddress(16, 0);
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
		return new Address(0,16,0);
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
