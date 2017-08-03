package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.ClientMain;
import application.Signal;
import application.StatusTextDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ClientController implements Initializable {

	@FXML private Button authorizationBtn;
	@FXML private Button connectionBtn;
	@FXML private PasswordField authorizationCodeTf;
	@FXML private Text statusTxt;
	
	@FXML private Text identyfierTxt;
	@FXML private Text lastUpdateTxt;
	@FXML private Text authorizationTxt;
	@FXML private Text authorizationCodeTxt;
	@FXML private Text connectionTxt;
	@FXML private Text signalToCommunicationWithServerTxt;
	
	private ClientMain main;
	private ConnectionThread connectionThread;
	
	public void setMain(ClientMain main) {
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
	
	@FXML public void debugConnection() {
		connectionThread.sendNewObject();
	}
	
	@FXML
	public void authorizeClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.AUTHORIZE.get());
		main.getClientData().setAuthorizationCode(Integer.parseInt(authorizationCodeTf.getText()));
		setUISignal();
		connectionThread.sendClientDataToServer();
		connectionThread.readClientDataFromServer();
		connectionThread.checkAuthorizationStatus();
	}
	
	public void setUIConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_OFFLINE.get());
		statusTxt.setText(StatusTextDB.CLIENT_CONNECTED.get());
		authorizationBtn.setDisable(false);
		connectionTxt.setFill(Color.GREEN);
		connectionTxt.setText(StatusTextDB.OK.get());
	}
	
	public void setUINotConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_ONLINE.get());
		statusTxt.setText(StatusTextDB.CLIENT_NOT_CONNECTED.get());
		authorizationBtn.setDisable(true);
		connectionTxt.setFill(Color.RED);
		connectionTxt.setText(StatusTextDB.NOT.get());
	}
	
	public void setUIAuthorized() {
		statusTxt.setText(StatusTextDB.CLIENT_AUTHORIZED.get());
		authorizationBtn.setDisable(true);
		authorizationTxt.setFill(Color.GREEN);
		authorizationTxt.setText(StatusTextDB.OK.get());
		authorizationCodeTxt.setFill(Color.GREEN);
		authorizationCodeTxt.setText(String.valueOf(main.getClientData().getAuthorizationCode()));
	}
	
	public void setUINotAuthorized() {
		statusTxt.setText(StatusTextDB.CLIENT_NOT_AUTHORIZED.get());
		authorizationBtn.setDisable(false);
		authorizationTxt.setFill(Color.RED);
		authorizationTxt.setText(StatusTextDB.NOT.get());
		authorizationCodeTxt.setFill(Color.RED);
		authorizationCodeTxt.setText(StatusTextDB.NONE.get());
	}
	
	public void setUISignal() {
		signalToCommunicationWithServerTxt.setFill(Color.GREEN);
		signalToCommunicationWithServerTxt.setText(String.valueOf(main.getClientData().getSignalToCommunicationWithServer()));
	}
	
	public void setClientIdentyfier() {
		identyfierTxt.setFill(Color.BLACK);
		identyfierTxt.setText(String.valueOf(main.getClientData().getClientNumber()));
	}
	
	public int getTypedAuthorizationCode() {
		return Integer.parseInt(authorizationCodeTf.getText());
	}

}
