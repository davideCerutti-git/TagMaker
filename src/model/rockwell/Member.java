package model.rockwell;

public class Member {
	
	/**
	 * Fields
	 */
	private String fName;
	private String fDataType;
	private int fDimension;
	private String fRadix;
	private boolean fHidden;
	private String fExternalAccess;
	private String fTarget;
	private int fBitNumber;
	private String fDescription;
	
	
	/**
	 * Getter & Setter
	 */
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getfDataType() {
		return fDataType;
	}
	public void setfDataType(String fDataType) {
		this.fDataType = fDataType;
	}
	public int getfDimension() {
		return fDimension;
	}
	public void setfDimension(int fDimension) {
		this.fDimension = fDimension;
	}
	public String getfRadix() {
		return fRadix;
	}
	public void setfRadix(String fRadix) {
		this.fRadix = fRadix;
	}
	public boolean isfHidden() {
		return fHidden;
	}
	public void setfHidden(boolean fHidden) {
		this.fHidden = fHidden;
	}
	public String getfExternalAccess() {
		return fExternalAccess;
	}
	public void setfExternalAccess(String fExternalAccess) {
		this.fExternalAccess = fExternalAccess;
	}
	public String getfTarget() {
		return fTarget;
	}
	public void setfTarget(String fTarget) {
		this.fTarget = fTarget;
	}
	public int getfBitNumber() {
		return fBitNumber;
	}
	public void setfBitNumber(int fBitNumber) {
		this.fBitNumber = fBitNumber;
	}
	public String getfDescription() {
		return fDescription;
	}
	public void setfDescription(String fDescription) {
		this.fDescription = fDescription;
	}
	
	
}
