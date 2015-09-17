import java.util.*;
import java.io.*;
public class Linker {
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    //BufferedReader dIn;
    ServerID[] otherServers;
    int myId, N;
    Connector connector;
    public Linker(ServerID[] s, Integer id, Integer numProc) throws Exception {
        myId = id;
        N = numProc;
        otherServers = s;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        connector = new Connector();
        connector.Connect(s, myId, numProc, dataIn, dataOut);
    }
    public void sendMsg(int destId, String tag, String msg) {     
        dataOut[destId].println(myId + " " + destId + " " + 
				      tag + " " + msg + "#");
        dataOut[destId].flush();
    }
    public void sendMsg(int destId, String tag) {
        sendMsg(destId, tag, " 0 ");
    }
    public void multicast(String tag, String msg){
        for (int i=0; i<N; i++) {
        	if(i!=myId){
        		sendMsg(i, tag, msg);
        	}
        }
    }
    public void receiveMsg(int fromId) throws IOException  {        
        String getline = dataIn[fromId].readLine();
        System.out.println(" received message " + getline);     
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return N; }
    public void close() {connector.closeSockets();}
}
