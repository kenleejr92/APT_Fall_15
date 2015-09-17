import java.io.*;
import java.net.*;


public class ServerThread extends Thread  {
	private Thread t;
	private BufferedReader dataIn;
	//private Socket socket;
	private int listeningID;
	private boolean r = true;
	
	ServerThread(BufferedReader in, int id){
		dataIn = in;
		//socket = socket_in;
		listeningID = id;
	}
	
	public void run() {
		while(r){
			System.out.println("Runnin...");
			try {
				String line = dataIn.readLine();
				System.out.println(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				r = false;
				t.interrupt();
				//e.printStackTrace();
			}
		}
		t.interrupt();
	}
	
	public void start() {
		System.out.println("Starting thread server listening to: " + Integer.toString(listeningID));
		if(t==null){
			t = new Thread (this, Integer.toString(listeningID));
	        t.start ();
		}
	}
}
