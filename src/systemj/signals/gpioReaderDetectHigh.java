// gpioReader which has been made for reading GPIO of BeagelBone Black.
// a singal has two value. if it present is saved in list[0]. value is saved in list[1].
// the value should only be "1" or "0"
// in xml file need "gpioPort", "Name", "Path"
package systemj.signals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.Serializer;

// to read bytes 
import java.io.InputStream;

import java.io.ByteArrayInputStream;

import java.util.Vector;

import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.signals.SOA.ControlMessageComm;

public class gpioReaderDetectHigh extends GenericSignalReceiver
{
    
    NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
    
       
        String serviceType;
        String relatedServiceName;
	//public void configure(HashMap<String, String> data) throws RuntimeException
	@Override
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{
		if(data.containsKey("Name")){
			signalName = (String)data.get("Name");
		} else throw new RuntimeException("The configuration parameter 'Name' is required!");
		
		if(data.containsKey("Path")){
			dir = (String) data.get("Path");
		} else throw new RuntimeException("The configuration parameter 'Path' is required!");
		if(data.containsKey("gpioPort")){
			gpioPort =(String) data.get("gpioPort");
			try{
			Process p;  // start the port if the port has not been setup 
			String[] command = {"/bin/bash", "-c", "su;echo " + gpioPort + " >/sys/class/gpio/export" , "echo", "in", ">", "/sys/class/gpio/gpio" + gpioPort + "/direction"};
				   p = Runtime.getRuntime().exec(command);
			}		
			catch (Exception e){
					e.printStackTrace();
			}
		}else throw new RuntimeException("The configuration parameter 'gpioPort' is required!");
		
                
                
                
	}

        
        
	@Override
	public void run()
	{
		Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
		String GPIOInput = "/sys/class/gpio/gpio" + gpioPort + "/value";
		//set gpio into "export", "in", mode.
		
		try
		{
                    
		ControlMessageComm cntrlMesg = new ControlMessageComm();
                    
			while(true){
				
                             Object[] list = new Object[2];
                            
                            if (SJServiceRegistry.getParsingStatus()){
                                  
                                    
                                        //Object[] list = new Object[2];
                                        list[0] = Boolean.FALSE; // now this is by default false 
                                        BufferedReader br; //set upt buffer reader
                                        String reading = "0";
                                        FileInputStream fdata = null;
                                        
                                        try {
                                            fdata = new FileInputStream(GPIOInput); //check if the file is there
                                        }
                                        catch (IOException e){
                                            System.out.println(" file error here");
                                        }finally {
						if (fdata != null){
							br = new BufferedReader(new InputStreamReader(fdata));
                                                        
                                                            reading = br.readLine();
                                           
							if(reading.trim().equals("1")){
								list[0] = Boolean.TRUE;
							
                                                                list[1] = reading; //save value into the send value in the arrary
                                                        } else {
                                                            list[0] = Boolean.FALSE;
                                                        }
							br.close();
							fdata.close();
						}else{
							System.out.println("file read error here");
                                                        list[0] = Boolean.FALSE;
						}						
                                    }
                                    
                                    
                                
                            } else {
                                list[0] = Boolean.FALSE;
                            }
                           
                            super.setBuffer(list);
                            
                        }        
				// if something is sent to here, the signal is valid
				
		} // end of try
		catch (BindException e)
		{
			e.printStackTrace();
                        System.exit(1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
                        System.exit(1);
		}
	}


	public gpioReaderDetectHigh(){
		super(); // Initializes the buffer
	}

	private InetAddress address;
	private int port;
    private String dir;
    private String gpioPort;
	private String signalName;
	private String Name;
	private Serializer se = null;
	private int readlength = 0;
	private int buffer_length = 0;
	private ServerSocket serverSocket = null;
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
}
