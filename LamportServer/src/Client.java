import java.io.IOException;
import java.net.*;

public class Client{
	
	public static void main(String[] args) {
		OtherServers o;
		o = new OtherServers("servers.txt");
		ServerID[] listOfServers= o.getServerIDs();
		//System.out.println(listOfServers[1].portNum);
		String command;
		String name;
		int count=0;
		//System.out.println(args.length);
		if(args.length==3){
			command = args[0];
			name = args[1];
			count = Integer.parseInt(args[2]);
			if(count == 0)throw new java.lang.IllegalArgumentException("Count cannot be 0");
		}
		else if(args.length==2){
			command = args[0];
			name = args[1];
		}
		else{
			throw new java.lang.IllegalArgumentException("Improve argument format");
		}
		System.out.println(command);
		System.out.println(name);
		System.out.println(count);
		Socket toServer;
		try {
			toServer = new Socket(listOfServers[3].IP,listOfServers[3].portNum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}