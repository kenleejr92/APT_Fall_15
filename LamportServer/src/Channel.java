import java.net.*;
import java.io.*;
import java.util.*;

public class Channel {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Integer ID;
	
	public Channel(Socket s, BufferedReader br, PrintWriter bw){
		socket = s;
		in = br;
		out = bw;
	}
	
	public void close() throws IOException{
		socket.close();
		in.close();
		out.close();
	}
	
	public synchronized void send(String msg) throws IOException{
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
	
	public void setID(Integer id){
		ID = id;
	}
	
	public Socket getSocket(){
		return socket;
	}
	
}
