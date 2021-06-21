package department.gui.tables.impl;

import java.util.Comparator;

import department.Controller;
import department.database.tables.Post;

import department.gui.forms.DataEditorForm;
import department.gui.tables.DataBaseTable;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class PostsTable extends DataBaseTable<Post> {

	public PostsTable(Controller controller) {
		super(controller, Post.class, "Посади");
		
		sortPolicyProperty().set(new Callback<TableView<Post>, Boolean>() {
		    @Override
		    public Boolean call(TableView<Post> item) {
		        FXCollections.sort(getItems(), Comparator.comparing(Post::getName));
		        return true;
		    }
		});
	}

	@Override
	public void delete(Post item) {
		database.delete(item);
	}
	
	@Override
	public void showEditForm(boolean newItem) {
		new DataEditorForm<>(this, newItem ? new Post() : selectedItem());
	}
	
	@Override
	public void updateAllItems() {
		updateItems(database.collectPosts());
		sortPolicyProperty().get().call(this);
	}

	@Override
	public void addOrUpdate(Post item) {
		database.saveOrUpdate(item);
		addItem(item);
		sortPolicyProperty().get().call(this);
	}
	
	@Override
	public String searchPromptText() {
		return "Назва посади";
	}
}