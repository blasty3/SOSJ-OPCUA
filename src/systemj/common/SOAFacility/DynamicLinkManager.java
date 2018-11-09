/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.util.Enumeration;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.interfaces.GenericInterface;

/**
 *
 * @author Udayanto
 */
public class DynamicLinkManager {
    
    
    
    public InterfaceManager ExecuteDynamicLinkManager(InterfaceManager IM){
                //if(SJSSCDSignalChannelMap.GetReqCreateLink()){
                          
                          //if request to create link is present
                                Vector SSsToContact =  TCPIPLinkRegistry.GetAllSSToContact();
                                
                                boolean contactSSAgain = false;

                                // find local SS port
                                
                                JSONObject jsLocalPortPair = TCPIPLinkRegistry.GetSSAndPortPair();
                                
                                String LocalPortNum="";
                                boolean LocalPortExist = false;
                                
                                try{
                                    
                                    Enumeration keysLoc = jsLocalPortPair.keys();
                                
                                    while(keysLoc.hasMoreElements()){
                                        String PortNumStr = keysLoc.nextElement().toString();

                                        if(jsLocalPortPair.getString(PortNumStr).equals(SJSSCDSignalChannelMap.getLocalSSName())){

                                            LocalPortExist=true;
                                            LocalPortNum = PortNumStr;
                                            
                                        }

                                    }
                                    
                                } catch (JSONException jex){
                                    jex.printStackTrace();
                                }
                                
                                //end find local SS port
                                
                                for(int i=0;i<SSsToContact.size();i++){
                                    
                                    String SSName = (String)SSsToContact.get(i);

                                                //InterfaceManager imA = getInterfaceManager();
                                    System.out.println("SJProgram, Trying to contact SS with name: " +SSName);
                                        
                                            Interconnection ic = IM.getInterconnection();
                          
                                            Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                            if(availRemoteLink.size()==0){

                                                String destAddr = RegAllSSAddr.getSSAddrOfSSName(SSName);
                          
                                                LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();
                                                
                                                int r;
                                                
                                                for(r=0;r<5;r++){
                                                    
                                                    String resp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destAddr);
                                                    
                                                        if(resp.equals("OK")){
                                                        
                                                            if(LocalPortExist){
                                                                
                                                                IM = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM, LocalPortNum);
                                                                SSsToContact.remove(i);
                                                                break;
                                                                
                                                            } else {
                                                                 IM = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM);
                                                                 SSsToContact.remove(i);
                                                                 break;
                                                            }
                                                            
                                                        } 
                                                        
                                                }
                                                
                                                if(r>=5){
                                                    contactSSAgain=true;
                                                }
                                                
                                                
                                                // update SS to Contact
                                                
                                                TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                
                                            }

                                    //request first

                                    
                                    
                                    //if local, then set to 127.0.0.1
                                    

                                    //LinkCreationSenderHSImpl lcshs = new LinkCreationSenderHSImpl();

                                    //InterfaceManager imA = lcshs.ExecuteLinkCreationHS(SSName, destAddr, IM);
                                    //setInterfaceManager(imA);
                                    // need to
                                     IM.setInterconnection(ic);
                                }

                                
                              //  if(!contactSSAgain){ //next end of exec cycle
                               //     SJSSCDSignalChannelMap.ResetReqCreateLink();
                               // }
              
                            // }
                          
                          //Unused Link Deletion
                          
                          Interconnection ic = IM.getInterconnection();
                          
                          JSONObject jsPortPair = TCPIPLinkRegistry.GetSSAndPortPair();
                          
                          try{
                              
                                  Enumeration keysPP = jsPortPair.keys();
                                  
                                  while(keysPP.hasMoreElements()){
                                      String portNum = keysPP.nextElement().toString();
                                      
                                      String SS = jsPortPair.getString(portNum);
                                      
                                      if(!SS.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                          
                                          if(!SJSSCDSignalChannelMap.IsAnyChanUseLinkToSS(SS)){
                                              
                                              Vector ListICSS = ic.getRemoteDestinationInterfaces(SS);
                                              
                                              for (int e=0;e<ListICSS.size();e++){
                                                  GenericInterface gct = (GenericInterface) ListICSS.get(e);
                                                  gct.TerminateInterface();
                                                  
                                              }
                                              
                                              ic.removeInterfaces(SS);
                                              
                                          }
                                          
                                      }
                                      
                                  }
                              
                          } catch (JSONException jex){
                              jex.printStackTrace();
                          }
                          
                          IM.setInterconnection(ic);
                          
                          return IM;
    }
    
}
