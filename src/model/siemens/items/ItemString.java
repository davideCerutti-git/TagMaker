package model.siemens.items;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.siemens.Address;
import model.siemens.ModelSiemens;

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
//		ModelSiemens.logSiem.info(this.toStringExtended());

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
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0),0, workingStruct);
		ModelSiemens.getgAddr().incrementAddress(256, 0);
		return itemString;
	}
	
	public static ItemString makeItemFromStringInArrayForm(ItemStruct workingStruct, String str,
			boolean typeChanged) {
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
		
		String indexString=ModelSiemens.removeInitialBrackets(str);
		indexString=ModelSiemens.removeComment(indexString);
		indexString=indexString.split(":")[1].trim();
		Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(indexString);
		while (m.find()) {
			indexString=m.group(1);
		}
		int index=0;
		if(isNumeric(indexString)) {
			index=Integer.parseInt(indexString);
		}else {
			if (ModelSiemens.getPLCTags().get(indexString) != null) {
				index = Integer.parseInt(ModelSiemens.getPLCTags().get(indexString));
			} else {
				ModelSiemens.logSiem.error("L'indice letto non è stato trovato!");
			}
		}
//		ModelSiemens.logSiem.info("---> "+index);
		
		ItemString itemString = new ItemString(workingStruct.getDbName(), str.split(":")[0].trim(), comment,
				new Address(workingStruct.getAddress().getDB(), ModelSiemens.getgAddr().gByte(), 0),0, workingStruct);
		ModelSiemens.getgAddr().incrementAddress(index+2, 0);
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
	

//	private static void incrementAddress(int[] tmp_num_word, int[] tmp_num_bit) {
//		// TODO da implementare
//		System.err.println("Error increment not implemented!!");
////		if(tmp_num_bit[0]>0) {
////			tmp_num_word[0]++;
////		
////			tmp_num_bit[0]=0;
////		}
////		tmp_num_word[0]=tmp_num_word[0]+4;
//	}

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
//		ModelSiemens.getgAddr().incrementAddress(4, 0);
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


}
