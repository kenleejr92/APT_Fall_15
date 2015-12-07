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
	
	public static OtherServers getServers;
	public static Linker linker;
	
	public static void main(String[] args) throws Exception {
		if(args.length > 1 || args.length == 0){
			System.out.println("Incorrect Arguments");
			System.exit(0);
		}else{
			serverID = Integer.parseInt(args[0]);
		}
		try{
			ConnectAllServers();
			linker.SetupListeningThreads();
			linker.SetupClientThread();
			Thread.sleep(10000);
			//linker.close();
		} catch(IOException e){
			System.out.println(e);
		} catch(IllegalArgumentException e){
			System.out.println("Must choose serverID that is less than number of servers");
		} 

	}
	
	static private void ConnectAllServers() throws Exception{
			getServers = new OtherServers("servers.txt");
			otherServers = getServers.getServerIDs();
			numServers = getServers.getNumServers();
			if(serverID >= numServers) throw new IllegalArgumentException();
			linker = new Linker(otherServers,serverID,numServers);
	}
	
}




