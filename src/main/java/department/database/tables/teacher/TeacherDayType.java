package department.database.tables.teacher;

public enum TeacherDayType {
	
	WORKER("р") {
		@Override
		public String toString() {
			return "Робочий";
		}
	},
	VACATION("в") {
		@Override
		public String toString() {
			return "Відпустка";
		}
	},
	MEDICAL("Л") {
		@Override
		public String toString() {
			return "Лікарняний";
		}
	},
	BUSINESS_TRIP("В") {
		@Override
		public String toString() {
			return "Відрядження";
		}
	};
	
	private final String tableCellSign;
	
	private TeacherDayType(String tableCellSign) {
		this.tableCellSign = tableCellSign;
	}
	
	public String getTableCellSign() {
		return tableCellSign;
	}
}
