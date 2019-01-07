package systemj.common.opcua_milo;

import java.util.Hashtable;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import systemj.common.opcua_milo.MiloServerSSHandler;

import systemj.bootstrap.ClockDomain;

public class OPCUAClientServerObjRepo {
	
	private static Hashtable OpcUaClientsSS = new Hashtable();
	private static Hashtable OpcUaServersSS = new Hashtable();
	
	private static Hashtable OpcUaClientsCD = new Hashtable();
	private static Hashtable OpcUaServersCD = new Hashtable();
	
	private final static Object OpcUaClientsSSLock = new Object();
	private final static Object OpcUaServersSSLock = new Object();
	
	private final static Object OpcUaClientsCDLock = new Object();
	private final static Object OpcUaServersCDLock = new Object();
	
	
	// For SS
	
	public static void AddClientObjSS(String CLName, ClientRunner OPCUAClientRunnerObj){
        synchronized(OpcUaClientsSSLock){
        	OpcUaClientsSS.put(CLName, OPCUAClientRunnerObj);
        }
    }
	
	public static void RemoveClientObjSS(String CLName){
        synchronized(OpcUaClientsSSLock){
        	OpcUaClientsSS.remove(CLName);
        }
    }
	
	
	
	
	public static void AddServerObjSS (String SEName, MiloServerSSHandler MiloServerObj){
        synchronized(OpcUaServersSSLock){
        	OpcUaServersSS.put(SEName, MiloServerObj);
        }
    }
	
	public static void RemoveServerObjSS (String SEName){
        synchronized(OpcUaServersSSLock){
        	OpcUaServersSS.remove(SEName);
        }
    }
	
	
	//For CD Client
	
	
	public static void AddClientObjCD(String CLCDName, String CLSigName, ClientRunner OPCUAClientRunnerObj){
        synchronized(OpcUaClientsCDLock){
        	
        	OpcUaClientsCD.put(CLCDName+":"+CLSigName,OPCUAClientRunnerObj);
        	
        	/*
        	if(OpcUaClientsCD.containsKey(CLCDName)) {
        		
        		Hashtable clcdnameSigs = (Hashtable) OpcUaClientsCD.get(CLCDName);
        		
        		clcdnameSigs.put(CLSigName, OPCUAClientRunnerObj);
        		
        		OpcUaClientsCD.put(CLCDName,clcdnameSigs);
        		
        	} else {
        		
        		Hashtable clcdnameSigs = new Hashtable();
        		clcdnameSigs.put(CLSigName, OPCUAClientRunnerObj);
        		
        		OpcUaClientsCD.put(CLCDName,clcdnameSigs);
        		
        	}
        	*/
        	
        	//OpcUaClientsCD.put(CLCDName, OPCUAClientRunnerObj);
        }
    }
	
	public static void RemoveClientObjCD(String CLCDName, String sigName){
        synchronized(OpcUaClientsCDLock){
        	
        	OpcUaClientsCD.remove(CLCDName+":"+sigName);
        	
        	/*
        	if(OpcUaClientsCD.containsKey(CLCDName)) {
        		
        		Hashtable clcdnameSigs = (Hashtable) OpcUaClientsCD.get(CLCDName);
        		
        		clcdnameSigs.remove(CLName);
        		
        		OpcUaClientsCD.put(CLCDName, clcdnameSigs);
        		
        	}
        	*/
        	
        }
    }
	
	public static ClientRunner GetClientObjCD(String CLCDName, String CLName){
        synchronized(OpcUaClientsCDLock){
        	
        	
        	return (ClientRunner) OpcUaClientsCD.get(CLCDName+":"+CLName);
        	
        	/*
        	if(OpcUaClientsCD.containsKey(CLCDName)) {
        		Hashtable hashSigList = (Hashtable)OpcUaClientsCD.get(CLCDName);
        		return (ClientRunner) hashSigList.get(CLName);
        		
        	} else {
        		return new ClientRunner();
        	}
        	*/
        	
        	
        }
    }
	
	
	//for CD Server
	
	public static void AddServerObjCD(String SEName, MiloServerCDHandler MiloServerObj){
        synchronized(OpcUaServersCDLock){
        	OpcUaServersCD.put(SEName, MiloServerObj);
        }
    }
	
	public static MiloServerCDHandler GetServerObjCD(String SEName){
        synchronized(OpcUaServersCDLock){
        	return (MiloServerCDHandler) OpcUaServersCD.get(SEName);
        }
    }
	
	public static void RemoveServerObjCD(String SEName){
        synchronized(OpcUaServersCDLock){
        	OpcUaServersCD.remove(SEName);
        }
    }

}
