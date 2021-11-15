package clientCli;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

import message.SendMessage;

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
			
			dcSocket = new Socket(connectedDCIP,connectedDCPort);
			int s = postRegisterMessage();
			
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
	        
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
	}
	
	
	
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
				postWriteMessage("WRITE",variable,writedata);
				System.out.println("WRITE operation was successful !!!");
			}		
		} else {
			System.out.println("ERR::Invalid Input::Supports only READ and WRITE operations.");
		}
		
		return;
	}

	
	
	
	public String postReadMessage(String op, String variable) {
		String resVal = "";
		
		
		
		return resVal;
	}
	
	public int postWriteMessage(String op, String variable, String writedata) {
		int status = 0;
		
		
		
		return status;
	}
	
	public int postRegisterMessage() {
		int status = 0;
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
		
		return status;
	}
	
	
	
	
	
}
