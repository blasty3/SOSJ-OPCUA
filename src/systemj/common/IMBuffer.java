/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common;

/**
 *
 * @author Udayanto
 */
public class IMBuffer {
    
    public static InterfaceManager imObj = new InterfaceManager();
    
    public final static Object imObjLock = new Object();
    
    public static void SaveInterfaceManagerConfig(InterfaceManager im){
        synchronized(imObjLock){
            imObj = im;
        }
    }
    
    public static InterfaceManager getInterfaceManagerConfig(){
        synchronized(imObjLock){
            return imObj;
        }
    }
    
}
