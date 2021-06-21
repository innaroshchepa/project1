package department.gui.tables.impl;

import java.util.Comparator;

import department.Controller;
import department.database.tables.teacher.Teacher;

import department.gui.forms.DataEditorForm;
import department.gui.tables.DataBaseTable;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class TeachersTable extends DataBaseTable<Teacher> {

	public TeachersTable(Controller controller) {
		super(controller, Teacher.class, "Співробітники");
		
		sortPolicyProperty().set(new Callback<TableView<Teacher>, Boolean>() {
		    @Override
		    public Boolean call(TableView<Teacher> item) {
		        FXCollections.sort(getItems(), Comparator.comparing(Teacher::getName));
		        return true;
		    }
		});
	}
	
	@Override
	public void delete(Teacher item) {
		database.delete(item);
	}
	
	@Override
	public void showEditForm(boolean newItem) {
		new DataEditorForm<>(this, newItem ? new Teacher() : selectedItem());
	}
	
	@Override
	public void updateAllItems() {
		updateItems(database.collectTeachers());
		sortPolicyProperty().get().call(this);
	}
	
	@Override
	public void addOrUpdate(Teacher item) {
		database.saveOrUpdate(item);
		addItem(item);
		if (controller.monthTablesController != null)
			controller.monthTablesController.updateTeacher(item);
		
		sortPolicyProperty().get().call(this);
	}
	
	@Override
	public String searchPromptText() {
		return "ПІБ Співробітника";
	}
}