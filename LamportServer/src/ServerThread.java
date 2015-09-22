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
						linker.Rec_Ack(channel.getChannelID(), timestamp);
						break;
					case "Req":
						linker.Rec_Req(channel.getChannelID(), timestamp, channel);
						break;
					case "Rel":
						linker.Rec_Rel(channel.getChannelID(), timestamp);
						break;
					case "Up":
						newX = Integer.parseInt(st.nextToken());
						//new String updatedSeating[] = null; 
						String updatedSeating[] = new String[linker.seating.length];
						for(int i=0; i<linker.seating.length;i++){
							updatedSeating[i] = st.nextToken();
						}
						//st.
						linker.Rec_Up(channel.getChannelID(), timestamp, channel, newX, updatedSeating);
						break;
					default:
						System.out.println(line);
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
