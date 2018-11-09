/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.interfaces.GenericInterface;

/**
 *
 * @author Udayanto
 */
public class TCPIPLinkRegistry {
    
    //allocate TCP port 40001-65535
    
    private static JSONObject PortSSPartnerPair = new JSONObject();
    private final static Object PortSSPartnerPairLock = new Object();
    private static Vector SSListToContact = new Vector(); 
    private final static Object SSListToContactLock = new Object();
    
    
    public static void AddSSToContact(String ssname){
        synchronized(SSListToContactLock){
            
            //only add if the ssname isn't here yet
            
           if(!SSListToContact.contains(ssname)){
               SSListToContact.addElement(ssname);
           }
           
        }
    }
    
    public static Vector GetAllSSToContact(){
        synchronized(SSListToContactLock){
            return SSListToContact;
        }
    }
    
    public static void UpdateAllSSToContact(Vector vec){
        synchronized(SSListToContactLock){
            SSListToContact = vec;
        }
    }
    
    public static void AddSSAndPortPair(String portNum, String ssname){
        synchronized(PortSSPartnerPairLock){
            try {
                PortSSPartnerPair.put(portNum,ssname);
            } catch (JSONException ex) {
               ex.printStackTrace();
            }
        }
    }
    
    public static JSONObject GetSSAndPortPair (){
        synchronized(PortSSPartnerPairLock){
            return PortSSPartnerPair;
        }
    }
    
    public static void removePort(String PortNum){
        synchronized(PortSSPartnerPairLock){
            PortSSPartnerPair.remove(PortNum);
        }
    }
    
    public static boolean IsPortUsed(String portNum){
        synchronized(PortSSPartnerPairLock){
            if(PortSSPartnerPair.has(portNum)){
                return true;
            } else {
                return false;
            }
        }
    }
    
}
