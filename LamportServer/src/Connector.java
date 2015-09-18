import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class Connector {
	public static ServerSocket listener;
	public int N;
	public void Connect(ServerID[] IDtable, int myId, Integer numProc,
			    List<Channel> activeList) throws Exception {
	        listener = new ServerSocket(IDtable[myId].portNum, 0, IDtable[myId].IP);
	        N = numProc;
	        
	        /* accept connections from all the smaller processes */
	        for (int i = 0; i < myId; i++) {
	            Socket s = listener.accept(); 
	            //s.setSoTimeout(5000);
	            BufferedReader dIn = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
                PrintWriter dOut = new PrintWriter(s.getOutputStream());
                Channel c = new Channel(s,dIn,dOut,i,myId);
                activeList.add(i,c);
	        }
	        activeList.add(myId,null);
	        /* contact all the bigger processes */
	        for (int i = myId + 1; i < numProc; i++) {
	            Socket s = new Socket(IDtable[i].IP, IDtable[i].portNum);
	            //s.setSoTimeout(5000);
	            BufferedReader dIn = new BufferedReader(new
	            InputStreamReader(s.getInputStream()));
	            PrintWriter dOut = new PrintWriter(s.getOutputStream());
	            Channel c = new Channel(s,dIn,dOut,i,myId);
	            activeList.add(i,c);
	        }
	    }
	
	
    public void closeSockets(){
        try {
            listener.close();
            for (int i=	0;i<N; i++) {
            	
            }
        } catch (Exception e) {
        	System.out.println("Here");
        	System.err.println(e);
        }
    }

}
