package clientCli;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

import message.SendMessage;

/*
 * This class is the primary Client Thread that begins the Interactive CLI for the user to send READ, WRITE commands.
 */
public class StartCli {
	
	private HashMap<String,String> configMap;
	private String connectedDC;
	private String connectedDCIP;
	private int connectedDCPort;
	private Socket dcSocket;
	
	public StartCli(HashMap <String,String> configMap){
		this.configMap = configMap;
		this.connectedDC = this.configMap.get("connectedDC");
		this.connectedDCIP = connectedDC.split(":")[0];
		this.connectedDCPort = Integer.parseInt(connectedDC.split(":")[1]);
	}

	//This method begins the interactive session and runs in a while loop until a user wants to exit.
	//The user may provide his/her interested messages as a client.
	
	public void enableInputs() {
		
		try {
			int s = postRegisterMessage();
			if(s==0) {
				System.out.println("ERR:: Registration with Data Center Failed!");
				return;
			}
			System.out.println("\n\n$$$$$$ Welcome to Causal Consistency Interactive Shell $$$$$$");
	        System.out.println("List out one (OR multiple) messages seperated by new line characters :- \n");
	        System.out.println("Example1 - READ,<key_name>");
	        System.out.println("Example2 - WRITE,<key_name>,<value_string>\n");
	        
	        System.out.println("Please Start Entering Here ------------- \n");
	        
	        while(true) {
	        	int check = 0;
	            Scanner sc = new Scanner(System.in);
	            while(sc.hasNextLine()){
	            	String userInput = sc.nextLine();
	                if(userInput.equals("exit")) {
	                	check = 1;
	                	break;
	                }
	                processInput(userInput);
	            }
	            if(check == 1) {
	            	System.out.println("Thank you!!! Goodbye!");
	            	sc.close();
	            	break;
	            }
	            sc.close();
	        }
	        return;       
		} catch (Exception e) {
			System.out.println(e);
		}
		
        
	}
	
	
	// This method processes different types of user inputs provided in the Interactive commanc Shell.
	public void processInput(String userInput) {
		String[] parts = userInput.split(",");
		String variable = "";
		String writedata = "";
		if(parts[0].equals("READ")) {
			if(parts.length != 2) {
				System.out.println("ERR::Invalid Input::READ operation must only have 2 parameters");
			}
			else{
				variable = parts[1];
				String val = postReadMessage("READ",variable);
				System.out.println("READ operation was successful :- ");
				System.out.println(variable + "=" + val);
				
			}
			
		} else if(parts[0].equals("WRITE")) {
			if(parts.length != 3) {
				System.out.println("ERR::Invalid Input::WRITE operation must only have 3 parameters");
			}
			else {
				variable = parts[1];
				writedata = parts[2];
				int s = postWriteMessage("WRITE",variable,writedata);
				if(s==1) {
					System.out.println("WRITE operation was successful !!!");
				}
				else {
					System.out.println("ERR:: WRITE operation failed");
				}
			}		
		} else {
			System.out.println("ERR::Invalid Input::Supports only READ and WRITE operations.");
		}
		
		return;
	}

	
	
	//This method handles a READ message
	public String postReadMessage(String op, String variable) {
		String response = "";
		try {
			dcSocket = new Socket(connectedDCIP,connectedDCPort);
			JSONObject reqObj = new JSONObject();
			reqObj.put("Authorization",configMap.get("authToken"));
			reqObj.put("Operation", "READ");
			reqObj.put("clientID", configMap.get("clientId"));
			reqObj.put("variable", variable);
			
			SendMessage sm = new SendMessage();
			String messageStr = reqObj.toString();
			byte[] message = messageStr.getBytes();
	    	response = sm.sendReq(dcSocket, message);
	    	if(response.contains("ERR::")) {
	    		System.out.println("RESPONSE: " + response);
	    		return "";
	    	}
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	//This method handles a WRITE message
	public int postWriteMessage(String op, String variable, String writedata) {
		int status = 1;
		String response = "";

		try {
			dcSocket = new Socket(connectedDCIP,connectedDCPort);
			JSONObject reqObj = new JSONObject();
			reqObj.put("Authorization",configMap.get("authToken"));
			reqObj.put("Operation", "WRITE");
			reqObj.put("clientID", configMap.get("clientId"));
			reqObj.put("variable", variable);
			reqObj.put("writedata", writedata);
			
			SendMessage sm = new SendMessage();
			String messageStr = reqObj.toString();
			byte[] message = messageStr.getBytes();
	    	response = sm.sendReq(dcSocket, message);
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	if(!response.equals("SUCCESS")) {
    		return 0;
    	}
    	
		return status;
	}
	
	//This method handles the initial REGISTER request when the client first connects to its DC.
	public int postRegisterMessage() {
		int status = 0;

		try {
			dcSocket = new Socket(connectedDCIP,connectedDCPort);
			JSONObject reqObj = new JSONObject();
			reqObj.put("Authorization",configMap.get("authToken"));
			reqObj.put("Operation", "Register");
			reqObj.put("clientID", configMap.get("clientId"));
			
			SendMessage sm = new SendMessage();
			String messageStr = reqObj.toString();
			System.out.println("REQUEST: " + messageStr);
			byte[] message = messageStr.getBytes();
	    	String response = sm.sendReq(dcSocket, message);
	    	
	    	System.out.println("RESPONSE: " + response);
	    	return 1;
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return status;
	}
	
	
	
	
	
}
