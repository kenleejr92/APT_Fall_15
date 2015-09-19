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

	public TimeQueue qR;
	public TimeQueue depClock;
	public static Integer x;
	public static Integer seatsLeft = 20;
	public static Double Uptime;
	public static String seating[] = null;
	
    public Linker(ServerID[] s, Integer id, Integer numProc) throws Exception {
    	depClock = new TimeQueue("depClock", numProc, id);
    	qR = new TimeQueue("reqQ", numProc, id);
 
    	x=0;
    	myId = id;
    	N = numProc;
  
    	seating = new String[20];
    	for(int i=0;i<seating.length;i++){
    		seating[i] = "empty";
    	}
    	
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
    
    public synchronized void Rec_Ack(Integer ID, Double timestamp){
    	RecvUpdateClock(ID, timestamp);
    }
    
    public synchronized void Rec_Req(Integer ID, Double timestamp, Channel c){
    	RecvUpdateClock(ID, timestamp);
    	UpdateRequest(ID,timestamp);
    	Send_Ack(c, depClock.get(myId));
    }
    
    
    public synchronized void Rec_Rel(Integer ID, Double timestamp){
    	//System.out.println("After release from " + ID + " " + x);
    	RecvUpdateClock(ID,timestamp);
    	UpdateRequest(ID,Double.POSITIVE_INFINITY);
    }
    
    public synchronized void Rec_Up(Integer ID, Double timestamp, Channel c, Integer newSeatsLeft){
    	RecvUpdateClock(ID, timestamp);
    	//x = newX;
    	seatsLeft = newSeatsLeft;
    	
    	Send_Ack(c, depClock.get(myId));
    }
    
    public void Send_Ack(Channel c, Double timestamp){
    	c.send("Ack "+ timestamp);
    	SendUpdateClock();
    }
    
    public synchronized void Send_Up(){
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
    
    public synchronized void Request(){
    	Double req_ts = depClock.get(myId);
    	qR.set(myId, req_ts);
    	BroadCast("Req ");
    }
    
    public synchronized void Release(){
    	qR.set(myId, Double.POSITIVE_INFINITY);
    	BroadCast("Rel ");
    }
    
    public void BroadCast(String msg){
    	for(Channel channel : activeChannels){
    		if(channel!=null){
    			if(msg.equals("Req ")){
    				channel.send(msg + qR.get(myId));
    				SendUpdateClock();
    			}else if(msg.equals("Rel ")){
    				channel.send(msg + depClock.get(myId));
    				SendUpdateClock();
    			}else if(msg.equals("Up ")){
    				//channel.send(msg + Uptime + " " + x);
    				channel.send(msg + Uptime + " " + seatsLeft);
    				//channel.send("UpSeating " + seating);
    				
    			}
    			
    		}
    	}
    }
    
    public synchronized boolean CanAccess(){
    	boolean canAccess = false;
    	if((myId == qR.minIndex()) && (qR.get(myId) <= depClock.get(depClock.minIndex()))){
    		canAccess = true;
    	} else canAccess = false;
		return canAccess;
    }
    
    public synchronized boolean DoneUpdating(){
    	boolean doneUpdating = false;
    	if(Uptime <=  depClock.get(depClock.minIndex())){
    		doneUpdating = true;
    	}else doneUpdating = false;
		return doneUpdating;
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
    
    
    public static void addSeatingForName(String Name, int seatsNeeded){
    	for(int i=0; i<seating.length;i++){
    		if(seating[i].equals("empty")){
    			seating[i] = Name;
    			System.out.print(i);
    			--seatsNeeded;
    			if(seatsNeeded==0) return;
    			System.out.print(",");
    		}
    	}
    }

	public static void reserveSeats(String name, String seats) {
		// TODO Auto-generated method stub
		int seatsNeeded = Integer.parseInt(seats);
		
		if(seatsLeft<seatsNeeded){
			System.out.println("Failed: only " + seatsLeft + " seats are left but "
					+ seats + " seats are requested");
			return;
		}
		
		seatsLeft -= seatsNeeded;
		System.out.println("The seats have been reserved for " + name + ": " + seats + " seats");
		
		
		System.out.println("After request: " + seatsLeft + " are left");
	}
}
