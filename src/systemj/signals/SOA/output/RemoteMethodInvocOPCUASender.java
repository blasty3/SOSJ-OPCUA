package systemj.signals.SOA.output;

import java.util.Hashtable;

import systemj.common.opcua_milo.InvokeGetAllServiceDescriptionFromGSR;
import systemj.common.opcua_milo.InvokeGetServiceDescription;
import systemj.interfaces.GenericSignalSender;

public class RemoteMethodInvocOPCUASender extends GenericSignalSender{

	@Override
	public void configure(Hashtable data) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		InvokeGetAllServiceDescriptionFromGSR invGetAllServ = new InvokeGetAllServiceDescriptionFromGSR();
		
		try {
			invGetAllServ.SetGSRValue(GSR_Addr);(GSR_ADDR);
			invGetServ.execute(addr, port, ssName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
