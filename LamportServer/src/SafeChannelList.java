import java.util.*;
import java.io.*;
import java.net.*;

public class SafeChannelList{
	List<Channel> activeChannels;
	
	public SafeChannelList(ArrayList<Channel> list){
		activeChannels = list;
	}
	public synchronized void remove(Integer index){
		activeChannels.set(index, null);
	}
	public synchronized void add(Integer index, Channel channel){
		activeChannels.add(index, channel);
	}
	public synchronized Channel get(Integer index){
		return activeChannels.get(index);
	}
}
