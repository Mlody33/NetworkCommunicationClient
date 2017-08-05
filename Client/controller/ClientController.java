package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.ClientMain;
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
	@FXML private Button updateConnectionBtn;
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
		if(main.getClientData().isConnected())
			disconnectClient();
		else
			connectClient();
	}

	private void connectClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.CONNECT);
		updateUISignal();
		connectionThread = new ConnectionThread();
		connectionThread.setClientController(this);
		connectionThread.setMain(main);
		connectionThread.start();
	}
	
	private void disconnectClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.DISCONNECT);
		updateUISignal();
		connectionThread.sendClientDataToServer();
		connectionThread.readClientDataFromServer();
		connectionThread.closeConnection();
		setUINotAuthorized();
		setUINotConnected();
	}
	
	@FXML
	public void authorizeClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.AUTHORIZE);
		main.getClientData().setAuthorizationCode(Integer.parseInt(authorizationCodeTf.getText()));
		updateTimeConnection();
		updateUISignal();
		connectionThread.sendClientDataToServer();
		connectionThread.readClientDataFromServer();
		connectionThread.checkAuthorizationStatus();
	}
	
	@FXML public void updateConnection() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.UPDATE);
		updateTimeConnection();
		connectionThread.sendClientDataToServer();
		connectionThread.readClientDataFromServer();
	}
	
	public void setUIConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_OFFLINE.get());
		statusTxt.setText(StatusTextDB.CLIENT_CONNECTED.get());
		authorizationBtn.setDisable(false);
		authorizationCodeTf.setDisable(false);
		updateConnectionBtn.setDisable(true);
		connectionTxt.setFill(Color.GREEN);
		connectionTxt.setText(StatusTextDB.OK.get());
	}
	
	public void setUINotConnected() {
		connectionBtn.setText(StatusTextDB.SET_APP_ONLINE.get());
		statusTxt.setText(StatusTextDB.CLIENT_NOT_CONNECTED.get());
		authorizationBtn.setDisable(true);
		authorizationCodeTf.setDisable(true);
		updateConnectionBtn.setDisable(true);
		connectionTxt.setFill(Color.RED);
		connectionTxt.setText(StatusTextDB.NOT.get());
		lastUpdateTxt.setFill(Color.RED);
		lastUpdateTxt.setText(StatusTextDB.NONE.get());
	}
	
	public void setUIAuthorized() {
		statusTxt.setText(StatusTextDB.CLIENT_AUTHORIZED.get());
		authorizationBtn.setDisable(true);
		authorizationCodeTf.setDisable(true);
		updateConnectionBtn.setDisable(false);
		authorizationTxt.setFill(Color.GREEN);
		authorizationTxt.setText(StatusTextDB.OK.get());
		authorizationCodeTxt.setFill(Color.GREEN);
		authorizationCodeTxt.setText(String.valueOf(main.getClientData().getAuthorizationCode()));
	}
	
	public void setUINotAuthorized() {
		statusTxt.setText(StatusTextDB.CLIENT_NOT_AUTHORIZED.get());
		authorizationBtn.setDisable(false);
		authorizationCodeTf.setDisable(false);
		updateConnectionBtn.setDisable(true);
		authorizationTxt.setFill(Color.RED);
		authorizationTxt.setText(StatusTextDB.NOT.get());
		authorizationCodeTxt.setFill(Color.RED);
		authorizationCodeTxt.setText(StatusTextDB.NONE.get());
	}
	
	public void updateTimeConnection() {
		lastUpdateTxt.setFill(Color.GREEN);
		lastUpdateTxt.setText(String.valueOf(main.getClientData().getTimeConnection()));
	}
	
	public void updateUISignal() {
		signalToCommunicationWithServerTxt.setFill(Color.GREEN);
		signalToCommunicationWithServerTxt.setText(main.getClientData().getSignalToCommunicationWithServer().name());
	}
	
	public void setClientIdentyfier() {
		identyfierTxt.setFill(Color.BLACK);
		identyfierTxt.setText(String.valueOf(main.getClientData().getClientNumber()));
	}
	
	public int getTypedAuthorizationCode() {
		return Integer.parseInt(authorizationCodeTf.getText());
	}

}
