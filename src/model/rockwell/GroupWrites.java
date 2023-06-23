package model.rockwell;

import java.util.ArrayList;

public class GroupWrites {
	
	ArrayList<EntryRockwellXls> writeBit;
	ArrayList<EntryRockwellXls> writeInt;
	ArrayList<EntryRockwellXls> writeDint;
	ArrayList<EntryRockwellXls> writeReal;
	ArrayList<EntryRockwellXls> writeString;
	ArrayList<EntryRockwellXls> writeManCmd;
	
	
	
	
	
	
	public GroupWrites() {
		writeBit= new ArrayList<EntryRockwellXls>();
		writeInt= new ArrayList<EntryRockwellXls>();
		writeDint= new ArrayList<EntryRockwellXls>();
		writeReal= new ArrayList<EntryRockwellXls>();
		writeString= new ArrayList<EntryRockwellXls>();
		writeManCmd= new ArrayList<EntryRockwellXls>();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Getter and Setter
	 */
	public ArrayList<EntryRockwellXls> getWriteBit() {
		return writeBit;
	}
	public void setWriteBit(ArrayList<EntryRockwellXls> writeBit) {
		this.writeBit = writeBit;
	}
	public ArrayList<EntryRockwellXls> getWriteInt() {
		return writeInt;
	}
	public void setWriteInt(ArrayList<EntryRockwellXls> writeInt) {
		this.writeInt = writeInt;
	}
	public ArrayList<EntryRockwellXls> getWriteDint() {
		return writeDint;
	}
	public void setWriteDint(ArrayList<EntryRockwellXls> writeDint) {
		this.writeDint = writeDint;
	}
	public ArrayList<EntryRockwellXls> getWriteReal() {
		return writeReal;
	}
	public void setWriteReal(ArrayList<EntryRockwellXls> writeReal) {
		this.writeReal = writeReal;
	}
	public ArrayList<EntryRockwellXls> getWriteString() {
		return writeString;
	}
	public void setWriteString(ArrayList<EntryRockwellXls> writeString) {
		this.writeString = writeString;
	}
	public ArrayList<EntryRockwellXls> getWriteManCmd() {
		return writeManCmd;
	}
	public void setWriteManCmd(ArrayList<EntryRockwellXls> writeManCmd) {
		this.writeManCmd = writeManCmd;
	}
	
	
	
	
	
	
	
	
	

}
