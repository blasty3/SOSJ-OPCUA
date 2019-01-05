/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.signals.SOA;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Udayanto
 */
public class DummyInputSignalCurrentTimeMillisTest extends GenericSignalReceiver implements Serializable{

    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
    }

    @Override
    public void run() {
        
        while (active){
            
            Object[] list = new Object[2];
            
            long time = System.currentTimeMillis();
            
            //System.out.println("DummyCurrentTime, Current Time: " +time);
            
            list[0]=Boolean.TRUE;

            list[1]=Long.toString(time);
            
            super.setBuffer(list);
            //try {
            //    Thread.sleep(2000);
            //} catch (InterruptedException ex) {
           //    ex.printStackTrace();
           // }
        }
        
    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
    
}
