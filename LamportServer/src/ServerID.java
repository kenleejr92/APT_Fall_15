import java.net.*;
import java.io.*;
import java.util.*;

public class ServerID {
		public InetAddress IP;
		public Integer portNum;
		
		public ServerID(InetAddress ip, Integer port){
			IP = ip;
			portNum = port;
		}

}
