import java.net.*;
import java.io.*;
import java.util.*;


public class Server {

	/**
	 * @param args
	 */
	public static Integer serverID;
	public static Integer numServers;
	public static ServerID[] otherServers;
	public static OtherServers o;
	public static Linker l;
	
	public static void main(String[] args) throws Exception {
		if(args.length > 1 || args.length == 0){
			System.out.println("Incorrect Arguments");
			System.exit(0);
		}else{
			serverID = Integer.parseInt(args[0]);
		}
		try{
			o = new OtherServers("servers.txt");
			otherServers = o.getServerIDs();
			numServers = o.getNumServers();
			l = new Linker(otherServers,serverID,numServers);
			
			for(int i=0; i<numServers; i++){
				int s = serverID;
				if(i!=s){
					//l.receiveMsg(i);
					ServerThread nt = new ServerThread(l.dataIn[i],i);
					nt.start();
					//nt.run();
				}
			}
			l.multicast("Hello", "MultiCast");
			Thread.sleep(5000);
			l.close();
		} catch(IOException e){
			System.out.println(e);
		}

	}

}




