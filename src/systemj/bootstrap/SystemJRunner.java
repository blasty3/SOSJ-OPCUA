package systemj.bootstrap;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.input.JDOMParseException;
import org.json.me.JSONObject;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
//import systemj.common.SOAFacility.DynamicCDLocalReqReceiverThread;
import systemj.common.SOAFacility.DynamicCDReqReceiverThread;
//import systemj.common.SOAFacility.DynamicCDRemoteReqReceiverThread;
//import systemj.common.SOAFacility.ExpiryCheckerThread;
import systemj.common.SOAFacility.LocalLinkCreationHSReceiverThread;
//import systemj.common.SOAFacility.MessageReceiverThread;
//import systemj.common.SOAFacility.MessageSenderThread;
import systemj.common.SOAFacility.MigrationAndLinkLocalReqMsgRecThread;
//import systemj.common.SOAFacility.MigrationAndLinkReqMsgRecThread;
import systemj.common.SOAFacility.NoP2PLocalServMessageReceiverThread;
//import systemj.common.SOAFacility.NoP2PRemoteServMessageReceiverThread;
import systemj.common.SOAFacility.NoP2PServMessageSenderThread;
import systemj.common.SOAFacility.NoP2PServRegExpiryCheckerThread;
import systemj.common.SOAFacility.RegExpiryCheckerThread;
import systemj.common.SOAFacility.RegDiscReceiver;
import systemj.common.SOAFacility.RegReceiver;
import systemj.common.SOAFacility.RegSender;
//import systemj.common.SOAFacility.RegRemoteDiscMessageReceiverThread;
//import systemj.common.SOAFacility.RegRemoteMessageReceiverThread;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.opcua_milo.FindServersClient;
import systemj.desktop.JdomParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * SystemJ Runtime Environment - Bootstrap
 * 
 * @author Heejong Park , Udayanto Dwi Atmojo
 * 
 * 
 */
public class SystemJRunner
{
	private static double version = 3.0;
	private static HashMap<String, String> option = new HashMap<String, String>();
	private static SystemJProgram program;
        
        private static String SSName = null;
        private static boolean IsSOSJNoSO = false;
        private static boolean IsSOSJNoCD = false;
        private static boolean IsSOSJReg = false;
        private static boolean IsSOSJ = false;
        private static boolean IsSOSJP2P = false;
	private static boolean genmain = false;
	private static boolean genmain_sunspot = false;
        private static boolean genmain_gui=false;
        private static boolean IsSOASPOTTest = false;
        private static boolean IsSPOTRemoteSOATest = false;
        private static boolean IsSPOTRemoteSOA = false;
        
        private static boolean IsSOSJOPCUA = false;
        private static boolean IsSOSJOPCUALDS = false;
        
        
        
        private static String SOSJRegID, SOSJRegAddr,SOSJRegAdvExpiryTime;
        private static String GtwyAddr;
                private static String SubnetMask;
	private static String filename;
	private static InputStream filestream;
        private static boolean IsSpotRegOnly = false;
        private static boolean IsSpotReg = false;
        private static String genplatform;
        
        private static ScheduledExecutorService periodicFindServerQueryExec;
        
        public static String LDS_ADDR="localhost";
        
	
	private static void printInfo(){
		System.out.println("SystemJ Runtime Environment version \""+version+"\"");
	}
	private static void printUsage(){
		System.out.println("Usage: java -cp [RTS_jar_filename] systemj.bootstrap.SystemJRunner [-options] xmlfile.xml");
		System.out.println();
		System.out.println("Options:");
		System.out.println("\t-version\tprint version");
		System.out.println("\t-x\t\tgenerate bootstrap file");
		System.out.println("\t-xsp\t\tgenerate bootstrap file for sunspot");
	}
        
        private static void printSOSJUsage(){
                System.out.println("SOSJ (SOA + SystemJ) by Udayanto Dwi Atmojo, Dept of Electrical and Computer Eng, The University of Auckland, New Zealand");
		System.out.println("Usage: 1st arg - Physical server address. 2nd arg - xml configuration file");
		
                //System.out.println();
		//System.out.println("Options:");
		//System.out.println("\t-version\tprint version");
		//System.out.println("\t-x\t\tgenerate bootstrap file");
		//System.out.println("\t-xsp\t\tgenerate bootstrap file for sunspot");
	}
        
        private static void printSOARegUsage(){
                System.out.println("SOSJ (SOA + SystemJ) dedicated service registry, by Udayanto Dwi Atmojo, Dept Electrical and Computer Eng, The University of Auckland, New Zealand");
		System.out.println("Usage: 1st arg - Registry ID, name of the registry . 2nd arg - Address of the Registry . 3rd arg - Registry Advertisement expiry (in milliseconds). 4th arg - Server Address of the network . 5th arg - SubnetMask Address of the network");
		//System.out.println();
		//System.out.println("Options:");
		//System.out.println("\t-version\tprint version");
		//System.out.println("\t-x\t\tgenerate bootstrap file");
		//System.out.println("\t-xsp\t\tgenerate bootstrap file for sunspot");
	}
        
        private static void printSOSJNoSOUsage(){
                System.out.println("SOSJ (SOA + SystemJ), Dynamic without SO, by Udayanto Dwi Atmojo, Dept Electrical and Computer Eng, The University of Auckland, New Zealand");
		System.out.println("Usage: 1st arg - XML config file");
		//System.out.println();
		//System.out.println("Options:");
		//System.out.println("\t-version\tprint version");
		//System.out.println("\t-x\t\tgenerate bootstrap file");
		//System.out.println("\t-xsp\t\tgenerate bootstrap file for sunspot");
	}
        
        private static void printSOSJNoCDUsage(){
                System.out.println("SOSJ (SOA + SystemJ), Dynamic without CDs, by Udayanto Dwi Atmojo, Dept Electrical and Computer Eng, The University of Auckland, New Zealand");
		System.out.println("Usage: 1st arg - Physical server address. 2nd arg - container 'subsystem' name");
		//System.out.println();
		//System.out.println("Options:");
		//System.out.println("\t-version\tprint version");
		//System.out.println("\t-x\t\tgenerate bootstrap file");
		//System.out.println("\t-xsp\t\tgenerate bootstrap file for sunspot");
	}
        
	private static boolean parseOption(String[] args){
		if(args.length == 0){
			printUsage();
			System.exit(1);
		}
		
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-version")){
				printInfo();
				System.exit(1);
			} else if(args[i].equals("-runrte")){
                            
                        }
			else if(args[i].toLowerCase().trim().endsWith(".xml")){
				// now need to parse xml
				filename = args[i];
                                System.out.println("filename: " +filename);
                                
			} else if (args[i].equals("-spotregsoatest")){
                            IsSOASPOTTest = true;
                            if (!args[i+1].isEmpty()){
                                filename = args[i+1];
                                break;
                            }
                        }
			else if(args[i].equals("-x")) {
				genmain = true;
			}
			else if(args[i].equals("-xsp")) {
				genmain_sunspot = true;
				genmain = true;
			}
                        else if(args[i].equals("-gui")) {
				genmain = true;
                                genmain_gui = true;
			}
                        else if (args[i].equals("-spotregonly")){
                            IsSpotRegOnly=true;
                        }
                        else if (args[i].equals("-spotreg")){
                            IsSpotReg=true;
                        } 
                        else if (args[i].equals("-x"))
                        {
                            genmain = true;
                            genplatform = "desktop";
                        }
                            else if (args[i].equals("-x-sunspot"))
                        {
                            genmain = true;
                            genplatform = "sunspot";
                        } 
                            else if (args[i].equals("-spotremotesoatest"))
                        {
                               IsSPOTRemoteSOATest = true; 
                               if (!args[i+1].isEmpty()){
                                    filename = args[i+1];
                                    break;
                               }
                        } 
                            else if (args[i].equals("-spotremotesoa"))
                        {
                               IsSPOTRemoteSOA = true; 
                               if (!args[i+1].isEmpty()){
                                    filename = args[i+1];
                                    break;
                               }
                        }
                        /* 
                        else if (args[i].equals("-sosjp2p"))
                        {
                               
                                IsSOSJP2P=true;
                                if (!args[i+1].isEmpty()){
                                    GtwyAddr = args[i+1];
                                    SubnetMask = args[i+2];
                                    filename = args[i+3];
                                    System.out.println("filename: " +filename);
                                    break;
                                }
                                    
                               
                        } 
                        */
                        else if (args[i].equals("-sosj")){
                            
                            IsSOSJ = true;
                            
                             if (args.length>i+1){
                                 
                                 if(args[i+1].equalsIgnoreCase("help")){
                                        printSOSJUsage();
                                        System.exit(1);
                                 } else {
                                    GtwyAddr = args[i+1];
                                    //SubnetMask = args[i+2];
                                    filename = args[i+2];
                                    System.out.println("filename: " +filename);
                                    break;
                                 }
                                 
                                    
                                } else {
                                    printSOSJUsage();
                                    System.exit(1);
                                }
                            
                        } 
                            else if (args[i].equals("-sosjreg"))
                        {
                                IsSOSJReg=true;
                                
                                if(args.length>i+1){
                                    if(args[i+1].equalsIgnoreCase("help")){
                                        printSOARegUsage();
                                        System.exit(1);
                                    } else {
                                        SOSJRegID = args[i+1];
                                        SOSJRegAddr = args[i+2];
                                        SOSJRegAdvExpiryTime = args[i+3];
                                        GtwyAddr = args[i+4];
                                        SubnetMask = args[i+5];
                                    }
                                } else {
                                    printSOARegUsage();
                                    System.exit(1);
                                }
                                    
                                    
                                    
                                    //filename = args[i+3];
                                    //System.out.println("filename: " +filename);
                                    break;
                                
                                
                        } else if(args[i].equals("-sosjnoso")){
                            IsSOSJNoSO = true;
                            
                            if(args.length>i+1){
                                    if(args[i+1].equalsIgnoreCase("help")){
                                        printSOSJNoSOUsage();
                                        System.exit(1);
                                    } else {
                                        filename = args[i+1];
                                    }
                                } else {
                                    printSOSJNoSOUsage();
                                    System.exit(1);
                                }
                            
                        }
                        
                        else if(args[i].equals("-sosjnocd")){
                            IsSOSJNoCD = true;
                            
                            if(args.length>i+1){
                                    if(args[i+1].equalsIgnoreCase("help")){
                                        printSOSJNoCDUsage();
                                        System.exit(1);
                                    } else {
                                    GtwyAddr = args[i+1];
                                    //SubnetMask = args[i+2];
                                    SSName = args[i+2];
                                    System.out.println("SS name: " +SSName);
                                    SOABuffer.AddEmptySSName(SSName);
                                    SJSSCDSignalChannelMap.addLocalSSName(SSName);
                                    break;
                                 }
                                } else {
                                    printSOSJNoCDUsage();
                                    System.exit(1);
                                }
                            
                        }
                        else if(args[i].equals("-sosjopcua")){
                            IsSOSJOPCUA = true;
                            
                            if(args.length>i+1){
                                    if(args[i+1].equalsIgnoreCase("help")){
                                        printSOSJNoCDUsage();
                                        System.exit(1);
                                    } else {
                                    GtwyAddr = args[i+1];
                                    //SubnetMask = args[i+2];
                                    SSName = args[i+2];
                                    System.out.println("SS name: " +SSName);
                                    SOABuffer.AddEmptySSName(SSName);
                                    SJSSCDSignalChannelMap.addLocalSSName(SSName);
                                    break;
                                 }
                                } else {
                                    printSOSJNoCDUsage();
                                    System.exit(1);
                                }
                            
                        }
                        else if(args[i].equals("-sosjopcualds")){
                        	IsSOSJOPCUALDS = true;
                            
                            if(args.length>i+1){
                                    if(args[i+1].equalsIgnoreCase("help")){
                                        printSOSJNoCDUsage();
                                        System.exit(1);
                                    } else {
                                    LDS_ADDR = args[i+1];
                                    //SubnetMask = args[i+2];
                                    //SSName = args[i+2];
                                    //System.out.println("SS name: " +SSName);
                                    //SOABuffer.AddEmptySSName(SSName);
                                    //SJSSCDSignalChannelMap.addLocalSSName(SSName);
                                    break;
                                 }
                                } else {
                                    printSOSJNoCDUsage();
                                    System.exit(1);
                                }
                            
                        }
                        
			else{
				System.out.println("Unrecognized option: "+args[i]);
				System.exit(1);
			}
		}
		return true;
	}
	private static void parseXML(){
	
            if (SJServiceRegistry.getParsingStatus()==true){
                SJServiceRegistry.setParsingStatus(false);
            }

            JdomParser parser;
                
		if(filename!=null) {
			
			//If using OPC UA
			
				parser = new JdomParser(filename, IsSOSJOPCUA);
			
		}
		else {
			parser = new JdomParser(filestream);
		}
		
		try{
			if(genmain_sunspot) {
				parser.setGenSunspot();
                                
			}
			if(genmain) {
				parser.generateMain();
                                //parser.generateMainSDSPOT();
			}
			program = parser.parse();
                        
                        
		}
		catch(Exception e){
			if(e instanceof JDOMParseException)
				System.err.println(e.getMessage());
			else
				e.printStackTrace();
			System.exit(1);
		}

	}
	
	public static void main(String[] args)
	{
            
            System.out.println("main started");
            
            parseOption(args);
            
            if(IsSOSJReg){
                setSOSJRegAddr();
                setSOSJRegID();
                setGatewayAddr();
                setSubnetMaskAddr();
                setRegAdvExpiryTime();
                StartSOARegThread();
            } else if(IsSOSJOPCUALDS){
                setGatewayAddr();
                //setSubnetMaskAddr();
                setSOSJRegAddr();
                setSOSJRegID();
                setGatewayAddr();
                setSubnetMaskAddr();
                StartOPC_UA_LDS();
            
            } 
            
            else
            {
                parseXML();
            }
            
            if (IsSpotRegOnly){
                //StartSOSJP2PThread();
                StartSPOTRegGatewayThread();
            } else if (IsSPOTRemoteSOATest){
                MainGeneratorSOATest mg = new MainGeneratorSOATest(filename);
                mg.setGenSunspot();
                try {
                    mg.generateMainSDSPOTSOATest();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                mg.flushSPOT();
            } else if (IsSOASPOTTest || IsSPOTRemoteSOA){
                MainGeneratorSOATest mg = new MainGeneratorSOATest(filename);
                mg.setGenSunspot();
                try {
                    mg.generateMainSDSPOT();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                mg.flushSPOT();
            } 
                else {
                
		//parseXML();

                if(!genmain){
                     SJServiceRegistry.setParsingStatus(true);
                     
                     while (!SJServiceRegistry.getParsingStatus()){
                         
                     }
                     
                     if(IsSOSJP2P){
                         setGatewayAddr();
                         setSubnetMaskAddr();
                         //StartSOSJP2PThread();
                     }
                     
                     if(IsSOSJ || IsSOSJNoCD){
                         setGatewayAddr();
                         //setSubnetMaskAddr();
                         StartSOSJThreads();
                     }
                     
                     
                     
                     if(IsSOSJNoSO){
                         StartSOSJNoSOThread();
                     }
                     
                     //StartSigChanReconfigurator();
                     if (genmain_gui){
                         //StartSOAGUI();
                     }
                     
                     if (IsSpotReg){
                         StartSPOTRegGatewayThread();
                     }
                     
                     if(IsSOSJ || IsSOSJNoCD || IsSOSJP2P || IsSOSJNoSO || IsSOSJOPCUA){
                         program.startProgram();
                     }
                     
                }
            }
    
	}
	
	
	public static void main(InputStream input){
            
            System.out.println("main started with filestream");
            
		filestream = input;
		parseXML();
                
		//SJServiceRegistry.setParsingStatus(true);
		if(!genmain){
                     SJServiceRegistry.setParsingStatus(true);
                     
                     while (!SJServiceRegistry.getParsingStatus()){
                         
                     }
                     //StartSOSJP2PThread();
                     //StartSigChanReconfigurator();
                     program.startProgram();
                }
                
	}
        
        private static void setGatewayAddr()
        {
            System.out.println("Gateway addr: " +GtwyAddr);
            SOABuffer.setGatewayAddr(GtwyAddr);
        }
        
        private static void setSubnetMaskAddr()
        {
            System.out.println("Subnet addr: " +SubnetMask);
            SOABuffer.setSubnetMaskAddr(SubnetMask);
        }
        
        private static void setSOSJRegID(){
            SOABuffer.setSOSJRegID(SOSJRegID);
        }
        
        private static void setSOSJRegAddr(){
            SOABuffer.setSOSJRegAddr(SOSJRegAddr);
        }
        
        private static void setRegAdvExpiryTime(){
            
            long refreshTime = 3*Long.parseLong(SOSJRegAdvExpiryTime);
            
            String StrRefTime = Long.toString(refreshTime);
            
            SOABuffer.setSOSJRegBeaconPeriodTime(StrRefTime);
        }
        
        private static void StartSOSJThreads(){
            Thread expirychecker = new Thread(new NoP2PServRegExpiryCheckerThread());
            //Thread remmsgreceiver = new Thread(new NoP2PRemoteServMessageReceiverThread());
            Thread locmsgreceiver = new Thread(new NoP2PLocalServMessageReceiverThread());
            Thread msgsender = new Thread(new NoP2PServMessageSenderThread());
           // Thread migMsgRec = new Thread(new MigrationAndLinkReqMsgRecThread());
            Thread migLocMsgRec = new Thread(new MigrationAndLinkLocalReqMsgRecThread());
            //Thread dynCDLocMsgRec = new Thread(new DynamicCDLocalReqReceiverThread());
            Thread dynCDMsgRec = new Thread(new DynamicCDReqReceiverThread());
           
            expirychecker.start();
            msgsender.start();
            //remmsgreceiver.start();
            locmsgreceiver.start();
           // migMsgRec.start();
            migLocMsgRec.start();
            
            //dynCDLocMsgRec.start();
            dynCDMsgRec.start();
            
        }
        
        private static void StartSOSJThreads(boolean IsOPCUA){
        	
        	if(IsOPCUA) {
        		
        		
                Thread locmsgreceiver = new Thread(new NoP2PLocalServMessageReceiverThread());
                Thread msgsender = new Thread(new NoP2PServMessageSenderThread());
               // Thread migMsgRec = new Thread(new MigrationAndLinkReqMsgRecThread());
                Thread migLocMsgRec = new Thread(new MigrationAndLinkLocalReqMsgRecThread());
                //Thread dynCDLocMsgRec = new Thread(new DynamicCDLocalReqReceiverThread());
                Thread dynCDMsgRec = new Thread(new DynamicCDReqReceiverThread());
               
                //expirychecker.start();
                msgsender.start();
                //remmsgreceiver.start();
                locmsgreceiver.start();
               // migMsgRec.start();
                migLocMsgRec.start();
                
                //dynCDLocMsgRec.start();
                dynCDMsgRec.start();
        		
        		
        	} else {
        		
        		Thread expirychecker = new Thread(new NoP2PServRegExpiryCheckerThread());
                //Thread remmsgreceiver = new Thread(new NoP2PRemoteServMessageReceiverThread());
                Thread locmsgreceiver = new Thread(new NoP2PLocalServMessageReceiverThread());
                Thread msgsender = new Thread(new NoP2PServMessageSenderThread());
               // Thread migMsgRec = new Thread(new MigrationAndLinkReqMsgRecThread());
                Thread migLocMsgRec = new Thread(new MigrationAndLinkLocalReqMsgRecThread());
                //Thread dynCDLocMsgRec = new Thread(new DynamicCDLocalReqReceiverThread());
                Thread dynCDMsgRec = new Thread(new DynamicCDReqReceiverThread());
               
                expirychecker.start();
                msgsender.start();
                //remmsgreceiver.start();
                locmsgreceiver.start();
               // migMsgRec.start();
                migLocMsgRec.start();
                
                //dynCDLocMsgRec.start();
                dynCDMsgRec.start();
        		
        		
        	}
        	
            
            
        }
        
        private static void StartSOSJNoSOThread(){
            //Thread migMsgRec = new Thread(new MigrationAndLinkReqMsgRecThread());
            //Thread migLocMsgRec = new Thread(new MigrationAndLinkLocalReqMsgRecThread());
            //Thread dynCDLocMsgRec = new Thread(new DynamicCDLocalReqReceiverThread());
            Thread dynCDMsgRec = new Thread(new DynamicCDReqReceiverThread());
            //Thread dynCDRemMsgRec = new Thread(new DynamicCDRemoteReqReceiverThread());
            
            //migMsgRec.start();
           //migLocMsgRec.start();
            //dynCDLocMsgRec.start();
            dynCDMsgRec.start();
            //dynCDRemMsgRec.start();
            
        }
        
        
        private static void StartSOARegThread(){
            Thread expirychecker = new Thread(new RegExpiryCheckerThread());
            //Thread regmsgreceiver = new Thread(new RegRemoteMessageReceiverThread());
            Thread locregmsgreceiver = new Thread(new RegReceiver());
            Thread locregdiscmsgreceiver = new Thread (new RegDiscReceiver());
            //Thread remregdiscmsgreceiver = new Thread(new RegRemoteDiscMessageReceiverThread());
            Thread regmsgsender = new Thread(new RegSender());
            
            
            expirychecker.start();
            regmsgsender.start();
            locregdiscmsgreceiver.start();
           // remregdiscmsgreceiver.start();
            //regmsgreceiver.start();
            locregmsgreceiver.start();
            
        }
        
        private static void StartOPC_UA_LDS(){
        	Thread locregmsgreceiver = new Thread(new RegReceiver());
        	Thread regmsgsender = new Thread(new RegSender());
        	
        	//need to start timer to regularly trigger FindServer in LDS
        	
        	periodicFindServerQueryExec = Executors.newScheduledThreadPool(2);
        	
        	
        	TimerTask periodServ = new TimerTask() {

            	//private String LDS_Addr = "localhost";
            	
    			public void run() {
    				// TODO Auto-generated method stub
    				
    				FindServersClient findServCl = new FindServersClient();
    				
    				try {
    					findServCl.outputFindServersOnNetwork("opc.tcp://"+LDS_ADDR+":4840/discovery")
    					      .thenCompose(aVoid -> findServCl.outputFindServers("opc.tcp://"+LDS_ADDR+":4840/discovery"))
    					.get();
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (ExecutionException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				
    				
    			}
    			
            };
        	
           
        	
        	// periodic execution to check if there are new devices
        	
        	periodicFindServerQueryExec.scheduleAtFixedRate(periodServ,2, 60, TimeUnit.SECONDS);
        	
        	// create an OPC UA client to trigger FindServer
        	
        }
        
       // private static void StartSigChanReconfigurator(){
      //      Thread sigrec = new Thread(new SignalChannelReconfiguratorBak());
       //     sigrec.start();
      //  }
     
        //16 Sept 2014 --> For SPOT registry specific thread, forward messages from Non-SunSPOT network to SunSPOT network
        private static void StartSPOTRegGatewayThread(){
            
        }
        
        private static void SetSSName(){
            
        }
        
        /*
        private class PeriodicFindServerQuery implements Runnable {

        	private String LDS_Addr = "localhost";
        	
        	public void init(String LDS_Addr) {
				// TODO Auto-generated constructor stub
        		this.LDS_Addr = LDS_Addr;
        		
			}
        	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				FindServersClient findServCl = new FindServersClient();
				
				try {
					findServCl.outputFindServersOnNetwork("opc.tcp://"+LDS_Addr+":4840/discovery")
					      .thenCompose(aVoid -> findServCl.outputFindServers("opc.tcp://"+LDS_Addr+":4840/discovery"))
					.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
        	
        }
        */
        
       // private static void StartSOAGUI(){
       //     Thread gui = new Thread(new SOSJGUI()); 
       //     gui.start();
       // }
}
