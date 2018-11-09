/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.interfaces.GenericInterface;
import systemj.interfaces.Scheduler;

/**
 *
 * @author Udayanto
 */
public class LinkCreationSenderHSImpl {
    
    public String SendLinkCreationReq(String originSS, String destSS, String destAddr){
        
        SJSOAMessage sjmsg = new SJSOAMessage();
        
        String msg = sjmsg.CreateLinkCreationReqMsg(originSS, destSS, destAddr, SJSSCDSignalChannelMap.GetLocalSSAddr());
        
        System.out.println("LinkCreationSenderHSImpl, destADDr : " +destAddr+ "LocalAddr: " +SJSSCDSignalChannelMap.GetLocalSSAddr());
        
       // try {
            //System.out.println("ServReg: " +SJServiceRegistry.obtainCurrentRegistry());
        //} catch (JSONException ex) {
        ///    ex.printStackTrace();
        //}
        
        if(destAddr.equals(SJSSCDSignalChannelMap.GetLocalSSAddr())){
            String answer = TransceiveIsLocalLinkCreationFree(msg);
            
             return answer;
            
        } else {
            String answer = TransceiveIsRemoteLinkCreationFree(destAddr, msg);
            
             return answer;
            
        }
        
       
        
    }
    
    private String TransceiveIsLocalLinkCreationFree (String reqMigMsg){
        
        String answer = "";
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        
                        InetAddress ipAddress = InetAddress.getByName("224.0.0.100");
                        byte[] msg = new byte[65508];
                        byte[] packet = new byte[65508];
                        //byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        MulticastSocket s1 = new MulticastSocket(78);
                        MulticastSocket s2 = new MulticastSocket(66);
                        
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        
                        out.writeObject(reqMigMsg);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,78);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddress+"port:" +77);
                        s1.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        s2.setSoTimeout(700);
                        
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
                                        
                                        
                                        answer = js.getString("data");
                                        
                                        
                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                             }
                                   
                                   
                                   s1.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
                        answer="NOT OK";
			e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                        bex.printStackTrace();
                        answer="NOT OK";
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        answer="NOT OK";
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                 return answer;     
                   
                    
      //  }
    }
    
    private String TransceiveIsRemoteLinkCreationFree (String destAddress, String reqMigMsg){
        
        String answer = "";
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(destAddress);
                        byte[] msg = new byte[65508];
                        byte[] packet = new byte[65508];
                        //byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        MulticastSocket s1 = new MulticastSocket(78);
                        MulticastSocket s2 = new MulticastSocket(66);
                        
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        
                        out.writeObject(reqMigMsg);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,78);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +destAddress+"port:" +78);
                        if (infoDebug==1) System.out.println("Sending migration msg " +reqMigMsg);
                        s1.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        s2.setSoTimeout(1000);
                        
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
                                        
                                        
                                        answer = js.getString("data");
                                        
                                        
                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                             }
                                   
                                   
                                   s1.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
                        answer="NOT OK";
			e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound " );
                        bex.printStackTrace();
                        answer="NOT OK";
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        answer="NOT OK";
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                 return answer;     
                   
                    
      //  }
    }
    
    public InterfaceManager ExecuteLinkCreationHSWithLocalICPortExist(String destSS, String destAddr, InterfaceManager im, String LocalPortNum) 
    {
        
        //RTS expect CD names (correspond to file name)
        
        //when a migration occur and a new destination is detected, automatically provide TCP/IP link and make it available to use
        
        //Hashtable hash = (Hashtable) CDLCBuffer.GetAllHibernateCDWithTimer();
        
        ObjectOutputStream sOut;
        Socket socketSend;
        
        ServerSocket ss = null;
        
        if (ss==null || !ss.isBound()){
                try {
                    ss = new ServerSocket(8895,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                    System.out.println("LinkHS ServSocket created");
                } catch (BindException bex) {
                    System.out.println("LinkHS Port is already bound");
                    bex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
        
        try{
            
            //String destAddr = (String) hash.get("destinationAddress");
            //String destSS = (String) hash.get("destinationSubsystem");
            //String migType = (String) hash.get("migrationType");
            //String OS = (String) hash.get("OS");
            
            socketSend = new Socket(InetAddress.getByName(destAddr),8890);
            System.out.println("Connected to host " + socketSend.getInetAddress());
                        
            sOut = new ObjectOutputStream(socketSend.getOutputStream());
            
            
            
          //  Hashtable fileList = new Hashtable();
            
            //for (int j=0;j<vecAllCD.size();j++){
                
            //    String cdn = vecAllCD.get(j).toString();
            //    
            //    fileList.put(Integer.toString(j),cdn);
            //}
            
            //Hashtable fileList = (Hashtable) hash.get("CDName");
            
             //Enumeration keysHash = fileList.keys();
             
             //int file_amount = fileList.size();
             
            
            
             // 1. start message
             sOut.writeObject("START");
             sOut.flush();
             
             System.out.println("Sent START message Link Creation");
             
             //2. receive SSName from the partner
             
             Socket socketReceive = ss.accept();
             
             ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
             
             String SSPartnerName = sInt.readUTF();
             
              //3. receive partner's port allocation
              
              String PartnerPortAlloc = (String)sInt.readObject();
               
              System.out.println("Receive PartnerPortAlloc " +PartnerPortAlloc);
              
              //convert to JSONObject
              
              JSONObject jsRemotePortAlloc = new JSONObject(new JSONTokener(PartnerPortAlloc));
              
              //get Local port allocation
              
              JSONObject jsLocalPortAlloc = TCPIPLinkRegistry.GetSSAndPortPair();
              
              //Combine both allocation list
              
              Enumeration keysJSRem = jsRemotePortAlloc.keys();
              
              JSONObject allPortAlloc = jsLocalPortAlloc;
              
              while(keysJSRem.hasMoreElements()){
                  String portNumPartner = keysJSRem.nextElement().toString();
                  
                  allPortAlloc.put(portNumPartner, jsRemotePortAlloc.getString(portNumPartner));
                  
              }
              
              JSONObject Portpair = new JSONObject(); // key = portNum, value = SSName
              
              //iterate from the smaller allocated port number for links : 40001 - interface to contact the SS IC
              
              int portToAlloc;
              
              Interconnection ic = im.getInterconnection();
              
              Portpair.put(LocalPortNum,SJSSCDSignalChannelMap.getLocalSSName());
              
              /*
              {
                  JSONObject jsPortSSPair = TCPIPLinkRegistry.GetSSAndPortPair();
                  
                  Enumeration keysPortPair = jsPortSSPair.keys();
                  
                  while(keysPortPair.hasMoreElements()){
                      String portNum = keysPortPair.nextElement().toString();
                      
                      String SSName = jsPortSSPair.getString(portNum);
                      
                      if(SSName.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                          Portpair.put(portNum, SJSSCDSignalChannelMap.getLocalSSName());
                          
                      }
                      
                  }
                  
              }
              */
              
              //iterate from the smaller allocated port number for links : 40001 - interface to contact remote SS
              
              Enumeration keysPortPair = Portpair.keys();
              
              boolean PartnerPortExist = false;
              
              String PartnerSSPortNum="";
              
              while(keysPortPair.hasMoreElements()){
                  String IndivPortNum = keysPortPair.nextElement().toString();
                  
                  String SSNameOfPortNum = Portpair.getString(IndivPortNum);
                  
                  if(SSNameOfPortNum.equals(SSPartnerName)){
                      PartnerPortExist = true;
                      break;
                  }
                  
              }
              
              if(!PartnerPortExist){
                  
                  for(int portNum=40001;portNum<65536;portNum++){
                  
                  if(!allPortAlloc.has(Integer.toString(portNum))){
                      portToAlloc=portNum;
                      //allPortAlloc.put(Integer.toString(portNum),SSPartnerName);
                      Portpair.put(Integer.toString(portNum),SSPartnerName);
                      TCPIPLinkRegistry.AddSSAndPortPair(Integer.toString(portToAlloc), SSPartnerName);
                      break;
                  }
                  
                }
                  
              }
              
              
              
              
              String PortpairToSend = Portpair.toString();
              
              System.out.println("PortpairToSend " +PortpairToSend);
              
              // 4. Send back SSName and then pair
              
              sOut.writeUTF(SJSSCDSignalChannelMap.getLocalSSName());
              sOut.flush();
              
              sOut.writeUTF(PortpairToSend);
              sOut.flush();
              
              JSONObject jsPairPortAlloc = Portpair;
                       
                       Enumeration keysPair = jsPairPortAlloc.keys();
                       
                       //Vector GCTVec = new Vector();
                       
                       //Interconnection ic = im.getInterconnection();
                       
                       Interconnection.Link linko = new Interconnection.Link();
                       
                       while(keysPair.hasMoreElements()){
                           String portNum = keysPair.nextElement().toString();
                           
                           String SS = jsPairPortAlloc.getString(portNum);
                           
                           String args = destAddr+":"+portNum;
                           
                           GenericInterface gct = (GenericInterface)Class.forName("systemj.desktop.TCPIPInterface").newInstance();
                           Hashtable ht = new Hashtable();
                           ht.put("Class", "systemj.desktop.TCPIPInterface");
                                        
                           ht.put("Args", args);
                           ht.put("SubSystem", SS);
                           gct.configure(ht);
                           linko.addInterface(SS, gct);
                           
                           System.out.println("Link interface addr: " +args+" is created!");
                           
                           
                           
                           //GCTVec.addElement(gct);
                           
                       }
                       
                       ic.addLink(linko, false);
                       
                       im.setInterconnection(ic);
                       
                       Vector localGCTs = ic.getRemoteDestinationInterfaces(SJSSCDSignalChannelMap.getLocalSSName());
                       
                       for(int k=0;k<localGCTs.size();k++){

                                GenericInterface gct = (GenericInterface)localGCTs.get(k);

                                gct.invokeReceivingThread();
                                gct.setInterfaceManager(im);

                       }
                       
                       IMBuffer.SaveInterfaceManagerConfig(im);
              
                       sInt.close();
                       sOut.close();
                       socketReceive.close();
                       socketSend.close();
                       ss.close();
                
                
                //CDLCBuffer.setChangedFlag();
                //CDLCBuffer.setCDAmountChangedFlag();
                
         
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            
            //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch(IOException ex2){
            ex2.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch (JSONException ex3) {
            ex3.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        /*
        catch (ClassNotFoundException ex) {
           ex.printStackTrace();
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        catch (InstantiationException ex) {
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        }
        */
        catch (Exception ex){
            ex.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
            //CDLCBuffer.updateMigrationStatus("Failed");
        }
        
        //Vector vec = new Vector();
        
        //vec.addElement(im);
        //vec.addElement(sc);
        return im;
        //return vec;
           
        }
    
    public InterfaceManager ExecuteLinkCreationHS(String destSS, String destAddr, InterfaceManager im) 
    {
        
        //RTS expect CD names (correspond to file name)
        
        //when a migration occur and a new destination is detected, automatically provide TCP/IP link and make it available to use
        
        //Hashtable hash = (Hashtable) CDLCBuffer.GetAllHibernateCDWithTimer();
        
        ObjectOutputStream sOut;
        Socket socketSend;
        
        ServerSocket ss = null;
        
        if (ss==null || !ss.isBound()){
                try {
                    ss = new ServerSocket(8895,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                    System.out.println("LinkHS ServSocket created");
                } catch (BindException bex) {
                    System.out.println("LinkHS Port is already bound");
                    bex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
        
        try{
            
            //String destAddr = (String) hash.get("destinationAddress");
            //String destSS = (String) hash.get("destinationSubsystem");
            //String migType = (String) hash.get("migrationType");
            //String OS = (String) hash.get("OS");
            
            socketSend = new Socket(InetAddress.getByName(destAddr),8890);
            System.out.println("Connected to host " + socketSend.getInetAddress());
                        
            sOut = new ObjectOutputStream(socketSend.getOutputStream());
            
            
            
          //  Hashtable fileList = new Hashtable();
            
            //for (int j=0;j<vecAllCD.size();j++){
                
            //    String cdn = vecAllCD.get(j).toString();
            //    
            //    fileList.put(Integer.toString(j),cdn);
            //}
            
            //Hashtable fileList = (Hashtable) hash.get("CDName");
            
             //Enumeration keysHash = fileList.keys();
             
             //int file_amount = fileList.size();
             
            
            
             // 1. start message
             sOut.writeObject("START");
             sOut.flush();
             
             System.out.println("Sent START message Link Creation");
             
             //receive SSName from the partner
             
             Socket socketReceive = ss.accept();
             
             ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
             
             String SSPartnerName = sInt.readUTF();
             
             System.out.println("Receive SSPartnerName:  " +SSPartnerName);
             
              //receive partner's port allocation
              
              String PartnerPortAlloc = (String)sInt.readObject();
               
               System.out.println("Receive PartnerPortAlloc " +PartnerPortAlloc);
              
              
              
              //convert to JSONObject
              
              JSONObject jsRemotePortAlloc = new JSONObject(new JSONTokener(PartnerPortAlloc));
              
              //get Local port allocation
              
              JSONObject jsLocalPortAlloc = TCPIPLinkRegistry.GetSSAndPortPair();
              
              //Combine both allocation list
              
              Enumeration keysJSRem = jsRemotePortAlloc.keys();
              
              JSONObject allPortAlloc = jsLocalPortAlloc;
              
              while(keysJSRem.hasMoreElements()){
                  String portNumPartner = keysJSRem.nextElement().toString();
                  
                  allPortAlloc.put(portNumPartner, jsRemotePortAlloc.getString(portNumPartner));
                  
              }
              
              JSONObject Portpair = new JSONObject(); // key = portNum, value = SSName
              
              //iterate from the smaller allocated port number for links : 40001 - interface to contact the SS IC
              
              int portToAlloc;
              
              Interconnection ic = im.getInterconnection();
              
              Vector localIC = ic.getRemoteDestinationInterfaces(SJSSCDSignalChannelMap.getLocalSSName());
              
              if(localIC.size()==0){
                  
                  for(int portNum=40001;portNum<65536;portNum++){
                  
                  if(!allPortAlloc.has(Integer.toString(portNum))){
                      portToAlloc=portNum;
                      allPortAlloc.put(Integer.toString(portToAlloc), SJSSCDSignalChannelMap.getLocalSSName());
                      Portpair.put(Integer.toString(portToAlloc), SJSSCDSignalChannelMap.getLocalSSName());
                      TCPIPLinkRegistry.AddSSAndPortPair(Integer.toString(portToAlloc), SJSSCDSignalChannelMap.getLocalSSName());
                      break;
                  }
                  
                }
                  
              } else {
                  
                  JSONObject jsPortSSPair = TCPIPLinkRegistry.GetSSAndPortPair();
                  
                  Enumeration keysPortPair = jsPortSSPair.keys();
                  
                  while(keysPortPair.hasMoreElements()){
                      String portNum = keysPortPair.nextElement().toString();
                      
                      String SSName = jsPortSSPair.getString(portNum);
                      
                      if(SSName.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                          Portpair.put(portNum, SJSSCDSignalChannelMap.getLocalSSName());
                          
                      }
                      
                  }
                  
              }
              
              
              
              //iterate from the smaller allocated port number for links : 40001 - interface to contact remote SS
              Enumeration keysPortPair = Portpair.keys();
              
              boolean PartnerPortExist = false;
              
              int PartnerSSPortNum=0;
              
              while(keysPortPair.hasMoreElements()){
                  String PortNum = keysPortPair.nextElement().toString();
                  
                  String SSNameOfPortNum = Portpair.getString(PortNum);
                  
                  if(SSNameOfPortNum.equals(SSPartnerName)){
                      PartnerPortExist = true;
                      PartnerSSPortNum = Integer.parseInt(PortNum);
                      break;
                  }
                  
              }
              
              if(!PartnerPortExist){
                  
                  for(int portNum=40001;portNum<65536;portNum++){
                  
                    if(!allPortAlloc.has(Integer.toString(portNum))){
                      portToAlloc=portNum;
                      //allPortAlloc.put(Integer.toString(portNum),SSPartnerName);
                      Portpair.put(Integer.toString(portNum),SSPartnerName);
                      TCPIPLinkRegistry.AddSSAndPortPair(Integer.toString(portToAlloc), SSPartnerName);
                      break;
                    }
                  
                  }
                  
              } else {
                   TCPIPLinkRegistry.AddSSAndPortPair(Integer.toString(PartnerSSPortNum), SSPartnerName);
              }
              
              String PortpairToSend = Portpair.toString();
              
              sOut.writeUTF(PortpairToSend);
              sOut.flush();
              
              System.out.println("Sent PortPairToSend " +PortpairToSend);
              
              JSONObject jsPairPortAlloc = new JSONObject(new JSONTokener(PortpairToSend));
                       
                       Enumeration keysPair = jsPairPortAlloc.keys();
                       
                       //Interconnection ic = im.getInterconnection();
                       
                       if(destAddr.equals(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                           destAddr = "127.0.0.1";
                       }
                       
                       //Vector GCTVec = new Vector();
                       
                       Interconnection.Link linko = new Interconnection.Link();
                       
                       while(keysPair.hasMoreElements()){
                           String portNum = keysPair.nextElement().toString();
                           
                           String SS = jsPairPortAlloc.getString(portNum);
                           
                           String args = destAddr+":"+portNum;
                           
                           GenericInterface gct = (GenericInterface)Class.forName("systemj.desktop.TCPIPInterface").newInstance();
                           Hashtable ht = new Hashtable();
                           ht.put("Class", "systemj.desktop.TCPIPInterface");
                                        
                           ht.put("Args", args);
                           ht.put("SubSystem", SS);
                           gct.configure(ht);
                           linko.addInterface(SS, gct);
                           
                           System.out.println("Link interface addr: " +args+" is created!");
                           
                       }
                       
                       ic.addLink(linko, false);
                       
                       im.setInterconnection(ic);
                       
                       Vector localGCTs = ic.getRemoteDestinationInterfaces(SJSSCDSignalChannelMap.getLocalSSName());
                       
                       for(int k=0;k<localGCTs.size();k++){

                                GenericInterface gct = (GenericInterface)localGCTs.get(k);

                                gct.invokeReceivingThread();
                                gct.setInterfaceManager(im);

                       }
                       
                       /*
                       for(int k=0;k<GCTVec.size();k++){

                                GenericInterface gct = (GenericInterface)GCTVec.get(k);

                                gct.invokeReceivingThread();
                                gct.setInterfaceManager(im);

                       }
                       */
                       
                       IMBuffer.SaveInterfaceManagerConfig(im);
              
                       sOut.close();
                       sInt.close();
                       
                       socketReceive.close();
                       ss.close();
                       socketSend.close();
                       
                
                
                //CDLCBuffer.setChangedFlag();
                //CDLCBuffer.setCDAmountChangedFlag();
                
         
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            
            //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch(IOException ex2){
            ex2.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch (JSONException ex3) {
            ex3.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        /*
        catch (ClassNotFoundException ex) {
           ex.printStackTrace();
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        catch (InstantiationException ex) {
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        }
        */
        catch (Exception ex){
            ex.printStackTrace();
             //CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
            //CDLCBuffer.updateMigrationStatus("Failed");
        }
        
        //Vector vec = new Vector();
        
        //vec.addElement(im);
        //vec.addElement(sc);
        return im;
        //return vec;
           
        }
    
}
