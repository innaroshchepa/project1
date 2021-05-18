package department.database;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Teacher;

import department.gui.ErrorHandler;

import static department.database.hibernate.HibernateService.*;


public class DatabaseManagerImpl implements DatabaseManager {
	
	private static final DatabaseManagerImpl instance = new DatabaseManagerImpl();
	
	private List<Department> cacheDepartments;
	private List<Teacher> cacheTeachers;
	private List<Post> cachePosts;
	
	public static DatabaseManagerImpl instance() {
		return instance;
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
		if (cacheDepartments != null) {
			cacheDepartments.remove(department);
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
		deleteRow(post);
		if (cachePosts != null) {
			cachePosts.remove(post);
		}
	}
	
	@Override
	public List<Department> collectDepartments() {
		if (cacheDepartments == null)
			return cacheDepartments = collect(Department.class);
		
		return cacheDepartments;
	}
	
	@Override
	public List<Teacher> collectTeachers() {
		if (cacheTeachers == null)
			return cacheTeachers = collect(Teacher.class);
		
		return cacheTeachers;
	}
	
	@Override
	public List<Post> collectPosts() {
		if (cachePosts == null)
			return cachePosts = collect(Post.class);
		
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