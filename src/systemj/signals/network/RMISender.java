package systemj.signals.network;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Hashtable;
import java.io.OutputStream;

import systemj.interfaces.GenericSignalSender;

public class RMISender extends GenericSignalSender {
	private Registry registry;
	private int port;
        private String name;
	private String host;
	private String dest;
	private static final int DEFAULT_RMI_PORT = 1099;
	private static final PrintStream stderrout = System.err;
	private static final PrintStream voidout = new PrintStream(new OutputStream (){
		@Override public void write(byte[] b) {}
		@Override public void write (byte[] b, int off, int len) {}
		@Override public void write(int b) {}
	});
	
	// Not the best way to bypass timeout, but could not find any other
	// way (setting properties won't work)
	private RMIClientSocketFactory csf = new RMIClientSocketFactory() {
		@Override
		public Socket createSocket(String host, int port) throws IOException {
			Socket s = new Socket();
			final int timeout = 500;
			s.setSoTimeout(timeout);
			s.connect(new InetSocketAddress(host, port), 500);
			return s;
		}
	};

	@Override
	public void configure(Hashtable data) throws RuntimeException {

		if(data.containsKey("Name")){
			this.name = (String)data.get("Name");
		}
		else
			throw new RuntimeException("Signal name is missing in LCF");
		if(data.containsKey("Port")){
			this.port = Integer.parseInt((String)data.get("Port"));
		}
		if(data.containsKey("Host")){
			this.host = (String)data.get("Host");
		}
		else
			throw new RuntimeException("Host name is required for RMISender");
		if(data.containsKey("To")){
			this.dest = (String)data.get("To");
		}
		else
			throw new RuntimeException("Destination name is required for RMISender, format : <CD>.<InputSignal>");
	}

	@Override
	public void run() {
		try {
			// Suppressing Timeout exception
			System.setErr(RMISender.voidout);
			RemoteObject ro = (RemoteObject)registry.lookup(dest);
			ro.setBuffer(super.buffer);
			// Re-enabling stderr
			System.setErr(RMISender.stderrout);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (ConnectException e){
			// Could not find the server
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean setup(Object[] obj){
		try{
			if(registry == null){
				if(port == 0)
					registry = LocateRegistry.getRegistry(host,RMISender.DEFAULT_RMI_PORT,csf);
				else
					registry = LocateRegistry.getRegistry(host,port,csf);
			}
			super.buffer = obj;
		}
		catch(RemoteException e){
			return false;
		} 
                catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
