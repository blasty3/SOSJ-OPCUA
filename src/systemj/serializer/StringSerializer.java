package systemj.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import systemj.interfaces.Serializer;

public class StringSerializer implements Serializer,Serializable
{

	
	public Object deserializePacket(byte[] msg, int length)
	{
		String str = new String(msg);
		return (Object)str;
	}

	public byte[] serializePacket(Object obj)
	{
		String str = (String)obj;
		return str.getBytes();
	}
	
	
	private static final int INC_SIZE = 10;
	private static final int PACKET_SIZE = 5;
	
	private int STRING_LENGTH = 20;
	private byte[] prevBytes = new byte[PACKET_SIZE];
	public boolean contFromPrev = false;
	private int prevSize = 0;
	
	public Object deserializeStream(InputStream is) throws IOException {
		
		byte[] b = new byte[PACKET_SIZE];
		byte[] tot = new byte[STRING_LENGTH];
		int count = 0;
		int total = 0;
		boolean etx = false;
		
		terminated = false;
		while(true){
			if (contFromPrev){
				System.arraycopy(prevBytes, 0, b, 0, prevSize);
				count = prevSize;
				contFromPrev = false;
			}
			else{
				count = is.read(b, 0, b.length);
				if(count < 0){
					terminated = true;
					return new String(tot);
				}
			}
			for(int i=0; i<count ; i++){
				if(b[i] == 3 || b[i] == 4){
					prevSize = count-i-1;
					System.arraycopy(b, i+1, prevBytes, 0, prevSize);
					contFromPrev=true;
					count = i;
					etx=true;
					break;
				}
			}
			
			if(total + count > STRING_LENGTH){
				STRING_LENGTH += INC_SIZE;
				byte[] tmp = new byte[STRING_LENGTH];
				System.arraycopy(tot, 0, tmp, 0, tot.length);
				tot = tmp;
			}
			System.arraycopy(b, 0, tot, total, count);
			total += count;
			
			if(etx)
				return new String(tot);
		}
		
	}
	
	public boolean terminated = false;

	public boolean isTerminated() {
		return terminated;
	}

	private byte[] toSend;
	private int total = 0;
	
	public int serializeStream(byte[] b, int length) {
		if(total == toSend.length)
			return -1;
		else if(total + length > toSend.length){
			length = toSend.length - total;
		}
		System.arraycopy(toSend, total, b, 0, length);
		total += length;
		return length;
	}

	public void setObjectToStream(Object obj) {
		total=0;
		toSend = ((String)obj).getBytes();
	}
}
