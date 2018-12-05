package systemj.common.opcua_milo;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.examples.server.KeyStoreLoader;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAClientManager
{

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
  private final KeyStoreLoader loader = new KeyStoreLoader();
  
  private String Addr;
  private int portNum;
  private String Id;
  
  public OPCUAClientManager(String Addr, int portNum, String Id)
  {
	  this.Addr = Addr;
	  this.portNum = portNum;
	  this.Id = Id;
  }

  private OpcUaClient createClient() throws Exception
  {
    SecurityPolicy securityPolicy = SecurityPolicy.None;//uaClient.getSecurityPolicy();
    
    //Endpoint example : "opc.tcp://localhost:12686/example"
    
    EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://"+Addr+":"+portNum+"/"+Id).get();

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
            .setApplicationName(LocalizedText.english("digitalpetri opc-ua client"))
            .setApplicationUri("urn:digitalpetri:opcua:client")
            .setCertificate(loader.getClientCertificate())
            .setKeyPair(loader.getClientKeyPair())
            .setEndpoint(endpointFinal)
            //.setIdentityProvider(uaClient.getIdentityProvider())
            .setIdentityProvider(new AnonymousProvider())
            .setRequestTimeout(uint(30000))
            .build();

    return new OpcUaClient(config);
  }

}