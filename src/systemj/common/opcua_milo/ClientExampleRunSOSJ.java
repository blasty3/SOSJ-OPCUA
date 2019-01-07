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

public class ClientExampleRunSOSJ implements ClientExample{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run(OpcUaClient client, String signalNameToConnect, boolean ReadOrWrite, String Direction, String valueToSend, CompletableFuture<OpcUaClient> future) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		client.connect().get();

        //List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, "HelloWorld/ScalarTypes/Int32"));
		
		//if read
		if (ReadOrWrite) {
			
			
			
			/*
			 // synchronous read request via VariableNode
        VariableNode node = client.getAddressSpace().createVariableNode(Identifiers.Server_ServerStatus_StartTime);
        DataValue value = node.readValue().get();

        logger.info("StartTime={}", value.getValue().getValue());

        // asynchronous read request
        readServerStateAndTime(client).thenAccept(values -> {
            DataValue v0 = values.get(0);
            DataValue v1 = values.get(1);

            logger.info("State={}", ServerState.from((Integer) v0.getValue().getValue()));
            logger.info("CurrentTime={}", v1.getValue().getValue());

            future.complete(client);
        });
			
			*/
			
			String direction = "";
			
			if(Direction.equalsIgnoreCase("input")) {
				direction = "Input";
			} else if(Direction.equalsIgnoreCase("output")) {
				direction = "Output";
			}
			
			// connect to signal status
			
			VariableNode nodeSignalStatus = client.getAddressSpace().createVariableNode(new NodeId(2, "Interfaces/Signals/"+direction+"/"+signalNameToConnect+"/Status"));
			VariableNode nodeSignalValue = client.getAddressSpace().createVariableNode(new NodeId(2, "Interfaces/Signals/"+direction+"/"+signalNameToConnect+"/Value"));
			// connect to signal status
			//List<NodeId> nodeIdsSignalStatus = ImmutableList.of(new NodeId(2, "/Signals/Output/"+signalNameToConnect+"/Status"));
	        
			DataValue statusDatVal = nodeSignalStatus.readValue().get();
			DataValue valueDatVal = nodeSignalValue.readValue().get();
			
			
			boolean signalStatus = (boolean) statusDatVal.getValue().getValue();
			String signalValue = valueDatVal.getValue().getValue().toString();
			
			
			SOSJSignalOPCUAReadSharedVariables.AddForSignalsToRead(signalNameToConnect, signalStatus, signalValue);
			
			
			

	        future.complete(client);
			
			
		} else {
			//if write
			
			// connect to signal status
				
			String direction = "";
			
			if(Direction.equalsIgnoreCase("input")) {
				direction = "Input";
			} else if(Direction.equalsIgnoreCase("output")) {
				direction = "Output";
			}
			
			List<NodeId> nodeIdsSignalStatus = ImmutableList.of(new NodeId(2, "Interfaces/Signals/"+direction+"/"+signalNameToConnect+"/Status"));
	        
			Variant vStatus = new Variant(true);
			
			 // don't write status or timestamps
	        DataValue dvStatus = new DataValue(vStatus, null, null);

	        // write asynchronously....
	        CompletableFuture<List<StatusCode>> fSignalStatus =
	            client.writeValues(nodeIdsSignalStatus, ImmutableList.of(dvStatus));

	        // ...but block for the results so we write in order
	        List<StatusCode> statusCodesSignalStatus = fSignalStatus.get();
	        StatusCode statusSignalStatus = statusCodesSignalStatus.get(0);

	        if (statusSignalStatus.isGood()) {
	            logger.info("Wrote '{}' to nodeId={}", vStatus, nodeIdsSignalStatus.get(0));
	        }
			
			
			//connect to signal value
			
			List<NodeId> nodeIdsSignalValue = ImmutableList.of(new NodeId(2, "Interfaces/Signals/"+direction+"/"+signalNameToConnect+"/Value"));
	        
			 Variant vValue = new Variant(valueToSend);

	         // don't write status or timestamps
	         DataValue dv = new DataValue(vValue, null, null);

	         // write asynchronously....
	         CompletableFuture<List<StatusCode>> f =
	             client.writeValues(nodeIdsSignalValue, ImmutableList.of(dv));

	         // ...but block for the results so we write in order
	         List<StatusCode> statusCodes = f.get();
	         StatusCode status = statusCodes.get(0);

	         if (status.isGood()) {
	             logger.info("Wrote '{}' to nodeId={}", vValue, nodeIdsSignalValue.get(0));
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
				
	}
	
	private CompletableFuture<List<DataValue>> readAsyncSignalStatus(OpcUaClient client, String folderName) {
        List<NodeId> nodeIds = ImmutableList.of(
        		new NodeId(2, folderName));

        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }

}
