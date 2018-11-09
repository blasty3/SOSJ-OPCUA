package systemj.common.util;

import java.util.Vector;

public class LinkQueue {
	private Vector q = new Vector();
	private static final int MAX_SIZE = 50;
	private int currentsize = 0;

	// Although Vector is thread-safe, use custom synchronized method
	public synchronized boolean push(Object o){
		if(q.size() < MAX_SIZE){
			q.addElement(o);
			currentsize = q.size();
			return true;
		}
		currentsize = q.size();
		return false;
	}
	
	public synchronized Object pop(){
		Object o = null;
		if(q.size() > 0){
			o = q.elementAt(0);
			q.removeElementAt(0);
		}
		currentsize = q.size();
		return o;
	}
	
	public boolean isFull(){ return currentsize > MAX_SIZE; }
	public boolean isEmpty(){ return currentsize == 0; }
	
}
