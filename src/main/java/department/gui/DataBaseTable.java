package department.gui;

import java.lang.reflect.Field;
import java.util.List;

import department.Controller;
import department.database.DatabaseManager;
import department.database.DatabaseManagerImpl;
import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Teacher;
import department.database.tables.annotation.Named;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javafx.util.Callback;

public class DataBaseTable<T> extends Table<T> {
	
	private Controller controller;
	private Class<T> clazz;
	
	private static final DatabaseManager database = DatabaseManagerImpl.instance();
	
	public DataBaseTable(Controller controller, Class<T> clazz) {
		this.controller = controller;
		this.clazz = clazz;
		
		Named named = clazz.getAnnotation(Named.class);
		TableColumn<T, String> head = new TableColumn<>(named != null ? named.name() : clazz.getSimpleName());
		getColumns().add(head);
		
		for (Field field : clazz.getDeclaredFields()) {
			named = field.getAnnotation(Named.class);
			if (named != null) {
				TableColumn<T, String> column = new TableColumn<>(named.name());
				column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
				
				head.getColumns().add(column);
			}
		}
		
		GridPane.setHgrow(this, Priority.ALWAYS);
		GridPane.setVgrow(this, Priority.ALWAYS);
		
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		resizeColumns();
		
		if (controller != null) {
			setRowFactory(rowFactory());
		}
	}
	
	private Callback<TableView<T>, TableRow<T>> rowFactory() {
		return new Callback<TableView<T>, TableRow<T>>() {
			@Override
			public TableRow<T> call(TableView<T> table) {
				TableRow<T> row = new TableRow<T>() {
					@Override
                    protected void updateItem(T item, boolean empty) {
						super.updateItem(item, empty);
					}
				};
				
				ContextMenu menu = new ContextMenu();
				MenuItem edit = new MenuItem("Змінити дані");
				edit.setOnAction(e -> new DataEditorForm<>(controller, row.getItem()));
				
				MenuItem delete = new MenuItem("Видалити");
				delete.setOnAction(e -> onDelete(row.getItem()));
				
				menu.getItems().add(edit);
				row.setContextMenu(menu);
				
				return row;
			}
		};
	}
	
	private void onDelete(T entity) {
		if (clazz == Department.class) {
			database.delete((Department) entity);
		} else if (clazz == Teacher.class) {
			database.delete((Teacher) entity);
		} else if (clazz == Post.class) {
			database.delete((Post) entity);
		}
	}
	
	public void updateItems(List<T> items) {
		getItems().clear();
		getItems().addAll(items);
		resizeColumns();
	}
}
