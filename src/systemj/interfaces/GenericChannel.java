package systemj.interfaces;

import java.util.Vector;
import systemj.common.BaseInterface;

public abstract class GenericChannel extends BaseInterface{
    
        protected boolean distStateChanged = false;
        
	protected boolean modified = false;
        protected boolean modified_chan = false;
        protected boolean chanReconfigureFlag = false;
        protected boolean chanReconfigured = false;
	protected boolean init = false;
	protected Object value;
	public String PartnerName;
	public String Name;
	
	public GenericChannel() {}
	public boolean isInit(){ return init; }
	public void setInit(){ init = true; }
        
        public void disableInit(){ init = false;}
	
        public String getName(){
            return Name;
        }
        
        //public void setDisableInit() { init = false;}
        
	// Modular
        protected Vector toReceiveBuffer = new Vector();
	protected Object[] toReceive = new Object[5];
        
	protected boolean incoming = false;
        protected boolean stateChangeIncoming = false;
        protected Object[] stateChangetoReceive = new Object[3];
	
	// To be called by GenericInterface (called by InterfaceManager)
	public synchronized void setBuffer(Object[] obj){
		if(toReceive.length < obj.length || toReceive.length>obj.length){
			toReceive = new Object[obj.length];
                }
                
		for(int i=0;i<obj.length; i++){
			toReceive[i] = obj[i];
                        //System.out.println("Generic Chan, toReceive data i: " +toReceive[i]);
                }
                
                toReceiveBuffer.addElement(toReceive);
                
		incoming = true;
                
	}
        
        /*
        public synchronized void setStateChangeBuffer(Object[] obj){
		if(stateChangetoReceive.length < obj.length)
			stateChangetoReceive = new Object[obj.length];
		for(int i=0;i<obj.length; i++)
			stateChangetoReceive[i] = obj[i];
		stateChangeIncoming = true;
	}
        */
        
	// SMCHAN
	protected boolean isLocal = true;
        
        
        //Added channel life status following CD state
        
        
        
}
