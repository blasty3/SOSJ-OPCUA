package systemj.bootstrap;

import java.io.Serializable;

/**
 * SystemJ program class </p>
 *
 * @Modified_by Heejong Park and Udayanto Dwi Atmojo
 */
public abstract class ClockDomain implements Runnable,Serializable
{
	public ClockDomain(){}
	public String getName(){ return name;}
	public void setName(String n){ name = n;}
        public String getState(){ return state;}
	public void setState(String n){ state = n;}
        //public void setSleepTimeDet(long startTime,long sleepTime){startSleepTime=startTime;sleepTimeAmount=sleepTime;}
        //public long getStartSleepTime(){return startSleepTime;}
        //public long getSleepTimeAmount(){return sleepTimeAmount;}
        //public void resetSleepTimeState(){startSleepTime=0;sleepTimeAmount=0;}
        
        /*
        public boolean IsTimedSleep(){
            if(getState().equals("Sleep")){
                if(getStartSleepTime()==0 && getSleepTimeAmount()==0){
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
                
        }
        */
        
	private String name;
        private String state;
        //private long startSleepTime;
        //private long sleepTimeAmount;
	public boolean isThreaded(){ return threaded;}
	protected boolean threaded = false;
	public void setThread(){ threaded = true ;}
	public abstract void init();
	public abstract void runClockDomain();

}
