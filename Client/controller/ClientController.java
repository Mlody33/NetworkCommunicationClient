package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class ClientController implements Initializable {

	private static final String NOT_CONNECTED = "Not connected";
	private static final String CONNECTED_BTN = "Disconnect";
	private static final String DISCONNECTED_BTN = "Connect"; 
	@FXML Button connectionButton;
	@FXML Text connectionText;
	
	private Main main;
	
	public void setMain(Main main) {
		this.main = main;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		connectionButton.setText(DISCONNECTED_BTN);
		connectionText.setText(NOT_CONNECTED);
	}

	@FXML public void connectionButtonAction() {
		ConnectionThread connectionThread = new ConnectionThread();
		connectionThread.start();
	}

}
