package controller;

import java.net.URL;
import java.util.ResourceBundle;

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
	
	private static final String NOT_CONNECTED = "Not connected";
	private static final String CONNECTED = "Connected";
	private static final String AUTHORIZED = "Authorized";
	private static final String SET_OFF = "Disconnect";
	private static final String SET_ON = "Connect";
	
	private Main main;
	private ConnectionThread connectionThread;
	
	public void setMain(Main main) {
		this.main = main;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setClientAppStatus(false, false);
	}

	@FXML
	public void connectionButtonAction() {
		connectionThread = new ConnectionThread();
		connectionThread.setClientController(this);
		connectionThread.setMain(main);
		connectionThread.start();
	}

	@FXML
	public void authorizationAction() {
		connectionThread.sendAuthorizationData();
	}
	
	public void setClientAppStatus(boolean connected, boolean authorized) {
		if(connected) {
			connectionBtn.setText(SET_OFF);
			if(authorized)
				statusTxt.setText(AUTHORIZED);
			else
				statusTxt.setText(CONNECTED);
			authorizationBtn.setDisable(false);
		} else {
			connectionBtn.setText(SET_ON);
			statusTxt.setText(NOT_CONNECTED);
			authorizationBtn.setDisable(true);
		}
	}
	
	public int getTypedAuthorizationCode() {
		return Integer.parseInt(authorizationCodeTf.getText());
	}

}
