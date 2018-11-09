package systemj.signals.output;

import java.util.Hashtable;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJMessageConstants;
import systemj.common.SJResponseMessage;

import systemj.interfaces.GenericSignalSender;

public class UserLED extends GenericSignalSender {
	//String LEDNumber = "";
	//String Status;
        private boolean IsSOA;
        

	public UserLED(){
		super();
	}
	
	@Override
	public void configure(Hashtable data) throws RuntimeException {
	        
	
	}

	@Override
	public void run() {
		//Object[] obj = super.buffer;
		//String data = (String) obj[1];
		
                EngageLED(null, "1");
                    //EngageLED("led0","1");
                
    
	}
        
        private void SetUserLamp(int lightInt){
            String LEDNumber = null;
            String Status;
            System.out.println("UserLED, lightIntsensor:" +lightInt);
            if (lightInt>=0 && lightInt<100){
                        LEDNumber = "led0123";
                    } else if (lightInt>=100 && lightInt<300){
                        LEDNumber = "led012";
                    } else if (lightInt>=300 && lightInt<400){
                        LEDNumber="led01";
                    } else if (lightInt>=400){
                        LEDNumber = "led0";
                    }
            //LEDNumber="led0123";
            Status="1";
            EngageLED(LEDNumber,Status);
        
        //    EngageLED("led0123","0");
        }
        
        private void EngageLED(String LEDNumber, String Status){
                try{
			
			String command = "./runled.sh "+LEDNumber+" "+Status;
			
			ExecuteCommand(command);
                        
                        
			
                    } catch(Exception e){
			e.printStackTrace();
                    }
        }
	
	private void ExecuteCommand(String comm){
		try {
			Process proc = Runtime.getRuntime().exec(comm);
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null){
				System.out.println(line);
			}
			br.close();
			proc.waitFor();
		} catch (Exception ex){
			System.out.println("Exception" +ex.getMessage());
		}
	}
}
