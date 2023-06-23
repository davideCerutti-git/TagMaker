package model.rockwell;

import java.util.ArrayList;

public class ArrayMember extends DataMember{

	private int dimensions;
	private ArrayList<Element_> elements;
	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public ArrayList<Element_> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Element_> elements) {
		this.elements = elements;
	}

}
