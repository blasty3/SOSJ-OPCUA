package systemj.lib;

import java.io.Serializable;

import java.util.*;
import systemj.interfaces.*;
/**
 * 01/11/2011
 * Asynchronous channel object
 * @author HeeJong Park 
 */
public class AChannel extends GenericChannel implements Serializable{
        //private String CDState;
        private String PartnerCDState;
	private final Object LOCK = new Object();
	private int status = 0;
	public AChannel(){}
	public Object getValue(){/*synchronized(this.LOCK){*/return value;/*}*/}
	public void setValue(Object in){
		synchronized(this.LOCK){
			if(status == Integer.MAX_VALUE) 
				status = 1;
			else
				status++; 
			this.value = in;
		} 
		this.modified=true;
	}
	public Object getLock(){return LOCK;}
	public int getStatus(){return status;}
	
        //public void setCDState(String state){CDState = state;}
        public void setPartnerCDState(String state){PartnerCDState = state;}
        public String getPartnerCDState(){return PartnerCDState;}
        //public String getCDState(){;}
        
	// Socket communication
	public synchronized void getBuffer(){
		value = toReceive[1];
		status = ((Integer)toReceive[2]).intValue();
		incoming = false;
	}
	
	public void gethook(){
		if(init && !isLocal){
			if(incoming){
				this.getBuffer();
			}
		}
	}
	public void sethook(){
		if(init && !isLocal){
                    //System.out.println("set hook 1");
			if(this.modified){
                            //System.out.println("set hook 2");
				Object[] toSend = new Object[5];  // Creating an Object!!
				toSend[0] = PartnerName;
				toSend[1] = value;
				toSend[2] = new Integer(status); // Creating an Object!!
                                toSend[4] = Name;
				if(value != null)
					toSend[3] = value;
				if(super.pushToQueue(toSend))
					this.modified = false; // This is set to false ONLY if the data is received by other side
			}
		}
	}
	public void setDistributed(){ isLocal = false;}
        
        
        public void setLocal(){isLocal = true;}
}
