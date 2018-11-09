// 20120727
// 0. Re-write by Wei-Tsun Sun
// 20120728 Wei-Tsun Sun
// 1. No checking nor sending acknowledgement at all
// 2. The defualt buffer size is 64k, if something is larger than 64k.....mmmmmmmm you might as well use TCP
// 20120730-1521
// 1. Allow any array type to be the value of signal

package systemj.signals.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJMessageConstants;
import systemj.common.SJResponseMessage;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.ControlMsgBuffer;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.Serializer;
import systemj.signals.SOA.ControlMessageComm;


public class UDPSOAReceiver extends GenericSignalReceiver implements Serializable
{
    
        
	//public void configure(HashMap<String, String> data) throws RuntimeException
	@Override
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{
		
		 
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
               
                
                if(data.containsKey("requestPort"))
		{
			reqPort= Integer.parseInt((String)data.get("requestPort"));
		} else {
                    throw new RuntimeException("'requestPort' number attribute needs to be defined");
                }
                
                if(data.containsKey("responsePort"))
		{
			respPort= Integer.parseInt((String)data.get("responsePort"));
		} else {
                    respPort = reqPort;
                }
                
               // if(data.containsKey("associatedServiceName"))
		//{
		//	//servName= (String)data.get("associatedServiceName");
		//} else {
               //     throw new RuntimeException("'associatedServiceName' number attribute needs to be defined");
                //}
                
                /*
                if (data.containsKey("IsDoubleACK"))
                {
                    IsDoubleACK= Boolean.parseBoolean((String)data.get("IsDoubleACK"));
                }else {
                    throw new RuntimeException("'IsDoubleACK' number attribute needs to be defined, true or false");
                }
                */
                
                if(data.containsKey("Name")){
                    this.name = (String)data.get("Name");
                }
  
	}

	@Override
	public void run()
	{

		
                    ProviderControlMessageReceiver();
                
  
        }
        
        private void ProviderControlMessageReceiver(){

            ControlMessageComm cntrlMsg = new ControlMessageComm();
            
            String msg;
            
            while (!SJServiceRegistry.getParsingStatus()){
                
            }
            
            Object[] list = new Object[2];
            
            while(!terminated){
                
                JSONObject js = new JSONObject();
                
                try{
                    
                    MulticastSocket s = new MulticastSocket(reqPort);
                    s.joinGroup(InetAddress.getByName("224.0.0.101"));
                    
                    int debug=1;
                
                        try{
                    
                     int debug1=0;
                   
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        
                        if (debug1==1) System.out.println("ReceiveControlMessageServTypeAndActionName port:" +reqPort);
                        
                       // MulticastSocket s = new MulticastSocket(SJServiceRegistry.getServicesProviderControlPortOfTypeAndAction(serviceType,actionName));
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                       
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (debug1==1)  System.out.println( "port" +resp.getPort()+ "is in loopback mode? : " +s.getLoopbackMode());
                       
                        
                        s.setSoTimeout(1000);
                        s.receive(resp);
                        
                        
                        
                        byte[] data;
                        if(debug1 == 1) System.out.println("received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        //SJServiceRegistry.addMessageTokensToBuffer(js);
                                        
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

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
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
    

                    
                    //catch (java.net.SocketTimeoutException e)
                    //{
		//	System.out.println("Timeout" +e.getMessage());
                    //    answer="{}";
                    //}
                     try {
                    Thread.sleep(400);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                        
                        if (debug==1) System.out.println("SOAActuatorControlMessage received control message:" +js);
                    
                    try
                    {
                        
                        //JSONObject js = new JSONTokener((cntrlMesg.receiveControlMessage(sensor).trim()));
                     //JSONObject js  = new JSONObject(new JSONTokener(jsRespMsg));
                    
                   //  if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()) && js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString()));
                   //    {
                    
                             String type = js.getString("msgType");
                             String code = js.getString("msgCode");
                             
                             //String con = js.getString("conf");
                             
                             if(type.equalsIgnoreCase(SJMessageConstants.MessageType.SINGLECON.toString()) && code.equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                 
                                 /*
                                  SJResponseMessage sjresp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                                           //sjresp.setCode(SJMessageConstants.MessageCode.PUT);
                                  
                                           
                                  
                                           sjresp.setSourceAddress(SJServiceRegistry.getOwnIPAddressFromRegistry());
                                           //sjresp.setMessageType(SJMessageConstants.MessageType.ACK);
                                           sjresp.setDestinationPort(respPort);
                                           sjresp.setMessageID(js.getInt("msgID"));
                                           //sjresp.setMessageToken();
                                           //sjresp.setPayload("ok");
                                           String respMessage = sjresp.createResponseMessage();
                                           
                                           cntrlMsg.sendControlMessage(js.getString("srcAddr"), respPort, respMessage);
                                   */
                                            list[0] = Boolean.TRUE;
                                            list[1] = js.toString();
                                 
                             } else if (type.equalsIgnoreCase(SJMessageConstants.MessageType.NON.toString()) && code.equalsIgnoreCase(SJMessageConstants.MessageCode.POST.toString())){
                                 
                                 list[0] = Boolean.TRUE;
                                 list[1] = js.toString();
                                 
                             } 
                             
                             
                             else if(type.equalsIgnoreCase(SJMessageConstants.MessageType.SINGLECON.toString()) && code.equalsIgnoreCase(SJMessageConstants.MessageCode.POST.toString())){
                                 
                                  SJResponseMessage sjresp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                                           //sjresp.setCode(SJMessageConstants.MessageCode.PUT);
                                  
                                           sjresp.setSourceAddress(SJSSCDSignalChannelMap.GetLocalSSAddr());
                                           //sjresp.setMessageType(SJMessageConstants.MessageType.ACK);
                                           sjresp.setDestinationPort(respPort);
                                           sjresp.setMessageID(js.getInt("msgID"));
                                           //sjresp.setMessageToken();
                                           //sjresp.setPayload("ok");
                                           String respMessage = sjresp.createResponseMessage();
                                           
                                           cntrlMsg.sendControlMessage(js.getString("srcAddr"), respPort, respMessage);
                                   
                                            list[0] = Boolean.TRUE;
                                            list[1] = js.toString();
                                 
                             } else if (type.equalsIgnoreCase(SJMessageConstants.MessageType.DUALCON.toString()) && code.equalsIgnoreCase(SJMessageConstants.MessageCode.POST.toString())){
                                 
                                 
                                 
                                 SJResponseMessage sjresp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                                           //sjresp.setCode(SJMessageConstants.MessageCode.PUT);
                                           sjresp.setSourceAddress(SJSSCDSignalChannelMap.GetLocalSSAddr());
                                           //sjresp.setMessageType(SJMessageConstants.MessageType.ACK);
                                           sjresp.setDestinationPort(respPort);
                                           sjresp.setMessageID(js.getInt("msgID"));
                                           
                                           String respMessage = sjresp.createResponseMessage();
                                           
                                           cntrlMsg.sendControlMessage(js.getString("srcAddr"), respPort, respMessage);
                                   
                                           //sjresp.setMessageToken();
                                           //sjresp.setPayload("ok");
                                           
                                           //access service description to get completion time
                                           
                                           /*
                                           JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistryProviderOnly();
                                           
                                           
                                           
                                           Enumeration keysServNames = jsIntServ.keys();
                                           
                                           while(keysServNames.hasMoreElements()){
                                               String keyServName = keysServNames.nextElement().toString();
                                               
                                               JSONObject IndivServ = jsIntServ.getJSONObject(keyServName);
                                               
                                               JSONObject actionDetIndivServ = IndivServ.getJSONObject("action");
                                               
                                               Enumeration keysActDetIndivServ = actionDetIndivServ.keys();
                                               
                                               while(keysActDetIndivServ.hasMoreElements()){
                                                   
                                                   String actIndex = keysActDetIndivServ.nextElement().toString();
                                                   
                                                   JSONObject IndivActs = actionDetIndivServ.getJSONObject(actIndex);
                                                   
                                                   JSONObject AllIndivActParams = IndivActs.getJSONObject("actionParameters");
                                                   
                                                   Enumeration keysAllIndivParams = AllIndivActParams.keys();
                                                   
                                                   while(keysAllIndivParams.hasMoreElements()){
                                                       
                                                       String paramInd = keysAllIndivParams.nextElement().toString();
                                                       
                                                       JSONObject indivParamDet = AllIndivActParams.getJSONObject(paramInd);
                                                       
                                                       if(indivParamDet.getString("name").equalsIgnoreCase("completionTime")){
                                                           
                                                           String time = indivParamDet.getString("value");
                                                           
                                                           sjresp.setPayload(time); 
                                                            String respMessage = sjresp.createResponseMessage();

                                                            cntrlMsg.sendControlMessage(js.getString("srcAddr"), respPort, respMessage);
                                                           
                                                       }
                                                       
                                                   }
                                                   
                                               }
                                               
                                           }
                                           */
                                          
                                   //ControlMsgBuffer.AddCntrlMsgToBuffer(servName, js);
                                 
                                   list[0] = Boolean.TRUE;           
                                   list[1] = js.toString();
                                   
                                   
                             } else {
                                 list[0] = Boolean.FALSE;           
                                   //list[1] = js.toString();
                                   if (debug==1) System.err.println("SOAActuatorControlMessage: Not a correct actuator request message ");
                             } 
    
                   //    } 
                    } catch (JSONException jex){
                        if (debug==1) System.err.println("UDPSOAReceiver, capesensor: " +jex.getMessage());
                        list[0] = Boolean.FALSE;      
                    }
                    
                    
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        
                    }
      //  }
                        super.setBuffer(list);
                    
                } catch (SocketTimeoutException ste){
                    
                    list[0] = Boolean.FALSE;
                    super.setBuffer(list);
                    
                                if(!active){
                                     //System.out.println("UDPSOAReceiver connection port:" +reqPort+ " is suspended!");
                                }
                                
                                while(!active){
                                    //try {
                                       
                                        //Thread.sleep(100);
                                        
                                    //} catch (InterruptedException ex) {


                                    //}
                                }
                    
                }

                        //while (true){
                            
                       // }
                        //here
                        //msg = cntrlMsg.receiveControlMessageWithServiceTypeAndActionName(reqPort).trim();
                        
                       
                        
                        
               
                    
                }  catch (Exception ex){
                    list[0] = Boolean.FALSE;
                    ex.printStackTrace();
                    super.setBuffer(list);
                }
                
                    //if (SJServiceRegistry.getParsingStatus()){
                        
                        
                    
                    
                
            }
                
                
                    
                    //SOA transceiver . receive and acquire sensor, transfer to signal sender
                        
                    //}
            
        }
        
        /*
        private void UDPMulticastResponseSender(String ipAddr, String message){
        
        int infoDebug=0;
        byte data[];
        byte packet[] = new byte[1024];
        String response="{}";

        try
                               {
                                  
                                  byte[] msg = new byte[1024];
                                  //ipAddress=InetAddress.getByName("192.168.1.255"); //assumed broadcast address
                                  //ipAddress = InetAddress.getByName(super.buffer[1].toString());
                                  //T2=System.currentTimeMillis();
                                  //if (T2-SJServiceRegistry.getRecordedAdvertisementTimeStamp()>=SJServiceRegistry.getOwnAdvertisementTimeLimit()) //the value need to be xtracted from the description, this is used for advertisement
                                  //{
                                     if (infoDebug==1) System.out.println("AdvertiseMessageSending, Stage 1");
                                      //JSONObject js =SJServiceRegistry.ConstructAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()));
                                      InetAddress ipAddress = InetAddress.getByName(ipAddr);
                                      
                                       //if (ipAddress==null){
                                       //    ipAddress = getBroadcastAddress();
                                       //}
                                       //if (infoDebug==1) System.out.println("AdvertiseMessageSending, Stage 2");
                                      //String message= sjdisc.ConstructReAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()));
                                              //SJServiceRegistry.ConstructReAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit())).toString();
                                      //DatagramSocket s = new DatagramSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("AdvertisementMessage")));
                                      //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                                      //MulticastSocket s = new MulticastSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("AdvertiseMessage")));
                                       //if (infoDebug==1) System.out.println("AdvertiseMessageSending, Stage 3");     
                                      MulticastSocket s = new MulticastSocket(port);
                                           
                                      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                      ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                                           //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                                      out.writeObject(message); //put service description to be sent to remote devices
                                      out.flush();
                                      msg = byteStream.toByteArray();
                                      out.close();
                                      
                                      //System.out.println("AdvertiseMessageSending, Stage 4");
                                      
                                      DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, port);
                                      if (infoDebug ==1 ) System.out.println("Sending unicastQueryResponse Message");
                                      s.send(hi);
                                      if (infoDebug ==1 )System.out.println("data has been sent!");
                                      
                                      
                                      //SJServiceRegistry.AcknowledgeAdvertisementMessageSent(true);
                                      //SJServiceRegistry.RecordAdvertisementTimeStamp(); //works as and acknowledgement too that message has been sent
                                      //System.out.println("AdvertiseMessageSending, Stage 5");
                                      s.close();
                                     //}
                               }
                               catch (java.net.SocketTimeoutException e)
                               {
                                       System.out.println("ActuatorAvailibilityChecker, Timeout when connecting to ip: " + ipAddr + " port :" +port );
                               }
                               catch (java.lang.NullPointerException nex)
                               {
                                    System.out.println("ActuatorAvailibilityChecker,Already left the network: " +nex.getMessage());
                               }
                               catch (Exception e)
                               {
                                       System.out.println("ActuatorAvailibilityChecker,Problem when connecting to ip: " + ipAddr + " port :" +port);
                                       e.printStackTrace();
                               }
        //return response;
        
    }
       */ 


	public UDPSOAReceiver(){
		super();
	}

	private InetAddress address;
	private int reqPort, respPort;
	private Serializer se = null;
	private int buffer_length = 64000;
	//private MulticastSocket socket = null;
        private boolean queryService;
        //private String servName;
        //private boolean IsDoubleACK;
}       
