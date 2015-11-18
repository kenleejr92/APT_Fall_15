import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;


public class ByzantineAgreement {
	Integer[] V;
	Integer x;
	Integer f;
	Integer myValue;
	Integer kingValue;
	
	public ByzantineAgreement(Integer proposal, Integer maxFailures){
		V = new Integer[Process.numProcesses];
		Init(proposal,maxFailures);
	}
	
	public void Init(Integer proposal, Integer maxFailures){
		x = proposal;
		f = maxFailures;
		kingValue = 0;
		for(int i=0; i<Process.numProcesses; i++){
			if(Process.myID.equals(i)){
				V[i]=x;
			}else{
				V[i]=new Integer(0); //default value;
			}
		}
	}
	
	public Integer run(){
		for(int k=0; k<f+1; k++){
			//first phase
			for(Channel c:Process.channelList){
				if(c!=null){
					//System.out.println("nP"+Process.myID+" "+V[Process.myID]);
					c.send("First " + V[Process.myID]);
				}
			}
			for(int j=0; j<Process.numProcesses;j++){
				if(!Process.myID.equals(j)){
					Channel c = Process.channelList.get(j);
					if(c!=null){
						String message;
						while(true){
							try{
								message = c.readLine();
								break;
							}catch(IOException e){
								continue;
							}
						}
						StringTokenizer st = new StringTokenizer(message);
						
						if(st.nextToken().equals("First")){
							V[j]=Integer.parseInt(st.nextToken());
						}
					}
				}
			}
			myValue = Majority();
			
			
			//second phase
			if(Process.myID.equals(k)){
				kingValue = myValue;
				for(Channel c:Process.channelList){
					if(c!=null){
						c.send("Second " + myValue);
					}
				}
			}
			//Receive kingValue from Pk
			Channel c = Process.channelList.get(k);
			if(c!=null){
				String message;
				while(true){
					try{
						message = c.readLine();
						break;
					}catch(IOException e){
						continue;
					}
				}
				StringTokenizer st = new StringTokenizer(message);
				if(st.nextToken().equals("Second")){
					kingValue=Integer.parseInt(st.nextToken());
				}
			}
			if(ChooseMyValue()){
				V[Process.myID] = myValue;
			} else{
				V[Process.myID] = kingValue;
			}
			
		}
		return V[Process.myID];
	}
	
	
	private Integer Majority(){
		Integer oneCount=0, zeroCount=0;
		for(int i=0; i<V.length; i++){
			if(V[i].equals(1)) oneCount++;
			if(V[i].equals(0)) zeroCount++;
		}
		if(oneCount > zeroCount){
			return 1;
		}else if(oneCount < zeroCount){
			return 0;
		}else return 0;
	}
	
	private Boolean ChooseMyValue(){
		Integer oneCount=0, zeroCount=0;
		Integer bound = Process.numProcesses/2+f;
		for(int i=0; i<V.length; i++){
			if(V[i].equals(1)) oneCount++;
			if(V[i].equals(0)) zeroCount++;
		}
		if(myValue.equals(0) && zeroCount>bound){
			return true;
		}else if(myValue.equals(1) && oneCount>bound){
			return true;
		}else{
			return false;
		}
	}
	
	protected void ReceivePulse(Integer k){
		for(int j=0; j<Process.numProcesses;j++){
			if(!Process.myID.equals(j)){
				Channel c = Process.channelList.get(j);
				if(c!=null){
					String message;
					while(true){
						try{
							message = c.readLine();
							break;
						}catch(IOException e){
							continue;
						}
					}
					StringTokenizer st = new StringTokenizer(message);
					if(st.nextToken().equals("Pulse") && k.equals(Integer.parseInt(st.nextToken()))){
						break;
					}
				}
			}
		}
	}
}
