package message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*This class is the lowest level ONE-STOP solution for sending requests.
 *Every other utility trying to make communication with the outside world would use this class.
 *
 *The sendReq method simply takes a destination socket, a request message byte array as input.
 *Once it receives a response from a Data Center, it returns the String back.
 * */

public class SendMessage {
	
	//Utility to send requests and receive responses used throughout the project.
	public String sendReq(Socket socket, byte[] message){
		String response = "FAILURE";
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			byte[] resp = null;
			resp = (byte[]) in.readObject();
			String str = new String(resp, StandardCharsets.UTF_8);
			return str;
		} 
		catch (Exception e) {
			System.out.println(e);
		}
		return response;
	}

}
