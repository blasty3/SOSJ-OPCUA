package systemj.lib;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import systemj.bootstrap.SystemJProgram;
import systemj.interfaces.*;
public class Signal implements Serializable{
	private boolean status = false;
	public Object value = null;
	public Object pre_val = null;
	public boolean pre_status = false;
	private Signal partner = null;
	private GenericSignalReceiver server;
	private GenericSignalSender client;
	private Object[] toSend = new Object[2];
	private Object[] toReceive = new Object[2];
	private boolean init = false;
        private Thread GSRThr;
        private static final Object threadLock = new Object();
        private boolean pause = false;

	public Signal(){}
	public boolean isInit(){ return init; }
	public void setInit(){ init = true; }
        
        public void disableInit(){ init = false; }
        
	public Signal(GenericSignalReceiver s, GenericSignalSender c){
		this.server=s; this.client=c;
	}
	public void setServer(GenericSignalReceiver gsr){ server = gsr; }
	public void setClient(GenericSignalSender gss){ client = gss; }
        
        public GenericSignalReceiver getServer(){
            return server;
        }
        
        public GenericSignalSender getClient(){
            return client;
        } 
        
        public void nullifyServer(){
            this.server=null;
        }
        
        public void nullifyClient(){
            this.client=null;
        }
        
        //private void storeThrObj(Thread th){
        //    GSRThr = th;
        //}
        /*
        public void pauseGSRTh(){
           
            
            synchronized(threadLock){
                try {
                    GSRThr.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            
        }
        
        public synchronized void restartGSRTh(){
           synchronized(threadLock){
               notify();
           }
        }
        */
        
	public void setuphook(){
		try{
                    //GSRThr = new Thread(server);
                    Thread th1 = new Thread(server);
                    th1.start();
                    
                    //GSRThr = th1;
                    
                    //GSRThr.start();
                    
                }
		catch(Exception e){e.printStackTrace();}
	}
        
        public void suspendInputSignalThread(){
            server.suspendInputSignalThread();
            //if (GSRThr!=null)
            //GSRThr.interrupt();
        }
        
        public void killInputSignalThread(){
            server.killInputSignalThread();
        }
        
        public void wakeupInputSignalThread(){
            server.wakeupInputSignalThread();
        }
	
	public void gethook(){
		if(server != null){
			server.execute();
			server.getBuffer(toReceive);
			if(((Boolean)toReceive[0]).booleanValue()){
				this.status = true;
				this.value = toReceive[1];
                                //System.out.println("Signal, GSR server config null");
			}
			else
				this.status = false;
                        //System.out.println("Signal, status false");
		} else {
                    this.status = false;
                } 
                //else {
               //     System.out.println("Signal, GSR server config null");
               // }
	}
        
	
	public void sethook(){
		if(client != null){
			if(status) {
				toSend[0] = Boolean.TRUE;
				toSend[1] = value;
				if(client.setup(toSend))
					client.run();
			}
			else{
				client.arun();
			}
		}
	}
	public void setPresent(){
		this.status = true;
	}
	public void setClear(){
		this.status = false;
	}
	public boolean getStatus(){
		return status;
	}
	public void setValue(Object value){
		this.value = value;
	}
	public Object getValue(){
		return value;
	}
	public Object getpreval(){
		return pre_val ;
	}
	public void setpreval(Object ob){pre_val = ob; if(partner != null) partner.pre_val  = ob; }
	public int setprepresent() {pre_status = true; if(partner != null) partner.pre_status = true; return 0;}
	public int setpreclear() {pre_status = false; if(partner != null) partner.pre_status = false; return 0;}
	public boolean getprestatus(){return pre_status;}
}