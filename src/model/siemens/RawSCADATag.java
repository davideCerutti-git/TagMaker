package model.siemens;

import model.siemens.ModelSiemens.Category;

public class RawSCADATag {

	private Category category=null;
	private int DB_number = 0;
	private String tag_name = "";
	private String tag_type = "";
	private String tag_address = "";
	private String tag_comment = "";
	
	
	public RawSCADATag(Category category, int dB_number, String tag_name, String tag_type, String tag_address,
			String tag_comment) {
		super();
		this.category = category;
		this.DB_number = dB_number;
		this.tag_name = tag_name;
		this.tag_type = tag_type;
		this.tag_address = tag_address;
		this.tag_comment = tag_comment;
	}
	
	@Override
	public String toString() {
		return this.category + " "+
				this.DB_number + " "+
				this.tag_name + " "+
				this.tag_type + " "+
				this.tag_address + " "+
				this.tag_comment + " ";
		
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}


	/**
	 * @return the dB_number
	 */
	public int getDB_number() {
		return DB_number;
	}


	/**
	 * @return the tag_name
	 */
	public String getTag_name() {
		return tag_name;
	}


	/**
	 * @return the tag_type
	 */
	public String getTag_type() {
		return tag_type;
	}


	/**
	 * @return the tag_address
	 */
	public String getTag_address() {
		return tag_address;
	}


	/**
	 * @return the tag_comment
	 */
	public String getTag_comment() {
		return tag_comment;
	}


	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}


	/**
	 * @param dB_number the dB_number to set
	 */
	public void setDB_number(int dB_number) {
		DB_number = dB_number;
	}


	/**
	 * @param tag_name the tag_name to set
	 */
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}


	/**
	 * @param tag_type the tag_type to set
	 */
	public void setTag_type(String tag_type) {
		this.tag_type = tag_type;
	}


	/**
	 * @param tag_address the tag_address to set
	 */
	public void setTag_address(String tag_address) {
		this.tag_address = tag_address;
	}


	/**
	 * @param tag_comment the tag_comment to set
	 */
	public void setTag_comment(String tag_comment) {
		this.tag_comment = tag_comment;
	}
	
	
	
	

}
