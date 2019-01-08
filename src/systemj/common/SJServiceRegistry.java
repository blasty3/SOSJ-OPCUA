package systemj.common;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.me.*;
import systemj.common.SOAFacility.Support.SOABuffer;

public class SJServiceRegistry {
   
    private static JSONObject currentServiceRegistry = new JSONObject();
    
    
    
    private static JSONObject currentCD_OPC_UA_Registry = new JSONObject();

    private static JSONObject serviceExpiryLength = new JSONObject();
    private final static Object ServExpiryLengthLock = new Object();
    private static JSONObject serviceAdvertisementReceivedTime = new JSONObject();
    private final static Object serviceAdvertisementReceivedTimeLock = new Object();
    
    private final static Object CurrentRegistryLock = new Object();
    
    private final static Object CurrentCD_OPC_UA_RegistryLock = new Object();
    
    
    private static Vector newServers = new Vector();
    private final static Object newServersLock = new Object();

    
    //private final static Object RegistryParsingStatLock=new Object();

    public static void AppendNodeServicesToCurrentRegistry(JSONObject js) throws JSONException{
        
        //if (IsInternalServ){
            
            synchronized (CurrentRegistryLock){
            	
                currentServiceRegistry.put(SJSSCDSignalChannelMap.getLocalSSName(),js);
            }
          
        //} 
    }
    
    public static void AddServicesToGSR(JSONObject js) throws JSONException{
        
        //if (IsInternalServ){
            
            synchronized (CurrentRegistryLock){
            	
            	Enumeration jsKeys = js.keys();
            	
            	while(jsKeys.hasMoreElements()) {
            		
            		String ssNameOfDesc = jsKeys.nextElement().toString();
            		
            		JSONObject jsServDesc = js.getJSONObject(ssNameOfDesc);
            		
            		currentServiceRegistry.put(ssNameOfDesc, jsServDesc);
            		
            	}
            	
                //currentServiceRegistry.put(,js);
            }
          
        //} 
    }
    
    public static void AddOPCUAServerToRegistryApplicationDescription(String ApplicationURI, String ApplicationName, String ProductURI, String ApplicationType, String[] DiscoveryURLs) throws JSONException{
        
        //if (IsInternalServ){
            
            synchronized (CurrentCD_OPC_UA_RegistryLock){
            	
            	if(currentCD_OPC_UA_Registry.has(ApplicationURI)) {
            		
            		
            	} else {
            		JSONObject jsChild = new JSONObject();
            		JSONObject jsDiscUrls = new JSONObject();
            		
            		if (DiscoveryURLs != null) {
                        //logger.info("\tDiscovery URLs:");
            			int index = 0;
                        for (String url : DiscoveryURLs) {
                          //  logger.info("\t\t" + url);
                        	jsDiscUrls.put(Integer.toString(index), url);
                        	index++;
                        }
                    }
            		
                	jsChild.put("ApplicationURI", ApplicationURI);
                	jsChild.put("ApplicationName", ApplicationName);
                	jsChild.put("ProductURI", ProductURI);
                	jsChild.put("ApplicationType", ApplicationType);
                	jsChild.put("DiscoveryURLs", jsDiscUrls);
                	
                	currentCD_OPC_UA_Registry.put(ApplicationURI,jsChild);
                	
                	newServers.addElement(ApplicationURI);
                	
            	}
            	
            	
            }
          
        //} 
    }
    
    public static Vector GetNewServers() {
    	return newServers;
    }
    
    public static void ClearNewServersList() {
    	newServers.clear();
    }
    
    public static boolean IsNewServersListEmpty() {
    	if (newServers.size()>0) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    
    
    public static void AddOPCUAServerToRegistryServerNetwork(String ServerName, String RecordID, String DiscoveryURL, String[] Capabilities) throws JSONException{
        
        //if (IsInternalServ){
            
            synchronized (CurrentCD_OPC_UA_RegistryLock){
            	
            	if(currentCD_OPC_UA_Registry.has(ServerName)) {
            		
            		
            		
            	} else {
            		
            		JSONObject jsChild = new JSONObject();
            		JSONObject jsCapabilities = new JSONObject();
            		
            		if (Capabilities != null) {
                        //logger.info("\tDiscovery URLs:");
            			int index = 0;
                        for (String capability : Capabilities) {
                          //  logger.info("\t\t" + url);
                        	jsCapabilities.put(Integer.toString(index), capability);
                        	index++;
                        }
                    }
            		
                	jsChild.put("RecordID", RecordID);
                	jsChild.put("DiscoveryURL", DiscoveryURL);
                	
                	jsChild.put("Capabilities", jsCapabilities);
                	
                	currentCD_OPC_UA_Registry.put(ServerName,jsChild);
                	
                	
            		
            	}
            	
            	
            }
          
        //} 
    }

    public static void RemoveServiceOfCD(String CDName){
        try {
            JSONObject jsInt = obtainInternalRegistry();
            
            Enumeration servIndKeys = jsInt.keys();
            
            while(servIndKeys.hasMoreElements()){
                
                String servInd = servIndKeys.nextElement().toString();
                
                JSONObject jsServDet = jsInt.getJSONObject(servInd);
                
                String assocCDName = jsServDet.getString("associatedCDName");
                
                if(assocCDName.equals(CDName)){
                    jsInt.remove(servInd);
                    
                    SOABuffer.removeAdvStatOfServName(servInd);
                    
                }
                
            }
            
            UpdateAllInternalRegistry(jsInt);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean HasNonLocalServiceCD(String cdname){
        
        boolean stat = false;
        
        try {
            JSONObject jsServ = SJServiceRegistry.obtainCurrentRegistry();
            
            Enumeration keysJsServ = jsServ.keys();
            
            while(keysJsServ.hasMoreElements()){
                
                String keyJsServ = keysJsServ.nextElement().toString();
                
                if(!keyJsServ.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                    JSONObject jsIndivServs = jsServ.getJSONObject(keyJsServ);
                    
                    Enumeration keysServName = jsIndivServs.keys();
                    
                    while(keysServName.hasMoreElements()){
                        
                        String ServName = keysServName.nextElement().toString();
                        
                        JSONObject jsIndivServ = jsIndivServs.getJSONObject(ServName);
                        
                        String CDName = jsIndivServ.getString("associatedCDName");
                        
                        if(CDName.equals(cdname)){
                            stat = true;
                        }
                        
                    }
                    
                }
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            
        }
        return stat;
    }
    
    public static boolean IsCDFromRemoteSS(String cdname, String SS){
        
        boolean stat = false;
        
        try {
            JSONObject jsServ = SJServiceRegistry.obtainCurrentRegistry();
            
            Enumeration keysJsServ = jsServ.keys();
            
            while(keysJsServ.hasMoreElements()){
                
                String keyJsServ = keysJsServ.nextElement().toString();
                
                if(!keyJsServ.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                    JSONObject jsIndivServs = jsServ.getJSONObject(keyJsServ);
                    
                    Enumeration keysServName = jsIndivServs.keys();
                    
                    while(keysServName.hasMoreElements()){
                        
                        String ServName = keysServName.nextElement().toString();
                        
                        JSONObject jsIndivServ = jsIndivServs.getJSONObject(ServName);
                        
                        String CDName = jsIndivServ.getString("associatedCDName");
                        
                        if(CDName.equals(cdname) && keyJsServ.equals(SS)){
                            stat = true;
                        }
                        
                    }
                    
                }
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            
        }
        
        return stat;
    }
    
    public static String GetCDRemoteSSLocation(String cdname){
        
        String stat = "";
        
        try {
            
            JSONObject jsServ = RegAllCDStats.getAllCDStats();
            
          
            Enumeration keysJsServ = jsServ.keys();
            
            while(keysJsServ.hasMoreElements()){
                
                String keyJsServ = keysJsServ.nextElement().toString();
                
                if(!keyJsServ.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                    JSONObject jsIndivServs = jsServ.getJSONObject(keyJsServ);
                    
                    Enumeration keysCDName = jsIndivServs.keys();
                    
                    while(keysCDName.hasMoreElements()){
                        
                        String keyCDName = keysCDName.nextElement().toString();
                        
                        //JSONObject jsIndivServ = jsIndivServs.getJSONObject(ServName);
                        
                        //String CDName = jsIndivServ.getString("associatedCDName");
                        
                        if(keyCDName.equals(cdname) ){
                            //stat = keyJsServ;
                            stat = keyJsServ;
                        }
                        
                    }
                    
                }
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            
        }
        
        return stat;
    }
    
    public static void RemoveUnavailableNodeServicesFromCurrentRegistryOfSSName(String SSName){
       // try {
            synchronized (CurrentRegistryLock) {
            if (currentServiceRegistry.has(SSName)){
                
               
                //System.out.println("SJServiceRegistry, prev SR: " +currentDetailedServiceRegistry);
                
                    currentServiceRegistry.remove(SSName);
                
                //System.out.println("SJServiceRegistry, curr SR: " +currentDetailedServiceRegistry);
                //synchronized regnodavail here
               
            } else {
                System.out.println("SJServiceRegistry, RemoveUnavailableNodeServicesOfIPAddressFromCurrentRegistry : IP address not listed in the registry, not removing");
            }
            
            }
          
    }

    public static JSONObject obtainInternalRegistry() throws JSONException{
        
        JSONObject intReg = new JSONObject();
        
        //System.out.println("obtained internal Registry: " +currentDetailedServiceRegistry.getJSONObject("Node0").toPrettyPrintedString(2, 0));
        if (!currentServiceRegistry.toString().equalsIgnoreCase("{}")){
            
            synchronized (CurrentRegistryLock){
                intReg= currentServiceRegistry.getJSONObject(SJSSCDSignalChannelMap.getLocalSSName());
            }
            
            //System.out.println("SJServiceRegistry Current Registry: " +currentDetailedServiceRegistry.toPrettyPrintedString(2, 0));
            return intReg;
        } else {
            return new JSONObject();
        } 
    }
    
    public static JSONObject obtainInternalRegistryForSOSJOPCUAGSR() throws JSONException{
        
        JSONObject intReg = new JSONObject();
        
        //System.out.println("obtained internal Registry: " +currentDetailedServiceRegistry.getJSONObject("Node0").toPrettyPrintedString(2, 0));
        if (!currentServiceRegistry.toString().equalsIgnoreCase("{}")){
            
            synchronized (CurrentRegistryLock){
            	
            	String ssName = SJSSCDSignalChannelMap.getLocalSSName();
            	JSONObject jsAllServ = currentServiceRegistry.getJSONObject(SJSSCDSignalChannelMap.getLocalSSName());
            	
            	intReg.put(ssName,jsAllServ);
            	
                //intReg= currentServiceRegistry.getJSONObject(SJSSCDSignalChannelMap.getLocalSSName());
            }
            
            //System.out.println("SJServiceRegistry Current Registry: " +currentDetailedServiceRegistry.toPrettyPrintedString(2, 0));
            return intReg;
        } else {
            return new JSONObject();
        } 
    }
    
    public static JSONObject obtainCurrentRegistry() throws JSONException{
        //System.out.println("current Registry: " +currentDetailedServiceRegistry.toPrettyPrintedString(2, 0));
        
        JSONObject jsAllCurr = new JSONObject();
        
        synchronized (CurrentRegistryLock){
            jsAllCurr = currentServiceRegistry;
        }
        
        
        return jsAllCurr;
        
    }
    
  
    public static void UpdateAllInternalRegistry(JSONObject js){
        
        synchronized(CurrentRegistryLock){
            try {
                //currentDetailedServiceRegistry.remove(SJSSCDSignalChannelMap.getLocalSSName());
                currentServiceRegistry.put(SJSSCDSignalChannelMap.getLocalSSName(), js);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
 
    //need to get node with expired advertisement
    
    
    /**
     * checking expiry to request advertise
     * @return 
     */
    
    
 
    public static synchronized long getOwnAdvertisementTimeLimit(){
        long[] num = new long[1000];
        long min=0;
        int i=0;
        long t=5000; //just in case tolerance is not yet known, standard minimum for advertisementExpiryTime
        try {
            if(!obtainCurrentRegistry().isEmpty()){
                
                JSONObject js = obtainInternalRegistry();
                //get all services expirytime and choose the least time for advertisement period
                Enumeration keys = js.keys();
          
                while (keys.hasMoreElements()){
                    Object key = keys.nextElement();

                    JSONObject js1 = (JSONObject)js.get(key.toString());
                    //System.out.println("advertisement: " +js1.get("advertisementExpiry").toString());
                    //t=Long.parseLong(js1.get("AdvertisementExpiry").toString());
                    
                    //if (js1.getString("serviceRole").equalsIgnoreCase("provider")){
                    if(js1.has("expiryTime")){
                        num[i] = Long.parseLong(js1.get("expiryTime").toString());
                   // System.out.println("num got " +num[i]);
                        i++;
                    }
                    
                        
                    //}
                    
                    
                }
                
                //t = minTimeValue(num);
                //System.out.println("Advertisement got " +t);
            }
            
            num[i] = SJSSCDSignalChannelMap.GetSSExpiryTime();
            
            
            for (int ktr = 0; ktr < num.length; ktr++) {
                             
                            if (ktr==0){
                                min = num[ktr];
                            } else if (num[ktr] < min) {
                                min = num[ktr];
                            } 
                        }

        } catch (JSONException ex) {
            System.out.println("no advertisement expiry info: " +ex.getMessage());
        }
        
        if (min!=0){
            return min;
        } else {
            return t;
        }
        
        
    }

    
    
    public static void SaveDiscoveredServicesNoP2P(JSONObject js){
       
        //System.out.println("Advertised service Prior to filtering: " +js);
        
        try {

           
            
                if (!js.isEmpty()){
                    
                    //InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
                        
                    //Interconnection ic = im.getInterconnection();
                    
                    synchronized (CurrentRegistryLock){
                
                        Enumeration keysSS = js.keys();
                        
                        while(keysSS.hasMoreElements()){
                            
                            String SSName = keysSS.nextElement().toString();
                            
                            JSONObject MatchingServsInSS = js.getJSONObject(SSName);
                            
                            Enumeration keysMatchingServs = MatchingServsInSS.keys();
                            
                            //JSONObject js3 = new JSONObject();
                            
                            while(keysMatchingServs.hasMoreElements()){
                                
                                String keyServName = keysMatchingServs.nextElement().toString();
                                
                                if(currentServiceRegistry.has(SSName)){
                                    JSONObject allServInSS = currentServiceRegistry.getJSONObject(SSName);
                                    
                                    allServInSS.put(keyServName,MatchingServsInSS.getJSONObject(keyServName));
                                    currentServiceRegistry.remove(SSName);
                                    currentServiceRegistry.put(SSName, allServInSS);
                                } else {
                                    currentServiceRegistry.put(SSName, MatchingServsInSS);
                                }
                                
                            }
                            
                        }
                        
                    }
                
                
                }
                
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public static void SaveAdvertisedServices(JSONObject js){
       
        try {

            JSONObject servAdvList = (JSONObject) js.get("serviceList");
             long advertisementLength = Long.parseLong(js.getString("expiryTime"));
            //JSONObject filteredServList = new JSONObject();
           
             //  filteredServList = servAdvList;

               boolean IsNewSSServAdded = false;
                
                    synchronized (CurrentRegistryLock){
                
                        if (currentServiceRegistry.has(js.getString("associatedSS"))){
                            currentServiceRegistry.remove(js.getString("associatedSS"));
                        } else {
                            IsNewSSServAdded = true;
                        } 
                        currentServiceRegistry.put(js.getString("associatedSS"),servAdvList);
                    }
                
                synchronized (serviceAdvertisementReceivedTimeLock){
                    
                    if(serviceAdvertisementReceivedTime.has(js.getString("associatedSS"))){
                        serviceAdvertisementReceivedTime.remove(js.getString("associatedSS"));
                    }
                    serviceAdvertisementReceivedTime.put(js.getString("associatedSS"),System.currentTimeMillis());
                } 
                            synchronized (ServExpiryLengthLock){
                                if(serviceExpiryLength.has(js.getString("associatedSS"))){
                                    serviceExpiryLength.remove(js.getString("associatedSS"));
                                } 
                                serviceExpiryLength.put(js.getString("associatedSS"),advertisementLength);
                            }
                JSONObject AllCDStat = js.getJSONObject("CDStats");
                RegAllCDStats.UpdateCDStat(js.getString("associatedSS"), AllCDStat);
                RegAllSSAddr.AddSSAddr(js.getString("associatedSS"), js.getString("SSAddr"));
                boolean notifySSStat = Boolean.parseBoolean(js.getString("Notify"));
                boolean notifyChangedCDStat = Boolean.parseBoolean(js.getString("changedCDStat"));
                if(notifySSStat || notifyChangedCDStat || IsNewSSServAdded){
                    
                    SOABuffer.SetRegNotifySS(true);
                }
                    /*
                    if(notifySSStat){
                        SOABuffer.SetNotifyChangedTotalSS(true);
                    }
                
                   if(notifyChangedCDStat){
                        SOABuffer.SetNotifyChangedCDStat(true);
                    }
                   */
              //  }
                
                //if(IsNewSSServAdded){
                //    SOABuffer.SetRegNotifySS(true);
                    //SOABuffer.SetNotifyChangedCDStat(true);
                    //SOABuffer.SetNotifyChangedTotalSS(true);
               // }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
     public static Hashtable checkServiceExpiry(){
        
        int index=1;
        
        Hashtable ExpAddrList = new Hashtable();
      
        JSONObject jsServAdvRecTime = new JSONObject();
        
        synchronized(serviceAdvertisementReceivedTimeLock){
            
            Enumeration keysServAdvRecTime = serviceAdvertisementReceivedTime.keys();
            
            while(keysServAdvRecTime.hasMoreElements()){
                String keyServAdvTime = keysServAdvRecTime.nextElement().toString();
                
                try {
                    jsServAdvRecTime.put(keyServAdvTime, serviceAdvertisementReceivedTime.getLong(keyServAdvTime));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            }
           
        }
        
        Enumeration keys = jsServAdvRecTime.keys(); //this will get each sourceIPaddress
          
          while (keys.hasMoreElements()){
              Object key = keys.nextElement();
            try {
                long deltaT = System.currentTimeMillis()-jsServAdvRecTime.getLong(key.toString());
                
                //if (deltaT>=0.8*serviceExpiryLength.getLong(key.toString())){
                    
               //     expiredServiceAddr=key.toString();
                    
              //  } else 
                
                JSONObject jsServExpLength = new JSONObject();
                JSONObject currReg = obtainCurrentRegistry();
                
                synchronized (ServExpiryLengthLock){
                    
                    Enumeration keysServExpiryLength = serviceExpiryLength.keys();
                    
                    while(keysServExpiryLength.hasMoreElements()){
                        
                        String keyServExpiry = keysServExpiryLength.nextElement().toString();
                        
                        jsServExpLength.put(keyServExpiry, serviceExpiryLength.getLong(keyServExpiry));
                        
                    }
                    
                    
                }
                
                

                if (jsServExpLength.has(key.toString())){
                    if (deltaT>jsServExpLength.getLong(key.toString())){
                    
                        if (currReg.has(key.toString())){    
                        //expiredServiceAddr = key.toString();
                        ExpAddrList.put(Integer.toString(index),key.toString());
                        
                        synchronized (ServExpiryLengthLock){
                            serviceExpiryLength.remove(key.toString());
                        }

                        if (jsServAdvRecTime.has(key.toString())){
                        
                            synchronized(serviceAdvertisementReceivedTimeLock){
                                serviceAdvertisementReceivedTime.remove(key.toString());
                            }
   
                        }
                    }
                    //servExpired = true;
                    //registeredNodeAvailability.put(key.toString(), "Unavailable");
                    //currentDetailedServiceRegistry.remove(key.toString());
                    }
                }
                
             }  catch (JSONException ex) {
                //Logger.getLogger(SJServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
                 System.err.println("SJServiceRegistry,checkServiceExpiry JSONException:  " +ex.getMessage());
                 ex.printStackTrace();
             }
          }
        
          return ExpAddrList;
          
    }
    
     public static Hashtable checkServiceExpiryForAdvertiseRequest(){
        
        int index=1;
        
        Hashtable ExpAddrList = new Hashtable();
        
        //String expiredServiceAddr =null;

       // long currenttime = System.currentTimeMillis();
        
        JSONObject jsServAdvRecTime = new JSONObject();
        
        synchronized(serviceAdvertisementReceivedTimeLock){
            
            Enumeration keysServAdvRecTime = serviceAdvertisementReceivedTime.keys();
            
            while(keysServAdvRecTime.hasMoreElements()){
                String keyServAdvTime = keysServAdvRecTime.nextElement().toString();
                
                try {
                    jsServAdvRecTime.put(keyServAdvTime, serviceAdvertisementReceivedTime.getLong(keyServAdvTime));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            }
            //jsServAdvRecTime = serviceAdvertisementReceivedTime;
        }
        
        
        
        Enumeration keys = jsServAdvRecTime.keys(); //this will get each sourceIPaddress
          
          while (keys.hasMoreElements()){
              Object key = keys.nextElement();
            try {
                long deltaT = System.currentTimeMillis()-jsServAdvRecTime.getLong(key.toString());
                
                
                
                JSONObject jsServExpLength = new JSONObject();
                JSONObject currReg = obtainCurrentRegistry();
                
                synchronized (ServExpiryLengthLock){
                    
                    Enumeration keysServExpiryLength = serviceExpiryLength.keys();
                    
                    while(keysServExpiryLength.hasMoreElements()){
                        
                        String keyServExpiry = keysServExpiryLength.nextElement().toString();
                        
                        jsServExpLength.put(keyServExpiry, serviceExpiryLength.getLong(keyServExpiry));
                        
                    }
                    
                     // jsServExpLength = serviceExpiryLength;
                }
                
                

                if (jsServExpLength.has(key.toString())){
                    if (deltaT>0.8*jsServExpLength.getLong(key.toString())){
                    
                        if (currReg.has(key.toString())){    
                        //expiredServiceAddr = key.toString();
                        ExpAddrList.put(Integer.toString(index),key.toString());
                        
                        
                    }
                    //servExpired = true;
                    //registeredNodeAvailability.put(key.toString(), "Unavailable");
                    //currentDetailedServiceRegistry.remove(key.toString());
                    }
                }
                
             }  catch (JSONException ex) {
                //Logger.getLogger(SJServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
                 System.err.println("SJServiceRegistry,checkServiceExpiry JSONException:  " +ex.getMessage());
                 ex.printStackTrace();
             }
          }
          /*
          if (expiredServiceAddr==null){
              return "nothing";
          } else {
              return expiredServiceAddr;
          }
          */
          return ExpAddrList;
          
    }
    
    public static void setParsingStatus(boolean stat){
        
        synchronized(RegistryParsingStatLock){
            parsingDone=stat;
        }
        
    }
    
    public static boolean getParsingStatus(){
        
        
        synchronized (RegistryParsingStatLock){
            
            return parsingDone;
            
        }
        
    }
    
    private static boolean parsingDone=false;
    private final static Object RegistryParsingStatLock=new Object();
   
    /*
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
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
     /*
    //method to save registry to a file, features MAY NOT be available on smaller java platform
    public static void WriteCurrentServiceRegistryToFile(JSONObject registry) throws IOException{

		FileWriter file = new FileWriter("CurrentReg.txt");
                
		file.write(registry.toString());
		file.flush();
		file.close();
    }

    public static JSONObject ReadCurrentServiceRegistryFromFile() throws IOException {
        JSONObject js;
        BufferedReader br = new BufferedReader(new FileReader("CurrentReg.txt"));
        try {
        StringBuilder sb = new StringBuilder();
        String line;
        
            line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
        js = (JSONObject) (Object)sb;
        
        //String everything = sb.toString();
        } finally {
            br.close();
        }
        return js;
    }
    */

}