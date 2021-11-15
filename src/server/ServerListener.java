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
import writeUtil.ReplicatedWrite;

public class ServerListener extends Thread {
	
	private int listenPort;
	private Socket socket;
	private HashMap<String,String> configMap;
	private JSONObject allData;
	private JSONObject dataversions;
	private JSONObject dependencyObj;
	private int lamportClock;
	
	public ServerListener(int listenPort, HashMap <String,String> configMap, JSONObject ad, JSONObject dObj, int l, JSONObject dv) {
		this.listenPort = listenPort;
		this.configMap = configMap;
		this.allData = ad;
		this.dependencyObj = dObj;
		this.lamportClock = l;
		this.dataversions = dv;
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
				System.out.println("\n\nServer Received the following message :- ");
				String str = new String(message, StandardCharsets.UTF_8);
				System.out.println(str);
				System.out.println("Server Printed the message above!!");
				
				
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
			String res = "ERR::Nothing to process";
			try {
				JSONObject mObj = new JSONObject(inputStr);
				String operation = mObj.getString("Operation");
				String authToken = mObj.getString("Authorization");
				String cid = "";
				String otherdcID = "";
				
				if(mObj.has("clientID")) {
					cid = mObj.getString("clientID");
				}
				if(mObj.has("otherdcID")) {
					cid = mObj.getString("otherdcID");
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
					//READ operation
					else if(operation.equals("READ")) {
						String variable = "";
						variable = mObj.getString("variable");
						if(allData.has(variable)) {
							return allData.getString(variable);
						}
						else {
							return "ERR::The variable " + variable + " is not present in this DC yet!!";
						}
					}
					//WRITE operation
					else if(operation.equals("WRITE")) {
						String variable = "";
						String writedata = "";
						variable = mObj.getString("variable");
						writedata = mObj.getString("writedata");
						
						allData.put(variable, writedata);
						dataversions.put(variable,lamportClock);
						
						// In-Parallel starts a thread for replications
						ReplicatedWrite rw = new ReplicatedWrite(configMap,cid,variable,writedata,dependencyObj,lamportClock);
						rw.start();
						
						return "SUCCESS";
					}
					else if(operation.equals("REPLICATE")) {
						
						String otherDC = "";
						String variable = "";
						String writedata = "";
						JSONArray dArr = new JSONArray();
						
						otherDC = mObj.getString("otherdcID");
						variable = mObj.getString("variable");
						writedata = mObj.getString("writedata");
//						dArr = mObj.getJSONArray("dependency");
						
						
						return "SUCCESS";
						
					}
					else {
						return "ERR::Invalid operation from client. Allowed operations - Register, READ, WRITE";
					}
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
			
			
			
			return res;
		}

}
