import java.net.*;
import java.io.*;
import java.util.*;

public class Process {
	public static Integer myID;
	public static Integer numProcesses;
	public static ProcessInfo[] processes;
	public static List<Channel> channelList;
	public static Integer f = 1;
	
	public static void main(String[] args) throws Exception {
		if(args.length > 1 || args.length == 0){
			System.out.println("Incorrect Arguments");
			System.exit(0);
		}else{
			myID = Integer.parseInt(args[0]);
		}
		try{
			ConnectAllServers();
			for(Channel c:channelList){
				if(c!=null){
					System.out.println("Connected to: " + String.valueOf(c.getRemoteID()));
				}
			}
			BufferedReader dIn = new BufferedReader(new InputStreamReader(System.in));
			String proposal_string = dIn.readLine();
			Integer proposal= Integer.parseInt(proposal_string);
			ByzantineAgreement ba = new ByzantineAgreement(proposal,f);
			Integer agreement = ba.run();
			System.out.println("I agree on "+ agreement);
			
		}catch(IOException e){
			System.out.println("Socket Error");
		}

	}
	
	static private void ConnectAllServers() throws IOException{
		readProcessFile("Processes.txt");
		channelList = new ArrayList<>(numProcesses);
		ServerSocket listener = new ServerSocket(processes[Process.myID].portNum, 0, processes[Process.myID].IP);
		/* accept connections from all the smaller processes */
        for (int i = 0; i < myID; i++) {
            Socket s = listener.accept(); 
            //s.setSoTimeout(5000);
            BufferedReader dIn = new BufferedReader(
            new InputStreamReader(s.getInputStream()));
            PrintWriter dOut = new PrintWriter(s.getOutputStream());
            Channel c = new Channel(s,dIn,dOut,i,myID);
            channelList.add(i,c);
        }
        channelList.add(myID,null);
        /* contact all the bigger processes */
        for (int i = myID + 1; i < numProcesses; i++) {
            Socket s = new Socket(processes[i].IP, processes[i].portNum);
            //s.setSoTimeout(5000);
            BufferedReader dIn = new BufferedReader(new
            InputStreamReader(s.getInputStream()));
            PrintWriter dOut = new PrintWriter(s.getOutputStream());
            Channel c = new Channel(s,dIn,dOut,i,myID);
            channelList.add(i,c);
        }
		
	}
	
	static private void readProcessFile(String filename){
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			int i=0;
			numProcesses = Integer.parseInt(in.readLine());
			processes = new ProcessInfo[numProcesses];
			while(true)
			{
				String line = in.readLine();
				if(line == null){
					in.close();
					break;
				}
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens()){
					InetAddress ip = InetAddress.getByName(st.nextToken());
					Integer port = Integer.parseInt(st.nextToken());
					processes[i++]=new ProcessInfo(ip, port);
				}
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}
	}
}
