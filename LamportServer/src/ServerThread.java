import java.io.*;
import java.net.*;


public class ServerThread extends Thread  {
	private Thread t;
	private BufferedReader dataIn;
	private PrintWriter dataOut;
	private Socket socket;
	private int myId;
	private int listeningID;
	private boolean timeout;
	
	ServerThread(BufferedReader in, PrintWriter out, int lId, int mId, ThreadGroup tg){
		super(tg, "Thread x");
		dataIn = in;
		dataOut = out;
		listeningID = lId;
		myId = mId;
		timeout = false;
	}
	
	public void run() {
		while(!timeout){
			try {
				dataOut.println("Hello From " + Integer.toString(myId));
				dataOut.flush();
				String line = dataIn.readLine();
				System.out.println(line);
				
			} catch (IOException e) {
				timeout = true;
				System.out.println("Timeout");
			} 
		}
	}
	
	public void start() {
		System.out.println("Server " + Integer.toString(myId)+ " listening to " + Integer.toString(listeningID));
		if(t==null){
			t = new Thread (this, Integer.toString(listeningID));
	        t.start ();
		}
	}
}
