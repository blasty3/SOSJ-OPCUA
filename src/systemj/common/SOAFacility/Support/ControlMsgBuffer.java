/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

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
public class ControlMsgBuffer {
    
    private static JSONObject MsgBuffer = new JSONObject();
    private static final Object MsgBufferLock = new Object();
    
    public static void AddCntrlMsgToBuffer(String serviceName, JSONObject CntrlMsg){
        
        JSONObject js = new JSONObject();
        
        Vector v = new Vector();
        
        try {
            //String msgID = CntrlMsg.getString("msgID");
            
            //js.put(msgID, CntrlMsg);
            
            synchronized (MsgBufferLock){
            
                if (MsgBuffer.has(serviceName)){
                   v = (Vector) MsgBuffer.get(serviceName);
                } 
                v.addElement(CntrlMsg); 
                MsgBuffer.put(serviceName, v);
            
            }
            
        } catch (JSONException ex) {
            System.out.println("ControlMsgBuffer, setCntrlMsgBuffer JSONException: " +ex.getMessage());
        }
        
        
    }
    
    
    public static JSONObject getCntrlMsgBuffer(String serviceName){
        
        Vector res = new Vector();
        
        synchronized(MsgBufferLock){
            try {
                res = (Vector) MsgBuffer.get(serviceName);
            } catch (JSONException ex) {
                System.out.println("ControlMsgBuffer, getCntrlMsgBuffer JSONException: " +ex.getMessage());
            }
        }

        return (JSONObject) res.remove(0);
        
    }
    
}
