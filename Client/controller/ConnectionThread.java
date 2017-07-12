package controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;

import javafx.application.Platform;
import model.Client;

public class ConnectionThread extends Thread {
	
	private Socket clientSocket;
	private ObjectOutputStream outcomeClientData;
	private Client clientData;
	private boolean connected = false;
	private ClientController clientController;

	public void setClientController(ClientController clientController) {
		this.clientController = clientController;
	}
	
	
	@Override
	public void run() {
		
		try {
			clientSocket = new Socket("localhost", 5588);
		} catch (IOException e) {
			closeConnection();
			e.printStackTrace();
		}
		
		connected = true;
		
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	clientController.setClientStatus(connected);
            }
        });
		
		try {
			outcomeClientData = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.println("[C]Created outcome stream");
		} catch (IOException e1) {
			closeConnection();
			System.err.println("Error while creating outcome stream");
			e1.printStackTrace();
		}
		
		clientData = new Client("NAZWA", 0, LocalDate.now());
		System.out.println(clientData.toString());
		
		try {
			outcomeClientData.writeObject(clientData);
			outcomeClientData.flush();
			System.out.println("[C]Object send");
		} catch (IOException e) {
			System.err.println("Error while sending data");
			e.printStackTrace();
			closeConnection();
		}
	}
	
	public void closeConnection() {
		try {
			clientSocket.close();
			outcomeClientData.close();
			connected = false;
			clientController.setClientStatus(connected);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
