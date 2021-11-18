package server;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import clientCli.StartCli;
import utils.ConfigHandler;

/*
 * This class starts the Data Center Server. It reads the relevant configurations first 
 * and spawns the listener thread.
 * 
 * It also maintains three static data structures namely allData, dependencyObj and dataVersions 
 * that are highly useful for maintaining causal consistency.
 */
public class StartServer {

	private int serverPort;
	private JSONArray otherdcs;
	private static JSONObject allData = new JSONObject();
	private static JSONObject dependencyObj = new JSONObject();
	private static JSONObject dataVersions = new JSONObject();
	private static int lamportClock = 0;
	
	public static void main(String[] args)  {
		
		StartServer ss = new StartServer();
		String configPath = args[0];
		int type = Integer.parseInt(args[1]);
		
		//Read configuration file
		ConfigHandler ch = new ConfigHandler();
		HashMap <String,String> configMap = new HashMap <String,String> ();
		try {
			configMap = ch.getPropValues(configPath,"server");
		} catch (IOException e) {
			System.out.println("Unable to load Config Values");
			e.printStackTrace();
		}
		
		//Print all configurations
	    System.out.println("#####################################################");
	    System.out.println("Starting Server with these configurations");
		for (String key: configMap.keySet()) {
		    String value = configMap.get(key);
		    System.out.println(key + "=" + value);
		}
	    System.out.println("#####################################################");
		System.out.println("Argument Passed :- " + Integer.toString(type));
		
		
		ss.otherdcs = new JSONArray();
		String owndc = configMap.get("serverIP") + ":" + configMap.get("serverPort");
		String[] parts = configMap.get("alldcs").split(",");
		for(int i=0; i<parts.length; i++) {
			if(!parts[i].equals(owndc)) {
				ss.otherdcs.put(parts[i]);
			}
		}
		
		
		//Spawn the Listener thread
		ss.serverPort = Integer.parseInt(configMap.get("serverPort"));
		ServerListener sl = new ServerListener(ss.serverPort, configMap, allData, dependencyObj, lamportClock, dataVersions);
		sl.start();
		
		
		return;
	}

}
