import java.io.IOException;


public class ChannelException extends IOException {
	Channel channel;
	ChannelException(Channel c){
		channel = c;
	}
}
