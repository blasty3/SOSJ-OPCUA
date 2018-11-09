/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

/**
 *
 * @author Udayanto
 */
public class ConnStatMem {
    
    private static String connStat="NotConnected";
    private static final Object connStatLock = new Object();
    
    public static String getConnStat(){
        
        String ConnStat;

        synchronized (connStatLock){

            ConnStat = connStat;
        }
        
        return ConnStat;
        
    }
    
    public static void setConnStat(String ConnStat){
        
        synchronized (connStatLock){
            connStat = ConnStat;
        }
        
    }
    
}
