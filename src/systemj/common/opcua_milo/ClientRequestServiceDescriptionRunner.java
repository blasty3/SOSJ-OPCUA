/*
 * Copyright (c) 2016 Kevin Herron
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

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import systemj.common.opcua_milo.util.KeyStoreLoader;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class ClientRequestServiceDescriptionRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();

    private final KeyStoreLoader loader = new KeyStoreLoader();

    //private final ClientExample clientExample;
    private final ClientTemplateSS clientExample;
    
    private String Addr = "";
    
    private int port = 12686;
    private String name = "";

    public ClientRequestServiceDescriptionRunner(ClientTemplateSS clientExample, String Addr, int port, String name) throws Exception {
        //this.clientExample = clientExample;

    	this.clientExample = clientExample;
    	
        this.Addr = Addr;
        this.port = port;
        this.name = name;
       // exampleServer = new ExampleServer();
        //exampleServer.startup().get();
    }

    private OpcUaClient createClient(String Addr, int port) throws Exception {
        SecurityPolicy securityPolicy = clientExample.getSecurityPolicy();

        //EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://localhost:12686/example").get();
        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://"+Addr+":"+port+"/"+name).get();

        EndpointDescription endpoint = Arrays.stream(endpoints)
            .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
            .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy);

        loader.load();

        OpcUaClientConfig config = OpcUaClientConfig.builder()
            .setApplicationName(LocalizedText.english("eclipse milo sosj-opc-ua client"))
            .setApplicationUri("urn:eclipse:milo:sosj-opc-ua:client")
            .setCertificate(loader.getClientCertificate())
            .setKeyPair(loader.getClientKeyPair())
            .setEndpoint(endpoint)
            .setIdentityProvider(clientExample.getIdentityProvider())
            .setRequestTimeout(uint(5000))
            .build();

        return new OpcUaClient(config);
    }

    public void run() {
        future.whenComplete((client, ex) -> {
            if (client != null) {
                try {
                    client.disconnect().get();
                    
                    
                    //exampleServer.shutdown().get();
                    //Stack.releaseSharedResources();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error disconnecting:", e.getMessage(), e);
                }
            } else {
                logger.error("Error running example: {}", ex.getMessage(), ex);
                Stack.releaseSharedResources();
            }

            try {
                Thread.sleep(5);
                //System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            OpcUaClient client = createClient(Addr, port);

            try {
                clientExample.run(client, future, name);
                future.get(5, TimeUnit.SECONDS);
            } catch (Throwable t) {
                logger.error("Error running client example: {}", t.getMessage(), t);
                future.complete(client);
            }
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }

        /*
        try {
            Thread.sleep(999999999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

}
