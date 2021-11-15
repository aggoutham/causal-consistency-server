package writeUtil;

import org.json.JSONObject;

public class IndividualThread extends Thread {
	
	
	private String otherDC;
	private String delay;
	private String variable;
	private String writedata;
	private String cid;
	private JSONObject dependencyObj;
	private String ownDC;
	public IndividualThread(String otherDC, String delay, String v, String w, String c, JSONObject dObj, String ownDC){
		this.otherDC = otherDC;
		this.delay = delay;
		this.variable = v;
		this.writedata = w;
		this.cid = c;
		this.dependencyObj = dObj;
		this.ownDC = ownDC;
	}
	
	@Override
	public void run() {
		
		if(otherDC.equals(ownDC)) {
			return;
		}
		
		try {
			long tId = Thread.currentThread().getId();
			Thread.sleep(10000);
			System.out.println("Replicated to :- " + otherDC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

}
