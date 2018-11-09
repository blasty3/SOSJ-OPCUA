/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.SJServiceRegistry;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Udayanto
 */
public class ServiceRegistryFetcher extends GenericSignalReceiver implements Serializable{

    //private String registryType;
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
       
        if(data.containsKey("Name")){
            this.name = (String)data.get("Name");
        }
        
    }

    @Override
    public void run() {
        /*
        //System.out.println("Fetcher started");
        
        while (active){
            //System.out.println("SJRegFetcher, Creating object");
            Object[] list = new Object[2];
            
            if (SJServiceRegistry.getParsingStatus())
            {
                //System.out.println("SJRegFetcher, Obtaining reg"); 
                try {
                    JSONObject jsAllCurrServ = SJServiceRegistry.obtainCurrentRegistry();
                    
                    //System.out.println("ServiceRegistryFetcher,obtainCurrentRegistry: " +jsAllCurrServ.toPrettyPrintedString(2, 0));
                    
                    list[0]=Boolean.TRUE;

                    list[1]=jsAllCurrServ.toString();
                    
                } catch (JSONException ex) {
                    
                    System.out.println("Error in grabbing info from service description: " +ex.getMessage());
                    System.exit(1);
                }
            } else {
                System.out.println("SJRegFetcher, Parsing incomplete");
                list[0]=Boolean.FALSE;
                
            }
            
            super.setBuffer(list);
        }
        
        System.out.println("Serv Registry Fetcher terminated in CD: " +cdname);
        */
    }
    
    public ServiceRegistryFetcher(){
        super();
    }
    
    @Override
    public void getBuffer(Object[] obj){
        
         if (SJServiceRegistry.getParsingStatus())
            {
                //System.out.println("SJRegFetcher, Obtaining reg"); 
                try {
                    JSONObject jsAllCurrServ = SJServiceRegistry.obtainCurrentRegistry();
                    
                    //System.out.println("ServiceRegistryFetcher,obtainCurrentRegistry: " +jsAllCurrServ.toPrettyPrintedString(2, 0));
                    
                    obj[0]=Boolean.TRUE;

                    obj[1]=jsAllCurrServ.toString();
                    
                } catch (JSONException ex) {
                    
                    System.out.println("Error in grabbing info from service description: " +ex.getMessage());
                    System.exit(1);
                }
            } else {
                //System.out.println("SJRegFetcher, Parsing incomplete");
                obj[0]=Boolean.FALSE;
                
            }
        
    }
    
}
