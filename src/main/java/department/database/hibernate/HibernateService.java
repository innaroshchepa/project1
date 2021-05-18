package department.database.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateService {
	
	private static final SessionFactory sessionFactory;
	
	static {
		Configuration config = new Configuration().configure("hibernate.cfg.xml");
		
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties()).build(); 
		
        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
