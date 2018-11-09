/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import java.util.Enumeration;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author Atmojo
 */
public class RegAllCDStats {
    
    private static JSONObject RegAllCDStats = new JSONObject();
    private final static Object RegAllCDStatsLock = new Object();
    
    public static void UpdateCDStat(String SSName, JSONObject CDStats){
        synchronized(RegAllCDStatsLock){
            try {
                
                JSONObject newAllCDStats = new JSONObject();
                    
                    Enumeration keysCDStats = CDStats.keys();
                    
                    while(keysCDStats.hasMoreElements()){
                        
                        String CDName = keysCDStats.nextElement().toString();
                        
                        newAllCDStats.put(CDName, CDStats.getString(CDName));
                        
                    }
                    
                    RegAllCDStats.put(SSName, newAllCDStats);
                
                
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static JSONObject getAllCDStatsOfSSName(String SSName){
        
        JSONObject AllCDStats = new JSONObject();
        
        synchronized (RegAllCDStatsLock){
            try {
                AllCDStats  = RegAllCDStats.getJSONObject(SSName);
             
            } catch (JSONException ex) {
                ex.printStackTrace();
               
            }
        }
        
        return AllCDStats;
    }
    
    public static JSONObject getAllCDStats(){
        
        synchronized (RegAllCDStatsLock){
            return RegAllCDStats;
        }
        
    }
    
    
    
    public static void removeAllCDStatInSS(String SSName){
        synchronized (RegAllCDStatsLock){
            RegAllCDStats.remove(SSName);
        }
    }
    
    
    
}
