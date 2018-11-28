package systemj.common.opcua_milo;

import java.util.Hashtable;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import systemj.common.opcua_milo.MiloServerHandler;

import systemj.bootstrap.ClockDomain;

public class OPCUAClientServerObjRepo {
	
	private static Hashtable OpcUaClients = new Hashtable();
	private static Hashtable OpcUaServers = new Hashtable();
	
	private final static Object OpcUaClientsLock = new Object();
	private final static Object OpcUaServersLock = new Object();
	
	
	
	public static void AddClientObj(String CLName, ClientRunner OPCUAClientRunnerObj){
        synchronized(OpcUaClientsLock){
        	OpcUaClients.put(CLName, OPCUAClientRunnerObj);
        }
    }
	
	public static void RemoveClientObj(String CLName){
        synchronized(OpcUaClientsLock){
        	OpcUaClients.remove(CLName);
        }
    }
	
	
	
	
	public static void AddServerObj(String SEName, MiloServerHandler MiloServerObj){
        synchronized(OpcUaServersLock){
        	OpcUaServers.put(SEName, MiloServerObj);
        }
    }
	
	public static void RemoveServerObj(String SEName){
        synchronized(OpcUaServersLock){
        	OpcUaServers.remove(SEName);
        }
    }

}
