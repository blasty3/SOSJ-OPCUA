/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import java.net.InetAddress;
import systemj.common.SJMessageConstants.MessageType;
import systemj.common.SJMessageConstants.MessageCode;
import java.util.Random;



import org.json.me.JSONObject;
import systemj.common.SJMessageConstants;
/**
 *
 * @author Udayanto
 */
public abstract class SJMessage {
    
    private SJMessageConstants.MessageCode messageCode;
    private SJMessageConstants.MessageType messageType;
   // private int token;
    private byte[] PayloadInByte;
    private String payloadInString; 
    private JSONObject payloadInJSON = new JSONObject();
    
    private String sourceAddress;
    private String destinationAddress;
    private int sourcePort,destinationPort,incomingPort,messageID;
    private String content;
    
    public SJMessage(){
        
    }
    
    //public void SetMessageCode(SJMessageConstants.MessageCode messageCode){
    //    this.messageCode = messageCode;
    //}
    
    public MessageCode getMessageCode(){
        return messageCode;
    }
    
    public void setMessageCode(MessageCode code){
       this.messageCode = code;
    }
    
    private int getRandomizedToken(){
        Random rand = new Random();
        return rand.nextInt();
    }
    
   // public void setMessageToken(){
   //     this.token = getRandomizedToken();
   // }
    
   // public void setMessageToken(int token){
   //     this.token = token;
   //  }
    
   // public int getMessageToken(){
  //      return token;
  //  }
    
    
    
    public void setCode(MessageCode code){
        this.messageCode = code;
    }
    
    //public MessageCode getCode(){
    //    return messageCode;
    //}
    
    public void setMessageType(MessageType type){
        this.messageType = type;
    }
    
    public MessageType getMessageType(){
        return messageType;
    }
    
    
    
   //public void setDestinationAddress(String destinationAddress){
   //     this.destinationAddress = destinationAddress;
   // }
    
    public void setDestinationPort(int destinationPort){
        this.destinationPort = destinationPort;
    }
    
   // public String getDestinationAddress(){
   //    return destinationAddress;
  //  }
    
    public int getDestinationPort(){
        return destinationPort;
    }
    
    public void setIncomingPort(int incomingPort){
        this.incomingPort = incomingPort;
    }
    
   // public String getDestinationAddress(){
   //    return destinationAddress;
  //  }
    
    public int getIncomingPort(){
        return incomingPort;
    }
    
     public String getSourceAddress(){
       return sourceAddress;
    }
    
 //   public int getSourcePort(){
 //       return sourcePort;
  //  }

    public void setSourceAddress(String sourceAddress){
        this.sourceAddress = sourceAddress;
    }
    
   // public void setSourcePort(int sourcePort){
   //     this.sourcePort = sourcePort;
   // }
    
    public void setContent(String content){       //or payload in CoAP terms
        this.content = content;
    }
    
    public void setMessageID(){
        Random rand = new Random();
        this.messageID = rand.nextInt();
    }
    
     public void setMessageID(int messageID){
       
        this.messageID = messageID;
    }
    
     public int getMessageID(){
       return messageID;
     }
     
     public void setPayloadInString(String payload){
         this.payloadInString=payload;
     }
     
     public String getPayloadInString() {
		
		return payloadInString;
     }
     
     public JSONObject getPayloadInJSON() {
		
		return payloadInJSON;
	}
     
     public void setPayloadInJSON(JSONObject payloadInJSON){
         this.payloadInJSON=payloadInJSON;
     }
    
    
  //  public int getPayloadSize() {
//		return PayloadInByte == null ? 0 : PayloadInByte.length;
   // }
	
	
      //  public SJMessage setPayload(String payloadString) {
	//	if (payloadString == null)
	//		throw new NullPointerException();
	//	setPayload(payloadString.getBytes());
	//	return this;
	//}
        
        
     //   public SJMessage setPayload(byte[] payloadByte) {
	//	this.PayloadInByte = payloadByte;
	//	this.payloadInString = null; // reset lazy-initialized variable
	//	return this;
	//}
        
    //    public String getPayloadInString() {
		
//		return payloadInString;
//	}
        
        
    //    public byte[] getPayloadInBytes() {
		
//		return PayloadInByte;
//	}
    
    
}
