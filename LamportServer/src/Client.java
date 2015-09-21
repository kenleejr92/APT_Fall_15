import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;

public class Client{
	
	public static void main(String[] args) {
		OtherServers o;
		o = new OtherServers("servers.txt");
		ServerID[] listOfServers= o.getServerIDs();
		//System.out.println(listOfServers[1].portNum);
		Socket socket = null;
		BufferedReader dIn = null;
		PrintWriter dOut = null;
		boolean done = false;
		//System.out.println(args.length);
		while(!done){
			while(true){
				Random rn = new Random();
				int i = rn.nextInt(listOfServers.length);
				try{
        			socket = new Socket(listOfServers[i].IP, listOfServers[i].portNum);
		            //s.setSoTimeout(5000);
        			System.out.println("Connected to: " + i);
		            dIn = new BufferedReader(new
		            InputStreamReader(socket.getInputStream()));
		            dOut = new PrintWriter(socket.getOutputStream());
		            break;
	        	}catch(IOException e){
	        		continue;
	        	}
			}
//			for(int i= 0; i<listOfServers.length; i++){
//					try{
//	        			socket = new Socket(listOfServers[i].IP, listOfServers[i].portNum);
//			            //s.setSoTimeout(5000);
//	        			System.out.println("Connected to: " + i);
//			            dIn = new BufferedReader(new
//			            InputStreamReader(socket.getInputStream()));
//			            dOut = new PrintWriter(socket.getOutputStream());
//			            break;
//		        	}catch(IOException e){
//		        		continue;
//		        	}
//	    	}
			dOut.println("Client");
			dOut.flush();
			while(!done){
				try {
					String line;
					System.out.print(">> ");
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					line = in.readLine();
					if(line.equals("exit")){
						done = true;
						break;
					}
					dOut.println(line);
					dOut.flush();
					String response = null;
					response = dIn.readLine();
					if(response!=null){
						System.out.println(response);
						
					}else{
						socket.close();
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}