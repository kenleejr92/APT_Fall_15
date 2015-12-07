import java.io.*;
import java.net.InetAddress;
import java.util.*;

public class OtherServers {
	private ServerID[] servers;
	private Integer numServers;
	
	public OtherServers(String filename){
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			int i=0;
			numServers = Integer.parseInt(in.readLine());
			servers = new ServerID[numServers];
			while(true)
			{
				String line = in.readLine();
				if(line == null){
					in.close();
					break;
				}
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens()){
					InetAddress ip = InetAddress.getByName(st.nextToken());
					Integer port = Integer.parseInt(st.nextToken());
					servers[i++]=new ServerID(ip, port);
				}
			}
		}catch(FileNotFoundException e)
		{
			System.out.println(e);
		}catch(IOException e)
		{
			System.out.println(e);
		}
	}
	
	public ServerID[] getServerIDs(){
		return servers;
	}
	public Integer getNumServers(){
		return numServers;
	}
}
