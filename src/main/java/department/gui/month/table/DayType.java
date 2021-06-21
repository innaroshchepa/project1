package department.gui.month.table;

public enum DayType {
	
	WORKING(8) {
		@Override
		public String toString() {
			return "робочий";
		}
	},
	WEEKEND(0) {
		@Override
		public String toString() {
			return "вихідний";
		}
	},
	HOLIDAY(0) {
		@Override
		public String toString() {
			return "святковий";
		}
	},
	PRE_HOLIDAY(7) {
		@Override
		public String toString() {
			return "передсвятковий";
		}
	};
	
	private final int hoursPerDay;
	
	private DayType(int hoursPerDay) {
		this.hoursPerDay = hoursPerDay;
	}

	public int getHoursPerDay() {
		return hoursPerDay;
	}
}