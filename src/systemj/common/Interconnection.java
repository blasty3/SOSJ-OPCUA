package systemj.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.interfaces.GenericInterface;

/**
 * Must be compatible with CLDC 1.1
 * 
 * May be put cdmap here?
 * Local : e.g USB
 * Destination : e.g TCP/IP
 * @author hpar081
 */
public class Interconnection {
	private Vector LocalLinks = new Vector();
	private Vector DestLinks = new Vector();
        private Hashtable CDPartnerLocSS = new Hashtable();
        private Vector NewSS = new Vector();
        private Vector DeletedSS = new Vector();
        private JSONObject SSPartPortPair = new JSONObject();
        
        //private static final Object CDPartnerLocSSLock = new Object();
	
	public static class Link{
		//public final String type;
		public Hashtable InterfaceMap = new Hashtable();
		public Vector Keys = new Vector();

		public void addInterface(String SS, GenericInterface gct){
			Keys.addElement(SS);
			if(InterfaceMap.containsKey(SS))
				System.err.println("WARNING : SubSystem "+SS+" overwritten in the Interconnection");
			
			InterfaceMap.put(SS, gct);
		}
                
                public boolean checkInterface(String SS){
                    if (InterfaceMap.containsKey(SS)){
                        return true;
                    } else {
                        return false;
                    }
                }
                
                public GenericInterface getInterface(String SS){
                    return (GenericInterface) InterfaceMap.get(SS);
                }
                
                public boolean IsInterfaceEmpty(){
                    if (InterfaceMap.size()==0){
                        return true;
                    } else {
                        return false;
                    }
                }
	}
	
        
        /*
        public void AddChanLinkUserToSS(String DestSSname, String cdName, String ChanDir, String ChanName){
            
            if(CDPartnerLocSS.containsKey(DestSSname)){
                
                Vector vec = (Vector) CDPartnerLocSS.get(DestSSname);
                
                for(int i=0;i<vec.size();i++){
                    
                   Hashtable hash = (Hashtable)vec.get(i);
                   
                   if(hash.containsKey(cdName)){
                       
                       Hashtable hashInOutChans = (Hashtable)hash.get(cdName);
                       
                       if(hashInOutChans.containsKey(ChanDir)){
                           
                           Vector InChans = (Vector) hashInOutChans.get(ChanDir);
                           
                           InChans.addElement(ChanName);
                           
                           hashInOutChans.put(ChanDir,InChans);
                           
                           hash.put(cdName, hashInOutChans);
                           
                           vec.remove(i);
                           
                           vec.addElement(hash);
                           //vec.insertElementAt(hash, i);
                           
                       } else {
                           Vector InChans = new Vector();
                           InChans.addElement(ChanName);
                           hashInOutChans.put(ChanDir, InChans);
                           hash.put(cdName, hashInOutChans);
                           vec.remove(i);
                           vec.addElement(hash);
                           
                           //vec.insertElementAt(hash, i);
                           
                       }
                       
                   } else {
                       
                           Vector InChans = new Vector();
                       
                           InChans.addElement(ChanName);
                           
                           Hashtable hashInOutChans = new Hashtable();
                           
                           hashInOutChans.put(ChanDir, InChans);
                           
                           hash.put(cdName, hashInOutChans);
                           
                           vec.remove(i);
                           vec.addElement(hash);
                          // vec.insertElementAt(hash, i);
                           
                       
                   }
                   
                }
                
                CDPartnerLocSS.put(DestSSname, vec);
                
            } else {
                
                Vector vec = new Vector();
                
                Hashtable hash = new Hashtable();
                
                Hashtable hashInOutChans = new Hashtable();
                
                Vector chans = new Vector();
                
                chans.addElement(ChanName);
                
                hashInOutChans.put(ChanDir, chans);
                
                hash.put(cdName, hashInOutChans);
                
                vec.addElement(hash);
                
                //System.out.println("One InterConnection: " +vec);
                
                CDPartnerLocSS.put(DestSSname, vec);
               
            }
            
            //System.out.println("All InterConnection: " +CDPartnerLocSS);
            
        }
        
        public void removeChanPartner(String ssname,String cdName, String ChanDir,String ChanName){
            
                if(CDPartnerLocSS.containsKey(ssname)){
                
                    Vector vec = (Vector) CDPartnerLocSS.get(ssname);

                    for(int i=0;i<vec.size();i++){
                       Hashtable hash = (Hashtable)vec.get(i);

                       if(hash.containsKey(cdName)){

                           Hashtable hashInOutChans = (Hashtable)hash.get(cdName);

                           if(hashInOutChans.containsKey(ChanDir)){

                               Vector InChans = (Vector) hashInOutChans.get(ChanDir);

                               for(int j=0;j<InChans.size();j++){
                                   
                                   String chanNam = (String)InChans.get(j);
                                   
                                   if(chanNam.equals(ChanName)){
                                       
                                        //InChans.removeElementAt(j);
                                        
                                        if(InChans.size()==1){
                                            hashInOutChans.remove(ChanDir);
                                        } else {
                                            InChans.removeElementAt(j);
                                            hashInOutChans.put(ChanDir,InChans);
                                        }
                                        
                                        if(hashInOutChans.isEmpty()){
                                            hash.remove(cdName);
                                        } else {
                                            hash.put(cdName, hashInOutChans);
                                        }
                                        //hashInOutChans.put(ChanDir,InChans);

                                        //hash.put(cdName, hashInOutChans);
                                        if(hash.isEmpty()){
                                            vec.remove(i);
                                        } else {
                                            //vec.insertElementAt(hash, i);
                                            vec.add(i, hash);
                                        }
                                        //here
                                        if(vec.size()==0){
                                            CDPartnerLocSS.remove(ssname);
                                        } else{
                                            CDPartnerLocSS.put(ssname, vec);
                                        }
                                        //vec.insertElementAt(hash, i);
                                        
                                   }
                                   
                               }
                               
                           } 

                       } 

                    }

                }
               
        }
        
        public boolean IsAnyChanUseLinkToSS(String DestSSname){
            
             if(CDPartnerLocSS.containsKey(DestSSname)){
                 return true;
             } else {
                 return false;
             }
            
        }
        
        public Hashtable GetAllSSPartnerLinkToUse(){
            return CDPartnerLocSS;
        }
              */     
        
        public void AddAdvertisedSSForLinkToList(String SS){
            NewSS.addElement(SS);
        }
        
        public void RemoveAdvertisedSSForLinkToList(String SS){
            for(int i=0;i<NewSS.size();i++){
                String ssname = (String)NewSS.get(i);
                
                if(ssname.equals(SS)){
                    NewSS.removeElementAt(i);
                }
                
            }
        }
        
        public Vector GetAdvertisedSSForLinkToList(){
            
            
             return NewSS;
        }
        
        
        public boolean IsNewAdvSSListEmpty(){
            if(NewSS.size()==0){
                return true;
            } else {
                return false;
            }
        }
        
        public boolean IsDelExistingSSListEmpty(){
            if(DeletedSS.size()==0){
                return true;
            } else {
                return false;
            }
        }
        
        public void AddNonAvailSSForLinkToList(String SS){
            DeletedSS.addElement(SS);
        }
        
        public void RemoveNonAvailSSForLinkToList(String SS){
            for(int i=0;i<DeletedSS.size();i++){
                String ssname = (String)DeletedSS.get(i);
                
                if(ssname.equals(SS)){
                    DeletedSS.removeElementAt(i);
                }
                
            }
        }
	
	public void printInterconnection(){
		System.out.println("Local links : ");
		for(int i=0;i<LocalLinks.size(); i++){
			System.out.println(i+":");
			Link l = (Link)LocalLinks.elementAt(i);
			Enumeration enumm = l.InterfaceMap.keys();
			while(enumm.hasMoreElements()){
				Object key = enumm.nextElement();
				System.out.println(key+" "+l.InterfaceMap.get(key));
			}
		}
		
		System.out.println("Destination links : ");
		for(int i=0;i<DestLinks.size(); i++){
			System.out.println(i+":");
			Link l = (Link)DestLinks.elementAt(i);
			Enumeration enumm = l.InterfaceMap.keys();
			while(enumm.hasMoreElements()){
				Object key = enumm.nextElement();
				System.out.println(key+" "+l.InterfaceMap.get(key));
			}
		}
	}

	public void addLink(Link link, boolean isLocal){
		if(isLocal)
			LocalLinks.addElement(link);
		else
                    
                    
			DestLinks.addElement(link);
	}
        
        public void removeLink (Link link, boolean isLocal){
            
            if (isLocal)
                    LocalLinks.removeElement(link);
                else
                    DestLinks.removeElement(link);
            
        }
        
        
	
	/**
	 * @param ssname
	 * @return Returns Vector array of interfaces with the Subsystem ssname
	 */
	public Vector getInterfaces(String ssname){
		Vector l = new Vector();
		for(int i=0; i<LocalLinks.size() ; i++){
			GenericInterface gct = (GenericInterface)((Link)LocalLinks.elementAt(i)).InterfaceMap.get(ssname);
			if(gct != null)
				l.addElement(gct);
			
		}
		for(int i=0; i<DestLinks.size() ; i++){
			GenericInterface gct = (GenericInterface)((Link)DestLinks.elementAt(i)).InterfaceMap.get(ssname);
			if(gct != null)
				l.addElement(gct);
		}
		
		return l;
	}
        
        /**
	 * @param ssname
	 * @return Returns Vector array of destination\remote interfaces with the Subsystem ssname
	 */
	public Vector getRemoteDestinationInterfaces(String ssname){
		Vector l = new Vector();
		
		for(int i=0; i<DestLinks.size() ; i++){
			GenericInterface gct = (GenericInterface)((Link)DestLinks.elementAt(i)).InterfaceMap.get(ssname);
			if(gct != null)
				l.addElement(gct);
		}
		
		return l;
	}
        
        public Vector getLocalInterfaces(String ssname){
		Vector l = new Vector();
		for(int i=0; i<LocalLinks.size() ; i++){
			GenericInterface gct = (GenericInterface)((Link)LocalLinks.elementAt(i)).InterfaceMap.get(ssname);
			if(gct != null)
				l.addElement(gct);
			
		}
		
		return l;
	}
        
        public boolean hasLocalInterfaces(String ssname){
            
                boolean bool = false;
		
		for(int i=0; i<LocalLinks.size() ; i++){
			if (((Link)LocalLinks.elementAt(i)).InterfaceMap.containsKey(ssname))
                            bool = true;
                    
		}
                
                return bool;
            
        }
        
        public boolean hasRemoteDestinationInterfaces(String ssname){
            
                boolean bool = false;
		
		for(int i=0; i<DestLinks.size() ; i++){
			if (((Link)DestLinks.elementAt(i)).InterfaceMap.containsKey(ssname))
                            bool = true;
                    
		}
                
                return bool;
            
        }
       
        
        public void removeInterfaces(String ssname){
            
            
            
		for(int i=0; i<LocalLinks.size() ; i++){
			((Link)LocalLinks.elementAt(i)).InterfaceMap.remove(ssname);
                        
			
		}
		for(int i=0; i<DestLinks.size() ; i++){
			((Link)DestLinks.elementAt(i)).InterfaceMap.remove(ssname);
				
		}
		
		
            
        }
	
        public void removeRemoteInterfaces(String ssname){
            
            
		
		for(int i=0; i<DestLinks.size() ; i++){
			((Link)DestLinks.elementAt(i)).InterfaceMap.remove(ssname);
				
		}
		
		
            
        }
        
	/**
	 * 
	 * @param me Local SubSystem name
	 * @param target Remote SubSystem name
	 * @return Vector array of GenericInterfaces that can be used for transmitting data.
	 */
	public Vector getInterfaces(String me, String target){
		Vector l = new Vector();
		for(int i=0;i<LocalLinks.size(); i++){
			GenericInterface local = (GenericInterface)((Link)LocalLinks.elementAt(i)).InterfaceMap.get(me);
			GenericInterface remote = (GenericInterface)((Link)LocalLinks.elementAt(i)).InterfaceMap.get(target);
			if(remote !=null && local !=null)
				l.addElement(local); // Using local (e.g. USB)
		}
		
		for(int i=0; i<DestLinks.size() ; i++){
			GenericInterface local = (GenericInterface)((Link)DestLinks.elementAt(i)).InterfaceMap.get(me);
			GenericInterface remote = (GenericInterface)((Link)DestLinks.elementAt(i)).InterfaceMap.get(target);
			if(remote !=null && local !=null)
				l.addElement(remote); // Using remote's (e.g. TCP/IP)
		}
		return l;
	}
        
        /*
        public InterfaceManager CreateNewLink(InterfaceManager im, String SS){
        
        try{
            
            int portNum=60001;
            boolean portStat = true;
            boolean portFull=false;
            
                                        Interconnection.Link linko = new Interconnection.Link();
            
                                        String clazz = "systemj.desktop.TCPIPInterface";
                                        
                                        //get IP from service registry!
                                        
                                        
                                        JSONObject jsAllServ = SJServiceRegistry.obtainCurrentRegistry();
                                        
                                        //
                                        Enumeration keysjsAllServ = jsAllServ.keys();
                                        
                                        while (keysjsAllServ.hasMoreElements()){
                                            
                                            String ssName = keysjsAllServ.nextElement().toString();
                                            
                                            if(!ssName.equals(SJSSCDSignalChannelMap.getLocalSSName()) && ssName.equals(SS)){
                                                
                                                JSONObject jsServs = jsAllServ.getJSONObject(ssName);
                                                
                                                Enumeration keysjsServs = jsServs.keys();
                                                
                                                while(keysjsServs.hasMoreElements()){
                                                    String servName = keysjsServs.nextElement().toString();
                                                    
                                                    JSONObject jsServDet = jsServs.getJSONObject(servName);
                                                    
                                                    String Addr = jsServDet.getString("nodeAddress");
                                                    
                                                    // find the available portNum;
                                                    
                                                    
                                                    //if(SSPartPortPair.containsValue(portNum)){
                                                    
                                                    while(portStat){
                                                        
                                                        if(SSPartPortPair.hasValue(Integer.toString(portNum))){  
                                                            portNum++;
                                                            if(portNum==100000){
                                                                portFull=true;
                                                                portStat=false;
                                                               
                                                            } 

                                                        } else {
                                                            portStat = false;
                                                            
                                                            SSPartPortPair.put(SS, Integer.toString(portNum));
                                                            
                                                        }
                                                        
                                                    }
                                                    
                                                    if(!portFull){
                                                        
                                                        String args = Addr+":"+portNum; 
        
                                                        GenericInterface gct = (GenericInterface)Class.forName(clazz).newInstance();
                                                        Hashtable ht = new Hashtable();
                                                        ht.put("Class", clazz);
                                                        //ht.put("Interface", interf);
                                                        ht.put("Args", args);
                                                        ht.put("SubSystem", SS);
                                                        gct.configure(ht);
                                                        linko.addInterface(SS, gct);

                                                        //String type = link.getAttributeValue("Type") ;

                                                        //type always 'Destination'

                                                        this.addLink(linko, false);
                                                        //SJSSCDSignalChannelMap.addLinkType((Object)linko,type);

                                                        im.setInterconnection(this);
                                                        
                                                        
                                                    }
                                                    
                                                    break;
                                                    
                                                }
                                                
                                                
                                                
                                            }
                                            
                                        }
                                        
                        return im;
            
        } catch (Exception ex){
            ex.printStackTrace();
            return im;
        }
                                        
    }
        */
        
        
	
        
        
        
	
}
