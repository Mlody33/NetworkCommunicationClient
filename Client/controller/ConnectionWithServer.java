package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import application.ClientMain;
import javafx.application.Platform;
import model.Client;

public class ConnectionWithServer implements Runnable {
	
	private Logger log = Logger.getLogger("Client"+this.getClass().getName());
	private Socket clientSocket;
	private ObjectOutputStream outcomeStream;
	private ObjectInputStream incomeStream;
	
	private ClientController clientController;
	private ClientMain main;

	public void setClientController(ClientController clientController) {
		this.clientController = clientController;
	}
	
	public void setMain(ClientMain main) {
		this.main = main;
	}
	
	@Override
	public void run() {
		if(createClientSocket())
			main.getClientData().setConnected();
		else
			main.getClientData().setNotConnected();
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	if(main.getClientData().isConnected()) {
            		clientController.setUIConnected();
            		clientController.setClientIdentyfier();
            	} else
            		clientController.setUINotConnected();
            }
        });
		createInputOutputStream();
		sendClientDataToServer();
		readClientDataFromServer();
	}

	private boolean createClientSocket() {
		try {
			clientSocket = new Socket("localhost", 5588);
			return true;
		} catch (IOException e) {
			closeConnection();
			return false;
		} catch (NullPointerException e) {
			closeConnection();
			return false;
		}
	}
	
	private void createInputOutputStream() {
		try {
			outcomeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			incomeStream = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e1) {
			closeConnection();
		}
	}
	
	public void sendClientDataToServer() {
		main.getClientData().setTimeConnection(LocalDateTime.now());
		Client clientDataToSend = new Client();
		clientDataToSend.setClientData(main.getClientData());
		try {
			outcomeStream.writeObject(clientDataToSend);
			outcomeStream.flush();
		} catch (IOException e) {
			log.warning("Can't send object");
			closeConnection();
		}
	}
	
	public void readClientDataFromServer() {
		Client clientDataToRead = new Client();
		try {
			clientDataToRead = (Client) incomeStream.readObject();
			main.getClientData().setClientData(clientDataToRead);
		} catch (ClassNotFoundException | IOException e) {
			log.warning("Can't read object");
			closeConnection();
		}
	}

	public void checkStatus() {
		Platform.runLater(new Runnable(){
			@Override
	        public void run() {
				if(!main.getClientData().isConnected()){
					main.getClientData().setSignalToCommunicationWithServer(Signal.DISCONNECT);
					clientController.setUINotAuthorized();
					clientController.setUINotConnected();
					clientController.updateUISignal();
					closeConnection();
				} else if(main.getClientData().isAuthorized())
					clientController.setUIAuthorized();
				else
					clientController.setUINotAuthorized();
			}
		});
	}
	
	public void closeConnection() {
		try {
			clientSocket.close();
			outcomeStream.close();
			main.getClientData().setNotConnected();
			main.getClientData().setNotAuthorized();
		} catch (IOException | NullPointerException e) {
			log.warning("Can't close connection");
		}
	}

}