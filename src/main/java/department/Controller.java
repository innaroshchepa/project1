package department;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import java.util.Optional;
import java.util.ResourceBundle;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Containsable;
import department.database.tables.teacher.Teacher;

import department.gui.ErrorHandler;

import department.gui.month.MonthSelectionPane;
import department.gui.month.MonthTablesController;
import department.gui.month.data.FileManager;
import department.gui.month.data.SavedMonthsTablesData;
import department.gui.month.data.export.ExcelWorkbook;
import department.gui.month.table.MonthTable;

import department.gui.tables.DataBaseTable;
import department.gui.tables.SearchContainer;
import department.gui.tables.impl.DepartmentsTable;
import department.gui.tables.impl.PostsTable;
import department.gui.tables.impl.TeachersTable;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	private static final ExtensionFilter EXCEL_FILE_FILTER = new ExtensionFilter("Excel файл (*.xlsx)", "*.xlsx");
	private static final URI README_FILE_URI = new File("README.html").toURI();
	
	@FXML
	private BorderPane contentPane;
	
	@FXML
	private GridPane scrollContentPane;
	
	@FXML
	private Button button;
	
	@FXML
	private MenuItem monthTablesMenuItem;
	
	@FXML
	private MenuItem saveExcelMenuItem;
	
	public SearchContainer searchContainer = new SearchContainer();
	
	private DataBaseTable<Department> departmentTable;
	private DataBaseTable<Teacher> teachersTable;
	private DataBaseTable<Post> postTable;
	
	private DataBaseTable<? extends Containsable> currentTable;
	
	public MonthTablesController monthTablesController;
	
	private MonthSelectionPane monthsForm;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		departmentTable = new DepartmentsTable(this);
		teachersTable   = new TeachersTable(this);
		postTable       = new PostsTable(this);
		
		// сохранение данных таблиц отделов при выходе из программы
		Main.primaryStage().setOnCloseRequest(e -> {
			if (monthTablesController != null)
				monthTablesController.saveDataToFile();
				
			onClickExit();
		});
		
		// инициализация данных таблиц отделов при старте программы
		SavedMonthsTablesData savedData = FileManager.readCacheFile();
		if (savedData != null) {
			monthTablesController = new MonthTablesController(this);
			
			if (monthTablesController.loadSavedTables(savedData)) {
				monthTablesMenuItem.setDisable(false);
				saveExcelMenuItem.setDisable(false);
				
				button.setText("Додати співробітника");
				button.setOnAction(e -> onAddMonthTeacher());
				return;
			}
		}
		
		onNewMonth();
	}
	
	private void onCreateTablesClick() {
		monthTablesController = new MonthTablesController(this);	
		monthTablesController.createInitialTables(monthsForm.getSelectedMonth());	
		
		monthTablesMenuItem.setDisable(false);
		saveExcelMenuItem.setDisable(false);
		
		onShowAllTables();
	}
	
	@FXML
	private void onNewMonth() {
		if (monthTablesController != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(Main.primaryStage().getIcons().get(0));
			
			alert.setTitle("Створення нового місяця");
			alert.setHeaderText("Створення нового місяця");
			alert.setContentText("При створенні нового місяця, таблиці відділів за попередній місяць будуть видалені.");
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL)
				return;
			
			FileManager.moveCurretFileToHistory();
			
			monthTablesController.destroy(false);
			monthTablesController = null;
		}
		
		scrollContentPane.getChildren().clear();
		
		if (monthsForm == null)
			monthsForm = new MonthSelectionPane();
		
		button.setText("Створити таблиці");
		button.setOnAction(e -> onCreateTablesClick());
		
		scrollContentPane.getChildren().add(monthsForm);
		
		monthTablesMenuItem.setDisable(true);
		saveExcelMenuItem.setDisable(true);
	}
	
	@FXML
	private void onClickExit() {
		Platform.exit();
		System.exit(0);
	}
	
	private void onShowDataBaseTable(DataBaseTable<? extends Containsable> table) {
		if (table == currentTable) return;
		currentTable = table;
		
		clearContentPane();
		scrollContentPane.getChildren().add(currentTable);
		currentTable.updateAllItems();
		
		searchContainer.setTable(currentTable);
		contentPane.setTop(searchContainer);
	}
	
	@FXML
	private void onShowDepartments() {
		onShowDataBaseTable(departmentTable);
	}
	
	@FXML
	private void onShowTeachers() {
		onShowDataBaseTable(teachersTable);
	}
	
	@FXML
	private void onShowPosts() {
		onShowDataBaseTable(postTable);
	}
	
	@FXML
	private void onShowAllTables() {
		monthTablesController.addedToScene();
		
		button.setText("Додати співробітника");
		button.setOnAction(e -> onAddMonthTeacher());
	}
	
	private void onAddMonthTeacher() {
		monthTablesController.addTeacherToCurrentTable();
	}
	
	public void addTableToScene(MonthTable table) {
		scrollContentPane.getChildren().clear();
		scrollContentPane.getChildren().add(table);
		table.resizeColumns();
	}
	
	public void setTop(Node node) {
		contentPane.setTop(node);
	}
	
	@FXML
	public void onButtonAddClick() {
		if (currentTable != null) {
			currentTable.showEditForm(true);
		}
	}
	
	@FXML
	public void onSaveTables() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Зберегти таблиці місяців");
		fileChooser.getExtensionFilters().add(EXCEL_FILE_FILTER);
		fileChooser.setInitialFileName(monthTablesController.getMonth().toString());

		File file = fileChooser.showSaveDialog(Main.primaryStage());
		if (file != null) {
			ExcelWorkbook excel = new ExcelWorkbook(monthTablesController.getTables());
			excel.save(file);
		}
	}
	
	@FXML
	public void openReadme() {
		try {
			Desktop.getDesktop().browse(README_FILE_URI);
		} catch (IOException e) {
			ErrorHandler.show(e);
		}
	}
	
	private void clearContentPane() {
		if (monthTablesController != null)
			monthTablesController.destroy(true);
		
		scrollContentPane.getChildren().clear();
		
		button.setText("Додати запис в Базу Даних");
		button.setOnAction(e -> onButtonAddClick());
	}
}
