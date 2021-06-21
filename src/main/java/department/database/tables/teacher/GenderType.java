package department.database.tables.teacher;

public enum GenderType {
	
	MALE {
		@Override
		public String toString() {
			return "ч";
		}
	},
	FEMALE {
		@Override
		public String toString() {
			return "ж";
		}
	}
}