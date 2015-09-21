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
				//System.out.println(line);
				//System.out.println(line + " from Server " + channel.getChannelID());
				if(line!=null){
					Integer newX = 0;
					Double timestamp = 0.0;
					Integer channelId;
					StringTokenizer st = new StringTokenizer(line);
					String tag = st.nextToken();
					switch(tag){
					case "Ack":
						timestamp = Double.parseDouble(st.nextToken());
						linker.Rec_Ack(channel.getChannelID(), timestamp);
						break;
					case "Req":
						timestamp = Double.parseDouble(st.nextToken());
						linker.Rec_Req(channel.getChannelID(), timestamp, channel);
						break;
					case "Rel":
						timestamp = Double.parseDouble(st.nextToken());
						linker.Rec_Rel(channel.getChannelID(), timestamp);
						break;
					case "Up":
						timestamp = Double.parseDouble(st.nextToken());
						newX = Integer.parseInt(st.nextToken());
						//new String updatedSeating[] = null; 
						String updatedSeating[] = new String[linker.seating.length];
						for(int i=0; i<linker.seating.length;i++){
							updatedSeating[i] = st.nextToken();
						}
						//st.
						linker.Rec_Up(channel.getChannelID(), timestamp, channel, newX, updatedSeating);
						break;
					case "ID":
						channelId = Integer.parseInt(st.nextToken());
						channel.setID(channelId);
						break;
					default:
						System.out.println(line);
					}
				}else{
					timeout = true;
					Linker.activeChannels.remove(channel.getChannelID());
					linker.qR.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
					linker.depClock.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
				}
			} catch (SocketTimeoutException e) {
				timeout = true;
				//channel.close();
				Linker.activeChannels.remove(channel.getChannelID());
				linker.qR.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
				linker.depClock.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
				System.out.println(e);
				System.out.println("Timeout from " + channel.getChannelID());
			} catch(IOException e){
				timeout = true;
				//channel.close();
				Linker.activeChannels.remove(channel.getChannelID());
				linker.qR.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
				linker.depClock.set(channel.getChannelID(), Double.POSITIVE_INFINITY);
				System.out.println(e);
				System.out.println("Timeout from " + channel.getChannelID());
			}
		}
	}
	
	public void start() {
		if(t==null){
			try {
				channel.send("ID "+ Linker.myId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			t = new Thread (this);
	        t.start ();
		}
	}
}
