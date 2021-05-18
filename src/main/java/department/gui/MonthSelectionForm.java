package department.gui;

import department.gui.month.Month;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MonthSelectionForm extends GridPane {
	
	private Label label = new Label("Виберіть місяць:");
	private ComboBox<Month> months = new ComboBox<>();
	
	public MonthSelectionForm() {
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(10, 10, 10, 10));
		
		label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
		add(label, 0, 1);
		
		months.setMinWidth(150);
		months.setMaxWidth(500);
		months.setCursor(Cursor.HAND);
		
		months.getItems().addAll(Month.values());
		months.getSelectionModel().select(0);
		
		months.setOnAction(e -> System.out.println("SelectMonthForm::setOnAction"));
		add(months, 0, 2);
	}
}
