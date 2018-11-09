/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import systemj.common.SJServiceRegistry;

/**
 *
 * @author Udayanto
 */
public class NetworkConnCheck {
    
    
    public String CheckNetworkConn(String address){
        
        int debug=0;
        
        String result=null;
        
        Vector j = new Vector();
             
             Object[] list = new Object[2];
             
             if (debug==1) System.out.println("iterate IP");

             try {
                    for( Enumeration e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); )
                               {
                                       NetworkInterface intf = ( NetworkInterface ) e.nextElement();
                                       
                                       for( Enumeration adrs = intf.getInetAddresses(); adrs.hasMoreElements(); )
                                       {
                                               
                                               InetAddress adr = ( InetAddress ) adrs.nextElement();
                                               
                                               if (adr.toString().contains(address)){
                                                  if (debug==1) System.out.println("Match requested address");
                                                  j.addElement("true");
                                               } else {
                                                  if (debug==1)System.out.println("No match requested address");
                                                  j.addElement("false");
                                               }
                                               
                                               /*
                                               if( adr instanceof Inet4Address && !adr.isLoopbackAddress() )
                                               {
                                                      System.out.println(adr +"\n");
                                                      System.out.println("Status acknowledge in detector:" +SJServiceRegistry.getAcknowledgeHelloMessageSent());
                                                      if(adr.toString().contains(address) && SJServiceRegistry.getAcknowledgeHelloMessageSent()==false){
                                                          //joined to a network
                                                          
                                                          //list[0] = Boolean.TRUE;
                                                          list[1]="Joined";
                                                          
                                                      } else if (!adr.toString().contains(address) && SJServiceRegistry.getAcknowledgeHelloMessageSent()==false){
                                                          //list[0] = Boolean.TRUE;
                                                          list[1]="NotConnected";
                                                          //SJServiceRegistry.AcknowledgeHelloMessageSent(false);
                                                          //left the network
                                                      } else {
                                                          System.out.println("Go into this False state");
                                                          list[1]="False";
                                                      }
                                               }
                                               * */
                                       }
                               }
                   
                    
                    if (j.contains("true")){
                         //list[0] = Boolean.TRUE;
                         //list[1]="Joined";
                        
                         result="JustJoined";
                        
                         if (debug==1) System.out.println("Just Connected");
                    } else if (j.contains("true") ){
                         //list[0] = Boolean.TRUE;
                         //list[1]="Connected";
                         result = "Connected";
                        
                         if (debug==1) System.out.println("Still Connected");
                    } else if (!j.contains("true")) {
                        //list[0] = Boolean.FALSE;
                        //list[1]="Open";
                        
                        result="NotConnected";
                        
                        //SJServiceRegistry.AcknowledgeHelloMessageSent(false);
                        if (debug==1)System.out.println("Disconnected");
                    } else {
                        //list[0] =Boolean.FALSE;
                        //list[1]="Unidentified";
                        
                        result="NotConnected";
                        if (debug==1) System.out.println("Unidentified Disconnected");
                    }
                   
                } catch (SocketException sockx) {
                   System.out.println("address cannot be detected: " +sockx.getMessage());
                   //list[0] = Boolean.FALSE;
                   //list[1]="SocketException";
                   result="NotConnected";
               } catch (Exception ex){
                   System.out.println("other problem exists: " +ex.getMessage());
                   //list[0] = Boolean.FALSE;
                   //list[1]="OtherException";
                   result = "NotConnected";
               }

               
             return result;
        
    }
    
}
