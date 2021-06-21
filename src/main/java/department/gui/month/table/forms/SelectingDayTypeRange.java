package department.gui.month.table.forms;

import java.util.List;

import department.Main;
import department.database.tables.teacher.TeacherDayType;

import department.gui.DefaultGridPane;
import department.gui.month.table.MonthTable;
import department.gui.month.table.MonthTableItem;
import department.gui.month.table.MonthTableItem.DayOfMonth;

import javafx.collections.FXCollections;

import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectingDayTypeRange extends Stage {
	
	private Button button = new Button("Зберегти");
	private Button cancelButton = new Button("Відмінити");
	
	private ComboBox<TeacherDayType> combo = new ComboBox<>(FXCollections.observableArrayList(TeacherDayType.values()));
	
	private DayTextField a = new DayTextField();
	private DayTextField b = new DayTextField();
	
	private MonthTableItem row;
	private MonthTable table;
	
	public SelectingDayTypeRange(MonthTableItem row, MonthTable table) {
		this.row = row;
		this.table = table;
		
		GridPane grid = new DefaultGridPane();
		Scene scene = new Scene(grid);
		
		button.setCursor(Cursor.HAND);
		button.setOnAction(e -> onButtonClick());
		
		cancelButton.setCursor(Cursor.HAND);
		cancelButton.setOnAction(e -> close());
		
		combo.getSelectionModel().select(0);
		grid.add(combo, 0, 0);
		
		grid.add(a, 0, 1);
		grid.add(b, 1, 1);
		
		HBox box = new HBox(10);
		box.setAlignment(Pos.BOTTOM_RIGHT);
		box.getChildren().addAll(button, cancelButton);
		
		grid.add(box, 1, 2);
		
		getIcons().add(Main.primaryStage().getIcons().get(0));
		
		setScene(scene);
		setTitle("Зміна типу дня в діапазоні");
		setResizable(false);
		
		initOwner(Main.primaryStage());
		initModality(Modality.APPLICATION_MODAL);
		
		showAndWait();
	}

	private void onButtonClick() {
		List<DayOfMonth> days = row.getDays();
		
		int min = Math.min(a.getDay(), b.getDay());
		int max = Math.max(a.getDay(), b.getDay()) + 1;
		if (min > 15) {
			min++;
			max++;
		}
		for (int i = min; i < max; i++) {
			if (i == 16) { // X
				max++;
				continue;
			}
			
			days.get(i - 1)
				.setTeacherDayType(combo.getSelectionModel().getSelectedItem(), row);
		}
		
		table.refresh();
		table.resizeColumns();
		
		close();
	}
}

class DayTextField extends TextField {
	
	public DayTextField() {
		super("1");
		textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				int i = Integer.parseInt(newValue);
				setText(i > 0 && i <= 31 ? newValue : (i > 31 ? "31" : oldValue));
			} catch (NumberFormatException e) {
				setText(oldValue);
			}
		});
	}
	
	public int getDay() {
		return Integer.parseInt(getText());
	}
}