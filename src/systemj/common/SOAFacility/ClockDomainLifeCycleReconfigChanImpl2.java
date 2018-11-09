/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.interfaces.Scheduler;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;

/**
 *
 * @author Atmojo
 */
public class ClockDomainLifeCycleReconfigChanImpl2 {
    
    public Vector ReconfigChannel(JSONObject jsLocalCDs, String keyCurrSS, String keyCDName, String ChanName, String ChanDir, String NewPartnerChanName, String NewPartnerChanCDName,InterfaceManager im, Scheduler sc)        
    {
        
        ClockDomain cdins = null;
        
        if(sc.SchedulerHasCD(keyCDName)){
            cdins = sc.getClockDomain(keyCDName);
        } else if (CDObjectsBuffer.CDObjBufferHas(keyCDName)){
            cdins = CDObjectsBuffer.GetCDInstancesFromBuffer(keyCDName);
        }
        
        Vector vec = new Vector();
        try{
            
                    Hashtable channels = im.getAllChannelInstances();
            
                                                    
                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("SChannels");
                                      
                                                            //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                                            if(ChanDir.equalsIgnoreCase("input")){
                                                                
                                                                JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                                                
                                                        
                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(ChanName);

                                                            String cname = ChanName.trim()+"_in";
                                                            
                                                            
                                                            String newPartnerpname = NewPartnerChanCDName+"."+NewPartnerChanName+"_o";
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] newPartnerpnames = newPartnerpname.split("\\.");
                                                                
                                                                input_Channel inchan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                
                                                            
                                                            if(SChansInputConfigs.getString("From").equalsIgnoreCase(".")){
                                                                
                                                                 //channel doesnt have any partner, reconfig goes to check the new partner's rendezvous straightaway
                                                               
                                                                    if(keyCurrSS.equals(im.getCDLocation(newPartnerpnames[0]))){
                                                                                        
                                                                                        ClockDomain newPartnercd=null;
                                                                                        
                                                                                        //if new partner is active
                                                                                        
                                                                                        if (sc.SchedulerHasCD(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = sc.getClockDomain(newPartnerpnames[0]);
                                                                                            
                                                                                        } else if(CDObjectsBuffer.CDObjBufferHas(newPartnerpnames[0])){
                                                                                            
                                                                                             newPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(newPartnerpnames[0]);
                                                                                            
                                                                                        }
                                                                                        
                                                                                        output_Channel newPartnerOchan;
                                                                                            
                                                                                            Field fNewPartnerOchan = newPartnercd.getClass().getField(NewPartnerChanName);
                                                                                            newPartnerOchan = (output_Channel)fNewPartnerOchan.get(newPartnercd);
                                                                                            
                                                                                            JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                                            JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                                            JSONObject jsNewPartnerOChans = jsNewPartnerChans.getJSONObject("outputs");
                                                                                            JSONObject jsNewPartnerOChan = jsNewPartnerOChans.getJSONObject(NewPartnerChanName);
                                                                                            String OldPartnerOfNewPartnerOChanTo = jsNewPartnerOChan.getString("To");
                                                                                            
                                                                                            //if new partner doesnt have a partner
                                                                                            if(OldPartnerOfNewPartnerOChanTo.equalsIgnoreCase(".")){
                                                                                                
                                                                                                //if no partner, then no need to check for rendezvous, reconfigure immediately
                                                                                               
                                                                                                   
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                
                                                                                                       if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                                       
                                                                                                       if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                        inchan.PartnerName = newPartnerpname;
                                                                                                        newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                                        //oldOchan.PartnerName=".";
                                                                                                        //newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        inchan.set_partner_smp(newPartnerOchan);
                                                                                                        newPartnerOchan.set_partner_smp(inchan);
                                                                                                        //oldOchan.set_partner_smp(new input_Channel());
                                                                                                        //newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(inchan.Name,inchan);
                                                                                                        channels.put(newPartnerOchan.Name,newPartnerOchan);
                                                                                                        //channels.put(oldOchan.Name,oldOchan);
                                                                                                        //channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        
                                                                                                        
                                                                                                        SChansInputConfigs.put("From",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerOChan.put("To", keyCDName+"."+ChanName);
                                                                                                        //jsOldPartnerOChan.put("To", ".");
                                                                                                        //jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                             jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                        
                                                                                            } else {
                                                                                                
                                                                                                String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                                                String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                ClockDomain NewPartnerOldPartnercd = null;
                                                                                                
                                                                                                if(sc.SchedulerHasCD(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = sc.getClockDomain(OldPartnerOfNewPartnerCDName);
                                                                                                    
                                                                                                } else if (CDObjectsBuffer.CDObjBufferHas(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(OldPartnerOfNewPartnerCDName);
                                                                                                }    
                                                                                                    
                                                                                                input_Channel newPartnerOldPartnerInchan;
                                                                                            
                                                                                                    Field fNewPartnerOldPartnerInchan = NewPartnerOldPartnercd.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                                    newPartnerOldPartnerInchan = (input_Channel)fNewPartnerOldPartnerInchan.get(NewPartnerOldPartnercd);
                                                                                                    
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                        CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                        
                                                                                                        if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        
                                                                                                        
                                                                                                        inchan.PartnerName = newPartnerpname;
                                                                                                        newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                                        //oldOchan.PartnerName=".";
                                                                                                        newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        inchan.set_partner_smp(newPartnerOchan);
                                                                                                        newPartnerOchan.set_partner_smp(inchan);
                                                                                                        //oldOchan.set_partner_smp(new input_Channel());
                                                                                                        newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(inchan.Name,inchan);
                                                                                                        channels.put(newPartnerOchan.Name,newPartnerOchan);
                                                                                                        //channels.put(oldOchan.Name,oldOchan);
                                                                                                        channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                       
                                                                                                        
                                                                                                         JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                            JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                            JSONObject jsOldPartnerNewPartnerInChans = jsOldPartnerNewPartnerChans.getJSONObject("inputs");
                                                                                                            JSONObject jsOldPartnerNewPartnerInChan = jsOldPartnerNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansInputConfigs.put("From",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerOChan.put("To", keyCDName+"."+ChanName);
                                                                                                        //jsOldPartnerOChan.put("To", ".");
                                                                                                        jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                       
                                                                                                        //in case of 2 channels coupling 3 communicating CDs
                                                                                                        
                                                                                                        if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                                            
                                                                                                            
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        //jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        //jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        }
                                                                                                         
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                          
                                                                                                            
                                                                                                             jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                      
                                                                                                        
                                                                                                    //}
                                                                                                    
                                                                                                
                                                                                                
                                                                                                
                                                                                            }
                                                                                        
                                                                                        
                                                                                    } else if (im.IsCDNameRegisteredInAnotherSS(newPartnerpnames[0])){
                                                                                        
                                                                                        //new partner distributed
                                                                                        
                                                                                        //if(inchan.IsChannelLocal()){
                                                                                            inchan.setDistributed();
                                                                                            inchan.setInterfaceManager(im);
                                                                                        //}
                                                                                        
                                                                                        
                                                                                        
                                                                                        inchan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName;
                                                                                        inchan.TransmitReconfigChanChanges(keyCDName+"."+ChanName);
                                                                                        //inchan.UpdateRemotePartner();
                                                                                        
                                                                                        
                                                                                        
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        
                                                                                        jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                        
                                                                                        
                                                                                    }
                                                                
                                                                
                                                            } else {
                                                                
                                                                 String oldInChanFrom = SChansInputConfigs.getString("From");
                                                                 String oldOchanName = oldInChanFrom.split("\\.")[1];
                                                                String oldPartnerpname = oldInChanFrom+"_o"; 
                                                                
                                                                String[] oldPartnerpnames = oldPartnerpname.split("\\.");
                                                                
                                                                //if old Partner is local
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(oldPartnerpnames[0]))){
                                                                    
                                                                    ClockDomain partnercdOld = null;
                                                                    
                                                                    if (sc.SchedulerHasCD(oldPartnerpnames[0])){
                                                                        
                                                                        partnercdOld = sc.getClockDomain(oldPartnerpnames[0]);
                                                                        
                                                                        
                                                                        
                                                                    } else if(CDObjectsBuffer.CDObjBufferHas(oldPartnerpnames[0])){
                                                                        
                                                                        partnercdOld = CDObjectsBuffer.GetCDInstancesFromBuffer(oldPartnerpnames[0]);
                                                                    }   
                                                                        output_Channel oldOchan;
                                                                            
                                                                                Field fOldOchan = partnercdOld.getClass().getField(oldOchanName);
                                                                                oldOchan = (output_Channel)fOldOchan.get(partnercdOld);
                                                                                
                                                                                //if(inchan.get_r_s()==0 && oldOchan.get_w_s()==0){
                                                                                    
                                                                                    //the chan and old partner hasn't rendezvous, the sender hasn't sent anything
                                                                                    //check the new partner's rendezvous
                                                                                    
                                                                                    //if new Partner is local
                                                                                    if(keyCurrSS.equals(im.getCDLocation(newPartnerpnames[0]))){
                                                                                        
                                                                                        ClockDomain newPartnercd=null;
                                                                                        
                                                                                        //if new partner is active
                                                                                        
                                                                                        if (sc.SchedulerHasCD(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = sc.getClockDomain(newPartnerpnames[0]);
                                                                                            
                                                                                        } else if (CDObjectsBuffer.CDObjBufferHas(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(newPartnerpnames[0]);
                                                                                        }    
                                                                                            output_Channel newPartnerOchan;
                                                                                            
                                                                                            Field fNewPartnerOchan = newPartnercd.getClass().getField(NewPartnerChanName);
                                                                                            newPartnerOchan = (output_Channel)fNewPartnerOchan.get(newPartnercd);
                                                                                            
                                                                                            JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                                            JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                                            JSONObject jsNewPartnerOChans = jsNewPartnerChans.getJSONObject("outputs");
                                                                                            JSONObject jsNewPartnerOChan = jsNewPartnerOChans.getJSONObject(NewPartnerChanName);
                                                                                            String OldPartnerOfNewPartnerOChanTo = jsNewPartnerOChan.getString("To");
                                                                                            
                                                                                            //if new partner doesnt have a partner
                                                                                            if(OldPartnerOfNewPartnerOChanTo.equalsIgnoreCase(".")){
                                                                                                
                                                                                                //if no partner, then no need to check for rendezvous, reconfigure immediately
                                                                                                //String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                                                //String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                
                                                                                                  
                                                                                                   
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                
                                                                                                        inchan.PartnerName = newPartnerpname;
                                                                                                        newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                                        oldOchan.PartnerName=".";
                                                                                                        //newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        inchan.set_partner_smp(newPartnerOchan);
                                                                                                        newPartnerOchan.set_partner_smp(inchan);
                                                                                                        oldOchan.set_partner_smp(new input_Channel());
                                                                                                        //newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(inchan.Name,inchan);
                                                                                                        channels.put(newPartnerOchan.Name,newPartnerOchan);
                                                                                                        channels.put(oldOchan.Name,oldOchan);
                                                                                                        //channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        
                                                                                                        JSONObject jsOldPartnerCDMap = jsLocalCDs.getJSONObject(oldPartnerpnames[0]);
                                                                                                        JSONObject jsOldPartnerChans = jsOldPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerOChans = jsOldPartnerChans.getJSONObject("outputs");
                                                                                                        JSONObject jsOldPartnerOChan = jsOldPartnerOChans.getJSONObject(oldOchanName);
                                                                                                        
                                                                                                        //JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                        //JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                        //JSONObject jsOldPartnerNewPartnerInChans = jsOldPartnerNewPartnerChans.getJSONObject("inputs");
                                                                                                        //JSONObject jsOldPartnerNewPartnerInChan = jsOldPartnerNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansInputConfigs.put("From",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerOChan.put("To", keyCDName+"."+ChanName);
                                                                                                        jsOldPartnerOChan.put("To", ".");
                                                                                                        //jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                        if(oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                            //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                            jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            //jsOldPartnerNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                                            //jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            //jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            //jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            //jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            //jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            //jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        } else
                                                                                                        
                                                                                                        //in case of 2 channels coupling 3 communicating CDs
                                                                                                       
                                                                                                            if (oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                            jsOldPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                            jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                             jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            //jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            //jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            //jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        }
                                                                                                       
                                                                                                        else
                                                                                                            
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            //jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            //jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            //jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            //jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                             jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                        
                                                                                                
                                                                                                
                                                                                            } else {
                                                                                                
                                                                                                String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                                                String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                ClockDomain NewPartnerOldPartnercd = null;
                                                                                                
                                                                                                if(sc.SchedulerHasCD(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = sc.getClockDomain(OldPartnerOfNewPartnerCDName);
                                                                                                    
                                                                                                } else if (CDObjectsBuffer.CDObjBufferHas(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(OldPartnerOfNewPartnerCDName);
                                                                                                }    
                                                                                                    input_Channel newPartnerOldPartnerInchan;
                                                                                            
                                                                                                    Field fNewPartnerOldPartnerInchan = NewPartnerOldPartnercd.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                                    newPartnerOldPartnerInchan = (input_Channel)fNewPartnerOldPartnerInchan.get(NewPartnerOldPartnercd);
                                                                                                    
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                        CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                        
                                                                                                        if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        inchan.PartnerName = newPartnerpname;
                                                                                                        newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                                        oldOchan.PartnerName=".";
                                                                                                        newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        inchan.set_partner_smp(newPartnerOchan);
                                                                                                        newPartnerOchan.set_partner_smp(inchan);
                                                                                                        oldOchan.set_partner_smp(new input_Channel());
                                                                                                        newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(inchan.Name,inchan);
                                                                                                        channels.put(newPartnerOchan.Name,newPartnerOchan);
                                                                                                        channels.put(oldOchan.Name,oldOchan);
                                                                                                        channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        
                                                                                                        JSONObject jsOldPartnerCDMap = jsLocalCDs.getJSONObject(oldPartnerpnames[0]);
                                                                                                        JSONObject jsOldPartnerChans = jsOldPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerOChans = jsOldPartnerChans.getJSONObject("outputs");
                                                                                                        JSONObject jsOldPartnerOChan = jsOldPartnerOChans.getJSONObject(oldOchanName);
                                                                                                        
                                                                                                        JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                        JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerNewPartnerInChans = jsOldPartnerNewPartnerChans.getJSONObject("inputs");
                                                                                                        JSONObject jsOldPartnerNewPartnerInChan = jsOldPartnerNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansInputConfigs.put("From",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerOChan.put("To", keyCDName+"."+ChanName);
                                                                                                        jsOldPartnerOChan.put("To", ".");
                                                                                                        jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                        if(oldPartnerpnames[0].equals(NewPartnerChanCDName) && keyCDName.equals(OldPartnerOfNewPartnerCDName)){
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                            //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                            jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            //jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            //jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            //jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        } else
                                                                                                        
                                                                                                        //in case of 2 channels coupling 3 communicating CDs
                                                                                                        
                                                                                                        if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        } 
                                                                                                        else if (oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                            jsOldPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                            jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                             jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        }
                                                                                                        
                                                                                                        else if(oldPartnerpnames[0].equals(OldPartnerOfNewPartnerCDName)){
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            
                                                                                                            jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            jsOldPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        } else
                                                                                                            
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                             jsNewPartnerOChans.put(NewPartnerChanName, jsNewPartnerOChan);
                                                                                                        jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                      
                                                                                                        
                                                                                                    //}
                                                                                                    
                                                                                                
                                                                                                
                                                                                                
                                                                                            }
                                                                                            
                                                                                            
                                                                                        
                                                                                        
                                                                                        
                                                                                    }
                                                                                    
                                                                                    
                                                                               // }
                                                                        
                                                                    
                                                                    
                                                                } if(im.IsCDNameRegisteredInAnotherSS(oldPartnerpnames[0]) || SJServiceRegistry.HasNonLocalServiceCD(oldPartnerpnames[0])){
                                                                   
                                                                        
                                                                    
                                                                        //notify old partner, old partner is reconfigured by the other side
                                                                        inchan.TransmitReconfigChanChanges(".");
                                                                    
                                                                        
                                                                        
                                                                    //if new partner is local
                                                                     if(im.getCDLocation(NewPartnerChanCDName).equals(keyCurrSS)){
                                                                         
                                                                         //local new partner
                                                                         
                                                                         ClockDomain newPartnerCD = null;
                                                                          
                                                                         if(sc.SchedulerHasCD(NewPartnerChanCDName)){
                                                                             
                                                                             newPartnerCD = sc.getClockDomain(NewPartnerChanCDName);
                                                                             
                                                                         } else if (CDObjectsBuffer.CDObjBufferHas(NewPartnerChanCDName)){
                                                                             
                                                                             newPartnerCD = CDObjectsBuffer.GetCDInstancesFromBuffer(NewPartnerChanCDName);
                                                                             
                                                                         }
                                                                         
                                                                         output_Channel newPartnerOchan;
                                                                         Field fNewPartnerOchan = newPartnerCD.getClass().getField(NewPartnerChanName);
                                                                         newPartnerOchan = (output_Channel)fNewPartnerOchan.get(newPartnerCD);
                                                                         
                                                                         JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                         JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                         JSONObject jsNewPartnerOChans = jsNewPartnerChans.getJSONObject("outputs");
                                                                         JSONObject jsNewPartnerOChan = jsNewPartnerOChans.getJSONObject(NewPartnerChanName);
                                                                         String OldPartnerOfNewPartnerOChanTo = jsNewPartnerOChan.getString("To");
                                                                         
                                                                         //check if old partner of the new partner exists
                                                                         
                                                                         if(OldPartnerOfNewPartnerOChanTo.equalsIgnoreCase(".")){
                                                                             
                                                                             //new partner has no partner..reconfigure new partner immediately
                                                                             
                                                                             if(!inchan.IsChannelLocal()){
                                                                                 inchan.setLocal();
                                                                             }
                                                                             
                                                                             if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                             
                                                                             newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                             inchan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_o";
                                                                             
                                                                             inchan.set_partner_smp(newPartnerOchan);
                                                                             newPartnerOchan.set_partner_smp(inchan);
                                                                             
                                                                             channels.put(inchan.Name, inchan);
                                                                             channels.put(newPartnerOchan.Name, newPartnerOchan);
                                                                             
                                                                             SChansInputConfigs.put("From", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                             jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                             
                                                                         } else {
                                                                             
                                                                             String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                             String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                             
                                                                             //if old partner of new partner is local
                                                                             
                                                                             if(im.getCDLocation(OldPartnerOfNewPartnerCDName).equals(keyCurrSS)){
                                                                                 
                                                                                 if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                     
                                                                                    JSONObject jsOldPartnerOfNewPartnerCDMap = jsLocalCDs.getJSONObject(keyCDName);
                                                                                    JSONObject jsOldPartnerOfNewPartnerChans = jsOldPartnerOfNewPartnerCDMap.getJSONObject("SChannels");
                                                                                    JSONObject jsOldPartnerOfNewPartnerInChans = jsOldPartnerOfNewPartnerChans.getJSONObject("inputs");
                                                                                    JSONObject jsOldPartnerOfNewPartnerInChan = jsOldPartnerOfNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                    //String OldPartnerOfNewPartnerTo = jsOldPartnerOfNewPartnerInChan.getString("To");
                                                                                    
                                                                                    input_Channel OldPartnerOfNewPartnerInchan;
                                                                                    Field fOldPartnerOfNewPartnerInchan = cdins.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                    OldPartnerOfNewPartnerInchan = (input_Channel)fOldPartnerOfNewPartnerInchan.get(cdins);
                                                                                    
                                                                                    if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    inchan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_o";
                                                                                    OldPartnerOfNewPartnerInchan.PartnerName=".";
                                                                                    newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                    
                                                                                    OldPartnerOfNewPartnerInchan.set_partner_smp(new output_Channel());
                                                                                    inchan.set_partner_smp(newPartnerOchan);
                                                                                    newPartnerOchan.set_partner_smp(inchan);
                                                                                    
                                                                                    channels.put(inchan.Name, inchan);
                                                                                     channels.put(OldPartnerOfNewPartnerInchan.Name, OldPartnerOfNewPartnerInchan);
                                                                                      channels.put(newPartnerOchan.Name, newPartnerOchan);
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerInChan.put("From",".");
                                                                                    SChansInputConfigs.put("From", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                    
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerInChans.put(OldPartnerOfNewPartnerChanName,jsOldPartnerOfNewPartnerInChan);
                                                                                    jsOldPartnerOfNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                    jsOldPartnerOfNewPartnerChans.put("inputs",jsOldPartnerOfNewPartnerInChans);
                                                                                    jsOldPartnerOfNewPartnerCDMap.put("SChannels",jsOldPartnerOfNewPartnerChans);
                                                                                    jsLocalCDs.put(keyCDName,jsOldPartnerOfNewPartnerCDMap);
                                                                                    
                                                                                    
                                                                                    
                                                                                    
                                                                                 } else {
                                                                                     
                                                                                     JSONObject jsOldPartnerOfNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                    JSONObject jsOldPartnerOfNewPartnerChans = jsOldPartnerOfNewPartnerCDMap.getJSONObject("SChannels");
                                                                                    JSONObject jsOldPartnerOfNewPartnerInChans = jsOldPartnerOfNewPartnerChans.getJSONObject("inputs");
                                                                                    JSONObject jsOldPartnerOfNewPartnerInChan = jsOldPartnerOfNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                    //String OldPartnerOfNewPartnerTo = jsOldPartnerOfNewPartnerInChan.getString("To");
                                                                                    
                                                                                    input_Channel OldPartnerOfNewPartnerInchan;
                                                                                    Field fOldPartnerOfNewPartnerInchan = cdins.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                    OldPartnerOfNewPartnerInchan = (input_Channel)fOldPartnerOfNewPartnerInchan.get(cdins);
                                                                                    
                                                                                    if(!inchan.IsChannelLocal()){
                                                                                                          inchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    if(!newPartnerOchan.IsChannelLocal()){
                                                                                                          newPartnerOchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    inchan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_o";
                                                                                    OldPartnerOfNewPartnerInchan.PartnerName=".";
                                                                                    newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                    
                                                                                    OldPartnerOfNewPartnerInchan.set_partner_smp(new output_Channel());
                                                                                    inchan.set_partner_smp(newPartnerOchan);
                                                                                    newPartnerOchan.set_partner_smp(inchan);
                                                                                    
                                                                                    channels.put(inchan.Name, inchan);
                                                                                     channels.put(OldPartnerOfNewPartnerInchan.Name, OldPartnerOfNewPartnerInchan);
                                                                                      channels.put(newPartnerOchan.Name, newPartnerOchan);
                                                                                    
                                                                                    SChansInputConfigs.put("From", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                    jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerInChan.put("From",".");
                                                                                    jsOldPartnerOfNewPartnerInChans.put(OldPartnerOfNewPartnerChanName,jsOldPartnerOfNewPartnerInChan);
                                                                                    //jsOldPartnerOfNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                    jsOldPartnerOfNewPartnerChans.put("inputs",jsOldPartnerOfNewPartnerInChans);
                                                                                    jsOldPartnerOfNewPartnerCDMap.put("SChannels",jsOldPartnerOfNewPartnerChans);
                                                                                    jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerOfNewPartnerCDMap);
                                                                                    
                                                                                 }
                                                                                 
                                                                                
                                                                                 
                                                                             } else {
                                                                                 
                                                                                 //old partner of new partner is distributed
                                                                                 
                                                                                 newPartnerOchan.TransmitReconfigChanChanges(".");
                                                                                 newPartnerOchan.PartnerName = keyCDName+"."+ChanName+"_in";
                                                                                 
                                                                                 if(!newPartnerOchan.IsChannelLocal()){
                                                                                     newPartnerOchan.setLocal();
                                                                                 }
                                                                                 
                                                                                 
                                                                                 channels.put(newPartnerOchan.Name, newPartnerOchan);
                                                                                 
                                                                                
                                                                                 
                                                                             }
                                                                             
                                                                            
                                                                             
                                                                         }
                                                                         
                                                                          jsNewPartnerOChan.put("To",keyCDName+"."+ChanName);
                                                                                    jsNewPartnerOChans.put(NewPartnerChanName,jsNewPartnerOChan);
                                                                                    jsNewPartnerChans.put("outputs",jsNewPartnerOChans);
                                                                                    jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                    jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                         
                                                                         
                                                                         
                                                                         //check if the 
                                                                         
                                                                       
                                                                     } else if (im.IsCDNameRegisteredInAnotherSS(NewPartnerChanCDName)){
                                                                         
                                                                         if(inchan.IsChannelLocal()){
                                                                              inchan.setDistributed();
                                                                              inchan.setInterfaceManager(im);
                                                                         }
                                                                         
                                                                         inchan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_o";
                                                                         
                                                                         
                                                                         //will this reconfigure the partner?
                                                                         inchan.TransmitReconfigChanChanges(keyCDName+"."+ChanName);
                                                                        
                                                                         //inchan.setDistributed();
                                                                        //inchan.setInterfaceManager(im);
                                                                         
                                                                         
                                                                     }
                                                                    
                                                                    // 2nd possibility : the new partner is distributed also
                                                                    
                                                                     //inchan.setDistributed();
                                                                       inchan.setInterfaceManager(im);
                                                                        
                                                                       
                                                                        //inchan.setChannelCDState("Active");
                                                                        
                                                                       CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);

                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);

                                                                      //  if(!channels.containsKey(inchan.Name)){
                                                                            channels.put(inchan.Name, inchan);
                                                                        
                                                                        // end
                                                                        
                                                                        
                                                                    }
                                                                
                                                               
                                                                
                                                                } 
                                                           
                                                            } 
                                                            else if (ChanDir.equalsIgnoreCase("output")){
                                                                
                                                                JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                                                
                                                            JSONObject SChansOutputConfigs = jsSChansOutputs.getJSONObject(ChanName);

                                                            String cname = ChanName.trim()+"_o";
                                                            
                                                            String newPartnerpname = NewPartnerChanCDName+"."+NewPartnerChanName+"_in";
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] newPartnerpnames = newPartnerpname.split("\\.");
                                                                
                                                                output_Channel ochan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                ochan = (output_Channel)f.get(cdins);
                                                                
                                                            
                                                            if(SChansOutputConfigs.getString("To").equalsIgnoreCase(".")){
                                                                
                                                                 //channel doesnt have any partner, reconfig goes to check the new partner's rendezvous straightaway
                                                               
                                                                    if(keyCurrSS.equals(im.getCDLocation(newPartnerpnames[0]))){
                                                                                        
                                                                                        ClockDomain newPartnercd=null;
                                                                                        
                                                                                        //if new partner is active
                                                                                        
                                                                                        if (sc.SchedulerHasCD(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = sc.getClockDomain(newPartnerpnames[0]);
                                                                                            
                                                                                        } else if(CDObjectsBuffer.CDObjBufferHas(newPartnerpnames[0])){
                                                                                            
                                                                                             newPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(newPartnerpnames[0]);
                                                                                            
                                                                                        }
                                                                                        
                                                                                        input_Channel newPartnerInchan;
                                                                                            
                                                                                            Field fNewPartnerInchan = newPartnercd.getClass().getField(NewPartnerChanName);
                                                                                            newPartnerInchan = (input_Channel)fNewPartnerInchan.get(newPartnercd);
                                                                                            
                                                                                            JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                                            JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                                            JSONObject jsNewPartnerInChans = jsNewPartnerChans.getJSONObject("inputs");
                                                                                            JSONObject jsNewPartnerInChan = jsNewPartnerInChans.getJSONObject(NewPartnerChanName);
                                                                                            String OldPartnerOfNewPartnerInChanFrom = jsNewPartnerInChan.getString("From");
                                                                                            
                                                                                            //if new partner doesnt have a partner
                                                                                            if(OldPartnerOfNewPartnerInChanFrom.equalsIgnoreCase(".")){
                                                                                                
                                                                                                //if no partner, then no need to check for rendezvous, reconfigure immediately
                                                                                                //String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                                                //String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                
                                                                                                  
                                                                                                   
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                
                                                                                                       if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                                       
                                                                                                       if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                        ochan.PartnerName = newPartnerpname;
                                                                                                        newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                                        //oldOchan.PartnerName=".";
                                                                                                        //newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        ochan.set_partner_smp(newPartnerInchan);
                                                                                                        newPartnerInchan.set_partner_smp(ochan);
                                                                                                        //oldOchan.set_partner_smp(new input_Channel());
                                                                                                        //newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(ochan.Name,ochan);
                                                                                                        channels.put(newPartnerInchan.Name,newPartnerInchan);
                                                                                                        //channels.put(oldOchan.Name,oldOchan);
                                                                                                        //channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        SChansOutputConfigs.put("To",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerInChan.put("From", keyCDName+"."+ChanName);
                                                                                                        //jsOldPartnerOChan.put("To", ".");
                                                                                                        //jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        
                                                                                                            
                                                                                                             jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                          
                                                                                                        
                                                                                                    
                                                                                                
                                                                                            } else {
                                                                                                
                                                                                                String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[0];
                                                                                                String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                ClockDomain NewPartnerOldPartnercd = null;
                                                                                                
                                                                                                if(sc.SchedulerHasCD(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = sc.getClockDomain(OldPartnerOfNewPartnerCDName);
                                                                                                    
                                                                                                } else if (CDObjectsBuffer.CDObjBufferHas(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(OldPartnerOfNewPartnerCDName);
                                                                                                }    
                                                                                                    
                                                                                                output_Channel newPartnerOldPartnerOchan;
                                                                                            
                                                                                                    Field fNewPartnerOldPartnerOchan = NewPartnerOldPartnercd.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                                    newPartnerOldPartnerOchan = (output_Channel)fNewPartnerOldPartnerOchan.get(NewPartnerOldPartnercd);
                                                                                                    
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                        CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                        
                                                                                                        if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        
                                                                                                        
                                                                                                        ochan.PartnerName = newPartnerpname;
                                                                                                        newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                                        //oldOchan.PartnerName=".";
                                                                                                        newPartnerOldPartnerOchan.PartnerName = ".";
                                                                                                        
                                                                                                        ochan.set_partner_smp(newPartnerInchan);
                                                                                                        newPartnerInchan.set_partner_smp(ochan);
                                                                                                        //oldOchan.set_partner_smp(new input_Channel());
                                                                                                        newPartnerOldPartnerOchan.set_partner_smp(new input_Channel());
                                                                                                        
                                                                                                        channels.put(ochan.Name,ochan);
                                                                                                        channels.put(newPartnerInchan.Name,newPartnerInchan);
                                                                                                        //channels.put(oldOchan.Name,oldOchan);
                                                                                                        channels.put(newPartnerOldPartnerOchan.Name,newPartnerOldPartnerOchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        
                                                                                                        //JSONObject jsOldPartnerCDMap = jsLocalCDs.getJSONObject(oldPartnerpnames[0]);
                                                                                                        //JSONObject jsOldPartnerChans = jsOldPartnerCDMap.getJSONObject("SChannels");
                                                                                                        //JSONObject jsOldPartnerOChans = jsOldPartnerChans.getJSONObject("outputs");
                                                                                                        //JSONObject jsOldPartnerOChan = jsOldPartnerOChans.getJSONObject(oldOchanName);
                                                                                                        
                                                                                                        //JSONObject jsOldPartnerNewPartnerCDMap;
                                                                                                        
                                                                                                         JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                            JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                            JSONObject jsOldPartnerNewPartnerOChans = jsOldPartnerNewPartnerChans.getJSONObject("outputs");
                                                                                                            JSONObject jsOldPartnerNewPartnerOChan = jsOldPartnerNewPartnerOChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansOutputConfigs.put("To",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerInChan.put("From", keyCDName+"."+ChanName);
                                                                                                        //jsOldPartnerOChan.put("To", ".");
                                                                                                        jsOldPartnerNewPartnerOChan.put("To", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                        //in case of 2 channels coupling 3 communicating CDs
                                                                                                        
                                                                                                        if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                                            
                                                                                                            
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerOChans.put(ChanName,SChansOutputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        //jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        //jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        }
                                                                                                          
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerOChans.put(oldOchanName,jsOldPartnerOChan);
                                                                                                        //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                        //jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        //jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                             jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                      
                                                                                                        
                                                                                                    //}
                                                                                                    
                                                                                                
                                                                                                
                                                                                                
                                                                                            }
                                                                                        
                                                                                        
                                                                                    } else if (im.IsCDNameRegisteredInAnotherSS(newPartnerpnames[0])){
                                                                                        
                                                                                        //new partner distributed
                                                                                        
                                                                                        if(ochan.IsChannelLocal()){
                                                                                            ochan.setDistributed();
                                                                                            ochan.setInterfaceManager(im);
                                                                                        }
                                                                                        
                                                                                        ochan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName;
                                                                                        ochan.TransmitReconfigChanChanges(keyCDName+"."+ChanName);
                                                                                        
                                                                                        channels.put(ochan.Name, ochan);
                                                                                        
                                                                                        jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                        
                                                                                        
                                                                                    }
                                                                
                                                                
                                                            } else {
                                                                
                                                                 String oldInChanFrom = SChansOutputConfigs.getString("To");
                                                                 String oldInchanName = oldInChanFrom.split("\\.")[1];
                                                                String oldPartnerpname = oldInChanFrom+"_in"; 
                                                                
                                                                String[] oldPartnerpnames = oldPartnerpname.split("\\.");
                                                                
                                                                //if old Partner is local
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(oldPartnerpnames[0]))){
                                                                    
                                                                    ClockDomain partnercdOld = null;
                                                                    
                                                                    if (sc.SchedulerHasCD(oldPartnerpnames[0])){
                                                                        
                                                                        partnercdOld = sc.getClockDomain(oldPartnerpnames[0]);
                                                                        
                                                                        
                                                                        
                                                                    } else if(CDObjectsBuffer.CDObjBufferHas(oldPartnerpnames[0])){
                                                                        
                                                                        partnercdOld = CDObjectsBuffer.GetCDInstancesFromBuffer(oldPartnerpnames[0]);
                                                                    }   
                                                                        input_Channel oldInchan;
                                                                            
                                                                                Field fOldOchan = partnercdOld.getClass().getField(oldInchanName);
                                                                                oldInchan = (input_Channel)fOldOchan.get(partnercdOld);
                                                                                
                                                                                //if(inchan.get_r_s()==0 && oldOchan.get_w_s()==0){
                                                                                    
                                                                                    //the chan and old partner hasn't rendezvous, the sender hasn't sent anything
                                                                                    //check the new partner's rendezvous
                                                                                    
                                                                                    //if new Partner is local
                                                                                    if(keyCurrSS.equals(im.getCDLocation(newPartnerpnames[0]))){
                                                                                        
                                                                                        ClockDomain newPartnercd=null;
                                                                                        
                                                                                        //if new partner is active
                                                                                        
                                                                                        if (sc.SchedulerHasCD(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = sc.getClockDomain(newPartnerpnames[0]);
                                                                                            
                                                                                            
                                                                                            
                                                                                            
                                                                                        } else if (CDObjectsBuffer.CDObjBufferHas(newPartnerpnames[0])){
                                                                                            
                                                                                            newPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(newPartnerpnames[0]);
                                                                                        }    
                                                                                            input_Channel newPartnerInchan;
                                                                                            
                                                                                            Field fNewPartnerOchan = newPartnercd.getClass().getField(NewPartnerChanName);
                                                                                            newPartnerInchan = (input_Channel)fNewPartnerOchan.get(newPartnercd);
                                                                                            
                                                                                            JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                                            JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                                            JSONObject jsNewPartnerInChans = jsNewPartnerChans.getJSONObject("inputs");
                                                                                            JSONObject jsNewPartnerInChan = jsNewPartnerInChans.getJSONObject(NewPartnerChanName);
                                                                                            String OldPartnerOfNewPartnerInChanFrom = jsNewPartnerInChan.getString("From");
                                                                                            
                                                                                            //if new partner doesnt have a partner
                                                                                            if(OldPartnerOfNewPartnerInChanFrom.equalsIgnoreCase(".")){
                                                                                                
                                                                                                //if no partner, then no need to check for rendezvous, reconfigure immediately
                                                                                                //String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerOChanTo.split("\\.")[0];
                                                                                                //String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerOChanTo.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                
                                                                                                  
                                                                                                   
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                                
                                                                                                CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                
                                                                                                        ochan.PartnerName = newPartnerpname;
                                                                                                        newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                                        oldInchan.PartnerName=".";
                                                                                                        //newPartnerOldPartnerInchan.PartnerName = ".";
                                                                                                        
                                                                                                        ochan.set_partner_smp(newPartnerInchan);
                                                                                                        newPartnerInchan.set_partner_smp(ochan);
                                                                                                        oldInchan.set_partner_smp(new output_Channel());
                                                                                                        //newPartnerOldPartnerInchan.set_partner_smp(new output_Channel());
                                                                                                        
                                                                                                        channels.put(ochan.Name,ochan);
                                                                                                        channels.put(newPartnerInchan.Name,newPartnerInchan);
                                                                                                        channels.put(oldInchan.Name,oldInchan);
                                                                                                        //channels.put(newPartnerOldPartnerInchan.Name,newPartnerOldPartnerInchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        
                                                                                                        JSONObject jsOldPartnerCDMap = jsLocalCDs.getJSONObject(oldPartnerpnames[0]);
                                                                                                        JSONObject jsOldPartnerChans = jsOldPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerInChans = jsOldPartnerChans.getJSONObject("inputs");
                                                                                                        JSONObject jsOldPartnerInChan = jsOldPartnerInChans.getJSONObject(oldInchanName);
                                                                                                        
                                                                                                        //JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                        //JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                        //JSONObject jsOldPartnerNewPartnerInChans = jsOldPartnerNewPartnerChans.getJSONObject("inputs");
                                                                                                        //JSONObject jsOldPartnerNewPartnerInChan = jsOldPartnerNewPartnerInChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansOutputConfigs.put("To",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerInChan.put("From", keyCDName+"."+ChanName);
                                                                                                        jsOldPartnerInChan.put("From", ".");
                                                                                                        //jsOldPartnerNewPartnerInChan.put("From", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                        if(oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                            //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                            jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                           
                                                                                                        } else
                                                                                                        
                                                                                                        
                                                                                                            if (oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                            jsOldPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                            jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                             jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            //jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            //jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            //jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            //jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        }
                                                                                                        
                                                                                                        else
                                                                                                            
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            //jsOldPartnerNewPartnerInChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerInChan);
                                                                                                            //jsOldPartnerNewPartnerChans.put("inputs", jsOldPartnerNewPartnerInChans);
                                                                                                            //jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            //jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                        jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                             jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                        
                                                                                                
                                                                                            } else {
                                                                                                
                                                                                                String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[0];
                                                                                                String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[1];
                                                                                                  
                                                                                                //if active
                                                                                                ClockDomain NewPartnerOldPartnercd = null;
                                                                                                
                                                                                                if(sc.SchedulerHasCD(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = sc.getClockDomain(OldPartnerOfNewPartnerCDName);
                                                                                                    
                                                                                                } else if (CDObjectsBuffer.CDObjBufferHas(OldPartnerOfNewPartnerCDName)){
                                                                                                    
                                                                                                    NewPartnerOldPartnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(OldPartnerOfNewPartnerCDName);
                                                                                                }    
                                                                                                    output_Channel newPartnerOldPartnerOchan;
                                                                                            
                                                                                                    Field fNewPartnerOldPartnerOchan = NewPartnerOldPartnercd.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                                    newPartnerOldPartnerOchan = (output_Channel)fNewPartnerOldPartnerOchan.get(NewPartnerOldPartnercd);
                                                                                                    
                                                                                                    //if(newPartnerOchan.get_w_s()==0 && newPartnerOldPartnerInchan.get_r_s()==0){
                                                                                                        
                                                                                                        //no rendezvous and data is not buffered yet by the sender
                                                                                                        
                                                                                                        //reconfigure!
                                                                                                        
                                                                                                        CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);
                                                                                                        
                                                                                                        if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                                        
                                                                                                        ochan.PartnerName = newPartnerpname;
                                                                                                        newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                                        oldInchan.PartnerName=".";
                                                                                                        newPartnerOldPartnerOchan.PartnerName = ".";
                                                                                                        
                                                                                                        ochan.set_partner_smp(newPartnerInchan);
                                                                                                        newPartnerInchan.set_partner_smp(ochan);
                                                                                                        oldInchan.set_partner_smp(new output_Channel());
                                                                                                        newPartnerOldPartnerOchan.set_partner_smp(new input_Channel());
                                                                                                        
                                                                                                        channels.put(ochan.Name,ochan);
                                                                                                        channels.put(newPartnerInchan.Name,newPartnerInchan);
                                                                                                        channels.put(oldInchan.Name,oldInchan);
                                                                                                        channels.put(newPartnerOldPartnerOchan.Name,newPartnerOldPartnerOchan);
                                                                                                        //change the cd config
                                                                                                        
                                                                                                        JSONObject jsOldPartnerCDMap = jsLocalCDs.getJSONObject(oldPartnerpnames[0]);
                                                                                                        JSONObject jsOldPartnerChans = jsOldPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerInChans = jsOldPartnerChans.getJSONObject("inputs");
                                                                                                        JSONObject jsOldPartnerInChan = jsOldPartnerInChans.getJSONObject(oldInchanName);
                                                                                                        
                                                                                                        JSONObject jsOldPartnerNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                                        JSONObject jsOldPartnerNewPartnerChans = jsOldPartnerNewPartnerCDMap.getJSONObject("SChannels");
                                                                                                        JSONObject jsOldPartnerNewPartnerOChans = jsOldPartnerNewPartnerChans.getJSONObject("outputs");
                                                                                                        JSONObject jsOldPartnerNewPartnerOChan = jsOldPartnerNewPartnerOChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                                        
                                                                                                        SChansOutputConfigs.put("To",NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                                        jsNewPartnerInChan.put("From", keyCDName+"."+ChanName);
                                                                                                        jsOldPartnerInChan.put("From", ".");
                                                                                                        jsOldPartnerNewPartnerOChan.put("To", ".");
                                                                                                        
                                                                                                        //in case of 2 channels coupling 2 communicating CDs
                                                                                                        
                                                                                                        if(oldPartnerpnames[0].equals(NewPartnerChanCDName) && keyCDName.equals(OldPartnerOfNewPartnerCDName)){
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                            //jsOldPartnerChans.put("outputs",jsOldPartnerOChans);
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                            jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerOChans.put(ChanName,SChansOutputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            //jsSChansInputs.put(ChanName, SChansInputConfigs);
                                                                                                            //jsSigsChansInd.put("inputs",jsSChansInputs);
                                                                                                            //jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            //jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        } else
                                                                                                        
                                                                                                        //in case of 2 channels coupling 3 communicating CDs
                                                                                                        
                                                                                                        if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerOChans.put(ChanName,SChansOutputConfigs);
                                                                                                            jsOldPartnerNewPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                        jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        } 
                                                                                                        else if (oldPartnerpnames[0].equals(NewPartnerChanCDName)){
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                            jsOldPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                            jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                             jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("inputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        }
                                                                                                        
                                                                                                        else if(oldPartnerpnames[0].equals(OldPartnerOfNewPartnerCDName)){
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            
                                                                                                            jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                            jsOldPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                            jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                            jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                            jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                            
                                                                                                        } else
                                                                                                            
                                                                                                            //in case of 2 channels previously coupling 2 pair of CDs
                                                                                                            
                                                                                                        {
                                                                                                            
                                                                                                            jsOldPartnerNewPartnerOChans.put(OldPartnerOfNewPartnerChanName, jsOldPartnerNewPartnerOChan);
                                                                                                            jsOldPartnerNewPartnerChans.put("outputs", jsOldPartnerNewPartnerOChans);
                                                                                                            jsOldPartnerNewPartnerCDMap.put("SChannels", jsOldPartnerNewPartnerChans);
                                                                                                            jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerNewPartnerCDMap);
                                                                                                            
                                                                                                            jsOldPartnerInChans.put(oldInchanName,jsOldPartnerInChan);
                                                                                                        jsOldPartnerChans.put("inputs",jsOldPartnerInChans);
                                                                                                        jsOldPartnerCDMap.put("SChannels",jsOldPartnerChans);
                                                                                                        jsLocalCDs.put(oldPartnerpnames[0],jsOldPartnerCDMap);
                                                                                                            
                                                                                                             jsNewPartnerInChans.put(NewPartnerChanName, jsNewPartnerInChan);
                                                                                                        jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                                        jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                                        jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                                                            
                                                                                                        
                                                                                                             jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                                            
                                                                                                        }
                                                                                                      
                                                                                                        
                                                                                                    //}
                                                                                                   
                                                                                            }
                                                                                            
                                                                                           
                                                                                        
                                                                                    }
                                                                                    
                                                                                    
                                                                               // }
                                                                        
                                                                    
                                                                    
                                                                } if(im.IsCDNameRegisteredInAnotherSS(oldPartnerpnames[0]) || SJServiceRegistry.HasNonLocalServiceCD(oldPartnerpnames[0])){
                                                                   
                                                                        //old partner CD is in another SS!
                                                                        
                                                                        //input_Channel inchan;
                                                                        
                                                                        //Field f = cdins.getClass().getField(cname);
                                                                    
                                                                        // 1st possibility : the new partner is local
                                                                        //inchan = (input_Channel)f.get(cdins);
                                                                        
                                                                        //inchan.PartnerName = oldPartnerpname;
                                                                        
                                                                        //inchan.setInit();
                                                                        //inchan.setDistributed();
                                                                        //inchan.setInterfaceManager(im);
                                                                    
                                                                    
                                                                        //notify old partner, old partner is reconfigured by the other side
                                                                        ochan.TransmitReconfigChanChanges(".");
                                                                    
                                                                        
                                                                        
                                                                    //if new partner is local
                                                                     if(im.getCDLocation(NewPartnerChanCDName).equals(keyCurrSS)){
                                                                         
                                                                         //local new partner
                                                                         
                                                                         ClockDomain newPartnerCD = null;
                                                                          
                                                                         if(sc.SchedulerHasCD(NewPartnerChanCDName)){
                                                                             
                                                                             newPartnerCD = sc.getClockDomain(NewPartnerChanCDName);
                                                                             
                                                                         } else if (CDObjectsBuffer.CDObjBufferHas(NewPartnerChanCDName)){
                                                                             
                                                                             newPartnerCD = CDObjectsBuffer.GetCDInstancesFromBuffer(NewPartnerChanCDName);
                                                                             
                                                                         }
                                                                         
                                                                         input_Channel newPartnerInchan;
                                                                         Field fNewPartnerInchan = newPartnerCD.getClass().getField(NewPartnerChanName);
                                                                         newPartnerInchan = (input_Channel)fNewPartnerInchan.get(newPartnerCD);
                                                                         
                                                                         JSONObject jsNewPartnerCDMap = jsLocalCDs.getJSONObject(NewPartnerChanCDName);
                                                                         JSONObject jsNewPartnerChans = jsNewPartnerCDMap.getJSONObject("SChannels");
                                                                         JSONObject jsNewPartnerInChans = jsNewPartnerChans.getJSONObject("inputs");
                                                                         JSONObject jsNewPartnerInChan = jsNewPartnerInChans.getJSONObject(NewPartnerChanName);
                                                                         String OldPartnerOfNewPartnerInChanFrom = jsNewPartnerInChan.getString("From");
                                                                         
                                                                         //check if old partner of the new partner exists
                                                                         
                                                                         if(OldPartnerOfNewPartnerInChanFrom.equalsIgnoreCase(".")){
                                                                             
                                                                             //new partner has no partner..reconfigure new partner immediately
                                                                             
                                                                             if(!ochan.IsChannelLocal()){
                                                                                 ochan.setLocal();
                                                                             }
                                                                             
                                                                             if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                             
                                                                             newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                             ochan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_in";
                                                                             
                                                                             ochan.set_partner_smp(newPartnerInchan);
                                                                             newPartnerInchan.set_partner_smp(ochan);
                                                                             
                                                                             channels.put(ochan.Name, ochan);
                                                                             channels.put(newPartnerInchan.Name, newPartnerInchan);
                                                                             
                                                                             SChansOutputConfigs.put("To", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                             jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                             
                                                                         } else {
                                                                             
                                                                             String OldPartnerOfNewPartnerCDName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[0];
                                                                             String OldPartnerOfNewPartnerChanName = OldPartnerOfNewPartnerInChanFrom.split("\\.")[1];
                                                                             
                                                                             //if old partner of new partner is local
                                                                             
                                                                             if(im.getCDLocation(OldPartnerOfNewPartnerCDName).equals(keyCurrSS)){
                                                                                 
                                                                                 if(OldPartnerOfNewPartnerCDName.equals(keyCDName)){
                                                                                     
                                                                                    JSONObject jsOldPartnerOfNewPartnerCDMap = jsLocalCDs.getJSONObject(keyCDName);
                                                                                    JSONObject jsOldPartnerOfNewPartnerChans = jsOldPartnerOfNewPartnerCDMap.getJSONObject("SChannels");
                                                                                    JSONObject jsOldPartnerOfNewPartnerOChans = jsOldPartnerOfNewPartnerChans.getJSONObject("outputs");
                                                                                    JSONObject jsOldPartnerOfNewPartnerOChan = jsOldPartnerOfNewPartnerOChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                    //String OldPartnerOfNewPartnerTo = jsOldPartnerOfNewPartnerInChan.getString("To");
                                                                                    
                                                                                    output_Channel OldPartnerOfNewPartnerOchan;
                                                                                    Field fOldPartnerOfNewPartnerOchan = cdins.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                    OldPartnerOfNewPartnerOchan = (output_Channel)fOldPartnerOfNewPartnerOchan.get(cdins);
                                                                                    
                                                                                    if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    ochan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_in";
                                                                                    OldPartnerOfNewPartnerOchan.PartnerName=".";
                                                                                    newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                    
                                                                                    OldPartnerOfNewPartnerOchan.set_partner_smp(new input_Channel());
                                                                                    ochan.set_partner_smp(newPartnerInchan);
                                                                                    newPartnerInchan.set_partner_smp(ochan);
                                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                                                     channels.put(OldPartnerOfNewPartnerOchan.Name, OldPartnerOfNewPartnerOchan);
                                                                                      channels.put(newPartnerInchan.Name, newPartnerInchan);
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerOChan.put("To",".");
                                                                                    SChansOutputConfigs.put("To", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                    
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerOChans.put(OldPartnerOfNewPartnerChanName,jsOldPartnerOfNewPartnerOChan);
                                                                                    jsOldPartnerOfNewPartnerOChans.put(ChanName,SChansOutputConfigs);
                                                                                    jsOldPartnerOfNewPartnerChans.put("outputs",jsOldPartnerOfNewPartnerOChans);
                                                                                    jsOldPartnerOfNewPartnerCDMap.put("SChannels",jsOldPartnerOfNewPartnerChans);
                                                                                    jsLocalCDs.put(keyCDName,jsOldPartnerOfNewPartnerCDMap);
                                                                                    
                                                                                    
                                                                                 } else {
                                                                                     
                                                                                     JSONObject jsOldPartnerOfNewPartnerCDMap = jsLocalCDs.getJSONObject(OldPartnerOfNewPartnerCDName);
                                                                                    JSONObject jsOldPartnerOfNewPartnerChans = jsOldPartnerOfNewPartnerCDMap.getJSONObject("SChannels");
                                                                                    JSONObject jsOldPartnerOfNewPartnerOChans = jsOldPartnerOfNewPartnerChans.getJSONObject("outputs");
                                                                                    JSONObject jsOldPartnerOfNewPartnerOChan = jsOldPartnerOfNewPartnerOChans.getJSONObject(OldPartnerOfNewPartnerChanName);
                                                                                    //String OldPartnerOfNewPartnerTo = jsOldPartnerOfNewPartnerInChan.getString("To");
                                                                                    
                                                                                    output_Channel OldPartnerOfNewPartnerOchan;
                                                                                    Field fOldPartnerOfNewPartnerOchan = cdins.getClass().getField(OldPartnerOfNewPartnerChanName);
                                                                                    OldPartnerOfNewPartnerOchan = (output_Channel)fOldPartnerOfNewPartnerOchan.get(cdins);
                                                                                    
                                                                                    if(!ochan.IsChannelLocal()){
                                                                                                          ochan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    if(!newPartnerInchan.IsChannelLocal()){
                                                                                                          newPartnerInchan.setLocal();
                                                                                                       }
                                                                                    
                                                                                    ochan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_in";
                                                                                    OldPartnerOfNewPartnerOchan.PartnerName=".";
                                                                                    newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                    
                                                                                    OldPartnerOfNewPartnerOchan.set_partner_smp(new input_Channel());
                                                                                    ochan.set_partner_smp(newPartnerInchan);
                                                                                    newPartnerInchan.set_partner_smp(ochan);
                                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                                                     channels.put(OldPartnerOfNewPartnerOchan.Name, OldPartnerOfNewPartnerOchan);
                                                                                      channels.put(newPartnerInchan.Name, newPartnerInchan);
                                                                                    
                                                                                    SChansOutputConfigs.put("To", NewPartnerChanCDName+"."+NewPartnerChanName);
                                                                                    jsSChansOutputs.put(ChanName, SChansOutputConfigs);
                                                                                                            jsSigsChansInd.put("outputs",jsSChansOutputs);
                                                                                                            jsSigsChans.put("SChannels", jsSigsChansInd);
                                                                                                            jsLocalCDs.put(keyCDName, jsSigsChans);
                                                                                    
                                                                                    jsOldPartnerOfNewPartnerOChan.put("To",".");
                                                                                    jsOldPartnerOfNewPartnerOChans.put(OldPartnerOfNewPartnerChanName,jsOldPartnerOfNewPartnerOChan);
                                                                                    //jsOldPartnerOfNewPartnerInChans.put(ChanName,SChansInputConfigs);
                                                                                    jsOldPartnerOfNewPartnerChans.put("outputs",jsOldPartnerOfNewPartnerOChans);
                                                                                    jsOldPartnerOfNewPartnerCDMap.put("SChannels",jsOldPartnerOfNewPartnerChans);
                                                                                    jsLocalCDs.put(OldPartnerOfNewPartnerCDName,jsOldPartnerOfNewPartnerCDMap);
                                                                                    
                                                                                 }
                                                                                 
                                                                                
                                                                                 
                                                                             } else {
                                                                                 
                                                                                 //old partner of new partner is distributed
                                                                                 
                                                                                 newPartnerInchan.TransmitReconfigChanChanges(".");
                                                                                 newPartnerInchan.PartnerName = keyCDName+"."+ChanName+"_o";
                                                                                 
                                                                                 if(!newPartnerInchan.IsChannelLocal()){
                                                                                     newPartnerInchan.setLocal();
                                                                                 }
                                                                                 
                                                                                 
                                                                                 channels.put(newPartnerInchan.Name, newPartnerInchan);
                                                                                 
                                                                             }
                                                                             
                                                                         }
                                                                         
                                                                          jsNewPartnerInChan.put("From",keyCDName+"."+ChanName);
                                                                                    jsNewPartnerInChans.put(NewPartnerChanName,jsNewPartnerInChan);
                                                                                    jsNewPartnerChans.put("inputs",jsNewPartnerInChans);
                                                                                    jsNewPartnerCDMap.put("SChannels",jsNewPartnerChans);
                                                                                    jsLocalCDs.put(NewPartnerChanCDName,jsNewPartnerCDMap);
                                                                         
                                                                         
                                                                         //check if the 
                                                                         
                                                                       
                                                                     } else if (im.IsCDNameRegisteredInAnotherSS(NewPartnerChanCDName)){
                                                                         
                                                                         if(ochan.IsChannelLocal()){
                                                                              ochan.setDistributed();
                                                                              ochan.setInterfaceManager(im);
                                                                         }
                                                                         
                                                                         ochan.PartnerName = NewPartnerChanCDName+"."+NewPartnerChanName+"_in";
                                                                         
                                                                         
                                                                         //will this reconfigure the partner?
                                                                         ochan.TransmitReconfigChanChanges(keyCDName+"."+ChanName);
                                                                        
                                                                         //inchan.setDistributed();
                                                                        //inchan.setInterfaceManager(im);
                                                                         
                                                                         
                                                                     }
                                                                    
                                                                    // 2nd possibility : the new partner is distributed also
                                                                    
                                                                     //inchan.setDistributed();
                                                                       ochan.setInterfaceManager(im);
                                                                        
                                                                       
                                                                        //inchan.setChannelCDState("Active");
                                                                        
                                                                       CDLCBuffer.AddStatChanReconfig(keyCDName,ChanName,true);

                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);

                                                                      //  if(!channels.containsKey(inchan.Name)){
                                                                            channels.put(ochan.Name, ochan);
                                                                        
                                                                        // end
                                                                        
                                                                        
                                                                    }
                                                                
                                                                } 
                                                                
                                                            }
                                                            
                                                           
                                                    im.setChannelInstances(channels);
                                                    
                                                    JSONObject currMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                                                    currMap.put(keyCurrSS, jsLocalCDs);
                                                    SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(currMap);
                                                    
            
        } catch (JSONException jex){
            jex.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
            
           
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    
        
        
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                
                                return vec;
            
        }
    
}
