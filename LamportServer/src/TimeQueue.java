
public class TimeQueue {
	Double[] queue;
	String type;
	Integer N;
	Integer myId;
	public TimeQueue(String t, Integer size, Integer id){
		queue = new Double[size];
		myId = id;
		N=size;
		type = t;
		if(type.equals("depClock")){
			for(int i=0;i<size;i++){
				if(i==myId) queue[i]=1.0;
				else queue[i]=0.0;
			}
		}else if(type.equals("reqQ")){
			for(int i=0; i<size; i++){
				queue[i]=Double.POSITIVE_INFINITY;
			}
		}
	}
	public synchronized void increment(int index){
		queue[index]++;
	}
	public synchronized Double get(int index){
		return queue[index];
	}
	public synchronized void set(int index, Double value){
		queue[index]=value;
	}
	public synchronized Integer minIndex(){
		Double min = queue[0];
    	Integer minID = 0;
    	for(int i=0; i<queue.length;i++){
    		if(queue[i]<min){
    			min = queue[i];
    			minID = i;
    		}
    	}
    	return minID;
	}
}
