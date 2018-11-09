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
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
import systemj.common.CyclicScheduler;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.interfaces.GenericInterface;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Scheduler;

/**
 *
 * @author Udayanto
 */
public class ClockDomainLifeCycleMigrationImpl {

    public Vector InitHandshakeAndTransfer(JSONObject jsLocalCDs, String DestinationSS, String DestinationAddr, String MigType, InterfaceManager im){
        
        //ClockDomainLifeCycleSignalImpl cdlcsigimpl = new ClockDomainLifeCycleSignalImpl();
        
        Vector vec = new Vector();
        
        SJSOAMessage sjsoa = new SJSOAMessage();
        
        // when implementation ready, change into searching in the Registry
            
        // end
        
                Vector vecAllCD = CDLCBuffer.GetAllCDNameOfMigTypeAndDestSS(DestinationSS, MigType);
                        
                        Vector allMigObjCD = CDLCBuffer.GetMigratingCDInsts();
                        
                        Vector migObjCDs = new Vector();
                        
                        for(int num=0;num<vecAllCD.size();num++){
                            
                            String cdname = (String)vecAllCD.get(num);
                            
                            CDLCBuffer.AddCDMacroState(cdname, "Migrating");
                            
                            for(int e=0;e<allMigObjCD.size();e++){
                                ClockDomain cdInst = (ClockDomain)allMigObjCD.get(e);
                                
                                if(cdInst.getName().equals(cdname)){
                                    migObjCDs.addElement(cdInst);
                                    
                                    CDLCBuffer.RemoveMigratingCDObjFromBufferCDInsts(cdname);
                                    
                                }
                                
                            }
                           
                        }
                        
                        
        
            if(MigType.equalsIgnoreCase("strong")){
                
            
                        String migMsg = sjsoa.ConstructReqStrongMigrationMessage(DestinationSS, SJSSCDSignalChannelMap.getLocalSSName());
            
                        
            
                        //CDLCBuffer.SetCDLCMigrationFlagBusy();
                        
                        Thread th = new Thread(new CDLCMigrationTransferThread(jsLocalCDs,migMsg,MigType, DestinationAddr, DestinationSS, vecAllCD,migObjCDs,im));
                        
                        th.start();
                        
                        //CDLCBuffer.RemoveReqMigrate(DestinationSS, MigType);
            
            
        } else if(MigType.equalsIgnoreCase("weak")){
            
             String migMsg = sjsoa.ConstructReqWeakMigrationMessage(DestinationSS, SJSSCDSignalChannelMap.getLocalSSName());

             //CDLCBuffer.SetCDLCMigrationFlagBusy();
                        
                        Thread th = new Thread(new CDLCMigrationTransferThread(jsLocalCDs,migMsg,MigType, DestinationAddr, DestinationSS, vecAllCD,migObjCDs,im));
                        
                        th.start();
                        
                        //CDLCBuffer.RemoveReqMigrate(DestinationSS, MigType);
             
        }
        
        return vec;
        
    }
    
    /*
    public Vector ExecuteMigrationTransfer(String migType, String destAddr, String destSS, InterfaceManager im, Scheduler sc) 
    {
        
        //RTS expect CD names (correspond to file name)
        
        //when a migration occur and a new destination is detected, automatically provide TCP/IP link and make it available to use
        
        //Hashtable hash = (Hashtable) CDLCBuffer.GetAllHibernateCDWithTimer();
        
        ObjectOutputStream sOut;
        Socket socketSend;
        
        ServerSocket ss = null;
        
        
     //   if (ss==null || !ss.isBound()){
     //           try {
     //               ss = new ServerSocket(8887,50,InetAddress.getByName(SJServiceRegistry.getLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
     //               System.out.println("Migration ServSocket created");
     //           } catch (BindException bex) {
     //               System.out.println("Port is already bound");
     //               bex.printStackTrace();
     //           } catch (IOException ex) {
     //               ex.printStackTrace();
     //           }
     //       }
        
        
        try{
            
            //String destAddr = (String) hash.get("destinationAddress");
            //String destSS = (String) hash.get("destinationSubsystem");
            //String migType = (String) hash.get("migrationType");
            //String OS = (String) hash.get("OS");
            
            socketSend = new Socket(InetAddress.getByName(destAddr),8888);
            System.out.println("Connected to host " + socketSend.getInetAddress());
                        
            
            
            sOut = new ObjectOutputStream(socketSend.getOutputStream());
            
            Vector vecAllCD = CDLCBuffer.GetAllCDNameOfMigTypeAndDestSS(destSS, migType);
            
            
            
          //  Hashtable fileList = new Hashtable();
            
            //for (int j=0;j<vecAllCD.size();j++){
                
            //    String cdn = vecAllCD.get(j).toString();
            //    
            //    fileList.put(Integer.toString(j),cdn);
            //}
            
            //Hashtable fileList = (Hashtable) hash.get("CDName");
            
             //Enumeration keysHash = fileList.keys();
             
             //int file_amount = fileList.size();
             
            int file_amount = vecAllCD.size();
            
             // 1. start message
             sOut.writeObject("START");
             sOut.flush();
             
             sOut.writeObject(SJServiceRegistry.getLocalSSAddr());
             sOut.flush();
             
             // send the origin SS name
             String ssname = SJSSCDSignalChannelMap.getLocalSSName();
             sOut.writeObject(ssname);
             sOut.flush();
             
             // 2. send total file name to sent
             sOut.writeInt(file_amount);
             sOut.flush();
        
             System.out.println("Amount of file to sent: " +file_amount); 
             
             {
               
               for (int l=0;l<file_amount;l++){
                   
                  
                   //String keyCDName = keysHash.nextElement().toString();
            
                        JSONObject jsCurrSigChanMapping = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                        Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                        while (keysJSCurrSigChanMap.hasMoreElements()){
                       
                            String SSName = keysJSCurrSigChanMap.nextElement().toString();
                       
                            if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                           
                                JSONObject jsAllCDs = jsCurrSigChanMapping.getJSONObject(SSName);
                           
                                Enumeration keysJSAllCDs = jsAllCDs.keys();
                           
                                while (keysJSAllCDs.hasMoreElements()){
                                    String CDName = keysJSAllCDs.nextElement().toString();
                                    
                                    JSONObject IndivMap = jsAllCDs.getJSONObject(CDName);
                               
                                    if(CDName.equals(vecAllCD.get(l).toString())){
                                        String fileName =  IndivMap.getString("CDClassName")+".class";
                                        
                                        //String fileName = (String)fileList.get(key);
            
                                        String filePath = System.getProperty("user.dir");
                                        filePath = filePath.replace("\\", "/");
                                         String fullFilePath;

                                        File testFile;

                                            fullFilePath = filePath+"/"+fileName;
                                            testFile = new File(fullFilePath);


                                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(testFile));

                                             String[] str = fullFilePath.split("/");

                                             if (ss==null || !ss.isBound()){
                                                 ss = new ServerSocket(8887,50,InetAddress.getByName(SJServiceRegistry.getLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                                                 System.out.println("Migration ServSocket created");
                                             }
                                             
                                             
                                             // 3. send file name
                                             
                                            //sOut.writeObject(str[str.length-1]);
                                            sOut.writeObject(fileName);
                                            sOut.flush();
                                            
                                            Socket socketReceive = ss.accept();
                                            
                                            ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
                                            
                                            boolean IsTransfer = sInt.readBoolean();
                                            
                                            ss.close();
                                            socketReceive.close();
                                            
                                            if(IsTransfer){
                                                
                                                System.out.println("Preparing file \"" + str[str.length-1] + "\" to be sent");

                                                int file_len = (int) testFile.length();
                                                int buff_size = 50000;
                                                int bytesRead = 0;
                                                int total_read_len = 0;
                                                //byte[] buffer = new byte[buff_size];
                                                byte[] buffer = new byte[file_len];
                                                int file_len_2 = file_len;

                                                //tell server to know size of the file
                                                sOut.writeInt(file_len);
                                                sOut.flush();

                                                System.out.println("file size: " +file_len);

                                                boolean sending=true;

                                    //This one copy the file in exact size
                                    //begin read and send chunks of file in loop
                                                //while( file_len_2 > 0 ){
                                                while(sending){
                                                    //if( file_len_2 < buff_size ){
                                                    //    buffer = new byte[file_len_2];
                                                    //    bytesRead = bis.read(buffer);
                                                    //}else{
                                                        bytesRead = bis.read(buffer);
                                                    //}
                                                    bis.close();

                                                    file_len_2 -= bytesRead;
                                                    total_read_len += bytesRead;
                                                    sOut.writeBoolean(true);
                                                    sOut.flush();
                                                    sOut.writeObject(buffer);
                                                    sOut.flush();
                                                    System.out.println("Sent: " + (float)total_read_len/file_len*100 + "%");

                                                    if((float)total_read_len/file_len*100>=100){
                                                        sending=false;
                                                    }

                                                }

                                                    sOut.writeBoolean(false);
                                                    sOut.flush();

                                    //This one copy a little bit bigger
                                      //while( (bytesRead = bis.read(buffer)) != -1 ){
                                      //  total_read_len += bytesRead;
                                      //  sOut.writeBoolean(true);
                                       // sOut.writeObject(buffer);
                                      //  System.out.println("Sent: " + (float)total_read_len/file_len*100 + "%");
                                   // }
                                                    
                                    
                                  //  sOut.writeBoolean(false);
                                  //  sOut.flush();
                                    
                                                System.out.println("Done sending file!");
                                                
                                                String fileRootDir = System.getProperty("user.dir");
                                                        fileRootDir = fileRootDir.replace("\\", "/");
                                                        //String fileDir = fileRootDir+"/"+sclazz+".java";
                                                        Path path = FileSystems.getDefault().getPath(fileRootDir,IndivMap.getString("CDClassName")+".class");
                                                        
                                                        Files.deleteIfExists(path);
                                                
                                                
                                            } else {
                                                
                                                System.out.println("fileName: " +fileName+ "exist in the local directory, no need for file transfer");
                                                
                                            }
                                            
                                            
                                            
                                            
                                             ClockDomainLifeCycleSignalImpl cdlcmsigimpl = new ClockDomainLifeCycleSignalImpl();
                                            //This is for Strong migration, thus the CD descriptors of each CD need to be transferred as well (statuses are stored there)
                                            
                                            if(migType.equals("strong")){
                                                
                                                if(sc.SchedulerHasCD(CDName)){
                                                //search scheduler and obtain the CD descriptor
                                                
                                                    ClockDomain cdObj = sc.getClockDomain(CDName);
                                                    System.out.println("Sending Active cd: " +CDName);
                                                
                                                    ClockDomain cdObjMod = cdlcmsigimpl.NullifySigObjForMigration(jsAllCDs, destSS, CDName,cdObj,im);
                                                
                                                    sOut.writeObject(cdObjMod);
                                                    sOut.flush();
                                                    sc.removeClockDomain(cdObj);
                                                    im.removeCDLocation(CDName);
                                                
                                                } else {
                                                // if doesn't exist in scheduler, get from the CDObjectsBuffer
                                                    ClockDomain cdObj =  CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                                                    System.out.println("Sending hibernated cd: " +CDName);
                                                    sOut.writeObject(cdObj);
                                                    sOut.flush();
                                                    sc.removeClockDomain(cdObj);
                                                    im.removeCDLocation(CDName);
                                                }
                                                
                                            } else {
                                                
                                                if(sc.SchedulerHasCD(CDName)){
                                                    
                                                    ClockDomain cdObj = sc.getClockDomain(CDName);
                                                    sc.removeClockDomain(cdObj);
                                                    im.removeCDLocation(CDName);
                                                    
                                                } else {
                                                    
                                                     ClockDomain cdObj =  CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                                                     sc.removeClockDomain(cdObj);
                                                    im.removeCDLocation(CDName);
                                                    
                                                }
                                                
                                            }
                                            
                                            
                                            
                                            //
                                            
                                            //modify scheduler
                                           
                                            //
                                            
                                    }
                                    
                                }
                            }
                        }
                   
               }
               
               
           }
             
             //transfer CD objects from the sc directly, which have got modified
             
             
             
             //transfer signal channel mapping
             
             JSONObject jsCurrSigChanMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
             
             JSONObject transferred = new JSONObject();
             
             //Enumeration keysList = fileList.keys();
             
            // while(keysList.hasMoreElements()){
                 
                 //String SSName = keysList.nextElement().toString();
              
                 sOut.writeInt(vecAllCD.size());
                 sOut.flush();
                 
                 JSONObject jsSSCDs = new JSONObject();
                 
              //   for (int j=1;j<=fileList.size();j++){
                     
                     Enumeration keysJsCurrSigChanMap = jsCurrSigChanMap.keys();
                     
                     while (keysJsCurrSigChanMap.hasMoreElements()){
                         String SSName = keysJsCurrSigChanMap.nextElement().toString();
                         
                         JSONObject allCDsCurrMap = jsCurrSigChanMap.getJSONObject(SSName);
                     
                    // sOut.writeObject(SSName);
                     //sOut.flush();
                     
                        Enumeration keysAllCDsCurrMap = allCDsCurrMap.keys();
                     
                        JSONObject jsNewCDList = new JSONObject();
                        
                        while(keysAllCDsCurrMap.hasMoreElements()){
                         
                            String CDName = keysAllCDsCurrMap.nextElement().toString();
                            
                            for (int j=0;j<vecAllCD.size();j++){
                                
                                if(CDName.equals(vecAllCD.get(j).toString())){
                                    transferred.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                                    SJSSCDSignalChannelMap.addCDLocation(CDName, destSS);
                                    sOut.writeObject(transferred.toString());
                                    sOut.flush();
                                    
                                //need to remove mapping and service description here for each CD? Yes! - 29 Jan 2015
                           
                         //   JSONObject jsEdited = jsCurrSigChanMap;
                        //    JSONObject allCDOneSS = jsEdited.getJSONObject(SSName);
                        //    allCDOneSS.remove(CDName);
                        //    jsEdited.put(SSName, allCDOneSS);
                        //    SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsEdited);
                            
                                
                                //SJSSCDSignalChannelMap.RemoveOneCDCurrSigChannelMapping(CDName, SSName);
                                } 
                                //else {
                                
                               //     jsNewCDList.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                                
                              //  }
                                
                            }
                            
                            if(!vecAllCD.contains(CDName)){
                                jsNewCDList.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                            }
                            
                            
                            
                        }
                        
                        jsSSCDs.put(SSName, jsNewCDList);
                        
                     }
                     
                     
                // }
                 
                 // updating the mapping should also be done when housekeeping    
                     
                 //SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsSSCDs);
                     
                     CDLCBuffer.updateMigTempSignalChannelMap(jsSSCDs);
                 
             //}
             
             System.out.println("Done sending SigChan Mapping!");
             
             // transfer service description, if any
             
                JSONObject jsIntReg = SJServiceRegistry.obtainInternalRegistry();
                
                //if(!jsIntReg.isEmpty()){
                    
                    JSONObject transferredReg  = new JSONObject();
             
                    //Enumeration keysFileList = fileList.keys();

                    
                    int x = 1;
                    int j=1;

                    JSONObject jsRemainingReg = new JSONObject();

                    for(int m=0;m<vecAllCD.size();m++){

                        //String ind = keysFileList.nextElement().toString();

                        String filename = vecAllCD.get(m).toString();
                        

                        Enumeration keysjsIntReg = jsIntReg.keys();

                        while (keysjsIntReg.hasMoreElements()){
                           String servIndex = keysjsIntReg.nextElement().toString();

                           JSONObject indivServ = jsIntReg.getJSONObject(servIndex);

                           if(filename.equalsIgnoreCase(indivServ.getString("associatedCDName"))){

                               transferredReg.put(Integer.toString(x),indivServ);

                           } else {
                               jsRemainingReg.put(indivServ.getString("serviceName"), indivServ);
                           }

                       }


                    }

                    //update internal service description should be during housekeeping, so the remaining reg should be saved somewhere.

                    SJServiceRegistry.UpdateAllInternalRegistry(jsRemainingReg);

                    if(transferredReg.isEmpty()){
                        sOut.writeBoolean(false);
                        sOut.flush();
                    } else {
                        sOut.writeBoolean(true);
                        sOut.flush();
                        
                        sOut.writeObject(transferredReg.toString());
                        sOut.flush();
                    }
                    
                    //System.out.println("Sending reg:" +transferredReg.toPrettyPrintedString(2, 0));
                    System.out.println("Registry sent. Updated reg after Migration:" +jsRemainingReg.toPrettyPrintedString(2, 0));
                    
                    //sending Stop migration message to terminate transfer process
                    
                    sOut.writeObject("STOP");
                    sOut.flush();
                    
                    //CDLCBuffer.updateMigrationStatus("Completed");
                    
                //} 
               
                //Check if there is a CD missing out a pair in the local SS and that the pair is sent to the dest SS
                //if no IC physical TCP IP , then create one
                
                boolean SSICNotUsed = true;
                
                JSONObject currMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                
                Enumeration keysCurrMap = currMap.keys();
                
                while(keysCurrMap.hasMoreElements()){
                    
                    String ssName = keysCurrMap.nextElement().toString();
                    
                    if(SJSSCDSignalChannelMap.IsSSNameLocal(ssName)){
                        
                        JSONObject jsAllCDs = currMap.getJSONObject(ssName);
                        
                        Enumeration keysJSAllCDs = jsAllCDs.keys();
                        
                        while (keysJSAllCDs.hasMoreElements()){
                            String cdName = keysJSAllCDs.nextElement().toString();
                            
                            JSONObject jsSigsChans = jsAllCDs.getJSONObject(cdName);
                            //need to get channel "From" and "To" to obtain the CDPair
                            
                            Enumeration keysSigsChans = jsSigsChans.keys();
                                     
                                                while (keysSigsChans.hasMoreElements()){
                                    
                                                    String tagSigsChans = keysSigsChans.nextElement().toString();
                                    
                                                    if (tagSigsChans.equalsIgnoreCase("SChannels")){
                                        
                                                    JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);

                                                        Enumeration keysSChansInOuts = jsSigsChansInd.keys();
                                        
                                            while (keysSChansInOuts.hasMoreElements()){
                                            
                                                String keyInOut = keysSChansInOuts.nextElement().toString();
                                            
                                                if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                        
                                                    Enumeration keysSigsInputsName = jsSChansInputs.keys();
                                        
                                                    while (keysSigsInputsName.hasMoreElements()){
                                            
                                                        String SChansInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                        JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);
                                            
                                                            String pname = SChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");
                                                                
                                                            
                                                          
                                                     }
                                            
                                            } else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                        
                                                Enumeration keysChansOutputsName = jsSChansOutputs.keys();
                                        
                                                    while (keysChansOutputsName.hasMoreElements()){
                                            
                                                        String SChansOutputsName = keysChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);
                                            
                                                       
                                                        String pname = SigOutputConfigs.getString("To").trim();
                                                        String[] pnames = pname.split("\\.");
                                                        
                                          }
                                                
                                       }
                                          
                                    }
                                     
                                    } else if (tagSigsChans.equalsIgnoreCase("Achannels")){
                                        
                                        JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);
                                        
                                         Enumeration keysAChansInOuts = jsSigsChansInd.keys();
                                        
                                            while (keysAChansInOuts.hasMoreElements()){
                                            
                                                String keyInOut = keysAChansInOuts.nextElement().toString();
                                            
                                                if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsAChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                        
                                                    Enumeration keysSigsInputsName = jsAChansInputs.keys();
                                        
                                                    while (keysSigsInputsName.hasMoreElements()){
                                            
                                                        String AChansInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                        JSONObject AChansInputConfigs = jsAChansInputs.getJSONObject(AChansInputsName);
                                            
                                                        //Enumeration keysChanInputConfigs = SChansInputConfigs.keys();
                                            
                                                       // while (keysChanInputConfigs.hasMoreElements()){
                                                
                                                       //     String keyChanInputConfig = keysChanInputConfigs.nextElement().toString();
                                                 
                                                      //  }
                                                        
                                                        
                                                            String pname = AChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");
                                        // If the channel is local
                                                                 
                                                            }
                                            
                                            } else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsAChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                        
                                                Enumeration keysAChansOutputsName = jsAChansOutputs.keys();
                                        
                                                    while (keysAChansOutputsName.hasMoreElements()){
                                            
                                                        String AChansOutputsName = keysAChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsAChansOutputs.getJSONObject(AChansOutputsName);
                                            
                                                        String pname = SigOutputConfigs.getString("To").trim();
                                                        
                                                        String[] pnames = pname.split("\\.");

                                // If the channel is local
                                                        String cdPairSSLoc = SJSSCDSignalChannelMap.getCDLocation(pnames[0]);
                                                       
                                
                                                         }
                                                
                                                     }
                                          
                                                 }
                                        
                                              }
                                    
                                           }
                            
                        }
                    }
                }
                
                if(SSICNotUsed){
                    // remove IC to SS dest
                    SJSSCDSignalChannelMap.addUnusedDestSS(destSS);
                    //CDLCBuffer.setICNeedToChangeFlag(true);
                    //CDLCBuffer.setICNeedToRemoveFlag(true);
                    
                }
                
                sOut.close();
                CDLCBuffer.SetMigrationStatus(destSS, "SUCCESSFUL");
                
                //CDLCBuffer.setChangedFlag();
                //CDLCBuffer.setCDAmountChangedFlag();
                
         
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch(IOException ex2){
            ex2.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch (JSONException ex3) {
            ex3.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        
        catch (Exception ex){
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
            //CDLCBuffer.updateMigrationStatus("Failed");
        }
        
        Vector vec = new Vector();
        
        vec.addElement(im);
        vec.addElement(sc);
        
        return vec;
           
        }
    */
    /*
    public void ExecuteMigrationTransfer(String migType, String destAddr, String destSS) {
        
        //RTS expect CD names (correspond to file name)
        
        //when a migration occur and a new destination is detected, automatically provide TCP/IP link and make it available to use
        
        //Hashtable hash = (Hashtable) CDLCBuffer.GetAllHibernateCDWithTimer();
        
        ObjectOutputStream sOut;
        Socket socket;
        
        try{
            
            //String destAddr = (String) hash.get("destinationAddress");
            //String destSS = (String) hash.get("destinationSubsystem");
            //String migType = (String) hash.get("migrationType");
            //String OS = (String) hash.get("OS");
            
            socket = new Socket(InetAddress.getByName(destAddr),8888);
            System.out.println("Connected to host " + socket.getInetAddress());
                        
            sOut = new ObjectOutputStream(socket.getOutputStream());
            
            Vector vecAllCD = CDLCBuffer.GetAllCDNameOfMigTypeAndDestSS(destSS, migType);
            
          //  Hashtable fileList = new Hashtable();
            
            //for (int j=0;j<vecAllCD.size();j++){
                
            //    String cdn = vecAllCD.get(j).toString();
            //    
            //    fileList.put(Integer.toString(j),cdn);
            //}
            
            //Hashtable fileList = (Hashtable) hash.get("CDName");
            
             //Enumeration keysHash = fileList.keys();
             
             //int file_amount = fileList.size();
             
            int file_amount = vecAllCD.size();
            
             // 1. start message
             sOut.writeObject("START");
             sOut.flush();
             
             
             // 2. send total file name to sent
             sOut.writeInt(file_amount);
             sOut.flush();
        
             System.out.println("Amount of file to sent: " +file_amount); 
             
             {
               
               for (int l=0;l<file_amount;l++){
                   
                  
                   //String keyCDName = keysHash.nextElement().toString();
            
                        JSONObject jsCurrSigChanMapping = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                        Enumeration keysJSCurrSigChanMap = jsCurrSigChanMapping.keys();
                   
                        while (keysJSCurrSigChanMap.hasMoreElements()){
                       
                            String SSName = keysJSCurrSigChanMap.nextElement().toString();
                       
                            if (SJSSCDSignalChannelMap.IsSSNameLocal(SSName)){
                           
                                JSONObject jsAllCDs = jsCurrSigChanMapping.getJSONObject(SSName);
                           
                                Enumeration keysJSAllCDs = jsAllCDs.keys();
                           
                                while (keysJSAllCDs.hasMoreElements()){
                                    String CDName = keysJSAllCDs.nextElement().toString();
                                    
                                    JSONObject IndivMap = jsAllCDs.getJSONObject(CDName);
                               
                                    if(CDName.equals(vecAllCD.get(l).toString())){
                                        String fileName =  IndivMap.getString("CDClassName")+".class";
                                        
                                        //String fileName = (String)fileList.get(key);
            
                                        String filePath = System.getProperty("user.dir");
                                        filePath = filePath.replace("\\", "/");
                                         String fullFilePath;

                                        File testFile;

                                            fullFilePath = filePath+"/"+fileName;
                                            testFile = new File(fullFilePath);


                                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(testFile));

                                             String[] str = fullFilePath.split("/");

                                             
                                             // 3. send file name
                                             
                                            //sOut.writeObject(str[str.length-1]);
                                            sOut.writeObject(fileName);
                                            sOut.flush();

                                            System.out.println("Preparing file \"" + str[str.length-1] + "\" to be sent");

                                            int file_len = (int) testFile.length();
                                            int buff_size = 4096;
                                            int bytesRead = 0;
                                            int total_read_len = 0;
                                            byte[] buffer = new byte[buff_size];

                                            int file_len_2 = file_len;

                                //tell server to know size of the file
                                            sOut.writeInt(file_len);
                                            sOut.flush();

                                //This one copy the file in exact size
                                //begin read and send chunks of file in loop
                                            while( file_len_2 > 0 ){
                                                if( file_len_2 < buff_size ){
                                                    buffer = new byte[file_len_2];
                                                    bytesRead = bis.read(buffer);
                                                }else{
                                                    bytesRead = bis.read(buffer);
                                                }
                                                bis.close();

                                                file_len_2 -= bytesRead;
                                                total_read_len += bytesRead;
                                                sOut.writeBoolean(true);
                                                sOut.flush();
                                                sOut.writeObject(buffer);
                                                sOut.flush();
                                                System.out.println("Sent: " + (float)total_read_len/file_len*100 + "%");
                                            }

                                                sOut.writeBoolean(false);
                                                sOut.flush();

                                
                                            System.out.println("Done sending file!");
                                            
                                            
                                            //This is Strong migration, thus the CD descriptors of each CD need to be transferred as well (statuses are stored there)
                                            
                                            
                                            
                                            //
                                            
                                    }
                                    
                                }
                            }
                        }
                   
               }
               
               
           }
             
             //transfer CD objects from the sc directly, which have got modified
             
             
             
             //transfer signal channel mapping
             
             JSONObject jsCurrSigChanMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
             
             JSONObject transferred = new JSONObject();
             
             //Enumeration keysList = fileList.keys();
             
            // while(keysList.hasMoreElements()){
                 
                 //String SSName = keysList.nextElement().toString();
              
                 sOut.writeInt(vecAllCD.size());
                 sOut.flush();
                 
                 JSONObject jsSSCDs = new JSONObject();
                 
              //   for (int j=1;j<=fileList.size();j++){
                     
                     Enumeration keysJsCurrSigChanMap = jsCurrSigChanMap.keys();
                     
                     while (keysJsCurrSigChanMap.hasMoreElements()){
                         String SSName = keysJsCurrSigChanMap.nextElement().toString();
                         
                         JSONObject allCDsCurrMap = jsCurrSigChanMap.getJSONObject(SSName);
                     
                    // sOut.writeObject(SSName);
                     //sOut.flush();
                     
                        Enumeration keysAllCDsCurrMap = allCDsCurrMap.keys();
                     
                        JSONObject jsNewCDList = new JSONObject();
                        
                        while(keysAllCDsCurrMap.hasMoreElements()){
                         
                            String CDName = keysAllCDsCurrMap.nextElement().toString();
                            
                            for (int j=0;j<vecAllCD.size();j++){
                                
                                if(CDName.equals(vecAllCD.get(j).toString())){
                                    transferred.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                                    SJSSCDSignalChannelMap.addCDLocation(CDName, destSS);
                                    sOut.writeObject(transferred.toString());
                                    sOut.flush();
                                    
                                //need to remove mapping and service description here for each CD? Yes! - 29 Jan 2015
                           
                         //   JSONObject jsEdited = jsCurrSigChanMap;
                        //    JSONObject allCDOneSS = jsEdited.getJSONObject(SSName);
                        //    allCDOneSS.remove(CDName);
                        //    jsEdited.put(SSName, allCDOneSS);
                        //    SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsEdited);
                            
                                
                                //SJSSCDSignalChannelMap.RemoveOneCDCurrSigChannelMapping(CDName, SSName);
                                } 
                                //else {
                                
                               //     jsNewCDList.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                                
                              //  }
                                
                            }
                            
                            if(!vecAllCD.contains(CDName)){
                                jsNewCDList.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                            }
                            
                            
                            
                        }
                        
                        jsSSCDs.put(SSName, jsNewCDList);
                        
                     }
                     
                     
                // }
                 
                 // updating the mapping should also be done when housekeeping    
                     
                 //SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsSSCDs);
                     
                     CDLCBuffer.updateMigTempSignalChannelMap(jsSSCDs);
                 
             //}
             
             System.out.println("Done sending SigChan Mapping!");
             
             // transfer service description, if any
             
                JSONObject jsIntReg = SJServiceRegistry.obtainInternalRegistry();
                
                //if(!jsIntReg.isEmpty()){
                    
                    JSONObject transferredReg  = new JSONObject();
             
                    //Enumeration keysFileList = fileList.keys();

                    
                    int x = 1;
                    int j=1;

                    JSONObject jsRemainingReg = new JSONObject();

                    for(int m=0;m<vecAllCD.size();m++){

                        //String ind = keysFileList.nextElement().toString();

                        String filename = vecAllCD.get(m).toString();
                        

                        Enumeration keysjsIntReg = jsIntReg.keys();

                        while (keysjsIntReg.hasMoreElements()){
                           String servIndex = keysjsIntReg.nextElement().toString();

                           JSONObject indivServ = jsIntReg.getJSONObject(servIndex);

                           if(filename.equalsIgnoreCase(indivServ.getString("associatedCDName"))){

                               transferredReg.put(Integer.toString(x),indivServ);

                           } else {
                               jsRemainingReg.put(indivServ.getString("serviceName"), indivServ);
                           }

                       }


                    }

                    //update internal service description should be during housekeeping, so the remaining reg should be saved somewhere.

                    SJServiceRegistry.UpdateAllInternalRegistry(jsRemainingReg);

                    if(transferredReg.isEmpty()){
                        sOut.writeBoolean(false);
                        sOut.flush();
                    } else {
                        sOut.writeBoolean(true);
                        sOut.flush();
                        
                        sOut.writeObject(transferredReg.toString());
                        sOut.flush();
                    }
                    
                    //System.out.println("Sending reg:" +transferredReg.toPrettyPrintedString(2, 0));
                    System.out.println("Registry sent. Updated reg after Migration:" +jsRemainingReg.toPrettyPrintedString(2, 0));
                    
                    //sending Stop migration message to terminate transfer process
                    
                    sOut.writeObject("STOP");
                    sOut.flush();
                    
                    //CDLCBuffer.updateMigrationStatus("Completed");
                    
                //} 
               
                //Check if there is a CD missing out a pair in the local SS and that the pair is sent to the dest SS
                //if no IC physical TCP IP , then create one
                
                boolean SSICNotUsed = true;
                
                JSONObject currMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                
                Enumeration keysCurrMap = currMap.keys();
                
                while(keysCurrMap.hasMoreElements()){
                    
                    String ssName = keysCurrMap.nextElement().toString();
                    
                    if(SJSSCDSignalChannelMap.IsSSNameLocal(ssName)){
                        
                        JSONObject jsAllCDs = currMap.getJSONObject(ssName);
                        
                        Enumeration keysJSAllCDs = jsAllCDs.keys();
                        
                        while (keysJSAllCDs.hasMoreElements()){
                            String cdName = keysJSAllCDs.nextElement().toString();
                            
                            JSONObject jsSigsChans = jsAllCDs.getJSONObject(cdName);
                            //need to get channel "From" and "To" to obtain the CDPair
                            
                            Enumeration keysSigsChans = jsSigsChans.keys();
                                     
                                                while (keysSigsChans.hasMoreElements()){
                                    
                                                    String tagSigsChans = keysSigsChans.nextElement().toString();
                                    
                                                    if (tagSigsChans.equalsIgnoreCase("SChannels")){
                                        
                                                    JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);

                                                        Enumeration keysSChansInOuts = jsSigsChansInd.keys();
                                        
                                            while (keysSChansInOuts.hasMoreElements()){
                                            
                                                String keyInOut = keysSChansInOuts.nextElement().toString();
                                            
                                                if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                        
                                                    Enumeration keysSigsInputsName = jsSChansInputs.keys();
                                        
                                                    while (keysSigsInputsName.hasMoreElements()){
                                            
                                                        String SChansInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                        JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);
                                            
                                                            String pname = SChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");
                                                           
                                                     }
                                            
                                            } else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                        
                                                Enumeration keysChansOutputsName = jsSChansOutputs.keys();
                                        
                                                    while (keysChansOutputsName.hasMoreElements()){
                                            
                                                        String SChansOutputsName = keysChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);
                                            
                                                       
                                                        String pname = SigOutputConfigs.getString("To").trim();
                                                        String[] pnames = pname.split("\\.");
                                                        
                                          }
                                                
                                       }
                                          
                                    }
                                     
                                    } else if (tagSigsChans.equalsIgnoreCase("Achannels")){
                                        
                                        JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);
                                        
                                         Enumeration keysAChansInOuts = jsSigsChansInd.keys();
                                        
                                            while (keysAChansInOuts.hasMoreElements()){
                                            
                                                String keyInOut = keysAChansInOuts.nextElement().toString();
                                            
                                                if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsAChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                        
                                                    Enumeration keysSigsInputsName = jsAChansInputs.keys();
                                        
                                                    while (keysSigsInputsName.hasMoreElements()){
                                            
                                                        String AChansInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                        JSONObject AChansInputConfigs = jsAChansInputs.getJSONObject(AChansInputsName);
                                            
                                                        //Enumeration keysChanInputConfigs = SChansInputConfigs.keys();
                                            
                                                       // while (keysChanInputConfigs.hasMoreElements()){
                                                
                                                       //     String keyChanInputConfig = keysChanInputConfigs.nextElement().toString();
                                                 
                                                      //  }
                                                        
                                                        
                                                            String pname = AChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");
                                        // If the channel is local
                                                                //if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                            
                                                                
                                                            }
                                            
                                            } else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsAChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                        
                                                Enumeration keysAChansOutputsName = jsAChansOutputs.keys();
                                        
                                                    while (keysAChansOutputsName.hasMoreElements()){
                                            
                                                        String AChansOutputsName = keysAChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsAChansOutputs.getJSONObject(AChansOutputsName);
                                            
                                                        String pname = SigOutputConfigs.getString("To").trim();
                                                        
                                                        String[] pnames = pname.split("\\.");

                                // If the channel is local
                                                        String cdPairSSLoc = SJSSCDSignalChannelMap.getCDLocation(pnames[0]);
                                                       
                                
                                                         }
                                                
                                                     }
                                          
                                                 }
                                        
                                              }
                                    
                                           }
                            
                        }
                    }
                }
                
                if(SSICNotUsed){
                    // remove IC to SS dest
                    SJSSCDSignalChannelMap.addUnusedDestSS(destSS);
                    //CDLCBuffer.setICNeedToChangeFlag(true);
                    //CDLCBuffer.setICNeedToRemoveFlag(true);
                    
                }
                
                sOut.close();
                CDLCBuffer.SetMigrationStatus(destSS, "SUCCESSFUL");
                
                //CDLCBuffer.setChangedFlag();
                //CDLCBuffer.setCDAmountChangedFlag();
                
         
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch(IOException ex2){
            ex2.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch (JSONException ex3) {
            ex3.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } 
        
        catch (Exception ex){
            ex.printStackTrace();
             CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
            //CDLCBuffer.updateMigrationStatus("Failed");
        }
           
        }
    
    */
    
    /*
    private boolean CheckLocalPortAvail(int port){
        
        try{
            
            Socket socket = new Socket(getLocalHostLANAddress(), port);
            
            socket.close();
            
            ServerSocket serverSocket = new ServerSocket(port, 50, getLocalHostLANAddress());
			
            serverSocket.close();
                   
            return true;
                   
        } catch (BindException bex) {
            System.out.println("port" +port+ "already bound");
            return false;
        }catch (IOException ex) {
            return false;
        }
                       
    }
    */
  
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
    
    
    /*
    private boolean TransceiveIsMigrationDestFree (String destAddress, String reqMigMsg){
        
        
        boolean stat = false;
        //String answer = "";
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(destAddress);
                        byte[] msg = new byte[1024];
                        byte[] packet = new byte[1024];
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
                                        
                                        
                                        String answer = js.getString("data");
                                        
                                        if(answer.equalsIgnoreCase("OK")){
                                            stat = true;
                                        }
                                        
                                        
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
                        System.out.println("local Migration transfer request ack timeout");
                        //*this cant be used to declare whether service is unreachable
                        //answer="NOT OK";
			//e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        bex.printStackTrace();
                        //answer="NOT OK";
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        //answer="NOT OK";
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                return stat;    
                   
                    
      //  }
    }
    
    private boolean TransceiveIsLocalMigrationDestFree (String reqMigMsg){
        
        boolean stat = false;
        
        //String answer;
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        
                        InetAddress ipAddress = InetAddress.getByName("224.0.0.100");
                        byte[] msg = new byte[1024];
                        byte[] packet = new byte[1024];
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
                                        
                                        String answer = js.getString("data");
                                        
                                        if(answer.equalsIgnoreCase("OK")){
                                            stat = true;
                                        }
                                        
                                        
                                 }
                                 catch(Exception e)
                                 {
                                     //answer = "NOT OK";
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
                        System.out.println("local Migration transfer request ack timeout");
                        //answer="NOT OK";
			//e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        bex.printStackTrace();
                        //answer="NOT OK";
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        //answer="NOT OK";
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                   return stat;
                    
      //  }
    }
    */
        
    }
    

