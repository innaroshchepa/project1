package department.gui.month;

import java.util.ArrayList;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import department.Controller;
import department.Main;

import department.database.DatabaseManager;
import department.database.DatabaseManagerImpl;

import department.database.tables.Department;
import department.database.tables.teacher.Teacher;

import department.gui.forms.AddTeacherForm;

import department.gui.month.data.FileManager;
import department.gui.month.data.SavedMonthsTablesData;
import department.gui.month.table.DayType;
import department.gui.month.table.MonthTable;
import department.gui.month.table.MonthTableItem;

import static department.gui.month.table.MonthTable.MAX_DAYS_OF_MONTH;

import javafx.collections.FXCollections;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javafx.stage.Stage;

public class MonthTablesController {
	
	private TabPane tabs = new TabPane();
	private List<MonthTable> allTables = new ArrayList<>();
	
	private Timer timer;
	
	private Month month;
	private List<DayType> dayTypes;
	
	private MonthTable curretTable;
	
	private Controller mainController;
	
	private static final DatabaseManager database = DatabaseManagerImpl.instance();
	
	public MonthTablesController(Controller mainController) {
		this.mainController = mainController;
	}
	
	public void addedToScene() {
		mainController.setTop(tabs);
		
		if (allTables.size() > 0)
			addTableToScene(allTables.get(tabs.getSelectionModel().getSelectedIndex()));
		
		// сохранение таблицы месяца в файл
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				saveDataToFile();
			}
		}, TimeUnit.MINUTES.toMillis(3), TimeUnit.MINUTES.toMillis(3)); // каждые 3 минуты
	}
	
	/**
	 * Загружает таблицы из сериализованного файла
	 * 
	 * @param savedData сохранённые данные для загрузки таблиц
	 * @return true – если таблицы загружены
	 */
	public boolean loadSavedTables(SavedMonthsTablesData savedData) {
		if (savedData == null || savedData.getTables().isEmpty())
			return false;
		
		month = savedData.getMonth();
		dayTypes = savedData.getMonthDayTypes();
		
		savedData.getTables().forEach((department, items) -> initTable(department, items));
		
		afterLoadForm();
		return true;
	}
	
	/**
	 * Создаёт новые таблицы с отделами, при этом таблицы инициализируются данными из БД
	 * 
	 * @param savedData месяц
	 */
	public void createInitialTables(Month month) {
		this.month = month;
		
		dayTypes = new ArrayList<>(MAX_DAYS_OF_MONTH);
		for (int i = 0; i <= MAX_DAYS_OF_MONTH; i++) {
			// если в месяце дней меньше чем 31, то оставшиися дни считаются выходными
			dayTypes.add(i > month.getDaysOfMonth() ? DayType.WEEKEND : DayType.WORKING);
		}
		
		database.collectDepartments().forEach(department -> initTable(department, null));
		afterLoadForm();
	}
	
	private void initTable(Department department, List<MonthTableItem> items) {
		if (items == null) {
			items = new ArrayList<>();
			for (Teacher teacher : department.getTeachers()) {
				items.add(MonthTableItem.createInitialItem(teacher));
			}
		}
		
		MonthTable table = new MonthTable(department, this, items);
		
		Tab tab = new Tab(department.getName());
		
		tab.setClosable(false);
		tab.setUserData(table);
		
		allTables.add(table);
		tabs.getTabs().add(tab);
	}
	
	private void afterLoadForm() {
		addedToScene();
		tabs.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			if (newTab != oldTab) {
				addTableToScene((MonthTable) newTab.getUserData());
			}
	    });
	}
	
	private void addTableToScene(MonthTable table) {
		mainController.addTableToScene(table);
		curretTable = table;
	}
	
	public void destroy(boolean saveData) {
		curretTable = null;
		mainController.setTop(null);	
		if (timer != null) {
			timer.cancel();
			timer = null;
			
			if (saveData) new Thread(() -> saveDataToFile()).start();
		}
	}
	
	/**
	 * Обновляет всем таблицам тип дня
	 * 
	 * @param skipable таблица, которую не нужно трогать, там уже был уже изменён тип дня
	 */
	public void updateTablesDayTypes(MonthTable skipable) {
		for (MonthTable table : allTables) {
			if (table != skipable) {		
				table.updateDayTypes();
			}
		}
	}
	
	/**
	 * Обновляет обьект учителя в таблице по id учителя.
	 * Вызывается при редактировании таблицы учителей БД.
	 * 
	 * @param currTeacher новый обьект
	 */
	public void updateTeacher(Teacher currTeacher) {
		for (MonthTable table : allTables) {
			for (MonthTableItem item : table.getItems()) {
				if (item.getTeacher().getId() == currTeacher.getId()) {
					item.setTeacher(currTeacher);
				}
			}
			table.refresh();
		}
	}
	
	/**
	 * Обновляет обьект отдела в таблице.
	 * Вызывается при редактировании таблицы отделов БД.
	 * 
	 * @param currDepartment новый обьект
	 */
	public void updateDepartment(Department currDepartment) {
		for (Tab tab : tabs.getTabs()) {
			MonthTable table = (MonthTable) tab.getUserData();
			
			Department tempDepartment = table.getDepartment();
			if (tempDepartment.getId() == currDepartment.getId()) {
				table.setDepartment(currDepartment);
				tab.setText(currDepartment.getName());
				return;
			}
		}
		
		// add department
		initTable(currDepartment, null);
	}
	
	public void addTeacherToCurrentTable() {
		List<Teacher> list = FXCollections.observableArrayList();
		
		Department department = database.getDepartmentById(curretTable.getDepartment().getId());
		for (Teacher teacher : department.getTeachers()) {			
			if (!curretTable.isTeacherExist(teacher)) {
				list.add(teacher);
			}
		}
		
		if (list.isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			
			alert.setTitle("Повідомлення");
			alert.setHeaderText("Всі співробітники вже в існують в таблиці");
			alert.setContentText("Для того, щоб додати нового користувача в поточну таблицю, "
					+ "потрібно спочатку його додати в таблицю «Співробітники».");
			
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(Main.primaryStage().getIcons().get(0));
			
			alert.showAndWait();
		} else {
			new AddTeacherForm(curretTable, list);
		}
	}
	
	/**
	 * Cериализация данных таблиц месяцев в файл
	 * 
	 */
	public void saveDataToFile() {
		SavedMonthsTablesData saved = new SavedMonthsTablesData();
		
		for (MonthTable table : allTables) {
			saved.addTable(table.getDepartment(), table.getTableItems());
		}
		
		saved.setMonth(month);
		saved.setMonthDayTypes(dayTypes);
		
		FileManager.saveCacheFile(saved);
	}
	
	public Month getMonth() {
		return month;
	}
	
	public List<DayType> getMonthDayTypes() {
		return dayTypes;
	}
	
	public List<MonthTable> getTables() {
		return allTables;
	}
}