import java.util.*;
import java.util.concurrent.locks.*;
import java.io.*;
import java.net.*;
public class Linker {
    public static int myId, N;
    Connector connector;
    ServerSocket listener;
    ServerID[] otherServers;
    public static List<Channel> activeChannels;
    
    public ThreadGroup serverListeners;
	public ThreadGroup clientListeners;
	
	public Lock requestLock;
	public Lock clockLock;
	private Double[] qR;
	private Double[] qW;
	public Double[] depClock;
	public static  Integer x;
	
    public Linker(ServerID[] s, Integer id, Integer numProc) throws Exception {
    	depClock = new Double[numProc];
    	qR = new Double[numProc];
    	qW = new Double[numProc];
    	x=0;
    	myId = id;
    	N = numProc;
    	for(int i=0;i<N;i++){
    		if(i==myId) depClock[i]=1.0;
    		else depClock[i]=0.0;
    		qR[i]=Double.POSITIVE_INFINITY;
    		qW[i]=Double.POSITIVE_INFINITY;
    	}
    	requestLock = new ReentrantLock();
        clockLock = new ReentrantLock();
        otherServers = s;
        activeChannels = new ArrayList<>(numProc);
        connector = new Connector();
        serverListeners = new ThreadGroup("Server Listeners");
		clientListeners = new ThreadGroup("Client Listeners");
        connector.Connect(s, myId, numProc, activeChannels);
    }

    public void SetupListeningThreads(){
    	ServerThread st;
    	Iterator<Channel> i = activeChannels.iterator();
    	while(i.hasNext()){
    		Channel c = i.next();
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
    public void RecvUpdateClock(Integer recID, Double recClock){
    	clockLock.lock();
    	depClock[myId] = max(depClock[myId],recClock)+1;
    	depClock[recID] = max(depClock[recID],recClock);
    	clockLock.unlock();
    }
    
    public void SendUpdateClock(){
    	clockLock.lock();
    	depClock[myId]++;
    	clockLock.unlock();
    }
    
    public void UpdateRequest(Integer recID, Double value){
    	requestLock.lock();
    	qR[recID] = value;
    	requestLock.unlock();
    }
    
    public void Request(){
    	requestLock.lock();
    	clockLock.lock();
    	qR[myId] = depClock[myId];
    	//System.out.println("Requesting at: " + depClock[myId]);
    	clockLock.unlock();
    	requestLock.unlock();
    	
    	BroadCast("Req ");
    }
    
    public void Release(){
    	requestLock.lock();
    	clockLock.lock();
    	qR[myId] = Double.POSITIVE_INFINITY;
    	//System.out.println("Releasing at: " + depClock[myId]);
    	clockLock.unlock();
    	requestLock.unlock();
    	BroadCast("Rel ");
    }
    
    public void Update(){
    	BroadCast("Up ");
    	//update x
    }
    
    public Double GetMyTimestamp(Integer myId){
    	return depClock[myId];
    }
    
    
    public void BroadCast(String msg){
    	Iterator<Channel> i= activeChannels.iterator();
    	while(i.hasNext()){
    		Channel c = i.next();
    		if(c!=null){
    			if(msg.equals("Req ")){
    				requestLock.lock();
    				c.send(msg + qR[myId]);
    				requestLock.unlock();
    			}else if(msg.equals("Rel ")){
    				clockLock.lock();
    				c.send(msg + depClock[myId] + " " + x);
    				clockLock.unlock();
    			}else if(msg.equals("Up ")){
    				c.send(msg + x);
    			}
    			SendUpdateClock();
    		}
    	}
    }
    
    public boolean CanAccess(){
    	boolean canAccess = false;
    	requestLock.lock();
    	clockLock.lock();
    	if((myId == Min(qR)) && (myId == Min(depClock))){
    		canAccess = true;
    	} else canAccess = false;
    	clockLock.unlock();
    	requestLock.unlock();
		return canAccess;
    }
    
    public void Listen(){
    	listener = Connector.listener;
		Socket newSocket;
		Integer portNum;
		InetAddress IP;
		boolean serverConnected;
		while(true){
			try{
				serverConnected = false;
				newSocket = listener.accept();
				portNum = newSocket.getPort();
				IP = newSocket.getInetAddress();
				ServerID sid = new ServerID(IP, portNum);
				for(int i=0; i<otherServers.length; i++){
					if(otherServers[i].equals(sid)){
						//spawn server thread;
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
