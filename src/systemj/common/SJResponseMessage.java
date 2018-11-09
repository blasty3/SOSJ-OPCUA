/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;


import systemj.common.SJMessageConstants.MessageType;
//import systemj.common.SJMessageConstants.ResponseCode;

/**
 *
 * @author Udayanto
 */

//create response messages, assume always piggy-backed re

public class SJResponseMessage extends SJMessage{
    
    //private final SJMessageConstants.ResponseCode responseCode;
    private final SJMessageConstants.MessageType messageType;
    
    //private final MessageType messageType;
    private InetAddress sourceAddress,destinationAddress;
    private int sourcePort,destinationPort,messageID;
    private String payload, SSOrigin;
    private JSONObject JSONpayload = new JSONObject();
   // private int token;
    
    
  //  public SJResponseMessage(ResponseCode responseCode){
        
  //      this.responseCode = responseCode;
  //  }
    
    public SJResponseMessage(MessageType messageType){
         super();
      // this.responseCode = responseCode;
       this.messageType = messageType;
        super.setMessageType(messageType);
    }
    
   // public SJResponseMessage(MessageType messageType){
        
  //      this.messageType = messageType;
  //  }
    
   
    //public SJMessageConstants.ResponseCode getResponseCode(){
    //    return responseCode;
    //}

//    public void validateBeforeSending() {
//		if (getDestinationAddress() == null)
//			throw new NullPointerException("Destination is null");
//		if (getDestinationPort() == 0)
//			throw new NullPointerException("Destination port is 0");
//    }
        
    
   // public void setMessageIDIncrement(int ){
  //      this.messageID = 
  //  }

     public InetAddress getDestinationAddr(String request){
         InetAddress inetAddr=null;
        try {
             JSONObject jsreq = new JSONObject(new JSONTokener(request));
             inetAddr = InetAddress.getByName(jsreq.get("srcAddr").toString());
        } catch (JSONException ex) {
            ex.printStackTrace();
           // Logger.getLogger(SJResponseMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException uhx){
            System.out.println(uhx.getMessage());
        }

         return inetAddr;
     }
     
     public InetAddress getDestinationPort(String request){
         InetAddress inetAddr=null;
        try {
             JSONObject jsreq = new JSONObject(new JSONTokener(request));
             inetAddr = InetAddress.getByName(jsreq.get("respPort").toString());
        } catch (JSONException ex) {
             System.out.println(ex.getMessage());
        } catch (UnknownHostException uhx){
            System.out.println(uhx.getMessage());
        }

         return inetAddr;
     }
     
     
     public void setPayload(String message){
         this.payload=message;
     }
     
      public void setJSONPayload(JSONObject message){
         this.JSONpayload=message;
     }
     
     public String getPayload(){
         return payload;
     }
     
     public JSONObject getJSONPayload(){
         return JSONpayload;
     }
     
     public void setSSOrigin(String SSName){
         this.SSOrigin = SSName;
     }
     
     public String getSSOrigin(){
         return SSOrigin;
     }

     //qeustionable as 11/12/2013
     public String createResponseMessage(){ //b4 there ws response code
         //request message in JSON format
        JSONObject js = new JSONObject();
        
        try {
            //JSONObject jsreq = new JSONObject(new JSONTokener(request));
            
            js.put("msgType",getMessageType().toString());
            //js.put("rspCode",code.toString());
            //js.put("srcSS", getSSOrigin());
            //js.put("msgCode", );
            js.put("srcAddr",getSourceAddress());
            //js.put("srcPort",Integer.toString(getDestinationPort()));
            
            js.put("reqPort",Integer.toString(getDestinationPort()));
            
            if(getIncomingPort()!=0){
                js.put("respPort", getIncomingPort());
            } else {
                js.put("respPort", getDestinationPort());
            }
            
            js.put("msgID", Integer.toString(getMessageID()));
       //     js.put("token",Integer.toString(getMessageToken()));
            if (getPayload()!=null){
                js.put("payload",getPayload());
            }
            
            else if (!getJSONPayload().isEmpty()){
                js.put("payload",getJSONPayload());
            }
            
            System.out.println("SJResponseMessage, created Response Message: " + js.toPrettyPrintedString(2, 0));
            
        } catch (JSONException ex) {
            Logger.getLogger(SJRequestMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return String.format("%s",js.toString());
     }

}
