import java.net.InetAddress;


public class ProcessInfo {
	public InetAddress IP;
	public Integer portNum;
	
	public ProcessInfo(InetAddress ip, Integer port){
		IP = ip;
		portNum = port;
	}

	public boolean equals(ProcessInfo sid){
		if(sid.IP.equals(this.IP) && sid.portNum.equals(this.portNum)){
			return true;
		}else return false;
	}
}
