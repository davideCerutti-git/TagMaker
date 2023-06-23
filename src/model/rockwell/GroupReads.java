package model.rockwell;

import java.util.ArrayList;

public class GroupReads {
	
	ArrayList<EntryRockwellXls> readBit;
	ArrayList<EntryRockwellXls> readInt;
	ArrayList<EntryRockwellXls> readDint;
	ArrayList<EntryRockwellXls> readReal;
	ArrayList<EntryRockwellXls> readString;
	ArrayList<EntryRockwellXls> readManCmd;
	
	
	
	public GroupReads() {
		readBit= new ArrayList<EntryRockwellXls>();
		readInt= new ArrayList<EntryRockwellXls>();
		readDint= new ArrayList<EntryRockwellXls>();
		readReal= new ArrayList<EntryRockwellXls>();
		readString= new ArrayList<EntryRockwellXls>();
	    readManCmd= new ArrayList<EntryRockwellXls>();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Getter and Setter
	 */
	public ArrayList<EntryRockwellXls> getReadBit() {
		return readBit;
	}
	public void setReadBit(ArrayList<EntryRockwellXls> readBit) {
		this.readBit = readBit;
	}
	public ArrayList<EntryRockwellXls> getReadInt() {
		return readInt;
	}
	public void setReadInt(ArrayList<EntryRockwellXls> readInt) {
		this.readInt = readInt;
	}
	public ArrayList<EntryRockwellXls> getReadDint() {
		return readDint;
	}
	public void setReadDint(ArrayList<EntryRockwellXls> readDint) {
		this.readDint = readDint;
	}
	public ArrayList<EntryRockwellXls> getReadReal() {
		return readReal;
	}
	public void setReadReal(ArrayList<EntryRockwellXls> readReal) {
		this.readReal = readReal;
	}
	public ArrayList<EntryRockwellXls> getReadString() {
		return readString;
	}
	public void setReadString(ArrayList<EntryRockwellXls> readString) {
		this.readString = readString;
	}
	public ArrayList<EntryRockwellXls> getReadManCmd() {
		return readManCmd;
	}
	public void setReadManCmd(ArrayList<EntryRockwellXls> readManCmd) {
		this.readManCmd = readManCmd;
	}
	
	
	
	
	

}
