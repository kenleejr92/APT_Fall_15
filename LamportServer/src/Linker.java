import java.util.*;
import java.util.concurrent.locks.*;
import java.io.*;
import java.net.*;
public class Linker {
    public static int myId, N;
    Connector connector;
    ServerSocket listener;
    ServerID[] otherServers;
    public static SafeChannelList activeChannels;
    
    public ThreadGroup serverListeners;
	public ThreadGroup clientListeners;
	public String option;
	public TimeQueue qR;
	public TimeQueue qW;
	public TimeQueue depClock;
	public static Integer x;
	public static Double Uptime;
	
    public Linker(ServerID[] s, Integer id, Integer numProc, String op) throws Exception {
    	depClock = new TimeQueue("depClock", numProc, id);
    	qR = new TimeQueue("reqQ", numProc, id);
    	qW = new TimeQueue("reqQ", numProc, id);
    	x=0;
    	myId = id;
    	N = numProc;
    	option = op;
        otherServers = s;
        activeChannels = new SafeChannelList(new ArrayList<Channel>(numProc));
        connector = new Connector();
        serverListeners = new ThreadGroup("Server Listeners");
		clientListeners = new ThreadGroup("Client Listeners");
        connector.Connect(s, myId, numProc, activeChannels, option);
    }

    public void SetupListeningThreads(){
    	ServerThread st;
    	Channel c;
    	for(int i=0; i<N; i++){
    		c = activeChannels.get(i);
    		if(c!=null){
	    		st = new ServerThread(c,this,serverListeners);
	    		st.start();
    		}
    	}
    }
    
    public void SetupClientThread(){
    	ClientThread ct = new ClientThread(this,clientListeners);
    	ct.start();
    }
    
    public synchronized void Rec_Ack(Integer ID, Double timestamp){
    	RecvUpdateClock(ID, timestamp);
    }
    
    public synchronized void Rec_Req(Integer ID, Double timestamp, Channel c) throws IOException{
    	RecvUpdateClock(ID, timestamp);
    	UpdateRequest(ID,timestamp);
    	Send_Ack(c, depClock.get(myId));
    }
    
    
    public synchronized void Rec_Rel(Integer ID, Double timestamp){
    	//System.out.println("After release from " + ID + " " + x);
    	RecvUpdateClock(ID,timestamp);
    	UpdateRequest(ID,Double.POSITIVE_INFINITY);
    }
    
    public synchronized void Rec_Up(Integer ID, Double timestamp, Channel c, Integer newX) throws IOException{
    	RecvUpdateClock(ID, timestamp);
    	x = newX;
    	Send_Ack(c, depClock.get(myId));
    }
    
    public void Send_Ack(Channel c, Double timestamp) throws IOException{
    	c.send("Ack "+ timestamp);
    	SendUpdateClock();
    }
    
    public synchronized void Send_Up() throws IOException{
    	Uptime = depClock.get(myId);
    	BroadCast("Up ");
    }
    
    public void RecvUpdateClock(Integer recID, Double recClock){
    	Double prev_myId = depClock.get(myId);
    	Double prev_recID = depClock.get(recID);
    	depClock.set(myId, max(prev_myId,recClock)+1);
    	depClock.set(recID, max(prev_recID,recClock));
    }
    
    public void SendUpdateClock(){
    	depClock.increment(myId);
    }
    
    public void UpdateRequest(Integer recID, Double value){
    	qR.set(recID, value);
    }
    
    public synchronized void Request()throws IOException{
    	Double req_ts = depClock.get(myId);
    	qR.set(myId, req_ts);
    	BroadCast("Req ");
    }
    
    public synchronized void Release() throws IOException{
    	qR.set(myId, Double.POSITIVE_INFINITY);
    	BroadCast("Rel ");
    }
    
    public void BroadCast(String msg) throws IOException{
    	for(int i = 0; i<N; i++){
    		Channel channel = activeChannels.get(i);
    		if(channel!=null){
    			if(msg.equals("Req ")){
    				channel.send(msg + qR.get(myId));
    				SendUpdateClock();
    			}else if(msg.equals("Rel ")){
    				channel.send(msg + depClock.get(myId));
    				SendUpdateClock();
    			}else if(msg.equals("Up ")){
    				channel.send(msg + Uptime + " " + x);
    			}
    			
    		}
    	}
    }
    
    public synchronized boolean CanAccess(){
    	boolean canAccess = false;
    	if((myId == qR.minIndex()) && (qR.get(myId) <= depClock.minElement())){
    		canAccess = true;
    	} else canAccess = false;
		return canAccess;
    }
    
    public synchronized boolean DoneUpdating(){
    	boolean doneUpdating = false;
    	if(Uptime <=  depClock.minElement()){
    		doneUpdating = true;
    	}else doneUpdating = false;
		return doneUpdating;
    }
    
    public void Listen(){
    	listener = Connector.listener;
    	ServerThread st;
		Socket newSocket;
		PrintWriter pw;
		BufferedReader br;
		Channel c;
		String line=null;
		boolean serverConnected;
		while(true){
			try{
				newSocket = listener.accept();
				newSocket.setSoTimeout(5000);
				pw = new PrintWriter(newSocket.getOutputStream());
				br = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
				c = new Channel(newSocket,br,pw);
				while(line==null){
					line = c.readLine();
				}
				System.out.println(line);
				StringTokenizer s = new StringTokenizer(line);
				if(s.nextToken().equals("ID")){
					Integer ID = Integer.parseInt(s.nextToken());
					c.setID(ID);
					activeChannels.add(ID,c);
					System.out.println("New Thread!");
					st = new ServerThread(c, this, serverListeners);
					st.start();
				}else{
					c.setID(null);
					//spawn client thread
				}
			}catch(IOException e){
				System.out.println(e);
			}
		}
    }
    public static Double max(Double a, Double b){
    	if(a>b) return a;
    	else if(a<b) return b;
    	else return a;
    }
    public static Integer Min(Double[] a){
    	Double min = a[0];
    	Integer minID = 0;
    	for(int i=0; i<a.length;i++){
    		if(a[i]<min){
    			min = a[i];
    			minID = i;
    		}
    	}
    	return minID;
    	
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return N; }
    public void close() {connector.closeSockets();}
}
