import java.util.*;
import java.io.*;
import java.net.*;
public class Linker {
    PrintWriter[] dataOut;   //Output stream for each server
    BufferedReader[] dataIn; //Input stream for each server
    ServerID[] otherServers; //IP and port# for each server
    Socket[] link; 			 //Socket for each server
    int myId, N;
    Connector connector;
    ServerSocket listener;
    public ThreadGroup serverListeners;
	public ThreadGroup clientListeners;
    public Linker(ServerID[] s, Integer id, Integer numProc) throws Exception {
        myId = id;
        N = numProc;
        otherServers = s;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        connector = new Connector();
        link = connector.link;
        serverListeners = new ThreadGroup("Server Listeners");
		clientListeners = new ThreadGroup("Client Listeners");
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
    
    public void SetupListeningThreads(){
    	ServerThread st;
    	for(int i=0; i<N; i++){
			if(i!=myId){
				st = new ServerThread(dataIn[i],dataOut[i],i,myId,serverListeners);
				st.start();
			}
		}
    }
    
    public void Listen(){
    	listener = Connector.listener;
		Socket newSocket;
		Integer portNum;
		InetAddress IP;
		boolean serverConnected;
		PrintWriter pw;
		BufferedReader br;
		ServerThread st;
		while(true){
			try{
				serverConnected = false;
				newSocket = listener.accept();
				portNum = newSocket.getPort();
				IP = newSocket.getInetAddress();
				ServerID sid = new ServerID(IP, portNum);
				for(int i=0; i<otherServers.length; i++){
					if(otherServers[i].equals(sid)){
						pw = new PrintWriter(newSocket.getOutputStream());
						br = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
						st = new ServerThread(br, pw, i, myId, serverListeners);
						st.start();
						serverConnected = true;
						break;
					}
				}
				if(!serverConnected){
					//spawn client thread
				}
				
			}catch(IOException e){
				System.out.println(e);
			}
		}
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return N; }
    public void close() {connector.closeSockets();}
}
