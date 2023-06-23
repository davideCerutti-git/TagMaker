package model.rockwell;

import java.util.ArrayList;

public class Structure {
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public ArrayList<DataMember> getDataMembers() {
		return dataMembers;
	}
	public void setDataMembers(ArrayList<DataMember> dataMembers) {
		this.dataMembers = dataMembers;
	}
	private String dataType;
	private ArrayList <DataMember> dataMembers;


}
