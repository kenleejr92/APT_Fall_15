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
			try{
				String line;
				System.out.print(">> ");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				line = in.readLine();
				//String line = "Increment";
				//Thread.sleep(500);
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
						//////////////////////////////////////////////////////////
						Linker.x++; //Critical section
						
						linker.Send_Up();
						while(!linker.DoneUpdating()){
							Thread.yield();
						}
						Linker.Uptime = Double.POSITIVE_INFINITY;
						////////////////////////////////////////////////////////////
						System.out.println("After increment: " + Linker.x);
						linker.Release();
						break;
					case "reserve":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						Linker.reserveSeats(name, seats);
						linker.Send_Up();
						while(!linker.DoneUpdating()){
							Thread.yield();
						}
						linker.Uptime = Double.POSITIVE_INFINITY;
						linker.Release();
						break;
					case "search":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						linker.search(name);
						//Ken: do we still need to send update for search?
						linker.Send_Up();
						while(!linker.DoneUpdating()){
							Thread.yield();
						}
						linker.Uptime = Double.POSITIVE_INFINITY;
						linker.Release();
						break;
					case "delete":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						linker.delete(name);
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
			} catch(IOException e){
				System.out.println("Exception in Client Thread");
			} 
//			catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
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
