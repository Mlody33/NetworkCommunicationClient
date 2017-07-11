package controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;

import model.Client;

public class ConnectionThread extends Thread {
	
	private Socket clientSocket;
	private ObjectOutputStream outcomeClientData;
	private PrintWriter outcomeString;
	private Client clientData;
	
	@Override
	public void run() {
		
		try {
			clientSocket = new Socket("localhost", 5588);
		} catch (IOException e) {
			e.printStackTrace();
			closeConnection();
		}
		
		try {
			outcomeClientData = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.println("[C]Created outcome stream");
		} catch (IOException e1) {
			System.err.println("Error while creating outcome stream");
			e1.printStackTrace();
		}
		
		clientData = new Client("NAZWA", LocalDate.now());
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
