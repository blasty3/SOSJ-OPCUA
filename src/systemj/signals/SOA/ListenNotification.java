/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.io.Serializable;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Udayanto
 */
public class ListenNotification extends GenericSignalReceiver implements Serializable{

    String serviceType,action,actionType;
    String serviceName,signalName;
    int timeout;
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if (data.containsKey("associatedServiceName")){
            
            serviceName = (String)data.get("associatedServiceName");
            
        } else {
            throw new RuntimeException("attribute 'associatedServiceName' is needed");
        }
        
        if (data.containsKey("Name")){
            
            signalName = (String)data.get("Name");
            
        } else {
            throw new RuntimeException("attribute 'Name' is needed");
        }
        
        if (data.containsKey("timeout")){
            
            timeout = Integer.parseInt((String)data.get("timeout"));
            
        } else {
            throw new RuntimeException("attribute 'timeout' is needed for the transmit and receive operation");
        }
        
    }

    @Override
    public void run() {
        
    }
    
    public ListenNotification(){
        super();
    }
    
}
