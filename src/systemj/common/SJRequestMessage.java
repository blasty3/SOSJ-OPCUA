/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import java.net.InetAddress;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.SJMessageConstants.MessageCode;
import systemj.common.SJMessageConstants.MessageType;

/**
 *
 * @author Udayanto
 */
public class SJRequestMessage extends SJMessage{
    
    private final SJMessageConstants.MessageCode messageCode;
    private final SJMessageConstants.MessageType messageType;
    private InetAddress sourceAddress,destinationAddress;
    private int sourcePort,messageID;
    private String SSOrigin;
    private int con; //confirmable
    private String content,payload;
    private JSONObject jsReq = new JSONObject();
    private JSONObject jsPayloadActuator = new JSONObject();
    
    
   // public SJRequestMessage(MessageCode messageCode){
   //     super();
    //   this.messageCode = messageCode;
   // }
    
    public SJRequestMessage (MessageCode messageCode, MessageType messageType){
        super();
        this.messageCode = messageCode;
        this.messageType = messageType;
        super.setMessageCode(messageCode);
        super.setMessageType(messageType);
    }
    

 //   public void validateBeforeSending() {
//		if (getDestinationAddress() == null)
//			throw new NullPointerException("Destination is null");
//		if (getDestinationPort() == 0)
//			throw new NullPointerException("Destination port is 0");
  //  }
        
    
   // public void setMessageIDIncrement(int ){
  //      this.messageID = 
  //  }
    
     public void setConfirmable(int con){
         this.con = con;
     }  
     
     public int getConfirmable(){
         return con;
     }
    
     public void setRequestMessagePayload(String payload){
         this.payload = payload;
     }
     
     public void setRequestMessagePayloadActuator(JSONObject js){
         this.jsPayloadActuator = js;
     }
     
     public String getRequestMessagePayload(){
         return payload;
     }
     
     public JSONObject getRequestMessagePayloadActuator(){
         return jsPayloadActuator;
     }
     
     public void setSSOrigin(String SSName){
         this.SSOrigin = SSName;
     }
     
     public String getSSOrigin(){
         return SSOrigin;
     }
     
     public String createRequestMessage(){
  
        //JSONObject js = new JSONObject();
        try {
            jsReq.put("msgCode",getMessageCode().toString());
            jsReq.put("msgType",getMessageType().toString());
            jsReq.put("srcAddr",getSourceAddress());
            jsReq.put("reqPort", getDestinationPort());
            if(getIncomingPort()!=0){
                jsReq.put("respPort", getIncomingPort());
            } else {
                jsReq.put("respPort", getDestinationPort());
            }
            
            //jsReq.put("srcPort",Integer.toString(getDestinationPort()));
            jsReq.put("msgID",Integer.toString(getMessageID()));
            jsReq.put("conf", Integer.toString(getConfirmable()));
            //jsReq.put("srcSS",getSSOrigin());
        //    jsReq.put("token",Integer.toString(getMessageToken()));
            
            
            
            if (getRequestMessagePayload()!=null){
                jsReq.put("payload",getRequestMessagePayload());
            } else if (!getRequestMessagePayloadActuator().isEmpty()){
                jsReq.put("payload",getRequestMessagePayloadActuator());
            }
            
        } catch (JSONException jex) {
            System.err.println(jex.getMessage());
        }
        return String.format("%s",jsReq.toString());
     }
     
     public String createActuatorRequestMessage(){
  
        //JSONObject js = new JSONObject();
        try {
            jsReq.put("msgCode",getMessageCode().toString());
            jsReq.put("msgType",getMessageType().toString());
            jsReq.put("srcAddr",getSourceAddress());
            //jsReq.put("srcPort",Integer.toString(getDestinationPort()));
            jsReq.put("reqPort",Integer.toString(getDestinationPort()));
            
            if(getIncomingPort()!=0){
                jsReq.put("respPort", getIncomingPort());
            } else {
                jsReq.put("respPort", getDestinationPort());
            }
            
            jsReq.put("conf", Integer.toString(getConfirmable()));
            jsReq.put("msgID",Integer.toString(getMessageID()));
            //jsReq.put("srcSS",getSSOrigin());
       //     jsReq.put("token",Integer.toString(getMessageToken()));
            //System.out.println("jsData Actuator Message: " +getRequestMessagePayloadActuator());
            if (!getRequestMessagePayloadActuator().isEmpty()){
                
                jsReq.put("payload",getRequestMessagePayloadActuator());
            }
            
        } catch (JSONException jex) {
            System.err.println(jex.getMessage());
        }
        return String.format("%s",jsReq.toString());
     }

}
