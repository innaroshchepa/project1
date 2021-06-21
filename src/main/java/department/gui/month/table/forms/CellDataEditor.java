package department.gui.month.table.forms;

import department.Main;
import department.database.tables.teacher.TeacherDayType;

import department.gui.DefaultGridPane;
import department.gui.FXUtils;

import department.gui.month.table.MonthTable;
import department.gui.month.table.MonthTableItem;
import department.gui.month.table.MonthTableItem.DayOfMonth;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Форма изменения данных дня таблицы
 */
public class CellDataEditor extends Stage {
	
	private ComboBox<TeacherDayType> combo = new ComboBox<>(FXCollections.observableArrayList(TeacherDayType.values()));
	
	private Button button = new Button("Зберегти зміни");
	private Button cancel = new Button("Відмінити");
	
	private TextField textField;
	
	private RadioButton enumCheck = new RadioButton();
	private RadioButton textCheck = new RadioButton();
	
	private MonthTable monthTable;
	private DayOfMonth day;
	private MonthTableItem item;
	
	public CellDataEditor(MonthTable monthTable, DayOfMonth day, MonthTableItem item) {
		this.monthTable = monthTable;
		this.day = day;
		this.item = item;
		
		GridPane grid = new DefaultGridPane();
		Scene scene = new Scene(grid);
		
		button.setCursor(Cursor.HAND);
		button.setOnAction(e -> onButtonClick());
		
		cancel.setCursor(Cursor.HAND);
		cancel.setOnAction(e -> close());
		
		configureRadioBoxes();
		
		grid.add(enumCheck, 0, 0);
		
		combo.setCursor(Cursor.HAND);
		combo.setMaxWidth(500);
			
		grid.add(combo, 1, 0);
		
		grid.add(textCheck, 0, 1);
		grid.add(textField, 1, 1);
		
		HBox box = new HBox(10);
		box.setAlignment(Pos.BOTTOM_RIGHT);
		box.getChildren().addAll(button, cancel);
		
		grid.add(box, 1, 2);
		
		getIcons().add(Main.primaryStage().getIcons().get(0));
		
		setScene(scene);
		setTitle("Зміна значення");
		setResizable(false);
		
		initOwner(Main.primaryStage());
		initModality(Modality.APPLICATION_MODAL);
		
		showAndWait();
	}
	
	private void onButtonClick() {
		String text = getText();
		
		day.setTeacherDayType(currentDayType());
		day.setValue(text);
		
		monthTable.refresh();
		monthTable.resizeColumns();
		close();
	}
	
	private void configureRadioBoxes() {
		ToggleGroup group = new ToggleGroup();
		
		enumCheck.setToggleGroup(group);
		textCheck.setToggleGroup(group);
		
		String value = day.getValue();
		if (FXUtils.isFloat(value)) {
			textField = new TextField(value);
			combo.getSelectionModel().select(0);
			
			textCheck.setSelected(true);
			combo.setDisable(true);
		} else {
			textField = new TextField();
			combo.getSelectionModel().select(day.getTeacherDayType());
			
			enumCheck.setSelected(true);
			textField.setDisable(true);
		}
		
		group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == enumCheck) {
				combo.setDisable(false);
				textField.setDisable(true);
			} else {
				combo.setDisable(true);
				textField.setDisable(false);
			}
		});
		
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			textField.setText(FXUtils.isFloat(newValue) || newValue.isEmpty() ? newValue : oldValue);
		});
	}
	
	private String getText() {
		if (combo.isDisable())
			return textField.getText();
		
		TeacherDayType current = currentDayType();
		if (current == TeacherDayType.WORKER && item.getTeacher().getDepartment().getIsBetInTable()) {
			return String.valueOf(item.calcHours(day.getMonthDayType()));
		}
		
		return current.getTableCellSign();
	}
	
	private TeacherDayType currentDayType() {
		return combo.isDisable() ? TeacherDayType.WORKER : combo.getSelectionModel().getSelectedItem();
	}
}