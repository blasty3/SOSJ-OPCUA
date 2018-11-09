// 20120727
// 0. Re-write by Wei-Tsun Sun
// 20120728 Wei-Tsun Sun
// 1. No checking nor sending acknowledgement at all
// 2. The defualt buffer size is 64k, if something is larger than 64k.....mmmmmmmm you might as well use TCP
// 20120730-1521
// 1. Allow any array type to be the value of signal

package systemj.signals.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.Serializer;


public class UDPReceiver extends GenericSignalReceiver implements Serializable
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

		if(data.containsKey("BufferSize"))
		{
			buffer_length = Integer.parseInt((String)data.get("BufferSize"));
		}
		/*
		else
		{
			throw new RuntimeException("The configuration parameter 'BufferSize' is required!");
		}
		 */
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
	}

	@Override
	public void run()
	{
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;

		try
		{
			socket = new DatagramSocket(port, address);

			
			while(!terminated){

                            Object[] list = new Object[2];
		             
                            try{
                                
                                byte packet[] = new byte[buffer_length];
                                DatagramPacket pack = new DatagramPacket(packet, packet.length);                    
                                byte data[];
                                
                                socket.setSoTimeout(1000);
				socket.receive(pack);
				// packet[] is received here

				if(debug == 1) System.out.println("received pack length = " + pack.getLength() + ", from " + pack.getSocketAddress());
				data = new byte[pack.getLength()];
				System.arraycopy(packet, 0, data, 0, pack.getLength());
                                list[0] = Boolean.TRUE;
				// time to decode make use of data[]
				   
				if(data.length > 0)
				{
					if(se != null)
					{
						if(infoDebug == 1) System.out.println("Overriden deserializer is used");
						// System.out.println("Buffer: " + buffer1);            
						Object buffer1 = se.deserializePacket(data, data.length);
						list[1] = buffer1;            
					}
					else if(((int)data[0] == -84) && ((int)data[1] == -19))
					{
						try
						{
							if(infoDebug == 1) System.out.println("Java built-in deserializer is used");
							ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
							Object mybuffer = ois.readObject();              
							if(infoDebug == 1) System.out.println(mybuffer);
							if(infoDebug == 1) System.out.println((mybuffer.getClass()).getName());
							if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
							{
								Object mybufferArray[] = (Object[])mybuffer;
								// System.out.println(mybufferArray.length);
								if(mybufferArray.length > 1)
								{
									//System.out.println((mybufferArray[0].getClass()).getName().compareTo("String") == 0);
									if((mybufferArray[0].getClass()).getName().compareTo("java.lang.String") == 0)
									{
										String checkingString = (String)mybufferArray[0];
										if(checkingString.compareTo("SystemJChannelCommunication") == 0)
										{
											if(infoDebug == 1) System.out.println("This is systemJ channel communication signal with length = " + mybufferArray.length);
											list = new Object[mybufferArray.length-1];
											for(int i = 0; i < (mybufferArray.length-1); i++) { list[i] = mybufferArray[i+1]; }
										}
										else
										{
											// non-channel communication TCPs
											list[1] = mybuffer;                      
										}
									}
									else
									{
										if(infoDebug == 1) System.out.println("Direct assign the received byffer to the value");
										list[1] = mybuffer;
									}
								}
								else
								{
									if(infoDebug == 1) System.out.println("Direct assign the received byffer to the value");
									list[1] = mybuffer;
								}
							}
							else
							{
								if(infoDebug == 1) System.out.println("Direct assign the received byffer to the value");
								list[1] = mybuffer;
							}

						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						// decode the object from the string          
						String stringToProcess = new String(data);
						if(stringToProcess.indexOf("!@#$%^&*()") == 0)
						{
							if(infoDebug == 1) System.out.println("Object scope deserilizer is used");
							// to obtain the className
							int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
							int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
							String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
							if(infoDebug == 1) System.out.println("Found className = " + className);            
							// get the content of the string
							String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
							if(infoDebug == 1) System.out.println("Class string = " + classString);
							// obtain the bytes and use the serilizer function to work
							byte classBytes[] = classString.getBytes();            
							list[1] = ((Serializer)(Class.forName(className).newInstance())).deserializePacket(classBytes, classBytes.length);;
						}
						else
						{
							if(infoDebug == 1) System.out.println("Not a serialized stream, decode as normal string");
							list[1] = stringToProcess;
						}
					}
				}
				else
				{
					if(infoDebug == 1) System.out.println("Pure signal");
					list[1] = null;
				}
                                
                            } catch(SocketTimeoutException ste){
                                
                                list[0]=Boolean.FALSE;
                                
                                if(!active){
                                     System.out.println("UDPReceiver connection port:" +port+ " is suspended!");
                                }
                                
                                while(!active){
                                    try {
                                       
                                        wait();
                                        
                                    } catch (InterruptedException ex) {


                                    }
                                }
                                
                            }
                            
                                
				super.setBuffer(list);         


			} // end of main while loop
                        System.out.println("UDPReceiver connection port: " +port+" is closed!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public UDPReceiver(){
		super();
	}

	private InetAddress address;
	private int port;
	private Serializer se = null;
	private int buffer_length = 64000;
	private DatagramSocket socket = null;
}
