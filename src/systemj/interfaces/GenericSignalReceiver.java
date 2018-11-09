package systemj.interfaces;

import java.util.Hashtable;

public abstract class GenericSignalReceiver implements Runnable
{
	public String name;
	public String cdname;
        protected volatile boolean active = true;
        protected volatile boolean terminated = false;
	/**
	 * This two element Object array represents SystemJ signal</br></br>
	 * buffer[0] contains signal status</br>
	 * buffer[1] contains signal value (any Java Object)
	 * 
	 * Depends on its usage (eg. for channel) size can grow to store other information
	 * @since 18/06/2012
	 * @author Heejong Park
	 */
	protected Object[] buffer = new Object[2];
	
	/**
	 * This method must be called from any subclasses constructor
	 */
	public GenericSignalReceiver(){
		buffer[0] = Boolean.FALSE;
	}
	public synchronized void getBuffer(Object[] obj){
		for(int i=0;i<buffer.length; i++)
			obj[i] = buffer[i];
		buffer[0] = Boolean.FALSE; // After reading always set the signal status to false
	}
	public synchronized void setBuffer(Object[] obj){
		if(buffer.length < obj.length)
			buffer = new Object[obj.length];
		for(int i=0;i<obj.length; i++)
			buffer[i] = obj[i];
	}
        
        public synchronized void suspendInputSignalThread(){
            active=false;
            //System.out.println("Signal name: " +name+"in CD: " +cdname+" is terminated");
        }
        
         public synchronized void killInputSignalThread(){
            terminated=true;
            //System.out.println("Signal name: " +name+"in CD: " +cdname+" is terminated");
        }
         
         public synchronized void wakeupInputSignalThread(){
             active=true;
         }

	
	/**
	 * Generic type is removed as it is not supported in the old versions of Java
	 * @param data Hashtable containing XML info
	 * @throws RuntimeException
	 */
	public abstract void configure(Hashtable/*<String,String>*/ data) throws RuntimeException;
	
	public abstract void run() ;
}
