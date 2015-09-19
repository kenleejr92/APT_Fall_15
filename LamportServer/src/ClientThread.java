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
				String line = "Increment";
				if(line!=null){
					StringTokenizer st = new StringTokenizer(line);
					String tag = st.nextToken();
					switch(tag){
					case "Increment":
						linker.Request();
						while(!linker.CanAccess()){
							Thread.yield();
						}
						Linker.x++;
						linker.Release();
						System.out.println(Linker.x);
						break;
					default:
						System.out.println("Message Error");
					}
				}
		}
	}
	
	public void start() {
		if(t==null){
			t = new Thread (this);
	        t.start ();
		}
	}
}
