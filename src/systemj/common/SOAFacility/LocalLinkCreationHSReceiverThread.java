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
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.interfaces.GenericInterface;

/**
 *
 * @author Udayanto
 */
public class LocalLinkCreationHSReceiverThread implements Runnable{

    //ServerSocket ss;
    //Socket socket;
    
    String sourceSS=null;
    
    
    @Override
    public void run(){
        
        
        //NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        //String currentOS = System.getProperty("os.name");
        
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
                    ss = new ServerSocket(8890,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));//new ServerSocket(8888, 50, getLocalHostLANAddress());
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
                        
                    
                    Socket socketReceive = ss.accept();
                   
                    ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
                   
                   //1. INIT Migration--> obtain migration
                   
                    String migrMsg = sInt.readObject().toString();
                   
                    String destAddr = socketReceive.getInetAddress().getHostAddress();
                   
                   if (migrMsg.equalsIgnoreCase("START")){
                      
                       //receive origin SS first
                       Socket socketSend = new Socket(InetAddress.getByName(destAddr),8895);
                       ObjectOutputStream sOut = new ObjectOutputStream(socketSend.getOutputStream());
                       
                       //send the SSname to partner
                       
                       sOut.writeUTF(SJSSCDSignalChannelMap.getLocalSSName());
                       sOut.flush();
                       
                       //send the port allocation
                       
                       String StringSSPortPair = TCPIPLinkRegistry.GetSSAndPortPair().toString();
                       
                       sOut.writeObject(StringSSPortPair);
                       sOut.flush();
                       
                       //after sending this, wait for port allocation decision by the opposite party
                       
                       String SSPartnerName = sInt.readUTF();
                       
                       String PairPortAlloc = sInt.readUTF();
                       
                       //convert back to JSONObject
                       
                       JSONObject jsPairPortAlloc = new JSONObject(new JSONTokener(PairPortAlloc));
                       
                       System.out.println("LocalLinkCreation PairPortAlloc: " +PairPortAlloc);
                       
                       Enumeration keysPair = jsPairPortAlloc.keys();
                       
                       InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
                       
                       Interconnection ic = im.getInterconnection();
                       
                       Interconnection.Link linko = new Interconnection.Link();
                       
                       //Vector GCTVec = new Vector();
                       
                       try{
                           
                           while(keysPair.hasMoreElements()){
                                String portNum = keysPair.nextElement().toString();

                                String SS = jsPairPortAlloc.getString(portNum);

                                if(SS.equals(SSPartnerName)){
                                    
                                    String args = "127.0.0.1:"+portNum;
                                

                                    GenericInterface gct = (GenericInterface)Class.forName("systemj.desktop.TCPIPInterface").newInstance();
                                    Hashtable ht = new Hashtable();
                                    ht.put("Class", "systemj.desktop.TCPIPInterface");

                                    ht.put("Args", args);
                                    ht.put("SubSystem", SS);
                                    gct.configure(ht);
                                    linko.addInterface(SS, gct);

                                    //ic.addLink(linko, false);

                                    TCPIPLinkRegistry.AddSSAndPortPair(portNum, SS);
                                    
                                    System.out.println("Local Link interface addr: " +args+" is created ! For SS: " +SS);

                                    
                                }
                                
                                //String args = destAddr+":"+portNum;
                               
                                //GCTVec.addElement(gct);

                            }
                           
                            if(!linko.IsInterfaceEmpty()){
                                ic.addLink(linko, false);
                            }

                            im.setInterconnection(ic);

                            //start TCP-IP thread

                            boolean mustCreate = false;
                            
                            JSONObject jsSSPortAlloc = TCPIPLinkRegistry.GetSSAndPortPair();
                            
                            Enumeration keysjsSSPortAlloc = jsSSPortAlloc.keys();
                            
                            while(keysjsSSPortAlloc.hasMoreElements()){
                             
                                String portNum = (keysjsSSPortAlloc.nextElement().toString());
                                
                                String SSName = jsPairPortAlloc.getString(portNum);
                                
                                if(SSName.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                    mustCreate = true;
                                }
                                
                            }
                            
                            if(mustCreate){
                                
                                Vector localGCTs = ic.getRemoteDestinationInterfaces(SJSSCDSignalChannelMap.getLocalSSName());
                       
                                System.out.println("LocalLinkCreationHS, Size local GCTs: " +localGCTs.size());

                                for(int k=0;k<localGCTs.size();k++){

                                         GenericInterface gct = (GenericInterface)localGCTs.get(k);



                                         gct.invokeReceivingThread();
                                         gct.setInterfaceManager(im);

                                }
                                
                            }
                            
                            
                            
                            /*
                            for(int k=0;k<GCTVec.size();k++){

                                GenericInterface gct = (GenericInterface)GCTVec.get(k);

                                gct.invokeReceivingThread();
                                gct.setInterfaceManager(im);

                            }
                            */
                           
                       } catch (Exception ex){
                           ex.printStackTrace();
                       }
                       
                       IMBuffer.SaveInterfaceManagerConfig(im);
                       
                       
                    
                   //CDLCBuffer.setCDAmountChangedFlag();
                   
                   sInt.close();
                   sOut.close();
                   
                   socketReceive.close();
                   ss.close();
                   socketSend.close();
                   
                   CDLCBuffer.SetLinkCreationBusyFlag(true);
                      
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
             CDLCBuffer.SetLinkCreationBusyFlag(false);
             
             
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
