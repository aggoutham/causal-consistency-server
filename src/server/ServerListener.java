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

/*
 * This class opens the DC server socket for external communication and to receive requests.
 * This class extends a Thread and run asynchronously to other handlers. It updates the static Data Structures
 * defined by StartServer.java to ensure causal consistency.
 */
public class ServerListener extends Thread {
	
	private int listenPort;
	private Socket socket;
	private HashMap<String,String> configMap;
	private static JSONObject allData;
	private static JSONObject dataversions;
	private static JSONObject dependencyObj;
	private int lamportClock;
	private String ownDC;
	private static ReplicatesHandler rh;
	
	public ServerListener(int listenPort, HashMap <String,String> configMap, JSONObject ad, JSONObject dObj, int l, JSONObject dv) {
		this.listenPort = listenPort;
		this.configMap = configMap;
		allData = ad;
		dependencyObj = dObj;
		this.lamportClock = l;
		dataversions = dv;
		this.ownDC = configMap.get("serverIP") + ":" + configMap.get("serverPort");
		
		rh = new ReplicatesHandler(allData,dataversions);
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
					otherdcID = mObj.getString("otherdcID");
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
						dependencyObj.put(cid, new JSONObject());
						return "Successfully registered cliend id :- " + cid + " with an empty dependency object";
					}
					//READ operation
					else if(operation.equals("READ")) {
						String variable = "";
						variable = mObj.getString("variable");
						if(allData.has(variable)) {
							
							JSONObject temp = new JSONObject();
							temp.put("version", dataversions.getInt(variable));
							temp.put("dataCenter", ownDC);
							//Add this read call to the client's dependency object
							dependencyObj.getJSONObject(cid).put(variable,temp);
							
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
						JSONObject dObj = new JSONObject();
						
						otherDC = mObj.getString("otherdcID");
						variable = mObj.getString("variable");
						writedata = mObj.getString("writedata");
						dObj = mObj.getJSONObject("dependency");
						
						int s1 = rh.performReplication(variable,writedata,otherDC,dObj,mObj.getInt("SenderClockValue"));
						int s2 = rh.tryQueuedObjects();
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
