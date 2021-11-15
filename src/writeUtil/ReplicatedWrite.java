package writeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

public class ReplicatedWrite extends Thread {
	
	private HashMap <String,String> configMap;
	private String cid;
	private String variable;
	private String writedata;
	private JSONObject dependencyObj;
	private static ArrayList<Thread> activeThreads = new ArrayList<Thread> ();
	private String ownDC;
	private int lamportClock;
	
	public ReplicatedWrite(HashMap <String,String> cm, String c, String v, String w, JSONObject dObj, int l){
		this.configMap = cm;
		this.cid = c;
		this.variable = v;
		this.writedata = w;
		this.dependencyObj= dObj;
		this.ownDC = configMap.get("serverIP") + ":" + configMap.get("serverPort");
		this.lamportClock = l;
	}
	
	@Override
	public void run() {
		String[] alldcs = configMap.get("alldcs").split(",");
		String[] respectiveDelays = configMap.get("respectiveDelays").split(",");
		
		int total = alldcs.length;
		for(int i=0; i<total; i++) {
            Thread object = new Thread(new IndividualThread(alldcs[i],respectiveDelays[i],variable,writedata,cid,dependencyObj,ownDC,lamportClock,configMap));
            activeThreads.add(object);
            object.start();
		}
		for(int a=0; a<activeThreads.size();a++) {
			try {
				activeThreads.get(a).join();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		return;
	}

}
