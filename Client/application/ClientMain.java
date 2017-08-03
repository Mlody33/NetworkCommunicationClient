package application;
	
import java.io.IOException;
import java.util.Random;

import controller.ClientController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Client;


public class ClientMain extends Application {
	
	private Client clientData = new Client();
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
		loader.setLocation(ClientMain.class.getResource("../view/ClientView.fxml"));

		rootLayout = loader.load();
		Scene scene = new Scene(rootLayout);
		
		clientData.setClientNumber(new Random().nextInt(89)+10);
		
		ClientController clientController = loader.getController();
		clientController.setMain(this);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle(StatusTextDB.TITLE_OF_APP.get() + " " + clientData.getClientNumber());
		primaryStage.show();
		
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					System.out.println("Clsoe");
				}
			});
			
		}
	
	public Client getClientData() {
		return this.clientData;
	}
	
}
