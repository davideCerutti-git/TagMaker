package model.rockwell;

public class Member {
	private String fName;
	private String fDataType;
	private int fDimension;
	private String fRadix;
	private boolean fHidden;
	private String fExternalAccess;
	private String fTarget;
	private int fBitNumber;
	private String fDescription;

	public String getfName() {
		return this.fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getfDataType() {
		return this.fDataType;
	}

	public void setfDataType(String fDataType) {
		this.fDataType = fDataType;
	}

	public int getfDimension() {
		return this.fDimension;
	}

	public void setfDimension(int fDimension) {
		this.fDimension = fDimension;
	}

	public String getfRadix() {
		return this.fRadix;
	}

	public void setfRadix(String fRadix) {
		this.fRadix = fRadix;
	}

	public boolean isfHidden() {
		return this.fHidden;
	}

	public void setfHidden(boolean fHidden) {
		this.fHidden = fHidden;
	}

	public String getfExternalAccess() {
		return this.fExternalAccess;
	}

	public void setfExternalAccess(String fExternalAccess) {
		this.fExternalAccess = fExternalAccess;
	}

	public String getfTarget() {
		return this.fTarget;
	}

	public void setfTarget(String fTarget) {
		this.fTarget = fTarget;
	}

	public int getfBitNumber() {
		return this.fBitNumber;
	}

	public void setfBitNumber(int fBitNumber) {
		this.fBitNumber = fBitNumber;
	}

	public String getfDescription() {
		return this.fDescription;
	}

	public void setfDescription(String fDescription) {
		this.fDescription = fDescription;
	}

}
