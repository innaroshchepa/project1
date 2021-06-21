package department.gui.month.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import department.database.tables.Department;
import department.database.tables.teacher.Teacher;
import department.database.tables.teacher.TeacherDayType;

import department.gui.month.Month;
import department.gui.month.MonthTablesController;

import department.gui.month.table.MonthTableItem.DayOfMonth;
import department.gui.month.table.forms.CellDataEditor;
import department.gui.month.table.forms.SelectingDayTypeRange;

import department.gui.tables.Table;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import javafx.util.Callback;

public class MonthTable extends Table<MonthTableItem> {
	
	public static final int MAX_DAYS_OF_MONTH = 31;
	
	private MonthTablesController controller;
	private Department department;
	
	private Month month;
	private List<DayType> monthDayTypes;
	
	// для удобства доступа к колонкам дней
	private List<TableColumn<MonthTableItem, String>> dayColumns = new ArrayList<>();
	
	/**
	 * Создаёт обьект с инициализацией элементов таблицы
	 * 
	 * @param department отдел сотрудников
	 * @param controller контроллер месячных таблиц отделов
	 * @param items элементы таблицы
	 * @return this
	 */
	/**
	 * @param department
	 * @param controller
	 * @param items
	 */
	public MonthTable(Department department, MonthTablesController controller, List<MonthTableItem> items) {
		this.controller = controller;
		this.department = department;
		
		month = controller.getMonth();
		monthDayTypes = controller.getMonthDayTypes();
		
		setEditable(true);
		columnMinWidth = 20;
		
		getItems().addAll(items);
		getSelectionModel().select(0);
		
		initColumns();
		resizeColumns();
		
		setRowFactory(data -> {
            TableRow<MonthTableItem> row = new TableRow<>();
            
            // контекстное меню удаления рядка
            ContextMenu menu = new ContextMenu();
            
            MenuItem remove = new MenuItem("Видалити рядок");
			remove.setOnAction(e -> {
				MonthTableItem selected = row.getItem();
				if (selected != null) {
					getItems().remove(selected);
					
					refresh();
					resizeColumns();
				}
            });
			menu.getItems().add(remove);
			
			MenuItem type = new MenuItem("Змінити тип днів в діапазоні");
			type.setOnAction(e -> {
				MonthTableItem selected = row.getItem();
				if (selected != null) {
					new SelectingDayTypeRange(selected, this);
				}
            });
			menu.getItems().add(type);
			
			row.setContextMenu(menu);
            return row;
        });
		
		sortPolicyProperty().set(new Callback<TableView<MonthTableItem>, Boolean>() {
		    @Override
		    public Boolean call(TableView<MonthTableItem> item) {
		        FXCollections.sort(getItems(), Comparator.comparing(e -> e.getTeacher().getName()));
		        return true;
		    }
		});
		sortPolicyProperty().get().call(this);
	}
	
	/**
	 * Инициализация всех колонок таблицы
	 * 
	 */
	private void initColumns() {
		ContextMenu headerContextMenu = new ContextMenu();
		MenuItem item = new MenuItem("Записати пусті ячейки днів даними");
		item.setOnAction(e -> {
			for (MonthTableItem tableItem : getItems()) {
				for (DayOfMonth day : tableItem.getDays()) {
					if (day.getValue().isEmpty()) {
						DayType dayType = day.getMonthDayType();
						if (dayType == DayType.HOLIDAY || dayType == DayType.WEEKEND) 
							continue;
						
						day.setTeacherDayType(TeacherDayType.WORKER);
						// если в отделе отмечен флаг isBetInTable, дни учителей заполняются их ставками
						day.setValue(department.getIsBetInTable() ? 
							String.valueOf(tableItem.getTeacher().getBet() * DayType.WORKING.getHoursPerDay()) : 
							day.getTeacherDayType().getTableCellSign());	
					}
				}
				tableItem.getDays().get(15).setValue("");
			}
			refresh();
			resizeColumns();
		});
		headerContextMenu.getItems().add(item);
		
		TableColumn<MonthTableItem, String> mainColumn = new TableColumn<>(department.getName() + " – " + month);
		mainColumn.setContextMenu(headerContextMenu);
		
		getColumns().add(mainColumn);
		
		TableColumn<MonthTableItem, String> genderColumn = new TableColumn<>("Стать");
		genderColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTeacher().getGender().toString()));
		
		mainColumn.getColumns().add(genderColumn);
		
		TableColumn<MonthTableItem, String> nameColumn = new TableColumn<>("П. І. Б");
		nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTeacher().getName()));
		
		mainColumn.getColumns().add(nameColumn);
		
		for (int i = 0; i <= MAX_DAYS_OF_MONTH; i++) {
			mainColumn.getColumns().add(createDayColumn(i));
		}
		
		TableColumn<MonthTableItem, String> workedOut = new TableColumn<>("Відпрацьовано");
		mainColumn.getColumns().add(workedOut);
		
		TableColumn<MonthTableItem, String> allDays = new TableColumn<>(" днів ");
		allDays.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAllDays())));
		workedOut.getColumns().add(allDays);
		
		TableColumn<MonthTableItem, String> allHours = new TableColumn<>(" годин ");
		allHours.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAllHours())));
		workedOut.getColumns().add(allHours);
	}
	
	/**
	 * Создаёт колонку дня с контекстным меню и фабрикой ячеек
	 * @param idx индекс дня
	 * @return колонка дня таблицы
	 */
	private TableColumn<MonthTableItem, String> createDayColumn(int idx) {
		TableColumn<MonthTableItem, String> column = new TableColumn<>();
		
		if (idx != 15) {
			column.setText(String.valueOf(idx < 15 ? idx + 1 : idx));
			column.setContextMenu(new DayContextMenu(idx));
		} else {
			// разделитель дней на первые 15 и последующие
			column.setText("X");
		}
		
		column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDays().get(idx).getValue()));
		column.setEditable(true);
		column.setSortable(false);
		
		column.setCellFactory(c -> {
			TableCell<MonthTableItem, String> cell = new TableCell<MonthTableItem, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					if (item == null || empty) {
						setText(null);
					} else {
						setText(item);
						
						// разный тип дня – разный цвет колонок
						DayContextMenu context = (DayContextMenu) column.getContextMenu();
						if (context == null) {
							getStyleClass().add("blackCellStyle"); // X
						} else {
							DayType dayType = context.getSelectedDayType();
							if (dayType == DayType.HOLIDAY || dayType == DayType.WEEKEND) {
								getStyleClass().add("grayCellStyle");
							} else {
								getStyleClass().remove("grayCellStyle");
							}
						}
					}
				}
			};
			
			cell.setContextMenu(new CellContextMenu(cell, idx));
			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
				if (e.getClickCount() > 1) {
					MonthTableItem selected = getSelectionModel().getSelectedItem();
					if (selected == null)
						return;
					
					new CellDataEditor(this, selected.getDays().get(idx), selected);
				}
			});
			
			return cell;
		});
			
		dayColumns.add(column);
		return column;
	}
	
	/**
	 * Контекстное меню ячейки дня
	 * 
	 */
	private class CellContextMenu extends ContextMenu {
		
		private MenuItem edit = new MenuItem("Редагувати ячейку");
		private MenuItem remove = new MenuItem("Видалити рядок");
		private MenuItem type = new MenuItem("Змінити тип днів в діапазоні");
		
		public CellContextMenu(TableCell<MonthTableItem, String> cell, int idx) {
			setOnShowing(e -> {
				DayContextMenu columnContextMenu = (DayContextMenu) cell.getTableColumn().getContextMenu();
				if (columnContextMenu == null) 
					return;
				
				DayType dayType = columnContextMenu.getSelectedDayType();	
				edit.setDisable(dayType == DayType.HOLIDAY || dayType == DayType.WEEKEND);
			});
			
			if (idx != 15) {
				edit.setOnAction(e -> {
					MonthTableItem selected = (MonthTableItem) cell.getTableRow().getItem();
					if (selected == null)
						return;
					
					new CellDataEditor(MonthTable.this, selected.getDays().get(idx), selected);
					
					refresh();
					resizeColumns();
				});
				
				getItems().add(edit);
			}
			
			remove.setOnAction(e -> {
				MonthTableItem selected = (MonthTableItem) cell.getTableRow().getItem();
				if (selected == null)
					return;
				
            	cell.getTableView().getItems().remove(selected);
            	sortPolicyProperty().get().call(MonthTable.this);
            	
            	refresh();
				resizeColumns();
            });
			
			type.setOnAction(e -> {
				MonthTableItem selected = (MonthTableItem) cell.getTableRow().getItem();
				if (selected != null) {
					new SelectingDayTypeRange(selected, MonthTable.this);
				}
            });
			
			getItems().addAll(type, remove);
		}
	}
	
	/**
	 * Контекстное меню хеадеров колонок дня
	 * 
	 */
	private class DayContextMenu extends ContextMenu {
		
		private int idx;
		
		private DayType selectedDay;
		private CheckMenuItem selectedItem;
		
		public DayContextMenu(int idx) {
			this.idx = idx;
			
			selectedDay = monthDayTypes.get(idx);
			for (MonthTableItem item : getTableItems()) {
				item.getDays().get(idx).setMonthDayType(selectedDay);
			}
			
			for (DayType type : DayType.values()) {
				CheckMenuItem item = new CheckMenuItem(type.toString());
				
				item.setUserData(type);
				if (type == selectedDay) {
					item.setSelected(true);
					selectedItem = item;
				}
				
				getItems().add(item);
				refresh();
			}
			
			setOnAction(e -> {
				// снимаем выделение у старого элемента списка
				selectedItem.setSelected(false);
				selectedItem = (CheckMenuItem) e.getTarget();
				
				selectedDay = (DayType) selectedItem.getUserData();
				monthDayTypes.set(idx, selectedDay);
				for (MonthTableItem item : getTableItems()) {
					DayOfMonth day = item.getDays().get(idx);
					day.setMonthDayType(selectedDay);
					
					if (day.getTeacherDayType() == TeacherDayType.WORKER) {		
						if (selectedDay == DayType.WEEKEND || selectedDay == DayType.HOLIDAY) {
							day.setValue("");
						} else {	
							day.setValue(department.getIsBetInTable() ? String.valueOf(item.calcHours(selectedDay)) : 
								TeacherDayType.WORKER.getTableCellSign());
						}
					}
				}
				
				controller.updateTablesDayTypes(MonthTable.this);
				refresh();
			});
		}
		
		public void select(DayType type) {
			for (int i = 0; i < getItems().size(); i++) {
				CheckMenuItem item = (CheckMenuItem) getItems().get(i);
				
				if ((DayType) item.getUserData() == type) {
					item.setSelected(true);
					
					selectedItem.setSelected(false);
					selectedItem = item;
					
					selectedDay = type;
					for (MonthTableItem tableItem : getTableItems()) {
						tableItem.getDays().get(idx).setMonthDayType(type);
					}
					break;
				}
			}
			
			refresh();
		}
		
		public DayType getSelectedDayType() {
			return selectedDay;
		}
	}
	
	public void updateDayTypes() {
		for (int i = 0; i < dayColumns.size(); i++) {
			DayContextMenu menu = (DayContextMenu) dayColumns.get(i).getContextMenu();
			if (menu != null) menu.select(monthDayTypes.get(i));
		}
	}
	
	public void addTeacher(Teacher teacher) {
		getItems().add(MonthTableItem.createInitialItem(teacher));
		sortPolicyProperty().get().call(this);
		resizeColumns();
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
		getColumns().get(0).setText(department.getName());
		refresh();
	}
	
	public List<MonthTableItem> getTableItems() {
		List<MonthTableItem> days = new ArrayList<>();
		getItems().forEach(e -> days.add(e));
		return days;
	}
	
	public boolean isTeacherExist(Teacher teacher) {
		for (MonthTableItem item : getItems()) {
			if (item.getTeacher().getId() == teacher.getId())
				return true;
		}
		
		return false;
	}
	
	public Month getMonth() {
		return month;
	}
}