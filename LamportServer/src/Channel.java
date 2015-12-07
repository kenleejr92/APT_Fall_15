import java.net.*;
import java.io.*;
import java.util.*;

public class Channel {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Integer ID;
	private Integer myID;
	
	public Channel(Socket s, BufferedReader br, PrintWriter bw, Integer id, Integer myId){
		socket = s;
		in = br;
		out = bw;
		ID = id;
		myID = myId;
	}
	
	public void close() throws IOException{
		socket.close();
		in.close();
		out.close();
	}
	
	public synchronized void send(String msg){
		out.println(msg);
		out.flush();
	}
	
	public String readLine() throws IOException{ 
		return in.readLine();
	}
	
	public void flush(){
		out.flush();
	}

	public Integer getChannelID() {
		return ID;
	}
	
	public Integer getMyID(){
		return myID;
	}
	
}
