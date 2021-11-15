package clientCli;

import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

public class StartCli {
	
	private HashMap<String,String> configMap;
	
	public StartCli(HashMap <String,String> configMap){
		this.configMap = configMap;
	}

	//This method begins the interactive session and runs in a while loop until a user wants to exit.
	//The user may provide his/her interested messages as a client.
	
	public void enableInputs() {
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
				System.out.println("READ operation was successful");
				
			}
			
		} else if(parts[0].equals("WRITE")) {
			if(parts.length != 3) {
				System.out.println("ERR::Invalid Input::WRITE operation must only have 3 parameters");
			}
			else {
				variable = parts[1];
				writedata = parts[2];
				System.out.println("WRITE operation was successful");
			}		
		} else {
			System.out.println("ERR::Invalid Input::Supports only READ and WRITE operations.");
		}
		
		return;
	}

}
