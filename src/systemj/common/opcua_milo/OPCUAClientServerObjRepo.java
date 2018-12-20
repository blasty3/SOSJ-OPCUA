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
	
	
	//For CD
	
	
	public static void AddClientObjCD(String CLName, ClientRunner OPCUAClientRunnerObj){
        synchronized(OpcUaClientsCDLock){
        	OpcUaClientsCD.put(CLName, OPCUAClientRunnerObj);
        }
    }
	
	public static void RemoveClientObjCD(String CLName){
        synchronized(OpcUaClientsCDLock){
        	OpcUaClientsCD.remove(CLName);
        }
    }
	
	
	
	
	public static void AddServerObjCD(String SEName, MiloServerCDHandler MiloServerObj){
        synchronized(OpcUaServersCDLock){
        	OpcUaServersCD.put(SEName, MiloServerObj);
        }
    }
	
	public static void RemoveServerObjCD(String SEName){
        synchronized(OpcUaServersCDLock){
        	OpcUaServersCD.remove(SEName);
        }
    }

}
