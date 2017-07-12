package application;
	
import java.io.IOException;

import controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static final String TITLE = "KLIENT #";
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		try {
			initMainView();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void initMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../view/view.fxml"));

		rootLayout = loader.load();
		Scene scene = new Scene(rootLayout);
		
		ClientController clientController = loader.getController();
		clientController.setMain(this);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle(TITLE);
		primaryStage.show();
		
	}
	
}
