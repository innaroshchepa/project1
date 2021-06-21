package department.gui.tables;

import department.database.tables.Containsable;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchContainer extends HBox {
	
	// поле поиска записей в БД
	private TextField searchInput = new TextField();
	private Button cleanButton = new Button("Очистити");
	
	private DataBaseTable<? extends Containsable> currentTable;
	
	public SearchContainer() {
		setSpacing(5);
		setPadding(new Insets(5));
		
		getChildren().addAll(searchInput, cleanButton);
		
		cleanButton.setCursor(Cursor.HAND);
		cleanButton.setOnAction(e -> {
			reset();
			
			currentTable.updateAllItems();
			currentTable.resizeColumns();
			currentTable.refresh();	
		});
		
		searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentTable != null) {
				currentTable.updateAllItems();
				currentTable.getItems().removeIf(search -> !search.containsIgnoreCase(newValue.toLowerCase()));
				
				currentTable.resizeColumns();
				currentTable.refresh();	
			}
		});
	}
	
	public void setTable(DataBaseTable<? extends Containsable> currentTable) {
		this.currentTable = currentTable;
		searchInput.setPromptText(currentTable.searchPromptText());
		reset();
	}
	
	public String getText() {
		return searchInput.getText().toLowerCase();
	}
	
	private void reset() {
		searchInput.setText("");
	}
}
