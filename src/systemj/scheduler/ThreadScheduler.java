package systemj.scheduler;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import systemj.bootstrap.ClockDomain;
import systemj.interfaces.Scheduler;

public class ThreadScheduler extends Scheduler{
	private Vector cdarray = new Vector();
	private boolean invoked_thread = false;
	private ScheduledExecutorService executor;
	
	//@Override
	public void addClockDomain(ClockDomain cd) {
		cdarray.addElement(cd);
	}
	
        public void removeClockDomain(String cdName){
            
        }

	//@Override
	public void addArguments(String args) {
		// Non
	}

        public Vector getAllClockDomain(){
            return cdarray;
        }
        
        public void updateClockDomain(ClockDomain cd, String cdname){
            
            
            
            for(int i=0;i<cdarray.size();i++){
                ClockDomain CD = (ClockDomain)cdarray.get(i);
                
                if(CD.getName().equalsIgnoreCase(cdname)){
                    cdarray.removeElementAt(i);
                    cdarray.addElement(cd);
                }
                
            }
            
        }
        
	//@Override
	public void run() {
            
            if(cdarray.size()>0){
                if(!invoked_thread){
			executor = Executors.newScheduledThreadPool(cdarray.size());
			for(int i=0;i<cdarray.size();i++){
				ClockDomain cd = (ClockDomain)cdarray.elementAt(i);
//				cd.setThread();
				executor.scheduleWithFixedDelay(cd, 0, 30, TimeUnit.MICROSECONDS);
			}
			invoked_thread = true;
		}
		else{
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.runInterfaceManager();
		}
                
            }
            
		
	}
        
        public void removeClockDomain (ClockDomain cd){
            cdarray.removeElement(cd);
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
        
        public int getClockDomainAmount(){
            return cdarray.size();
        }
        
        public ClockDomain getClockDomainOfIndex(int i){
            return (ClockDomain)cdarray.get(i);
        }

    @Override
    public boolean SchedulerHasCD(String cdName) {
        
        boolean stat=false;
        
        for(int i=0;i<cdarray.size();i++){
                ClockDomain cd = (ClockDomain)cdarray.get(i);
                
                if(cd.getName().equalsIgnoreCase(cdName)){
                    stat=true;
                }
                
            }
        return stat;
    }
        
}
