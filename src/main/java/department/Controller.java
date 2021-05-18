package department;

import java.net.URL;
import java.util.ResourceBundle;

import department.database.DatabaseManager;
import department.database.DatabaseManagerImpl;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Teacher;

import department.gui.DataEditorForm;
import department.gui.MonthSelectionForm;
import department.gui.DataBaseTable;
import department.gui.month.MonthTable;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class Controller implements Initializable {
	
	private static final DatabaseManager database = DatabaseManagerImpl.instance();
	
	private DataBaseTable<Department> departmentTable = new DataBaseTable<>(this, Department.class);
	private DataBaseTable<Teacher> teachersTable      = new DataBaseTable<>(this, Teacher.class);
	private DataBaseTable<Post> postTable             = new DataBaseTable<>(this, Post.class);
	
	private MonthTable monthTable;
	
	private MonthSelectionForm monthsForm;
	
	@FXML
	private GridPane scrollContentPane;
	
	@FXML
	private Button button;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//onNewMonth();
		
	}
	
	private void onCreateTablesClick() {
		if (monthTable == null) {
			scrollContentPane.getChildren().clear();
			monthTable = new MonthTable();
			scrollContentPane.getChildren().add(monthTable);
		}
	}
	
	@FXML
	private void onNewMonth() {
		scrollContentPane.getChildren().clear();
		
		if (monthsForm == null)
			monthsForm = new MonthSelectionForm();
		
		button.setText("Створити таблиці");
		button.setOnAction(e -> onCreateTablesClick());
		
		scrollContentPane.getChildren().add(monthsForm);
	}
	
	@FXML
	private void onClickExit() {
		Platform.exit();
	}
	
	@FXML
	public void onShowDepartments() {
		clearContentPane();
		
		scrollContentPane.getChildren().add(departmentTable);
		departmentTable.updateItems(database.collectDepartments());
	}
	
	@FXML
	public void onShowTeachers() {
		clearContentPane();
		
		scrollContentPane.getChildren().add(teachersTable);
		teachersTable.updateItems(database.collectTeachers());
	}
	
	@FXML
	public void onShowPosts() {
		clearContentPane();
		
		scrollContentPane.getChildren().add(postTable);
		postTable.updateItems(database.collectPosts());
	}
	
	@FXML
	public void onButtonAddClick() {
		Node node = scrollContentPane.getChildren().get(0);
		
		if (node == departmentTable) {
			new DataEditorForm<>(this, new Department());
		} else if (node == teachersTable) {
			new DataEditorForm<>(this, new Teacher());
		} else if (node == postTable) {
			new DataEditorForm<>(this, new Post());
		}
	}
	
	private void clearContentPane() {
		scrollContentPane.getChildren().clear();
		
		button.setText("Додати запис в Базу Даних");
		button.setOnAction(e -> onButtonAddClick());
	}
}
