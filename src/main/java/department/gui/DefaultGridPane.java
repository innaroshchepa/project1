package department.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class DefaultGridPane extends GridPane {
	
	public DefaultGridPane() {
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 35, 25, 35));
		setAlignment(Pos.CENTER);
	}
}
