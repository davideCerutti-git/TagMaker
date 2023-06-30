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
	
}
