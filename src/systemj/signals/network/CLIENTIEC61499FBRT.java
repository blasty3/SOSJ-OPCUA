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


import fb.datatype.*;
import fb.rt.AbstractEvent;
import fb.rt.EventServer;
import fb.rt.FBRManagementException;
import fb.rt.net.CLIENT;
import fb.rt.net.PUBLISH;

public class CLIENTIEC61499FBRT extends GenericSignalSender implements Serializable
{
	//public void configure(HashMap<String, String> data) throws RuntimeException
	@Override
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{
		if(data.containsKey("IP"))
		{
                    /*
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
                        */
                    address = (String)data.get("IP");
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
                /*
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
                */
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run()
	{
		
                        final EventServer eventCNF = new AbstractEvent() {
                            @Override
                            public void serviceEvent(EventServer e) {
                                System.out.println("IEC 61499 FBRT sent hrough," +address+ " ,port: " +port+" with status: " +client.STATUS);
                                
                                client.QI.value = false;
                                client.INIT.serviceEvent(null);
                            }
                        };
            
                //String dataToSend = null;
		try
		{
                                        client.CNF.connectTo(eventCNF);
                                        //if(super.buffer[1] == String.class){
                                             
                                            //sd1 = new WSTRING(super.buffer[1].toString());
                                        sd1.initialize(super.buffer[1].toString());
                                            
                                            //sd1.initialize(super.buffer[1].toString());
                                        //}
                    
                                       
					
                                        
                                        client.connectIV("SD_1", sd1);
                                        client.REQ.serviceEvent(null);
                                        
                        
                        

                        //client.INITO.connectTo(null);
                        
                        
                        //disconnecting
			
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
			//initialize vars
                        
                        final EventServer eventINITO = new AbstractEvent() {
                            @Override
                            public void serviceEvent(EventServer e) {
                                System.out.println("Connection with status: " +client.STATUS);
                                
                           
                            }
                        };
                        client.INITO.connectTo(eventINITO);
                        client.QI.value = true;
			client.ID.value = address+":"+port;
			client.INIT.serviceEvent(null);
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

	//private InetAddress address;
        private String address;
	private int port;
	private final CLIENT client = new CLIENT(1,0);
        //private DatagramSocket client = null;
	//private Serializer se = null;
        private final WSTRING sd1 = new WSTRING();
}
