package client;

import java.io.IOException;
import java.util.HashMap;

import clientCli.StartCli;
import utils.ConfigHandler;

/*
 * This class is to begin a CLient server in the distributed replication system.
 * It checks for the right configurations and starts an interactive CLI thread.
 */
public class StartClient {
	
	
	public static void main(String[] args)  {
		
		StartClient sc = new StartClient();
		String configPath = args[0];
		int type = Integer.parseInt(args[1]);
		
		//Read configuration file
		ConfigHandler ch = new ConfigHandler();
		HashMap <String,String> configMap = new HashMap <String,String> ();
		try {
			configMap = ch.getPropValues(configPath,"client");
		} catch (IOException e) {
			System.out.println("Unable to load Config Values");
			e.printStackTrace();
		}
		
		//Print all configurations
	    System.out.println("#####################################################");
	    System.out.println("Starting Client with these configurations");
		for (String key: configMap.keySet()) {
		    String value = configMap.get(key);
		    System.out.println(key + "=" + value);
		}
	    System.out.println("#####################################################");
		System.out.println("Argument Passed :- " + Integer.toString(type));
		
		

		//Spawn the interactive shell thread
		StartCli cli = new StartCli(configMap);
		cli.enableInputs();
	}

}
