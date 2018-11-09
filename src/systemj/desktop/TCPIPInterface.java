package systemj.desktop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONObject;


import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.SOAFacility.TCPIPLinkRegistry;
import systemj.interfaces.GenericInterface;


public class TCPIPInterface extends GenericInterface implements Runnable {
	private String ip,destIp;
	private int port,destPort;
	private String ssname; // May be used later? Not ATM
	private Object[] buffer;
        private boolean IsLocal=true;
        
	
	@Override
	public void configure(Hashtable ht) {
		//if(ht.containsKey("Interface")){
			// This can be used later..
		//}
                
		if(ht.containsKey("Args")){
			String[] args = ((String)ht.get("Args")).trim().split(":");
			if(args.length != 2)
				throw new RuntimeException("Incorrect Args for TCP/IP interface : must be <IP>:<Port>");
			ip = args[0];
			port = new Integer(args[1]).intValue();
		}
		else
			throw new RuntimeException("Missing Args");
                /*
                if(ht.containsKey("DestArgs")){
			String[] DestArgs = ((String)ht.get("DestArgs")).trim().split(":");
			if(DestArgs.length != 2)
				throw new RuntimeException("Incorrect Destination Args for TCP/IP interface : must be <IP>:<Port>");
			destIp = DestArgs[0];
			destPort = new Integer(DestArgs[1]).intValue();
		} else {
                    throw new RuntimeException("Missing Args");
                }
		*/
		if(ht.containsKey("SubSystem")){
			ssname = ((String)ht.get("SubSystem")).trim();
		}
	}

	@Override
	public void invokeReceivingThread() {
                unterminated = true;
		new Thread(this).start();
	}
        
        @Override
        public void TerminateInterface(){
            unterminated=false;
            
            //to synchronize with the thread.run instance, wait until socket timesout and current socket ends before restarting a new one
            while(timeoutcomplete){
                timeoutcomplete=false;
            }
            
        }

	@Override
	public void setup(Object[] o) {
		buffer = o;
	}

	@Override
	public boolean transmitData() {
		try {
                    //if(IsLocal){
                        Socket client = new Socket(ip, port);
                        
			// Uses simple object output stream. no hassle.
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject(buffer);
                        
                        if(buffer.length==6){
                            System.out.println("TCPIPInterface Transmitting data: " +buffer[0]+"|"+buffer[1]+"|"+buffer[2]+"|"+buffer[3]+"|"+buffer[4]+"|"+buffer[5]+"| to :" +ip+":"+port);
                        } else if(buffer.length==7){
                            System.out.println("TCPIPInterface Transmitting data: " +buffer[0]+"|"+buffer[1]+"|"+buffer[2]+"|"+buffer[3]+"|"+buffer[4]+"|"+buffer[5]+"|"+buffer[6]+"| to :" +ip+":"+port);
                        }
                        
                        
			client.close();
                    //} else 
                    //{
                      //  Socket client = new Socket(destIp, destPort);
			// Uses simple object output stream. no hassle.
			//ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			//out.writeObject(buffer);
			//client.close();
                    //}
			
		}
		catch(java.net.ConnectException e){
			System.out.println("Could not reach server "+ip+":"+port);
                        //e.printStackTrace();
			return false;
		}
                catch(java.net.SocketException e){
                        //e.printStackTrace();
                        System.out.println("Cannot bind"+ip+":"+port);
			return false;
                }
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void receiveData() {/* empty */}

	@Override
	public void run() {
            
		// This replace receive data for TCP case
            System.out.println("Trying to establish link interface IPPort: " +ip+":"+port);
            
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
                
                System.out.println("link interface IPPort: " +ip+":"+port+ "has been created!");
                //ServerSocket serverSocket = null
            } catch (IOException ex) {
                System.out.println("TCPIPInterface of IPPort: " +ip+":"+port+"has been bound");
                //ex.printStackTrace();
            }
            Socket socket=null;
            //while(unterminated){
            
                if(serverSocket!=null){
                    
                    System.out.println("TCPIPInterface, listening IPPort: " +ip+":"+port+ "!");
                    
                    try {
                        while(unterminated){
                            //serverSocket.setSoTimeout(1000);
                            try{
                                serverSocket.setSoTimeout(100);
                                    socket = serverSocket.accept();
                                    
                                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                    Object[] o = (Object[])ois.readObject();
    //				GenericChannel chan = (GenericChannel)im.getChannelInstance(((String)o[0]).trim());
    //				if(chan == null)
    //					throw new RuntimeException("Fatal error : Could not find channel instance "+o[0]);

    //				chan.setBuffer(o);

                                    if(o.length==6){
                                        System.out.println("TCPIPInterface receiving data:" +o[0]+"|"+o[1]+"|"+o[2]+"|"+o[3]+"|"+o[4]+"|"+o[5]+"| from :" +ip+": " +port);
                                    } else if (o.length==7){
                                        System.out.println("TCPIPInterface receiving data:" +o[0]+"|"+o[1]+"|"+o[2]+"|"+o[3]+"|"+o[4]+"|"+o[5]+"|" +o[6]+"|  from :" +ip+": " +port);
                                    }


                                    super.forwardChannelData(o);
                                    socket.setSoLinger(true, 0);
                                    socket.close();

                            }catch (SocketTimeoutException ex){

                            }
                                    //System.out.println("Listening on "+ip+" "+port);

                          }

                        if(socket!=null){
                            socket.close();
                            serverSocket.close();
                        }
                        
                        System.out.println("TCPIPInterface, IPPort: " +ip+":"+port+ " closed!");

                          timeoutcomplete=true;
                            //}
                    }

                    catch (BindException bex){
                        bex.printStackTrace();
                            try {
                                socket.close();
                                } catch (IOException ex1) {
                                ex1.printStackTrace();
                            }
                    }
                    catch(RuntimeException e){
                            e.printStackTrace();

                            try {
                                socket.close();
                                } catch (IOException ex1) {
                                ex1.printStackTrace();
                            }
                            //System.exit(1);
                    }
                    catch(ClassNotFoundException e){
                            System.err.println("Fatal error : Could not construct the class from receiving channel data");
                            e.printStackTrace();
                            //System.exit(1);
                    }
                    catch(IOException e){

                            try {
                                socket.close();
                                } catch (IOException ex1) {
                                ex1.printStackTrace();
                            }

                            System.err.println("Error occured in TCPIPInterface, check the TCP/IP setting in the XML Interface");
                            e.printStackTrace();

                            InterfaceManager im = IMBuffer.getInterfaceManagerConfig();

                            Interconnection ic = im.getInterconnection();

                            //JSONObject js = TCPIPLinkRegistry.GetSSAndPortPair();
                            TCPIPLinkRegistry.removePort(Integer.toString(port));

                            ic.removeRemoteInterfaces(ssname);
                            
                            im.setInterconnection(ic);
                            
                            IMBuffer.SaveInterfaceManagerConfig(im);

                            //System.exit(1);
                        }
                    
                }
		
		//finally{
		//	System.exit(1);
		//}

	    //}
           
        }
}
