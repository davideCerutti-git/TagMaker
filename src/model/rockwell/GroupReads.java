package model.rockwell;

import java.util.ArrayList;

public class GroupReads {
	private ArrayList<EntryRockwellXls> readBit;
	private ArrayList<EntryRockwellXls> readInt;
	private ArrayList<EntryRockwellXls> readDint;
	private ArrayList<EntryRockwellXls> readReal;
	private ArrayList<EntryRockwellXls> readString;
	private ArrayList<EntryRockwellXls> readManCmd;

	public GroupReads() {
		this.readBit = new ArrayList<EntryRockwellXls>();
		this.readInt = new ArrayList<EntryRockwellXls>();
		this.readDint = new ArrayList<EntryRockwellXls>();
		this.readReal = new ArrayList<EntryRockwellXls>();
		this.readString = new ArrayList<EntryRockwellXls>();
		this.readManCmd = new ArrayList<EntryRockwellXls>();
	}

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
