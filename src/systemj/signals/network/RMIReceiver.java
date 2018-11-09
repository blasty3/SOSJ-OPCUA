package systemj.signals.network;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import systemj.interfaces.GenericSignalReceiver;

public class RMIReceiver extends GenericSignalReceiver implements RemoteObject {

	private static transient Registry registry;
	
	public static void setRegistry (int port) throws RemoteException { 
		registry = LocateRegistry.createRegistry(port); 
		System.out.println("Created registry at port "+port);
	}
	
	@Override
	public void configure(Hashtable data) throws RuntimeException {
		if (registry == null) {
			try {
				registry = LocateRegistry.createRegistry(1099);
				System.out.println("Created registry at port 1099");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		if(data.containsKey("Name")){
			this.name = (String)data.get("Name");
			try {
				RemoteObject stub =  (RemoteObject) UnicastRemoteObject.exportObject(this, 0);
				this.registry.rebind(cdname+"."+name, stub);
			} catch (AccessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else
			throw new RuntimeException("Signal name is missing in LCF");
	}
	
	public RMIReceiver (){ super(); }

	@Override
	public void run() { }

}
