/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.interfaces.GenericSignalReceiver;
import systemj.lib.Signal;

/**
 *
 * @author Udayanto
 */
public class RegExpiryCheckerThread implements Runnable {

    @Override
    public void run() {
        
        boolean notif = false;
        
        System.out.println("ExpiryChecker thread started");
        
        //int i=0;
        
        while (true){
            
          //  if (SJServiceRegistry.getParsingStatus()){
                
                Hashtable ExpiredService = CheckExpiry();
                
                if(!ExpiredService.isEmpty()){
                    
                    //System.out.println("ExpiredServ: " +ExpiredService);
                    
                    Enumeration keysExpInd = ExpiredService.keys();
                    
                    while(keysExpInd.hasMoreElements()){
                        String indSS = keysExpInd.nextElement().toString();
                        
                        String SSName = (String) ExpiredService.get(indSS);
                        
                        RemoveExpiredService(SSName);
                        RegAllCDStats.removeAllCDStatInSS(SSName);
                        RegAllSSAddr.removeSSAddrOfSSName(SSName);
                        
                        // notify that the SS name is removed, thus at the end of the tick (HK time), the link to that SS should be removed
                        if(!notif){
                            notif = true;
                        }
                        //InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
                        
                        //Interconnection ic = im.getInterconnection();
                        //
                        //ic.AddNonAvailSSForLinkToList(SSName);
                        
                        //
                        
                    }
                    
                    if(notif){
                        SOABuffer.SetRegNotifySS(true);
                        //SOABuffer.SetNotifyChangedCDStat(true);
                        //SOABuffer.SetNotifyChangedTotalSS(true);
                        notif = false;
                    }
                    
                               
                }
               
                /*
                if (!ExpiredService.equalsIgnoreCase("nothing")){
                    RemoveExpiredService(ExpiredService);
                }
                */
 
                try {
                    Thread.sleep(50);
                    
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                /*
                if (i==50){
                    
                    JSONObject hsh = new JSONObject();
                    
                    try {
                        hsh.put("Name","DummyOutputTest");
                        hsh.put("Class","systemj.signals.output.DummyOutputTest2");
                   } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                   
                    //SJSSSignalChannelMap.setLocalSignalChangeLocation("CentralControlMainCD", "TestOutputChangeCD", "output", "DummyOutputTest", hsh);
                    //SJSSSignalChannelMap.setLocalCDChangedFlag();
               //     Object objSig = SJSSSignalChannelMap.getInputSignalClassInstanceToMap("CentralControlMainCD", "acquirePEMidDetectionTimeServiceCD", "GetServiceRegistry");
               //     Object objGSR = SJSSSignalChannelMap.getInputSignalGSRInstanceToMap("CentralControlMainCD", "acquirePEMidDetectionTimeServiceCD", "GetServiceRegistry");
                    //System.out.println("ExpiryChecket sig: " + obj+ "and GSR:" +obj2);
               //     GenericSignalReceiver gsr = (GenericSignalReceiver) objGSR;
               //     Signal signal = (Signal) objSig;
               //     signal.setServer(gsr);
              //      signal.terminateInputSignalThread();
                }     
                */
        }
    }
    
    private void RemoveExpiredService(String ExpiredService){
       // SJServiceRegistry.UpdateServiceRegistryBasedOnServiceExpiry(ExpiredService);
        SJServiceRegistry.RemoveUnavailableNodeServicesFromCurrentRegistryOfSSName(ExpiredService);
    }
    
    private Hashtable CheckExpiry(){
                
            //    if (SJServiceRegistry.getParsingStatus()){
             
                        Hashtable answer = SJServiceRegistry.checkServiceExpiry();
            
                        if (answer.size()==0){
                           // list[0]=Boolean.FALSE;
                           // list[1]="";
                            return new Hashtable();
                        } else {
                            //list[0]=Boolean.TRUE;
                            //list[1]=answer;
                            return answer;
                        }
            
                   
           // } else {
                    //list[0]=Boolean.FALSE;
           //         return new Hashtable();
            //}       
        
        
    }
    
    /*
     private Hashtable CheckExpiry(){
                
                if (SJServiceRegistry.getParsingStatus()){
             
                    if (SJServiceRegistry.HasServiceConsumer()){
                        Hashtable answer = SJServiceRegistry.checkServiceExpiry();
            
                        if (answer.isEmpty()){
                           // list[0]=Boolean.FALSE;
                           // list[1]="";
                            return new Hashtable();
                        } else {
                            //list[0]=Boolean.TRUE;
                            //list[1]=answer;
                            return answer;
                        }
            
                   } else {
                        //list[0]=Boolean.FALSE;
                        return new Hashtable();
                   }
            } else {
                    //list[0]=Boolean.FALSE;
                    return new Hashtable();
            }       
        
        
    }
     */
    
}
