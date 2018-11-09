/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common;

import java.util.Hashtable;
import systemj.bootstrap.ClockDomain;

/**
 *
 * @author Udayanto
 */
public class CDObjectsBuffer {
    
    private static Hashtable SSCDInstances = new Hashtable();
    private final static Object SSCDInstancesLock = new Object();
    private static Hashtable TempStrongMigCDInst = new Hashtable();
    private final static Object TempStrongMigCDInstLock = new Object();
    private static Hashtable TempStrongMigSSAndCDInst = new Hashtable();
    private final static Object TempStrongMigSSAndCDInstLock = new Object();
    private static Hashtable TempWeakMigCDInst = new Hashtable();
    private final static Object TempWeakMigCDInstLock = new Object();
     private static Hashtable TempWeakMigSSAndCDInst = new Hashtable();
    private final static Object TempWeakMigSSAndCDInstLock = new Object();
    
    //private static Hashtable SSCDSCInstances = new Hashtable();
    //private final static Object SSCDSCInstancesLock = new Object();
    
    public static void CopyCDInstancesToBuffer(Hashtable CDInstances){
        synchronized (SSCDInstancesLock){
            SSCDInstances = CDInstances;
        }
    }
    
    public static void AddCDInstancesToBuffer(String cdname,ClockDomain cdins){
        synchronized(SSCDInstancesLock){
            SSCDInstances.put(cdname, cdins);
        }
    }
    
    public static void RemoveCDInstancesFromBuffer(String cdname){
        synchronized(SSCDInstancesLock){
            SSCDInstances.remove(cdname);
        }
    }
    
    public static ClockDomain GetCDInstancesFromBuffer(String cdname){
        synchronized(SSCDInstancesLock){
            return (ClockDomain)SSCDInstances.get(cdname);
        }
    }
    
    public static Hashtable getAllCDInstancesFromBuffer(){
        synchronized (SSCDInstancesLock){
            return SSCDInstances;
        }
    }
    
    public static boolean CDObjBufferHas(String cdName){
       synchronized(SSCDInstancesLock){
           if(SSCDInstances.containsKey(cdName)){
               return true;
           } else {
               return false;
           }
       }
    }
    
    public static boolean IsCDObjBufferEmpty(){
         synchronized(SSCDInstancesLock){
             if(SSCDInstances.size()==0){
                 return true;
             } else {
                 return false;
             }
         }
    }
    
    public static void AddCDObjToTempStrongMigBuffer(String CDName,ClockDomain cdInst){
        synchronized(TempStrongMigCDInstLock){
            TempStrongMigCDInst.put(CDName, cdInst);
        }
    }
    
    public static void GroupSSCDObjsToTempStrongMig(String ssName){
        synchronized(TempStrongMigSSAndCDInstLock){
            TempStrongMigSSAndCDInst.put(ssName, TempStrongMigCDInst);
            TempStrongMigCDInst = new Hashtable();
        }
    }
    
    public static ClockDomain getCDInsFromGroupedTempStrongMigBuffer(String ssName, String CDName){
        synchronized(TempStrongMigSSAndCDInstLock){
            
            
                Hashtable allCDInSS = (Hashtable)TempStrongMigSSAndCDInst.get(ssName);
                
                ClockDomain CDIns = (ClockDomain)allCDInSS.get(CDName);
            
                return CDIns;
            
        }
    }
    
    public static ClockDomain getCDInsFromGroupedTempWeakMigBuffer(String ssName, String CDName){
        synchronized(TempWeakMigSSAndCDInstLock){
            
            
                Hashtable allCDInSS = (Hashtable)TempWeakMigSSAndCDInst.get(ssName);
                
                ClockDomain CDIns = (ClockDomain)allCDInSS.get(CDName);
            
                return CDIns;
            
        }
    }
    
    public static void ModifyCDNameOfCDObjTempStrongMigBuffer(String DesiredCDName, String CurrentCDName){
        synchronized(TempStrongMigCDInstLock){
            
            ClockDomain cd = (ClockDomain)TempStrongMigCDInst.get(CurrentCDName);
            cd.setName(DesiredCDName);
            TempStrongMigCDInst.remove(CurrentCDName);
            TempStrongMigCDInst.put(DesiredCDName, cd);
            
        }
    }
    
    public static boolean TempStrongMigBufferHasEntry(String CDEntryName){
        synchronized(TempStrongMigCDInstLock){
            if(TempStrongMigCDInst.containsKey(CDEntryName)){
                return true;
            } else{
                return false;
            }
        }
    }
    
    public static boolean IsTempStrongMigBufferEmpty(){
        
        synchronized(TempStrongMigCDInstLock){
            
            if(TempStrongMigCDInst.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static Hashtable TakeAllCDObjFromTempStrongMigBuffer(){
        synchronized(TempStrongMigCDInstLock){
            Hashtable hash = TempStrongMigCDInst;
            TempStrongMigCDInst = new Hashtable();
            return hash;
        }
        
    }
    
    public static Hashtable GetAllCDObjFromTempStrongMigBuffer(){
        synchronized(TempStrongMigCDInstLock){
             return TempStrongMigCDInst;
            
        }
        
    }
    
    public static void AddCDObjToTempWeakMigBuffer(String CDName,ClockDomain cdInst){
        synchronized(TempWeakMigCDInstLock){
            
            TempWeakMigCDInst.put(CDName, cdInst);
            
        }
    }
    
    public static void GroupSSCDObjsToTempWeakMig(String ssName){
        synchronized(TempWeakMigSSAndCDInstLock){
            TempWeakMigSSAndCDInst.put(ssName, TempWeakMigCDInst);
            TempWeakMigCDInst = new Hashtable();
        }
    }
    
     public static Hashtable GetGroupSSCDObjStrongMig(){
        synchronized(TempStrongMigSSAndCDInstLock){
            return TempStrongMigSSAndCDInst;
        }
    }
    
    public static Hashtable GetGroupSSCDObjWeakMig(){
        synchronized(TempWeakMigSSAndCDInstLock){
            return TempWeakMigSSAndCDInst;
        }
    }
    
    public static void ModifyCDNameOfCDObjTempWeakMigBuffer(String DesiredCDName, String CurrentCDName){
        synchronized(TempWeakMigCDInstLock){
            
            ClockDomain cd = (ClockDomain)TempWeakMigCDInst.get(CurrentCDName);
            cd.setName(DesiredCDName);
            TempWeakMigCDInst.remove(CurrentCDName);
            TempWeakMigCDInst.put(DesiredCDName, cd);
            
        }
    }
    
    public static boolean TempWeakMigBufferHasEntry(String CDEntryName){
        synchronized(TempWeakMigCDInstLock){
            if(TempWeakMigCDInst.containsKey(CDEntryName)){
                return true;
            } else{
                return false;
            }
        }
    }
    
    public static boolean IsTempWeakMigBufferEmpty(){
        
        synchronized(TempWeakMigCDInstLock){
            
            if(TempWeakMigCDInst.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static Hashtable TakeAllCDObjFromTempWeakMigBuffer(){
        synchronized(TempWeakMigCDInstLock){
            Hashtable hash = TempWeakMigCDInst;
            TempWeakMigCDInst = new Hashtable();
            return hash;
        }
        
    }
    
    public static Hashtable GetAllCDObjFromTempWeakMigBuffer(){
        synchronized(TempWeakMigCDInstLock){
            Hashtable hash = TempWeakMigCDInst;
            
            return hash;
        }
        
    }
    
    /*
    public static void CopySCCDInstancesToMap(Hashtable CDInstances){
        synchronized (SSCDSCInstancesLock){
            SSCDSCInstances = CDInstances;
        }
    }
    
    public static void UpdateSCCDInstancesMap(String cdname,ClockDomain cdins){
        synchronized(SSCDSCInstancesLock){
            SSCDSCInstances.put(cdname, cdins);
        }
    }
    
    public static void RemoveSCCDInstancesMap(String cdname){
        synchronized(SSCDSCInstancesLock){
            SSCDSCInstances.remove(cdname);
        }
    }
    
    public static ClockDomain GetSCCDInstancesFromMap(String cdname){
        synchronized(SSCDSCInstancesLock){
            return (ClockDomain)SSCDSCInstances.get(cdname);
        }
    }
    
    public static Hashtable getAllSCCDInstancesFromMap(){
        synchronized (SSCDSCInstancesLock){
            return SSCDSCInstances;
        }
    }
    
    public static void addSCCDInstancesToMap(String cdname, ClockDomain cd){
        synchronized(SSCDSCInstancesLock){
            SSCDSCInstances.put(cdname, cd);
        }
    }
    
    public static void clearSCCDInstancesFromMap(){
        synchronized(SSCDSCInstancesLock){
            SSCDSCInstances = new Hashtable();
        }
    }
    */
    
}
