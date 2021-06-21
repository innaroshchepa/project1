package department.gui.month;

import department.gui.DefaultGridPane;

import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Панель выбора месяца для генерации новых таблиц
 * 
 */
public class MonthSelectionPane extends DefaultGridPane {
	
	private Label label = new Label("Виберіть місяць:");
	private ComboBox<Month> months = new ComboBox<>();
	
	public MonthSelectionPane() {
		label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
		add(label, 0, 1);
		
		months.setMinWidth(150);
		months.setMaxWidth(500);
		months.setCursor(Cursor.HAND);
		
		months.getItems().addAll(Month.values());
		months.getSelectionModel().select(0);
		
		add(months, 0, 2);
	}
	
	public Month getSelectedMonth() {
		return months.getSelectionModel().getSelectedItem();
	}
}
