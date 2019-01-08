/*
 * Copyright (c) 2016 Kevin Herron and Udayanto Dwi Atmojo
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

package systemj.common.opcua_milo.method;

import org.eclipse.milo.opcua.sdk.server.annotations.UaInputArgument;
import org.eclipse.milo.opcua.sdk.server.annotations.UaMethod;
import org.eclipse.milo.opcua.sdk.server.annotations.UaOutputArgument;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler.InvocationContext;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler.Out;
import org.json.me.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import systemj.common.SJServiceRegistry;

public class GetAllServiceDescription {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @UaMethod
    public void invoke(
        InvocationContext context,

        //@UaInputArgument(
        //    name = "x",
        //    description = "A value.")
        //    Double x,

        @UaOutputArgument(
           // name = "x_sqrt",
            //description = "The positive square root of x. If the argument is NaN or less than zero, the result is NaN."
            name = "GetAllServiceDescription",
        	description = "Obtain service description of all services in SOSJ GSR"	
        	)
            //Out<Double> xSqrt
        	Out<String> serviceDescription
     )
    
    {

        //System.out.println("sqrt(" + x.toString() + ")");
        //logger.debug("Invoking sqrt() method of Object '{}'", context.getObjectNode().getBrowseName().getName());
    	
    	try {
    		System.out.println("GetAllServiceDescription()");
    		logger.debug("Invoking GetAllServiceDescription() method of Object '{}'", context.getObjectNode().getBrowseName().getName());
			serviceDescription.set(SJServiceRegistry.obtainCurrentRegistry().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
        //xSqrt.set(Math.sqrt(x));
    }

}