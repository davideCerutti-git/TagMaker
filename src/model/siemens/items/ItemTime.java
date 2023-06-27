package model.siemens.items;

import org.apache.poi.ss.usermodel.Row;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public class ItemTime extends Item{

	private int value;
	
	public ItemTime(String dbName,String stringName, String stringComment, Address addressGlobal, ItemStruct _parent) {
		this.dbName=dbName;
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
			return "ItemTime [value=" + value + ", name=" + name + ", address=" + address +", simbolicName=" + this.getSimbolicName() +"]";
		return "ItemTime [value=" + value + ", name=" + name + ", comment=" + comment + ", address=" + address
				+", simbolicName=" + this.getSimbolicName() +"]";
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return new ItemTime(this.getDbName(),this.getName(), this.getComment(), this.getAddress().clone(), this.getParent());
	}

	public static Item makeItemFromString(ItemStruct workingStruct, String str, boolean typeChanged) {
		
		String comment = "";
		if(str.split("//").length>1) {
			comment=str.split("//")[1].trim();
		}
		if((ModelSiemens.getgAddr().gByte()%2)!=0) {
			ModelSiemens.getgAddr().incrByte(1);
		}
		ItemTime itemTime=new ItemTime(workingStruct.getDbName(),str.split(":")[0].trim(),comment, new Address(workingStruct.getAddress().getDB(),ModelSiemens.getgAddr().gByte(),0),workingStruct);
		ModelSiemens.getgAddr().incrementAddress(4,0);
//		ModelSiemens.logSiem.info(itemTime.toStringExtended());
		return itemTime;
	}

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
	
	@Override
	protected void insertItem(Item item, Row rowGen) {
		String strFormula = item.getDbName() + "_DB" + ItemStruct.intToStringFormatted(item.getAddress().getDB()) + "TIME"
				+ ItemStruct.intToStringFormatted(item.getAddress().gByte());
		rowGen.createCell(5).setCellValue(strFormula);
		rowGen.createCell(4).setCellValue("DB" + item.getAddress().getDB() + ".DBD" + item.getAddress().gByte());
		rowGen.createCell(3).setCellValue(item.getSimbolicName().toString());
		if (item.getSimbolicName().toString().contains(".R."))
			rowGen.createCell(6).setCellValue("Dint_read<AI_DINT>");// TODO questo � un Time ma per il momento viene
																	// trattato come DInt
		if (item.getSimbolicName().toString().contains(".W."))
			rowGen.createCell(6).setCellValue("Dint_write<WAI_DINT>");// TODO questo � un Time ma per il momento viene
																		// trattato come DInt

	}
}
