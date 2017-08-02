package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.logging.Logger;

import application.Main;
import javafx.application.Platform;
import model.Client;

public class ConnectionThread extends Thread {
	
	private Logger log = Logger.getLogger("Client"+this.getClass().getName());
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
		if(createClientSocket())
			main.getClientData().setConnected();
		else
			main.getClientData().setNotConnected();
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
			controlClientData = new Client(main.getClientData().getClientNumber(), main.getClientData().isConnected(), clientController.getTypedAuthorizationCode(), main.getClientData().isAuthorized(), LocalDate.now());
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
			Client controlClientData = (Client) incomeClientData.readObject();
			log.info("[C]Object received: " + controlClientData.toString() + " AUTHORIZATION IS: "+controlClientData.isAuthorized());
			if(controlClientData.isAuthorized())
				main.getClientData().setAuthorized();
			else 
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
