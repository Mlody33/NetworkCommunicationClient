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

public class ConnectionThread extends Thread {
	
	private Logger log = Logger.getLogger("Client"+this.getClass().getName());
	private Socket clientSocket;
	private ObjectOutputStream outcomeClientData;
	private ObjectInputStream incomeClientData;
	
	private Client controlClientData; //FIXME use general object in main
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
	}

	private boolean createClientSocket() {
		try {
			clientSocket = new Socket("localhost", 5588);
			return true;
		} catch (IOException e) {
			closeConnection();
			e.printStackTrace();
			return false;
		}
	}
	
	private void createInputOutputStream() {
		try {
			outcomeClientData = new ObjectOutputStream(clientSocket.getOutputStream());
			incomeClientData = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e1) {
			closeConnection();
			e1.printStackTrace();
		}
	}
	
	public void sendDataToServer() {
		try {
			outcomeClientData.writeObject(controlClientData);
			outcomeClientData.flush();
		} catch (IOException e) {
			log.warning("Error while sending data");
			closeConnection();
			e.printStackTrace();
		}
	}
	
	public void readDataFromServer() {
		try {
			controlClientData = (Client) incomeClientData.readObject();
		} catch (ClassNotFoundException | IOException e) {
			closeConnection();
			e.printStackTrace();
		}
	}

	public void sendAuthorizationData() {
		try {
			controlClientData = new Client(main.getClientData().getClientNumber(), main.getClientData().isConnected(), clientController.getTypedAuthorizationCode(), main.getClientData().isAuthorized(), LocalDateTime.now());
			outcomeClientData.writeObject(controlClientData);
			outcomeClientData.flush();
			log.info("[C]Object send");
		} catch (IOException e) {
			log.warning("Error while sending data");
			e.printStackTrace();
			closeConnection();
		}
	}
	
	public void checkAuthorizationStatus() {
		try {
			controlClientData = (Client) incomeClientData.readObject();
			log.info("[C]Object received: " + controlClientData.toString() + " AUTHORIZATION IS: "+controlClientData.isAuthorized());
			if(controlClientData.isAuthorized()) {
				main.getClientData().setAuthorized();
				main.getClientData().setAuthorizationCode(controlClientData.getAuthorizationCode());
			} else 
				main.getClientData().setNotAuthorized();
			Platform.runLater(new Runnable(){
				@Override
		        public void run() {
					if(main.getClientData().isAuthorized())
						clientController.setUIAuthorized();
					else
						clientController.setUINotAuthorized();
				}
			});
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	

	public void sendDisconnectSignal() {
		try {
			outcomeClientData.writeObject(main.getClientData());
			outcomeClientData.flush();
		} catch (IOException e) {
			e.printStackTrace();
			closeConnection();
		}
		log.info("[C]Send signal to disconnect");
	}
	
	public void closeConnection() {
		try {
			clientSocket.close();
			outcomeClientData.close();
			main.getClientData().setNotConnected();
			main.getClientData().setNotAuthorized();
			clientController.setUINotConnected();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
