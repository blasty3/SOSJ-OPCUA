package systemj.signals.input;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;

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

// to read bytes from TCP
import java.io.InputStream;
// create a stream from bytes
import java.io.ByteArrayInputStream;
import java.io.IOException;
// to store series of bytes
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJMessageConstants;
import systemj.common.SJServiceRegistry;

public class WeatherCapeSensor extends GenericSignalReceiver
{
	//public void configure(HashMap<String, String> data) throws RuntimeException
	//@Override
        
        boolean checker=false;
        int LightINT,HumidityINT,PressureINT;
        double temperatureNum;
        String status=null;
        
	public void configure(Hashtable/*<String, String>*/ data) throws RuntimeException
	{

		
		if(data.containsKey("Serializer"))
		{
			try
			{
				se = (Serializer) Class.forName((String)data.get("Serializer")).newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error creating serializer object.", e);
			}
		}
             
                if (data.containsKey("Sensor")){
                    sensor = (String)data.get("Sensor");

                }
                else
		{
			throw new RuntimeException("The configuration parameter 'Sensor' is required! Choose 'temperatureSensor','pressureSensor','lightIntensitySensor', or 'humiditySensor' ");
		} 
                
	}


	//@Override
	public void run()
	{

                while(true){
                    
                        Object[] list = new Object[2];
                        
                        String result = null;
                         //status = CheckConnection("192.168.1.1");

                        //JSONObject js = new JSONTokener((cntrlMesg.receiveControlMessage(sensor).trim()));
                                //JSONObject js  = new JSONObject(new JSONTokener(msg));
                    
                   //  if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()) && js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString()));
                   //    {
                    
                     
                                //String type = js.getString("msgType");
                                //String code = js.getString("msgCode");
                                
                                 if (sensor.equalsIgnoreCase("temperature")){
                                     result = GetTemperature();
                                     list[0]=Boolean.TRUE;
                                     list[1]=result;
                                 } else if (sensor.equalsIgnoreCase("pressure")){
                                     result = GetPressure();
                                     list[0]=Boolean.TRUE;
                                     list[1]=result;
                                 } else if (sensor.equalsIgnoreCase("humidity")){
                                     result = GetHumidity();
                                     list[0]=Boolean.TRUE;
                                     list[1]=result;
                                 } else if (sensor.equalsIgnoreCase("light")){
                                     result = GetLightIntensity();
                                     list[0]=Boolean.TRUE;
                                     list[1]=result;
                                 } else{
                                     list[0]=Boolean.FALSE;
                                     
                                 }
   
                                 
                                 super.setBuffer(list);
                                 
                             } 
                             
                                   
                             
                   //    } 
                    
                         

                    
                    
                    //SOA transceiver . receive and acquire sensor, transfer to signal sender
                
                
               
            
               
           }
           
            
        
        
        
        private String GetTemperature(){
            Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
                String sensorAddr=null,sensorRead=null;
		//String pressureInput = "/sys/bus/i2c/devices/1-0077/pressure0_input";
		//String humidityInput = "/sys/bus/i2c/devices/1-0040/humidity1_input";
		//String temperatureInput0= "/sys/bus/i2c/devices/1-0077/temp0_input";
		//String luxInput= "/sys/bus/i2c/devices/1-0039/lux1_input";
		//String temperatureInput1= "/sys/bus/i2c/devices/1-0040/temp1_input";

              //  while (true)
              //  {
                
                    try
                    {

			/*
			 * 22/09/2011 (HJ)
			 * This is a modified version which this thread waits on a specific port without timeout
			 */
			//while(true){
				//socket = serverSocket.accept();
				//System.out.println("i am in here");
				// if something is sent to here, the signal is valid
				//Object[] list = new Object[2];
				
				
                            //    if (sensorType.equalsIgnoreCase("temperatureSensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0077/temp0_input";  //2 available sensor address 
                                    //sensorAddr = "/sys/bus/i2c/devices/1-0040/temp1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    //temperatureNum = (Double.parseDouble(br.readLine()))/10;
                                    //temperatureNum = temperatureNum/10;
                                    
                                    //list[1] = Double.toString(temperatureNum);
                                    sensorRead = Double.toString((Double.parseDouble(br.readLine()))/10);
                                    br.close();
                          //      }
                                
				//System.out.println("starting print address");
				//System.out.println(Address);
				// code start there //this is not safe need to fix can not repeat open this file.
                                
				//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
				
				//System.out.print("the signal is " + Name);
				//System.out.println(" the reading is " + reading);
/* hard coded
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pressureInput)));
				int pressureINT = Integer.parseInt(br.readLine());
				pressureINT = pressureINT/100;
				System.out.println("the pressure is " + pressureINT   + " millibar"  );

				BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(humidityInput)));
				int humidityINT = Integer.parseInt(br1.readLine());
				humidityINT = humidityINT/100;
			    System.out.println("the humidity is " + humidityINT  + "% Humidity" );

				BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(temperatureInput0)));
				int temperatureINT = Integer.parseInt(br2.readLine());
				temperatureINT = temperatureINT/10;
			    System.out.println("the temperature is " + temperatureINT + " Celcius");
*/
				
				//br.close();
				//super.setBuffer(list);
				//socket.setSoLinger(true, 0); // setting immeidate closing for this connection (however not assured)
				//in.close();
				//socket.close();
			//} // end of while(true)
                    } // end of try
                    catch (BindException e)
                    {
                    	e.printStackTrace();
                    }catch (IOException iex){
                        System.out.println("temperature sensor temporarily busy: " +iex.getMessage());  //* this can be used to create new error/avaailability handling
                        sensorRead="unavailable";
                    }
                    catch (Exception e)
                    {
                    	e.printStackTrace();
                    }
                    
                //}
                    return sensorRead;
		
        }
        
        
        private String GetHumidity(){
            Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
                String sensorAddr=null,sensorRead=null;
		//String pressureInput = "/sys/bus/i2c/devices/1-0077/pressure0_input";
		//String humidityInput = "/sys/bus/i2c/devices/1-0040/humidity1_input";
		//String temperatureInput0= "/sys/bus/i2c/devices/1-0077/temp0_input";
		//String luxInput= "/sys/bus/i2c/devices/1-0039/lux1_input";
		//String temperatureInput1= "/sys/bus/i2c/devices/1-0040/temp1_input";

              //  while (true)
              //  {
                
                    try
                    {

			/*
			 * 22/09/2011 (HJ)
			 * This is a modified version which this thread waits on a specific port without timeout
			 */
			//while(true){
				//socket = serverSocket.accept();
				//System.out.println("i am in here");
				// if something is sent to here, the signal is valid
				//Object[] list = new Object[2];
				
				
                               // if (sensorType.equalsIgnoreCase("humiditySensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0040/humidity1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    //HumidityINT = (Integer.parseInt(br.readLine()))/100;
                                    //humidityINT = humidityINT/100;
                                   // list[1] = Integer.toString(humidityINT);
                                    sensorRead= Integer.toString((Integer.parseInt(br.readLine()))/100);
                                    br.close();
                              //  }
                                
				//System.out.println("starting print address");
				//System.out.println(Address);
				// code start there //this is not safe need to fix can not repeat open this file.
                                
				//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
				
				//System.out.print("the signal is " + Name);
				//System.out.println(" the reading is " + reading);
/* hard coded
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pressureInput)));
				int pressureINT = Integer.parseInt(br.readLine());
				pressureINT = pressureINT/100;
				System.out.println("the pressure is " + pressureINT   + " millibar"  );

				BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(humidityInput)));
				int humidityINT = Integer.parseInt(br1.readLine());
				humidityINT = humidityINT/100;
			    System.out.println("the humidity is " + humidityINT  + "% Humidity" );

				BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(temperatureInput0)));
				int temperatureINT = Integer.parseInt(br2.readLine());
				temperatureINT = temperatureINT/10;
			    System.out.println("the temperature is " + temperatureINT + " Celcius");
*/
				
				//br.close();
				//super.setBuffer(list);
				//socket.setSoLinger(true, 0); // setting immeidate closing for this connection (however not assured)
				//in.close();
				//socket.close();
			//} // end of while(true)
                    } // end of try
                    catch (BindException e)
                    {
                    	e.printStackTrace();
                    }catch (IOException iex){
                        System.out.println("humidity sensor temporarily busy: " +iex.getMessage());  //* this can be used to create new error/avaailability handling
                        sensorRead="unavailable";
                    }
                    catch (Exception e)
                    {
                    	e.printStackTrace();
                    }
                    
                //}
                    return sensorRead;
		
        }
        
        private String GetLightIntensity(){
            Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
                String sensorAddr=null,sensorRead=null;
		//String pressureInput = "/sys/bus/i2c/devices/1-0077/pressure0_input";
		//String humidityInput = "/sys/bus/i2c/devices/1-0040/humidity1_input";
		//String temperatureInput0= "/sys/bus/i2c/devices/1-0077/temp0_input";
		//String luxInput= "/sys/bus/i2c/devices/1-0039/lux1_input";
		//String temperatureInput1= "/sys/bus/i2c/devices/1-0040/temp1_input";

              //  while (true)
              //  {
                
                    try
                    {

			/*
			 * 22/09/2011 (HJ)
			 * This is a modified version which this thread waits on a specific port without timeout
			 */
			//while(true){
				//socket = serverSocket.accept();
				//System.out.println("i am in here");
				// if something is sent to here, the signal is valid
				//Object[] list = new Object[2];
				
				
                               // if (sensorType.equalsIgnoreCase("humiditySensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0039/lux1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    //LightINT = Integer.parseInt(br.readLine());
                                    //humidityINT = humidityINT/100;
                                   // list[1] = Integer.toString(humidityINT);
                                    sensorRead= Integer.toString(Integer.parseInt(br.readLine()));
                                    br.close();
                              //  }
                                
				//System.out.println("starting print address");
				//System.out.println(Address);
				// code start there //this is not safe need to fix can not repeat open this file.
                                
				//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
				
				//System.out.print("the signal is " + Name);
				//System.out.println(" the reading is " + reading);
/* hard coded
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pressureInput)));
				int pressureINT = Integer.parseInt(br.readLine());
				pressureINT = pressureINT/100;
				System.out.println("the pressure is " + pressureINT   + " millibar"  );

				BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(humidityInput)));
				int humidityINT = Integer.parseInt(br1.readLine());
				humidityINT = humidityINT/100;
			    System.out.println("the humidity is " + humidityINT  + "% Humidity" );

				BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(temperatureInput0)));
				int temperatureINT = Integer.parseInt(br2.readLine());
				temperatureINT = temperatureINT/10;
			    System.out.println("the temperature is " + temperatureINT + " Celcius");
*/
				
				//br.close();
				//super.setBuffer(list);
				//socket.setSoLinger(true, 0); // setting immeidate closing for this connection (however not assured)
				//in.close();
				//socket.close();
			//} // end of while(true)
                    } // end of try
                    catch (BindException e)
                    {
                    	System.out.println("WeatherCapeSensorRead Cannot bound "+e.getMessage());
                    }catch (IOException iex){
                        System.out.println("Light intensity sensor temporarily busy: " +iex.getMessage());  //* this can be used to create new error/avaailability handling
                        sensorRead="unavailable";
                    }
                    catch (Exception e)
                    {
                    	e.printStackTrace();
                    }
                    
                //}
                    return sensorRead;
		
        }
        
         private String GetPressure(){
            Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
                String sensorAddr=null,sensorRead=null;
		//String pressureInput = "/sys/bus/i2c/devices/1-0077/pressure0_input";
		//String humidityInput = "/sys/bus/i2c/devices/1-0040/humidity1_input";
		//String temperatureInput0= "/sys/bus/i2c/devices/1-0077/temp0_input";
		//String luxInput= "/sys/bus/i2c/devices/1-0039/lux1_input";
		//String temperatureInput1= "/sys/bus/i2c/devices/1-0040/temp1_input";

              //  while (true)
              //  {
                
                    try
                    {

			
                                    sensorAddr = "/sys/bus/i2c/devices/1-0077/pressure0_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    //PressureINT = (Integer.parseInt(br.readLine()))/100;
                                    //pressureINT = pressureINT/100;
                                    //System.out.println("the pressure is " + pressureINT   + " millibar"  );
                                    //list[1] = Integer.toString(pressureINT);
                                   sensorRead = Integer.toString((Integer.parseInt(br.readLine()))/100);
                                    br.close();
                             
                    } // end of try
                    catch (BindException e)
                    {
                    	e.printStackTrace();
                    }catch (IOException iex){
                        System.out.println("Pressure sensor temporarily busy: " +iex.getMessage());  //* this can be used to create new error/avaailability handling
                        sensorRead="unavailable";
                    }
                    catch (Exception e)
                    {
                    	e.printStackTrace();
                    }
                    
                //}
                    return sensorRead;
		
        }
        
        /*
         
         private String GetPressure(){
            Socket socket = null;
		int debug = 0;
		int infoDebug = 0;
		int trafficDebug = 0;
		int bufferSize = 100;
                String sensorAddr=null,sensorRead=null;
		//String pressureInput = "/sys/bus/i2c/devices/1-0077/pressure0_input";
		//String humidityInput = "/sys/bus/i2c/devices/1-0040/humidity1_input";
		//String temperatureInput0= "/sys/bus/i2c/devices/1-0077/temp0_input";
		//String luxInput= "/sys/bus/i2c/devices/1-0039/lux1_input";
		//String temperatureInput1= "/sys/bus/i2c/devices/1-0040/temp1_input";

              //  while (true)
              //  {
                
                    try
                    {

			
				
				
                                if (sensorType.equalsIgnoreCase("temperatureSensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0040/temp1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    double temperatureNum = Integer.parseInt(br.readLine());
                                    temperatureNum = temperatureNum/10;
                                    
                                    //list[1] = Double.toString(temperatureNum);
                                    sensorRead = Double.toString(temperatureNum);
                                    br.close();
                                } else if (sensorType.equalsIgnoreCase("pressureSensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0077/pressure0_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    int pressureINT = Integer.parseInt(br.readLine());
                                    pressureINT = pressureINT/100;
                                    //System.out.println("the pressure is " + pressureINT   + " millibar"  );
                                    //list[1] = Integer.toString(pressureINT);
                                   sensorRead = Integer.toString(pressureINT);
                                    br.close();
                                } else if (sensorType.equalsIgnoreCase("humiditySensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0040/humidity1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    int humidityINT = Integer.parseInt(br.readLine());
                                    humidityINT = humidityINT/100;
                                   // list[1] = Integer.toString(humidityINT);
                                    sensorRead= Integer.toString(humidityINT);
                                    br.close();
                                } else if (sensorType.equalsIgnoreCase("lightSensor")){
                                    //list[0] = Boolean.TRUE;
                                    sensorAddr = "/sys/bus/i2c/devices/1-0039/lux1_input";
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sensorAddr)));
                                    //String reading = br.readLine();
                                    int lightINT = Integer.parseInt(br.readLine());
                                    //lightINT = humidityINT/100;
                                   // list[1] = Integer.toString(humidityINT);
                                    sensorRead= Integer.toString(lightINT);
                                    br.close();
                                } else {
                                    //list[0] = Boolean.FALSE;
                                    //list[1] = "";
                                    sensorRead = "";
                                    System.err.println("the 'sensor' parameter is wrongly configured, choose 'temperatureSensor','humiditySensor',or 'pressureSensor'");
                                    System.exit(1);
                                }
                                

                    } // end of try
                    catch (BindException e)
                    {
                    	e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                    	e.printStackTrace();
                    }
                    
                //}
                    return sensorRead;
		
        }
         
         

	/*  
	public synchronized Object[] getBuffer(){
    System.out.println("getBuffer() is called");
		return super.buffer;
	}
	 */

	public WeatherCapeSensor(){
		super(); // Initializes the buffer
	}
        
        

	
	private int port;
    
	private String sensor;
	private Serializer se = null;
	private int readlength = 0;
	private int buffer_length = 0;
	private ServerSocket serverSocket = null;
        
}
