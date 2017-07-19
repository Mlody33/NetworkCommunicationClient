package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;

import application.Main;
import javafx.application.Platform;
import model.Client;

public class ConnectionThread extends Thread {
	
	private Socket clientSocket;
	private ObjectOutputStream outcomeClientData;
	private ObjectInputStream incomeClientData;
	
	private boolean connected = false;
	private boolean authorized = false;
	
	private Client clientData;
	private ClientController clientController;
	private Main main;

	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setClientController(ClientController clientController) {
		this.clientController = clientController;
	}
	
	@Override
	public void run() {
		connected = createClientSocket();
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	clientController.setClientAppStatus(connected, authorized);
            }
        });
		connected = createInputOutputStream();
	}

	private boolean createInputOutputStream() {
		try {
			outcomeClientData = new ObjectOutputStream(clientSocket.getOutputStream());
			incomeClientData = new ObjectInputStream(clientSocket.getInputStream());
			return true;
		} catch (IOException e1) {
			closeConnection();
			e1.printStackTrace();
			return false;
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

	public void sendAuthorizationData() {
		clientData = new Client("NAZWA", clientController.getTypedAuthorizationCode(), main.getClientNumber(), LocalDate.now(), authorized);
		
		try {
			outcomeClientData.writeObject(clientData);
			outcomeClientData.flush();
			System.out.println("[C]Object send");
			checkAuthorization();
		} catch (IOException e) {
			System.err.println("Error while sending data");
			e.printStackTrace();
			closeConnection();
		}
	}
	
	private void checkAuthorization() {
		try {
			clientData = (Client) incomeClientData.readObject();
			System.out.println("[C]Object received");
			System.out.println(clientData.toString());
			System.out.println("CHECK AUTHORIZATION:"+clientData.isAuthorized());
			authorized = clientData.isAuthorized();
			if(clientData.isAuthorized()) {
				Platform.runLater(new Runnable(){
		            @Override
		            public void run() {
		            	clientController.setClientAppStatus(connected, authorized);
		            }
		        });
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			clientSocket.close();
			outcomeClientData.close();
			connected = false;
			clientController.setClientAppStatus(connected, authorized);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
