package department.database;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.teacher.Teacher;
import department.gui.ErrorHandler;

import static department.database.hibernate.HibernateService.*;


public class DatabaseManagerImpl implements DatabaseManager {
	
	private static final DatabaseManagerImpl INSTANCE = new DatabaseManagerImpl();
	
	private List<Department> cacheDepartments;
	private List<Teacher> cacheTeachers;
	private List<Post> cachePosts;
	
	// singleton
	private DatabaseManagerImpl() {}
	
	public static DatabaseManagerImpl instance() {
		return INSTANCE;
	}
	
	@Override
	public void saveOrUpdate(Department department) {
		saveOrUpdateRow(department);
		if (cacheDepartments != null && !cacheDepartments.contains(department)) {
			cacheDepartments.add(department);
		}
	}
	
	@Override
	public void saveOrUpdate(Teacher teacher) {
		saveOrUpdateRow(teacher);
		if (cacheTeachers != null && !cacheTeachers.contains(teacher)) {
			cacheTeachers.add(teacher);
		}
	}
	
	@Override
	public void saveOrUpdate(Post post) {
		saveOrUpdateRow(post);
		if (cachePosts != null && !cachePosts.contains(post)) {
			cachePosts.add(post);
		}
	}
	
	@Override
	public void delete(Department department) {
		deleteRow(department);
		if (cacheDepartments != null)
			cacheDepartments.remove(department);
		
		for (Teacher teacher : collectTeachers()) {
			if (teacher.getDepartment() == null) continue;
			if (teacher.getDepartment().getId() == department.getId()) {
				teacher.setDepartment(null);
				saveOrUpdate(teacher);
			}
		}
	}
	
	@Override
	public void delete(Teacher teacher) {
		deleteRow(teacher);
		if (cacheTeachers != null) {
			cacheTeachers.remove(teacher);
		}
	}
	
	@Override
	public void delete(Post post) {
		for (Teacher teacher : collectTeachers()) {
			if (teacher.getPost() == null) continue;
			if (teacher.getPost().getId() == post.getId()) {
				teacher.setPost(null);
				saveOrUpdate(teacher);
			}
		}
		
		deleteRow(post);
		if (cachePosts != null)
			cachePosts.remove(post);
	}
	
	@Override
	public Department getDepartmentById(long id) {
		Department department = null;
        Session session = null;
        Transaction tx = null;
        
        try {
        	session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
            
			department = (Department) session.get(Department.class, id);
            tx.commit();
        } catch (HibernateException e) {
        	if (tx != null && tx.wasRolledBack()) 
				tx.rollback();
			
			ErrorHandler.show(e);
        }
        
        return department;
	}
	
	@Override
	public List<Department> collectDepartments() {
		if (cacheDepartments == null)
			cacheDepartments = collect(Department.class);
		
		Collections.sort(cacheDepartments, Comparator.comparing(Department::getName));
		return cacheDepartments;
	}
	
	@Override
	public List<Teacher> collectTeachers() {
		if (cacheTeachers == null)
			cacheTeachers = collect(Teacher.class);
		
		Collections.sort(cacheTeachers, Comparator.comparing(Teacher::getName));
		return cacheTeachers;
	}
	
	@Override
	public List<Post> collectPosts() {
		if (cachePosts == null)
			cachePosts = collect(Post.class);
		
		Collections.sort(cachePosts, Comparator.comparing(Post::getName));
		return cachePosts;
	}
	
	private void saveOrUpdateRow(Object object) {
        Session session = null;
        Transaction tx = null;
        
        try {
        	session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
            
            session.saveOrUpdate(object);
            tx.commit();
        } catch (HibernateException e) {
        	if (tx != null && tx.wasRolledBack()) 
				tx.rollback();
			
			ErrorHandler.show(e);
        }
	}
	
	private void deleteRow(Object object) {
        Session session = null;
        Transaction tx = null;
        
        try {
        	session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
            
            session.delete(object);
            tx.commit();
        } catch (HibernateException e) {
        	if (tx != null && tx.wasRolledBack()) 
				tx.rollback();
			
			ErrorHandler.show(e);
        }
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> collect(Class<?> clazz) {
		List<T> list = null;
		try {
			Session session = getSessionFactory().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			list = session.createCriteria(clazz).list();
			tx.commit();
		} catch (HibernateException e) {
			ErrorHandler.show(e);
		}
		
		return list;
	}
}