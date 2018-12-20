package systemj.common.opcua_milo;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
  private final String endpointUrl;
  private final ClientExample uaClient;
  
  private OpcUaClient opcuaclient;
  private String Id;

  public ClientRunner(String Addr, int portNum, String Id, ClientExample clientExample)
  {
    //this.endpointUrl = endpointUrl;
	this.endpointUrl = "opc.tcp://"+Addr+":"+portNum+"/"+Id;
	this.Id = Id;
    this.uaClient = clientExample;
    
  }

  private OpcUaClient createClient() throws Exception
  {
    SecurityPolicy securityPolicy = uaClient.getSecurityPolicy();
    EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpointUrl).get();

    EndpointDescription endpointAux = Arrays.stream(endpoints)
            .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
            .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

    EndpointDescription endpointFinal = new EndpointDescription(
            endpointAux.getEndpointUrl()/*
       * .replace("kuka-introsys", "172.20.11.100")
             */, endpointAux.getServer(),
            endpointAux.getServerCertificate(), endpointAux.getSecurityMode(), endpointAux.getSecurityPolicyUri(),
            endpointAux.getUserIdentityTokens(), endpointAux.getTransportProfileUri(), endpointAux.getSecurityLevel());

    logger.info("Using endpoint: {} [{}]", endpointFinal.getEndpointUrl(), securityPolicy);

    loader.load();
    OpcUaClientConfig config = OpcUaClientConfig.builder()
            .setApplicationName(LocalizedText.english("SOSJ opc-ua client for " +Id))
            .setApplicationUri("urn:sosj-opcua:client:" +Id)
            .setCertificate(loader.getClientCertificate())
            .setKeyPair(loader.getClientKeyPair())
            .setEndpoint(endpointFinal)
            .setIdentityProvider(uaClient.getIdentityProvider())
            .setRequestTimeout(uint(30000))
            .build();
    
    opcuaclient = new OpcUaClient(config);

    return opcuaclient;
  }

  public void run()
  {
    future.whenComplete((client, ex)
            ->
    {
      if (client != null)
      {
        try
        {
          
          client.disconnect().get();
          Stack.releaseSharedResources();
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

    try
    {
      OpcUaClient client = createClient();

      try
      {
        uaClient.run(client, future);
      } catch (Exception t)
      {
        logger.error("Error running client example: {}", t.getMessage(), t);
        future.complete(client);
      }
    } catch (Exception t)
    {
      future.completeExceptionally(t);
    }
  }
  
  public OpcUaClient getOpcUaClient() {
	  return opcuaclient;
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
	          Stack.releaseSharedResources();
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

	    try
	    {
	      createClient();

	    } catch (Exception t)
	    {
	      future.completeExceptionally(t);
	    }
	  }
  }