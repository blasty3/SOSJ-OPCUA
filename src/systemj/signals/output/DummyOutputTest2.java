/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.signals.output;

import java.util.Hashtable;
import systemj.interfaces.GenericSignalSender;

/**
 *
 * @author Udayanto
 */
public class DummyOutputTest2 extends GenericSignalSender{

    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
    }

    @Override
    public void run() {
        Object[] obj = super.buffer;
		String data = (String) obj[1];
                
                System.out.println("Dummy Output Test 2 Output!");
    }
    
}
