package department.gui.tables;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javafx.scene.text.Text;

public class Table<T> extends TableView<T> {
	
	// минимальная ширина колонки
	protected double columnMinWidth = 100;
	
	public Table() {
		GridPane.setHgrow(this, Priority.ALWAYS);
		GridPane.setVgrow(this, Priority.ALWAYS);
		
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setPlaceholder(new Label("Таблиця не має введених даних"));
	}
	
	/**
	 * Автоматиский ресайз колонок таблицы под размер текста в нём
	 * 
	 */
	public void resizeColumns() {
		for (TableColumn<T, ?> column : getColumns()) {
			resizeColumn(column);
		}
	}
	
	private void resizeColumn(TableColumn<T, ?> column) {
		Text text = new Text(column.getText());
		double max = text.getLayoutBounds().getWidth() + 4;
		
		for (int i = 0; i < getItems().size(); i++) {
			if (column.getCellData(i) != null) {
				text = new Text(column.getCellData(i).toString());
				
				double calcWidth = text.getLayoutBounds().getWidth() + 4;
				if (calcWidth > max) {
					max = calcWidth;
				}
			}
		}
		
		double width = max + 15;
		double newWidth = width < columnMinWidth ? columnMinWidth : width;
		if (newWidth > column.getMinWidth())
			column.setPrefWidth(newWidth);
		
		for (TableColumn<T, ?> col : column.getColumns()) {
			resizeColumn(col);
		}
	}
}
