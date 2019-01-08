package systemj.signals.SOA.input;

import java.util.Hashtable;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.json.me.JSONException;

import systemj.common.SJServiceRegistry;
import systemj.common.opcua_milo.SOSJSignalOPCUAReadSharedVariables;
import systemj.interfaces.GenericSignalReceiver;

public class SOSJOPCUARegistryRead extends GenericSignalReceiver{

	@Override
	public void configure(Hashtable data) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		Object[] obj = new Object[2];
		
		
				try {
					String jsServDesc = SJServiceRegistry.obtainCurrentRegistry().toString();
					obj[0] = Boolean.TRUE;
		            obj[1] = jsServDesc;
		            super.setBuffer(obj);
				} catch (JSONException e) {
					obj[0] = Boolean.FALSE;
		            obj[1] = "{}";
		            super.setBuffer(obj);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
	}

}
