package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerListener extends Thread {
	
	private int listenPort;
	private Socket socket;
	private HashMap<String,String> configMap;
	private JSONObject allData;
	private JSONObject dependencyObj;
	private int lamportClock;
	
	public ServerListener(int listenPort, HashMap <String,String> configMap, JSONObject ad, JSONObject dObj, int l) {
		this.listenPort = listenPort;
		this.configMap = configMap;
		this.allData = ad;
		this.dependencyObj = dObj;
		this.lamportClock = l;
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
				
				
				String respond = processInputMessage(str);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(respond.getBytes());
				

				/// Lamport clock logic
				System.out.println("CLOCK VALUE = " + Integer.toString(lamportClock));
			} 

			catch (Exception e) {
				System.out.println("Some Invalid Message has come! Receiving Message Failed");
			}
			return message;
		}
		
		private String processInputMessage(String inputStr) {
			String res = "Nothing to process";
			try {
				JSONObject mObj = new JSONObject(inputStr);
				String operation = mObj.getString("Operation");
				String authToken = mObj.getString("Authorization");
				String cid = "";
				String otherdcID = "";
				
				if(mObj.has("clientID")) {
					cid = mObj.getString("clientID");
				}
				
				///// Lamport Clock's Logic //////////////////
				if(mObj.has("SenderClockValue")) {
					int a = lamportClock + 1;
					int b = mObj.getInt("SenderClockValue") + 1;
					if(a>b) {
						lamportClock = a;
					}
					else {
						lamportClock = b;
					}
				}
				else {
					lamportClock = lamportClock + 1;
				}
				//////////////////////////////////////////////
				
				if(authToken.equals(configMap.get("authToken"))) {
					//Register Operation
					if(operation.equals("Register")){
						dependencyObj.put(cid, new JSONArray());
						return "Successfully registered cliend id :- " + cid + " with an empty dependency object";
					}
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
			
			
			
			return res;
		}

}
