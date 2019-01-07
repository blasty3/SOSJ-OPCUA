/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA.output;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJSOAMessage;
import systemj.common.opcua_milo.ClientRunner;
import systemj.common.opcua_milo.MiloServerCDHandler;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.common.opcua_milo.SOSJOPCUAServerNamespaceForCD;
import systemj.interfaces.GenericSignalSender;

/**
 *
 * @author Atmojo
 */
public class LocalWriteOPCUA extends GenericSignalSender{

   
    String NameToWrite = null;
    String CDName = null;
    
    UShort ush = Unsigned.ushort(2);
    
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("NameToWrite")){
        	NameToWrite = (String)data.get("NameToWrite");
        }
        
        if(data.containsKey("CDName")){
            CDName = (String)data.get("CDName");
        }
        
    }

    @Override
    public void run() {
        
    	Object[] obj = super.buffer;
		String data = (String) obj[1];
		
		// 02.01.2019 Udayanto
		//before hand, (re-)configuration of the client should occur first at the application level.
		
		MiloServerCDHandler miloserv_hand = OPCUAClientServerObjRepo.GetServerObjCD(cdname);
        //obj[0] = Boolean.TRUE;
        //obj[1] = jsAllServs.toString();
	    OpcUaServer miloserv = miloserv_hand.getServer();
	    
	   
	    SOSJOPCUAServerNamespaceForCD sosjnamespace = (SOSJOPCUAServerNamespaceForCD) miloserv.getNamespaceManager().getNamespace(ush);
	    
	    UaVariableNode signalStatusNode = sosjnamespace.GetNodeObjFromStorage(NameToWrite+":Status");
	    UaVariableNode signalValueNode = sosjnamespace.GetNodeObjFromStorage(NameToWrite+":Value");
	    
	    signalStatusNode.setValue(new DataValue(new Variant(true)));
	    signalValueNode.setValue(new DataValue(new Variant(data)));
	    
	    sosjnamespace.AddNodeObjToStorage(NameToWrite+":Status", signalStatusNode);
	    sosjnamespace.AddNodeObjToStorage(NameToWrite+":Value", signalValueNode);
	    
        
    }
    
    
}
