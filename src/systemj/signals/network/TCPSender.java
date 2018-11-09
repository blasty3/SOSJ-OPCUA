// 20120725
// 0. Re-write by Wei-Tsun Sun
// 1. Unless explicitly declaired in the XML file, the Serilizer is not required at all
// 2. Only send the value of the signal, because at receiving side, if something is received, that means the value is true. At least for the TCP case.
// 20120726-0138
// 1. attempts to support channel communication TCP

package systemj.signals.network;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Serializer;

public class TCPSender extends GenericSignalSender implements Serializable
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
		int infoDebug = 1;
		try
		{
			if (super.buffer[1] == null)
			{
				if(infoDebug == 1) System.out.println("Pure signal is emitted, or data of the valued signal is null");
				client.close();
			}      
			else if (se != null)   
			{
				if(infoDebug == 1) System.out.println("Using over-ridden Serializer");
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				byte[] tosend = se.serializePacket(super.buffer[1]);				
				out.write(tosend, 0, tosend.length);
				out.close();
				client.close();
			}			           
			/*else if(super.buffer[1] instanceof java.lang.String)
			{
				if(infoDebug == 1) System.out.println("Value is of type String");
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				byte[] tosend = ((String)(super.buffer[1])).getBytes();				
				out.write(tosend, 0, tosend.length);
				out.close();
				client.close();        
			}   */         
			/*
      else if(super.buffer[1] instanceof java.io.Serializable)
			{
			}
			 */
			else if (super.buffer[1] instanceof systemj.interfaces.Serializer)
			{
				if(infoDebug == 1) System.out.println("SystemJ Serializer is used");
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				byte[] tosend = ((Serializer)super.buffer[1]).serializePacket(super.buffer[1]);				
				out.write(tosend, 0, tosend.length);
				out.close();
				client.close();
			}
			else
			{      
				try{
					if(infoDebug == 1) System.out.println("Java Serializable is used");
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
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
                                        System.out.println("Transmitted via TCP to: " +address+ " port: " +port);
					out.close();
					client.close();
				}
				catch(Exception e)
				{
					client.close();
					e.printStackTrace();
					throw new Exception("No serializable interface is implemented, object can not be sent through " + this.getClass().getName());
				}                        
			}			
		}
		catch (Exception e)
		{
			System.err.println("Problem occurs when connecting to ip: " + address + " port :" + port);
			e.printStackTrace();			
		}
	}

	@Override
	public boolean setup(Object[] obj)
	{
		try
		{
                     System.out.println("Creating TCP Socket");
			client = new Socket(address, port);
			super.buffer = obj;
		}
		catch(java.net.ConnectException e){
                    System.out.println("No one is listening");
			// This means there is no listener
			return false;
		}
		catch(Exception e)
		{
                    System.out.println("Other exceptions");
			// Any other unexpected exceptions
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private InetAddress address;
	private int port;
	private Socket client = null;
	private Serializer se = null;
}
