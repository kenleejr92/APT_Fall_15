import java.net.*;
import java.io.*;
import java.util.*;


public class Server {

	/**
	 * @param args
	 */
	public static Integer serverID;
	public static String option;
	public static Integer numServers;
	public static ServerID[] otherServers;
	
	public static OtherServers getServers;
	public static Linker linker;
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("Incorrect Arguments");
			System.exit(0);
		}else{
			serverID = Integer.parseInt(args[1]);
			option = args[0];
		}
		try{
			if(option.equals("-i")){		//initialize 
				ConnectAllServers(option);
				linker.SetupListeningThreads();
				//linker.SetupClientThread();  //replace with linker.listen
				linker.Listen();
			}else if(option.equals("-r")){    //reconnect
				ConnectAllServers(option);
				linker.SetupListeningThreads();
				//linker.SetupClientThread();
				linker.Listen();
			}
			//linker.close();
		} catch(IOException e){
			System.out.println(e);
		} catch(IllegalArgumentException e){
			System.out.println("Must choose serverID that is less than number of servers");
		} 

	}
	
	static private void ConnectAllServers(String option) throws Exception{
			getServers = new OtherServers("servers.txt");
			otherServers = getServers.getServerIDs();
			numServers = getServers.getNumServers();
			if(serverID >= numServers) throw new IllegalArgumentException();
			linker = new Linker(otherServers,serverID,numServers,option);
	}
	
}




