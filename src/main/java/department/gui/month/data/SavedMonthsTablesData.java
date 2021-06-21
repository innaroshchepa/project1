package department.gui.month.data;

import java.io.Serializable;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import department.database.tables.Department;

import department.gui.month.Month;
import department.gui.month.table.DayType;
import department.gui.month.table.MonthTableItem;

public class SavedMonthsTablesData implements Serializable {
	
	private static final long serialVersionUID = -1851148016333795537L;
	
	private Month month;
	private List<DayType> monthDayTypes;
	private Map<Department, List<MonthTableItem>> tables = new TreeMap<>();
	
	public void addTable(Department department, List<MonthTableItem> items) {
		tables.put(department, items);
	}
	
	public Map<Department, List<MonthTableItem>> getTables() {
		return tables;
	}
	
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}
	
	public List<DayType> getMonthDayTypes() {
		return monthDayTypes;
	}

	public void setMonthDayTypes(List<DayType> monthDayTypes) {
		this.monthDayTypes = monthDayTypes;
	}
}