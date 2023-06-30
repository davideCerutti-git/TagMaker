package model.rockwell;

import java.util.ArrayList;
import settings.Settings;

public class Tag {
	public static final String ALARMSs = "ALARM_CHANNEL";
	public static final String READs = "READ_CHANNEL";
	public static final String WRITEs = "WRITE_CHANNEL";
	public static final String RAWs = "RAW_CHANNEL";
	private String fName;
	private String fTagType;
	private String DataType;
	private boolean fConstant;
	private String fExternalAccess;
	private String fDimensions = "";
	private ArrayList<Comment> fComments;
	private ArrayList<Data> fData;

	public String getfName() {
		return this.fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getfTagType() {
		return this.fTagType;
	}

	public void setfTagType(String fTagType) {
		this.fTagType = fTagType;
	}

	public String getDataType() {
		return this.DataType;
	}

	public void setDataType(String dataType) {
		this.DataType = dataType;
	}

	public boolean isfConstant() {
		return this.fConstant;
	}

	public void setfConstant(boolean fConstant) {
		this.fConstant = fConstant;
	}

	public String getfExternalAccess() {
		return this.fExternalAccess;
	}

	public void setfExternalAccess(String fExternalAccess) {
		this.fExternalAccess = fExternalAccess;
	}

	public String getfDimensions() {
		return this.fDimensions;
	}

	public void setfDimensions(String fDimensions) {
		this.fDimensions = fDimensions;
	}

	public ArrayList<Comment> getfComments() {
		return this.fComments;
	}

	public void setfComments(ArrayList<Comment> fComments) {
		this.fComments = fComments;
	}

	public String getChannel(Settings properties) {
		String[] stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_ALARMs").trim().split(";");
		} catch (Exception e) {
			ModelRockwell.logRock.error("Unable to get property: suffixList_ALARMs");
			return null;
		}
		for (String suffixALARM : stringsArray) {
			if (this.getfName().contains(suffixALARM.trim())) {
				return ALARMSs;
			}
		}
		stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_READs").trim().split(";");
		} catch (Exception e) {
			ModelRockwell.logRock.error("Unable to get property: suffixList_READs");
			return null;
		}
		for (String suffixREAD : stringsArray) {
			if (this.getfName().contains(suffixREAD.trim())) {
				return READs;
			}
		}
		stringsArray = null;
		try {
			stringsArray = properties.getProperty("suffixList_WRITEs").trim().split(";");
		} catch (Exception e) {
			ModelRockwell.logRock.error("Unable to get property: suffixList_WRITEs");
			return null;
		}
		for (String suffixWRITE : stringsArray) {
			if (this.getfName().contains(suffixWRITE.trim())) {
				return WRITEs;
			}
		}
		return RAWs;
	}

	public ArrayList<Data> getfData() {
		return this.fData;
	}

	public void setfData(ArrayList<Data> fData) {
		this.fData = fData;
	}

}
