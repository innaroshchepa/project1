package department.database.tables;

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
