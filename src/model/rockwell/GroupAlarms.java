package model.rockwell;

import java.util.ArrayList;

public class GroupAlarms {
	private ArrayList<EntryRockwellXls> alarmsBit;
	private ArrayList<EntryRockwellXls> alarmsInt;
	private ArrayList<EntryRockwellXls> alarmsDint;
	private ArrayList<EntryRockwellXls> alarmsReal;
	private ArrayList<EntryRockwellXls> alarmsString;
	private ArrayList<EntryRockwellXls> alarmsManCmd;

	public GroupAlarms() {
		this.alarmsBit = new ArrayList<EntryRockwellXls>();
		this.alarmsInt = new ArrayList<EntryRockwellXls>();
		this.alarmsDint = new ArrayList<EntryRockwellXls>();
		this.alarmsReal = new ArrayList<EntryRockwellXls>();
		this.alarmsString = new ArrayList<EntryRockwellXls>();
		this.alarmsManCmd = new ArrayList<EntryRockwellXls>();
	}

	public ArrayList<EntryRockwellXls> getAlarmsBit() {
		return alarmsBit;
	}

	public void setAlarmsBit(ArrayList<EntryRockwellXls> alarmsBit) {
		this.alarmsBit = alarmsBit;
	}

	public ArrayList<EntryRockwellXls> getAlarmsInt() {
		return alarmsInt;
	}

	public void setAlarmsInt(ArrayList<EntryRockwellXls> alarmsInt) {
		this.alarmsInt = alarmsInt;
	}

	public ArrayList<EntryRockwellXls> getAlarmsDint() {
		return alarmsDint;
	}

	public void setAlarmsDint(ArrayList<EntryRockwellXls> alarmsDint) {
		this.alarmsDint = alarmsDint;
	}

	public ArrayList<EntryRockwellXls> getAlarmsReal() {
		return alarmsReal;
	}

	public void setAlarmsReal(ArrayList<EntryRockwellXls> alarmsReal) {
		this.alarmsReal = alarmsReal;
	}

	public ArrayList<EntryRockwellXls> getAlarmsString() {
		return alarmsString;
	}

	public void setAlarmsString(ArrayList<EntryRockwellXls> alarmsString) {
		this.alarmsString = alarmsString;
	}

	public ArrayList<EntryRockwellXls> getAlarmsManCmd() {
		return alarmsManCmd;
	}

	public void setAlarmsManCmd(ArrayList<EntryRockwellXls> alarmsManCmd) {
		this.alarmsManCmd = alarmsManCmd;
	}

}
