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

import fb.datatype.*;
import fb.rt.AbstractEvent;
import fb.rt.EventServer;
import fb.rt.FBRManagementException;
import fb.rt.net.SERVER;
import fb.rt.net.SUBSCRIBE;
import java.awt.Color;

public class SERVERIEC61499FBRT extends GenericSignalReceiver implements Serializable
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

                
                if(data.containsKey("Name")){
                    this.name = (String)data.get("Name");
                }
	}

	@Override
	public void run()
	{
            //phyThreadLoop = true;
		
		try
		{
                    
                    //define output event handling
                        final EventServer eventCNF = new AbstractEvent() {
                            @Override
                            public void serviceEvent(EventServer e) {
                                
                                WSTRING data = rd1;
                                System.out.println("IEC 61499 FBRT received data through," +address+ " ,port: " +port+ " received data: " +data.toString().replace("\"", ""));
                                Object dtObj = (Object) data.toString().replace("\"", "");
                                Object[] list = new Object[2];
                                list[0] = Boolean.TRUE;
				list[1] = dtObj;
				setSigBuffer(list);
                           
                            }
                        };
                    
                    //initialize server fb
			
                        server.ID.value=address+":"+port;
                        server.QI.value=true;
                        server.connectOV("RD_1", rd1);
                        server.IND.connectTo(eventCNF);
                        server.INIT.serviceEvent(null); 
                       
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
                //socket.close();
                //terminationDone = true;
	}


	public SERVERIEC61499FBRT(){
		super();
	}
        
        private void setSigBuffer(Object[] obj){
            super.setBuffer(obj);
        }

	private String address;
	private int port;
	private Serializer se = null;
	private int buffer_length = 64000;
	//private DatagramSocket socket = null;
        private final SERVER server = new SERVER(0,1);
        private final WSTRING rd1 = new WSTRING();
		@Override
		public void execute() {
			// TODO Auto-generated method stub
			
		}
}
