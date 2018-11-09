package systemj.interfaces;

import java.util.Hashtable;

public abstract class GenericSignalSender implements Runnable
{
	public String name;
	public String cdname;
	/**
	 * This two element Object array represents SystemJ signal</br></br>
	 * buffer[0] contains signal status</br>
	 * buffer[1] contains signal value (any Java Object)
	 * @since 18/06/2012
	 * @author Heejong Park
	 */
	protected Object[] buffer = new Object[2];

	/**
	 * Preparing the buffer to be sent
	 * 
	 * @param obj - Object to send
	 * @return false if an exception occurred (e.g. no receiver found) true otherwise
	 * @author Heejong Park 
	 */
	public boolean setup(Object[] obj){
		if(buffer.length < obj.length)
			buffer = new Object[obj.length];
		for(int i=0; i<obj.length; i++)
			buffer[i] = obj[i];
		
		return true;
	}
	
	/**
	 * Generic type is removed as it is not supported in the old versions of Java
	 * @param data Hashtable containing XML info
	 * @throws RuntimeException
	 */
	public abstract void configure(Hashtable/*<String,String>*/ data) throws RuntimeException;
	
	/**
	 * Actual sending operation implemented
	 * @author Heejong Park
	 */
	public abstract void run();
	
	/**
	 * This method is executed when signal status is FALSE. If there is
	 * nothing to do leave this method unimplemented
	 * 
	 * @author Heejong Park
	 */
	public void arun(){ /* blank */}
}
