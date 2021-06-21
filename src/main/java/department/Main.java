package department;


import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import department.database.hibernate.HibernateService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import javafx.stage.Stage;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

public class Main extends Application {
	
	private static Stage stage;
	
	public static void main(String[] args) {
		singletonApp();
		PropertyConfigurator.configure(Main.class.getResource("/log4j.properties"));
		
		databaseConnect();
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		
		Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
		Scene scene = new Scene(root, 1250, 700);
		
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		
		primaryStage.setTitle("Відділ кадрів");
		primaryStage.setMinWidth(250);
		primaryStage.setMinHeight(250);
		primaryStage.getIcons().add(new Image(getClass().getResource("/logo.png").toExternalForm()));
		primaryStage.setScene(scene);
		
		//primaryStage.setFullScreen(true);
		primaryStage.setMaximized(true);		
		primaryStage.show();	
	}
	
	private static void databaseConnect() {
		System.out.println("[SQLite]: Connecting to DB...");
		
		Session session = HibernateService.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		SQLQuery query = session.createSQLQuery("PRAGMA encoding = 'UTF-8';");
		System.out.println("[SQLite]: Setting UTF-8 charset on DB: " + query.executeUpdate());
		
		session.getTransaction().commit();
	}
	
	private static void singletonApp() {
		// приложение может быть запущено только в одном экземпляре
		boolean alreadyRunning;
		try {
			JUnique.acquireLock("department");
			alreadyRunning = false;
		} catch (AlreadyLockedException e) {
			alreadyRunning = true;
		}
		
		if (alreadyRunning) {
			System.out.println("An Instance of this app is already running");
		    System.exit(1);
		}
	}
	
	public static Stage primaryStage() {
		return stage;
	}
}
