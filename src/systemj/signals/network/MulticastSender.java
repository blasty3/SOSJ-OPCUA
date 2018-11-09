// 20120727
// 0. Re-write by Wei-Tsun Sun

package systemj.signals.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Serializer;

public class MulticastSender extends GenericSignalSender implements Serializable
{
	//public void configure(HashMap<String, String> data) throws RuntimeException
	@Override
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{
		if(data.containsKey("GROUP"))
		{
			try
			{
				address = InetAddress.getByName((String)data.get("GROUP"));
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				throw new RuntimeException("Unknown host: " + data.get("GROUP"), e);
			}
		}
		else
		{
			throw new RuntimeException("The configuration parameter 'GROUP' is required!");
		}

		if(data.containsKey("IP"))
		{
			try
			{
				interfaceAddress = InetAddress.getByName((String)data.get("IP"));
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				throw new RuntimeException("Problem with connecting to group: " + data.get("IP"), e);
			}
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
				tosend = se.serializePacket(super.buffer[1]);				
			}			           
			/*else if(super.buffer[1] instanceof java.lang.String)
			{
				if(infoDebug == 1) System.out.println("Value is of type String");
				tosend = ((String)(super.buffer[1])).getBytes();				
			}  */          
			else if (super.buffer[1] instanceof systemj.interfaces.Serializer)
			{
				if(infoDebug == 1) System.out.println("SystemJ Serializer is used");
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

			// send the data
			DatagramPacket packet = new DatagramPacket(tosend, tosend.length, address, port);
			client.send(packet);

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
			//client = new MulticastSocket(port);    
			client = new MulticastSocket();
			if(interfaceAddress != null)
			{
				client.setInterface(interfaceAddress);
			}
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

	private InetAddress address = null;
	private int port = 0;
	private MulticastSocket client = null;
	private Serializer se = null;
	private InetAddress interfaceAddress = null;
}
