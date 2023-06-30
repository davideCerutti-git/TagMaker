package model.rockwell;

public class Machine {

	private String fName;
	private GroupAlarms fgroupAlarms;
	private GroupReads fgroupReads;
	private GroupWrites fgroupWrites;
	private GroupEntry fgroupEntryes;

	public Machine(String nameMachineTmpString) {
		this.fName = nameMachineTmpString;
		this.fgroupAlarms = new GroupAlarms();
		this.fgroupReads = new GroupReads();
		this.fgroupWrites = new GroupWrites();
		this.fgroupEntryes = new GroupEntry();
	}

	public String getfName() {
		return this.fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public GroupAlarms getFgroupAlarms() {
		return this.fgroupAlarms;
	}

	public void setFgroupAlarms(GroupAlarms fgroupAlarms) {
		this.fgroupAlarms = fgroupAlarms;
	}

	public GroupReads getFgroupReads() {
		return this.fgroupReads;
	}

	public void setFgroupReads(GroupReads fgroupReads) {
		this.fgroupReads = fgroupReads;
	}

	public GroupWrites getFgroupWrites() {
		return this.fgroupWrites;
	}

	public void setFgroupWrites(GroupWrites fgroupWrites) {
		this.fgroupWrites = fgroupWrites;
	}

}
