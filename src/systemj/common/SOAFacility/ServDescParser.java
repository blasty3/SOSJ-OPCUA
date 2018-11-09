package systemj.common.SOAFacility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
//import org.json.simple.*;
//import net.minidev.json.*;
import org.json.me.*;

import systemj.bootstrap.ClockDomain;
import systemj.bootstrap.SystemJProgram;
import systemj.common.CDObjectsBuffer;

import systemj.common.CyclicScheduler;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
//import systemj.common.SignalObjBuffer;
import systemj.interfaces.GenericInterface;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Scheduler;
import systemj.lib.AChannel;
import systemj.lib.Signal;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;

public class ServDescParser{

private String file;
private String fnameAll;
//private String SSName;
//private SystemJProgram program;
private InputStream is;
//private MainPrinter mp = new MainPrinter();
private int gid = 0;

//private int intServiceIndex=1;

//Udayanto modification
JSONObject jsParsedCD = new JSONObject();
JSONObject jsParsedSS = new JSONObject();


//JSONObject jsNonLocalSSName = new JSONObject();

JSONObject jsAllSSName = new JSONObject();

JSONObject localServList = new JSONObject();

JSONObject allPhyServDescr = new JSONObject();
JSONObject phyServDescrParamInd = new JSONObject();
JSONObject phyServDescrParam = new JSONObject();

JSONObject localServAttr = new JSONObject();
JSONObject localServAttrPrnt = new JSONObject();

JSONObject argumentServ = new JSONObject();
JSONObject actionServ = new JSONObject();
JSONObject actionServTot = new JSONObject();
JSONObject stateVarServ = new JSONObject();
JSONObject stateVarServTot = new JSONObject();

JSONObject allowedValRangeServ = new JSONObject();
JSONObject allowedValListServ = new JSONObject();

JSONObject stateVarParams = new JSONObject();
JSONObject stateVarParamsAttrs = new JSONObject();

JSONObject actionIntf = new JSONObject();
JSONObject actionIntfTot = new JSONObject();

//SJServiceRegistry currServReg = new SJServiceRegistry();


//Udayanto modification

public JSONObject parse(String file) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        //if(file != null){
                File f = new File(file);
                doc = builder.build(f);
                
        //}
        //else if(is != null)
                //doc = builder.build(is);
        //else
                //throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        //if(!e.getName().equals("System"))
         //       throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        
                }
        }

       
        
        JSONObject jsCDMap = parseSubSystem(e);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
       
        //mp.flushSDSPOT();
        return jsCDMap;
        //SJServiceRegistry.setParsingStatus(true);
       
}

public JSONObject parse(String CDName, String ServName, String file) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        //if(file != null){
                File f = new File(file);
                doc = builder.build(f);
                
        //}
        //else if(is != null)
                //doc = builder.build(is);
        //else
                //throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        //if(!e.getName().equals("System"))
         //       throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        
                }
        }

       
        
        JSONObject jsCDMap = parseSubSystem(e,CDName,ServName);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
       
        //mp.flushSDSPOT();
        return jsCDMap;
        //SJServiceRegistry.setParsingStatus(true);
       
}


public JSONObject parseSubSystem(Element subsystem){
        List<Element> cds = subsystem.getChildren("serviceDescription");
        //List<Element> schedulers = subsystem.getChildren("Scheduler");
        //List<Element> rmi = subsystem.getChildren("RMI");

        //JSONObject jsSSCDTot = new JSONObject();
        //Udayanto modification  // parse Subsystem to obtain all available services in the node-self
      //  Element devDescSS = subsystem.getChild("deviceDescription");
    //    List<Element> devDescSSLists = devDescSS.getAttributes();

     //   for (Element devDescSSList : devDescSSLists)
    //    {
    //        if(devDescSSList.getAttributeValue("deviceType") == null || devDescSSList.getAttributeValue("nodeIPAddress")==null)
//				throw new RuntimeException("'deviceDescription' require 'deviceType' and 'NodeIPAddress' attributes, NodeIPAddress' being the node IPv4 address");
   //     }
         int internalServiceIndex = 0;

        //Udayanto modification END

        //System.out.println("Local SubSystem : "+subsystem.getAttributeValue("Name"));
       
        
        //SSName = subsystem.getAttributeValue("Name");
        
        Hashtable clockdomains = new Hashtable();

        // First pass to create all the local subsystem's clock-domains
       

        //System.out.println("Local cds : "+clockdomains);
        
        
        //CDObjectsBuffer.CopySCCDInstancesToMap(clockdomains);
        
        Hashtable channels = new Hashtable();
        
        
         // Checking whether RMI port num has been defined
         
         
        JSONObject jsCDtot = new JSONObject();
        
        // This goes to default scheduler
        
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
        
        //System.out.println("CD ss =" +subsystem);
                    
                    JSONObject jsCDMap = parseClockDomain(cd,channels, clockdomains, subsystem.getAttributeValue("Class"));
                    
                        Enumeration enumParsedCD = jsCDMap.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsCDMap.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                        
                        // need to handle device description

                                try {

                                //if (!localServAttr.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    //if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    
                                    
                                    
                                    if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {
                                        internalServiceIndex++; 
                                        
                                        Enumeration ServAttrEnum = localServAttrPrnt.keys();
                                        
                                        while (ServAttrEnum.hasMoreElements()){
                                            Object key = ServAttrEnum.nextElement(); 
                                            localServList.put(key.toString(), localServAttrPrnt.get(key.toString()));
                                            //String k = key.toString();
                                            
                                        }
                                        
                                        //localServList.put("service"+internalServiceIndex, localServAttr);
                                        //localServList=localServAttrPrnt;
                                    }
                   
                                    
                                   // System.out.println("CDLCMapParser, localServAttrPrnt:" +localServAttrPrnt.toPrettyPrintedString(2, 0));
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        
                        
               //     }

                        //  Udayanto modification

                }
             
       
        return localServList;
        
}


/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
 * @param cd
 * @param subsystem
 * @return ClockDomain instance initialized from this method call
 */
public JSONObject parseClockDomain(Element cd, Hashtable channels, Hashtable clockdomains, String CDClassName){

    
    
        //clear the attribute storage
        boolean servChecker = false;
    //Udayanto modif
        localServAttr = new JSONObject();
        
        localServAttrPrnt = new JSONObject();
        
        //argumentServ = new JSONObject();
        actionServ = new JSONObject();
        actionServTot = new JSONObject();
        stateVarServ = new JSONObject();
        stateVarServTot = new JSONObject();

        //allowedValRangeServ = new JSONObject();
        //allowedValListServ = new JSONObject();

        /*
        boolean isServices = false;
        String cdname = cd.getAttributeValue("Name");
        if(cd.getAttributeValue("IsServices") == null){
           throw new RuntimeException("'IsServices' attribute has to be put in 'ClockDomain' configuration, set as 'true' for CD as service, or 'false' for CD as a normal software behavior");
        } else {
            isServices = Boolean.parseBoolean(cd.getAttributeValue("IsServices"));
        }
        
        if (isServices && cd.getChild("Services")==null){
            throw new RuntimeException("CDLCMapParser: XML config require <Services> tag for CD that provide services");
        } else if (isServices && cd.getChild("Services")!=null){
            List <Element> sds = cd.getChild("Services").getChildren();
            */
            
            //for (Element sd : sds){  

            //System.out.println("port value: " +port.);
                
                
                localServAttr = new JSONObject();
               
                
             //   if (sd.getName().equals("serviceDescription")){

               // }
                

                if (cd.getName().equals("serviceDescription")){
                    

                    //System.out.println("ServDesc exist");

                     //System.out.println("Service Description exist");
                   //  if(sd.getAttributeValue("serviceName") == null || sd.getAttributeValue("serviceType") == null || sd.getAttributeValue("nodeAddress")==null)
                   //  throw new RuntimeException("'ServiceDescription' require 'ServiceName', 'ServiceType', 'IsServices', and 'NodeIPAddress' attributes, NodeIPAddress' being the node IPv4 address");



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                     List<Attribute> SOAAttributes = cd.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}
                     
                     

                     for (Attribute SOAAttribute : SOAAttributes){
                         
                         
                         
                       try {
                            localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                            
                            //localServAttr.clear();
                                        //config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                           } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                     
                 //    if (!localServAttr.has("relatedSSName")){
                         
                 //        throw new RuntimeException("'relatedSSName' attribute needs to be included");
                         
                 //    }
                                
                    try {
                        //if (localServAttr.getString("serviceRole").equalsIgnoreCase("provider")){
                           
                           // System.out.println("Physical desc: "+sd.getChild("physicalDescription"));
                            
                            
                           if (cd.getChild("physicalDescription")!=null){
                               
                                Element sdDevDescParam = cd.getChild("physicalDescription");
                                
                                phyServDescrParamInd=new JSONObject();
                                
                                
                                    
                                    
                                    List <Element> sdDevDescIndivParams = sdDevDescParam.getChildren("parameter");
                                    
                                    int y=1;
                                    
                                    for (Element sdDevIndivParam : sdDevDescIndivParams){
                                        
                                        List <Attribute> devDescrAttrs = sdDevIndivParam.getAttributes();
                                        
                                            //System.out.println("parameters attrib: " +sdDevDescParams);
                                            
                                            phyServDescrParam=new JSONObject();
                                        
                                           // System.out.println("parameters attrib: " +devDescrAttrs);
                                            
                                            for (Attribute devDescrAttr : devDescrAttrs){
                                             
                                                try {
                                                    phyServDescrParam.put(devDescrAttr.getName(), devDescrAttr.getValue());
                                                                             
                                                } catch (JSONException ex) {
                                                    System.out.println("CDLCMapParser, cannot put phyServDescrParam attribute" +ex.getMessage());
                                                }
                                            
                                            }
                                        phyServDescrParamInd.put("parameter"+y, phyServDescrParam);
                                        y++;
                                        
                                      //} 
                                        
                                   // }
                                    
                                        
                                    }
                                    localServAttr.put("physicalDescription",phyServDescrParamInd);
                                   // if (sdDevDescParam.getName().equalsIgnoreCase("physicalDescription")){
                                        
                                        
                                    
                                       // System.out.println("parameters: " +sdDevDescParams);
                                        
                                       // for (Element devDescrVar : devDescrVars){
                                        
                                            
                               
                           }
                           
                            List <Element> sdDetAllActs = cd.getChildren("action");
                            //System.out.println("CDLCMapParser, action tot : " +sdDetAllActs+ "total: " +sdDetAllActs.size());
                            
                            int actionInt = 0;
                            
                            for (Element sdDetAllAct : sdDetAllActs){
                                
                                //System.out.println("action exist: " +sdDetAllAct.getName());
                                //List <Element> sdDetActs = sd.getChild("action").getChildren();
                                
                                List <Element> sdDetActs = sdDetAllAct.getChildren("actionAttributes");
                            
                                        for (Element sdDetAct : sdDetActs){
                                            
                                            if (sdDetAct.getName().equalsIgnoreCase("actionAttributes")){
                                         
                                                //List <Element> sdActAttrs = sd.getChildren("actionAttributes");
                                                
                                               // for (Element sdActAttr : sdActAttrs){
                                                    
                                                    
                                                    
                                                   // if (sdActAttr.getName().equalsIgnoreCase("actionAttributes")){
                                                        
                                                        
                                        
                                                   //     for (Element sdDetAct : sdDetActs){

                                                          //  if (sdDetAct.getName().equalsIgnoreCase("action")){
                                                
                                                
                                                                actionServ = new JSONObject();
                                                               
                                                
                                                
                                                //action Parameters
                                                
                                                                List <Element> sdDetActsParams = sdDetAct.getChildren("actionParameters");
                                                
                                                                for (Element sdDetActsParam : sdDetActsParams){
                                                
                                                                    stateVarServTot = new JSONObject();
                                                                    
                                                                    if (sdDetActsParam.getName().equalsIgnoreCase("actionParameters")){
                                        
                                                                    List <Element> stateVars = sdDetActsParam.getChildren("parameter");

                                                                    int stateVarInt = 1;
                                                                    for (Element stateVar: stateVars)
                                                                    {
                                                                        stateVarServ = new JSONObject();
                                                              //mp.println("JSONObject stateVarServ = new JSONObject();\n");
                                                                        
                                                                        List <Attribute> stateVarAttrs = stateVar.getAttributes();

                                                                        for (Attribute stateVarAttr : stateVarAttrs){
                                                                            try {
                                                                             stateVarServ.put(stateVarAttr.getName(), stateVarAttr.getValue());
                                                                            
                                                                             } catch (JSONException ex) {
                                                                               System.out.println("CDLCMapParser, cannot put stateVariable attribute" +ex.getMessage());
                                                                           }

                                                                  }

                                                                 
                                                                   try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       stateVarServTot.put("parameter"+stateVarInt,stateVarServ);
                                                                       //String s = "parameter"+stateVarInt;
                                                                       //mp.println("stateVarServTot.put(\""+s+"\",stateVarServ);\n");
                                                                      
                                                                      // localServAttr.put("stateVariable",stateVarServTot);
                                                                       
                                                                   } catch (JSONException ex) {
                                                                       System.out.println("CDLCMapParser, cannot put parameters" +ex.getMessage());
                                                                   }
                                                            stateVarInt++;

                                                           }
                                                        
                                                         try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       //stateVarServTot.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       
                                                                       actionServ.put("actionParameters",stateVarServTot);
                                                                      
                                                                       
                                                             } catch (JSONException ex) {
                                                                   System.out.println("CDLCMapParser, cannot put stateVariableTot" +ex.getMessage());
                                                             }
                                                        
                                                        
                                                    }
                                                }
                                                
                                                //action Parameters END
                                                                
                                                                
                                                                //action interfaces
                                                                
                                                                List <Element> sdDetActsIntfs = sdDetAct.getChildren("actionInterfaces");
                                                                
                                                                        for (Element sdDetActsIntf : sdDetActsIntfs){

                                                                            actionIntfTot = new JSONObject();

                                                                            if (sdDetActsIntf.getName().equalsIgnoreCase("actionInterfaces")){

                                                                            List <Element> actIntfVars = sdDetActsIntf.getChildren("interface");

                                                                            int actIntfInt = 1;
                                                                            for (Element actIntf: actIntfVars)
                                                                            {
                                                                                //stateVarServ = new JSONObject();
                                                                                actionIntf = new JSONObject();
                                                                      //mp.println("JSONObject stateVarServ = new JSONObject();\n");
                                                                                //mp.println("actionIntf = new JSONObject();\n");
                                                                                List <Attribute> actIntfAttrs = actIntf.getAttributes();

                                                                                for (Attribute actIntfAttr : actIntfAttrs){
                                                                                    try {
                                                                                     actionIntf.put(actIntfAttr.getName(), actIntfAttr.getValue());
                                                                                     //mp.println("stateVarServ.put(\""+actIntfAttr.getName()+"\",\""+actIntfAttr.getValue()+"\");\n");
                                                                                     } catch (JSONException ex) {
                                                                                       System.out.println("JDOMParser, cannot put stateVariable attribute" +ex.getMessage());
                                                                                   }

                                                                          }


                                                                           try {
                                                                               //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                               actionIntfTot.put("interface"+actIntfInt,actionIntf);
                                                                               //String s = "parameter"+stateVarInt;
                                                                               //mp.println("stateVarServTot.put(\""+s+"\",stateVarServ);\n");
                                                                               //mp.println("stateVarServTot.put(\"+parameter"+actIntfInt+"\",stateVarServ);\n");
                                                                              // localServAttr.put("stateVariable",stateVarServTot);

                                                                           } catch (JSONException ex) {
                                                                               System.out.println("JDOMParser, cannot put parameters" +ex.getMessage());
                                                                           }
                                                                    actIntfInt++;

                                                                   }

                                                                 try {
                                                                               //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                               //stateVarServTot.put("stateVariable"+stateVarInt,stateVarServ);

                                                                               actionServ.put("actionInterfaces",actionIntfTot);
                                                                               //mp.println("actionServ.put(\"actionParameters"+actIntfInt+"\",stateVarServTot);\n");

                                                                     } catch (JSONException ex) {
                                                                           System.out.println("JDOMParser, cannot put stateVariableTot" +ex.getMessage());
                                                                     }


                                                            }
                                                        }
                                                                
                                                                // action interfaces End
                                                
                                                        List <Attribute> actAttrs = sdDetAct.getAttributes();

                                                       for (Attribute actAttr : actAttrs){
                                                                 try {
                                                                    actionServ.put(actAttr.getName(), actAttr.getValue());
                                                                    
                                                                } catch (JSONException ex) {
                                                                 ex.printStackTrace();
                                                             }

                                                            }

                                                             

                                                        //}
                                            
                                                        // }
                                                        
                                                 //   }
                                                    
                                               // }
                                             
                                                       
                                            
                                            
                                            
                                        }
                                
                                            try  {
                                                     
                                                                //   localServAttr.put("action"+actionInt,actionServ);
                                                                actionServTot.put("action"+actionInt,actionServ);
                                                                //localServAttr.put("action",actionServTot);

                                                               
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                            }
                                                        actionInt++;
                                                       
                                                    }
                                
                            }
                            
                            
                            
                            localServAttr.put("action",actionServTot);
                           
                                        
                            //SOABuffer.setServiceOccupancy(localServAttr.getString("serviceName"),"ready");
                            
                        //}
                        
                               //    if (IPChecker==false){
                               //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                                //   }
                    } catch (JSONException ex) {
                       ex.printStackTrace();
                    }

                                        //action parsing , need to be xpanded for mulitple actions

                                        //List <Element> sdDetActs = sd.getChildren("action");
                                        
                                                       try{
                                                           
                                                           if (!localServAttr.isEmpty()){
                                                             //  if (localServAttr.has("serviceVisibility")){
                                                                    //SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), localServAttr.getString("serviceVisibility"));
                                                                    SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), "visible");
                                                                    //localServAttr.remove("serviceVisibility");
                                                                    
                                                              // }
                                                           }
                                                           
                                                       } catch (JSONException jex){
                                                           System.out.println("Jdomparser, parseClockDomain: "+jex.getCause());
                                                       }
                                            
                                            
                                                    try {
                                                        if (!localServAttr.isEmpty()){
                                                            
                                                            //int localServAmount = SJServiceRegistry.GetLocalInternalServicesAmount()+1;
                                                            
                                                            localServAttrPrnt.put(localServAttr.getString("serviceName"),localServAttr);
                                                           
                                                            //localServAmount++;
                                                        }
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }
               
                        }

                
            //}
            return localServAttrPrnt;
            
        }

public JSONObject parseSubSystem(Element subsystem, String CDName, String ServName){
        List<Element> cds = subsystem.getChildren("serviceDescription");
        //List<Element> schedulers = subsystem.getChildren("Scheduler");
        //List<Element> rmi = subsystem.getChildren("RMI");

        //JSONObject jsSSCDTot = new JSONObject();
        //Udayanto modification  // parse Subsystem to obtain all available services in the node-self
      //  Element devDescSS = subsystem.getChild("deviceDescription");
    //    List<Element> devDescSSLists = devDescSS.getAttributes();

     //   for (Element devDescSSList : devDescSSLists)
    //    {
    //        if(devDescSSList.getAttributeValue("deviceType") == null || devDescSSList.getAttributeValue("nodeIPAddress")==null)
//				throw new RuntimeException("'deviceDescription' require 'deviceType' and 'NodeIPAddress' attributes, NodeIPAddress' being the node IPv4 address");
   //     }
         int internalServiceIndex = 0;

        //Udayanto modification END

        //System.out.println("Local SubSystem : "+subsystem.getAttributeValue("Name"));
       
        
        //SSName = subsystem.getAttributeValue("Name");
        
        Hashtable clockdomains = new Hashtable();

        // First pass to create all the local subsystem's clock-domains
       

        //System.out.println("Local cds : "+clockdomains);
        
        
        //CDObjectsBuffer.CopySCCDInstancesToMap(clockdomains);
        
        Hashtable channels = new Hashtable();
        
        
         // Checking whether RMI port num has been defined
         
         
        JSONObject jsCDtot = new JSONObject();
        
        // This goes to default scheduler
        
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
        
        //System.out.println("CD ss =" +subsystem);
                    
                    JSONObject jsCDMap = parseClockDomain(cd,channels, clockdomains, subsystem.getAttributeValue("Class"),CDName,ServName);
                    
                        Enumeration enumParsedCD = jsCDMap.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsCDMap.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                        
                        // need to handle device description

                                try {

                                //if (!localServAttr.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    //if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    
                                    
                                    
                                    if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {
                                        internalServiceIndex++; 
                                        
                                        Enumeration ServAttrEnum = localServAttrPrnt.keys();
                                        
                                        while (ServAttrEnum.hasMoreElements()){
                                            Object key = ServAttrEnum.nextElement(); 
                                            localServList.put(key.toString(), localServAttrPrnt.get(key.toString()));
                                            //String k = key.toString();
                                            
                                        }
                                        
                                        //localServList.put("service"+internalServiceIndex, localServAttr);
                                        //localServList=localServAttrPrnt;
                                    }
                   
                                    
                                   // System.out.println("CDLCMapParser, localServAttrPrnt:" +localServAttrPrnt.toPrettyPrintedString(2, 0));
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        
                        
               //     }

                        //  Udayanto modification

                }
             
       
        return localServList;
        
}


/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
 * @param cd
 * @param subsystem
 * @return ClockDomain instance initialized from this method call
 */
public JSONObject parseClockDomain(Element cd, Hashtable channels, Hashtable clockdomains, String CDClassName, String CDName, String ServName){

    
    
        //clear the attribute storage
        boolean servChecker = false;
    //Udayanto modif
        localServAttr = new JSONObject();
        
        localServAttrPrnt = new JSONObject();
        
        //argumentServ = new JSONObject();
        actionServ = new JSONObject();
        actionServTot = new JSONObject();
        stateVarServ = new JSONObject();
        stateVarServTot = new JSONObject();

        //allowedValRangeServ = new JSONObject();
        //allowedValListServ = new JSONObject();

        /*
        boolean isServices = false;
        String cdname = cd.getAttributeValue("Name");
        if(cd.getAttributeValue("IsServices") == null){
           throw new RuntimeException("'IsServices' attribute has to be put in 'ClockDomain' configuration, set as 'true' for CD as service, or 'false' for CD as a normal software behavior");
        } else {
            isServices = Boolean.parseBoolean(cd.getAttributeValue("IsServices"));
        }
        
        if (isServices && cd.getChild("Services")==null){
            throw new RuntimeException("CDLCMapParser: XML config require <Services> tag for CD that provide services");
        } else if (isServices && cd.getChild("Services")!=null){
            List <Element> sds = cd.getChild("Services").getChildren();
            */
            
            //for (Element sd : sds){  

            //System.out.println("port value: " +port.);
                
                
                localServAttr = new JSONObject();
               
                
             //   if (sd.getName().equals("serviceDescription")){

               // }
                

                if (cd.getName().equals("serviceDescription")){
                    

                    //System.out.println("ServDesc exist");

                     //System.out.println("Service Description exist");
                   //  if(sd.getAttributeValue("serviceName") == null || sd.getAttributeValue("serviceType") == null || sd.getAttributeValue("nodeAddress")==null)
                   //  throw new RuntimeException("'ServiceDescription' require 'ServiceName', 'ServiceType', 'IsServices', and 'NodeIPAddress' attributes, NodeIPAddress' being the node IPv4 address");



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                     List<Attribute> SOAAttributes = cd.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}
                     
                     

                     for (Attribute SOAAttribute : SOAAttributes){
                         
                         
                         
                       try {
                            localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                            
                            //localServAttr.clear();
                                        //config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                           } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                     
                 //    if (!localServAttr.has("relatedSSName")){
                         
                 //        throw new RuntimeException("'relatedSSName' attribute needs to be included");
                         
                 //    }
                                
                    try {
                       // if (localServAttr.getString("serviceRole").equalsIgnoreCase("provider")){
                           
                           // System.out.println("Physical desc: "+sd.getChild("physicalDescription"));
                            
                            
                           if (cd.getChild("physicalDescription")!=null){
                               
                                Element sdDevDescParam = cd.getChild("physicalDescription");
                                
                                phyServDescrParamInd=new JSONObject();
                                
                                
                                    
                                    
                                    List <Element> sdDevDescIndivParams = sdDevDescParam.getChildren("parameter");
                                    
                                    int y=1;
                                    
                                    for (Element sdDevIndivParam : sdDevDescIndivParams){
                                        
                                        List <Attribute> devDescrAttrs = sdDevIndivParam.getAttributes();
                                        
                                            //System.out.println("parameters attrib: " +sdDevDescParams);
                                            
                                            phyServDescrParam=new JSONObject();
                                        
                                           // System.out.println("parameters attrib: " +devDescrAttrs);
                                            
                                            for (Attribute devDescrAttr : devDescrAttrs){
                                             
                                                try {
                                                    phyServDescrParam.put(devDescrAttr.getName(), devDescrAttr.getValue());
                                                                             
                                                } catch (JSONException ex) {
                                                    System.out.println("CDLCMapParser, cannot put phyServDescrParam attribute" +ex.getMessage());
                                                }
                                            
                                            }
                                        phyServDescrParamInd.put("parameter"+y, phyServDescrParam);
                                        y++;
                                        
                                      //} 
                                        
                                   // }
                                    
                                        
                                    }
                                    localServAttr.put("physicalDescription",phyServDescrParamInd);
                                   // if (sdDevDescParam.getName().equalsIgnoreCase("physicalDescription")){
                                        
                                        
                                    
                                       // System.out.println("parameters: " +sdDevDescParams);
                                        
                                       // for (Element devDescrVar : devDescrVars){
                                        
                                            
                               
                           }
                           
                            List <Element> sdDetAllActs = cd.getChildren("action");
                            //System.out.println("CDLCMapParser, action tot : " +sdDetAllActs+ "total: " +sdDetAllActs.size());
                            
                            int actionInt = 0;
                            
                            for (Element sdDetAllAct : sdDetAllActs){
                                
                                //System.out.println("action exist: " +sdDetAllAct.getName());
                                //List <Element> sdDetActs = sd.getChild("action").getChildren();
                                
                                List <Element> sdDetActs = sdDetAllAct.getChildren("actionAttributes");
                            
                                        for (Element sdDetAct : sdDetActs){
                                            
                                            if (sdDetAct.getName().equalsIgnoreCase("actionAttributes")){
                                         
                                                //List <Element> sdActAttrs = sd.getChildren("actionAttributes");
                                                
                                               // for (Element sdActAttr : sdActAttrs){
                                                    
                                                    
                                                    
                                                   // if (sdActAttr.getName().equalsIgnoreCase("actionAttributes")){
                                                        
                                                        
                                        
                                                   //     for (Element sdDetAct : sdDetActs){

                                                          //  if (sdDetAct.getName().equalsIgnoreCase("action")){
                                                
                                                
                                                                actionServ = new JSONObject();
                                                               
                                                
                                                
                                                //action Parameters
                                                
                                                                List <Element> sdDetActsParams = sdDetAct.getChildren("actionParameters");
                                                
                                                                for (Element sdDetActsParam : sdDetActsParams){
                                                
                                                                    stateVarServTot = new JSONObject();
                                                                    
                                                                    if (sdDetActsParam.getName().equalsIgnoreCase("actionParameters")){
                                        
                                                                    List <Element> stateVars = sdDetActsParam.getChildren("parameter");

                                                                    int stateVarInt = 1;
                                                                    for (Element stateVar: stateVars)
                                                                    {
                                                                        stateVarServ = new JSONObject();
                                                              //mp.println("JSONObject stateVarServ = new JSONObject();\n");
                                                                        
                                                                        List <Attribute> stateVarAttrs = stateVar.getAttributes();

                                                                        for (Attribute stateVarAttr : stateVarAttrs){
                                                                            try {
                                                                             stateVarServ.put(stateVarAttr.getName(), stateVarAttr.getValue());
                                                                            
                                                                             } catch (JSONException ex) {
                                                                               System.out.println("CDLCMapParser, cannot put stateVariable attribute" +ex.getMessage());
                                                                           }

                                                                  }

                                                                 
                                                                   try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       stateVarServTot.put("parameter"+stateVarInt,stateVarServ);
                                                                       //String s = "parameter"+stateVarInt;
                                                                       //mp.println("stateVarServTot.put(\""+s+"\",stateVarServ);\n");
                                                                      
                                                                      // localServAttr.put("stateVariable",stateVarServTot);
                                                                       
                                                                   } catch (JSONException ex) {
                                                                       System.out.println("CDLCMapParser, cannot put parameters" +ex.getMessage());
                                                                   }
                                                            stateVarInt++;

                                                           }
                                                        
                                                         try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       //stateVarServTot.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       
                                                                       actionServ.put("actionParameters",stateVarServTot);
                                                                      
                                                                       
                                                             } catch (JSONException ex) {
                                                                   System.out.println("CDLCMapParser, cannot put stateVariableTot" +ex.getMessage());
                                                             }
                                                        
                                                        
                                                    }
                                                }
                                                
                                                //action Parameters END
                                                
                                                        List <Attribute> actAttrs = sdDetAct.getAttributes();

                                                       for (Attribute actAttr : actAttrs){
                                                                 try {
                                                                    actionServ.put(actAttr.getName(), actAttr.getValue());
                                                                    
                                                                } catch (JSONException ex) {
                                                                 ex.printStackTrace();
                                                             }

                                                            }

                                                             

                                                        //}
                                            
                                                        // }
                                                        
                                                 //   }
                                                    
                                               // }
                                             
                                                       
                                            
                                            
                                            
                                        }
                                
                                            try  {
                                                     
                                                                //   localServAttr.put("action"+actionInt,actionServ);
                                                                actionServTot.put("action"+actionInt,actionServ);
                                                                //localServAttr.put("action",actionServTot);

                                                               
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                            }
                                                        actionInt++;
                                                       
                                                    }
                                
                            }
                            
                            
                            
                            localServAttr.put("action",actionServTot);
                           
                                        
                            //SOABuffer.setServiceOccupancy(localServAttr.getString("serviceName"),"ready");
                            
                       // }
                        
                               //    if (IPChecker==false){
                               //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                                //   }
                    } catch (JSONException ex) {
                       ex.printStackTrace();
                    }

                                        //action parsing , need to be xpanded for mulitple actions

                                        //List <Element> sdDetActs = sd.getChildren("action");
                                        
                                                       try{
                                                           
                                                           if (!localServAttr.isEmpty()){
                                                             //  if (localServAttr.has("serviceVisibility")){
                                                                    //SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), localServAttr.getString("serviceVisibility"));
                                                                    SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), "visible");
                                                                    //localServAttr.remove("serviceVisibility");
                                                                    
                                                              // }
                                                           }
                                                           
                                                       } catch (JSONException jex){
                                                           System.out.println("Jdomparser, parseClockDomain: "+jex.getCause());
                                                       }
                                            
                                            
                                                    try {
                                                        if (!localServAttr.isEmpty()){
                                                            
                                                            localServAttr.put("associatedCDName", CDName);
                                                            //int localServAmount = SJServiceRegistry.GetLocalInternalServicesAmount()+1;
                                                            
                                                            localServAttrPrnt.put(ServName,localServAttr);
                                                           
                                                            //localServAmount++;
                                                        }
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }
               
                        }

                
            //}
            return localServAttrPrnt;
            
        }
 
        
}