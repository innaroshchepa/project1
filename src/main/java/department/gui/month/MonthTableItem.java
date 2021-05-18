package department.gui.month;

import java.util.ArrayList;
import java.util.List;

import department.database.tables.Teacher;

public class MonthTableItem {
	
	private Teacher teacher;
	private List<String> days = new ArrayList<>();
	
	public Teacher getTeacher() {
		return teacher;
	}
	
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	
	public List<String> getDays() {
		return days;
	}
	
	public void setDays(List<String> days) {
		this.days = days;
	}
}
