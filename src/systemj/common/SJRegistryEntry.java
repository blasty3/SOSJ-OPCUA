/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;


/**
 *
 * @author Udayanto
 */
public class SJRegistryEntry {
    
    private static JSONObject currentAvailRegistry = new JSONObject();
    private static final Object currentAvailRegistryLock = new Object();
    
    private static Hashtable availRegistryExpiryDet = new Hashtable();
    private static final Object availRegistryExpiryDetLock = new Object();
    
            
     
    public static void AddRegistryToEntry(JSONObject jsRegAdvMsg){
        
        synchronized(currentAvailRegistryLock){
            
            System.out.println("Saving registry to entry: " +jsRegAdvMsg);
            
            try {
            
            String regID = jsRegAdvMsg.getString("regID");
            String regAddr = jsRegAdvMsg.getString("regAddr");
            System.out.println("Saving registry to entry, regID: " +regID);
            System.out.println("Saving registry to entry, regAddr: " +regAddr);
            
                //if(currentAvailRegistry.has(regID)){
                //    currentAvailRegistry.remove(regID);
                //}
            //if(!currentAvailRegistry.has(regID)){
                currentAvailRegistry.put(regID, regAddr);
                
            //}
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
            
        }
        
    }
    
    public static JSONObject GetRegistryFromEntry(){
        
        synchronized(currentAvailRegistryLock){
            return currentAvailRegistry;
        }
        
    }
    
    public static void RemoveRegistryInEntry(String regID){
        
        synchronized(currentAvailRegistryLock){
            
          //  try {
              
            if(currentAvailRegistry.has(regID)){
                currentAvailRegistry.remove(regID);
            }
            
        //} catch (JSONException ex) {
        //    ex.printStackTrace();
        //}
            
        }
        
    }
    
    public static void UpdateAllRegistryExpiry(Hashtable hash){
        synchronized(availRegistryExpiryDetLock){
            availRegistryExpiryDet = hash;
        }
    }
    
    public static void UpdateRegistryExpiry(JSONObject jsRegAdvMsg){
        
        synchronized(availRegistryExpiryDetLock){
            
            try {
            
            String regID = jsRegAdvMsg.getString("regID");
            String regAddr = jsRegAdvMsg.getString("regAddr");
            String regExpTime = jsRegAdvMsg.getString("retransmissionTime");
            
            //long refTime = 3*(Long.parseLong(regExpTime));
            
           // String StrRefTime = Long.toString(refTime);
            
            if(availRegistryExpiryDet.containsKey(regID)){
                
               Hashtable regExpiryDet = (Hashtable)availRegistryExpiryDet.get(regID);
               
               regExpiryDet.put("loginTime",Long.toString(System.currentTimeMillis()));
               regExpiryDet.put("regAddr",regAddr);
               regExpiryDet.put("expiry",regExpTime);
                
            } else {
                
                 Hashtable regExpiryDet = new Hashtable();
                 
                 regExpiryDet.put("loginTime",Long.toString(System.currentTimeMillis()));
                 regExpiryDet.put("regAddr",regAddr);
                 regExpiryDet.put("expiry",regExpTime);
               
               availRegistryExpiryDet.put(regID, regExpiryDet);
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
            
        }
        
    }
    
    private static Hashtable GetRegistryExpiryDet(String regID){
        synchronized(availRegistryExpiryDetLock){
            return (Hashtable)availRegistryExpiryDet.get(regID);
        }
    }
    
    /*
    private static long GetRegistryExpiry(String regID){
        synchronized(availRegistryExpiryDetLock){
            if(availRegistryExpiryDet.containsKey(regID)){
                Hashtable regDet = (Hashtable)availRegistryExpiryDet.get(regID);
                
                long regExpiry = Long.parseLong(regDet.get("expiry").toString());
                
                return regExpiry;
            } else {
                return 0;
            }
        }
    }
    
    private static long GetRegistryRegisterTime(String regID){
        synchronized(availRegistryExpiryDetLock){
            if(availRegistryExpiryDet.containsKey(regID)){
                Hashtable regDet = (Hashtable)availRegistryExpiryDet.get(regID);
                
                long loginTime = Long.parseLong(regDet.get("loginTime").toString());
                
                return loginTime;
            } else {
                return System.currentTimeMillis();
            }
        }
    }
    */
    
    public static void UpdateRegistryEntryWithNewList(JSONObject js){
        synchronized(currentAvailRegistryLock){
            currentAvailRegistry = js;
        }
    }
    
    public static Hashtable GetAllRegistryExpiryDet(){
        synchronized(availRegistryExpiryDetLock){
            return availRegistryExpiryDet;
        }
    }
    
    public static Vector getExpiredRegistry(){
        
        Vector ExpiredReg = new Vector();
        
        int index=1;
        
        JSONObject allReg = GetRegistryFromEntry();
        
        Enumeration keysAllReg = allReg.keys();
        
        while(keysAllReg.hasMoreElements()){
            String regID = keysAllReg.nextElement().toString();
            
            Hashtable expiryDetOfId = GetRegistryExpiryDet(regID);
            
            long regLoginTime = Long.parseLong((String)expiryDetOfId.get("loginTime"));
            long expiryTime = Long.parseLong((String) expiryDetOfId.get("expiry"));
            //long regLoginTime = GetRegistryRegisterTime(regID);
            //long expiryTime = GetRegistryExpiry(regID);
            
            long deltaT = System.currentTimeMillis()-(regLoginTime);
            
            System.out.println("Check expiry of RegID: " +regID+ " Expiry: " +expiryTime+ " LoginTime: " +regLoginTime+ " and difference: " +deltaT );
            
            if(deltaT>=expiryTime){
               ExpiredReg.addElement(regID);
            }
        }
        
          return ExpiredReg;
          
    }
    
    
    
}
