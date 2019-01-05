/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA.input;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Optional;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJRegistryEntry;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.opcua_milo.ClientRunner;
import systemj.common.opcua_milo.MiloServerCDHandler;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.common.opcua_milo.SOSJOPCUAServerNamespaceForCD;
import systemj.common.opcua_milo.SOSJSignalOPCUAReadSharedVariables;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Atmojo
 */
public class RemoteReadOPCUA extends GenericSignalReceiver{

    String cdname = null;
    String signalname = null;
    
    UShort ush = Unsigned.ushort(2);
    
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("CDName")){
            cdname = (String)data.get("CDName");
        } else {
            throw new RuntimeException("the 'CDName' attribute is required");
        }
        
        if(data.containsKey("Name")){
            signalname = (String)data.get("Name");
        } else {
            throw new RuntimeException("the signalname attribute 'Name' is required");
        }
        
        
    }
    
    

    @Override
    public void run(){
        
       
        
    }
    
    public RemoteReadOPCUA(){
		super(); // Initializes the buffer
    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		Object[] obj = new Object[2];
		
		ClientRunner clrun = OPCUAClientServerObjRepo.GetClientObjCD(cdname, signalname);
		
		clrun.SetReadOrWrite(true);
		
	    clrun.InstantiateClient();
	    
	    clrun.SetSignalValue("");

	    clrun.run();
	    
	    boolean signalStatus = SOSJSignalOPCUAReadSharedVariables.GetSignalStatusToRead(signalname+":Status");
	    String signalValue = SOSJSignalOPCUAReadSharedVariables.GetSignalValueToRead(signalname+":Value");
	    
		if(signalStatus) {
			obj[0] = Boolean.TRUE;
            obj[1] = signalValue;
            super.setBufferIncomingValue(obj);
            SOSJSignalOPCUAReadSharedVariables.ResetSignalStatus(signalname);
		} else {
			
			obj[0] = Boolean.FALSE;
            obj[1] = signalValue;
            super.setBuffer(obj);
		}
		/*
        MiloServerCDHandler miloserv_hand = OPCUAClientServerObjRepo.GetServerObjCD(cdname);
            //obj[0] = Boolean.TRUE;
            //obj[1] = jsAllServs.toString();
        OpcUaServer miloserv = miloserv_hand.getServer();
        
       
        SOSJOPCUAServerNamespaceForCD sosjnamespace = (SOSJOPCUAServerNamespaceForCD) miloserv.getNamespaceManager().getNamespace(ush);
        
        UaVariableNode signalStatusNode = sosjnamespace.GetNodeObjFromStorage(signalname+":Status");
        UaVariableNode signalValueNode = sosjnamespace.GetNodeObjFromStorage(signalname+":Value");
        
        boolean signalStatus = (boolean) signalStatusNode.getValue().getValue().getValue();
        String signalValue = (String) signalValueNode.getValue().getValue().getValue();
        
        if(signalStatus) {
        	obj[0] = Boolean.TRUE;
            obj[1] = signalValue;
            super.setBufferIncomingValue(obj);
        } else {
        	obj[0] = Boolean.FALSE;
            obj[1] = signalValue;
            super.setBuffer(obj);
        }
        
        //Optional<ServerNode> inSigStatusNode = miloserv.getNodeMap().getNode(new NodeId(2, "/Signals/Input/"+signalname+"/Status"));
        
        boolean IsRead = super.getIsRead();
        
        if(IsRead) {
        	signalStatusNode.setValue(new DataValue(new Variant(false)));
        	sosjnamespace.AddNodeObjToStorage(signalname+":Status", signalStatusNode);
        	super.setIsRead(false);
        } 
        */
		
	}
    
    
}
