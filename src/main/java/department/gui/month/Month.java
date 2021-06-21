package department.gui.month;

public enum Month {
	
	JANUARY("Січень", 31),
	FEBRUARY("Лютий", 29),
	MARCH("Березень", 31),
	APRIL("Квітень", 30),
	MAY("Травень", 31),
	JUNE("Червень", 30),
	JULY("Липень", 31),
	AUGUST("Серпень", 31),
	SEPTEMBER("Вересень", 30),
	OCTOBER("Жовтень", 31),
	NOVEMBER("Листопад", 30),
	DECEMBER("Грудень", 31);
	
	private String nameUkr;
	private int daysOfMonth;
	
	private Month(String nameUkr, int daysOfMonth) {
		this.nameUkr = nameUkr;
		this.daysOfMonth = daysOfMonth;
	}
	
	public String getNameUkr() {
		return nameUkr;
	}
	
	public int getDaysOfMonth() {
		return daysOfMonth;
	}
	
	@Override
	public String toString() {
		return nameUkr;
	}
}
