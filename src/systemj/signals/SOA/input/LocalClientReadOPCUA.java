package systemj.signals.SOA.input;

import java.util.Hashtable;

import systemj.common.opcua_milo.ClientRunner;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.common.opcua_milo.SOSJSignalOPCUAReadSharedVariables;
import systemj.interfaces.GenericSignalReceiver;

public class LocalClientReadOPCUA extends GenericSignalReceiver{

	String SenderSigName = null;
	String cdName = null;
	
	@Override
	public void configure(Hashtable data) throws RuntimeException {
		// TODO Auto-generated method stub
		if(data.containsKey("SenderSignalName")){
			SenderSigName = (String)data.get("SenderSignalName");
        } 
		
		if(data.containsKey("CDName")){
			cdName = (String)data.get("CDName");
        } 
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		   Object[] obj = new Object[2];
		
		   ClientRunner clrun = OPCUAClientServerObjRepo.GetClientObjCD(cdName, SenderSigName);
		
		   if(clrun != null) {
			
			    String signalPair = clrun.GetSignalNameToConnect();
			   
			    boolean signalStatus = SOSJSignalOPCUAReadSharedVariables.GetSignalStatusToRead(signalPair);
			    String signalValue = SOSJSignalOPCUAReadSharedVariables.GetSignalValueToRead(signalPair);
			    
				if(signalStatus) {
					obj[0] = Boolean.TRUE;
		            obj[1] = signalValue;
		            super.setBufferIncomingValue(obj);
		            SOSJSignalOPCUAReadSharedVariables.ResetSignalStatus(clrun.GetSignalNameToConnect());
				} else {
					
					obj[0] = Boolean.FALSE;
		            obj[1] = signalValue;
		            super.setBuffer(obj);
				}
			   
		   } else {
			   
			    obj[0] = Boolean.FALSE;
	            obj[1] = "";
	            super.setBuffer(obj);
			   
		   }
		   
		  
	}

}
