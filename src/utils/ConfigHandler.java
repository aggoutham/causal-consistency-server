package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/*This class is a ONE-STOP SOLUTION for reading configuration for the entire system.
 *First it looks for an argument sent while invoking main function in StartServer.java
 *
 *If the argument value is present, it looks for a file "config.properties" in that PATH.
 *If found, this file will DICTATE the properties of the entire system.
 *
 *Else, if there is an issue with this file, then by default we use "config.properties" in resources folder in our code base.
 * */
public class ConfigHandler {
	
	HashMap<String, String> result = new HashMap<>();
	InputStream inputStream;
 
	public HashMap<String, String> getPropValues(String configPath, String type) throws IOException {
		
		
		
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			
			if(configPath != null) {
				try {
				    prop.load(new FileInputStream(configPath));
				} catch (IOException e) {
				    e.printStackTrace();
				}
			}
			//ELSE use default config.properties in our code base.
			else {
				inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
				if (inputStream != null) {
					prop.load(inputStream);
					inputStream.close();
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}
			}
			
			if(type.equals("server")) {
				result.put("serverPort", prop.getProperty("serverPort"));
				result.put("serverIP", prop.getProperty("serverIP"));
				result.put("alldcs", prop.getProperty("alldcs"));
				result.put("authToken", prop.getProperty("authToken"));
			}
			else if(type.equals("client")) {
				result.put("clientId", prop.getProperty("clientId"));
				result.put("connectedDC", prop.getProperty("connectedDC"));
				result.put("authToken", prop.getProperty("authToken"));
			}
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} 
		
		return result;
	}

}
