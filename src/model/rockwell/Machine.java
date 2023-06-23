package model.rockwell;

public class Machine {
	
	private String fName;
	private GroupAlarms fgroupAlarms;
	private GroupReads fgroupReads;
	private GroupWrites fgroupWrites;
	private GroupEntry fgroupEntryes;
	
	
	
	
	
	
	
	public Machine(String nameMachineTmpString) {
		fName=nameMachineTmpString;
		fgroupAlarms=new GroupAlarms();
		fgroupReads=new GroupReads();
		fgroupWrites=new GroupWrites();
		fgroupEntryes=new GroupEntry();
	}
	
	
	
	/**
	 * Getter and Setter
	 */
	
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public GroupAlarms getFgroupAlarms() {
		return fgroupAlarms;
	}
	public void setFgroupAlarms(GroupAlarms fgroupAlarms) {
		this.fgroupAlarms = fgroupAlarms;
	}
	public GroupReads getFgroupReads() {
		return fgroupReads;
	}
	public void setFgroupReads(GroupReads fgroupReads) {
		this.fgroupReads = fgroupReads;
	}
	public GroupWrites getFgroupWrites() {
		return fgroupWrites;
	}
	public void setFgroupWrites(GroupWrites fgroupWrites) {
		this.fgroupWrites = fgroupWrites;
	}
	
	
	
	
	

}
