package writeUtil;

import java.net.Socket;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import message.SendMessage;

/*
 * This class is the lowest level thread implementation that serves replication request to ONE other DC.
 */
public class IndividualThread extends Thread {

	private HashMap <String,String> configMap;
	private String otherDC;
	private String delay;
	private String variable;
	private String writedata;
	private String cid;
	private JSONObject dependencyObj;
	private String ownDC;
	private int lamportClock;
	
	public IndividualThread(String otherDC, String delay, String v, String w, String c, JSONObject dObj, String ownDC, int l, HashMap <String,String> cm){
		this.otherDC = otherDC;
		this.delay = delay;
		this.variable = v;
		this.writedata = w;
		this.cid = c;
		this.dependencyObj = dObj;
		this.ownDC = ownDC;
		this.lamportClock = l;
		this.configMap = cm;
	}
	
	@Override
	public void run() {
		
		if(otherDC.equals(ownDC)) {
			return;
		}
		try {
			long tId = Thread.currentThread().getId();
			
			String sIP = otherDC.split(":")[0];
			int sPort = Integer.parseInt(otherDC.split(":")[1]);
			
			JSONObject reqObj = new JSONObject();
			reqObj.put("Authorization",configMap.get("authToken"));
			reqObj.put("Operation", "REPLICATE");
			reqObj.put("otherdcID", ownDC);
			reqObj.put("variable", variable);
			reqObj.put("writedata", writedata);
			reqObj.put("SenderClockValue", lamportClock);
			JSONObject dObj = new JSONObject();

			
			
			if(dependencyObj.has(cid)) {
				dObj = dependencyObj.getJSONObject(cid);
			}
			reqObj.put("dependency", dObj);
			
//			System.out.println(reqObj.toString());
//			System.out.println(dependencyObj.toString());
//			System.out.println(cid);
//			System.out.println("Replicating to :- " + otherDC);
			Thread.sleep(Integer.parseInt(delay)*1000);
			

			Socket ods = new Socket(sIP,sPort);
			SendMessage sm = new SendMessage();
			String messageStr = reqObj.toString();
			byte[] message = messageStr.getBytes();
	    	String response = sm.sendReq(ods, message);
			
			System.out.println("Replicated to :- " + otherDC);
		} catch (Exception e) {
			System.out.println(e);
		}
		return;
	}

}
