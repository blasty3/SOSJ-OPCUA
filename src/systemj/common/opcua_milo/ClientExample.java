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

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;


public interface ClientExample {

    default SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.None;
    }

    default IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }

    /*
     * boolean ReadOrWrite : True if read, false if write
     */
    void run(OpcUaClient client, String signalNameToConnect, boolean ReadOrWrite, String valueToSend, CompletableFuture<OpcUaClient> future) throws Exception;

}
