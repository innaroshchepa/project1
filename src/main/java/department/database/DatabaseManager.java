package department.database;

import java.util.List;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.teacher.Teacher;

public interface DatabaseManager {
	
	void saveOrUpdate(Department department);
	void saveOrUpdate(Teacher teacher);
	void saveOrUpdate(Post post);
	
	void delete(Department department);
	void delete(Teacher teacher);
	void delete(Post post);
	
	Department getDepartmentById(long id);
	
	List<Department> collectDepartments();
	List<Teacher> collectTeachers();
	List<Post> collectPosts();
}