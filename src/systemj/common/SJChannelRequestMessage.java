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
public class SJChannelRequestMessage extends SJMessage{
    
   // private final SJMessageConstants.MessageCode messageCode;
    //private final SJMessageConstants.MessageType messageType;
    private InetAddress sourceAddress,destinationAddress;
    private int sourcePort,messageID;
    private String SSOrigin;
    private int con; //confirmable
    private String content,payload;
    private JSONObject jsReq = new JSONObject();
    private JSONObject jsPayload = new JSONObject();
    private String destSSName;
    private String destCDName;
    private String destChanName;
    private String srcSSName;
    private String srcCDName;
    private String srcChanName;
    private String respChanName;
    private String action;
    private String data;
     
    
   // public SJRequestMessage(MessageCode messageCode){
   //     super();
    //   this.messageCode = messageCode;
   // }
    
    public SJChannelRequestMessage (){
        super();
        //this.messageCode = messageCode;
        //this.messageType = messageType;
        //super.setMessageCode(messageCode);
        //super.setMessageType(messageType);
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
     
     public void setRequestMessageJSPayload(JSONObject js){
         this.jsPayload = js;
     }
     
     public String getRequestMessagePayload(){
         return payload;
     }
     
     public JSONObject getRequestMessageJSPayload(){
         return jsPayload;
     }
     
     public void setSSOrigin(String SSName){
         this.SSOrigin = SSName;
     }
     
     public String getSSOrigin(){
         return SSOrigin;
     }
     
     public String getDestSSName(){
         return destSSName;
     }
     
     public void setDestSSName(String destSSName){
         this.destSSName = destSSName;
     }
     
     public void setDestCDName(String destCDName){
         this.destCDName = destCDName;
     }
     
     public String getDestCDName(){
         return destCDName;
     }
     
     public void setDestChanName(String destChanName){
         this.destChanName = destChanName;
     }
     
     public String getDestChanName(){
         return destChanName;
     }
     
     public void setSrcSSName(String srcSSName){
         this.srcSSName = srcSSName;
     }
     
     public String getSrcSSName(){
         return srcSSName;
     }
     
     public void setSrcCDName(String srcCDName){
         this.srcCDName = srcCDName;
     }
     
     public String getSrcCDName(){
         return srcCDName;
     }
     
     public void setSrcChanName(String srcChanName){
         this.srcChanName = srcChanName;
     }
     
     public String getSrcChanName(){
         return srcChanName;
     }
     
     public void setRespChanName(String respChanName){
         this.respChanName = respChanName;
     }
     
      public String getRespChanName(){
         return respChanName;
     }
      
      public void setAction(String actionName){
          action = actionName;
      }
     
      public String getAction(){
          return action;
      }
      
      public void setActionData(String data){
          this.data = data;
      }
      
      public String getActionData(){
          return data;
      }
      
     public String createRequestMessage(){
  
        //JSONObject js = new JSONObject();
        try {
            //jsReq.put("msgCode",getMessageCode().toString());
            //jsReq.put("msgType",getMessageType().toString());
            
            //jsReq.put("destSSName",getDestSSName());
            //jsReq.put("destCDName",getDestCDName());
            //jsReq.put("destChanName",getDestChanName());
            //jsReq.put("respAddr",SJSSCDSignalChannelMap.GetLocalSSAddr());
            jsReq.put("respSSName",getSrcSSName());
            jsReq.put("respCDName",getSrcCDName());
            jsReq.put("respChanName",getRespChanName());
            
            if(getAction()!=null){
                jsReq.put("action", getAction());
            }
            
            if(getActionData()!=null){
                jsReq.put("data", getActionData());
            }
            
        } catch (JSONException jex) {
            System.err.println(jex.getMessage());
        }
        return String.format("%s",jsReq.toString());
     }
     
     public String createOldRequestMessage(){
  
        //JSONObject js = new JSONObject();
        try {
            jsReq.put("respSSName",getMessageCode().toString());
            jsReq.put("msgType",getMessageType().toString());
            jsReq.put("srcAddr",getSourceAddress());
            //jsReq.put("srcPort",Integer.toString(getDestinationPort()));
            jsReq.put("reqPort",Integer.toString(getDestinationPort()));
            
            if(getIncomingPort()!=0){
                jsReq.put("respPort", getIncomingPort());
            } else {
                jsReq.put("respPort", getDestinationPort());
            }
            
            //jsReq.put("conf", Integer.toString(getConfirmable()));
            //jsReq.put("msgID",Integer.toString(getMessageID()));
            //jsReq.put("srcSS",getSSOrigin());
       //     jsReq.put("token",Integer.toString(getMessageToken()));
            //System.out.println("jsData Actuator Message: " +getRequestMessagePayloadActuator());
            if (!getRequestMessageJSPayload().isEmpty()){
                
                jsReq.put("payload",getRequestMessageJSPayload());
            }
            
        } catch (JSONException jex) {
            System.err.println(jex.getMessage());
        }
        return String.format("%s",jsReq.toString());
     }

}
