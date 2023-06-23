package model.rockwell;

import java.util.ArrayList;

public class GroupEntry {
	ArrayList<EntryRockwellXls> entryBit;
	ArrayList<EntryRockwellXls> entryInt;
	ArrayList<EntryRockwellXls> entryDint;
	ArrayList<EntryRockwellXls> entryReal;
	ArrayList<EntryRockwellXls> entryString;
	ArrayList<EntryRockwellXls> entryManCmd;
	
	
	
	public GroupEntry() {
		entryBit= new ArrayList<EntryRockwellXls>();
		entryInt= new ArrayList<EntryRockwellXls>();
		entryDint= new ArrayList<EntryRockwellXls>();
		entryReal= new ArrayList<EntryRockwellXls>();
		entryString= new ArrayList<EntryRockwellXls>();
		entryManCmd= new ArrayList<EntryRockwellXls>();
	}
}
