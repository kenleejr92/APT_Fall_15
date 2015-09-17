import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	/**
	 * @param args
	 */
	public static InetAddress loopBackAddress;
	private InetAddress serverIP;
	private int serverPort;
	public static ServerSocket a;
	public static Socket b,c;
	public static OtherServers o;
	
	public static void main(String[] args) {
		try{
			loopBackAddress = InetAddress.getLoopbackAddress();
			a = new ServerSocket(1600, 0, loopBackAddress);
			b = new Socket(loopBackAddress, 1600);
			Socket client = a.accept();
			PrintWriter out = new PrintWriter(b.getOutputStream(), true);
			out.println("Hello server");
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			System.out.println(in.readLine());
			a.close();
			b.close();
			client.close();
			o = new OtherServers("servers.txt");
			System.exit(0);
		} catch(UnknownHostException e){
			System.err.println(e);
		
		} catch (IOException e) {
			System.err.println(e);
		}

	}

}




