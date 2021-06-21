package department.gui.forms;

import java.util.List;

import department.Main;

import department.database.tables.teacher.Teacher;
import department.gui.DefaultGridPane;
import department.gui.month.table.MonthTable;

import javafx.scene.Cursor;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import javafx.scene.layout.GridPane;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Форма добавления учителя в таблицу месяца
 * @see MonthTable
 */
public class AddTeacherForm extends Stage {
	
	private GridPane pane = new DefaultGridPane();
	
	private ComboBox<Teacher> comboBox;
	private Button button;
	
	private MonthTable table;
	private List<Teacher> teachers;
	
	public AddTeacherForm(MonthTable table, List<Teacher> teachers) {
		this.table = table;
		this.teachers = teachers;
		
		Scene scene = new Scene(pane);
		
		initComboBox();
		initButton();
		
		getIcons().add(Main.primaryStage().getIcons().get(0));
		
		setScene(scene);
		setTitle("Додавання нового співробітника");
		setResizable(false);
		
		initOwner(Main.primaryStage());
		initModality(Modality.APPLICATION_MODAL);
		
		showAndWait();
	}
	
	private void initComboBox() {
		comboBox = new ComboBox<>();
		
		comboBox.setMaxWidth(500);
		comboBox.setCursor(Cursor.HAND);
		comboBox.getItems().addAll(teachers);
		comboBox.getSelectionModel().select(0);
		
		pane.add(comboBox, 0, 0);
	}
	
	private void initButton() {
		button = new Button("Додати співробітника");
		
		button.setCursor(Cursor.HAND);
		button.setOnAction(e -> {
			table.addTeacher(comboBox.getSelectionModel().getSelectedItem());
			table.refresh();
			close();
		});
		
		pane.add(button, 0, 1);
	}
}
