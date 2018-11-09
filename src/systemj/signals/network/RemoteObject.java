package systemj.signals.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObject extends Remote {
	public void setBuffer(Object[] o) throws RemoteException;
}
