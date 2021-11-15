package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ServerListener extends Thread {
	
	private int listenPort;
	private Socket socket;
	private HashMap<String,String> configMap;
	
	public ServerListener(int listenPort, HashMap <String,String> configMap) {
		this.listenPort = listenPort;
		this.configMap = configMap;
	}
	
	//This is the main listener run method for the Server's thread.
		@Override
		public void run() {
			System.out.println("Starting DataCenter...");
			System.out.println("Listening on Port :- " + listenPort);
			try {
				ServerSocket listener = new ServerSocket(listenPort);
				while(true) {
					socket = listener.accept();
					byte[] message = receiveMessage();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Once there is some request in the InputStream, all messages are processed in this single method.
		private byte[] receiveMessage() {
			
			byte[] message = null;
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				message = (byte[]) in.readObject();
				System.out.println("Server Received the following message :- ");
				String str = new String(message, StandardCharsets.UTF_8);
				System.out.println(str);
				System.out.println("Server Printed the message above!!\n");
				
				String respond = str;
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(respond.getBytes());
			} 

			catch (Exception e) {
				System.out.println("Some Invalid Message has come! Receiving Message Failed");
			}
			return message;
		}
	

}
