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
		clientData.setConnected(createClientSocket());
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	if(clientData.isConnected())
            		clientController.setUIConnected();
            	else
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

	public void sendAuthorizationData() {
		try {
			clientData = new Client(main.getClientNumber(), clientData.isConnected(), clientController.getTypedAuthorizationCode(), clientData.isAuthorized(), LocalDate.now());
			outcomeClientData.writeObject(clientData);
			outcomeClientData.flush();
			System.out.println("[C]Object send");
		} catch (IOException e) {
			System.err.println("Error while sending data");
			e.printStackTrace();
			closeConnection();
		}
	}
	
	public void checkAuthorizationStatus() {
		try {
			clientData = (Client) incomeClientData.readObject();
			System.out.println("[C]Object received");
			System.out.println(clientData.toString());
			System.out.println("CHECK AUTHORIZATION:"+clientData.isAuthorized());
			Platform.runLater(new Runnable(){
				@Override
		        public void run() {
					if(clientData.isAuthorized())
						clientController.setUIAuthorized();
				}
			});
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			clientSocket.close();
			outcomeClientData.close();
			clientData.setConnected(false);
			clientData.setAuthorized(false);
			clientController.setUINotConnected();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
