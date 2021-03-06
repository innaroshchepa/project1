package department.gui.forms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import department.Main;

import department.database.DatabaseManager;
import department.database.DatabaseManagerImpl;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.annotation.Named;
import department.database.tables.teacher.GenderType;

import department.gui.DefaultGridPane;
import department.gui.ErrorHandler;
import department.gui.tables.DataBaseTable;

import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Форма добавления/редактирования обьектов Базы Данных
 *
 * @param <T> обьект БД
 */
public class DataEditorForm<T> extends Stage {
	
	private T instance;
	
	// fieldName -> value 
	private Map<String, Object> values = new HashMap<>();
	
	private DataBaseTable<T> table;
	
	private static final DatabaseManager database = DatabaseManagerImpl.instance();
	
	public DataEditorForm(DataBaseTable<T> table, T instance) {
		this.table = table;
		this.instance = instance;
		
		try {
			// инициализация полей и их значений обьекта instance в карту 
			for (Field field : instance.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Named.class)) {
					values.put(field.getName(), field.get(instance));
				}
			}
			
			createForm();
			setResizable(false);
			
			initOwner(Main.primaryStage());
			initModality(Modality.APPLICATION_MODAL);
			
			showAndWait();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			ErrorHandler.show(e);
		}
	}
	
	private boolean isValueExists(String id) {
		return values.get(id) != null;
	}
	
	private void onClickSaveButton() {
		for (Field field : instance.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			
			String id = field.getName();
			if (isValueExists(id)) {
				setField(field, values.get(id));
			}
		}
		
		table.addOrUpdate(instance);
	}
	
	private void setField(Field field, Object value) {
		try {
			switch (field.getType().getName()) {
				case "float":
					field.set(instance, (float) value);
					break;
				case "long":
					field.set(instance, (long) value);
					break;
				default:
					field.set(instance, value);
					break;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			ErrorHandler.show(e);
		}
	}
	
	/**
	 * ComboBox для полей классов (перечисления, ManyToOne обьекты итп.)
	 * 
	 */
	private class DataComboBox<E> extends ComboBox<E> {
		
		private SingleSelectionModel<E> selectionModel = getSelectionModel();
		
		@SuppressWarnings("unchecked")
		public DataComboBox(List<E> items, String id) {
			setMaxWidth(500);
			setCursor(Cursor.HAND);
			
			if (items == null || items.isEmpty()) {
				setPromptText("Введіть дані в Базу Даних");
				setDisable(true);
				return;
			}
			
			getItems().addAll(items);
			setOnAction(e -> values.put(id, getValue()));
			
			if (isValueExists(id)) {
				selectionModel.select((E) values.get(id));
			} else {
				selectionModel.select(0);
				values.put(id, selectionModel.getSelectedItem());
			}
		}
	}
	
	/**
	 * Проверяет нужно ли блокировать кнопку (если не введены не nullable поля)
	 * 
	 * @return флаг блокировки кнопки
	 */
	private boolean disabledButton() {
		Class<?> clazz = instance.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Named.class)) continue;
			
			Column column = field.getAnnotation(Column.class);
			if (column != null && !column.nullable()) {
				Object value = values.get(field.getName());
				if (value == null) return true;
				if (value instanceof String && ((String) value).isEmpty()) return true; 	
			}
		}
		return false;
	}
	
	private void createForm() {
		getIcons().add(Main.primaryStage().getIcons().get(0));
		
		GridPane grid = new DefaultGridPane();	
		Scene scene = new Scene(grid);
		
		Named classNamed = instance.getClass().getAnnotation(Named.class);
		String titleText = classNamed == null ? instance.getClass().getSimpleName() : classNamed.name();
		
		Text title = new Text(titleText);
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		setTitle(titleText);
		
		grid.add(title, 0, 0, 2, 1);
		
		Button save = new Button("Зберегти");
		save.setCursor(Cursor.HAND);
		save.setPrefWidth(200);
		save.setOnAction(e -> {
			onClickSaveButton();
			close();
		});
		
		Button cancel = new Button("Скасувати");
		cancel.setCursor(Cursor.HAND);
		cancel.setOnAction(e -> close());
		
		int i = 1;
		for (Field field : instance.getClass().getDeclaredFields()) {
			String id = field.getName();
			Class<?> fieldType = field.getType();
			
			// AUTO_INCREMENT
			if (id.equals("id")) continue;
			
			Named annotation = field.getAnnotation(Named.class);
			if (annotation == null) continue;
			
			Label label = new Label(annotation.name() + ":");
			grid.add(label, 0, i);
			
			Node node = null;
			if (fieldType == Department.class) {
				node = new DataComboBox<>(database.collectDepartments(), id);
			} else if (fieldType == Post.class) {
				node = new DataComboBox<>(database.collectPosts(), id);
			} else if (fieldType == GenderType.class) {
				node = new DataComboBox<>(Arrays.asList(GenderType.values()), id);
			} else if (fieldType == Boolean.class) {
				node = new DataComboBox<>(Arrays.asList(false, true), id);
			} else {
				TextField textField = new TextField();
				if (isValueExists(id))
					textField.setText(String.valueOf(values.get(id)));
				
				textField.textProperty().addListener((observable, oldValue, newValue) -> {
					// из чисел в текстовых полях есть только float
					if (fieldType.getName().equals("float")) {
						try {
							float value = Float.parseFloat(newValue);
							values.put(id, value);
						} catch (NumberFormatException e) {
							textField.setText(oldValue);
						}
					} else {
						values.put(id, newValue);
					}
					
					save.setDisable(disabledButton());
				});
				
				node = textField;
			}
			
			grid.add(node, 1, i++);
		}
		
		HBox box = new HBox(10);
		box.setAlignment(Pos.BOTTOM_RIGHT);
		box.getChildren().addAll(save, cancel);
		
		save.setDisable(disabledButton());
		
		grid.add(box, 1, i);
		setScene(scene);
	}
}
