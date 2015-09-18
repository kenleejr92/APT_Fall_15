import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;



public class ServerThread extends Thread  {
	private Thread t;
	private Channel channel;
	private boolean timeout;
	private Linker linker;
	
	ServerThread(Channel c, Linker link, ThreadGroup tg){
		super(tg, "Thread x");
		channel = c;
		timeout = false;
		linker = link;
	}
	
	public void run() {
		while(!timeout){
			try {
				String line = channel.readLine();
				if(line!=null){
					//System.out.println(line);
					StringTokenizer st = new StringTokenizer(line);
					String tag = st.nextToken();
					Double timestamp = Double.parseDouble(st.nextToken());
					linker.RecvUpdateClock(channel.getChannelID(), timestamp);
					switch(tag){
					case "Ack":
						System.out.println("Ack from Server " + channel.getChannelID() + " at: " + timestamp);
						break;
					case "Req":
						linker.UpdateRequest(channel.getChannelID(),timestamp);
						channel.send("Ack "+ linker.GetMyTimestamp(Linker.myId));
						break;
					case "Rel":
						linker.UpdateRequest(channel.getChannelID(),Double.POSITIVE_INFINITY);
						channel.send("Ack "+ linker.GetMyTimestamp(Linker.myId));
						break;
					default:
						System.out.println("Message Error");
					}
				}
			} catch (IOException e) {
				try{
					channel.close();
					//activeList.remove(channel);
					timeout = true;
					System.out.println("Timeout from " + channel.getChannelID());
				} catch(IOException d){
					System.out.println(d);
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
