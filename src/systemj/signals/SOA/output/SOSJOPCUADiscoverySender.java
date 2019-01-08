package systemj.signals.SOA.output;

import java.util.Hashtable;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;

import systemj.common.opcua_milo.InvokeGetAllServiceDescriptionFromGSR;
import systemj.common.opcua_milo.InvokeGetServiceDescription;
import systemj.common.opcua_milo.MiloServerSSHandler;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.common.opcua_milo.SOSJOPCServerNamespace;
import systemj.common.opcua_milo.SOSJOPCUAServerNamespaceForCD;
import systemj.interfaces.GenericSignalSender;

public class SOSJOPCUADiscoverySender extends GenericSignalSender{

	private String ssName;
	
	 UShort ush = Unsigned.ushort(2);
	
	@Override
	public void configure(Hashtable data) throws RuntimeException {
		// TODO Auto-generated method stub
		
		if(data.containsKey("SSName")){
            ssName = (String)data.get("SSName");
        } else {
            throw new RuntimeException("the 'SSName' attribute is required");
        }
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		MiloServerSSHandler milo_serv_ss = OPCUAClientServerObjRepo.GetServerObjSS(ssName);
		
		OpcUaServer miloserv = milo_serv_ss.getServer();
		SOSJOPCServerNamespace sosjnamespace = (SOSJOPCServerNamespace) miloserv.getNamespaceManager().getNamespace(ush);
        
        //first, obtain GSR Addr and Port through local reading OPC UA variable node
        
        UaVariableNode GSRAddrNode = sosjnamespace.GetNodeObjFromStorage("GSR_ADDR");
        UaVariableNode GSRPortNode = sosjnamespace.GetNodeObjFromStorage("GSR_PORT");
        
        String GSRAddr = (String) GSRAddrNode.getValue().getValue().getValue();
        int GSRPort = Integer.parseInt(GSRPortNode.getValue().getValue().getValue().toString());
        
		InvokeGetAllServiceDescriptionFromGSR invGetAllServ = new InvokeGetAllServiceDescriptionFromGSR();
		
		try {
			
			invGetAllServ.execute(GSRAddr, GSRPort, "Params");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
