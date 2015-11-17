import java.io.IOException;
import java.util.StringTokenizer;


public class ByzantineFeedback {
	ByzantineAgreement byzantineAgreement;
	ByzantineFaulty byzantineFaulty;
	Integer[] V;
	Double[] W;
	String processType;
	Integer maxFailures;
	Integer decided;
	Double epsilon;
	Integer rounds;
	
	public ByzantineFeedback(String processType, Integer maxFailures) {
		V = new Integer[Process.numProcesses];
		W = new Double[Process.numProcesses];
		epsilon = 0.5;
		rounds = 10;
		for(int i=0; i<V.length; i++){
			V[i] = 0;
			W[i] = 1.0/Process.numProcesses;
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
		Double s0 = 0.0;
		Double s1 = 0.0;
		for(int i=0; i<rounds; i++){
			switch(processType){
			case "-n":
				V[Process.myID] = rand0or1();
				break;
			case "-b":
				V[Process.myID] = rand0or1();
				break;
			case "-c":
				V[Process.myID] = Process.correctValue;
				break;
			default:
				break;
			}
			ExchangeValues(V[Process.myID]);
			for(int j=0;j<V.length;j++){
				switch(processType){
				case "-n":
					byzantineAgreement.Init(V[j], maxFailures);
					V[j] = byzantineAgreement.run();
					break;
				case "-b":
					byzantineFaulty.Init(V[j], maxFailures);
					V[j] = byzantineFaulty.run();
					break;
				case "-c":
					byzantineAgreement.Init(V[j], maxFailures);
					V[j] = byzantineAgreement.run();
					break;
				default:
					break;
				}	
			}
			//PrintV();
			for(int j=0; j<W.length; j++){
				if(V[j].equals(0)){
					s0 += W[j];
				}else{
					s1 += W[j];
				}
			}
			if(s0>=s1) decided = 0;
			else decided = 1;
			
			if(!decided.equals(Process.correctValue)){
				System.out.println("Incorrect");
				//Processes decided incorrectly, need to decrease weights of incorrect processes
				for(int j=0; j<V.length; j++){
					if(!V[j].equals(Process.correctValue)){
						W[j] = (1-epsilon)*W[j];
					}
				}
			}else{
				System.out.println("Correct");
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
