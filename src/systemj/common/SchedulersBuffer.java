/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common;

import java.util.Vector;

/**
 *
 * @author Udayanto
 */
public class SchedulersBuffer {
    
    private static Vector SchedulerObjs = new Vector();
    private final static Object SchedulerObjsLock = new Object();
    
    public static void SaveSchedulers(Vector scs){
        synchronized(SchedulerObjsLock){
            SchedulerObjs = scs;
        }
    }
    
    public static Vector ObtainSchedulers(){
        synchronized(SchedulerObjsLock){
            return SchedulerObjs;
        }
    }
    
}
