package systemj.common.opcua_milo;

import java.util.Hashtable;

public class SOSJSignalOPCUAReadSharedVariables {

	private static Hashtable SOSJSignalsComm = new Hashtable();
	
	
	private final static Object SOSJSignalsCommLock = new Object();
	
	
	public static void AddForSignalsToRead(String signalName, boolean signalStatus, String signalValue) {
		synchronized(SOSJSignalsCommLock) {
			SOSJSignalsComm.put(signalName+":Status", signalStatus);
			SOSJSignalsComm.put(signalName+":Value", signalValue);
		}
	}
	
	public static boolean CheckForSignalToRead(String signalName) {
		synchronized(SOSJSignalsCommLock) {
			if(SOSJSignalsComm.containsKey(signalName+":Status")) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static boolean GetSignalStatusToRead(String signalName) {
		synchronized(SOSJSignalsCommLock) {
			 boolean status = ((boolean) SOSJSignalsComm.get(signalName+":Status"));
			 SOSJSignalsComm.put(signalName+":Status", false);
			 return status;
		}
	}
	
	public static void ResetSignalStatus(String signalName) {
		synchronized(SOSJSignalsCommLock) {
			SOSJSignalsComm.put(signalName+":Status", false);
		}
	}
	
	public static String GetSignalValueToRead(String signalName) {
		synchronized(SOSJSignalsCommLock) {
			return (SOSJSignalsComm.get(signalName+":Value")).toString();
		}
	}
	
	
}
