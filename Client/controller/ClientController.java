package controller;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.ClientMain;
import application.ClientStatuses;
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
		if(main.getClientData().isConnected()) {
			disconnectClient();
			updateUISignal();
			setUINotAuthorized();
			setUINotConnected();
		} else {
			connectClient();
			updateUISignal();
		}
	}

	private void connectClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.CONNECT);
		connectionThread = new ConnectionThread();
		connectionThread.setClientController(this);
		connectionThread.setMain(main);
		connectionThread.start();
	}
	
	public void disconnectClient() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.DISCONNECT);
		if(main.getClientData().isConnected()) {
			connectionThread.sendClientDataToServer();
			connectionThread.readClientDataFromServer();
			connectionThread.closeConnection();
		}
	}
	
	@FXML
	public void authorizeClient() {
		try {
			main.getClientData().setSignalToCommunicationWithServer(Signal.AUTHORIZE);
			main.getClientData().setAuthorizationCode(Integer.parseInt(authorizationCodeTf.getText()));
			updateTimeConnection();
			updateUISignal();
			connectionThread.sendClientDataToServer();
			connectionThread.readClientDataFromServer();
			connectionThread.checkStatus();
		} catch (NumberFormatException e) {
			statusTxt.setFill(Color.RED);
			statusTxt.setText(ClientStatuses.WRONG_AUTH_FIELD_VALUE.get());
		}
	}
	
	@FXML public void updateConnection() {
		main.getClientData().setSignalToCommunicationWithServer(Signal.UPDATE);
		connectionThread.sendClientDataToServer();
		connectionThread.readClientDataFromServer();
		connectionThread.checkStatus();
		updateTimeConnection();
		updateUISignal();
	}
	
	public void setUIConnected() {
		statusTxt.setFill(Color.BLACK);
		connectionBtn.setText(ClientStatuses.SET_APP_OFFLINE.get());
		statusTxt.setText(ClientStatuses.CLIENT_CONNECTED.get());
		authorizationBtn.setDisable(false);
		authorizationCodeTf.setDisable(false);
		updateConnectionBtn.setDisable(true);
		connectionTxt.setFill(Color.GREEN);
		connectionTxt.setText(ClientStatuses.OK.get());
	}
	
	public void setUINotConnected() {
		statusTxt.setFill(Color.RED);
		connectionBtn.setText(ClientStatuses.SET_APP_ONLINE.get());
		statusTxt.setText(ClientStatuses.CLIENT_NOT_CONNECTED.get());
		authorizationBtn.setDisable(true);
		authorizationCodeTf.setDisable(true);
		authorizationCodeTf.clear();
		updateConnectionBtn.setDisable(true);
		connectionTxt.setFill(Color.RED);
		connectionTxt.setText(ClientStatuses.NO.get());
		lastUpdateTxt.setFill(Color.RED);
		lastUpdateTxt.setText(ClientStatuses.NONE.get());
	}
	
	public void setUIAuthorized() {
		statusTxt.setFill(Color.BLACK);
		statusTxt.setText(ClientStatuses.CLIENT_AUTHORIZED.get());
		authorizationBtn.setDisable(true);
		authorizationCodeTf.setDisable(true);
		authorizationCodeTf.clear();
		updateConnectionBtn.setDisable(false);
		authorizationTxt.setFill(Color.GREEN);
		authorizationTxt.setText(ClientStatuses.OK.get());
		authorizationCodeTxt.setFill(Color.GREEN);
		authorizationCodeTxt.setText(String.valueOf(main.getClientData().getAuthorizationCode()));
	}
	
	public void setUINotAuthorized() {
		statusTxt.setFill(Color.RED);
		statusTxt.setText(ClientStatuses.CLIENT_NOT_AUTHORIZED.get());
		authorizationBtn.setDisable(false);
		authorizationCodeTf.setDisable(false);
		authorizationCodeTf.clear();
		updateConnectionBtn.setDisable(true);
		authorizationTxt.setFill(Color.RED);
		authorizationTxt.setText(ClientStatuses.NO.get());
		authorizationCodeTxt.setFill(Color.RED);
		authorizationCodeTxt.setText(ClientStatuses.NONE.get());
	}
	
	public void updateTimeConnection() {
		lastUpdateTxt.setFill(Color.GREEN);
		long lastUpdateInDays = Duration.between(main.getClientData().getTimeConnection(), LocalDateTime.now()).toDays();
		if(lastUpdateInDays >= 1)
			lastUpdateTxt.setText(main.getClientData().getTimeConnection().withNano(0).toString());
		else
			lastUpdateTxt.setText(main.getClientData().getTimeConnection().toLocalTime().withNano(0).toString());
	}
		
	
	public void updateUISignal() {
		signalToCommunicationWithServerTxt.setFill(Color.GREEN);
		signalToCommunicationWithServerTxt.setText(main.getClientData().getSignalToCommunicationWithServer().name());
	}
	
	public void setClientIdentyfier() {
		identyfierTxt.setFill(Color.BLACK);
		identyfierTxt.setText(String.valueOf(main.getClientData().getClientNumber()));
	}
	
}