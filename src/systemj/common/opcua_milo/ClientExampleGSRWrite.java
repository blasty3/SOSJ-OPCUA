package systemj.common.opcua_milo;



import java.util.List;

import java.util.concurrent.CompletableFuture;
import com.google.common.collect.ImmutableList;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public class ClientExampleGSRWrite implements ClientExample{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run(OpcUaClient client, String signalNameToConnect, boolean ReadOrWrite, String Direction, String valueToSend, CompletableFuture<OpcUaClient> future) throws Exception {
		// TODO Auto-generated method stub
		
		client.connect().get();

        //List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, "HelloWorld/ScalarTypes/Int32"));
		
		{
			//if write
			
			// connect to signal status
			
			
			List<NodeId> nodeIdsGSRAddr = ImmutableList.of(new NodeId(2, "Params/GSR/GSR_ADDR"));
	        
			Variant vStatus = new Variant(valueToSend);
			
			 // don't write status or timestamps
	        DataValue dvStatus = new DataValue(vStatus, null, null);

	        // write asynchronously....
	        CompletableFuture<List<StatusCode>> fGSRAddr =
	            client.writeValues(nodeIdsGSRAddr, ImmutableList.of(dvStatus));

	        // ...but block for the results so we write in order
	        List<StatusCode> statusCodesGSRAddr = fGSRAddr.get();
	        StatusCode statusGSRAddr = statusCodesGSRAddr.get(0);

	        if (statusGSRAddr.isGood()) {
	            logger.info("Wrote '{}' to nodeId={}", vStatus, nodeIdsGSRAddr.get(0));
	        }
			
			/*
	        
	        for (int i = 0; i < 10; i++) {
	            Variant v = new Variant(i);

	            // don't write status or timestamps
	            DataValue dv = new DataValue(v, null, null);

	            // write asynchronously....
	            CompletableFuture<List<StatusCode>> f =
	                client.writeValues(nodeIds, ImmutableList.of(dv));

	            // ...but block for the results so we write in order
	            List<StatusCode> statusCodes = f.get();
	            StatusCode status = statusCodes.get(0);

	            if (status.isGood()) {
	                logger.info("Wrote '{}' to nodeId={}", v, nodeIds.get(0));
	            }
	        }
	        */

	        future.complete(client);
	        
		}
		
		client.disconnect().get();
		
		
				
	}
	
	private CompletableFuture<List<DataValue>> readAsyncSignalStatus(OpcUaClient client, String folderName) {
        List<NodeId> nodeIds = ImmutableList.of(
        		new NodeId(2, folderName));

        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }

}
