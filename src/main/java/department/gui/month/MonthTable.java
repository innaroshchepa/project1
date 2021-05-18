package department.gui.month;

import department.gui.Table;
import javafx.beans.property.SimpleStringProperty;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MonthTable extends Table<MonthTableItem> {
	
	public MonthTable() {
		columnMinWidth = 20;
		
		addColumns();
		
		GridPane.setHgrow(this, Priority.ALWAYS);
		GridPane.setVgrow(this, Priority.ALWAYS);
		
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		resizeColumns();
	}
	
	private void addColumns() {
		TableColumn<MonthTableItem, String> nameColumn = new TableColumn<>("П. І. Б");
		nameColumn.setCellValueFactory(prop -> new SimpleStringProperty(prop.getValue().getTeacher().getName()));
		
		getColumns().add(nameColumn);
		
		for (int i = 1; i <= 32; i++) {
			int idx = i - 1;
			
			TableColumn<MonthTableItem, String> column = new TableColumn<>();
			
			column.setText(i == 16 ? "X" : (i < 16 ? String.valueOf(i) : String.valueOf(idx)));	
			column.setCellValueFactory(prop -> new SimpleStringProperty(prop.getValue().getDays().get(idx)));
			
			getColumns().add(column);
		}
	}
}
