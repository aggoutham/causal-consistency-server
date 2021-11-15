package server;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReplicatesHandler {
	
	public static JSONArray queuedWrites = new JSONArray();
	private JSONObject allData;
	private JSONObject dataversions;
	
	public ReplicatesHandler(JSONObject ad, JSONObject dv) {
		this.allData = ad;
		this.dataversions = dv;
		
	}
	
	public int performReplication(String variable, String writedata, String otherDC, JSONObject dObj, int SenderClockValue) {
		
		int status = 1;
		
		if(dObj.length() == 0) {
			allData.put(variable, writedata);
			dataversions.put(variable, SenderClockValue);
			return 1;
		}
		
		Iterator<String> keys = dObj.keys();

		while(keys.hasNext()) {
		    String key = keys.next();
		    if (dObj.get(key) instanceof JSONObject) {
		    	
		        ////Checking dependency 
		    	if(!dataversions.has(key)) {
		    		System.out.println("ERR:: REPLICATION OF " + variable + " NOT POSSIBLE AT THIS MOMENT! Adding to queue!!!");
		    		System.out.println("REASON:: WAITING on KEY=" + key + " and VERSION=" + Integer.toString(dObj.getJSONObject(key).getInt("version")));
		    		status = 0;
		    		break;
		    	}
		    	
		    	else if(dataversions.getInt(key) < dObj.getJSONObject(key).getInt("version")) {
		    		System.out.println("ERR:: REPLICATION OF " + variable + " NOT POSSIBLE AT THIS MOMENT! Adding to queue!!!");
		    		System.out.println("REASON:: WAITING on KEY=" + key + " and VERSION=" + Integer.toString(dObj.getJSONObject(key).getInt("version")));
		    		status = 0;
		    		break;
		    	}
		    		    	
		    }
		}
		
		if(status==0) {
			//add write task to queue
			JSONObject tempObj = new JSONObject();
			tempObj.put("variable", variable);
			tempObj.put("writedata", writedata);
			tempObj.put("otherDC", otherDC);
			tempObj.put("dObj", dObj);
			tempObj.put("SenderClockValue", SenderClockValue);
			queuedWrites.put(tempObj);
		}
		else {
			allData.put(variable, writedata);
			dataversions.put(variable, SenderClockValue);
		}
		
		
		
		return status;
	}


	
	
	public int tryQueuedObjects() {
		JSONArray newqueue = new JSONArray();
		
		int status = 1;
		int len = queuedWrites.length();
		
		for(int i=0; i<len; i++) {
			
			
			String variable = queuedWrites.getJSONObject(i).getString("variable");
			String writedata = queuedWrites.getJSONObject(i).getString("writedata");
			String otherDC = queuedWrites.getJSONObject(i).getString("otherDC");
			JSONObject dObj = queuedWrites.getJSONObject(i).getJSONObject("dObj");
			int SenderClockValue = queuedWrites.getJSONObject(i).getInt("SenderClockValue");
			
			
			System.out.println("Trying to clean Replication queue for key :- " + variable);
			status = performReplication(variable,writedata,otherDC,dObj,SenderClockValue);
			if(status==1) {
				System.out.println("Successfully Replicated key :- " + variable + " now!");
			}
			else {
				newqueue.put(queuedWrites.getJSONObject(i));
			}
		}
		queuedWrites = newqueue;
		
		return status;
	}



}
