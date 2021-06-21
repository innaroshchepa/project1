package department.gui;

import java.io.PrintWriter;
import java.io.StringWriter;

import department.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ErrorHandler {
	
	public static void show(Exception e) {
		e.printStackTrace();
		
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(Main.primaryStage().getIcons().get(0));
			
			alert.setTitle("Помилка");
			alert.setHeaderText(e.getMessage());
			
			VBox dialogPaneContent = new VBox();
			
			Label label = new Label("Stack Trace:");
			TextArea textArea = new TextArea();
			
			textArea.setText(getStackTrace(e));
			textArea.setPrefSize(700, 400);
			
			dialogPaneContent.getChildren().addAll(label, textArea);
			
			alert.getDialogPane().setContent(dialogPaneContent);
			alert.showAndWait();
		});
	}
	
	private static String getStackTrace(Exception e) {
   		StringWriter sw = new StringWriter();
   		e.printStackTrace(new PrintWriter(sw));
   		return sw.toString();
   	}
}
