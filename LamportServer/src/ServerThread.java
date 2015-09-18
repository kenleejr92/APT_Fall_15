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
					Integer newX = 0;
					Double timestamp = 0.0;
					StringTokenizer st = new StringTokenizer(line);
					String tag = st.nextToken();
					timestamp = Double.parseDouble(st.nextToken());
					switch(tag){
					case "Ack":
						linker.RecvUpdateClock(channel.getChannelID(), timestamp);
						linker.clockLock.lock();
						System.out.println("Ack from Server " + channel.getChannelID() + " at: " + linker.GetMyTimestamp(Linker.myId));
						linker.clockLock.unlock();
						break;
					case "Req":
						linker.RecvUpdateClock(channel.getChannelID(), timestamp);
						linker.UpdateRequest(channel.getChannelID(),timestamp);
						linker.clockLock.lock();
						channel.send("Ack "+ linker.GetMyTimestamp(Linker.myId));
						linker.clockLock.lock();
						break;
					case "Rel":
						linker.RecvUpdateClock(channel.getChannelID(), timestamp);
						linker.UpdateRequest(channel.getChannelID(),Double.POSITIVE_INFINITY);
						linker.clockLock.lock();
						channel.send("Ack "+ linker.GetMyTimestamp(Linker.myId));
						linker.clockLock.unlock();
						linker.SendUpdateClock();
						break;
					case "Up":
						//linker.RecvUpdateClock(channel.getChannelID(),timestamp);
						linker.x = newX;
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
