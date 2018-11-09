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
public class ServiceACKSender extends GenericSignalSender implements Serializable{

     //private String servName;
    
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
        
        
    }

    public void run() {
        
        int debug=1;
        
        //boolean NeedResending=true;
        
        ControlMessageComm cntrlMsg = new ControlMessageComm();
        
        if(SJServiceRegistry.getParsingStatus())
        {

                try {
                
                    JSONObject js = new JSONObject(new JSONTokener(super.buffer[1].toString()));
                
                
                //JSONObject js = ControlMsgBuffer.getCntrlMsgBuffer(servName);
                    
                //int port = Integer.parseInt(js.getString("srcPort"));
                int port = Integer.parseInt(js.getString("respPort"));
            //InetAddress ipAddr = InetAddress.getByName(js.getString("srcAddr"));
                String Addr = js.getString("srcAddr");
 
                    SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                    //sjResp.setMessageToken(Integer.parseInt(js.getString("token")));
            //sjResp.setSourceAddress(InetAddress.getByName(ipAddr));
                    sjResp.setMessageID(js.getInt("msgID"));
            
                    sjResp.setSourceAddress(SJSSCDSignalChannelMap.GetLocalSSAddr());
                    sjResp.setDestinationPort(port);
                    
                    if(Addr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                        Addr = "224.0.0.101";
                    }
                    
                    
                    //JSONObject jsPyld = new JSONObject();
                    
                    //jsPyld.put("payload", "ACK");
                    
                    //sjResp.setPayloadInJSON(js);
                    
                    String msg = sjResp.createResponseMessage();
                       
                        //String msg = sjResp.createResponseMessage(SJMessageConstants.ResponseCode.CONTENT);
                        if (debug==1) {System.out.println("ResponseService, Created Resp Message:" +new JSONObject(new JSONTokener(msg)).toPrettyPrintedString(2, 0));}
            
                        
                        
                        //ControlMessage cntrlMsg = new ControlMessageComm();
                        cntrlMsg.sendControlMessage(Addr, port, msg);
                        //SJServiceRegistry.removeParticularMessageTokensFromBuffer(Integer.parseInt(js.getString("msgID")));
                    
                }  catch (JSONException ex) {
                    ex.printStackTrace();
                }
               
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
