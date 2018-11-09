// 20120725
// 0. Re-write by Wei-Tsun Sun
// 1. Unless explicitly declaired in the XML file, the Serilizer is not required at all
// 2. After discussed with HeeJong, buffer size is not used correctly at all
// 3. Everything received store in a byte array first, then process on the first few bytes to tell the incoming traffic to auto-detect the type.
// 4. Introducing object scope serilizer, which is with the header !@#$%^&*()CLASSNAME!@#$%^&*()
// 20120726-0138
// 1. attempts to support channel communication TCP
// 20120730-1521
// 1. Allow any array type to be the value of signal

package systemj.signals.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.Serializer;

public class TCPReceiver extends GenericSignalReceiver implements Serializable
{
	@Override
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{
		if(data.containsKey("IP"))
		{
			try
			{
				address = InetAddress.getByName((String)data.get("IP"));
			}
			catch (UnknownHostException e)
			{
				throw new RuntimeException("Unknown host: " + data.get("IP"), e);
			}
		}
		else
		{
			throw new RuntimeException("The configuration parameter 'IP' is required!");
		}
		if(data.containsKey("Port"))
		{
			port = Integer.parseInt((String)data.get("Port"));
		}
		else
		{
			throw new RuntimeException("The configuration parameter 'Port' is required!");
		} 
		if(data.containsKey("Serializer"))
		{
			try
			{
				se = (Serializer) Class.forName((String)data.get("Serializer")).newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error creating serializer object.", e);
			}
		}
		if(data.containsKey("Name")){
			this.name = (String)data.get("Name");
		}
		else
			throw new RuntimeException("Signal name is missing in LCF");
	}


	class Worker implements Runnable {

		public GenericSignalReceiver gsr;
		public Socket socket;
		public Worker (GenericSignalReceiver g, Socket s){ gsr = g; socket = s;}
		@Override
		public void run() {
			Object[] list = new Object[2];
			list[0] = Boolean.TRUE;
			ObjectInputStream ois = null;

			try{
				InputStream in = socket.getInputStream();
				boolean CONT = true;
				if(se != null){
					while(CONT){
						Object buffer1 = se.deserializeStream(in);
						list[1] = buffer1;
						if(se.isTerminated())
							CONT = false;
						gsr.setBuffer(list);
					}
				}
				else{
					ois = new ObjectInputStream(in);
					while(CONT){
						Object obj;
						try {
							obj = ois.readObject();
							list[1] = obj;
							gsr.setBuffer(list);
						} catch (ClassNotFoundException e) {
							System.err.println("Received class cannot be loaded");
							continue;
						}
					}
				}
			}
			catch(StreamCorruptedException e){
				System.err.println("Error occured while receiving an Object, did you correctly specify Serializer class in LCF?");
			}
			catch (IOException e){}
			finally{
				System.err.println("iSignal "+name+" lost connection from "+socket.getInetAddress().getHostAddress()+" - disconnecting..");
				try {
//					if(ois != null)
//						ois.close();
//					socket.setSoLinger(true, 0);
					socket.close();
				} catch (IOException e1) {e1.printStackTrace();} 	
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			serverSocket = new ServerSocket(port, 50, address);
			while(active){
                            
                            try{
                                serverSocket.setSoTimeout(7000);
				Socket socket = serverSocket.accept();
				new Thread(new Worker(this,socket)).start(); 
                            } catch (SocketTimeoutException tex){
                                
                            }
                               
			}
                        serverSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*  
	public synchronized Object[] getBuffer(){
    System.out.println("getBuffer() is called");
		return super.buffer;
	}
	 */

	public TCPReceiver(){
		super(); // Initializes the buffer
	}

	private InetAddress address;
	private int port;
	private Serializer se = null;
	private int readlength = 0;
	private int buffer_length = 0;
	private ServerSocket serverSocket = null;
}
