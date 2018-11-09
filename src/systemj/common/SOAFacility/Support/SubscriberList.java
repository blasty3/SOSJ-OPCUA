/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

import org.json.me.JSONObject;

/**
 *
 * @author Udayanto
 */
public class SubscriberList {
    
    private static JSONObject SubscriberListBuffer = new JSONObject();
    private static final Object SubscriberListBufferLock = new Object();
    private static int SubscriberAmount = 1;
    
    
    public static void AddSubscriber(String servProviderName, String servConsumerName, String consumerAddr){
        
        synchronized (SubscriberListBufferLock){
            
        }
        
    }
    
  //  public static JSONObject GetSubscriber(){
        
    //    synchronized (SubscriberListBufferLock){
            
    //    }
   // }
    
}
