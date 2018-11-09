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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.interfaces.Scheduler;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;
import systemj.common.SOAFacility.Mig.SJClassVisitor;

/**
 *
 * @author Udayanto
 */
public class CDLCMigrationTransferThread implements Runnable{

    String migType;
    String migReqMsg;
            String destAddr;
            String destSS;
            InterfaceManager im;
            //InterfaceManager imOrig;
            //Scheduler sc;
            JSONObject jsLocalCDs;
            Vector vecAllCDName;
            Vector vecAllCDIns;
            
    
            public CDLCMigrationTransferThread(JSONObject jsLocalCDs, String migReqMsg,String migType, String destAddr,String destSS,Vector vecAllCDName,Vector vecAllCDIns,InterfaceManager im){
                this.migType = migType;
                this.migReqMsg = migReqMsg;
                this.destAddr = destAddr;
                this.destSS = destSS;
                this.im = im;
                //this.imOrig = im;
                //this.sc = sc;
                this.jsLocalCDs = jsLocalCDs;
                this.vecAllCDName = vecAllCDName;
                this.vecAllCDIns = vecAllCDIns;
            }
            
           
            
    @Override
    public void run() {
        
        
        int i=0;
            
            boolean startQueryMig = true;
            
            while(i<5 && startQueryMig){
                
                if(destAddr.equalsIgnoreCase("224.0.0.100")){
                    
                    Vector vecAns = TransceiveIsLocalMigrationDestFree(migReqMsg);
                    
                    boolean Resp = Boolean.parseBoolean(vecAns.get(0).toString());
                    
                    //boolean Resp = TransceiveIsLocalMigrationDestFree(migReqMsg);
                
                     //System.out.println("Response from the other side: " +Resp);
                    
                    if(Resp){
                        
                        String destMigAddr = vecAns.get(1).toString();
                        
                        InitTransferProcess(destMigAddr);
                        
                        CDLCBuffer.RemoveReqMigrate(destSS, migType);
                       
                        for(int r=0;r<vecAllCDName.size();r++){
                            String CDName = vecAllCDName.get(r).toString();
                                    
                                    CDLCBuffer.RemoveCDMacroState(CDName);
                        }
                        
                        startQueryMig = false;
                        
                    } else {
                        i++;
                    }
                } else {
                    boolean Resp = TransceiveIsMigrationDestFree(destAddr, migReqMsg);
                
                     //System.out.println("Response from the other side: " +Resp);
                    
                    if(Resp){
                        
                        InitTransferProcess(destAddr);
                        
                        CDLCBuffer.RemoveReqMigrate(destSS, migType);
                        
                        for(int r=0;r<vecAllCDName.size();r++){
                            String CDName = vecAllCDName.get(r).toString();
                                    
                                    CDLCBuffer.RemoveCDMacroState(CDName);
                        }
                        
                        startQueryMig = false;
                        
                    } else {
                        i++;
                    }
                }
            }
            
        if(i>=5){
                        
                        
                        CDLCBuffer.RemoveReqMigrate(destSS, migType);
                        CDLCBuffer.SetMigrationStatus(destSS, "NORESPONSE");
        }
             
        
        //RTS expect CD names (correspond to file name)
        
        //when a migration occur and a new destination is detected, automatically provide TCP/IP link and make it available to use
        
        //Hashtable hash = (Hashtable) CDLCBuffer.GetAllHibernateCDWithTimer();
        
       
    }
    
    private boolean InitTransferProcess(String addr){
        
        boolean TransferStat = false;
        
         ObjectOutputStream sOut;
        Socket socketSend;
        
        ServerSocket ss = null;
        
        /*
        if (ss==null || !ss.isBound()){
                try {
                    ss = new ServerSocket(8887,50,InetAddress.getByName(SJServiceRegistry.getLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                    System.out.println("Migration ServSocket created");
                } catch (BindException bex) {
                    System.out.println("Port is already bound");
                    bex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        */
        
        try{
            
            //String destAddr = (String) hash.get("destinationAddress");
            //String destSS = (String) hash.get("destinationSubsystem");
            //String migType = (String) hash.get("migrationType");
            //String OS = (String) hash.get("OS");
            
            socketSend = new Socket(InetAddress.getByName(addr),8888);
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
             
            ArrayList<String> cdclassFileNames = new ArrayList<String>();
            
            for (int l=0;l<vecAllCDName.size();l++){
                
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
                               
                                    if(CDName.equals(vecAllCDName.get(l).toString())){
                                        
                                        String fileName =  IndivMap.getString("CDClassName");
                                        
                                        if(!cdclassFileNames.contains(fileName)){
                                            cdclassFileNames.add(fileName);
                                        }
                                        
                                    }
                                }
                            }
                        }
                
            }
            
           // int file_amount = vecAllCDName.size();
            
            int cd_file_amount = cdclassFileNames.size();
            int cdobj_amount = vecAllCDName.size();
            
             // 1. start message
             sOut.writeObject("START");
             sOut.flush();
             
             sOut.writeObject(SJSSCDSignalChannelMap.GetLocalSSAddr());
             sOut.flush();
             
             // send the origin SS name
             String ssname = SJSSCDSignalChannelMap.getLocalSSName();
             sOut.writeObject(ssname);
             sOut.flush();
             
             // 2. send total cd  to sent
             sOut.writeInt(cd_file_amount);
             sOut.flush();
        
             System.out.println("Amount of file to sent: " +cd_file_amount); 
             
             // 2. send total cd  to sent
             sOut.writeInt(cdobj_amount);
             sOut.flush();
        
             System.out.println("Amount of cd to sent: " +cdobj_amount);
             
             //{
               
             //scan for required user library classes
             Vector depLibList = new Vector();
             
             for(int k=0;k<cdclassFileNames.size();k++){
                 
                 String fileName = cdclassFileNames.get(k);
                 
                 depLibList = ScanUserLib(fileName, depLibList);
             }
             
             
             
             /*
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
                               
                                    if(CDName.equals(vecAllCDName.get(l).toString())){
                                        //String fileName =  IndivMap.getString("CDClassName")+".class";
                                        String fileName =  IndivMap.getString("CDClassName");
                                        //String fileName = (String)fileList.get(key);
            
                                        depLibList = ScanUserLib(fileName, depLibList);
                                        
                                        
                                    }
                                }
                            }
                        }
             }
             */
             
             
             
             // query the other side whether the class library exists there
             //send how many class files
             sOut.writeInt(depLibList.size());
             sOut.flush();
             
             
             
             for(int k=0;k<depLibList.size();k++){
                 
                 String filePath = System.getProperty("user.dir");
                                        filePath = filePath.replace("\\", "/");
                                         
                 
                 String indivDep = depLibList.get(k).toString();
                 
                 //send the dependency
                 
                 if (ss==null || !ss.isBound()){
                                                 ss = new ServerSocket(8887,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
                                                 System.out.println("Migration ServSocket created");
                                             }
                 
                 sOut.writeObject(indivDep);
                 sOut.flush();
                 
                 Socket socketReceive = ss.accept();
                                            
                                            ObjectInputStream sInt2 = new ObjectInputStream(socketReceive.getInputStream());
                                            
                                            boolean respToIncl = sInt2.readBoolean();
                                            
                                            ss.close();
                                            socketReceive.close();
                                            
                                            if(respToIncl){
                                                
                                                String[] splitIndivDep = indivDep.split("\\.");
                                                
                                                String clzFileName = splitIndivDep[splitIndivDep.length-1];
                                                
                                                indivDep = indivDep.replace(".", "/");
                                                
                                                String fullFilePath = filePath+"/"+indivDep;
                                                
                                                File clzFile = new File(fullFilePath+".class");
                                                
                                                //transfer file
                                                
                                                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(clzFile));
                                                
                                                System.out.println("Preparing file \"" + clzFileName + "\" to be sent");

                                                int file_len = (int) clzFile.length();
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
                                                
                                            }
                 
             }
             
             
             for(int e=0;e<cdclassFileNames.size();e++){
                 
                  //String fileName =  IndivMap.getString("CDClassName")+".class";
                    String fileName = cdclassFileNames.get(e);
                                        
                                        
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
                                                 ss = new ServerSocket(8887,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
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

                                   
                                                System.out.println("Done sending class file!");
                                                
                                                // now transfer all user library (class file)
                                                //but also need the relative path
                                                
                                                
                                                String fileRootDir = System.getProperty("user.dir");
                                                        fileRootDir = fileRootDir.replace("\\", "/");
                                                        //String fileDir = fileRootDir+"/"+sclazz+".java";
                                                        Path path = FileSystems.getDefault().getPath(fileRootDir,fileName+".class");
                                                        
                                                        Files.deleteIfExists(path);
                                                
                                                
                                            }
                 
             }
             
            
             
             // end
             
             
               for (int l=0;l<cdobj_amount;l++){
                   
                  
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
                                    
                                   // JSONObject IndivMap = jsAllCDs.getJSONObject(CDName);
                               
                                    if(CDName.equals(vecAllCDName.get(l).toString())){
                                        
                                         
                                            
                                             ClockDomainLifeCycleSignalImpl cdlcmsigimpl = new ClockDomainLifeCycleSignalImpl();
                                            //This is for Strong migration, thus the CD descriptors of each CD need to be transferred as well (statuses are stored there)
                                            for(int t=0;t<vecAllCDIns.size();t++){
                                                
                                                ClockDomain migCDInst = (ClockDomain)vecAllCDIns.get(t);
                                                
                                                 if(migType.equals("strong")){
                                                
                                                    if (migCDInst.getState().equalsIgnoreCase("Active")){
                                                        
                                                         System.out.println("Sending Active cd: " +CDName);
                                                
                                                        ClockDomain cdObjMod = cdlcmsigimpl.NullifySigObjForMigration(jsAllCDs, destSS, CDName,migCDInst,im);

                                                        sOut.writeObject(cdObjMod);
                                                        sOut.flush();
                                                        //sc.removeClockDomain(cdObj);
                                                        //im.removeCDLocation(CDName);
                                                        
                                                    } else if (migCDInst.getState().equalsIgnoreCase("Sleep")){
                                                        ClockDomain cdObj =  CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                                                        System.out.println("Sending hibernated cd: " +CDName);
                                                        sOut.writeObject(cdObj);
                                                        sOut.flush();
                                                        //sc.removeClockDomain(cdObj);
                                                        //im.removeCDLocation(CDName);
                                                    }
                                                    
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
               
             
             
                                            
                                            
                                            
                                            //
                                            
                                            //modify scheduler
                                           
                                            //
                                           
                                            
                                            
                                            
                                    
               
               
           //}
             
             //transfer CD objects from the sc directly, which have got modified
             
             
             
             //transfer signal channel mapping
             
             JSONObject jsCurrSigChanMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
             
             JSONObject transferredMap = new JSONObject();
             
             //Enumeration keysList = fileList.keys();
             
            // while(keysList.hasMoreElements()){
                 
                 //String SSName = keysList.nextElement().toString();
              
                 sOut.writeInt(vecAllCDName.size());
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
                            
                            for (int j=0;j<vecAllCDName.size();j++){
                                
                                if(CDName.equals(vecAllCDName.get(j).toString())){
                                    transferredMap.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                                    //im.addCDLocation(CDName, destSS);
                                    sOut.writeObject(transferredMap.toString());
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
                            
                            if(!vecAllCDName.contains(CDName)){
                                jsNewCDList.put(CDName, allCDsCurrMap.getJSONObject(CDName));
                            }
                            
                            
                            
                        }
                        
                        for (int j=0;j<vecAllCDName.size();j++){
                                
                               
                            String CDName = vecAllCDName.get(j).toString();
                            
                                    JSONObject CDMap =  allCDsCurrMap.getJSONObject(CDName);
                                    
                                    JSONObject SChanMap = CDMap.getJSONObject("SChannels");
                                    
                                    JSONObject SChanIn = SChanMap.getJSONObject("inputs");
                                    
                                    if(!SChanIn.isEmpty()){
                                        
                                        Enumeration keysSChanIn = SChanIn.keys();
                                        
                                        while(keysSChanIn.hasMoreElements()){
                                            
                                            String InChanName = keysSChanIn.nextElement().toString();
                                            
                                            JSONObject InChanDet = SChanIn.getJSONObject(InChanName);
                                            
                                            String CDNameOriginFrom = InChanDet.getString("From");
                                            
                                            String[] pnames = CDNameOriginFrom.split("\\.");
                                            
                                            String SSLoc;
                                            
                                            if(im.hasCDLocation(pnames[0])){
                                                SSLoc = im.getCDLocation(pnames[0]);
                                            } else {
                                                SSLoc = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                            }
                                            
                                            if(!SSLoc.equals(destSS) && !SSLoc.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                                //Need to notify the other side that the location of this CD is changed!
                                                
                                                for(int h=0;h<vecAllCDIns.size();h++){
                                                    
                                                    ClockDomain CDIns = (ClockDomain)vecAllCDIns.get(h);
                                                    
                                                    if(CDIns.getName().equals(CDName)){
                                                        
                                                        String cname = InChanName+"_in";
                                                        
                                                        Field f = CDIns.getClass().getField(cname);
                                                       
                                                        input_Channel inchan = (input_Channel)f.get(CDIns);
                                                        
                                                        inchan.TransmitCDLocChanges(destSS);
                                                        
                                                    }
                                                    
                                                }
                                                
                                            }
                                            
                                        }
                                        
                                    }
                                       
                                    /*
                                    JSONObject SChanOut = SChanMap.getJSONObject("outputs");
                                    
                                    if(!SChanOut.isEmpty()){
                                        
                                        Enumeration keysSChanOut = SChanOut.keys();
                                        
                                        while(keysSChanOut.hasMoreElements()){
                                            
                                            String OutChanName = keysSChanOut.nextElement().toString();
                                            
                                            JSONObject OutChanDet = SChanOut.getJSONObject(OutChanName);
                                            
                                            String CDNameDestTo = OutChanDet.getString("To");
                                            
                                            String[] pnames = CDNameDestTo.split("\\.");
                                            
                                            String SSLoc;
                                            
                                            if(im.hasCDLocation(pnames[0])){
                                                SSLoc = im.getCDLocation(pnames[0]);
                                            } else {
                                                SSLoc = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                            }
                                            
                                            //String SSLoc = im.getCDLocation(pnames[0]);
                                            
                                            if(!SSLoc.equals(destSS) && !SSLoc.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                                //Need to notify the other side that the location of this CD is changed!
                                                
                                                for(int h=0;h<vecAllCDIns.size();h++){
                                                    
                                                    ClockDomain CDIns = (ClockDomain)vecAllCDIns.get(h);
                                                    
                                                    if(CDIns.getName().equals(CDName)){
                                                        
                                                        String cname = OutChanName+"_out";
                                                        
                                                        Field f = CDIns.getClass().getField(cname);
                                                       
                                                        output_Channel ochan = (output_Channel)f.get(CDIns);
                                                        
                                                        ochan.TransmitCDLocChanges(destSS);
                                                        
                                                    }
                                                    
                                                }
                                                
                                            }
                                            
                                        }
                                        
                                    }
                                    */
                                
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

                    for(int m=0;m<vecAllCDName.size();m++){

                        //String ind = keysFileList.nextElement().toString();

                        String filename = vecAllCDName.get(m).toString();
                        

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
                    
                    
                    
                    JSONObject jsInt = SJServiceRegistry.obtainInternalRegistry();
                                            
                                            Enumeration keysJsInt = jsInt.keys();
                                            
                                            while(keysJsInt.hasMoreElements()){
                                                
                                                String servName = keysJsInt.nextElement().toString();
                                                
                                                JSONObject jsIndivServ = jsInt.getJSONObject(servName);
                                                
                                                String assocCDName = jsIndivServ.getString("associatedCDName");
                                                
                                                for(int r=0;r<vecAllCDName.size();r++){
                                                    
                                                    String CDName = vecAllCDName.get(r).toString();
                                                    
                                                    if(assocCDName.equals(CDName)){
                                                    
                                                       // String servRole = jsIndivServ.getString("serviceRole");

                                                        //if(servRole.equalsIgnoreCase("provider")){

                                                            SOABuffer.removeAdvStatOfServName(servName);

                                                        //}

                                                    }
                                                    
                                                }
                                                
                                            }
                    
                    //CDLCBuffer.updateMigrationStatus("Completed");
                    
                //} 
               
                //IMBuffer.SaveInterfaceManagerConfig(im);
                                            
                CDLCBuffer.SetMigrationStatus(destSS, "SUCCESSFUL");
                
                CDLCBuffer.SetAllMigCDObjsBuffer(destSS, migType, vecAllCDIns,transferredMap);
                
                CDLCBuffer.SetMigGoAheadModPartner(true);
                
                while(!CDLCBuffer.GetMigModPartnerDone()){
                    
                }
                
                TransferStat = true;
                                            
                //Check if there is a CD missing out a pair in the local SS and that the pair is sent to the dest SS
                //if no IC physical TCP IP , then create one
                sOut.writeObject("STOP");
                sOut.flush();
                
                sOut.close();
                
                
                
                //System.out.println("CDLCMigrationTransferThread, all destSS & migType : " +CDLCBuffer.GetRequestMigrate());
                
                //Vector vecAllCDName = CDLCBuffer.GetAllCDNameOfMigTypeAndDestSS(destSS, migType);
                 
                //CDLCBuffer.RemoveReqMigrate(destSS, migType);
                
                        
                //CDLCBuffer.setChangedFlag();
                //CDLCBuffer.setCDAmountChangedFlag();
                
         
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            
            CDLCBuffer.RecoverReqMigrateAllCD(destSS, migType, vecAllCDName);
            
            //CDLCBuffer.SetMigrationIMBuffer(imOrig);
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch(IOException ex2){
            ex2.printStackTrace();
            
            CDLCBuffer.RecoverReqMigrateAllCD(destSS, migType, vecAllCDName);
            
            //CDLCBuffer.SetMigrationIMBuffer(imOrig);
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
        } catch (JSONException ex3) {
            ex3.printStackTrace();
             
            CDLCBuffer.RecoverReqMigrateAllCD(destSS, migType, vecAllCDName);
            
            //CDLCBuffer.SetMigrationIMBuffer(imOrig);
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
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
            
            CDLCBuffer.RecoverReqMigrateAllCD(destSS, migType, vecAllCDName);
            CDLCBuffer.SetMigrationStatus(destSS, "FAIL");
            //CDLCBuffer.updateMigrationStatus("Failed");
        }
        
        //Vector vec = new Vector();
        
        //vec.addElement(im);
        //vec.addElement(sc);
        
        //return vec;
        
        CDLCBuffer.SetMigModPartnerDone(false);
        CDLCBuffer.SetCDLCMigrationFlagFree();
        
        return TransferStat;
        
    }
    
     private Vector TransceiveIsLocalMigrationDestFree (String reqMigMsg){
        
        Vector vecAns = new Vector();
        boolean stat = false;
        String addr = "";
        //String answer;
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
                        s2.joinGroup(ipAddress);
                        
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
                        
                        s2.setSoTimeout(5000);
                        
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
                                        addr = js.getString("sourceAddress");
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
                        
                    } catch (java.net.BindException bex){
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
                    
                   vecAns.addElement(Boolean.toString(stat));
                   vecAns.addElement(addr);
                   
                   return vecAns;
                    
      //  }
    }
    
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
     
    private Vector ScanUserLib(String CDClassFileName, Vector existingDepList){
        
        
        
        String filePath = System.getProperty("user.dir");
        filePath = filePath.replace("\\", "/");
            try{
                
                File classesDirSS = new File(filePath);
            
            // TODO code application logic here
            SJClassVisitor scv = null;
            InputStream is = ClassLoader.getSystemResourceAsStream(CDClassFileName);
            scv = new SJClassVisitor(Opcodes.ASM5, classesDirSS, CDClassFileName, existingDepList);
            //scv.addDependency("test.class");
            ClassReader cr = new ClassReader(is);
            cr.accept(scv, ClassReader.SKIP_DEBUG);
            is.close();
            
            //Set<String> depList = scv.getDependencies();
             existingDepList = scv.getDependencies();
                
            } catch (Exception ex){
                ex.printStackTrace();
            }
            
           return existingDepList;
            
        
    }
     
}
