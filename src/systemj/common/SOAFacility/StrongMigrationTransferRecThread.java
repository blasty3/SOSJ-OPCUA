/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
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
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.interfaces.GenericInterface;
import systemj.lib.input_Channel;

/**
 *
 * @author Udayanto
 */
public class StrongMigrationTransferRecThread implements Runnable{

    //ServerSocket ss;
    //Socket socket;
    
    String sourceSS=null;
    
    @Override
    public void run() {
        
        
        
        //NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        String currentOS = System.getProperty("os.name");
        
        System.out.println("Creating ServSocket");
        
        ServerSocket ss = null;
        //try {
        //    ipAddr = getLocalHostLANAddress().getHostAddress();
        //    System.out.println("Node Addr: " +ipAddr);
//
        //} catch (UnknownHostException ex) {
        //    Logger.getLogger(MigrationMsgReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        //}
        
      //   while(true){
             
        //     String connStat = netcheck.CheckNetworkConn("192.168.1.1", 1300);
                
                //System.out.println("MessageReceiver, ConnectionStat: " +connStat);
                
           //     if (connStat.equalsIgnoreCase("Connected")){
                    
            if (ss==null || !ss.isBound()){
                try {
                    
                    ss = new ServerSocket(8888,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                    System.out.println("Migration ServSocket created");
                } catch (BindException bex) {
                    System.out.println("Port is already bound");
                    bex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
                    try {
                        
                   //ServerSocket ss = new ServerSocket(8888, 50, InetAddress.getByName(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        ObjectOutputStream sOut;
                        Socket socketSend;
                        
                    
                    Socket socketReceive = ss.accept();
                   
                    ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
                   
                   //1. INIT Migration--> obtain migration
                   
                   String StartMsg = sInt.readObject().toString();
                   
                   //1b. Get IP Addr
                   
                   String migrAddr = sInt.readObject().toString();
                   
                   
                   socketSend = new Socket(InetAddress.getByName(migrAddr),8887);
                   
                   sOut = new ObjectOutputStream(socketSend.getOutputStream());
                   
                   System.out.println("Connected to host " + socketSend.getInetAddress());
                   
                   if (StartMsg.equalsIgnoreCase("START")){
                      
                       //receive origin SS first
                       
                    String originSSName = sInt.readObject().toString();
                    
                    //InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
                    
                       
                       // 2.  receive info on how many CD class files
                       
                   int cd_file_amount = sInt.readInt();
                   
                   int cd_obj_amount = sInt.readInt();
                   
                   //  based on the amount of files to be sent, make a loop to receive each file
                   
                   
                   //receive the amount of user class lib to receive
                   
                   int user_class_file_amount = sInt.readInt();
                   
                   for(int k=0;k<user_class_file_amount;k++){
                       
                       String depLoc = sInt.readUTF();
                       
                       String path = System.getProperty("user.dir");
                       String [] splittedDepLoc = depLoc.split("\\.");
                       depLoc = depLoc.replace(".", "/");
                       path = path.replace("\\", "/");
                       String fullPath = path+"/"+depLoc+".class";
                       
                       //String userClassFileName = splittedDepLoc[splittedDepLoc.length-1]; 
                       
                       File libFileClass = new File(fullPath);
                       
                       if(libFileClass.isFile()){
                           
//                          //class lib present, not include
                           sOut.writeBoolean(false);
                           sOut.flush();
                           
                       } else {
                           
                           //include
                           sOut.writeBoolean(true);
                            sOut.flush();
                            
                            int file_size = sInt.readInt();

                            System.out.println("file size: " +file_size);

                            // create file output stream
                            FileOutputStream outStr = new FileOutputStream(libFileClass);

                            // create buffered output
                            BufferedOutputStream bos = new BufferedOutputStream(outStr);

                            byte[] buffer = null;
                            int total_read_len = 0;

                            //5. receiving file loop
                            while(sInt.readBoolean()){
                                buffer = (byte[]) sInt.readObject();
                                total_read_len += buffer.length;
                                bos.write(buffer);
                                bos.flush();
                                System.out.println("Receive: " + (float)total_read_len/file_size*100 + "%");

                            }
                            bos.close();
                            
                           
                       } 
                       
                   }
                   
                   
                   for (int j=0;j<cd_file_amount;j++){
                       
                       System.out.println("Attempting file number: " +j);
                       // 3. receive file name
                        String fileName = (String) sInt.readObject();
                        
                        // open file
                //File file = new File("/home/root/Desktop/Udayanto/SysJ/TCPReceiveTest/" + fileName);
                        String filePath = System.getProperty("user.dir");
                        System.out.println("File path: " +filePath+" with fileName: " +fileName);
                        
                        File file;
                        
                        if (currentOS.equalsIgnoreCase("Linux")){
                            file = new File(filePath+"/"+fileName);
                        } else {
                            file = new File(filePath+"\\"+fileName);
                        }
                      
                        if(file.isFile()){
                            sOut.writeBoolean(false);
                            sOut.flush();
                        } else {
                            sOut.writeBoolean(true);
                            sOut.flush();
                            //4. get file size
                            int file_size = sInt.readInt();

                            System.out.println("file size: " +file_size);

                            // create file output stream
                            FileOutputStream outStr = new FileOutputStream(file);

                            // create buffered output
                            BufferedOutputStream bos = new BufferedOutputStream(outStr);

                            byte[] buffer = null;
                            int total_read_len = 0;

                            //5. receiving file loop
                            while(sInt.readBoolean()){
                                buffer = (byte[]) sInt.readObject();
                                total_read_len += buffer.length;
                                bos.write(buffer);
                                bos.flush();
                                System.out.println("Receive: " + (float)total_read_len/file_size*100 + "%");

                            }
                            bos.close();
                            
                        }
                        
                        
                        
                        // receive CD descriptor
                        
                        
                        
                        //
                       
                   }
                   
                   for (int y=0;y<cd_obj_amount;y++){
                    
                        ClockDomain newCD = (ClockDomain)sInt.readObject();
                        
                       
                        CDObjectsBuffer.AddCDObjToTempStrongMigBuffer(newCD.getName(), newCD);
                   
                   }
                  
                    CDObjectsBuffer.GroupSSCDObjsToTempStrongMig(originSSName);
                  
                   
                   //receive cd inst in hashtables, happens only if it's a weak mobility (with data)
                   
                   /*
                   Hashtable AllRecCDInst = (Hashtable)sInt.readObject();
                   
                   Enumeration keysMigdCDInst = AllRecCDInst.keys();
                   
                   while(keysMigdCDInst.hasMoreElements()){
                       
                       String keyCDname = keysMigdCDInst.nextElement().toString();
                       
                       ClockDomain cd = (ClockDomain)AllRecCDInst.get(keyCDname);
                       
                       CDObjectsBuffer.AddCDInstancesToBuffer(keyCDname, cd);
                       
                       
                       //String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
                       //SJSSCDSignalChannelMap.addCDLocation(keyCDname, localSSName);
                       
                       
                   }
                   */
                   
                   //System.out.println("MigMsgReceiver, Current SigChan Mapping before sigchan map transfer : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                   //System.out.println("MigMsgReceiver, Previous SigChan Mapping before sigchan map transfer : "+SJSSCDSignalChannelMap.getPrevSignalChannelMapping());
                   
                   // 6. receive signal channel mapping
                   
                   int ss_amount = sInt.readInt();
                   
                   JSONObject allTransferredMapping = new JSONObject();
                  
                   JSONObject jsCurrSigChanMapping = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                   
                   for(int j=0;j<ss_amount;j++){
                       
                        //String SSName = sInt.readObject().toString();
                        
                        String mapping = sInt.readObject().toString();
                        
                        //allTransferredMapping.put(SSName, new JSONObject(new JSONTokener(mapping)));
                        JSONObject TransferredMapping = new JSONObject(new JSONTokener(mapping));
                        
                        Enumeration keysTransMap = TransferredMapping.keys();
                        
                        while (keysTransMap.hasMoreElements()){
                            
                            String keyTM = keysTransMap.nextElement().toString();
                            
                            //JSONObject Map1 = TransferredMapping.getJSONObject(keyTM);
                            //String sourceSSName = Map1.getString("CDSSLocation");
                            //String CDClassName = Map1.getString("CDClassName");
                            //Map1.put("CDSSLocation", SJSSCDSignalChannelMap.getLocalSSName());
                            
                            
                            
                            //sourceSS = sourceSSName;
                            
                            //if(op==0){
                                
                                //String InetSource = socket.getInetAddress().getHostAddress(); //save this somewhere
                           
                                //JSONObject IndivMap = TransferredMapping.getJSONObject(keyTM);
                                
                                //String sourceSSName = IndivMap.getString("CDSSLocation");
                                
                                //SJSSCDSignalChannelMap.addInterconnectionSSAddr(sourceSSName, InetSource);
                                
                                //op++;
                            //}
                            
                            //Weak mobility, get CD instances and save on data structures
                            
                            //happens only if it's a code-only mobility
                            
                            //Enumeration keyMigdServ = 
                            
                            //ClockDomain cd;
                           //     cd = (ClockDomain) Class.forName(CDClassName).newInstance();
                            //    cd.setName(keyTM);
                               
                            //SJSSCDSignalChannelMap.UpdateCDInstancesMap(keyTM, cd);
                            //String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
                            //SJSSCDSignalChannelMap.addCDLocation(keyTM, localSSName);
                            // CD instances
                            
                            
                            
                            allTransferredMapping.put(keyTM,TransferredMapping.getJSONObject(keyTM));
                            
                            
                            
                            
                            /*
                            
                            //outputs
                            
                            JSONObject jsSChansOutMap = jsSChansMap.getJSONObject("outputs");
                            
                            if(!jsSChansOutMap.isEmpty()){
                                 Enumeration keysJSChansOutMap = jsSChansOutMap.keys();
                                 
                                 while(keysJSChansOutMap.hasMoreElements()){
                                     
                                     String OutChanName = keysJSChansOutMap.nextElement().toString();
                                     
                                     JSONObject jsSOutChanDet = jsSChansOutMap.getJSONObject(OutChanName);
                                     
                                     String DestCDLoc = jsSOutChanDet.getString("To");
                                     
                                     String[] cnames = DestCDLoc.split("\\.");
                                     
                                     Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                                        while (keysJSCurrSigChanMap.hasMoreElements()){
                                            
                                            String SSName = keysJSCurrSigChanMap.nextElement().toString();
                                            
                                            if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                                                
                                                jsSOutChanDet.put("To",SSName+"."+cnames[1]);
                                                
                                            }
                                            
                                        }
                                     
                                     
                                     //get source location "From" and modify the SSName to the current SSName
                                     
                                     
                                 }
                                 
                            }   
                            
                            //modify AChannels mapping SS
                            
                            JSONObject jsAChansMap =  jsSigChansMap.getJSONObject("AChannels");
                            
                            JSONObject jsAChansInMap = jsAChansMap.getJSONObject("inputs");
                            
                            if(!jsAChansInMap.isEmpty()){
                                 Enumeration keysJSAChansInMap = jsAChansInMap.keys();
                                 
                                 while(keysJSAChansInMap.hasMoreElements()){
                                     
                                     String InChanName = keysJSAChansInMap.nextElement().toString();
                                     
                                     JSONObject jsAInChanDet = jsAChansInMap.getJSONObject(InChanName);
                                     
                                     String SourceCDLoc = jsAInChanDet.getString("From");
                                     
                                     String[] pnames = SourceCDLoc.split("\\.");
                                     
                                     Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                                        while (keysJSCurrSigChanMap.hasMoreElements()){
                                            
                                            String SSName = keysJSCurrSigChanMap.nextElement().toString();
                                            
                                            if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                                                
                                                jsAInChanDet.put("From",SSName+"."+pnames[1]);
                                                
                                            }
                                            
                                        }
                                     
                                     
                                     //get source location "From" and modify the SSName to the current SSName
                                     
                                     
                                 }
                                 
                            }
                            
                            //outputs
                            
                            JSONObject jsAChansOutMap = jsAChansMap.getJSONObject("outputs");
                            
                            if(!jsAChansOutMap.isEmpty()){
                                 Enumeration keysJSAChansOutMap = jsAChansOutMap.keys();
                                 
                                 while(keysJSAChansOutMap.hasMoreElements()){
                                     
                                     String OutChanName = keysJSAChansOutMap.nextElement().toString();
                                     
                                     JSONObject jsAOutChanDet = jsSChansOutMap.getJSONObject(OutChanName);
                                     
                                     String DestCDLoc = jsAOutChanDet.getString("To");
                                     
                                     String[] cnames = DestCDLoc.split("\\.");
                                     
                                     Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                                        while (keysJSCurrSigChanMap.hasMoreElements()){
                                            
                                            String SSName = keysJSCurrSigChanMap.nextElement().toString();
                                            
                                            if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                                                
                                                jsAOutChanDet.put("To",SSName+"."+cnames[1]);
                                                
                                            }
                                            
                                        }
                                     
                                     
                                     //get source location "From" and modify the SSName to the current SSName
                                     
                                     
                                 }
                                 
                            }
                            */
                            
                        }
                        
                   }
                   
                   //all these are transferred CDs signal channel mapping
                   
                   System.out.println("received sig chan mapping:" +allTransferredMapping.toPrettyPrintedString(2, 0));
                   
                   
                   
                   //update sig chan mapping
                   
                   
                   
                   Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                   while (keysJSCurrSigChanMap.hasMoreElements()){
                       
                       String SSName = keysJSCurrSigChanMap.nextElement().toString();
                       
                       if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                           
                           JSONObject jsAllCDs = jsCurrSigChanMapping.getJSONObject(SSName);
                           
                           /*
                           Enumeration keysJSAlltransMap = allTransferredMapping.keys();
                           
                       //     System.out.println("MigMsgReceiver, Current SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                       //   System.out.println("MigMsgReceiver, Previous SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getPrevSignalChannelMapping());
                           
                           while(keysJSAlltransMap.hasMoreElements()){
                               
                               String CDName = keysJSAlltransMap.nextElement().toString();
                               
                               JSONObject jsIndivCDSigChanMap = allTransferredMapping.getJSONObject(CDName);
                               
                               
                               
                               //jsAllCDs.put(CDName, allTransferredMapping.getJSONObject(CDName));
                               
                           }
                                   */
                           
                           Enumeration keysJSAllCDs = jsAllCDs.keys();
                           
                           while (keysJSAllCDs.hasMoreElements()){
                               String CDName = keysJSAllCDs.nextElement().toString();
                               
                               JSONObject indivMap = jsAllCDs.getJSONObject(CDName);
                               
                               allTransferredMapping.put(CDName, indivMap);
                               
                           }
                          
                           JSONObject currMapping = new JSONObject();
                           
                           currMapping.put(SSName, allTransferredMapping);
                           
                           //save SS IP addr
                           
                           SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(currMapping);
                           
                           
                          
                          System.out.println("MigMsgReceiver, Current SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                          System.out.println("MigMsgReceiver, Previous SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getPrevSignalChannelMapping());
                           
                           //SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCurrSigChanMapping);
                           
                       }
                       
                   }
                   
                   // 10. receive the service description, if any
                   
                   InetAddress addr = InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr());//getLocalHostLANAddress();
                   
                   boolean IsServRegExist = sInt.readBoolean();
                   
                   if(IsServRegExist){
                       
                        String recServDescStr = sInt.readObject().toString();
                   
                        JSONObject recServDesc = new JSONObject(new JSONTokener(recServDescStr));

                        // updating own service description

                        JSONObject intReg = SJServiceRegistry.obtainInternalRegistry();

                        int amount_serv_entity = intReg.length();

                        int iter = amount_serv_entity+1;

                        Enumeration keysRecServDesc = recServDesc.keys();

                        while (keysRecServDesc.hasMoreElements()){

                            String keyRecServDesc = keysRecServDesc.nextElement().toString();

                            JSONObject recvdIndivServ = recServDesc.getJSONObject(keyRecServDesc);

                            //suppose to update nodeAddress attribute here?? 

                            //recvdIndivServ.remove("nodeAddress");
                            //recvdIndivServ.put("nodeAddress",addr.getHostAddress());


                            // include new service description here to the registry

                            intReg.put(recvdIndivServ.getString("serviceName"),recvdIndivServ);

                            iter++;

                        }

                        System.out.println("Transferred serv desc:" +intReg.toPrettyPrintedString(2, 0));
                       
                   }
                   
                   //Finish Service registry transfr
                   
                   System.out.println("MigMsgReceiver, Current Service Desc : "+SJServiceRegistry.obtainCurrentRegistry().toPrettyPrintedString(2, 0));
                   
                   String msg = sInt.readObject().toString();
                   
                  // System.out.println("MigMsgReceiver, Current SigChan Mapping : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                   
                   
                   //CDLCBuffer.setCDAmountChangedFlag();
                   
                   sInt.close();
                   
                   socketReceive.close();
                   ss.close();
                   
                   
                      
                  }
                   
                } catch (IOException ex) {
                    
                    
                    ex.printStackTrace();
                    
                } catch (ClassNotFoundException ex2) {
                    ex2.printStackTrace();
                    
                    
                    
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    
                    
                    
                } 
                 //  catch (InstantiationException ex) {
                //    ex.printStackTrace();
                // } catch (IllegalAccessException ex) {
                //     ex.printStackTrace();
               //  }
                    
              //  }
             
             CDLCBuffer.releaseMigrationBusyFlag();
             
             CDLCBuffer.SetStrongMigrationDoneFlag();
             
      //   }
    }
    
    private boolean CheckLocalPortAvail(int port){
        
        try{
            
            InetAddress inetaddr = InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr());  //getLocalHostLANAddress();
            
            Socket socket = new Socket(inetaddr, port);
            
            socket.close();
            
            ServerSocket serverSocket = new ServerSocket(port, 50, inetaddr);
			
            serverSocket.close();
                   
            return true;
                   
        } catch (BindException bex) {
            System.out.println("port" +port+ "already bound");
            return false;
        }catch (IOException ex) {
            return false;
        }
                       
    }
    
    /*
    private InetAddress getLocalHostLANAddress() throws UnknownHostException {
    try {
        InetAddress candidateAddress = null;
        // Iterate all NICs (network interface cards)...
        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            // Iterate all IP addresses assigned to each card...
            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                if (!inetAddr.isLoopbackAddress()) {

                    if (inetAddr.isSiteLocalAddress()) {
                        // Found non-loopback site-local address. Return it immediately...
                        if (!inetAddr.getHostAddress().equalsIgnoreCase("192.168.7.2")){
                            return inetAddr;
                        } 
                       
                    }
                    else if (candidateAddress == null) {
                        // Found non-loopback address, but not necessarily site-local.
                        // Store it as a candidate to be returned if site-local address is not subsequently found...
                        candidateAddress = inetAddr;
                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                        // only the first. For subsequent iterations, candidate will be non-null.
                    }
                }
            }
        }
        if (candidateAddress != null) {
            // We did not find a site-local address, but we found some other non-loopback address.
            // Server might have a non-site-local address assigned to its NIC (or it might be running
            // IPv6 which deprecates the "site-local" concept).
            // Return this non-loopback candidate address...
            return candidateAddress;
        }
        // At this point, we did not find a non-loopback address.
        // Fall back to returning whatever InetAddress.getLocalHost() returns...
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }
        return jdkSuppliedAddress;
    }
    catch (Exception e) {
        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
        unknownHostException.initCause(e);
        throw unknownHostException;
    }
    
    
    
}
    */
    
    
}
