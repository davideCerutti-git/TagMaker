package model.rockwell;

import java.util.ArrayList;

public class GroupWrites {
	private ArrayList<EntryRockwellXls> writeBit;
	private ArrayList<EntryRockwellXls> writeInt;
	private ArrayList<EntryRockwellXls> writeDint;
	private ArrayList<EntryRockwellXls> writeReal;
	private ArrayList<EntryRockwellXls> writeString;
	private ArrayList<EntryRockwellXls> writeManCmd;

	public GroupWrites() {
		this.writeBit = new ArrayList<EntryRockwellXls>();
		this.writeInt = new ArrayList<EntryRockwellXls>();
		this.writeDint = new ArrayList<EntryRockwellXls>();
		this.writeReal = new ArrayList<EntryRockwellXls>();
		this.writeString = new ArrayList<EntryRockwellXls>();
		this.writeManCmd = new ArrayList<EntryRockwellXls>();
	}

	public ArrayList<EntryRockwellXls> getWriteBit() {
		return this.writeBit;
	}

	public void setWriteBit(ArrayList<EntryRockwellXls> writeBit) {
		this.writeBit = writeBit;
	}

	public ArrayList<EntryRockwellXls> getWriteInt() {
		return this.writeInt;
	}

	public void setWriteInt(ArrayList<EntryRockwellXls> writeInt) {
		this.writeInt = writeInt;
	}

	public ArrayList<EntryRockwellXls> getWriteDint() {
		return this.writeDint;
	}

	public void setWriteDint(ArrayList<EntryRockwellXls> writeDint) {
		this.writeDint = writeDint;
	}

	public ArrayList<EntryRockwellXls> getWriteReal() {
		return this.writeReal;
	}

	public void setWriteReal(ArrayList<EntryRockwellXls> writeReal) {
		this.writeReal = writeReal;
	}

	public ArrayList<EntryRockwellXls> getWriteString() {
		return this.writeString;
	}

	public void setWriteString(ArrayList<EntryRockwellXls> writeString) {
		this.writeString = writeString;
	}

	public ArrayList<EntryRockwellXls> getWriteManCmd() {
		return this.writeManCmd;
	}

	public void setWriteManCmd(ArrayList<EntryRockwellXls> writeManCmd) {
		this.writeManCmd = writeManCmd;
	}

}
