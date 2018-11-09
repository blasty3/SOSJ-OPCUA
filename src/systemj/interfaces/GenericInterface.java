package systemj.interfaces;
import java.util.Hashtable;

import systemj.common.BaseInterface;
import systemj.common.InterfaceManager;

/**
 * Must be compatible with CLDC 1.1
 * @author hpar081
 *
 */
public abstract class GenericInterface extends BaseInterface{

    
        protected volatile boolean unterminated = true;
        protected volatile boolean timeoutcomplete = false;
	// Initialization
	public abstract void configure(Hashtable ht);
        
        public abstract void TerminateInterface();
	
	// Executed once after configure(), if receivingThread requires a thread, this method should spawn one.
	public abstract void invokeReceivingThread();
	
	// Client side -------------
	// Executed before transmitData()
	public abstract void setup(Object[] o);
	public abstract boolean transmitData();

        
        
	// Server side -------------
	public abstract void receiveData();
}











