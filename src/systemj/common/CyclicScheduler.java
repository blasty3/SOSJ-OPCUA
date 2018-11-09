package systemj.common;

import java.util.Vector;

import systemj.bootstrap.ClockDomain;
import systemj.interfaces.Scheduler;

/**
 * This is a simple cyclic scheduler
 * 
 * Must be compatible with CLDC 1.1
 * 
 * @author hpar081
 *
 */
public class CyclicScheduler extends Scheduler{
	private Vector cdarray = new Vector();
       
	
	//@Override
	public void addClockDomain(ClockDomain cd) {
		cdarray.addElement(cd);
	}
        
        public void removeClockDomain (ClockDomain cd){
            
           
                //cdarray.removeElement(cd);
            
            
        }
        
        public void removeClockDomain(String cdName){
            
            for(int i=0;i<cdarray.size();i++){
                
                ClockDomain cd = (ClockDomain)cdarray.get(i);
                
                if(cd.getName().equals(cdName)){
                    
                    cdarray.remove(i);
                    
                }
                
            }
            
        }
        
        public Vector getAllClockDomain(){
            return cdarray;
        }
        
        public ClockDomain getClockDomain(String CDName){
            
            ClockDomain cdRet = null;
            
            for(int i=0;i<cdarray.size();i++){
                ClockDomain cd = (ClockDomain)cdarray.get(i);
                
                if(cd.getName().equalsIgnoreCase(CDName)){
                    cdRet = cd;
                }
                
            }
            
            return cdRet;
        }
        
        public void updateClockDomain(ClockDomain cd, String cdname){
            
            for(int i=0;i<cdarray.size();i++){
                ClockDomain CD = (ClockDomain)cdarray.get(i);
                
                if(CD.getName().equalsIgnoreCase(cdname)){
                    cdarray.remove(i);
                    cdarray.addElement(cd);
                }
                
            }
            
        }
	

	//@Override
	public void addArguments(String args) {
		// Non
	}

	//@Override
	public void run() {
            
            if(cdarray.size()>0){
                for(int i=0;i<cdarray.size();i++){
			tick((ClockDomain)cdarray.elementAt(i));
                        
                        //System.out.println("CyclicScheduler, CDArray no: " +i);
		}
            }
            
	}
        
        public int getClockDomainAmount(){
            return cdarray.size();
        }

    //@Override
    public ClockDomain getClockDomainOfIndex(int i) {
        return (ClockDomain)cdarray.get(i);
    }

    //@Override
    public boolean SchedulerHasCD(String cdName) {
       
        boolean stat=false;
        
        for(int i=0;i<cdarray.size();i++){
                ClockDomain cd = (ClockDomain)cdarray.get(i);
                
                if(cd.getName().equalsIgnoreCase(cdName)){
                    stat=true;
                    //System.out.println("CDName is in scheduler");
                }
                
            }
        
        return stat;
    }

    

}
