import java.net.*;
import java.io.*;
import java.util.*;

public class Connector {
	public static ServerSocket listener;
	public Socket[] link;
	public int thisServer;
	public void Connect(ServerID[] s, int myId, int numProc,
			    BufferedReader[] dataIn, PrintWriter[] dataOut) throws Exception {
			thisServer = myId;
	        link = new Socket[numProc];
	        int localport = s[myId].portNum;
	        listener = new ServerSocket(localport, 0, s[myId].IP);
	        
	        
	        /* accept connections from all the smaller processes */
	        for (int i = 0; i < myId; i++) {
	            Socket a = listener.accept(); 
	            a.setSoTimeout(5000);
	            BufferedReader dIn = new BufferedReader(
                new InputStreamReader(a.getInputStream()));
                link[i] = a;
                dataIn[i] = dIn;
                dataOut[i] = new PrintWriter(a.getOutputStream());
	        }
	        /* contact all the bigger processes */
	        for (int i = myId + 1; i < numProc; i++) {
	            link[i] = new Socket(s[i].IP, s[i].portNum);
	            link[i].setSoTimeout(5000);
	            dataOut[i] = new PrintWriter(link[i].getOutputStream());
	            dataIn[i] = new BufferedReader(new
	            InputStreamReader(link[i].getInputStream()));
	        }
	    }
    public void closeSockets(){
        try {
            listener.close();
            for (int i=	0;i<link.length; i++) {
            	if(i!=thisServer){
            		link[i].close();
            	}
            }
        } catch (Exception e) {
        	System.out.println("Here");
        	System.err.println(e);
        }
    }
}
