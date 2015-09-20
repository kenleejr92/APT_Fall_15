import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ClientThread extends Thread {
	private Thread t;
	private Channel channel;
	private boolean timeout;
	private Linker linker;
	
	ClientThread(Channel c, Linker link, ThreadGroup tg){
		super(tg, "Thread x");
		channel = c;
		timeout = false;
		linker = link;
	}
	ClientThread(Linker link, ThreadGroup tg){
		super(tg, "Thread y");
		timeout = false;
		linker = link;
	}
	
	public void run() {
		while(true){
//			try{
//				String line;
//				System.out.println(">> ");
//				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//				line = in.readLine();
				
				//String line = "Increment";
				String line = "reserve shreyas,rao 1";
				
				
				if(line!=null){
					StringTokenizer st = new StringTokenizer(line);
					String tag = st.nextToken();
					String name = st.nextToken();
					String seats = Integer.toString(0);
					if(st.hasMoreTokens()){
						seats = st.nextToken();
					}
					switch(tag){
					case "Increment":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						Linker.x++; //Critical section
						linker.Send_Up();
						while(!linker.DoneUpdating()){
							Thread.yield();
						}
						linker.Uptime = Double.POSITIVE_INFINITY;
						System.out.println("After increment: " + Linker.x);
						linker.Release();
						break;
					case "reserve":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						Linker.reserveSeats(name, seats);
						//Linker.seatsLeft--; //Critical section
						linker.Send_Up();
						while(!linker.DoneUpdating()){
							Thread.yield();
						}
						linker.Uptime = Double.POSITIVE_INFINITY;
						linker.Release();
						break;
					default:
						System.out.println("Message Error");
					}
				}
//			}catch(IOException e){
//				System.out.println(e);
//			}
		}
	}
	
	public void start() {
		if(t==null){
			t = new Thread (this);
	        t.start ();
		}
	}
}
