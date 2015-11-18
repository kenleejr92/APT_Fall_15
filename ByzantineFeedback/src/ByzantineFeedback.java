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
	Double accuracyPercentage;
	Double correctDecisions;
	Double probabilityCorrect = 0.52;
	Integer byzantineModel = 2;
	Double[] roundValues;
	
	public ByzantineFeedback(String processType, Integer maxFailures) {
		V = new Integer[Process.numProcesses];
		W = new Double[Process.numProcesses];
		epsilon = 0.1;
		rounds = 50;
		roundValues = new Double[rounds];
		accuracyPercentage = 0.0;
		correctDecisions = 0.0;
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
//			byzantineFaulty = new ByzantineFaulty(init_proposal, maxFailures);
			byzantineAgreement = new ByzantineAgreement(init_proposal, maxFailures);
		}else if(processType.equals("-c")){
			Integer init_proposal = 0;
			byzantineAgreement = new ByzantineAgreement(init_proposal, maxFailures);
		}
	}
	
	public void run(){
		Double s0 = 0.0;
		Double s1 = 0.0;
		for(int i=0; i<rounds; i++){
			s0=0.0;
			s1=0.0;
			if(Process.myID.equals(12)) System.out.println("Round: " + String.valueOf(i));
			switch(processType){
			case "-n":
				V[Process.myID] = rand0or1(); //normal 
				break;
			case "-b":
				switch(byzantineModel){
				case 1:
					V[Process.myID] = 0; //model 1
					break;
				case 2:
					if(i<20) V[Process.myID] = 1;
					else V[Process.myID] = 0;
					break;
				case 3:
					if(i<40) V[Process.myID] = 1; //model 3
					else V[Process.myID] = 0; 
					break;
				default:
					break;
				}
				break;
			case "-c":
				V[Process.myID] = 1;  //correct process
				break;
			default:
				break;
			}
			//All processes exchange values with each other
			ExchangeValues(V[Process.myID]);
			//Makes all V's the same on all non-faulty processes
			for(int j=0;j<V.length;j++){
				switch(processType){
				case "-n":
					byzantineAgreement.Init(V[j], maxFailures);
					V[j] = byzantineAgreement.run();
					break;
				case "-b":
//					byzantineFaulty.Init(V[j], maxFailures);
//					V[j] = byzantineFaulty.run();
					byzantineAgreement.Init(V[j], maxFailures);
					V[j] = byzantineAgreement.run();
					break;
				case "-c":
					byzantineAgreement.Init(V[j], maxFailures);
					V[j] = byzantineAgreement.run();
					break;
				default:
					break;
				}	
			}
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
				//Processes decided incorrectly, need to decrease weights of incorrect processes
				if(Process.myID.equals(12)) System.out.println("Incorrect");
				for(int j=0; j<V.length; j++){
					if(!V[j].equals(Process.correctValue)){
						W[j] = (1-epsilon)*W[j];			//Multiplicative 
					}
				}
			}else{
				if(Process.myID.equals(12)) System.out.println("Correct!");
				correctDecisions += 1;
			}
			if(Process.myID.equals(12)){
				System.out.println("s0: " + s0);
				System.out.println("s1: " + s1);
				PrintV();
				PrintW();
			}
		}
		accuracyPercentage = correctDecisions/(rounds);
		if(Process.myID.equals(12)){
			System.out.println("Accuracy: " + accuracyPercentage);
		}
	}
	
	private void ExchangeValues(Integer value){
		for(Channel c:Process.channelList){
			if(c!=null){
				c.send("Exchange " + V[Process.myID]);
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
		if(Math.random() <= probabilityCorrect){
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
	
	private void PrintW(){
		StringBuilder sb = new StringBuilder("");
		sb.append(processType + "P" + Process.myID + " Weights: ");
		for(int i=0; i<Process.numProcesses; i++){
			sb.append(W[i] + " ");
		}
		System.out.println(sb);
	}

}
