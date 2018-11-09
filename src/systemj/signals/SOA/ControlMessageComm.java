/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJMessageConstants;
import systemj.common.SJServiceRegistry;


/**
 *
 * @author Udayanto
 */
public class ControlMessageComm {
    
    //private static final Object lock = new Object();
    
    /*
    public JSONObject transceiveRequestMessageShortTimeout(String serviceName, String requestMessage, int requestPort, int responsePort, String ipAddr)
    {
        JSONObject answer = new JSONObject();
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            MulticastSocket s = null;
            MulticastSocket s2 = null;
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(ipAddr);
                        byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        
                        if(responsePort==requestPort){
                             s = new MulticastSocket(requestPort);
                            //s2 = new MulticastSocket(responsePort);
                            //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                            out.writeObject(requestMessage);
                            out.flush();
                            msg = byteStream.toByteArray();
                            out.close();

                            //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                            //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                            DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,requestPort);
                            if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +requestPort);
                            s.send(req);
                            if (infoDebug==1)System.out.println("data has been sent! Now wait for response");

                            //DatagramPacket resp = new DatagramPacket(packet, packet.length,ipAddress,responsePort);
                            DatagramPacket resp = new DatagramPacket(packet, packet.length);
                            
                            s.setSoTimeout(3000);

                            //do {

                            s.receive(resp);

                           // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));

                            byte[] data;
                            if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                            data = new byte[resp.getLength()];

                            System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                    // time to decode make use of data[]
                            //Object[] list = new Object[2];
                             //list[0] = Boolean.TRUE;        
                              if(data.length > 0)
                              {
                                 if(((int)data[0] == -84) && ((int)data[1] == -19))
                                 {
                                    try
                                    {
                                         if(debug == 1) System.out.println("Java built-in deserializer is used");
                                         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                         Object mybuffer = ois.readObject();              
                                         if(debug == 1) System.out.println(mybuffer);
                                         if(debug == 1) System.out.println((mybuffer.getClass()).getName());

                                            if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");

                                            if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                            JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                            answer=js;
                                     }
                                     catch(Exception e)
                                     {
                                          e.printStackTrace();
                                     }
                                  }
                                 }


                                       s.close();
                                       //s2.close();
                        } else {
                             s = new MulticastSocket(requestPort);
                            s2 = new MulticastSocket(responsePort);
                            //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                            out.writeObject(requestMessage);
                            out.flush();
                            msg = byteStream.toByteArray();
                            out.close();

                            //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                            //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                            DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,requestPort);
                            if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +requestPort);
                            s.send(req);
                            if (infoDebug==1)System.out.println("data has been sent! Now wait for response");

                            //DatagramPacket resp = new DatagramPacket(packet, packet.length,ipAddress,responsePort);
                            DatagramPacket resp = new DatagramPacket(packet, packet.length);
                            s2.setSoTimeout(3000);

                            //do {

                            s2.receive(resp);

                           // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));

                            byte[] data;
                            if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                            data = new byte[resp.getLength()];

                            System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                    // time to decode make use of data[]
                            //Object[] list = new Object[2];
                             //list[0] = Boolean.TRUE;        
                              if(data.length > 0)
                              {
                                 if(((int)data[0] == -84) && ((int)data[1] == -19))
                                 {
                                    try
                                    {
                                         if(debug == 1) System.out.println("Java built-in deserializer is used");
                                         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                         Object mybuffer = ois.readObject();              
                                         if(debug == 1) System.out.println(mybuffer);
                                         if(debug == 1) System.out.println((mybuffer.getClass()).getName());

                                            if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");

                                            if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                            JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                            answer=js;
                                     }
                                     catch(Exception e)
                                     {
                                          e.printStackTrace();
                                     }
                                  }
                                 }


                                       s.close();
                                       s2.close();
                        }
                        
                       

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
			System.out.println("ControlMessageTimeout,transceiveRequestMessageShortTimeout: " +e.getMessage()+ "serviceName: " +serviceName);
                       
                        //if(s!=null){
                            s.close();
                       // }
                        
                        if(s2!=null){
                            s2.close();
                        }
                        
                        
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                        bex.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
			e.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    
                    if (answer.isEmpty()){
                        try {
                            answer.put("msgType", "");
                            //answer.put("rspCode", "");
                            //answer.put("token","0");
                            answer.put("msgID","0");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }  
                   return answer;
                    
      //  }

    }
    */
    
    public JSONObject transceiveRequestMessageShortTimeout(String requestMessage, int controlPort, int responsePort, String ipAddr)
    {
        JSONObject answer = new JSONObject();
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=1;
            
            if(responsePort==controlPort){
                
                MulticastSocket s = null;
            //MulticastSocket s2 = null;
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(ipAddr);
                        byte[] msg = new byte[65508];
                        byte packet[] = new byte[65508];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        s = new MulticastSocket(controlPort);
                        s.joinGroup(InetAddress.getByName("224.0.0.101"));
                        //s2 = new MulticastSocket(responsePort);
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        out.writeObject(requestMessage);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,controlPort);
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +controlPort);
                        s.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        //DatagramPacket resp = new DatagramPacket(packet, packet.length,ipAddress,responsePort);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        s.setSoTimeout(3000);
                        
                        //do {
                        
                        s.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        answer=js;
                                       

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              
                                   }
                                   
                                   
                                   s.close();
                                   //s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
			System.out.println("ControlMessageTimeout,transceiveRequestMessageNoServiceTypeShortTimeout: " +e.getMessage());
                        s.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                       bex.printStackTrace();
                        s.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
			e.printStackTrace();
                        s.close();
                    }
                
            } else {
                
                MulticastSocket s = null;
                MulticastSocket s2 = null;
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(ipAddr);
                        byte[] msg = new byte[65508];
                        byte packet[] = new byte[65508];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        s = new MulticastSocket(controlPort);
                        s2 = new MulticastSocket(responsePort);
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        out.writeObject(requestMessage);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,controlPort);
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +controlPort);
                        s.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        //DatagramPacket resp = new DatagramPacket(packet, packet.length,ipAddress,responsePort);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        s2.setSoTimeout(3000);
                        
                        //do {
                        
                        s2.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        answer=js;
                                       

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              
                                   }
                                   
                                   
                                   s.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
			System.out.println("ControlMessageTimeout,transceiveRequestMessageNoServiceTypeShortTimeout: " +e.getMessage());
                        s.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                       bex.printStackTrace();
                        s.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
			e.printStackTrace();
                        s.close();
                    }
                
            }
            
            if (answer.isEmpty()){
                        try {
                            answer.put("msgType", "");
                            //answer.put("rspCode", "");
                            //answer.put("token","0");
                            answer.put("msgID","0");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    
                   return answer;
                    
      //  }

    }
    
    
    public JSONObject transceiveRequestMessageNoServiceTypeShortTimeoutForActuatorResponse(String requestMessage, int controlPort, int responsePort, String ipAddr, int timeout)
    {
        JSONObject answer = new JSONObject();
        JSONObject jsDat = new JSONObject();
        try {
            jsDat.put("data","");
        } catch (JSONException ex) {
            Logger.getLogger(ControlMessageComm.class.getName()).log(Level.SEVERE, null, ex);
        }
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            MulticastSocket s = null;
            MulticastSocket s2 = null;
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(ipAddr);
                        byte[] msg = new byte[65508];
                        byte packet[] = new byte[65508];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        s = new MulticastSocket(controlPort);
                        s2 = new MulticastSocket(responsePort);
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        out.writeObject(requestMessage);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,controlPort);
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +controlPort);
                        s.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        s2.setSoTimeout(timeout);
                        
                        //do {
                        
                        s2.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        answer = new JSONObject(new JSONTokener(mybuffer.toString().trim()));

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
                                   else
                                   {
                                       if(debug == 1) System.out.println("Pure signal");
                                        //list[1] = null;
                                   }
                                   
                                   s.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
			System.out.println("ControlMessageTimeout, transceiveRequestMessageNoServiceTypeShortTimeoutForActuatorResponse: " +e.getMessage());
                        
                        answer=new JSONObject();
                        
                        s.close();
                        s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                        bex.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
			e.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    
                   if (answer.isEmpty()){
                    try {
                        answer.put("msgType","");
                        answer.put("rspCode","");
                        answer.put("payload",jsDat);
                    } catch (JSONException ex) {
                        System.out.println("ControlMessage,transceiveRequestMessageShortTimeout: " +ex.getMessage());
                    }
                   } 
                    
                   return answer;
                    
      //  }

    }
    
    /*
    public boolean transceiveRequestMessageNoServiceTypeShortTimeoutForResendingDoneACK(String requestMessage, int controlPort, int responsePort, String ipAddr, int timeout)
    {
        boolean answer = false;
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            MulticastSocket s = null;
            MulticastSocket s2 = null;
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(ipAddr);
                        byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        s = new MulticastSocket(controlPort);
                        s2 = new MulticastSocket(responsePort);
                        
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        out.writeObject(requestMessage);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,controlPort);
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddr+"port:" +controlPort);
                        s.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        //DatagramPacket resp = new DatagramPacket(packet, packet.length,ipAddress,controlPort);
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        s2.setSoTimeout(timeout);
                        
                        //do {
                        
                        s2.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        JSONObject jsRcv = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        //JSONObject jsMsg = new JSONObject(new JSONTokener(requestMessage.toString()));
                                        
                                        if (jsRcv.getString("msgType").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()) || jsRcv.getString("msgCode").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                            
                                            JSONObject jsRcvPyld = jsRcv.getJSONObject("payload");
                                            
                                            if (jsRcvPyld.getString("data").equalsIgnoreCase("ACK3")){
                                                
                                                answer=false;
                                            } else if (jsRcvPyld.getString("data").equalsIgnoreCase("RequestACK2")){
                                                
                                                answer=true;
                                            }
                                        }
                                       

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
                                   else
                                   {
                                       if(debug == 1) System.out.println("Pure signal");
                                        //list[1] = null;
                                   }
                                   
                                   s.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
			System.out.println("ControlMessageTimeout,transceiveRequestMessageNoServiceTypeShortTimeoutForResendingDoneACK: " +e.getMessage());
                        answer=false;
                        s.close();
                        s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                        bex.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
			e.printStackTrace();
                        s.close();
                        s2.close();
                    }
                    
                    
                   return answer;
                    
      //  }

    }
    */
    
    
     public void receiveDoubleACKMessage(int recPort)
     {
        
         //String recACK="NoACK";
         boolean stat = false;
         
         JSONObject answer = new JSONObject();
        //synchronized (lock)
        //{
            int debug=0;
                    try
                    {
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[65508];
                        
                        //if (debug==1) System.out.println("ReceiveControlMessageServTypeOnly port:" +SJServiceRegistry.getServicesProviderControlPortOfType(serviceType) + "of serviceType: "  +serviceType);
                        
                        MulticastSocket s = new MulticastSocket(recPort);
                        s.joinGroup(InetAddress.getByName("224.0.0.101"));
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        //s.setSoTimeout(timeout);
                        s.receive(resp);
                        
                        byte[] data;
                        if(debug == 1) System.out.println(" transceiveRequestMessageLongTimeout, received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        answer = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        
                                        String status = answer.getString("msgType");
                                        
                                        if(status.equalsIgnoreCase("ACK")){
                                           stat=true;
                                        }
                                        
                                        
                                        //answer = js.toString();
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                            }
                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        /*
                        try {
                            System.out.println("ControlMessage, receivesend Timeout" +e.getMessage());
                            MulticastSocket s = new MulticastSocket(recPort);
                 
                            byte[] msg = new byte[1024];
                            //InetAddress ipAddress;
                 
                            InetAddress ipAddress = InetAddress.getByName(ipAddr);
                 
                            int infoDebug=1;
     //JSONObject js = new JSONObject(message);
                            //SJRequestMessage sjreq= new SJRequestMessage(SJMessageConstants.MessageCode.GET, SJMessageConstants.MessageType.CON);
                            //sjreq.setSourceAddress(SJServiceRegistry.getOwnIPAddressFromRegistry());
                           // sjreq.setDestinationAddress(ipAddr);
                            //sjreq.setDestinationPort(controlPort);
                            //sjreq.setMessageID();
                            //sjreq.setMessageToken();
                            //sjreq.setRequestMessagePayload(ipAddr);
                            //String message2 = sjreq.createRequestMessage();
                            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
       //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                            out.writeObject(message);
        //out.writeObject(SJServiceRegistry.AdvertiseNodeServices("HelloMessage").toString()); //put service description to be sent to remote devices
                            out.flush();
                            msg = byteStream.toByteArray();
                            out.close(); 
         
                            DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, recPort);
         //if (infoDebug ==1 )System.out.println("Sending control message:" +message );
                            s.send(hi);
                            if (infoDebug ==1 ) System.out.println("data has been sent!");
                                   
         //SJServiceRegistry.AcknowledgeHelloMessageSent(true);
          // if (infoDebug ==1 ) System.out.println("Status acknowledge in sender:" +SJServiceRegistry.getAcknowledgeHelloMessageSent());
                                  // SJServiceRegistry.RecordAdvertisementTimeStamp();
                            s.close();
                 
                 
                            //answer="{}";
                            } catch (UnknownHostException ex) {
                                System.out.println("ControlMessage, transceiveRequestMessageLongTimeout UnknownHostException when sending: " +ex.getMessage());
                            } catch (IOException iex){
                                 System.out.println("ControlMessage, transceiveRequestMessageLongTimeout IOException when sending: " +iex.getMessage());
                            }
                        */
                        
                        //return "NoACK";
                        
                        
                    }
                    
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        //return e.getMessage();
                        
                        
                    }
      //  }
        if (debug==1) System.out.println("ControlMessage debug on: Answer:" +answer);
       // return String.format("", answer);
        //return answer;
                    
      //  }
        //return answer;
        //return recACK;
        //return stat;
        
    }
    
    
    /*
     public JSONObject receiveSendRequestMessageWithTimeout(String message, int controlPort, String ipAddr, int timeout)
     {
        JSONObject answer = new JSONObject();
        //synchronized (lock)
        //{
            int debug=0;
                    try
                    {
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        
                        //if (debug==1) System.out.println("ReceiveControlMessageServTypeOnly port:" +SJServiceRegistry.getServicesProviderControlPortOfType(serviceType) + "of serviceType: "  +serviceType);
                        
                        MulticastSocket s = new MulticastSocket(controlPort);
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        s.setSoTimeout(timeout);
                        s.receive(resp);
                        
                        byte[] data;
                        if(debug == 1) System.out.println(" transceiveRequestMessageLongTimeout, received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        answer = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        
                                         
                                        
                                        
                                        //answer = js.toString();
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
    

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        try {
                            System.out.println("ControlMessage, receivesend Timeout" +e.getMessage());
                            MulticastSocket s = new MulticastSocket(controlPort);
                 
                            byte[] msg = new byte[1024];
                            //InetAddress ipAddress;
                 
                            InetAddress ipAddress = InetAddress.getByName(ipAddr);
                 
                            int infoDebug=1;
     //JSONObject js = new JSONObject(message);
                            //SJRequestMessage sjreq= new SJRequestMessage(SJMessageConstants.MessageCode.GET, SJMessageConstants.MessageType.CON);
                            //sjreq.setSourceAddress(SJServiceRegistry.getOwnIPAddressFromRegistry());
                           // sjreq.setDestinationAddress(ipAddr);
                            //sjreq.setDestinationPort(controlPort);
                            //sjreq.setMessageID();
                            //sjreq.setMessageToken();
                            //sjreq.setRequestMessagePayload(ipAddr);
                            //String message2 = sjreq.createRequestMessage();
                            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
       //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                            out.writeObject(message);
        //out.writeObject(SJServiceRegistry.AdvertiseNodeServices("HelloMessage").toString()); //put service description to be sent to remote devices
                            out.flush();
                            msg = byteStream.toByteArray();
                            out.close(); 
         
                            DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, controlPort);
         //if (infoDebug ==1 )System.out.println("Sending control message:" +message );
                            s.send(hi);
                            if (infoDebug ==1 ) System.out.println("data has been sent!");
                                   
         //SJServiceRegistry.AcknowledgeHelloMessageSent(true);
          // if (infoDebug ==1 ) System.out.println("Status acknowledge in sender:" +SJServiceRegistry.getAcknowledgeHelloMessageSent());
                                  // SJServiceRegistry.RecordAdvertisementTimeStamp();
                            s.close();
                 
                 
                            //answer="{}";
                            } catch (UnknownHostException ex) {
                                System.out.println("ControlMessage, transceiveRequestMessageLongTimeout UnknownHostException when sending: " +ex.getMessage());
                            } catch (IOException iex){
                                 System.out.println("ControlMessage, transceiveRequestMessageLongTimeout IOException when sending: " +iex.getMessage());
                            }
                    }
                    
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        
                    }
      //  }
        if (debug==1) System.out.println("ControlMessage debug on: Answer:" +answer);
       // return String.format("", answer);
        //return answer;
                    
      //  }
        return answer;
    }
    */
    
    
     
    
    public void sendControlMessage(String ipAddr, int controlPort, String message){
        try {
            byte[] msg = new byte[65508];
            InetAddress ipAddress = InetAddress.getByName(ipAddr);
            int infoDebug=1;
            //JSONObject js = new JSONObject(message);
              
               ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
               ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
              //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
               out.writeObject(message);
               //out.writeObject(SJServiceRegistry.AdvertiseNodeServices("HelloMessage").toString()); //put service description to be sent to remote devices
               out.flush();
                msg = byteStream.toByteArray();
                out.close(); 
                MulticastSocket s = new MulticastSocket(controlPort);
                DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, controlPort);
                if (infoDebug ==1 )System.out.println("Sending control message:" +message+ "to: " +ipAddress );
                s.send(hi);
                if (infoDebug ==1 ) System.out.println("data has been sent!");
                                          
                //SJServiceRegistry.AcknowledgeHelloMessageSent(true);
                 // if (infoDebug ==1 ) System.out.println("Status acknowledge in sender:" +SJServiceRegistry.getAcknowledgeHelloMessageSent());
                                         // SJServiceRegistry.RecordAdvertisementTimeStamp();
                s.close();
        } catch (UnknownHostException hex) {
            
            System.err.println("ControlMessage, problem IOException: " +hex.getMessage());
        } catch (Exception e){
            System.err.println("ControlMessage, problem Exception: " +e.getMessage());
        }
    }
    
    /*
    private InetAddress getBroadcastAddress(String Addr){
            InetAddress broadcastAddr = null;
             try {
            // TODO code application logic here
                    Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback())
                        continue;    // Don't want to broadcast to the loopback interface
                        for (InterfaceAddress interfaceAddress :
                            networkInterface.getInterfaceAddresses()) {
                            String addr = interfaceAddress.getAddress().getHostAddress();
                            
                            if(addr.equals(Addr)){
                                 InetAddress broadcast = interfaceAddress.getBroadcast();
                                 
                                  if (broadcast == null) {
                                    continue;
                                  } else {
                                      broadcastAddr = broadcast;
                                  }
                                 
                            }
                            
                           
                            
                            
                          //  if (broadcast == null) {
                         //           continue;
                          //      }
                          //  if (broadcast.toString().contains("192.168.1")) {
                          //          broadcastAddr = broadcast;
                         //       }
                           
                        // Use the address
                         }
                     }
               } catch (SocketException ex) {
                System.out.println("Cannot find address: " +ex.getMessage());
                
            }
             return broadcastAddr;
        }
    */
    
    
    //return raw message
    /**
     * Ask for serviceType
     * @param serviceType
     * @return 
     */
    /*
    public String receiveControlMessageWithServiceTypeAndActionName(int port)
    {
        String answer = null;
        //synchronized (lock)
        //{
            int debug=0;
                    try
                    {
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        
                        if (debug==1) System.out.println("ReceiveControlMessageServTypeAndActionName port:" +port);
                        
                       // MulticastSocket s = new MulticastSocket(SJServiceRegistry.getServicesProviderControlPortOfTypeAndAction(serviceType,actionName));
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        MulticastSocket s = new MulticastSocket(port);
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (debug==1)  System.out.println( "port" +resp.getPort()+ "is in loopback mode? : " +s.getLoopbackMode());
                        //s.setSoTimeout(1000);
                        s.receive(resp);
                        
                        byte[] data;
                        if(debug == 1) System.out.println("received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        //SJServiceRegistry.addMessageTokensToBuffer(js);
                                        answer = js.toString();
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
    

                    }
                    //catch (java.net.SocketTimeoutException e)
                    //{
		//	System.out.println("Timeout" +e.getMessage());
                    //    answer="{}";
                    //}
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        answer="{}";
                    }
      //  }
        if (debug==1) System.out.println("ControlMessage debug on: Answer:" +answer);
       // return String.format("", answer);
        return answer;
        
    }
    */
    
    
    /*
    public String receiveControlMessageOfServiceType(int port)
    {
        String answer = null;
        //synchronized (lock)
        //{
            int debug=1;
                    try
                    {
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        
                        if (debug==1) System.out.println("ReceiveControlMessageServTypeOnly port:" +port);
                        
                        MulticastSocket s = new MulticastSocket(port);
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        //s.setSoTimeout(1000);
                        s.receive(resp);
                        
                        byte[] data;
                        if(debug == 1) System.out.println("received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        SJServiceRegistry.addMessageTokensToBuffer(js);
                                        answer = js.toString();
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
    

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
			System.out.println("Timeout" +e.getMessage());
                        answer="{}";
                    }
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        answer="{}";
                    }
      //  }
        if (debug==1) System.out.println("ControlMessage debug on: Answer:" +answer);
       // return String.format("", answer);
        return answer;
        
    }
    */
    
    
    /*
    public JSONObject receiveTimeout2(int port)
    {
        JSONObject answer = new JSONObject();
        //synchronized (lock)
        //{
            int debug=1;
                    try
                    {
                        //byte[] msg = new byte[1024];
                        byte packet[] = new byte[8096];
                        
                        if (debug==1) System.out.println("ReceiveControlMessageServTypeOnly port:" +port);
                        
                        MulticastSocket s = new MulticastSocket(port);
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        
                        
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        //s.setSoTimeout(1000);
                        s.receive(resp);
                        
                        byte[] data;
                        if(debug == 1) System.out.println("received control message pack length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                         data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     //if(debug == 1) System.out.println(mybuffer);
                                     //if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    // if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                     //{
                                    //     Object mybufferArray[] = (Object[])mybuffer;
                                          // System.out.println(mybufferArray.length);	
                                   //  }
                                   //  else
                                  //   {
                                        //if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("received info control: " +mybuffer.toString().trim()+"\n");
                                        
                                        
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        
                                        answer = js;
                                        //SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK, SJMessageConstants.ResponseCode.CONTENT);
                                        
                                       // if (js.getString("type").equalsIgnoreCase(SJMessageConstants.MessageType.CON.toString()))
                                       // {
                                       //     if (js.getString("code").equalsIgnoreCase(SJMessageConstants.MessageCode.GET.toString())){
                                                
                                                //sjResp.set
                                                
                                       //     }
                                     //   }
                                       

                                   //  }

                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                              else
                              {
                                      // decode the object from the string          
                                     String stringToProcess = new String(data);
                                     if(stringToProcess.indexOf("!@#$%^&*()") == 0)
                                     {
                                          if(debug == 1) System.out.println("Object scope deserilizer is used");
                                                                        // to obtain the className
                                          int beginningOfClassName = stringToProcess.indexOf("!@#$%^&*()") + "!@#$%^&*()".length();
                                          int endOfClassName = stringToProcess.indexOf("!@#$%^&*()", beginningOfClassName);
                                          String className = stringToProcess.substring(beginningOfClassName, endOfClassName);
                                          if(debug == 1) System.out.println("Found className = " + className);            
                                            // get the content of the string
                                          String classString = stringToProcess.substring(endOfClassName + "!@#$%^&*()".length());            
                                          if(debug == 1) System.out.println("Class string = " + classString);
                                           // obtain the bytes and use the serilizer function to work
                                          byte classBytes[] = classString.getBytes();            
                                         // list[1] = ((Serializer)(Class.forName(className).newInstance())).deserialize(classBytes, classBytes.length);;
                                      }
                                      else
                                      {
                                          if(debug == 1) System.out.println("Not a serialized stream, decode as normal string");

                                            // list[1] = stringToProcess;
                                       }
                                     }
                                   }
    

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
			System.out.println("Timeout" +e.getMessage());
                        answer=new JSONObject();
                    }
                    catch (Exception e)
                    {
                        System.out.println("What happened in ControlMessageReceive:" +e.getMessage());
			//System.out.println("Problem when connecting to ip: " + ipAddress + " port :" + port);
			e.printStackTrace();
                        answer=new JSONObject();
                    }
      //  }
        if (debug==1) System.out.println("ControlMessage debug on: Answer:" +answer);
       // return String.format("", answer);
        return answer;
        
    }
    */

    private InetAddress address,ipAddress;
    private static long T1=0,T2=0;
    //private InetAddress ipAddress;
    private int port;
    
}
