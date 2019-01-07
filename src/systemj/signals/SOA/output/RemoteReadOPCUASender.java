/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA.output;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJSOAMessage;
import systemj.common.opcua_milo.ClientRunner;
import systemj.common.opcua_milo.OPCUAClientServerObjRepo;
import systemj.interfaces.GenericSignalSender;

/**
 *
 * @author Atmojo
 */
public class RemoteReadOPCUASender extends GenericSignalSender{

    DatagramSocket s1 = null;
    String SigName = null;
    String CDName = null;
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("Name")){
            SigName = (String)data.get("Name");
        }
        
        if(data.containsKey("CDName")){
            CDName = (String)data.get("CDName");
        }
        
    }

    @Override
    public void run() {
        
    	Object[] obj = super.buffer;
		//String data = (String) obj[1];
		
		// 02.01.2019 Udayanto
		//before hand, (re-)configuration of the client should occur first at the application level.
		
		ClientRunner clrun = OPCUAClientServerObjRepo.GetClientObjCD(CDName, SigName);
		
		clrun.SetReadOrWrite(true);
		
	    clrun.InstantiateClient();
	    
	    clrun.SetSignalValue("");

	    clrun.run();
	    
	    //clrun.DisconnectClient();
	    
        
    }
    
    
}
