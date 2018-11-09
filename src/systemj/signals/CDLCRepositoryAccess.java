/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.signals;

import java.io.Serializable;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Udayanto
 */
public class CDLCRepositoryAccess extends GenericSignalReceiver implements Serializable{

    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("Status")){
            
        } else {
            throw new RuntimeException("attribute 'Status' has to be defined. Put value of 'All' to get all CDs status, 'Created','Active','Dormant','Invisible','' to get all CDs in the Created status, ");
        }
        
    }

    @Override
    public void run() {
        
        while(active){
            
            Object[] obj = new Object[2];
            
            
            obj[0] = Boolean.TRUE;
            obj[1] = "";
            
            
            
        }
        
    }
    
}
