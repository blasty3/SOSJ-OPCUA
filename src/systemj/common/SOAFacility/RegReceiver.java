/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import java.net.MulticastSocket;
import java.net.SocketException;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import systemj.common.InflaterDeflater;

import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.common.SOAFacility.Support.SOABuffer;

public class RegReceiver implements Runnable{

    private MulticastSocket socket=null;
    
    @Override
    public void run() {
        
        NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
       
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
                                    socket = new MulticastSocket(177);
                                   
                                        DatagramPacket pack ;
                                        
                                        while (true){
                                            
                                            byte data[];
                                            
                                            byte packet[] = new byte[65508];
                                            
                                            pack = new DatagramPacket(packet, packet.length);
                                           
                                            socket.receive(pack);
                                          
                                                data = new byte[pack.getLength()];
                                                
                                                System.arraycopy(packet, 0, data, 0, pack.getLength());
     
                                                data = InflaterDeflater.decompress(data);
                                                
                                                if(data.length > 0)
                                                {
                                                        if(((int)data[0] == -84) && ((int)data[1] == -19))
                                                        {
                                                                try
                                                                {
                                                                       
                                                                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                                                        Object mybuffer = ois.readObject();              
                                                                       
                                                                              js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                                                             
                                                                               String msgType = js.getString("MsgType");
                        
                                                                                System.out.println("RegLocalMessageReceiver, RcvdMsgType: " +msgType);

                                                                                if (msgType.equalsIgnoreCase("Advertisement") || msgType.equalsIgnoreCase("responseProvToRegReqAdvertise")){

                                                                                    SJServiceRegistry.SaveAdvertisedServices(js);

                                                                            } else if (msgType.equalsIgnoreCase("regRequestAdvertise")){

                                                                                    try {
                                                                                        String regID = js.getString("regID");

                                                                                        if(regID.equalsIgnoreCase(SOABuffer.getSOSJRegID())){
                                                                                            SOABuffer.putReqAdvToReqAdvBuffer(js);
                                                                                        }
                                                                                    } catch (JSONException ex) {
                                                                                        ex.printStackTrace();
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
                        }
                        catch (Exception e)
                        {
                                e.printStackTrace(); 
                        }
    }
}
