package department.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class Table<T> extends TableView<T> {
	
	protected double columnMinWidth = 100;
	
	public Table() {
		GridPane.setHgrow(this, Priority.ALWAYS);
		GridPane.setVgrow(this, Priority.ALWAYS);
		
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		resizeColumns();
	}
	
	protected void resizeColumns() {
		for (TableColumn<T, ?> column : getColumns()) {
			Text text = new Text(column.getText());
			double max = text.getLayoutBounds().getWidth();
			
			for (int i = 0; i < getItems().size(); i++) {
				if (column.getCellData(i) != null) {
					text = new Text(column.getCellData(i).toString());
					
					double calcWidth = text.getLayoutBounds().getWidth();
					if (calcWidth > max) {
						max = calcWidth;
					}
				}
			}
			
			double width = max + 15;
			double minWidth = columnMinWidth;
	        column.setPrefWidth(width < minWidth ? minWidth : width);
		}
	}
}
