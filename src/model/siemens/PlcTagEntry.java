package model.siemens;

public class PlcTagEntry {

private String name, value;

public PlcTagEntry(String name, String value) {
	super();
	this.setName(name);
	this.setValue(value);
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}


}
