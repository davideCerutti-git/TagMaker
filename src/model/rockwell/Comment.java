package model.rockwell;

public class Comment {

	private String fOperand;
	private String fDescription;
	private String fUsedInProgram;
	
	/**
	 * Getter & Setter
	 */
	public String getfOperand() {
		return fOperand;
	}
	
	public void setfOperand(String fOperand) {
		this.fOperand = fOperand;
	}
	
	public String getfDescription() {
		return fDescription;
	}
	
	public void setfDescription(String fDescription) {
		this.fDescription = fDescription;
	}
	
	public String getfUsedInProgram() {
		return fUsedInProgram;
	}
	
	public void setfUsedInProgram(String fUsedInProgram) {
		this.fUsedInProgram = fUsedInProgram;
	}
	
}
