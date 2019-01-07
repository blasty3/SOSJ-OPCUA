package systemj.common.opcua_milo;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.milo.examples.server.KeyStoreLoader;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRunner
{

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
  private final KeyStoreLoader loader = new KeyStoreLoader();
  private String endpointUrl= "";
  private ClientExample uaClient;
  
  private boolean ReadOrWrite;
  
  private OpcUaClient opcuaclient;
  private String ssNameToConnect = "";
  private String cdnameToConnect = "";
  
  private String Direction;
  
  private String signalNameToConnect = "";
  
  //for reading operation
  private String signalNameForReading = "";
  
  private String signalValue;

  
  public ClientRunner() {
	  
  }
  
  public ClientRunner(boolean ReadOrWrite) {
	  this.ReadOrWrite = ReadOrWrite;
  }
  
  public ClientRunner(ClientExample clientExample) {
	  this.uaClient = clientExample;
  }
  
  public ClientRunner(String Addr, int portNum, String ssname, String cdname, ClientExample clientExample)
  {
    //this.endpointUrl = endpointUrl;
	this.endpointUrl = ("opc.tcp://"+Addr+":"+portNum+"/"+ssname+"/"+cdname);
	this.ssNameToConnect = (ssname);
	this.cdnameToConnect = (cdname);
    this.uaClient = clientExample;
  }
  
  public ClientRunner(String ssname, String cdname, ClientExample clientExample)
  {
    //this.endpointUrl = endpointUrl;
	//this.endpointUrl = "opc.tcp://"+Addr+":"+portNum+"/"+ssname+"/"+cdname;
	this.ssNameToConnect = (ssname);
	this.cdnameToConnect = (cdname);
    this.uaClient = clientExample;
  }
  
  public void SetEndpointUrl(String Addr, int portNum) {
	  this.endpointUrl = ("opc.tcp://"+Addr+":"+portNum+"/"+ssNameToConnect+"/"+cdnameToConnect);
  }
  
  public void SetReadOrWrite(boolean ReadOrWrite) {
	  this.ReadOrWrite = ReadOrWrite;
  }
  
  public void SetTargetDirection(String direction) {
	  this.Direction = direction;
  }
  
  
  public void SetEndpointUrl(String Addr, int portNum, String ssNameDestination, String cdNameDestination) {
	  this.ssNameToConnect = (ssNameDestination);
	  this.cdnameToConnect = (cdNameDestination);
	  this.endpointUrl = ("opc.tcp://"+Addr+":"+portNum+"/"+ssNameDestination+"/"+cdNameDestination);
	  //System.out.println("ClientRunner, SetEndpointUrl invoked 1, endpointUrl: " +this.endpointUrl);
	  System.out.println("ClientRunner, SetEndpointUrl invoked, endpointUrl: " +endpointUrl);
  }
  
  public boolean EndpointURLNotInitialized() {
	  if(endpointUrl.length()==0) {
		  return true;
	  } else {
		  return false;
	  }
  }
  
  public void SetSignalNameToConnect(String signalNameToConnect){
	  this.signalNameToConnect = (signalNameToConnect);
  }
  
  public void SetSignalNameForRead(String signalNameForRead){
	  this.signalNameForReading = (signalNameForRead);
  }
  
  public String GetSignalNameToConnect() {
	  return signalNameToConnect;
  }
  
  public String GetSignalNameForRead() {
	  return signalNameForReading;
  }
  
  public void SetSignalValue(String value) {
	  this.signalValue = value;
  }

  private OpcUaClient createClient() throws Exception
  {
    SecurityPolicy securityPolicy = uaClient.getSecurityPolicy();
    
    //EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://localhost:12686/example").get();
    
    System.out.println("ClientRunner, endpoint url: " +endpointUrl);
    
    EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpointUrl).get();
    
    EndpointDescription endpoint = Arrays.stream(endpoints)
            .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
            .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));
    
    //EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpointUrl).get();

    /*
    EndpointDescription endpointAux = Arrays.stream(endpoints)
            .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
            .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

    EndpointDescription endpointFinal = new EndpointDescription(
            endpointAux.getEndpointUrl()
       * .replace("kuka-introsys", "172.20.11.100")
             // , endpointAux.getServer(),
            endpointAux.getServerCertificate(), endpointAux.getSecurityMode(), endpointAux.getSecurityPolicyUri(),
            endpointAux.getUserIdentityTokens(), endpointAux.getTransportProfileUri(), endpointAux.getSecurityLevel());

    */
    //logger.info("Using endpoint: {} [{}]", endpointFinal.getEndpointUrl(), securityPolicy);

    loader.load();
    OpcUaClientConfig config = OpcUaClientConfig.builder()
            .setApplicationName(LocalizedText.english("SOSJ opc-ua client connecting to SS and cd:" +ssNameToConnect+" "+cdnameToConnect ))
            .setApplicationUri("urn:sosj-opcua:client:" +ssNameToConnect+":" +cdnameToConnect)
            .setCertificate(loader.getClientCertificate())
            .setKeyPair(loader.getClientKeyPair())
            .setEndpoint(endpoint)
            .setIdentityProvider(uaClient.getIdentityProvider())
            .setRequestTimeout(uint(30000))
            .build();
    
    opcuaclient = new OpcUaClient(config);
    
    return opcuaclient;
  }

  public boolean run()
  {
	
	
    future.whenComplete((opcuaclient, ex)
            ->
    {
      if (opcuaclient != null)
      {
        try
        {
          
          opcuaclient.disconnect().get();
          //Stack.releaseSharedResources();
        } catch (InterruptedException | ExecutionException e)
        {
          logger.error("Error disconnecting:", e.getMessage(), e);
        }
        
       
      } 
      else
      {
        logger.error("Error running example: {}", ex.getMessage(), ex);
        Stack.releaseSharedResources();
      }
    });

    //try
    //{
       //createClient();

      try
      {
        uaClient.run(opcuaclient, signalNameToConnect,ReadOrWrite, Direction,signalValue, future);
        //future.get(500, TimeUnit.MILLISECONDS);
        return true;
      } 
      catch (Exception t)
      {
        logger.error("Error running client example: {}", t.getMessage(), t);
        future.complete(opcuaclient);
        return false;
      }
    } 
    //catch (Exception t)
    //{
     // future.completeExceptionally(t);
    //}
  //}
  
  public OpcUaClient getOpcUaClient() {
	  return opcuaclient;
  }
  
  public void DisconnectClient() {
	  if (opcuaclient != null)
      {
        try
        {
         opcuaclient.disconnect().get();
          //Stack.releaseSharedResources();
        } catch (InterruptedException | ExecutionException e)
        {
          logger.error("Error disconnecting:", e.getMessage(), e);
        }
      } 
	  else
      {
        logger.error("Error running example: {}");
        Stack.releaseSharedResources();
      }
  }
  
  
  public void TerminateClient() {
	  if (opcuaclient != null)
      {
        try
        {
          opcuaclient.disconnect().get();
          //Stack.releaseSharedResources();
          opcuaclient = null;
        } catch (InterruptedException | ExecutionException e)
        {
          logger.error("Error disconnecting:", e.getMessage(), e);
        }
      } 
	  else
      {
        logger.error("Error running example: {}");
        Stack.releaseSharedResources();
      }
  }
  
  
  public void InstantiateClient() {
	  future.whenComplete((client, ex)
	            ->
	    {
	      if (client != null)
	      {
	        try
	        {
	          client.disconnect().get();
	          //Stack.releaseSharedResources();
	        } catch (InterruptedException | ExecutionException e)
	        {
	          logger.error("Error disconnecting:", e.getMessage(), e);
	        }
	      } else
	      {
	        logger.error("Error running example: {}", ex.getMessage(), ex);
	        Stack.releaseSharedResources();
	      }
	    });

		  if (opcuaclient != null) {
			  
		  } else {
			  try
			    {
			      createClient();
	
			    } catch (Exception t)
			    {
			      future.completeExceptionally(t);
			    }
		  }
	  
	  }
  }