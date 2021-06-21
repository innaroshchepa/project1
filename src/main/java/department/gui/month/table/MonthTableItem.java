package department.gui.month.table;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import department.database.tables.teacher.Teacher;
import department.database.tables.teacher.TeacherDayType;

import department.gui.FXUtils;

public class MonthTableItem implements Serializable {
	
	private static final long serialVersionUID = -7909225881158488351L;
	
	private Teacher teacher;
	private List<DayOfMonth> days = new ArrayList<>();
	
	public static class DayOfMonth implements Serializable {
		
		private static final long serialVersionUID = 6319620729171722138L;
		
		private String value = "";
		
		private TeacherDayType teacherDayType;
		private DayType monthDayType;
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value.replace(".0", "");
		}
		
		public TeacherDayType getTeacherDayType() {
			return teacherDayType;
		}
		
		public void setTeacherDayType(TeacherDayType teacherDayType) {
			this.teacherDayType = teacherDayType;
		}
		
		public void setTeacherDayType(TeacherDayType teacherDayType, MonthTableItem item) {
			this.teacherDayType = teacherDayType;
			if (monthDayType == DayType.WEEKEND || monthDayType == DayType.HOLIDAY) {
				if (teacherDayType == TeacherDayType.WORKER) {
					return;
				}
			}
			
			if (teacherDayType == TeacherDayType.WORKER && item.getTeacher().getDepartment().getIsBetInTable()) {
				setValue(String.valueOf(item.getHours(this)));
			} else {
				setValue(teacherDayType.getTableCellSign());
			}
		}
		
		public DayType getMonthDayType() {
			return monthDayType;
		}
		
		public void setMonthDayType(DayType monthDayType) {
			this.monthDayType = monthDayType;
		}
	}
	
	public Teacher getTeacher() {
		return teacher;
	}
	
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	
	public List<DayOfMonth> getDays() {
		return days;
	}
	
	public void setDays(List<DayOfMonth> days) {
		this.days = days;
	}
	
	/**
	 * Возвращает количество отработанных дней за месяц
	 * 
	 * @return количество отработанных дней за месяц
	 */
	public int getAllDays() {
		int count = 0;
		for (DayOfMonth day : days) {
			if (isWorkedDay(day)) {
				count++;
			}
		}
		return count;
	}
	
	public float calcHours(DayType dayType) {
		float bet = teacher.getBet();
		// если ставка полная (1 и больше) и день предпраздничный - там другое время
		if (dayType == DayType.PRE_HOLIDAY) 
			return bet < 1 ? bet * DayType.WORKING.getHoursPerDay() : bet * DayType.PRE_HOLIDAY.getHoursPerDay();
		
		return bet * dayType.getHoursPerDay();
	}
	
	private float getHours(DayOfMonth day) {
		// если в ячейке записаны часы - возвращаем их
		return FXUtils.getFloat(day.getValue(), calcHours(day.getMonthDayType()));
	}
	
	/**
	 * Возвращает количество отработанных часов за месяц
	 * 
	 * @return количество отработанных часов за месяц
	 */
	public int getAllHours() {
		int hours = 0;
		for (DayOfMonth day : days) {
			if (isWorkedDay(day)) {
				hours += getHours(day);
			}
		}
		return hours;
	}
	
	/**
	 * Проверяет день на рабочий
	 * 
	 * @param day день месяца (ячейка таблицы)
	 * @return true – если день рабочий
	 */
	public boolean isWorkedDay(DayOfMonth day) {
		DayType md = day.getMonthDayType();
		// если ячейка таблицы пустая или тип дня не рабочий – день не рабочий
		if (md == null || day.getValue().isEmpty())
			return false;
		
		// день считается рабочим если тип дня в заголовке таблицы рабочий, и ячейка таблицы (день) рабочий
		TeacherDayType td = day.getTeacherDayType();
		return (td == TeacherDayType.WORKER || td == TeacherDayType.BUSINESS_TRIP)
				&& (md == DayType.WORKING || md == DayType.PRE_HOLIDAY);
	}
	
	/**
	 * Создаёт новый обьект с инициализацией первых 15-ти дней
	 * 
	 * @return this
	 */
	public static MonthTableItem createInitialItem(Teacher teacher) {
		MonthTableItem item = new MonthTableItem();
		item.setTeacher(teacher);
		
		List<DayOfMonth> days = new ArrayList<>();
		for (int i = 0; i <= MonthTable.MAX_DAYS_OF_MONTH; i++) {
			DayOfMonth day = new DayOfMonth();
			
			// первые 15 дней
			if (i < 15) {
				day.setTeacherDayType(TeacherDayType.WORKER);
				day.setMonthDayType(DayType.WORKING);
				// если в отделе отмечен флаг isBetInTable, дни учителей заполняются их ставками
				day.setValue(teacher.getDepartment().getIsBetInTable() ? 
					String.valueOf(teacher.getBet() * DayType.WORKING.getHoursPerDay()) : 
					day.getTeacherDayType().getTableCellSign());
			}
			
			days.add(day);
		}
		
		item.setDays(days);
		return item;
	}
}