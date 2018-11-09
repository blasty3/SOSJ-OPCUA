/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author Atmojo
 */
public class RegAllSSAddr {
    
    private static JSONObject AllSSAddr = new JSONObject();
    private final static Object AllSSAddrLock = new Object();
    
     public static void AddSSAddr(String SSName, String SSAddr){
        synchronized(AllSSAddrLock){
            try {
                AllSSAddr.put(SSName, SSAddr);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static String getSSAddrOfSSName(String SSName){
        synchronized (AllSSAddrLock){
            try {
                String SSAddr = AllSSAddr.getString(SSName);
                return SSAddr;
            } catch (JSONException ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }
    
    public static JSONObject getAllSSAddr(){
        synchronized (AllSSAddrLock){
           return AllSSAddr;
        }
    }
    /*
    public static String getAllSSAddrInString(){
        synchronized (AllSSAddrLock){
           return AllSSAddr.toString();
        }
    }
    */
    
    public static void removeSSAddrOfSSName(String SSName){
        synchronized (AllSSAddr){
            AllSSAddr.remove(SSName);
        }
    }
    
}
