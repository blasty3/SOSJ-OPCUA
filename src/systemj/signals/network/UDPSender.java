// 20120727
// 0. Re-write by Wei-Tsun Sun
// 20120728 Wei-Tsun Sun
// 1. No checking for acknowledgement at all

package systemj.signals.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Serializer;

public class UDPSender extends GenericSignalSender implements Serializable
{
	//public void configure(HashMap<String, String> data) throws RuntimeException
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
			catch(Exception e)
			{
				e.printStackTrace();
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
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run()
	{
		int debug = 0;
		int infoDebug = 0;

		try
		{
			byte[] tosend = new byte[1];

			if (super.buffer[1] == null)
			{
				if(infoDebug == 1) System.out.println("Pure signal is emitted, or data of the valued signal is null");
				tosend = new byte[0];
			}      
			else if (se != null)   
			{
				if(infoDebug == 1) System.out.println("Using over-ridden Serializer");
                                
				//tosend = se.serializePacket(super.buffer[1]);
                                tosend = se.serializePacket(super.buffer[1]);
			}			           
			/*else if(super.buffer[1] instanceof java.lang.String)
			{
				if(infoDebug == 1) System.out.println("Value is of type String");
				tosend = ((String)(super.buffer[1])).getBytes();				
			}       */     
			else if (super.buffer[1] instanceof systemj.interfaces.Serializer)
			{
				if(infoDebug == 1) System.out.println("SystemJ Serializer is used");
				//tosend = ((Serializer)super.buffer[1]).serializePacket(super.buffer[1]);
                                tosend = ((Serializer)super.buffer[1]).serializePacket(super.buffer[1]);
			}
			else
			{      
				try{
					if(infoDebug == 1) System.out.println("Java Serializable is used");
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));          
					Object mybuffer = super.buffer;
					if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
					{
						Object mybufferArray[] = (Object[])mybuffer;
						// System.out.println(mybufferArray.length);
						if(mybufferArray.length > 2) // for normal signal, number of elements for each signal is 2
						{
							Object list[] = new Object[mybufferArray.length+1];
							for(int i = 1; i < (mybufferArray.length + 1); i++) { list[i] = mybufferArray[i-1]; }
							list[0] = new String("SystemJChannelCommunication");
							out.writeObject(list);
						}
						else
						{
							out.writeObject(super.buffer[1]); 
                                                        
						}
					}
                                        
					out.flush();				
					tosend = byteStream.toByteArray();
					out.close();
				}
				catch(Exception e)
				{
					client.close();
					e.printStackTrace();
					throw new Exception("No serializable interface is implemented, object can not be sent through " + this.getClass().getName());
				}                        
			}	      

			DatagramPacket packet = new DatagramPacket(tosend, tosend.length, address, port);
			client.send(packet);      
                        System.out.println("Transmitted via UDP to : " +address+ " port: " +port);
			client.close();
		}
		catch (java.net.SocketTimeoutException e)
		{
			System.out.println("Timeout when connecting to ip: " + address + " port :" + port);
		}
		catch (Exception e)
		{
			System.out.println("Problem when connecting to ip: " + address + " port :" + port);
			e.printStackTrace();
		}
	}

	@Override
	public boolean setup(Object[] obj)
	{
		try
		{
			// 20120727
			// sender does not need to bind a port           
			//client = new DatagramSocket(port, address);
			client = new DatagramSocket();   
			super.buffer = obj;
		}
		catch(Exception e)
		{
			// Any other unexpected exceptions
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private InetAddress address;
	private int port;
	private DatagramSocket client = null;
	private Serializer se = null;
}
