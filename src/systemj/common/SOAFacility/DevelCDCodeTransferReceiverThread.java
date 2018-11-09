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
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.lib.input_Channel;

/**
 *
 * @author Atmojo
 */
public class DevelCDCodeTransferReceiverThread implements Runnable{

    private String CDName;
    private JSONObject CDMap = new JSONObject();
    private JSONObject CDServDesc = new JSONObject();
    
    public DevelCDCodeTransferReceiverThread(String CDName, JSONObject CDMap, JSONObject CDServDesc){
        this.CDName = CDName;
        this.CDMap = CDMap;
        this.CDServDesc = CDServDesc;
    }
    
    @Override
    public void run() {
        
        //NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        String currentOS = System.getProperty("os.name");
        
        //CDLCBuffer.setMigrationBusyFlag();
        
        //System.out.println("Creating ServSocket");
        
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
                    ss = new ServerSocket(9999,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
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
                        
                    
                    Socket socket = ss.accept();
                   
                    ObjectInputStream sInt = new ObjectInputStream(socket.getInputStream());
                   
                   //1. INIT Migration--> obtain migration
                   
                   String migrMsg = sInt.readObject().toString();
                   
                   if (migrMsg.equalsIgnoreCase("START")){
                      System.out.println("DevelCDCodeTransfer : Received Start message to initiate created CD code transfer" );
                       //receive ssname origin
                       
                        //String originSSName = sInt.readObject().toString();
                       
                       // 2.  receive info on how many CD class files
                       
                   //int file_amount = sInt.readInt();
                   
                   //  based on the amount of files to be sent, make a loop to receive each file
                   
                   //for (int j=0;j<file_amount;j++){
                       
                       //System.out.println("Attempting file number: " +j);
                       // 3. receive file name
                        String fileName = (String) sInt.readObject();
                        
                        
                        System.out.println("DevelCDCodeTransfer : Received file name of created CD code transfer" );
                        //4. get file size
                        int file_size = sInt.readInt();

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
                        
                        // create new CD descriptor
                        
                        
                       // String ClassNameOnly = fileName.split("\\.")[0];
                        
                       // ClockDomain newCD = (ClockDomain)Class.forName(ClassNameOnly).newInstance();
                       // newCD.setState("Active");
                       // newCD.setName(ClassNameOnly);
                        
                        //CDObjectsBuffer.AddCDObjToTempWeakMigBuffer(newCD.getName(), newCD);
                        
                        //
                       
                   //}
                   
                 //  CDObjectsBuffer.GroupSSCDObjsToTempWeakMig(originSSName);
                   
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
                   
                        /*
                   int ss_amount = sInt.readInt();
                   
                   JSONObject allTransferredMapping = new JSONObject();
                  
                   //JSONObject jsCurrSigChanMapping = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                   
                   //for(int j=0;j<ss_amount;j++){
                       
                        //String SSName = sInt.readObject().toString();
                        
                        String mapping = sInt.readObject().toString();
                        
                        //allTransferredMapping.put(SSName, new JSONObject(new JSONTokener(mapping)));
                        JSONObject TransferredMapping = new JSONObject(new JSONTokener(mapping));
                        
                        Enumeration keysTransMap = TransferredMapping.keys();
                        
                       // int op=0;
                        
                        while (keysTransMap.hasMoreElements()){
                            
                            String keyTM = keysTransMap.nextElement().toString();
                            
                            allTransferredMapping.put(keyTM,TransferredMapping.getJSONObject(keyTM));
                            
                        }
                        
                   //}
                   
                   //all these are transferred CDs signal channel mapping
                   
                   System.out.println("received sig chan mapping:" +allTransferredMapping.toPrettyPrintedString(2, 0));
                   
                   */
                   
                   //update sig chan mapping
                   
                   
                   
                   //Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                   //while (keysJSCurrSigChanMap.hasMoreElements()){
                       
                       //String SSName = keysJSCurrSigChanMap.nextElement().toString();
                       
                       //if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                           
                           //JSONObject jsAllCDs = jsCurrSigChanMapping.getJSONObject(SSName);
                           
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
                           /*
                           Enumeration keysJSAllCDs = jsAllCDs.keys();
                           
                           while (keysJSAllCDs.hasMoreElements()){
                               String CDName = keysJSAllCDs.nextElement().toString();
                               
                               JSONObject indivMap = jsAllCDs.getJSONObject(CDName);
                               
                               allTransferredMapping.put(CDName, indivMap);
                               
                           }
                           */
                          
                           //JSONObject currMapping = new JSONObject();
                           
                           //currMapping.put(SSName, allTransferredMapping);
                           
                           //save SS IP addr
                           
                           //SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(currMapping);
                           
                           
                          
                          //System.out.println("MigMsgReceiver, Current SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                          //System.out.println("MigMsgReceiver, Previous SigChan Mapping just after sigchan map transfer : "+SJSSCDSignalChannelMap.getPrevSignalChannelMapping());
                           
                           //SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCurrSigChanMapping);
                           
                       //}
                       
                   //}
                   
                   // 10. receive the service description, if any
                   
                   //InetAddress addr = InetAddress.getByName(SJServiceRegistry.getLocalSSAddr());//getLocalHostLANAddress();
                   
                   //boolean IsServRegExist = sInt.readBoolean();
                   
                   //if(IsServRegExist){
                       /*
                        String recServDescStr = sInt.readObject().toString();
                   
                        JSONObject recServDesc = new JSONObject(new JSONTokener(recServDescStr));
*/
                        // updating own service description

                        //JSONObject intReg = SJServiceRegistry.obtainInternalRegistry();

                        //int amount_serv_entity = intReg.length();

                        //int iter = amount_serv_entity+1;

                        /*
                        Enumeration keysRecServDesc = recServDesc.keys();
                        
                        while (keysRecServDesc.hasMoreElements()){

                            String keyRecServDesc = keysRecServDesc.nextElement().toString();

                            JSONObject recvdIndivServ = recServDesc.getJSONObject(keyRecServDesc);

                            //suppose to update nodeAddress attribute here?? 

                            recvdIndivServ.remove("nodeAddress");
                            recvdIndivServ.put("nodeAddress",addr.getHostAddress());


                            // include new service description here to the registry

                            intReg.put(recvdIndivServ.getString("serviceName"),recvdIndivServ);

                            iter++;

                        }
                        */

                        //System.out.println("Transferred serv desc:" +intReg.toPrettyPrintedString(2, 0));
                       
                   //}
                   
                   //Finish Service registry transfr
                   
                   //System.out.println("MigMsgReceiver, Current Service Desc : "+SJServiceRegistry.obtainCurrentRegistry().toPrettyPrintedString(2, 0));
                   
                   //String msg = sInt.readObject().toString();
                   
                  // System.out.println("MigMsgReceiver, Current SigChan Mapping : "+SJSSCDSignalChannelMap.getCurrentSignalChannelMapping());
                   
                   CDLCBuffer.AddDevelCreateCD(CDName, CDMap, CDServDesc);
                   //CDLCBuffer.setCDAmountChangedFlag();
                   
                   sInt.close();
                   
                   socket.close();
                   ss.close();
                      
                  }
                   
                } catch (IOException ex) {
                    
                    
                    ex.printStackTrace();
                    
                } catch (ClassNotFoundException ex2) {
                    ex2.printStackTrace();
                    
                    
                    
                } 
                    //catch (JSONException ex) {
                    //ex.printStackTrace();
                    
                    
                    
                //} 
                   catch (Exception ex) {
                    ex.printStackTrace();
                }
                // } catch (IllegalAccessException ex) {
                //     ex.printStackTrace();
               //  }
                    
              //  }
             
             CDLCBuffer.SetDevelThreadBusyFlag(false);
             
             
      //   }
    }
    
}
