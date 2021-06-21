package department.gui.tables;

import java.lang.reflect.Field;
import java.util.List;

import department.Controller;

import department.database.DatabaseManager;
import department.database.DatabaseManagerImpl;
import department.database.tables.annotation.Named;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public abstract class DataBaseTable<T> extends Table<T> {
	
	protected Controller controller;
	
	protected static final DatabaseManager database = DatabaseManagerImpl.instance();
	
	public DataBaseTable(Controller controller, Class<T> clazz, String header) {
		this.controller = controller;
		
		TableColumn<T, String> head = new TableColumn<>(header);
		getColumns().add(head);
		
		for (Field field : clazz.getDeclaredFields()) {
			Named named = field.getAnnotation(Named.class);
			if (named != null) {
				TableColumn<T, String> column = new TableColumn<>(named.name());
				column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
				column.setSortable(false);
				
				head.getColumns().add(column);
			}
		}
		
		resizeColumns();
		setRowFactory(rowFactory());
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
				edit.setStyle("-fx-pref-width: 100px;");
				edit.setOnAction(e -> {
					if (row.getItem() == null)
						return;
					
					showEditForm(false);
				});
				
				MenuItem delete = new MenuItem("Видалити");
				delete.setOnAction(e -> onDelete(row.getItem()));
				
				menu.getItems().addAll(edit, delete);
				row.setContextMenu(menu);
				
				return row;
			}
		};
	}
	
	private void onDelete(T entity) {
		if (entity == null)
			return;
		
		delete(entity);
		getItems().remove(entity);
		refresh();
	}
	
	public void updateItems(List<T> items) {
		if (items == null) {
			System.out.println("[DataBaseTable::updateItems]: Items is null!");
			return;
		}
		
		getItems().clear();
		getItems().addAll(items);
		
		getSelectionModel().select(0);
		resizeColumns();
	}
	
	protected T selectedItem() {
		return getSelectionModel().getSelectedItem();
	}
	
	protected void addItem(T item) {
		getItems().add(item);
		refresh();
		resizeColumns();
	}
	
	public abstract void delete(T item);
	public abstract void showEditForm(boolean newItem);
	public abstract void updateAllItems();
	public abstract void addOrUpdate(T item);
	
	public abstract String searchPromptText();
}
