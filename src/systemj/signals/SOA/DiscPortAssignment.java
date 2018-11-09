/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.util.Hashtable;

/**
 *
 * @author Atmojo
 */
public class DiscPortAssignment {
    
    private static Hashtable DiscPortAssignment = new Hashtable();
    private static Object DiscPortAssignmentLock = new Object();
    
    public static void SetDiscPortAssignment(String OSignalName, int assignedPort){
        synchronized(DiscPortAssignmentLock){
            DiscPortAssignment.put(OSignalName, assignedPort);
        }
    }
    
    public static int GetDiscPortAssignment(String OSignalName){
        synchronized(DiscPortAssignmentLock){
            int portNum = (int)DiscPortAssignment.get(OSignalName);
            DiscPortAssignment.remove(OSignalName);
            return portNum;
        }
    }
    
    public static boolean IsDiscPortAssignmentExist(String OSignalName){
        synchronized(DiscPortAssignmentLock){
            if(DiscPortAssignment.containsKey(OSignalName)){
                return true;
            } else {
                return false;
            }
        }
    }
    
}
