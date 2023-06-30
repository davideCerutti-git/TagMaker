package model.siemens;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import model.siemens.items.ItemStruct;

public class DB{

	private String title;
	private String name;
	private String strName;
	protected int idNumber;
	protected float version;
	protected ItemStruct mainStruct;
		
	public DB(int numDataBlock, String titleDataBlock, String nameDataBlock, float versionDataBlock) {
		idNumber = numDataBlock;
		title = titleDataBlock;
		name = nameDataBlock;
		version = versionDataBlock;
		mainStruct = new ItemStruct(nameDataBlock,nameDataBlock,nameDataBlock, "", new Address(numDataBlock,0,0), mainStruct,0,0);
	}
	
	public ItemStruct getMainStruct() {
		return mainStruct;
	}

	public void allToString() {
		mainStruct.allToString();
	}
	
	public int getDbNumber() {
		return this.idNumber;
	}

	public String toString() {
		return "DB " + idNumber;// +" "+  title + " " + name;
	}
	
	public int generateXlsx(XSSFWorkbook wb, Sheet sheet, int ind, CellStyle styleRed) {
		return mainStruct.generateXlsx(wb,sheet, ind, this.strName, styleRed,false,false);
	}

	public void setStringName() {
		this.strName=mainStruct.getName();
	}

	public String toStringExtended() {
		return "DataBlock [idNumber=" + idNumber + ", title=" + title + ", name=" + name + ", version=" + version
				+ ", mainStruct=" + "]";
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the strName
	 */
	public String getStrName() {
		return strName;
	}

	/**
	 * @param strName the strName to set
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}
	
}
