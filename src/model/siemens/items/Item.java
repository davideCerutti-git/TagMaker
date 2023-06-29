package model.siemens.items;

import org.apache.poi.ss.usermodel.Row;

import model.siemens.Address;
import model.siemens.ModelSiemens;

public abstract class Item {

	// CLASSE DA FINIRE
	protected String dbName;
	protected String name;
	protected String comment;
//	protected String simbolicName;
	protected Address address;
	protected boolean selected;
	protected Item.TAG_TYPE tagType;
	protected Item.TAG_CATEGORY tagCategory;
	protected ItemStruct parent;


	enum TAG_TYPE {
		NULL, READ_BIT, READ_BYTE, READ_INT, READ_DINT, READ_REAL, READ_STRING, WRITE_BIT, WRITE_BYTE, WRITE_INT,
		WRITE_DINT, WRITE_REAL, WRITE_STRING, ALARM_BIT,ALARM_BYTE,ALARM_WORD,ALARM_DWORD, PLATE;
	}
	
	enum TAG_CATEGORY {
		NULL, ALARM, READ, WRITE;
	}

	// ######################################
	// ######## GETTER e SETTER #############

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract String getComment();

	public void setComment(String comment) {
		this.comment = comment;
	}

	public abstract Address getAddress();

	public void printSimbolicName() {
		ModelSiemens.logSiem.info(this.getSimbolicName() + "  " + address + " " + comment);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setAddress(Address _address) {
		this.address = _address;
	}

	public abstract StringBuffer getSimbolicName();

	public void addAddress(Address addressGlobal) {
		this.address.add(addressGlobal);
	}

	@Override
	public abstract Item clone() throws CloneNotSupportedException;

	@Override
	public abstract String toString();

	public String toStringExtended() {
//		ModelSiemens.logSiem.info(this.toStringExtended());
		return this.toStringExtended();
	}

	public void setUpType() {
		if (!(this instanceof ItemStruct)) {
			if (this.getSimbolicName().toString().toUpperCase().contains("ALM")) {
				this.tagType = TAG_TYPE.ALARM_BIT;
				this.tagCategory = TAG_CATEGORY.ALARM;
//			} else if (this.getSimbolicName().toString().toUpperCase().contains("CAP")
//					|| this.getSimbolicName().toString().toUpperCase().contains("PLATE")) {
//				this.tagType = TAG_TYPE.PLATE;
			} 

			if (this.getSimbolicName().toString().toUpperCase().contains("HMW")
					|| this.getSimbolicName().toString().toUpperCase().contains("HM_W")
					|| this.getSimbolicName().toString().toUpperCase().contains("WRITE")
					|| this.getSimbolicName().toString().toUpperCase().contains("MANCOM")) {
				this.tagCategory = TAG_CATEGORY.WRITE;
				if (this instanceof ItemBool) {
					this.tagType = TAG_TYPE.WRITE_BIT;
				} else if (this instanceof ItemInt) {
					this.tagType = TAG_TYPE.WRITE_INT;
				} else if (this instanceof ItemDint) {
					this.tagType = TAG_TYPE.WRITE_DINT;
				} else if (this instanceof ItemReal) {
					this.tagType = TAG_TYPE.WRITE_REAL;
				} else if (this instanceof ItemString) {
					this.tagType = TAG_TYPE.WRITE_STRING;
				} else if (this instanceof ItemByte) {
					this.tagType = TAG_TYPE.WRITE_BYTE;
				}else {
					this.tagType = TAG_TYPE.NULL;
				}
			} 

			if (this.getSimbolicName().toString().toUpperCase().contains("HMR")
					|| this.getSimbolicName().toString().toUpperCase().contains("HM_R")
					|| this.getSimbolicName().toString().toUpperCase().contains("READ")) {
				this.tagCategory = TAG_CATEGORY.READ;
				if (this instanceof ItemBool) {
					this.tagType = TAG_TYPE.READ_BIT;
				} else if (this instanceof ItemInt) {
					this.tagType = TAG_TYPE.READ_INT;
				} else if (this instanceof ItemDint) {
					this.tagType = TAG_TYPE.READ_DINT;
				} else if (this instanceof ItemReal) {
					this.tagType = TAG_TYPE.READ_REAL;
				} else if (this instanceof ItemString) {
					this.tagType = TAG_TYPE.READ_STRING;
				} else if (this instanceof ItemByte) {
					this.tagType = TAG_TYPE.READ_BYTE;
				}else {
					this.tagType = TAG_TYPE.NULL;
				}
			}
		}

	}

	protected TAG_TYPE getType() {
		return this.tagType;
	}
	
	protected TAG_CATEGORY getCategory() {
		return this.tagCategory;
	}

	public String getDbName() {
		return this.dbName;
	}

	public abstract void addAddresRec(Address gAddr);

	/**
	 * @return the parent
	 */
	public ItemStruct getParent() {
		return parent;
	}

	protected abstract void updateParent(ItemStruct st);

	public abstract Address getByteOccupation();

	protected abstract void updateDbName(String nameDbItem);

	protected void insertItem(Item item, Row rowGen) {
		ModelSiemens.logSiem.error("ERROR: " + item.getName());
	}

	public static String getStringTypeForSCADATag(Item item) {
		if(item.getCategory()==TAG_CATEGORY.ALARM) {
			return "_ALM";
		}
		
		if(item.getCategory()==TAG_CATEGORY.WRITE) {
			return "_HMW";
		}
		if(item.getCategory()==TAG_CATEGORY.READ) {
			return "_HMR";
		}
		
		return "";
	}

	

}
