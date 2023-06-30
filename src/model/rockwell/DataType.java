package model.rockwell;

import java.util.ArrayList;

public class DataType {

	private String fName;
	private String fFamily;
	private String fClass;
	private ArrayList<Member> fMembers;

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getfFamily() {
		return fFamily;
	}

	public void setfFamily(String fFamily) {
		this.fFamily = fFamily;
	}

	public String getfClass() {
		return fClass;
	}

	public void setfClass(String fClass) {
		this.fClass = fClass;
	}

	public ArrayList<Member> getfMembers() {
		return fMembers;
	}

	public void setfMembers(ArrayList<Member> fMembers) {
		this.fMembers = fMembers;
	}

}
