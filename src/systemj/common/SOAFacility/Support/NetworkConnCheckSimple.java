/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import systemj.common.SJServiceRegistry;

/**
 *
 * @author Udayanto
 */
public class NetworkConnCheckSimple {
    
    public String CheckNetworkConn(String IPAddr, int timeout){
        
        String ConnectionStat=null;
        
        int debug=0;

             try {
 
                    String ipAddress = IPAddr;
                    InetAddress inet = InetAddress.getByName(ipAddress);

                    //System.out.println("Sending Ping Request to " + ipAddress);
                    
                    if (inet.isReachable(timeout) ){
                       //list[0] = Boolean.TRUE;
                       
                         ConnectionStat="Connected";
                         if (debug==1) System.out.println("Just Connected");
                        
                   } else if (!inet.isReachable(timeout)){
                        //list[0] = Boolean.TRUE;
                        ConnectionStat="NotConnected";
                        if (debug==1) System.out.println("Not Connected");
                        //SJServiceRegistry.AcknowledgeHelloMessageSent(false);
                        //SOABuffer.setIsInitAdvDone(false);
                        //SOABuffer.setIsInitDiscDone(false);
                        //SJServiceRegistry.setJustJoinedStatus(false);
                   } else {
                        //list[0] =Boolean.FALSE;
                        ConnectionStat="NotConnected";
                        if (debug==1) System.out.println("Unknown Connection");
                        //SJServiceRegistry.setJustJoinedStatus(false);
                        //SOABuffer.setIsInitAdvDone(false);
                        //SOABuffer.setIsInitDiscDone(false);
                   }
                    
                   /*
                    
                   if (inet.isReachable(timeout) && !SOAConnAdvDiscStatus.getSOAInitStatResolver() ){
                       //list[0] = Boolean.TRUE;
                       
                         ConnectionStat="JustJoined";
                         if (debug==1) System.out.println("Just Connected");
                        
                   } else if (inet.isReachable(timeout) && SOAConnAdvDiscStatus.getSOAInitStatResolver()) {
                       //list[0] = Boolean.TRUE;
                         ConnectionStat="Connected";
                         if (debug==1) System.out.println("Still Connected");
                   } else if (!inet.isReachable(timeout)){
                        //list[0] = Boolean.TRUE;
                        ConnectionStat="Open";
                        if (debug==1) System.out.println("Not Connected");
                        //SJServiceRegistry.AcknowledgeHelloMessageSent(false);
                        SJServiceRegistry.setJustJoinedStatus(false);
                   } else {
                        //list[0] =Boolean.FALSE;
                        ConnectionStat="NotConnected";
                        if (debug==1) System.out.println("Unknown Connection");
                        SJServiceRegistry.setJustJoinedStatus(false);
                   }
                    */
                    
                    //System.out.println(inet.isReachable(300) ? "Host is reachable" : "Host is NOT reachable");
                   
                //} 
             //catch (SocketException sockx) {
                   //System.out.println("address cannot be detected: " +sockx.getMessage());
                  // list[0] = Boolean.FALSE;
                  // list[1]="SocketException";
               } 
                catch (UnknownHostException hex){
                   System.out.println("unknown host: " +hex.getMessage());
                   //list[0] = Boolean.FALSE;
                   ConnectionStat="Notconnected";
                   //SOABuffer.setIsInitAdvDone(false);
                        //SOABuffer.setIsInitDiscDone(false);
                   //SJServiceRegistry.setJustJoinedStatus(false);
               } catch (IOException iex){
                  // System.out.println("other problem exists: " +iex.getMessage());
                  // list[0] = Boolean.FALSE;
                  // list[1]="Open";
                   ConnectionStat="Notconnected";
                   //SOABuffer.setIsInitAdvDone(false);
                        //SOABuffer.setIsInitDiscDone(false);
                   //SJServiceRegistry.AcknowledgeHelloMessageSent(false);
                   //SJServiceRegistry.setJustJoinedStatus(false);
               } catch (Exception ex){
                   ConnectionStat="Notconnected";
                   //SOABuffer.setIsInitAdvDone(false);
                        //SOABuffer.setIsInitDiscDone(false);
                  // SJServiceRegistry.setJustJoinedStatus(false);
               }

              // super.setBuffer(list);
             return ConnectionStat;
            
    }
    
}
