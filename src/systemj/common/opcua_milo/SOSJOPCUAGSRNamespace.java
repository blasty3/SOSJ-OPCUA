/*
 * Copyright (c) 2018 Kevin Herron, Udayanto Dwi Atmojo
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package systemj.common.opcua_milo;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;

import org.eclipse.milo.examples.server.RestrictedAccessDelegate;
import org.eclipse.milo.examples.server.ValueLoggingDelegate;

import systemj.common.opcua_milo.method.GetAllServiceDescription;
import systemj.common.opcua_milo.method.GetServiceDescription;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRank;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.AccessContext;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.MethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegateChain;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.eclipse.milo.opcua.stack.core.util.FutureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

public class SOSJOPCUAGSRNamespace implements Namespace {

    public static final String NAMESPACE_URI = "urn:eclipse:milo:sosj-ss";
    
    private String folderName="";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Random random = new Random();

    private final SubscriptionModel subscriptionModel;

    private final OpcUaServer server;
    private final UShort namespaceIndex;

    
    //Creating the Namespace with name. 
    
    public SOSJOPCUAGSRNamespace(OpcUaServer server, UShort namespaceIndex, String folderName) {
        this.server = server;
        this.namespaceIndex = namespaceIndex;
        this.folderName = folderName;

        subscriptionModel = new SubscriptionModel(server, this);

        try {
            // Create a "HelloWorld" folder and add it to the node manager
            NodeId folderNodeId = new NodeId(namespaceIndex, folderName);

            UaFolderNode folderNode = new UaFolderNode(
                server.getNodeMap(),
                folderNodeId,
                new QualifiedName(namespaceIndex, folderName),
                LocalizedText.english(folderName)
            );

            server.getNodeMap().addNode(folderNode);

            // Make sure our new folder shows up under the server's Objects folder
            server.getUaNamespace().addReference(
                Identifiers.ObjectsFolder,
                Identifiers.Organizes,
                true,
                folderNodeId.expanded(),
                NodeClass.Object
            );

            // Add the rest of the nodes
            //addVariableNodes(folderNode);

            //addMethodNode(folderNode);
            
            addGetServiceDescriptionMethodNode(folderNode);
        } catch (UaException e) {
            logger.error("Error adding nodes: {}", e.getMessage(), e);
        }
    }

    @Override
    public UShort getNamespaceIndex() {
        return namespaceIndex;
    }

    @Override
    public String getNamespaceUri() {
        return NAMESPACE_URI;
    }

    
    private void addGetServiceDescriptionMethodNode(UaFolderNode folderNode) {
        UaMethodNode methodNode = UaMethodNode.builder(server.getNodeMap())
            .setNodeId(new NodeId(namespaceIndex, folderName+"/getAllServiceDescription()"))
            .setBrowseName(new QualifiedName(namespaceIndex, "getAllServiceDescription()"))
            .setDisplayName(new LocalizedText(null, "getAllServiceDescription()"))
            .setDescription(
                LocalizedText.english("Returns the service description recorded in the GSR."))
            .build();


        try {
            AnnotationBasedInvocationHandler invocationHandler =
                AnnotationBasedInvocationHandler.fromAnnotatedObject(
                    server.getNodeMap(), new GetAllServiceDescription());

            methodNode.setProperty(UaMethodNode.InputArguments, invocationHandler.getInputArguments());
            methodNode.setProperty(UaMethodNode.OutputArguments, invocationHandler.getOutputArguments());
            methodNode.setInvocationHandler(invocationHandler);

            server.getNodeMap().addNode(methodNode);

            folderNode.addReference(new Reference(
                folderNode.getNodeId(),
                Identifiers.HasComponent,
                methodNode.getNodeId().expanded(),
                methodNode.getNodeClass(),
                true
            ));

            methodNode.addReference(new Reference(
                methodNode.getNodeId(),
                Identifiers.HasComponent,
                folderNode.getNodeId().expanded(),
                folderNode.getNodeClass(),
                false
            ));
        } catch (Exception e) {
            logger.error("Error creating getServiceDescription() method.", e);
        }
    }

    @Override
    public CompletableFuture<List<Reference>> browse(AccessContext context, NodeId nodeId) {
        ServerNode node = server.getNodeMap().get(nodeId);

        if (node != null) {
            return CompletableFuture.completedFuture(node.getReferences());
        } else {
            return FutureUtils.failedFuture(new UaException(StatusCodes.Bad_NodeIdUnknown));
        }
    }

    @Override
    public void read(
        ReadContext context,
        Double maxAge,
        TimestampsToReturn timestamps,
        List<ReadValueId> readValueIds) {

        List<DataValue> results = Lists.newArrayListWithCapacity(readValueIds.size());

        for (ReadValueId readValueId : readValueIds) {
            ServerNode node = server.getNodeMap().get(readValueId.getNodeId());

            if (node != null) {
                DataValue value = node.readAttribute(
                    new AttributeContext(context),
                    readValueId.getAttributeId(),
                    timestamps,
                    readValueId.getIndexRange()
                );

                results.add(value);
            } else {
                results.add(new DataValue(StatusCodes.Bad_NodeIdUnknown));
            }
        }

        context.complete(results);
    }

    @Override
    public void write(WriteContext context, List<WriteValue> writeValues) {
        List<StatusCode> results = Lists.newArrayListWithCapacity(writeValues.size());

        for (WriteValue writeValue : writeValues) {
            ServerNode node = server.getNodeMap().get(writeValue.getNodeId());

            if (node != null) {
                try {
                    node.writeAttribute(
                        new AttributeContext(context),
                        writeValue.getAttributeId(),
                        writeValue.getValue(),
                        writeValue.getIndexRange()
                    );

                    results.add(StatusCode.GOOD);

                    logger.info(
                        "Wrote value {} to {} attribute of {}",
                        writeValue.getValue().getValue(),
                        AttributeId.from(writeValue.getAttributeId()).map(Object::toString).orElse("unknown"),
                        node.getNodeId());
                } catch (UaException e) {
                    logger.error("Unable to write value={}", writeValue.getValue(), e);
                    results.add(e.getStatusCode());
                }
            } else {
                results.add(new StatusCode(StatusCodes.Bad_NodeIdUnknown));
            }
        }

        context.complete(results);
    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }

    @Override
    public Optional<MethodInvocationHandler> getInvocationHandler(NodeId methodId) {
        Optional<ServerNode> node = server.getNodeMap().getNode(methodId);

        return node.flatMap(n -> {
            if (n instanceof UaMethodNode) {
                return ((UaMethodNode) n).getInvocationHandler();
            } else {
                return Optional.empty();
            }
        });
    }

}
