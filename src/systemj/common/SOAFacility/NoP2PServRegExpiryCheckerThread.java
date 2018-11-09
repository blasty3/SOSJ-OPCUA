/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJRegistryEntry;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.interfaces.GenericSignalReceiver;
import systemj.lib.Signal;

/**
 *
 * @author Udayanto
 */
public class NoP2PServRegExpiryCheckerThread implements Runnable {

    @Override
    public void run() {
        
        System.out.println("NoP2PServRegExpiryChecker thread started");
        
        while (true){
            
          //  if (SJServiceRegistry.getParsingStatus()){
                
                //Vector ExpiredService = SJRegistryEntry.getExpiredRegistry();
                
                Hashtable allRegExpiryDet = SJRegistryEntry.GetAllRegistryExpiryDet();
                
                JSONObject jsAllAvailRegFromReg = SJRegistryEntry.GetRegistryFromEntry();
                
                //System.out.println("allRegExpiryDet : " +allRegExpiryDet);
                
                Enumeration keysAllReg = allRegExpiryDet.keys();
                
                while(keysAllReg.hasMoreElements()){
                    
                    String regID = keysAllReg.nextElement().toString();
                    
                    Hashtable RegExpiryDet = (Hashtable) allRegExpiryDet.get(regID);
                    
                    long regLoginTime = Long.parseLong((String)RegExpiryDet.get("loginTime"));
                    long expiryTime = Long.parseLong((String)RegExpiryDet.get("expiry"));
                    //long regLoginTime = GetRegistryRegisterTime(regID);
                    //long expiryTime = GetRegistryExpiry(regID);

                    long deltaT = System.currentTimeMillis()-(regLoginTime);
                    
                    if(deltaT>=expiryTime){
                        jsAllAvailRegFromReg.remove(regID);
                        allRegExpiryDet.remove(regID);
                    }
                    
                    
                    // System.out.println("Check expiry of RegID: " +regID+ " Expiry: " +expiryTime+ " LoginTime: " +regLoginTime+ " and difference: " +deltaT );
                    
                }
                
                
                    
                    
                    
                    
                    
                    /*
                    for(int j=0;j<ExpiredService.size();j++){
                        String regID = ExpiredService.get(j).toString();
                        
                        jsAllAvail.remove(regID);
                        allRegExpiryDet.remove(regID);
                        
                    }
                    */
                    SJRegistryEntry.UpdateRegistryEntryWithNewList( jsAllAvailRegFromReg);
                    SJRegistryEntry.UpdateAllRegistryExpiry(allRegExpiryDet);
                    
               
                
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
    
}
