package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import application.ClientMain;
import javafx.application.Platform;
import model.Client;
import model.TestCom;

public class ConnectionThread extends Thread {
	
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
		createClientSocket();
		createInputOutputStream();
		
		Client testCom = new Client();
		testCom.setClientNumber(1234);
		
		try {
			outcomeStream.writeObject(testCom);
			outcomeStream.flush();
			log.info("Send object to server: " + testCom.toString());
		} catch (IOException e) {
			log.warning("Error while sending object to server");
			closeConnection();
			e.printStackTrace();
		}
		
		try {
			Client testComRead = (Client) incomeStream.readObject();
			log.info("Read object from server: " + testComRead.toString());
		} catch (ClassNotFoundException | IOException e) {
			log.warning("Error while reading object from server");
			closeConnection();
			e.printStackTrace();
		}
		
	}
	
	public void sendNewObject() {
		Client testCom = new Client();
		testCom.setClientNumber(9999);
		testCom.setAuthorized();
		testCom.setNotConnected();
		
		try {
			outcomeStream.writeObject(testCom);
			outcomeStream.flush();
			log.info("Send object to server: " + testCom.toString());
		} catch (IOException e) {
			log.warning("Error while sending object to server");
			closeConnection();
			e.printStackTrace();
		}
		
		try {
			Client testComRead = (Client) incomeStream.readObject();
			log.info("Read object from server: " + testComRead.toString());
		} catch (ClassNotFoundException | IOException e) {
			log.warning("Error while reading object from server");
			closeConnection();
			e.printStackTrace();
		}
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
			outcomeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			incomeStream = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e1) {
			closeConnection();
			e1.printStackTrace();
		}
	}
	
	public void sendClientDataToServer() {
		try {
			outcomeStream.writeObject(main.getClientData());
			outcomeStream.flush();
			log.info("Send object to server: " + main.getClientData().toString());
		} catch (IOException e) {
			log.warning("Error while sending object to server");
			closeConnection();
			e.printStackTrace();
		}
	}
	
	public void readClientDataFromServer() {
		try {
			Client controlClientData = (Client) incomeStream.readObject();
			main.getClientData().setClient(controlClientData);
			log.info("Read object from server: " + controlClientData.toString());
//			log.info("-----------------------: " + main.getClientData().toString());
		} catch (ClassNotFoundException | IOException e) {
			log.warning("Error while reading object from server");
			closeConnection();
			e.printStackTrace();
		}
	}

	public void checkAuthorizationStatus() {
		Platform.runLater(new Runnable(){
			@Override
	        public void run() {
				if(main.getClientData().isAuthorized())
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
