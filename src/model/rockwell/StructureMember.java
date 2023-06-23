package model.rockwell;

import java.util.ArrayList;

public class StructureMember extends DataMember {
	private ArrayList<DataMember> listMembers;

	public ArrayList<DataMember> getListMembers() {
		return listMembers;
	}

	public void setListMembers(ArrayList<DataMember> listMembers) {
		this.listMembers = listMembers;
	}

}
