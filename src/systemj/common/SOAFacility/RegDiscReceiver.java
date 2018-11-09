package systemj.common.SOAFacility;

import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SOAFacility.Support.SOABuffer;

public class RegDiscReceiver implements Runnable{

    MulticastSocket socket = null;
    
    @Override
    public void run() {
        
        NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        System.out.println("RegMessageReceiver thread started");
        
        while(true){
        
                String connStat = netcheck.CheckNetworkConn(SOABuffer.getGatewayAddr(), 1800);
               
                if (connStat.equalsIgnoreCase("Connected")){
                    
                    ReceiveSOAMsg();
                   
                } else {
                    
                    if(socket!=null){
                        socket.close();
                    }
                }
        }
        
    }
   
    private void ReceiveSOAMsg(){
        JSONObject js = new JSONObject();
                            try
                            {
                                    socket = new MulticastSocket(199);
                                  
                                        DatagramPacket pack; 
                                       
                                        while (true){
                                            byte data[];
                                            byte packet[] = new byte[65508];
                                            pack = new DatagramPacket(packet, packet.length);
                                            socket.receive(pack);
                                                data = new byte[pack.getLength()];
                                                System.arraycopy(packet, 0, data, 0, pack.getLength());
                                                if(data.length > 0)
                                                {
                                                        if(((int)data[0] == -84) && ((int)data[1] == -19))
                                                        {
                                                                try
                                                                {
                                                                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                                                        Object mybuffer = ois.readObject();  
                                                                        {
                                                                              js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                                                               if(js.getString("regID").equals(SOABuffer.getSOSJRegID()))
                                                                               {
                                                                                  SOABuffer.putDiscMsgToDiscBuffer(js);
                                                                               } 
                                                                        }
                                                                }
                                                                catch(Exception e)
                                                                {
                                                                        e.printStackTrace();
                                                                }
                                                        }
                                                }
                                         }
                        }
                        catch (SocketException se)
                        {
                                se.printStackTrace();
                               // SOABuffer.setIsInitAdvDone(false);
                                //SOABuffer.setIsInitDiscDone(false);
                        }
                        catch (Exception e)
                        {
                                e.printStackTrace();
                                //SOABuffer.setIsInitAdvDone(false);
                                //SOABuffer.setIsInitDiscDone(false);
                        }
        }
}