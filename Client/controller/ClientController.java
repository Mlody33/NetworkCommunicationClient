package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.StatusTextDB;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class ClientController implements Initializable {

	@FXML private Button authorizationBtn;
	@FXML private Button connectionBtn;
	@FXML private PasswordField authorizationCodeTf;
	@FXML private Text statusTxt;
	
	private Main main;
	private ConnectionThread connectionThread;
	
	public void setMain(Main main) {
		this.main = main;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setUINotConnected();
	}

	@FXML
	public void connectClientToServer() {
		connectionThread = new ConnectionThread();
		connectionThread.setClientController(this);
		connectionThread.setMain(main);
		connectionThread.start();
	}

	@FXML
	public void authorizeClient() {
		connectionThread.sendAuthorizationData();
		connectionThread.checkAuthorizationStatus();
	}
	
	public void setUIConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_OFF.get());
		statusTxt.setText(StatusTextDB.CLIENT_CONNECTED.get());
		authorizationBtn.setDisable(false);
	}
	
	public void setUINotConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_ON.get());
		statusTxt.setText(StatusTextDB.CLIENT_NOT_CONNECTED.get());
		authorizationBtn.setDisable(true);
	}
	
	public void setUIAuthorized() {
		statusTxt.setText(StatusTextDB.CLIENT_AUTHORIZED.get());
	}
	
	public int getTypedAuthorizationCode() {
		return Integer.parseInt(authorizationCodeTf.getText());
	}

}
