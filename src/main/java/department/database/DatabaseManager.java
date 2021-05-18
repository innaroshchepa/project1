package department.database;

import java.util.List;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Teacher;

public interface DatabaseManager {
	
	void saveOrUpdate(Department department);
	void saveOrUpdate(Teacher teacher);
	void saveOrUpdate(Post post);
	
	void delete(Department department);
	void delete(Teacher teacher);
	void delete(Post post);
	
	List<Department> collectDepartments();
	List<Teacher> collectTeachers();
	List<Post> collectPosts();
}