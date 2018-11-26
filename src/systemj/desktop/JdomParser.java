package systemj.desktop;

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
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
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
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.CyclicScheduler;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.opcua_milo.ClientExampleRunSOSJ;
import systemj.common.opcua_milo.ClientRunner;
import systemj.common.opcua_milo.IClient;
import systemj.common.opcua_milo.MiloServerHandler;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.common.SOAFacility.TCPIPLinkRegistry;
//import systemj.common.SignalObjBuffer;
import systemj.interfaces.GenericInterface;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Scheduler;
import systemj.lib.AChannel;
import systemj.lib.Signal;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;



public class JdomParser {

private String file;
private String fnameAll;
private String SSName;
private SystemJProgram program;
private InputStream is;
private MainPrinter mp = new MainPrinter();
private int gid = 0;

private int intServiceIndex=1;

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

//SJServiceRegistry currServReg = new SJServiceRegistry();
JSONObject actionIntf = new JSONObject();
JSONObject actionIntfTot = new JSONObject();

private boolean IsSOSJOPCUA = false;

//Udayanto modification

public JdomParser(){}
public JdomParser(String file){
        this.file = file;
        program = new SystemJProgram();
}

public JdomParser(String file, boolean IsSOSJOPCUA){
    this.file = file;
    program = new SystemJProgram();
    
    this.IsSOSJOPCUA = IsSOSJOPCUA;
    
}

public JdomParser(InputStream iis){
        this.is = iis;
        program = new SystemJProgram();
}

public void setFile(String file){
        this.file = file;
        program = new SystemJProgram();
}

public void setGenSunspot() {
        mp.genSunspot = true;
}

public void generateMain() throws FileNotFoundException{
    String fname = Paths.get(file).getFileName().toString();
    int pos = fname.lastIndexOf(".");
    if (pos > 0) {
        fname = fname.substring(0, pos); 
    }
    fnameAll=fname;
        mp.setGen(fname+".java");
        //mp.setGenServDesc(fname+"SD.java");
}

public void generateMainSDSPOT() throws FileNotFoundException{
    String fname = Paths.get(file).getFileName().toString();
    int pos = fname.lastIndexOf(".");
    if (pos > 0) {
        fname = fname.substring(0, pos); 
    }
    fnameAll=fname;
        //mp.setGen(fname+".java");
        mp.setGenServDesc(fname+"SD.java");
}

public SystemJProgram parse() throws Exception{
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        if(file != null){
                File f = new File(file);
                doc = builder.build(f);
        }
        else if(is != null)
                doc = builder.build(is);
        else
                throw new RuntimeException("Error : cannot parse - no inputstream");

        Element e = doc.getRootElement();
        if(!e.getName().equals("System"))
                throw new RuntimeException("Error : XML file should have a root element : System");

        if(e.getAttribute("jpf") != null){
                if(e.getAttribute("jpf").getBooleanValue()){
                        //SystemJProgram.jpfenabled = true;
                        mp.println("SystemJProgram.jpfenabled = true;");
                }
        }

        parseSystem(e);
        //mp.printlnSDJSONString(localServList.tostring());
        //mp.printlnSDJSONByte(localServList.toString().getBytes());
        mp.flush();
        //mp.flushSDSPOT();
        
        //SJServiceRegistry.setParsingStatus(true);
        return program;
}

private void parseSystem(Element el){
        
        List<Element> subsystems = el.getChildren("SubSystem");
        List<Element> intercon = el.getChildren("Interconnection");
        InterfaceManager im = new InterfaceManager();
        mp.println("InterfaceManager im = new InterfaceManager();");

        if(intercon.size() > 1)
                throw new RuntimeException("Multiple Interconnection element found");
        else if(intercon.size() == 1)
                parseInterconnection(intercon.get(0), im);
        
        im = IMBuffer.getInterfaceManagerConfig();
        
        if(subsystems.size() == 0)
                throw new RuntimeException("No subsystems found in the XML");

        mp.println("try {\n");
        //mp.println("SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsReg,true);");
       
        
        Element ss = makeMap(subsystems, im);
        parseSubSystem(ss, im);
        
        //SJSSCDSignalChannelMap.InitAllSignalChannelToMap(jsParsedSS);
        
        SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsParsedSS);
        SJSSCDSignalChannelMap.UpdateAllSignalChannelPrevMap(jsParsedSS);
        
        SJSSCDSignalChannelMap.InitAllNonLocalSSToMap(jsAllSSName);
    
     /*   
    try {
        //System.out.println("All SS: " +jsAllSSName.toPrettyPrintedString(3, 1));
        System.out.println("One SS: " +jsParsedSS.toPrettyPrintedString(3, 1));
        
    } catch (JSONException ex) {
        Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    */
//  try {
    //Udayanto modification
     //System.out.println("service available: " +localServList);
         try {
                SJServiceRegistry.AppendNodeServicesToCurrentRegistry(localServList);
         } catch (JSONException ex) {
                ex.printStackTrace();
          }
        mp.println("SJServiceRegistry.AppendNodeServicesToCurrentRegistry(localServList, true);\n");
        
         mp.println("} catch (JSONException jex) {\n");
        mp.println("jex.printStackTrace();\n");
         mp.println("}\n");
//} catch (JSONException ex) {
//     Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
// }
         

        IMBuffer.SaveInterfaceManagerConfig(im);
        //Udayanto modification

        program.setInterfaceManager(im);
        program.init();
        mp.println("program.setInterfaceManager(im);");
        mp.println("program.init();");

        im.printLocalInterface();
        mp.println("im.printLocalInterface();");

        System.out.println("\nConstructed clock-domain map : ");
        System.out.println(program.getInterfaceManager().getcdmap());

        mp.println("System.out.println(\"\\nConstructed clock-domain map : \");");
        mp.println("System.out.println(program.getInterfaceManager().getcdmap());");
        //mp.println("JSONObject jsReg ="+fnameAll+"SD.getServDesc();");
        
       
        
        //should inject Service description here
        
        
        mp.println("program.startSOAThread();");
        mp.println("program.startProgram();");
}

public Element makeMap(List<Element> el, InterfaceManager im){
    
        Element localsub = null;
        try {
                for(Element subsystem : el){
                        String name = subsystem.getAttributeValue("Name");
                        if(name == null || name.isEmpty())
                                throw new RuntimeException("Specify subsystem name");

                        if(subsystem.getAttribute("Local") != null && subsystem.getAttribute("Local").getBooleanValue()){
                                if(localsub == null){
                                        localsub = subsystem;
                                        SJSSCDSignalChannelMap.addLocalSSName(name);
                                        System.out.println("JDOMParser is added SSName true?:" +SJSSCDSignalChannelMap.IsSSNameLocal(name));
                                }
                                else {
                                        throw new RuntimeException("Multiple Local subsystem found");
                                }
                        }
                        
                        if(subsystem.getAttribute("Addr")!=null){
                            
                            String SSAddr = subsystem.getAttribute("Addr").getValue();
                            SJSSCDSignalChannelMap.SetLocalSSAddr(SSAddr);
                            
                        }else {
                            throw new RuntimeException("Addr need to be defined");
                        }
                        
                        if(subsystem.getAttribute("SSExpiry")!=null){
                            
                            String SSExpiryTime = subsystem.getAttribute("SSExpiry").getValue();
                            SJSSCDSignalChannelMap.SetSSExpiryTime(Long.parseLong(SSExpiryTime));
                            
                        } else {
                            throw new RuntimeException("SSExpiry need to be defined");
                        }
            
                        
                        constructMap(subsystem, name, im);
                        
                        
                    //try {
                    //    jsSS.put(name,jsParsedSS);
                  
                    //} catch (JSONException ex) {
                   //     ex.printStackTrace();
                   // }
                        
                }
        } catch (DataConversionException e) {
                e.printStackTrace();
                System.exit(1);
        }

        //jsParsedAllSS = jsSS;

        if(localsub == null)
                throw new RuntimeException("No local subsystems found");

        return localsub;
}

public void constructMap(Element el, String ssname, InterfaceManager im){
    
        
        if(el.getName().equals("ClockDomain")){
                if(el.getAttributeValue("Name") == null || el.getAttributeValue("Name").isEmpty())
                        throw new RuntimeException("Specify clockdomain name");

                // This needs to go to the channel receiver, and sender
                im.addCDLocation(ssname, el.getAttributeValue("Name"));
                //SJSSCDSignalChannelMap.addCDLocation(el.getAttributeValue("Name"), ssname);
                //SJSSCDSignalChannelMap.addCDNameToSSLocation(el.getAttributeValue("Name"), ssname);
            try {
                jsAllSSName.put(ssname,el.getAttributeValue("Name"));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
                mp.println("im.addCDLocation(\""+ssname+"\",\""+el.getAttributeValue("Name")+"\");");

        }
        else if(el.getName().equals("SubSystem") && !el.getParentElement().getName().equals("System"))
                throw new RuntimeException("SubSystems cannot be nested");

        List<Element> l = el.getChildren();
        for(Element ee : l){
                constructMap(ee, ssname, im);

        }
}


public void parseSubSystem(Element subsystem, InterfaceManager im){
        List<Element> cds = subsystem.getChildren("ClockDomain");
        List<Element> schedulers = subsystem.getChildren("Scheduler");
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

        System.out.println("Local SubSystem : "+subsystem.getAttributeValue("Name"));
        mp.println("System.out.println(\"Local SubSystem : "+subsystem.getAttributeValue("Name")+"\");");
        program.setSubSystemName(subsystem.getAttributeValue("Name"));
        SSName = program.getSubSystemName();
        im.setLocalInterface(subsystem.getAttributeValue("Name"));

        mp.println("program.setSubSystemName(\""+SSName+"\");");
        mp.println("im.setLocalInterface(\""+subsystem.getAttributeValue("Name")+"\");");

        Hashtable clockdomains = new Hashtable();

        // First pass to create all the local subsystem's clock-domains
        createCDInstances(subsystem, 0, clockdomains);

        System.out.println("Local cds : "+clockdomains);
        
        CDObjectsBuffer.CopyCDInstancesToBuffer(clockdomains);
        //CDObjectsBuffer.CopySCCDInstancesToMap(clockdomains);
        
        Hashtable channels = new Hashtable();
        mp.println("Hashtable channels = new Hashtable();");
        
         // Checking whether RMI port num has been defined
         if(rmi.size() > 0 ){
            Element rmir = rmi.get(0);
            try {
                systemj.signals.network.RMIReceiver.setRegistry(Integer.parseInt(rmir.getAttributeValue("Port")));
                mp.println("systemj.signals.network.RMIReceiver.setRegistry(Integer.parseInt(rmir.getAttributeValue(\"Port\")));\n");
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
        if(cds.size() > 0){
                CyclicScheduler cs = new CyclicScheduler();
                mp.println("CyclicScheduler cs = new CyclicScheduler();");
                
                for(Element cd : cds){

                        // More intuitive
                    //int cdCount = cds.size();
                        // Udayanto modiification

                //    if (!cd.getName().equals("deviceDescription"))  //this may not be necessayr
                //    {
                    //mp.println("for(int i=0;i<"+cdCount+";i++){\n"); 
                    
                        ClockDomain cdd = parseClockDomain(cd, subsystem.getAttributeValue("Name"),channels, im, clockdomains);
                        
                        Enumeration enumParsedCD = jsParsedCD.keys();
                        
                        while (enumParsedCD.hasMoreElements()){
                            String key = enumParsedCD.nextElement().toString();
                            try {
                                jsCDtot.put(key, jsParsedCD.getJSONObject(key));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                        
                        // need to handle device description

                                try {

                                //if (!localServAttr.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    //if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {internalServiceIndex++; localServList.put("service"+internalServiceIndex, localServAttr);}
                                    
                                    mp.println("JSONObject localServAttrPrnt=new JSONObject();\n");
                                    mp.println("JSONObject localServList=new JSONObject();\n");
                                    
                                    if (!localServAttrPrnt.toString().equalsIgnoreCase("{}")) {
                                        internalServiceIndex++; 
                                        
                                        Enumeration ServAttrEnum = localServAttrPrnt.keys();
                                        
                                        while (ServAttrEnum.hasMoreElements()){
                                            Object key = ServAttrEnum.nextElement(); 
                                            localServList.put(key.toString(), localServAttrPrnt.get(key.toString()));
                                            //String k = key.toString();
                                            mp.println("localServList.put(\""+key.toString()+"\", localServAttrPrnt.get(\""+key.toString()+"\"));\n");
                                        }
                                        
                                        //localServList.put("service"+internalServiceIndex, localServAttr);
                                        //localServList=localServAttrPrnt;
                                    }
                   
                                    
                                   // System.out.println("JDOMParser, localServAttrPrnt:" +localServAttrPrnt.toPrettyPrintedString(2, 0));
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                //localServAttr.clear();
                                
                        //internal service list doesn't need additional node ID, node ID is put when integrating into external registry
                        cs.addClockDomain(cdd);
                        mp.println("cs.addClockDomain("+cd.getAttributeValue("Name")+");");
                        //mp.println("}/n");
               //     }

                        //  Udayanto modification

                }
                program.addScheduler(cs);
                mp.println("program.addScheduler(cs);");
        }

    try {
        jsSSCDTot.put(subsystem.getAttributeValue("Name"), jsCDtot);
        
        //System.out.println("JDOMParser One SS total: " +jsSSCDTot.toPrettyPrintedString(3, 1));
        
        jsParsedSS = jsSSCDTot;
    } catch (JSONException ex) {
        ex.printStackTrace();
    }
        
        // Pre-defined scheduler
        if(schedulers.size() > 0){
                for(Element ell : schedulers){
                        String sclazz = ell.getAttributeValue("Class");
                        if(sclazz == null)
                                throw new RuntimeException("Scheduler must have Class attribute");

                        String args = ell.getAttributeValue("Args");
                        try {
                                Scheduler sc = ((Class<Scheduler>)Class.forName(sclazz)).newInstance();
                                List<Element> cdss = ell.getChildren("ClockDomain");
                                String scname = "sc"+(gid++);
                                mp.println(sclazz+" "+scname+" = new "+sclazz+"();");

                                if(args != null){
                                        sc.addArguments(args);
                                        mp.println(scname+".addArguments(\""+args+"\");");
                                }

                                for(Element cd : cdss){
                                        ClockDomain cdd = parseClockDomain(cd, subsystem.getAttributeValue("Name"),channels, im, clockdomains);
                                        sc.addClockDomain(cdd);
                                        mp.println(scname+".addClockDomain("+cd.getAttributeValue("Name")+");");
                                }
                                program.addScheduler(sc);
                                mp.println("program.addScheduler("+scname+");");
                        } catch(Exception e){
                                e.printStackTrace();
                                System.exit(1);
                        }

                }

                //Scheduler scheduler = s
        }

        im.setChannelInstances(channels);
        
        //Enumeration keysChan = channels.keys();
        
        //while(keysChan.hasMoreElements()){
         //   String key = keysChan.nextElement().toString();
            
            //System.out.println("channels hash: " +key+ " ,value: " +channels.get(key));
       // }
        
        mp.println("im.setChannelInstances(channels);");
}

private void createCDInstances(Element e, int level, Hashtable clockdomains) {
        if(e.getName().equals("ClockDomain")){
                if(level != 0)
                        throw new RuntimeException("ClockDomains cannot be nested");

                level++;
                String name = e.getAttributeValue("Name");
                String clazz = e.getAttributeValue("Class");
                if(name == null || name.isEmpty())
                        throw new RuntimeException("Specify clockdomain name");
                if(clazz == null || clazz.isEmpty())
                        throw new RuntimeException("Specify clockdomain Class");

                if(!mp.isGenerating()){
                        ClockDomain cdins = null;
                        try {
                                cdins = cdins = (ClockDomain)Class.forName(clazz).newInstance();
                        } catch(Exception ee){
                                ee.printStackTrace();
                                System.exit(1);
                        }
                        
                        cdins.setName(name);
                        cdins.setState("Active");
                        clockdomains.put(name, cdins);
                }

                mp.println(clazz+" "+name+" = new "+clazz+"();");
                mp.println(name+".setName(\""+name+"\");");
        }

        List<Element> l = e.getChildren();
        for(Element el : l){
                this.createCDInstances(el, level,clockdomains);
        }
}

/**
 * 
 * Parses clock-domain and initializes interface ports using reflection
 * 
 * @param cd
 * @param subsystem
 * @return ClockDomain instance initialized from this method call
 */
public ClockDomain parseClockDomain(Element cd, String ssname, Hashtable channels, InterfaceManager im, Hashtable clockdomains){

    JSONObject jsSigInOut = new JSONObject();

    JSONObject jsSChanInOut = new JSONObject();
    JSONObject jsSigsChans = new JSONObject();

    JSONObject jsAChanInOut = new JSONObject();
    JSONObject jsAChans = new JSONObject();
    
    JSONObject jsCDSigsChans = new JSONObject();
    
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

        boolean isServices = false;
        
        //OPCUA config
        //String RegistrationPeriod = "10000"; //some initialization value to avoid NullException being thrown. //in milliseconds??
        String OwnAddr = "127.0.0.1"; //some initialization value to avoid NullException being thrown. //localhost by default
        int BindPort = 4840; //some initialization value  //4840 is usually the port used in OPC UA, but not necessarily this unless it's a discovery server
        //END OPC UA config
        String DiscServAddr = "127.0.0.1"; //some initialization value to avoid NullException being thrown. //localhost by default
        
        String cdname = cd.getAttributeValue("Name");
        String CDClassName = cd.getAttributeValue("Class");
        if(cd.getAttributeValue("IsServices") != null){
            
           //throw new RuntimeException("'IsServices' attribute has to be put in 'ClockDomain' configuration, set as 'true' for CD as service, or 'false' for CD as a normal software behavior");
        //}
        //else
        //{
            isServices = Boolean.parseBoolean(cd.getAttributeValue("IsServices"));
        }
        
        /*
         * Udayanto
         * OPC UA changes
         */
        if (IsSOSJOPCUA) {
        	/*
        	if(cd.getAttributeValue("RegistrationPeriod") != null){
        		
        		RegistrationPeriod = cd.getAttribute("RegistrationPeriod").getValue();
        		
        		
        		
        	} else {
        		throw new RuntimeException("CD " +cdname+ " is OPC UA enabled, but missing 'RegistrationPeriod' parameter ");
        	}
        	*/
        	
        	
        	
        	if(cd.getAttributeValue("OwnAddr") != null){
        		
        		OwnAddr = cd.getAttribute("OwnAddr").getValue();
        		
        	} else {
        		throw new RuntimeException("CD " +cdname+ " is OPC UA enabled, but missing 'OwnAddr' parameter ");
        	}
        	
        	if(cd.getAttributeValue("DiscServAddr") != null){
        		
        		DiscServAddr = cd.getAttribute("DiscServAddr").getValue();
        		
        	} else {
        		throw new RuntimeException("CD " +cdname+ " is OPC UA enabled, but missing 'DiscServAddr' parameter ");
        	}
        	
        	if(cd.getAttributeValue("BindPort") != null){
        		
        	    BindPort = Integer.parseInt(cd.getAttribute("BindPort").getValue());
        		
        		
        		
        	} else {
        		throw new RuntimeException("CD " +cdname+ " is OPC UA enabled, but missing 'BindPort' parameter ");
        	}
        	
        	
        	// Create Milo Server
        	
        	try {
        		
        			
        			MiloServerHandler milo_server_h = new MiloServerHandler(cdname,OwnAddr,BindPort);

        			milo_server_h.startup(DiscServAddr).get();
        	        
        	        OPCUAClientServerObjRepo.AddServerObj(cdname, milo_server_h);
        	        //final CompletableFuture<Void> future = new CompletableFuture<>();

        	        //Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown().thenRun(() -> future.complete(null))));

        	        //future.get();
        	        
        	        
        			
        		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	System.out.println("Done creating Milo Server, now Client...");
        	
        	
        	
        	// Create Milo Client?
        	
        	
        	try {
        		
        		ClientExampleRunSOSJ icl = new ClientExampleRunSOSJ();
    			
    			ClientRunner client_run = new ClientRunner(endpointUrl, icl);
    	        
    	        OPCUAClientServerObjRepo.AddClientObj(cdname, );
    	        //final CompletableFuture<Void> future = new CompletableFuture<>();

    	        //Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown().thenRun(() -> future.complete(null))));

    	        //future.get();
    	        
	    	        
	    			
	    		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        }
        
        if (isServices && cd.getChild("Services")==null){
            throw new RuntimeException("JDOMParser: XML config require <Services> tag for CD that provide services");
        } else if (isServices && cd.getChild("Services")!=null){
            List <Element> sds = cd.getChild("Services").getChildren();
            
            
            for (Element sd : sds){  

            //System.out.println("port value: " +port.);
                
                
                localServAttr = new JSONObject();
                mp.println("localServAttr=new JSONObject();\n");
                
             //   if (sd.getName().equals("serviceDescription")){

               // }
                

                if (sd.getName().equals("serviceDescription")){
                    

                     servChecker=true;

                     //System.out.println("Service Description exist");
                   //  if(sd.getAttributeValue("serviceName") == null || sd.getAttributeValue("serviceType") == null || sd.getAttributeValue("nodeAddress")==null)
                   //  throw new RuntimeException("'ServiceDescription' require 'ServiceName', 'ServiceType', 'IsServices', and 'NodeIPAddress' attributes, NodeIPAddress' being the node IPv4 address");



                                //List<Attribute> SOAAttributes = ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                     List<Attribute> SOAAttributes = sd.getAttributes();
                                        //ports.get(ports.indexOf("ServiceDescription")).getAttributes();  //service description information is passed to signal/channel classes in case needed

                                //if (!SOAAttributes.contains("NodeIPAddress")){

                                //}
                     
                     

                     for (Attribute SOAAttribute : SOAAttributes){
                         
                         
                         
                       try {
                            localServAttr.put(SOAAttribute.getName(), SOAAttribute.getValue());
                            mp.println("localServAttr.put(\""+SOAAttribute.getName()+"\",\""+SOAAttribute.getValue()+"\");\n");
                            //localServAttr.clear();
                                        //config.put(SOAAttribute.getName(), SOAAttribute.getValue());
                                        //mp.toBuffer(cn+".put(\""+SOAAttribute.getName()+"\", \""+SOAAttribute.getValue()+"\");"); //?? what is this
                                       // if (SOAAttribute.getName().trim().equalsIgnoreCase("NodeIPAddress")){
                                       //     IPChecker = true;
                                      //  }
                           } catch (JSONException ex) {
                                        Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                     
                 //    if (!localServAttr.has("relatedSSName")){
                         
                 //        throw new RuntimeException("'relatedSSName' attribute needs to be included");
                         
                 //    }
                                
                    try {
                        //if (localServAttr.getString("serviceRole").equalsIgnoreCase("provider")){
                           
                           // System.out.println("Physical desc: "+sd.getChild("physicalDescription"));
                            
                            
                           if (sd.getChild("physicalDescription")!=null){
                               
                                Element sdDevDescParam = sd.getChild("physicalDescription");
                                
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
                                                    System.out.println("JDOMParser, cannot put phyServDescrParam attribute" +ex.getMessage());
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
                           
                            List <Element> sdDetAllActs = sd.getChildren("action");
                            System.out.println("JDOMParser, action tot : " +sdDetAllActs+ "total: " +sdDetAllActs.size());
                            
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
                                                                // mp.println("JSONObject actionServ = new JSONObject();\n");
                                                                mp.println("actionServ=new JSONObject();\n");
                                                
                                                
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
                                                                        mp.println("stateVarServ=new JSONObject();\n");
                                                                        List <Attribute> stateVarAttrs = stateVar.getAttributes();

                                                                        for (Attribute stateVarAttr : stateVarAttrs){
                                                                            try {
                                                                             stateVarServ.put(stateVarAttr.getName(), stateVarAttr.getValue());
                                                                             mp.println("stateVarServ.put(\""+stateVarAttr.getName()+"\",\""+stateVarAttr.getValue()+"\");\n");
                                                                             } catch (JSONException ex) {
                                                                               System.out.println("JDOMParser, cannot put stateVariable attribute" +ex.getMessage());
                                                                           }

                                                                  }

                                                                 
                                                                   try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       stateVarServTot.put("parameter"+stateVarInt,stateVarServ);
                                                                       //String s = "parameter"+stateVarInt;
                                                                       //mp.println("stateVarServTot.put(\""+s+"\",stateVarServ);\n");
                                                                       mp.println("stateVarServTot.put(\"+parameter"+stateVarInt+"\",stateVarServ);\n");
                                                                      // localServAttr.put("stateVariable",stateVarServTot);
                                                                       
                                                                   } catch (JSONException ex) {
                                                                       System.out.println("JDOMParser, cannot put parameters" +ex.getMessage());
                                                                   }
                                                            stateVarInt++;

                                                           }
                                                        
                                                         try {
                                                                       //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       //stateVarServTot.put("stateVariable"+stateVarInt,stateVarServ);
                                                                       
                                                                       actionServ.put("actionParameters",stateVarServTot);
                                                                       mp.println("actionServ.put(\"actionParameters"+stateVarInt+"\",stateVarServTot);\n");
                                                                       
                                                             } catch (JSONException ex) {
                                                                   System.out.println("JDOMParser, cannot put stateVariableTot" +ex.getMessage());
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
                                                                                mp.println("actionIntf = new JSONObject();\n");
                                                                                List <Attribute> actIntfAttrs = actIntf.getAttributes();

                                                                                for (Attribute actIntfAttr : actIntfAttrs){
                                                                                    try {
                                                                                     actionIntf.put(actIntfAttr.getName(), actIntfAttr.getValue());
                                                                                     mp.println("stateVarServ.put(\""+actIntfAttr.getName()+"\",\""+actIntfAttr.getValue()+"\");\n");
                                                                                     } catch (JSONException ex) {
                                                                                       System.out.println("JDOMParser, cannot put stateVariable attribute" +ex.getMessage());
                                                                                   }

                                                                          }


                                                                           try {
                                                                               //localServAttr.put("stateVariable"+stateVarInt,stateVarServ);
                                                                               actionIntfTot.put("interface"+actIntfInt,actionIntf);
                                                                               //String s = "parameter"+stateVarInt;
                                                                               //mp.println("stateVarServTot.put(\""+s+"\",stateVarServ);\n");
                                                                               mp.println("stateVarServTot.put(\"+parameter"+actIntfInt+"\",stateVarServ);\n");
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
                                                                               mp.println("actionServ.put(\"actionParameters"+actIntfInt+"\",stateVarServTot);\n");

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
                                                                    mp.println("actionServ.put(\""+actAttr.getName()+"\",\""+actAttr.getValue()+"\");\n");
                                                                } catch (JSONException ex) {
                                                                 Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
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

                                                                mp.println("actionServTot.put(\"action"+actionInt+"\",actionServ);\n");
                                                    } catch (JSONException ex) {
                                                        Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
                                                            }
                                                        actionInt++;
                                                       
                                                    }
                                
                            }
                            
                            
                            
                            localServAttr.put("action",actionServTot);
                            mp.println("localServAttr.put(\"action\",\"actionServTot\");\n");
                                        
                            //SOABuffer.setServiceOccupancy(localServAttr.getString("serviceName"),"ready");
                            
                        //}
                        
                               //    if (IPChecker==false){
                               //        throw new RuntimeException("'NodeIPAddress' attribute needs to be included in the service description, which is the node IP address (IPv4)");
                                //   }
                    } catch (JSONException ex) {
                        Logger.getLogger(JdomParser.class.getName()).log(Level.SEVERE, null, ex);
                    }

                                        //action parsing , need to be xpanded for mulitple actions

                                        //List <Element> sdDetActs = sd.getChildren("action");
                                        
                    /*
                                                       try{
                                                           
                                                           if (!localServAttr.isEmpty()){
                                                               //if (localServAttr.has("serviceVisibility")){
                                                                    //SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), localServAttr.getString("serviceVisibility"));
                                                                //SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString("serviceName"), "visible");
                                                                    //mp.println("SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString(\"serviceName\"),localServAttr.getString(\"serviceVisibility\"));\n");
                                                                mp.println("SOABuffer.setInitAdvVisibOneByOne(localServAttr.getString(\"serviceName\"),\"serviceVisibility\");\n");                                                                    
//localServAttr.remove("serviceVisibility");
                                                                    //mp.println("localServAttr.remove(\"serviceVisibility\");\n");
                                                               //}
                                                           }
                                                           
                                                       } catch (JSONException jex){
                                                           System.out.println("Jdomparser, parseClockDomain: "+jex.getCause());
                                                       }
                                                       */
                                            
                                            
                                                    try {
                                                        if (!localServAttr.isEmpty()){
                                                            localServAttrPrnt.put(localServAttr.getString("serviceName"),localServAttr);
                                                            mp.println("localServAttrPrnt.put(localServAttr.getString(\"serviceName\"),\"localServAttr\");\n");
                                                            intServiceIndex++;
                                                        }
                                                    } catch (JSONException ex) {
                                                        System.out.println("JDOMParser, cannot list localServAttrPrnt" +ex.getMessage());
                                                    }
               
                        }

        }
            
            
        }
 
        //Element signalChannel = cd.getChild("SignalChannel");
        List <Element> ports = cd.getChild("SignalChannel").getChildren();

        
        if (ports.isEmpty() || ports==null){
            throw new RuntimeException("'SignalChannel' attribute doesn't exist in CD: " + cd.getName()+ "please check your xml file");
        }
        //System.out.println("ports total:" +ports.size());
        


    // end Udayanto modif

        ClockDomain cdins = (ClockDomain)clockdomains.get(cdname);

        //List<Element> ports = cd.getChildren();

        // Udayanto modification --> save service description and to make the information easily accessible by other classes
        // obtain all available service in a subsystem\program (internal on a node) and to make it available on a registry that other machines could get that information

    //    for (Element port : ports){  

            //System.out.println("port value: " +port.);

   //         if (port.getName().equals("serviceDescription")){
      //          servChecker=true;
     //       }
     //   }

        if (servChecker==false && isServices==true){
            throw new RuntimeException("'CD as a service'Require 'Services' configuration containing at least 'ServiceName', 'ServiceType', and 'NodeIPAddress' attributes");
        }

        //end all

        JSONObject jsSigIn = new JSONObject();
                JSONObject jsSigOut = new JSONObject();
                JSONObject jsSChanInput = new JSONObject();
                JSONObject jsSChanOutput = new JSONObject();
                JSONObject jsAChanInput = new JSONObject();
                JSONObject jsAChanOutput = new JSONObject();
        
        for(Element port : ports){ //for each clock domain obtain signal/channel port config
                
                //System.out.println("ports:" +port.getName());
            
                if(port.getName().equals("iSignal")){
                    
                    if(port.getAttributeValue("Name")==null){
                        throw new RuntimeException("Interface signals must have Name)");
                    }
                    
                    if(!port.getAttributeValue("Name").equals("SOSJDiscovery") && port.getAttributeValue("Class") == null){
                        throw new RuntimeException("Interface signals must have both Name and Class attribute");
                    }
                    
                } else if (port.getName().equals("oSignal")){
                    
                   // if(port.getAttributeValue("Class") == null || port.getAttributeValue("Name") == null)
                         ///       throw new RuntimeException("Interface signals must have both Name and Class attribute");
                    
                    if(port.getAttributeValue("Name")==null){
                        throw new RuntimeException("Interface signals must have Name)");
                    }
                    
                    if(!port.getAttributeValue("Name").equals("SendACK") && port.getAttributeValue("Class") == null){
                        throw new RuntimeException("Interface signals must have both Name and Class attribute");
                    }
                    
                }
            
               // if(port.getName().equals("iSignal") || port.getName().equals("oSignal")){
              //          if(port.getAttributeValue("Class") == null || port.getAttributeValue("Name") == null)
              //                  throw new RuntimeException("Interface signals must have both Name and Class attribute");
              //  }
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

                        mp.clearBuffer();
                        mp.toBuffer("Hashtable "+cn+" = new Hashtable();");
                        for(Attribute attribute : attributes){
                                config.put(attribute.getName(), attribute.getValue());
                                jsSigChan.put(attribute.getName(), attribute.getValue());
                                
                                mp.toBuffer(cn+".put(\""+attribute.getName()+"\", \""+attribute.getValue()+"\");");
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
                                if(!mp.isGenerating()){
                                    
                                    if(port.getAttributeValue("Name").equalsIgnoreCase("SOSJDiscovery")){
                                        
                                        //server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.TransceiveDisc").newInstance();
                                        //server = (GenericSignalReceiver) Class.forName(port.getAttributeValue("Class")).newInstance();
                                        server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                        server.cdname = cdname;
                                        server.configure(config);

                                        // Reflection !!
                                        Field f = cdins.getClass().getField(port.getAttributeValue("Name"));
                                        Signal signal = (Signal)f.get(cdins);
                                        
                                        signal.setServer(server);
                                        signal.setuphook();
                                        signal.setInit();
                                        
                                        //Object inSigObj = (Object) signal;
                                        //Object inSigObj2 = (Object) server;
                                        
                                        //SignalObjBuffer.putInputSignalClassInstanceToMap(inSigObj, ssname,cdname, port.getAttributeValue("Name"));
                                        //SignalObjBuffer.putInputSignalGSRInstanceToMap(inSigObj2, ssname,cdname, port.getAttributeValue("Name"));
                                    
                                    } 
                                    
                                    else {
                                        
                                        server = (GenericSignalReceiver) Class.forName(port.getAttributeValue("Class")).newInstance();
                                        
                                        server.cdname = cdname;
                                        server.configure(config);

                                        // Reflection !!
                                        Field f = cdins.getClass().getField(port.getAttributeValue("Name"));
                                        Signal signal = (Signal)f.get(cdins);
                                        
                                        signal.setServer(server);
                                        signal.setuphook();
                                        signal.setInit();
                                        
                                        //Object inSigObj = (Object) signal;
                                        //Object inSigObj2 = (Object) server;
                                        
                                        //SignalObjBuffer.putInputSignalClassInstanceToMap(inSigObj, ssname,cdname, port.getAttributeValue("Name"));
                                        //SignalObjBuffer.putInputSignalGSRInstanceToMap(inSigObj2, ssname,cdname, port.getAttributeValue("Name"));
                                        
                                    }
                                    
                                        
                                        
                                        //System.out.println("JDOMParser ISignal signal: " +signal);
                                        //System.out.println("JDOMParser ISignal field: " +f);
                                        //System.out.println("JDOMParser ISignal cdins: " +cdins);
                                        //System.out.println("JDOMParser ISignal server:" +server);
                                }
                                
                                jsSigIn.put(port.getAttributeValue("Name"),jsSigChan);

                                mp.writeBuffer();
                                mp.println("GenericSignalReceiver "+dd+" = new "+port.getAttributeValue("Class")+"();");
                                //mp.println(dd+".cdname = "+cdname);
                                mp.println(dd+".cdname = \""+cdname+"\";");
                                mp.println(dd+".configure("+cn+");");
                                
                                mp.println(cdname+"."+port.getAttributeValue("Name")+".setServer("+dd+");");
                                mp.println(cdname+"."+port.getAttributeValue("Name")+".setuphook();");
                                mp.println(cdname+"."+port.getAttributeValue("Name")+".setInit();");
                        }
                        else if(portname.equals("oSignal")){
                            
                                jsSigOut.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                if(!mp.isGenerating()){
                                    
                                    if (port.getAttributeValue("Name").equalsIgnoreCase("SendACK")){
                                         
                                        client = (GenericSignalSender) Class.forName("systemj.signals.SOA.ServiceACKSender").newInstance();
                                        
                                        client.cdname = cdname;
                                        client.configure(config);
                                        
                                        // Reflection !!
                                        Field f = cdins.getClass().getField(port.getAttributeValue("Name"));
                                        Signal signal = (Signal)f.get(cdins);
                                        signal.setClient(client);
                                        signal.setInit();
                                        
                                        //Object outSigObj = (Object) signal;
                                        //Object outSigObj2 = (Object) client;
                                        
                                       // SignalObjBuffer.putOutputSignalClassInstanceToMap(outSigObj, ssname,cdname, port.getAttributeValue("Name"));
                                        //SignalObjBuffer.putOutputSignalGSSInstanceToMap(outSigObj2, ssname,cdname, port.getAttributeValue("Name"));
                                        
                                    } else {
                                        
                                        client = (GenericSignalSender) Class.forName(port.getAttributeValue("Class")).newInstance();
                                        
                                        client.cdname = cdname;
                                        client.configure(config);
                                        
                                        // Reflection !!
                                        Field f = cdins.getClass().getField(port.getAttributeValue("Name"));
                                        Signal signal = (Signal)f.get(cdins);
                                        signal.setClient(client);
                                        signal.setInit();
                                        
                                        //Object outSigObj = (Object) signal;
                                        //Object outSigObj2 = (Object) client;
                                        
                                        //SignalObjBuffer.putOutputSignalClassInstanceToMap(outSigObj, ssname,cdname, port.getAttributeValue("Name"));
                                        //SignalObjBuffer.putOutputSignalGSSInstanceToMap(outSigObj2, ssname,cdname, port.getAttributeValue("Name"));
                                        
                                        
                                    }
                                    
                                        
                                }
                                
                                mp.writeBuffer();
                                mp.println("GenericSignalSender "+dd+" = new "+port.getAttributeValue("Class")+"();");
                               //mp.println(dd+".cdname = "+cdname);
                                mp.println(dd+".cdname = \""+cdname+"\";");
                                mp.println(dd+".configure("+cn+");");
                                mp.println(cdname+"."+port.getAttributeValue("Name")+".setClient("+dd+");");
                                mp.println(cdname+"."+port.getAttributeValue("Name")+".setInit();");
                        }

                        else if(portname.equals("iChannel")){
                            
                                jsSChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                String cname = port.getAttributeValue("Name").trim()+"_in";
                                String pname = port.getAttributeValue("From").trim()+"_o";
                                
                                if(port.getAttributeValue("From").equalsIgnoreCase(".")){
                                    
                                    input_Channel inchan;
                                                output_Channel ochan = new output_Channel();
                                    
                                     Field f = cdins.getClass().getField(cname);
                                                        //Field f2 = partnercd.getClass().getField(pnames[1]);
                                                        inchan = (input_Channel)f.get(cdins);
                                                       
                                                        inchan.setInit();
                                                        inchan.setDistributed();
                                                        ochan.setInit();
                                                        
                                                        inchan.Name = cdname+"."+cname;
                                                inchan.PartnerName = pname;
                                                inchan.setChannelCDState("Active");
                                                inchan.setInterfaceManager(im);
                                                channels.put(inchan.Name, inchan);
                                                
                                    
                                } else {
                                    
                                
                                
                                String[] pnames = pname.split("\\.");

                                if(pnames.length != 2){
                                        throw new RuntimeException("Incorrect attribute value format for "+cname);
                                }
                                if(im.getCDLocation(pnames[0]) == null)
                                        throw new RuntimeException("Unrecognized Clockdomain name : "+pnames[0]);

                                if(!channels.containsKey(cdname+"."+cname)){
                                    
                                        // If the channel is local
                                        if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                ClockDomain partnercd = (ClockDomain)clockdomains.get(pnames[0]);
                                                if(partnercd == null && !mp.isGenerating())
                                                        throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                input_Channel inchan;
                                                output_Channel ochan;
                                                if(!mp.isGenerating()){
                                                        Field f = cdins.getClass().getField(cname);
                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                        inchan = (input_Channel)f.get(cdins);
                                                        ochan = (output_Channel)f2.get(partnercd);
                                                        inchan.setInit();
                                                        ochan.setInit();
                                                        
                                                }
                                                else{
                                                        inchan = new input_Channel();
                                                        ochan = new output_Channel();
                                                }

                                                // Mine
                                                inchan.Name = cdname+"."+cname;
                                                inchan.PartnerName = pname;
                                                // Partner
                                                ochan.Name = pname;
                                                ochan.PartnerName = cdname+"."+cname;

                                                inchan.setChannelCDState("Active");
                                                ochan.setChannelCDState("Active");
                                                
                                                inchan.set_partner_smp(ochan);
                                                ochan.set_partner_smp(inchan);
                                                
                                                inchan.setInterfaceManager(im);

                                                //Vector vec = new Vector();
                                                //vec.addElement(inchan);
                                                //vec.addElement(ochan);
                                                
                                                SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "SChannel", "input", port.getAttributeValue("Name"),(Object)inchan);
                                                SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, pnames[0], "SChannel", "output", port.getAttributeValue("Name"),(Object) ochan);
                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, cdname, "SChannel", "input", port.getAttributeValue("Name"),(Object) inchan);
                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, cdname, "SChannel", "output", port.getAttributeValue("Name"),(Object) ochan);
                                                
                                                mp.println(inchan.Name+".Name = \""+inchan.Name+"\";");
                                                mp.println(inchan.Name+".PartnerName = \""+inchan.PartnerName+"\";");
                                                mp.println(pname+".Name = \""+pname+"\";");
                                                mp.println(pname+".PartnerName = \""+inchan.Name+"\";");
                                                mp.println(inchan.Name+".set_partner_smp("+pname+");");
                                                mp.println(pname+".set_partner_smp("+inchan.Name+");");
                                                mp.println(inchan.Name+".setInit();");
                                                mp.println(pname+".setInit();");

                                                if(!channels.containsKey(ochan.Name) ){
                                                        channels.put(inchan.Name, inchan);
                                                        channels.put(ochan.Name, ochan);
                                                        mp.println("channels.put(\""+inchan.Name+"\", "+inchan.Name+");");
                                                        mp.println("channels.put(\""+ochan.Name+"\", "+ochan.Name+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);

                                                System.out.println("DEBUG : initialized "+inchan.Name+", "+inchan.PartnerName);
                                                mp.println("System.out.println(\"DEBUG : initialized "+inchan.Name+", "+inchan.PartnerName+"\");");
                                                //SJSSCDSignalChannelMap.addCDLocation(cdname, ssname);
                                        }
                                        else{
                                                input_Channel inchan;
                                                if(!mp.isGenerating()){
                                                        Field f = cdins.getClass().getField(cname);
                                                        inchan = (input_Channel)f.get(cdins);
                                                        inchan.setInit();
                                                }
                                                else 
                                                        inchan = new input_Channel();

                                                inchan.Name = cdname+"."+cname;
                                                inchan.PartnerName = pname;
                                                inchan.setDistributed();
                                                inchan.setInterfaceManager(im);
                                                inchan.setChannelCDState("Active");
                                                String SSDest = im.getCDLocation(pnames[0]);
                                                
                                                //Interconnection ic = im.getInterconnection();//SJSSCDSignalChannelMap.getInterConnection();
                                                
                                               // ic.AddChanLinkUserToSS(SSDest, cdname, "input", port.getAttributeValue("Name"));
                                                
                                                 SJSSCDSignalChannelMap.AddChanLinkUserToSS(SSDest, cdname, "input", port.getAttributeValue("Name"));
                                                
                                                //im.setInterconnection(ic);
                                                //SJSSCDSignalChannelMap.saveInterConnection(ic);
                                                
                                                //Vector vec = new Vector();
                                                //vec.addElement(inchan);
                                                
                                                SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, cdname, "SChannel", "input", port.getAttributeValue("Name"),(Object) inchan);
                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "SChannel", "input", port.getAttributeValue("Name"),);
                                 
                                                mp.println(inchan.Name+".Name = \""+inchan.Name+"\";");
                                                mp.println(inchan.Name+".PartnerName = \""+inchan.PartnerName+"\";");
                                                mp.println(inchan.Name+".setDistributed();");
                                                mp.println(inchan.Name+".setInterfaceManager(im);");
                                                mp.println(inchan.Name+".setInit();");

                                                if(!channels.containsKey(inchan.Name)){
                                                        channels.put(inchan.Name, inchan);
                                                        mp.println("channels.put(\""+inchan.Name+"\", "+inchan.Name+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+inchan.Name);

                                                System.out.println("DEBUG : initialized "+inchan.Name);
                                                mp.println("System.out.println(\"DEBUG : initialized "+inchan.Name+"\");");
                                        }
                                    }
                                }
                        }
                        else if(portname.equals("oChannel")){
                            
                                jsSChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                            
                                String cname = port.getAttributeValue("Name").trim()+"_o";
                                String pname = port.getAttributeValue("To").trim()+"_in";
                                
                                if(port.getAttributeValue("To").equalsIgnoreCase(".")){
                                    
                                    input_Channel inchan = new input_Channel();
                                                output_Channel ochan ;
                                    
                                     Field f = cdins.getClass().getField(cname);
                                                        //Field f2 = partnercd.getClass().getField(pnames[1]);
                                                        ochan = (output_Channel)f.get(cdins);
                                                       
                                                        ochan.setInit();
                                                        ochan.setDistributed();
                                                        inchan.setInit();
                                                        
                                                        ochan.Name = cdname+"."+cname;
                                                ochan.PartnerName = pname;
                                                ochan.setChannelCDState("Active");
                                                ochan.setInterfaceManager(im);
                                                channels.put(ochan.Name, ochan);
                                    
                                } else {
                                    
                                
                                
                                String[] pnames = pname.split("\\.");

                                if(pnames.length != 2){
                                        throw new RuntimeException("Incorrect attribute value format for "+cname);
                                }
                                if(im.getCDLocation(pnames[0]) == null)
                                        throw new RuntimeException("Unrecognized Clockdomain name : "+pnames[0]);

                                // If the channel is local
                                if(!channels.containsKey(cdname+"."+cname)){
                                        if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                ClockDomain partnercd = (ClockDomain)clockdomains.get(pnames[0]);
                                                if(partnercd == null && !mp.isGenerating())
                                                        throw new RuntimeException("Clock-domain "+pnames[0]+" not found");

                                                output_Channel ochan;
                                                input_Channel inchan;
                                                if(!mp.isGenerating()){
                                                    
                                                    System.out.println("parsing channel name: " +cdname+"."+cname);
                                                    
                                                        Field f = cdins.getClass().getField(cname);
                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                        ochan = (output_Channel)f.get(cdins);
                                                        inchan = (input_Channel)f2.get(partnercd);
                                                        ochan.setInit();
                                                        inchan.setInit();
                                                }
                                                else{
                                                        ochan = new output_Channel();
                                                        inchan = new input_Channel();
                                                }
                                                
                                                
                                                // Mine
                                                ochan.Name = cdname+"."+cname;
                                                ochan.PartnerName = pname;
                                                // Partner
                                                inchan.Name = pname; 
                                                inchan.PartnerName = cdname+"."+cname;

                                                inchan.setChannelCDState("Active");
                                                ochan.setChannelCDState("Active");
                                                
                                                inchan.set_partner_smp(ochan);
                                                ochan.set_partner_smp(inchan);
                                                
                                                ochan.setInterfaceManager(im);
                                                
                                               // Vector vec = new Vector();
                                                
                                                //vec.addElement(ochan);
                                               // vec.addElement(inchan);
                                                
                                               // SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "SChannel", "output", port.getAttributeValue("Name"),vec);
                                                
                                                SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, pnames[0], "SChannel", "input", port.getAttributeValue("Name"),(Object) inchan);
                                                SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, cdname, "SChannel", "output", port.getAttributeValue("Name"),(Object) ochan);

                                                mp.println(ochan.Name+".Name = \""+ochan.Name+"\";");
                                                mp.println(ochan.Name+".PartnerName = \""+ochan.PartnerName+"\";");
                                                mp.println(pname+".Name = \""+pname+"\";");
                                                mp.println(pname+".PartnerName = \""+ochan.Name+"\";");
                                                mp.println(ochan.Name+".set_partner_smp("+pname+");");
                                                mp.println(pname+".set_partner_smp("+ochan.Name+");");
                                                mp.println(ochan.Name+".setInit();");
                                                mp.println(pname+".setInit();");

                                                if(!channels.containsKey(inchan.Name)){
                                                        channels.put(inchan.Name, inchan);
                                                        channels.put(ochan.Name, ochan);
                                                        mp.println("channels.put(\""+inchan.Name+"\", "+inchan.Name+");");
                                                        mp.println("channels.put(\""+ochan.Name+"\", "+ochan.Name+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+inchan.Name);

                                                System.out.println("DEBUG : initialized "+inchan.Name+", "+inchan.PartnerName);
                                                mp.println("System.out.println(\"DEBUG : initialized "+inchan.Name+", "+inchan.PartnerName+"\");");
                                                //SJSSCDSignalChannelMap.addCDPairLocation(cdname, ssname);
                                        }
                                        else{
                                                output_Channel ochan;
                                                if(!mp.isGenerating()){
                                                        Field f = cdins.getClass().getField(cname);
                                                        ochan = (output_Channel)f.get(cdins);
                                                        ochan.setInit();
                                                }
                                                else
                                                        ochan = new output_Channel();
                                                
                                                ochan.Name = cdname+"."+cname;
                                                ochan.PartnerName = pname;
                                                ochan.setDistributed();
                                                ochan.setInterfaceManager(im);
                                                ochan.setChannelCDState("Active");
                                                String SSDest = im.getCDLocation(pnames[0]);
                                                //Interconnection ic = im.getInterconnection();//SJSSCDSignalChannelMap.getInterConnection();
                                                //ic.AddChanLinkUserToSS(SSDest, cdname, "output", port.getAttributeValue("Name"));
                                                
                                                SJSSCDSignalChannelMap.AddChanLinkUserToSS(SSDest, cdname, "output", port.getAttributeValue("Name"));
                                                //SJSSCDSignalChannelMap.saveInterConnection(ic);
                                                //im.setInterconnection(ic);

//Vector vec = new Vector();
                                                //vec.addElement(ochan);
                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "SChannel", "output", port.getAttributeValue("Name"),(Object) ochan);
                                                
                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(SSName, cdname, "SChannel", "output", port.getAttributeValue("Name"),(Object) ochan);
                                                
                                                mp.println(ochan.Name+".Name = \""+ochan.Name+"\";");
                                                mp.println(ochan.Name+".PartnerName = \""+ochan.PartnerName+"\";");
                                                mp.println(ochan.Name+".setDistributed();");
                                                mp.println(ochan.Name+".setInterfaceManager(im);");
                                                mp.println(ochan.Name+".setInit();");

                                                if(!channels.containsKey(ochan.Name)){
                                                        channels.put(ochan.Name, ochan);
                                                        mp.println("channels.put(\""+ochan.Name+"\", "+ochan.Name+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);

                                                System.out.println("DEBUG : initialized "+ochan.Name);
                                                mp.println("System.out.println(\"DEBUG : initialized "+ochan.Name+"\");");
                                        }
                                    }
                                }
                        }
                        else if(portname.equals("iAChannel")|| portname.equals("oAChannel")){
                                String cname = port.getAttributeValue("Name").trim();
                                String pname;
                                if(portname.equals("iAChannel")){
                                    
                                        pname = port.getAttributeValue("From").trim();
                                        jsAChanInput.put(port.getAttributeValue("Name"),jsSigChan);
                                }
                                else {
                                        jsAChanOutput.put(port.getAttributeValue("Name"),jsSigChan);
                                
                                        pname = port.getAttributeValue("To").trim();
                                }
                                String[] pnames = pname.split("\\.");

                                if(pnames.length != 2){
                                        throw new RuntimeException("Incorrect attribute value format for "+cname);
                                }
                                if(im.getCDLocation(pnames[0]) == null)
                                        throw new RuntimeException("Unrecognized Clockdomain name : "+pnames[0]);

                                if(!channels.containsKey(cdname+"."+cname)){
                                        // If the channel is local
                                        if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                ClockDomain partnercd = (ClockDomain)clockdomains.get(pnames[0]);
                                                if(partnercd == null && !mp.isGenerating())
                                                        throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                // Mine
                                                AChannel chan;

                                                if(!mp.isGenerating()){
                                                        Field f = cdins.getClass().getField(cname);
                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                        //f.set(cdins, chan);
                                                        chan = (AChannel)f.get(cdins);
                                                       
                                                        f2.set(partnercd, chan); // sharing achan obj
                                                        chan.setInit();
                                                        
                                                }
                                                else
                                                        chan = new AChannel();

                                                mp.println(pname+" = "+cdname+"."+cname+";");
                                                mp.println(pname+".setInit();");

                                                if(!channels.containsKey(pname) ){
                                                        channels.put(cdname+"."+cname, chan);
                                                        channels.put(pname, chan);
                                                        mp.println("channels.put(\""+cdname+"."+cname+"\", "+cdname+"."+cname+");");
                                                        mp.println("channels.put(\""+pname+"\", "+pname+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+pname);

                                                //Vector vec = new Vector();
                                                //vec.addElement(chan);
                                                
                                                //if (portname.equals("iAChannel")){
                                                    
                                                    SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "AChannel", "input", port.getAttributeValue("Name"),(Object)chan);
                                               // } else {
                                                    SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, pnames[0], "AChannel", "output", port.getAttributeValue("Name"),(Object)chan);
                                               // }
                                                
                                                
                                                System.out.println("DEBUG : initialized "+cdname+"."+cname+", "+pname);
                                                mp.println("System.out.println(\"DEBUG : initialized "+cdname+"."+cname+", "+pname+"\");");
                                                //SJSSCDSignalChannelMap.addCDLocation(cdname, ssname);
                                        }
                                        else{
                                                AChannel chan;
                                                if(!mp.isGenerating()){
                                                        Field f = cdins.getClass().getField(cname);
                                                        chan = (AChannel)f.get(cdins);
                                                        chan.setInit();
                                                }
                                                else
                                                        chan = new AChannel();
                                                
                                                chan.Name = cdname+"."+cname;
                                                chan.PartnerName = pname;
                                                chan.setDistributed();
                                                chan.setInterfaceManager(im);
                                                
                                                String SSDest = im.getCDLocation(pnames[0]);
                                                //Interconnection ic = im.getInterconnection();//SJSSCDSignalChannelMap.getInterConnection();
                                                
                                                if(portname.equals("iAChannel")){
                                    
                                                         //ic.AddChanLinkUserToSS(SSDest, cdname, "input", port.getAttributeValue("Name"));
                                                    SJSSCDSignalChannelMap.AddChanLinkUserToSS(SSDest, cdname, "input", port.getAttributeValue("Name"));
                                                    
                                                }
                                                else {
                                                         //ic.AddChanLinkUserToSS(SSDest, cdname, "output", port.getAttributeValue("Name"));
                                                    SJSSCDSignalChannelMap.AddChanLinkUserToSS(SSDest, cdname, "output", port.getAttributeValue("Name"));
                                                }
                                                
                                                
                                                
                                                //SJSSCDSignalChannelMap.saveInterConnection(ic);
                                                //im.setInterconnection(ic);
                                                mp.println(chan.Name+".Name = \""+chan.Name+"\";");
                                                mp.println(chan.Name+".PartnerName = \""+pname+"\";");
                                                mp.println(chan.Name+".setDistributed();");
                                                mp.println(chan.Name+".setInterfaceManager(im);");
                                                mp.println(chan.Name+".setInit();");
                                                
                                                //Vector vec = new Vector();
                                                //vec.addElement(chan);
                                                
                                                if (portname.equals("iAChannel")){
                                                    
                                                    SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "AChannel", "input", port.getAttributeValue("Name"),(Object) chan);
                                                } else {
                                                    SJSSCDSignalChannelMap.addInOutChannelObjectToMap(ssname, cdname, "AChannel", "output", port.getAttributeValue("Name"),(Object) chan);
                                                }

                                                if(!channels.containsKey(cdname+"."+cname)){
                                                        channels.put(cdname+"."+cname, chan);
                                                        mp.println("channels.put(\""+cdname+"."+cname+"\", "+cdname+"."+cname+");");
                                                }
                                                else
                                                        throw new RuntimeException("Tried to initialize the same channel twice : "+cdname+"."+cname);

                                                System.out.println("DEBUG : initialized "+cdname+"."+cname);
                                                mp.println("System.out.println(\"DEBUG : initialized "+cdname+"."+cname+"\");");
                                        }
                                }
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
        
        //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(cdname, "Active");

        //System.out.println("JDOMParser Mapped signals channels one CD:" +jsCDSigsChans.toPrettyPrintedString(3, 1));
        
        jsParsedCD = jsCDSigsChans;
    } catch (JSONException ex) {
        
        ex.printStackTrace();
    }
    
    CDLCBuffer.AddCDMacroState(cdname, "Active");
    
        return cdins;
}

public void parseInterconnection(Element el, InterfaceManager im){
        List<Element> l = el.getChildren("Link");
        Interconnection ic = new Interconnection();
        mp.println("Interconnection ic = new Interconnection();");

        for(Element link : l){
                Interconnection.Link linko = new Interconnection.Link();
                mp.println("Interconnection.Link linko = new Interconnection.Link();");
                List<Element> intfs = link.getChildren("Interface");
               // Vector SSNameColl = new Vector();
                for(Element intf : intfs){
                    
                        String SS = intf.getAttributeValue("SubSystem");
                        String clazz = intf.getAttributeValue("Class");
                        String interf = intf.getAttributeValue("Interface");
                        String args = intf.getAttributeValue("Args");
                        
                        //added small modification to register link port number usage
                        String[] ARGS = args.split(":");
                        String portNum = ARGS[1];
                        //need to add utilized port to the local port-usage 'registry'
                        
                        TCPIPLinkRegistry.AddSSAndPortPair(portNum, SS);
                        
                        // end
                        
                        if(SS == null || clazz == null || intf == null || args == null)
                                throw new RuntimeException("Interface should have the following elements : SubSystem, Class, Interface, Args");

                        try {
                                if(!mp.isGenerating()){
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
                                }

                                String htname = "ht"+(gid++);
                                String gctname = "gct"+(gid++);
                                mp.println(clazz+" "+gctname+" = new "+clazz+"();");
                                mp.println("Hashtable "+htname+" = new Hashtable();");
                                mp.println(htname+".put(\"Class\", "+"\""+clazz+"\");");
                                mp.println(htname+".put(\"Interface\", "+"\""+interf+"\");");
                                mp.println(htname+".put(\"Args\", "+"\""+args+"\");");
                                mp.println(htname+".put(\"SubSystem\","+"\""+SS+"\");");
                                mp.println(gctname+".configure("+htname+");");
                                mp.println("linko.addInterface(\""+SS+"\","+gctname+");");
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
                        mp.println("ic.addLink(linko, true);");
                }
                else if(type.equals("Destination")){
                        ic.addLink(linko, false);
                        //SJSSCDSignalChannelMap.addLinkType((Object)linko,type);
                        mp.println("ic.addLink(linko, false);");
                }
                else
                        throw new RuntimeException("Unrecognized Link Type : "+type);
        }
        
        //SJSSCDSignalChannelMap.saveInterConnection(ic);
        
        im.setInterconnection(ic);
        mp.println("im.setInterconnection(ic);");

        ic.printInterconnection(); // Debug
        mp.println("ic.printInterconnection(); // Debug");
        IMBuffer.SaveInterfaceManagerConfig(im);
}

}


