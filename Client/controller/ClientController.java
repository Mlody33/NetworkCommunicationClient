package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;

public class ClientController implements Initializable {

	@FXML Button loginBtn;
	@FXML Button connectionBtn;
	@FXML PasswordField passwordTf;
	@FXML Text statusTxt;
	
	private static final String NOT_CONNECTED = "Not connected";
	private static final String CONNECTED = "Connected";
	private static final String SET_OFF = "Disconnect";
	private static final String SET_ON = "Connect"; 
	
	private Main main;
	
	public void setMain(Main main) {
		this.main = main;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		connectionBtn.setText(SET_ON);
		statusTxt.setText(NOT_CONNECTED);
		loginBtn.setDisable(true);
	}

	@FXML public void connectionButtonAction() {
		ConnectionThread connectionThread = new ConnectionThread();
		connectionThread.setClientController(this);
		connectionThread.start();
	}

	@FXML public void loginAction() {
		
	}
	
	public void setClientStatus(boolean connected) {
		if(connected) {
			connectionBtn.setText(SET_OFF);
			statusTxt.setText(CONNECTED);
			loginBtn.setDisable(false);
		} else {
			connectionBtn.setText(SET_ON);
			statusTxt.setText(NOT_CONNECTED);
			loginBtn.setDisable(true);
		}
	}

}
