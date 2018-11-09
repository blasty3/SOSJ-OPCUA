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
import systemj.common.IMBuffer;
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



public class CDLCMapParser{


private String fnameAll;

//private SystemJProgram program;
private InputStream is;
//private MainPrinter mp = new MainPrinter();
private int gid = 0;

private int intServiceIndex=1;

//Udayanto modification
JSONObject jsParsedCD = new JSONObject();
JSONObject jsParsedSS = new JSONObject();


//JSONObject jsNonLocalSSName = new JSONObject();

JSONObject jsAllSSName = new JSONObject();

/*
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
*/
//SJServiceRegistry currServReg = new SJServiceRegistry();


//Udayanto modification

public JSONObject parse(String file) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
       // if(file != null){
                File f = new File(file);
                doc = builder.build(f);
        ////}
        //else if(is != null)
        //        doc = builder.build(is);
        //else
        //        throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        //if(!e.getName().equals("System"))
        //        throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        
                }
        }

        
        parseInterconnection(e);
        
        JSONObject jsCDMap = parseSubSystem(e);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
       
        //mp.flushSDSPOT();
        return jsCDMap;
        //SJServiceRegistry.setParsingStatus(true);
       
}

public JSONObject parse(String CDName, String ClassName, String file) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
       // if(file != null){
                File f = new File(file);
                doc = builder.build(f);
        ////}
        //else if(is != null)
        //        doc = builder.build(is);
        //else
        //        throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        //if(!e.getName().equals("System"))
        //        throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        
                }
        }

        
        parseInterconnection(e);
        
        JSONObject jsCDMap = parseSubSystem(CDName,ClassName, e);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
       
        //mp.flushSDSPOT();
        return jsCDMap;
        //SJServiceRegistry.setParsingStatus(true);
       
}

public JSONObject parse(String CDName, String file) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
       // if(file != null){
                File f = new File(file);
                doc = builder.build(f);
        ////}
        //else if(is != null)
        //        doc = builder.build(is);
        //else
        //        throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        //if(!e.getName().equals("System"))
        //        throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        
                }
        }

        
        parseInterconnection(e);
        
        JSONObject jsCDMap = parseSubSystem(CDName,e);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
       
        //mp.flushSDSPOT();
        return jsCDMap;
        //SJServiceRegistry.setParsingStatus(true);
       
}

public JSONObject parseSubSystem(String CDName, String ClassName,Element subsystem){
        List<Element> cds = subsystem.getChildren("ClockDomain");
        
        //System.out.println(cds);
                //List<Element> cds = subsystem.getChildren("ClockDomain");
        //List<Element> schedulers = subsystem.getChildren("Scheduler");
        List<Element> rmi = subsystem.getChildren("RMI");

        JSONObject jsSSCDTot = new JSONObject();
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
         if(rmi.size() > 0 ){
            Element rmir = rmi.get(0);
            try {
                systemj.signals.network.RMIReceiver.setRegistry(Integer.parseInt(rmir.getAttributeValue("Port")));
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.exit(1);
            }
          }
         
        JSONObject jsCDtot = new JSONObject();
        
        // This goes to default scheduler
        
                
                
                
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
                    
                    jsCDtot = parseClockDomain(CDName, ClassName,cd,channels, clockdomains);
                    
                    /*
                        Enumeration enumParsedCD = jsCDMap.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsCDMap.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                      */  
                        // need to handle device description

                                
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        
                        
               //     }

                        //  Udayanto modification

                }
                
                
        

    //try {
        //jsSSCDTot.put(subsystem.getAttributeValue("Name"), jsCDtot);
        
        //System.out.println("CDLCMapParser One SS total: " +jsSSCDTot.toPrettyPrintedString(3, 1));
        
        //jsParsedSS = jsSSCDTot;
        //jsParsedSS = jsCDtot;
    //} catch (JSONException ex) {
        //ex.printStackTrace();
    //}
       
        return jsCDtot;
        
}

public JSONObject parseSubSystem(String CDName, Element subsystem){
        List<Element> cds = subsystem.getChildren("ClockDomain");
        
        //System.out.println(cds);
                //List<Element> cds = subsystem.getChildren("ClockDomain");
        //List<Element> schedulers = subsystem.getChildren("Scheduler");
        List<Element> rmi = subsystem.getChildren("RMI");

        JSONObject jsSSCDTot = new JSONObject();
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
         if(rmi.size() > 0 ){
            Element rmir = rmi.get(0);
            try {
                systemj.signals.network.RMIReceiver.setRegistry(Integer.parseInt(rmir.getAttributeValue("Port")));
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.exit(1);
            }
          }
         
        JSONObject jsCDtot = new JSONObject();
        
        // This goes to default scheduler
        
                
                
                
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
                    
                    jsCDtot = parseClockDomain(CDName, cd,channels, clockdomains);
                    
                    /*
                        Enumeration enumParsedCD = jsCDMap.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsCDMap.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                      */  
                        // need to handle device description

                                
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        
                        
               //     }

                        //  Udayanto modification

                }
                
                
        

    //try {
        //jsSSCDTot.put(subsystem.getAttributeValue("Name"), jsCDtot);
        
        //System.out.println("CDLCMapParser One SS total: " +jsSSCDTot.toPrettyPrintedString(3, 1));
        
        //jsParsedSS = jsSSCDTot;
        //jsParsedSS = jsCDtot;
    //} catch (JSONException ex) {
        //ex.printStackTrace();
    //}
       
        return jsCDtot;
        
}

public JSONObject parseSubSystem(Element subsystem){
        List<Element> cds = subsystem.getChildren("ClockDomain");
        
        //System.out.println(cds);
                //List<Element> cds = subsystem.getChildren("ClockDomain");
        //List<Element> schedulers = subsystem.getChildren("Scheduler");
        List<Element> rmi = subsystem.getChildren("RMI");

        JSONObject jsSSCDTot = new JSONObject();
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
         if(rmi.size() > 0 ){
            Element rmir = rmi.get(0);
            try {
                systemj.signals.network.RMIReceiver.setRegistry(Integer.parseInt(rmir.getAttributeValue("Port")));
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.exit(1);
            }
          }
         
        JSONObject jsCDtot = new JSONObject();
        
        // This goes to default scheduler
        
                
                
                
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
                    
                    jsCDtot = parseClockDomain(cd,channels, clockdomains);
                    
                    /*
                        Enumeration enumParsedCD = jsCDMap.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsCDMap.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                      */  
                        // need to handle device description

                                
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        
                        
               //     }

                        //  Udayanto modification

                }
                
                
        

    //try {
        //jsSSCDTot.put(subsystem.getAttributeValue("Name"), jsCDtot);
        
        //System.out.println("CDLCMapParser One SS total: " +jsSSCDTot.toPrettyPrintedString(3, 1));
        
        //jsParsedSS = jsSSCDTot;
        //jsParsedSS = jsCDtot;
    //} catch (JSONException ex) {
        //ex.printStackTrace();
    //}
       
        return jsCDtot;
        
}

public void parseInterconnection(Element el){
        List<Element> p = el.getChildren("Interconnection");
        
        if(p.size() > 0){
            
            Element q = p.get(0);
        List<Element> l = q.getChildren("Link");
        Interconnection ic = IMBuffer.getInterfaceManagerConfig().getInterconnection();
        //Interconnection ic = (Interconnection)SJSSCDSignalChannelMap.getInterConnection();
        

        for(Element link : l){
                Interconnection.Link linko = new Interconnection.Link();
                
                List<Element> intfs = link.getChildren("Interface");
               // Vector SSNameColl = new Vector();
                for(Element intf : intfs){
                    
                        String SS = intf.getAttributeValue("SubSystem");
                        String clazz = intf.getAttributeValue("Class");
                        String interf = intf.getAttributeValue("Interface");
                        String args = intf.getAttributeValue("Args");
                        
                        if(SS == null || clazz == null || intf == null || args == null)
                                throw new RuntimeException("Interface should have the following elements : SubSystem, Class, Interface, Args");

                        try {
                                
                                        GenericInterface gct = (GenericInterface)Class.forName(clazz).newInstance();
                                        Hashtable ht = new Hashtable();
                                        ht.put("Class", clazz);
                                        ht.put("Interface", interf);
                                        ht.put("Args", args);
                                        ht.put("SubSystem", SS);
                                        gct.configure(ht);
                                        linko.addInterface(SS, gct);
                                       // SSNameColl.addElement(SS);
                                        SJSSCDSignalChannelMap.AddGenericInterfaceDet(SS, clazz, interf, args, link.getAttributeValue("Type"));
                                        //SJSSCDSignalChannelMap.addInterconnectionLink(SS,(Object) linko);   //link of a specific SS
                                

                                String htname = "ht"+(gid++);
                                String gctname = "gct"+(gid++);
                               
                        } catch(Exception e){
                                e.printStackTrace();
                                System.exit(1);
                        }
                }
                
                
                
                String type = link.getAttributeValue("Type") ;
                if(type == null)
                        throw new RuntimeException("Link 'Type' Missing");
                else if(type.equals("Local")){
                        ic.addLink(linko, true);
                        
                        //SJSSCDSignalChannelMap.addLinkType((Object)linko,type); //shows the link type of a specific link
                        
                }
                else if(type.equals("Destination")){
                        ic.addLink(linko, false);
                        //SJSSCDSignalChannelMap.addLinkType((Object)linko,type);
                        
                }
                else
                        throw new RuntimeException("Unrecognized Link Type : "+type);
        }
        
        //SJSSCDSignalChannelMap.saveInterConnection(ic);
        
        InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
        
        im.setInterconnection(ic);
       
        IMBuffer.SaveInterfaceManagerConfig(im);

        ic.printInterconnection(); // Debug
            
        }
        

}


/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
     * @param CDName
 * @param cd
 * @param subsystem
 * @param ClassName
 * @return ClockDomain instance initialized from this method call
 */
public JSONObject parseClockDomain(String CDName, String ClassName, Element cd, Hashtable channels, Hashtable clockdomains){

    JSONObject jsSigInOut = new JSONObject();

    JSONObject jsSChanInOut = new JSONObject();
    JSONObject jsSigsChans = new JSONObject();

    JSONObject jsAChanInOut = new JSONObject();
    //JSONObject jsAChans = new JSONObject();
    
    JSONObject jsCDSigsChans = new JSONObject();
    
        //clear the attribute storage
        boolean servChecker = false;
    //Udayanto modif
        //localServAttr = new JSONObject();
        
        //localServAttrPrnt = new JSONObject();
        
        //argumentServ = new JSONObject();
        //actionServ = new JSONObject();
        //actionServTot = new JSONObject();
        //stateVarServ = new JSONObject();
        //stateVarServTot = new JSONObject();

        //allowedValRangeServ = new JSONObject();
        //allowedValListServ = new JSONObject();

        //boolean isServices = false;
        //String cdname = cd.getAttributeValue("Name");
        
        //String cdname = CDName;
        
        String CDClassName = ClassName;
        //Element signalChannel = cd.getChild("SignalChannel");
        List <Element> ports = cd.getChild("SignalChannel").getChildren();

        
        if (ports.isEmpty() || ports==null){
            throw new RuntimeException("'SignalChannel' attribute doesn't exist in CD: " + cd.getName()+ "please check your xml file");
        }
        //System.out.println("ports total:" +ports.size());
        


    // end Udayanto modif

        

        //List<Element> ports = cd.getChildren();

        // Udayanto modification --> save service description and to make the information easily accessible by other classes
        // obtain all available service in a subsystem\program (internal on a node) and to make it available on a registry that other machines could get that information

    //    for (Element port : ports){  

            //System.out.println("port value: " +port.);

   //         if (port.getName().equals("serviceDescription")){
      //          servChecker=true;
     //       }
     //   }

        

        //end all

        JSONObject jsSigIn = new JSONObject();
                JSONObject jsSigOut = new JSONObject();
                JSONObject jsSChanInput = new JSONObject();
                JSONObject jsSChanOutput = new JSONObject();
                JSONObject jsAChanInput = new JSONObject();
                JSONObject jsAChanOutput = new JSONObject();
        
        for(Element port : ports){ //for each clock domain obtain signal/channel port config
                
                //System.out.println("ports:" +port.getName());
            
                if(port.getName().equals("iSignal") || port.getName().equals("oSignal")){
                        if(port.getAttributeValue("Class") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Interface signals must have both Name and Class attribute");
                }
                else if(port.getName().equals("iChannel") || port.getName().equals("iAChannel")){
                        if(port.getAttributeValue("From") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Input channels must have both Name and From attributes");
                }
                else if(port.getName().equals("oChannel") || port.getName().equals("oAChannel")){
                        if(port.getAttributeValue("To") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Output channels must have both Name and To attributes");
                }

                //.....///
                //Udayanto modification ,  start here to add SOA attributes


                //....///
                // End here


                GenericSignalReceiver server = null;
                GenericSignalSender client = null;


                String portname = port.getName();

                
                String dd = "gsr"+(gid++);
                String cn = "conf"+(gid++);
                try {
                        List<Attribute> attributes = port.getAttributes();

                        Hashtable config = new Hashtable();
                        JSONObject jsSigChan = new JSONObject();

                       
                        for(Attribute attribute : attributes){
                                config.put(attribute.getName(), attribute.getValue());
                                jsSigChan.put(attribute.getName(), attribute.getValue());
                                
                        }
                        
                        // Udayanto modification , get SOA attribute for all signals and channels



                //	if (port.getName().equals("serviceDescription")){



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                    //            List<Attribute> SOAAttributes = port.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}

                //		for (Attribute SOAAttribute : SOAAttributes){


                    //                    localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //localServAttr.clear();
                //			config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                        //		mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                        //	}

                            //    if (IPChecker==false){
                            //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                             //   }

                //	}

                        // end here

                        if(portname.equals("iSignal")){
                                
                                jsSigIn.put(port.getAttributeValue("Name"),jsSigChan);

                               //System.out.println("iSignal detected");
                                
                        }
                        else if(portname.equals("oSignal")){
                            
                                jsSigOut.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                
                        }

                        else if(portname.equals("iChannel")){
                            
                                jsSChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                

                        }
                        else if(portname.equals("oChannel")){
                            
                                jsSChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                        }
                        else if(portname.equals("iAChannel")|| portname.equals("oAChannel")){
                               
                                if(portname.equals("iAChannel")){
                                    
                                        
                                        jsAChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                                }
                                else
                                        jsAChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                                
                                
                                        
                                
                        }
                } catch(Exception e){
                        e.printStackTrace();
                        System.exit(1);
                }
        }
        
    try {
        jsSigInOut.put("inputs",jsSigIn);
        jsSigInOut.put("outputs",jsSigOut);
        //jsSigs.put("signals",jsSigInOut);
        jsSChanInOut.put("inputs",jsSChanInput);
        jsSChanInOut.put("outputs",jsSChanOutput);
        
        jsAChanInOut.put("inputs", jsAChanInput);
        jsAChanInOut.put("outputs",jsAChanOutput);
        //jsSChans.put("SChannels", jsSChanInOut);
        //jsAChans.put("AChannels",hfefe);
        
        jsSigsChans.put("signals",jsSigInOut);
        jsSigsChans.put("SChannels",jsSChanInOut);
        jsSigsChans.put("AChannels",jsAChanInOut);
        //jsSigsChans.put("CDSSLocation",ssname);
        jsSigsChans.put("CDClassName",CDClassName);
        //jsSigsChans.put("CDLifeStatus","Alive");
        
        //jsSigsChans.put("AChannels",jsSigInOut);
        
        jsCDSigsChans.put(CDName,jsSigsChans);
        
        

        
        
        //jsParsedCD = jsCDSigsChans;
    } catch (JSONException ex) {
        
        ex.printStackTrace();
    }
    
    //System.out.println("CDLCMapParser Mapped signals channels one CD:" +jsCDSigsChans);
        return jsCDSigsChans;
}

/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
     * @param CDName
 * @param cd
 * @param subsystem
 * @return ClockDomain instance initialized from this method call
 */
public JSONObject parseClockDomain(String CDName, Element cd, Hashtable channels, Hashtable clockdomains){

    JSONObject jsSigInOut = new JSONObject();

    JSONObject jsSChanInOut = new JSONObject();
    JSONObject jsSigsChans = new JSONObject();

    JSONObject jsAChanInOut = new JSONObject();
    //JSONObject jsAChans = new JSONObject();
    
    JSONObject jsCDSigsChans = new JSONObject();
    
        //clear the attribute storage
        boolean servChecker = false;
    //Udayanto modif
        //localServAttr = new JSONObject();
        
        //localServAttrPrnt = new JSONObject();
        
        //argumentServ = new JSONObject();
        //actionServ = new JSONObject();
        //actionServTot = new JSONObject();
        //stateVarServ = new JSONObject();
        //stateVarServTot = new JSONObject();

        //allowedValRangeServ = new JSONObject();
        //allowedValListServ = new JSONObject();

        //boolean isServices = false;
        //String cdname = cd.getAttributeValue("Name");
        
        //String cdname = CDName;
        
        String CDClassName = cd.getAttributeValue("Class");
        //Element signalChannel = cd.getChild("SignalChannel");
        List <Element> ports = cd.getChild("SignalChannel").getChildren();

        
        if (ports.isEmpty() || ports==null){
            throw new RuntimeException("'SignalChannel' attribute doesn't exist in CD: " + cd.getName()+ "please check your xml file");
        }
        //System.out.println("ports total:" +ports.size());
        


    // end Udayanto modif

        

        //List<Element> ports = cd.getChildren();

        // Udayanto modification --> save service description and to make the information easily accessible by other classes
        // obtain all available service in a subsystem\program (internal on a node) and to make it available on a registry that other machines could get that information

    //    for (Element port : ports){  

            //System.out.println("port value: " +port.);

   //         if (port.getName().equals("serviceDescription")){
      //          servChecker=true;
     //       }
     //   }

        

        //end all

        JSONObject jsSigIn = new JSONObject();
                JSONObject jsSigOut = new JSONObject();
                JSONObject jsSChanInput = new JSONObject();
                JSONObject jsSChanOutput = new JSONObject();
                JSONObject jsAChanInput = new JSONObject();
                JSONObject jsAChanOutput = new JSONObject();
        
        for(Element port : ports){ //for each clock domain obtain signal/channel port config
                
                //System.out.println("ports:" +port.getName());
            
                if(port.getName().equals("iSignal") || port.getName().equals("oSignal")){
                        if(port.getAttributeValue("Class") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Interface signals must have both Name and Class attribute");
                }
                else if(port.getName().equals("iChannel") || port.getName().equals("iAChannel")){
                        if(port.getAttributeValue("From") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Input channels must have both Name and From attributes");
                }
                else if(port.getName().equals("oChannel") || port.getName().equals("oAChannel")){
                        if(port.getAttributeValue("To") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Output channels must have both Name and To attributes");
                }

                //.....///
                //Udayanto modification ,  start here to add SOA attributes


                //....///
                // End here


                GenericSignalReceiver server = null;
                GenericSignalSender client = null;


                String portname = port.getName();

                
                String dd = "gsr"+(gid++);
                String cn = "conf"+(gid++);
                try {
                        List<Attribute> attributes = port.getAttributes();

                        Hashtable config = new Hashtable();
                        JSONObject jsSigChan = new JSONObject();

                       
                        for(Attribute attribute : attributes){
                                config.put(attribute.getName(), attribute.getValue());
                                jsSigChan.put(attribute.getName(), attribute.getValue());
                                
                        }
                        
                        // Udayanto modification , get SOA attribute for all signals and channels



                //	if (port.getName().equals("serviceDescription")){



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                    //            List<Attribute> SOAAttributes = port.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}

                //		for (Attribute SOAAttribute : SOAAttributes){


                    //                    localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //localServAttr.clear();
                //			config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                        //		mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                        //	}

                            //    if (IPChecker==false){
                            //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                             //   }

                //	}

                        // end here

                        if(portname.equals("iSignal")){
                                
                                jsSigIn.put(port.getAttributeValue("Name"),jsSigChan);

                               //System.out.println("iSignal detected");
                                
                        }
                        else if(portname.equals("oSignal")){
                            
                                jsSigOut.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                
                        }

                        else if(portname.equals("iChannel")){
                            
                                jsSChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                

                        }
                        else if(portname.equals("oChannel")){
                            
                                jsSChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                        }
                        else if(portname.equals("iAChannel")|| portname.equals("oAChannel")){
                               
                                if(portname.equals("iAChannel")){
                                    
                                        
                                        jsAChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                                }
                                else
                                        jsAChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                                
                                
                                        
                                
                        }
                } catch(Exception e){
                        e.printStackTrace();
                        System.exit(1);
                }
        }
        
    try {
        jsSigInOut.put("inputs",jsSigIn);
        jsSigInOut.put("outputs",jsSigOut);
        //jsSigs.put("signals",jsSigInOut);
        jsSChanInOut.put("inputs",jsSChanInput);
        jsSChanInOut.put("outputs",jsSChanOutput);
        
        jsAChanInOut.put("inputs", jsAChanInput);
        jsAChanInOut.put("outputs",jsAChanOutput);
        //jsSChans.put("SChannels", jsSChanInOut);
        //jsAChans.put("AChannels",hfefe);
        
        jsSigsChans.put("signals",jsSigInOut);
        jsSigsChans.put("SChannels",jsSChanInOut);
        jsSigsChans.put("AChannels",jsAChanInOut);
        //jsSigsChans.put("CDSSLocation",ssname);
        jsSigsChans.put("CDClassName",CDClassName);
        //jsSigsChans.put("CDLifeStatus","Alive");
        
        //jsSigsChans.put("AChannels",jsSigInOut);
        
        jsCDSigsChans.put(CDName,jsSigsChans);
        
        

        
        
        //jsParsedCD = jsCDSigsChans;
    } catch (JSONException ex) {
        
        ex.printStackTrace();
    }
    
    //System.out.println("CDLCMapParser Mapped signals channels one CD:" +jsCDSigsChans);
        return jsCDSigsChans;
}


/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
 * @param cd
 * @param subsystem
 * @return ClockDomain instance initialized from this method call
 */
public JSONObject parseClockDomain(Element cd, Hashtable channels, Hashtable clockdomains){

    JSONObject jsSigInOut = new JSONObject();

    JSONObject jsSChanInOut = new JSONObject();
    JSONObject jsSigsChans = new JSONObject();

    JSONObject jsAChanInOut = new JSONObject();
    //JSONObject jsAChans = new JSONObject();
    
    JSONObject jsCDSigsChans = new JSONObject();
    
        //clear the attribute storage
        boolean servChecker = false;
    //Udayanto modif
        //localServAttr = new JSONObject();
        
        //localServAttrPrnt = new JSONObject();
        
        //argumentServ = new JSONObject();
        //actionServ = new JSONObject();
        //actionServTot = new JSONObject();
        //stateVarServ = new JSONObject();
        //stateVarServTot = new JSONObject();

        //allowedValRangeServ = new JSONObject();
        //allowedValListServ = new JSONObject();

        //boolean isServices = false;
        String cdname = cd.getAttributeValue("Name");
        String CDClassName = cd.getAttributeValue("Class");
        //Element signalChannel = cd.getChild("SignalChannel");
        List <Element> ports = cd.getChild("SignalChannel").getChildren();

        
        if (ports.isEmpty() || ports==null){
            throw new RuntimeException("'SignalChannel' attribute doesn't exist in CD: " + cd.getName()+ "please check your xml file");
        }
        //System.out.println("ports total:" +ports.size());
        


    // end Udayanto modif

        

        //List<Element> ports = cd.getChildren();

        // Udayanto modification --> save service description and to make the information easily accessible by other classes
        // obtain all available service in a subsystem\program (internal on a node) and to make it available on a registry that other machines could get that information

    //    for (Element port : ports){  

            //System.out.println("port value: " +port.);

   //         if (port.getName().equals("serviceDescription")){
      //          servChecker=true;
     //       }
     //   }

        

        //end all

        JSONObject jsSigIn = new JSONObject();
                JSONObject jsSigOut = new JSONObject();
                JSONObject jsSChanInput = new JSONObject();
                JSONObject jsSChanOutput = new JSONObject();
                JSONObject jsAChanInput = new JSONObject();
                JSONObject jsAChanOutput = new JSONObject();
        
        for(Element port : ports){ //for each clock domain obtain signal/channel port config
                
                //System.out.println("ports:" +port.getName());
            
                if(port.getName().equals("iSignal") || port.getName().equals("oSignal")){
                        if(port.getAttributeValue("Class") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Interface signals must have both Name and Class attribute");
                }
                else if(port.getName().equals("iChannel") || port.getName().equals("iAChannel")){
                        if(port.getAttributeValue("From") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Input channels must have both Name and From attributes");
                }
                else if(port.getName().equals("oChannel") || port.getName().equals("oAChannel")){
                        if(port.getAttributeValue("To") == null || port.getAttributeValue("Name") == null)
                                throw new RuntimeException("Output channels must have both Name and To attributes");
                }

                //.....///
                //Udayanto modification ,  start here to add SOA attributes


                //....///
                // End here


                GenericSignalReceiver server = null;
                GenericSignalSender client = null;


                String portname = port.getName();

                
                String dd = "gsr"+(gid++);
                String cn = "conf"+(gid++);
                try {
                        List<Attribute> attributes = port.getAttributes();

                        Hashtable config = new Hashtable();
                        JSONObject jsSigChan = new JSONObject();

                       
                        for(Attribute attribute : attributes){
                                config.put(attribute.getName(), attribute.getValue());
                                jsSigChan.put(attribute.getName(), attribute.getValue());
                                
                        }
                        
                        // Udayanto modification , get SOA attribute for all signals and channels



                //	if (port.getName().equals("serviceDescription")){



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                    //            List<Attribute> SOAAttributes = port.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}

                //		for (Attribute SOAAttribute : SOAAttributes){


                    //                    localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //localServAttr.clear();
                //			config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                        //		mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                        //	}

                            //    if (IPChecker==false){
                            //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                             //   }

                //	}

                        // end here

                        if(portname.equals("iSignal")){
                                
                                jsSigIn.put(port.getAttributeValue("Name"),jsSigChan);

                               //System.out.println("iSignal detected");
                                
                        }
                        else if(portname.equals("oSignal")){
                            
                                jsSigOut.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                
                        }

                        else if(portname.equals("iChannel")){
                            
                                jsSChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                

                        }
                        else if(portname.equals("oChannel")){
                            
                                jsSChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                        }
                        else if(portname.equals("iAChannel")|| portname.equals("oAChannel")){
                               
                                if(portname.equals("iAChannel")){
                                    
                                        
                                        jsAChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                                }
                                else
                                        jsAChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                                
                                
                                        
                                
                        }
                } catch(Exception e){
                        e.printStackTrace();
                        System.exit(1);
                }
        }
        
    try {
        jsSigInOut.put("inputs",jsSigIn);
        jsSigInOut.put("outputs",jsSigOut);
        //jsSigs.put("signals",jsSigInOut);
        jsSChanInOut.put("inputs",jsSChanInput);
        jsSChanInOut.put("outputs",jsSChanOutput);
        
        jsAChanInOut.put("inputs", jsAChanInput);
        jsAChanInOut.put("outputs",jsAChanOutput);
        //jsSChans.put("SChannels", jsSChanInOut);
        //jsAChans.put("AChannels",hfefe);
        
        jsSigsChans.put("signals",jsSigInOut);
        jsSigsChans.put("SChannels",jsSChanInOut);
        jsSigsChans.put("AChannels",jsAChanInOut);
        //jsSigsChans.put("CDSSLocation",ssname);
        jsSigsChans.put("CDClassName",CDClassName);
        //jsSigsChans.put("CDLifeStatus","Alive");
        
        //jsSigsChans.put("AChannels",jsSigInOut);
        
        jsCDSigsChans.put(cdname,jsSigsChans);
        
        

        
        
        //jsParsedCD = jsCDSigsChans;
    } catch (JSONException ex) {
        
        ex.printStackTrace();
    }
    
    //System.out.println("CDLCMapParser Mapped signals channels one CD:" +jsCDSigsChans);
        return jsCDSigsChans;
}



}


