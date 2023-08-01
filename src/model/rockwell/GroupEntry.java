package model.rockwell;

import java.util.ArrayList;

public class GroupEntry {
	private ArrayList<EntryRockwellXls> entryBit;
	private ArrayList<EntryRockwellXls> entryInt;
	private ArrayList<EntryRockwellXls> entryDint;
	private ArrayList<EntryRockwellXls> entryReal;
	private ArrayList<EntryRockwellXls> entryString;
	private ArrayList<EntryRockwellXls> entryManCmd;

	public GroupEntry() {
		this.entryBit= new ArrayList<EntryRockwellXls>();
		this.entryInt= new ArrayList<EntryRockwellXls>();
		this.entryDint= new ArrayList<EntryRockwellXls>();
		this.entryReal= new ArrayList<EntryRockwellXls>();
		this.entryString= new ArrayList<EntryRockwellXls>();
		this.entryManCmd= new ArrayList<EntryRockwellXls>();
	}

	/**
	 * @return the entryBit
	 */
	public ArrayList<EntryRockwellXls> getEntryBit() {
		return entryBit;
	}

	/**
	 * @return the entryInt
	 */
	public ArrayList<EntryRockwellXls> getEntryInt() {
		return entryInt;
	}

	/**
	 * @return the entryDint
	 */
	public ArrayList<EntryRockwellXls> getEntryDint() {
		return entryDint;
	}

	/**
	 * @return the entryReal
	 */
	public ArrayList<EntryRockwellXls> getEntryReal() {
		return entryReal;
	}

	/**
	 * @return the entryString
	 */
	public ArrayList<EntryRockwellXls> getEntryString() {
		return entryString;
	}

	/**
	 * @return the entryManCmd
	 */
	public ArrayList<EntryRockwellXls> getEntryManCmd() {
		return entryManCmd;
	}

	/**
	 * @param entryBit the entryBit to set
	 */
	public void setEntryBit(ArrayList<EntryRockwellXls> entryBit) {
		this.entryBit = entryBit;
	}

	/**
	 * @param entryInt the entryInt to set
	 */
	public void setEntryInt(ArrayList<EntryRockwellXls> entryInt) {
		this.entryInt = entryInt;
	}

	/**
	 * @param entryDint the entryDint to set
	 */
	public void setEntryDint(ArrayList<EntryRockwellXls> entryDint) {
		this.entryDint = entryDint;
	}

	/**
	 * @param entryReal the entryReal to set
	 */
	public void setEntryReal(ArrayList<EntryRockwellXls> entryReal) {
		this.entryReal = entryReal;
	}

	/**
	 * @param entryString the entryString to set
	 */
	public void setEntryString(ArrayList<EntryRockwellXls> entryString) {
		this.entryString = entryString;
	}

	/**
	 * @param entryManCmd the entryManCmd to set
	 */
	public void setEntryManCmd(ArrayList<EntryRockwellXls> entryManCmd) {
		this.entryManCmd = entryManCmd;
	}
	
}
