import java.io.IOException;
import java.util.StringTokenizer;


public class ByzantineFeedback {
	ByzantineAgreement byzantineAgreement;
	ByzantineFaulty byzantineFaulty;
	Integer[] V;
	Double[] W;
	String processType;
	Integer maxFailures;
	
	public ByzantineFeedback(String processType, Integer maxFailures) {
		V = new Integer[Process.numProcesses];
		W = new Double[Process.numProcesses];
		for(int i=0; i<V.length; i++){
			V[i] = 0;
		}
		this.maxFailures = maxFailures;
		this.processType = processType;
		if(processType.equals("-n")){
			Integer init_proposal = 0;
			byzantineAgreement = new ByzantineAgreement(init_proposal, maxFailures);
		}else if(processType.equals("-b")){
			Integer init_proposal = 0;
			byzantineFaulty = new ByzantineFaulty(init_proposal, maxFailures);
		}else if(processType.equals("-c")){
			Integer init_proposal = 0;
			byzantineAgreement = new ByzantineAgreement(init_proposal, maxFailures);
		}
	}
	
	public void run(){
		if(processType.equals("-n")){
			V[Process.myID] = rand0or1();
			ExchangeValues(V[Process.myID]);
			for(int j=0;j<V.length;j++){
				byzantineAgreement.Init(V[j], maxFailures);
				V[j] = byzantineAgreement.run();
			}
			
			
		}else if(processType.equals("-b")){
			V[Process.myID] = rand0or1();
			ExchangeValues(V[Process.myID]);
			for(int j=0;j<V.length;j++){
				byzantineFaulty.Init(V[j], maxFailures);
				V[j] = byzantineFaulty.run();
			}
			
			
		}else if(processType.equals("-c")){
			V[Process.myID] = Process.correctValue;
			ExchangeValues(V[Process.myID]);
			for(int j=0;j<V.length;j++){
				byzantineAgreement.Init(V[j], maxFailures);
				V[j] = byzantineAgreement.run();
			}
			
		}
		 
	}
	
	private void ExchangeValues(Integer value){
		for(Channel c:Process.channelList){
			if(c!=null){
				if(processType.equals("n") || processType.equals("c")){
					c.send("Exchange " + V[Process.myID]);
				} else{
					c.send("Exchange " + rand0or1());
				}
				
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
					
					if(st.nextToken().equals("Exchange")){
						V[j]=Integer.parseInt(st.nextToken());
					}
				}
			}
		}
	}
	
	
	private Integer rand0or1(){
		if(Math.random()<0.5){
			return new Integer(1);
		}else return new Integer(0);
	}
	
	private void PrintV(){
		StringBuilder sb = new StringBuilder("");
		sb.append(processType + "P" + Process.myID + " Vector: ");
		for(int i=0; i<Process.numProcesses; i++){
			sb.append(V[i] + " ");
		}
		System.out.println(sb);
	}

}
