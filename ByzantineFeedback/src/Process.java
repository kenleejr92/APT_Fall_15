import java.net.*;
import java.io.*;
import java.util.*;

public class Process {
	public static Integer myID;
	public static String processType;
	public static Integer numProcesses;
	public static ProcessInfo[] processes;
	public static List<Channel> channelList;
	public static Random rand;
	public static Integer f;
	public static Integer correctValue = 1;
	public static final Boolean NORMAL_BYZANTINE = false;
	public static Integer numRounds;
	public static Double[] roundWeights;
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("Incorrect Arguments");
			System.exit(0);
		}else{
			processType = args[0];
			myID = Integer.parseInt(args[1]);
		}
		try{
			ConnectAllServers();
			Thread.sleep(5000);
			if(NORMAL_BYZANTINE){
				if(processType.equals("-n")){
					Integer proposal = rand0or1();
					System.out.println("nP" + myID + " Proposing: " + proposal);
					ByzantineAgreement ba = new ByzantineAgreement(proposal,f);
					Integer agreement = ba.run();
					System.out.println("nP" + myID + " Agrees on: " + agreement);
				}else if(processType.equals("-b")){
					Integer proposal = rand0or1();
					System.out.println("bP" + myID + " Proposing: " + proposal);
					ByzantineFaulty bf = new ByzantineFaulty(proposal,f);
					Integer agreement = bf.run();
					System.out.println("bP" + myID + " Agrees on: " + agreement);
				}else if(processType.equals("-c")){
					Integer proposal = correctValue;
					System.out.println("cP" + myID + " Proposing: " + correctValue);
					ByzantineAgreement ba = new ByzantineAgreement(proposal,f);
					Integer agreement = ba.run();
					System.out.println("cP" + myID + " Agrees on: " + agreement);
				}
			}else{
				ByzantineFeedback bf = new ByzantineFeedback(processType, f);
				bf.run();
			}
			
			
		}catch(IOException e){
			System.out.println("Socket Error");
		}

	}
	
	static private void ConnectAllServers() throws IOException{
		readProcessFile("Processes.txt");
		readWeightsFile("RoundWeights.txt");
		channelList = new ArrayList<>(numProcesses);
		ServerSocket listener = new ServerSocket(processes[Process.myID].portNum, 0, processes[Process.myID].IP);
		/* accept connections from all the smaller processes */
        for (int i = 0; i < myID; i++) {
            Socket s = listener.accept(); 
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
			f = Integer.parseInt(in.readLine());
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
	
	static private void readWeightsFile(String filename){
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			int i=0;
			numRounds = Integer.parseInt(in.readLine());
			roundWeights = new Double[numRounds];
			while(true)
			{
				String line = in.readLine();
				if(line == null){
					in.close();
					break;
				}
				roundWeights[i++] = Double.parseDouble(line);
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}catch(NumberFormatException e){
			//do nothing
		}
	}

	static private Integer rand0or1(){
		if(Math.random()<0.5){
			return new Integer(1);
		}else return new Integer(0);
	}
}
