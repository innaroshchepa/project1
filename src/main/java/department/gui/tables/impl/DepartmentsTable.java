package department.gui.tables.impl;

import java.util.Comparator;

import department.Controller;
import department.database.tables.Department;

import department.gui.forms.DataEditorForm;
import department.gui.tables.DataBaseTable;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class DepartmentsTable extends DataBaseTable<Department> {

	public DepartmentsTable(Controller controller) {
		super(controller, Department.class, "Відділи");
		
		sortPolicyProperty().set(new Callback<TableView<Department>, Boolean>() {
		    @Override
		    public Boolean call(TableView<Department> item) {
		        FXCollections.sort(getItems(), Comparator.comparing(Department::getName));
		        return true;
		    }
		});
	}

	@Override
	public void delete(Department item) {
		database.delete(item);
	}
	
	@Override
	public void showEditForm(boolean newItem) {
		new DataEditorForm<>(this, newItem ? new Department() : selectedItem());
	}

	@Override
	public void updateAllItems() {
		updateItems(database.collectDepartments());
		sortPolicyProperty().get().call(this);
	}
	
	@Override
	public void addOrUpdate(Department item) {
		database.saveOrUpdate(item);
		addItem(item);
		if (controller.monthTablesController != null)
			controller.monthTablesController.updateDepartment(item);
		
		sortPolicyProperty().get().call(this);
	}

	@Override
	public String searchPromptText() {
		return "Назва відділу";
	}
}