/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJMessageConstants;
import systemj.common.SJResponseMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.ControlMsgBuffer;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.interfaces.GenericSignalSender;

/**
 *
 * @author Udayanto
 */
public class ServiceRequestResponder extends GenericSignalSender implements Serializable{

     private boolean IsDoubleACK=false;
    // private String servName;
     private int timeout;
    
    public void configure(Hashtable data) throws RuntimeException {
        
       /*
        
        if (data.containsKey("IsDoubleACK")){
            IsDoubleACK=Boolean.parseBoolean((String)data.get("IsDoubleACK"));
            
            if (IsDoubleACK){
                
                if (data.containsKey("timeout")){
                    timeout=Integer.parseInt((String)data.get("timeout"));
                
                } else {
                    throw new RuntimeException("ServiceRequestResponder, requires 'timeout' attribute, needs to be at least 3 times the consumer's timeout to wait ack from the actuator provider");
                }
            }  
          } else {
            throw new RuntimeException("ServiceRequestResponder, requires 'IsDoubleACK' attribute, choose 'true' or 'false'");
          }
        */
        /*
        if(data.containsKey("associatedServiceName"))
		{
			servName= (String)data.get("associatedServiceName");
		} else {
                    throw new RuntimeException("'associatedServiceName' number attribute needs to be defined");
                }
        */
       
        
        
    }

    public void run() {
        
        int debug=1;
        
        boolean NeedResending=true;
        
        ControlMessageComm cntrlMsg = new ControlMessageComm();
        
        if(SJServiceRegistry.getParsingStatus())
        {
  
                try {
                
                    String msg = super.buffer[1].toString();
                    
                JSONObject js = new JSONObject(new JSONTokener(msg));
                
                //JSONObject js = ControlMsgBuffer.getCntrlMsgBuffer(servName);
                    
                //int port = Integer.parseInt(js.getString("srcPort"));
                int port = Integer.parseInt(js.getString("respPort"));
            //InetAddress ipAddr = InetAddress.getByName(js.getString("srcAddr"));
                String ipAddr = js.getString("srcAddr");
                js.put("srcAddr", SJSSCDSignalChannelMap.GetLocalSSAddr());
 
                //if (js.has("rspCode")){
                    
                //    cntrlMsg.sendControlMessage(ipAddr, port, js.toString());
                    
                //} else
                
                    
                    //if (js.has("payload")){
                    //String msgPayload = js.getString("payload");
                /*
                    SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                    //sjResp.setMessageToken(Integer.parseInt(js.getString("token")));
            //sjResp.setSourceAddress(InetAddress.getByName(ipAddr));
                    sjResp.setMessageID(js.getInt("msgID"));
            
                    sjResp.setSourceAddress(SJServiceRegistry.getOwnIPAddressFromRegistry());
                    sjResp.setDestinationPort(port);
                    sjResp.setPayloadInJSON(js.getJSONObject("payload")); //resolve
                      
                     
                        
                        //String msgCode = js.getString("msgCode");
                        
                        
                        
                        
                        String    msg = sjResp.createResponseMessage();
                        */
                        
                        
                        //String msg = sjResp.createResponseMessage(SJMessageConstants.ResponseCode.CONTENT);
                        if (debug==1) {System.out.println("ServiceRequestRespond, Resp Message:" +js.toPrettyPrintedString(2, 0));}
            
                        if(ipAddr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                            ipAddr = "224.0.0.101";
                        }
                        
                        //ControlMessage cntrlMsg = new ControlMessageComm();
                        cntrlMsg.sendControlMessage(ipAddr, port, js.toString());
                        //SJServiceRegistry.removeParticularMessageTokensFromBuffer(Integer.parseInt(js.getString("msgID")));
                    
                }  catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            
          //  if (super.buffer[1].toString().startsWith("{")){
                
                
                
          //  } else {
                
                //super.buffer[1].
                
                /*
                
                String str = super.buffer[1].toString();
                
                if (str.equalsIgnoreCase("success")){
                   
                   SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.NON);
                    sjResp.setMessageToken();
            //sjResp.setSourceAddress(InetAddress.getByName(ipAddr));
                    sjResp.setMessageID();
            
                    sjResp.setSourceAddress(ipAddr);
                    sjResp.setDestinationPort(port);
                    sjResp.setPayload(js.getString("payload"));
                    
                }
                * */
                
          //  }
                
            
            
            
        }
        
    }
    
    
	public boolean setup(Object[] obj)
	{
		try
		{

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
    
}
