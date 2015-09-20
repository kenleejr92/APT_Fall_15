import java.io.IOException;
import java.net.*;

public class Client{
	
	public static void main(String[] args) {
		OtherServers o;
		o = new OtherServers("servers.txt");
		ServerID[] listOfServers= o.getServerIDs();
		
	}
}