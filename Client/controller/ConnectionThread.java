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
	
	private Client controlClientData;
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
		main.getClientData().setConnected(createClientSocket());
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	if(main.getClientData().isConnected())
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
			Client controlClientData = new Client(main.getClientData().getClientNumber(), main.getClientData().isConnected(), clientController.getTypedAuthorizationCode(), main.getClientData().isAuthorized(), LocalDate.now());
			outcomeClientData.writeObject(controlClientData);
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
			Client controlClientData = (Client) incomeClientData.readObject();
			System.out.println("[C]Object received");
			System.out.println(controlClientData.toString());
			System.out.println("CHECK AUTHORIZATION:"+controlClientData.isAuthorized());
			main.getClientData().setAuthorized(controlClientData.isAuthorized());
			Platform.runLater(new Runnable(){
				@Override
		        public void run() {
					if(main.getClientData().isAuthorized())
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
			main.getClientData().setConnected(false);
			main.getClientData().setAuthorized(false);
			clientController.setUINotConnected();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
