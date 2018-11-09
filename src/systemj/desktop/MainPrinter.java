package systemj.desktop;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Hashtable;
import org.json.me.*;


public class MainPrinter {
	private boolean gen = false;
        private boolean genSPOT=false;
	private String filename;
        private String filenameSPOT;
	private PrintWriter pw;
        private PrintWriter pwSPOT;
	private int indent = 1;
        private int indentSPOT=1;
	private StringBuilder sb =new StringBuilder();
	public boolean genSunspot = false;
        

	public void setGen(String filename) throws FileNotFoundException{ 
		this.filename = filename;
		gen = true;
		pw = new PrintWriter(filename);
		this.printDeclare();
		if(genSunspot) {
			pw.println("import systemj.common.*;\n" + 
				   "import systemj.bootstrap.*;\n" + 
                                   "import systemj.common.SOAFacility.*;\n" + 
                                   "import org.json.me.*;\n" + 
				   "import java.util.*;\n" +
				   "import systemj.lib.*;\n" + 
                                   "import systemj.common.SOAFacility.Support.*;\n"+
				   "import systemj.interfaces.*;\n" +
				   "import com.sun.spot.resources.Resources;\n" +
				   "import com.sun.spot.service.BootloaderListenerService;\n" +
				   "import javax.microedition.midlet.MIDlet;\n" + 
				   "import javax.microedition.midlet.MIDletStateChangeException;\n" +
				   "public class "+filename.split("\\.")[0]+" extends MIDlet{\n" +
                                    "\tJSONObject stateVarServTot = new JSONObject();\n" +
                
                                    "\tJSONObject localServAttr = new JSONObject();\n"+
                                    "\tJSONObject actionServ = new JSONObject();\n"+
                                    "\tJSONObject stateVarServ = new JSONObject();\n"+
                                    "\tJSONObject actionServTot = new JSONObject();\n"+
                                    "\tJSONObject localServAttrPrnt = new JSONObject();\n"+
				   "\tprotected void startApp() throws MIDletStateChangeException {\n" +
				   "\t\tBootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host\n" +
				   "\t\tSystemJProgram program = new SystemJProgram();");
		}
		else {
			pw.println("import systemj.common.*;\n" +
					 "import systemj.bootstrap.*;\n" +
					 "import java.util.*;\n" +
					 "import systemj.lib.*;\n" +
					 "import systemj.interfaces.*;\n"+
					 "public class "+filename.split("\\.")[0]+"{\n" +
					 "\tpublic static void main(String[] arg){\n" +
					 "\t\tSystemJProgram program = new SystemJProgram();");
		}
		indent++;
	}
        
        public void setGenServDesc(String filename) throws FileNotFoundException{
            
            this.filenameSPOT = filename;
		genSPOT = true;
		pwSPOT = new PrintWriter(filename);
		this.printDeclareSPOT();
		if(genSunspot) {
			pwSPOT.println(//"import systemj.common.*;\n" + 
				  // "import systemj.bootstrap.*;\n" + 
				   "import java.util.*;\n" +
				  // "import systemj.lib.*;\n" + 
				  // "import systemj.interfaces.*;\n" +
				  // "import com.sun.spot.resources.Resources;\n" +
				  // "import com.sun.spot.service.BootloaderListenerService;\n" +
				  // "import javax.microedition.midlet.MIDlet;\n" +
                                   "import org.json.me.*;\n" +
				  //  "import javax.microedition.midlet.MIDletStateChangeException;\n" +
				   "public class "+filename.split("\\.")[0]+"{\n" +
                                 //  "private static JSONObject ServiceDescription = new JSONObject();\n" +
                                   // Initial service description content saved in a String object in JSON format
                                   
				  // "\tprotected void startApp() throws MIDletStateChangeException {\n" +
				  // "\t\tBootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host\n" +
				  // "\t\tSystemJProgram program = new SystemJProgram();");
                                 "");
		}
		else {
			pwSPOT.println("import systemj.common.*;\n" +
					 "import systemj.bootstrap.*;\n" +
					 "import java.util.*;\n" +
					 "import systemj.lib.*;\n" +
					 "import systemj.interfaces.*;\n"+
					 "public class "+filename.split("\\.")[0]+"{\n" +
					 "\tpublic static void main(String[] arg){\n" +
					 "\t\tSystemJProgram program = new SystemJProgram();");
		}
		indentSPOT++;
        }
        
	public void printDeclare(){
		Calendar c = Calendar.getInstance();
		pw.println("" +
				"/*\n" +
				"*  This file is part of SystemJ Runtime Enviornment\n" +
				"*  Generated at "+c.get(Calendar.HOUR_OF_DAY)+":"+String.format("%02d", c.get(Calendar.MINUTE))+", "+c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+"\n" +
				"*/");
	}
        
        public void printDeclareSPOT(){
		Calendar c = Calendar.getInstance();
		pwSPOT.println("" +
				"/*\n" +
				"*  This file is part of SystemJ Runtime Enviornment\n" +
				"*  Generated at "+c.get(Calendar.HOUR_OF_DAY)+":"+String.format("%02d", c.get(Calendar.MINUTE))+", "+c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+"\n" +
				"*/");
	}
        
	public void println(String n){
		if(gen){
			if(n.endsWith("}"))
				indent--;
			for(int i=0;i<indent;i++)
				pw.print("\t");
			if(n.endsWith("{"))
				indent++;
			pw.println(n);
		}
	}
        
        public void printlnSDJSONString(String JSONSDString ){
		if(genSPOT){
			//if(n.endsWith("}"))
			//	indent--;
			//for(int i=0;i<indent;i++)
			//	pw.print("\t");
			//if(n.endsWith("{"))
			//	indent++;
			//pwSPOT.println(n);
                       
                    
                    //System.out.println(jsonSDBytes);
                        pwSPOT.println("\t\tprivate static Vector ServiceDescription = new Vector();\n");
                        pwSPOT.println("");
                    
		}
	}
        
        public void printlnSDJSON(JSONObject JSONSDObject ){
		if(genSPOT){
			//if(n.endsWith("}"))
			//	indent--;
			//for(int i=0;i<indent;i++)
			//	pw.print("\t");
			//if(n.endsWith("{"))
			//	indent++;
			//pwSPOT.println(n);
                    
                    //System.out.println(jsonSDBytes);
                    pwSPOT.println("\t\tprivate static JSONObject ServiceDescription=\n " +JSONSDObject+";\n");
                    
		}
	}
        
        public void printlnSDJSONByte(byte[] JSONSDObject ){
		if(genSPOT){
			//if(n.endsWith("}"))
			//	indent--;
			//for(int i=0;i<indent;i++)
			//	pw.print("\t");
			//if(n.endsWith("{"))
			//	indent++;
			//pwSPOT.println(n);
                    
                    //System.out.println(new String(JSONSDObject));
                    //gr
                    pwSPOT.println("\t\tprivate static byte[] ServiceDescription=\n " +JSONSDObject.toString()+";\n");
                    
		}
	}

	public void flush(){
		if(gen){
			System.out.println("generated : "+filename);
			pw.println("}");
			if(genSunspot) {
				pw.println("protected void pauseApp() {");
				pw.println("}");
				pw.println("protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {");
				pw.println("}");
			}
			pw.println("}");
			pw.flush();
                        
			pw.close();
                        
			//System.exit(1);
		}
	}
        
        public void setGenTest(String filename) throws FileNotFoundException{ 
		this.filenameSPOT = filename;
		gen = true;
		pwSPOT = new PrintWriter(filename);
		this.printDeclareSPOT();
		if(genSunspot) {
			pwSPOT.println("import systemj.common.*;\n" + 
				   "import systemj.bootstrap.*;\n" + 
                                   "import systemj.common.*;\n" +
                                   "import systemj.common.SOAFacility.*;\n" + 
				   "import java.util.*;\n" +
				   "import systemj.lib.*;\n" + 
				   "import systemj.interfaces.*;\n" +
				   "import com.sun.spot.resources.Resources;\n" +
				   "import com.sun.spot.service.BootloaderListenerService;\n" +
				   "import javax.microedition.midlet.MIDlet;\n" + 
				   "import javax.microedition.midlet.MIDletStateChangeException;\n" +
				   "public class "+filename.split("\\.")[0]+" extends MIDlet{\n" +
				   "\tprotected void startApp() throws MIDletStateChangeException {\n" +
				   "\t\tBootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host\n" +
				   "");
		}
		else {
			pwSPOT.println("import systemj.common.*;\n" +
					 "import systemj.bootstrap.*;\n" +
					 "import java.util.*;\n" +
					 "import systemj.lib.*;\n" +
					 "import systemj.interfaces.*;\n"+
					 "public class "+filename.split("\\.")[0]+"{\n" +
					 "\tpublic static void main(String[] arg){\n" +
					 "");
		}
		indent++;
	}
        
        
        
        public void addSPOTDummyServDesc(){
            pwSPOT.println("\t\tSJServiceRegistry.setSPOTRemoteInitialRegistrySOATest();");
        }
        
        public void flushSPOTTest(){
		
			System.out.println("generated : "+filenameSPOT);
			
                        if (genSunspot){
                            
                            pwSPOT.println("\t\t\t\tStartSOAThread();\n");
                            
                            pwSPOT.println("}");
			    
                            pwSPOT.println("private void StartSOAThread(){\n");
                            pwSPOT.println("\t\t\t\tThread expirychecker = new Thread(new ExpiryCheckerThread()); \n");
                            pwSPOT.println("\t\t\t\tThread spotmsgudpreceiver = new Thread(new SPOTUDPMessageReceiverThread()); \n");
                            pwSPOT.println("\t\t\t\tThread spotmsgradiogramreceiver = new Thread(new SPOTRadiogramMessageReceiverThread()); \n");
                            pwSPOT.println("\t\t\t\tThread spotmsgsender = new Thread(new SPOTMessageSenderThread()); \n");
                            
                            pwSPOT.println("\t\t\t\texpirychecker.start();\n");
                            pwSPOT.println("\t\t\t\tspotmsgudpreceiver.start(); \n");
                            pwSPOT.println("\t\t\t\tspotmsgradiogramreceiver.start(); \n");
                            pwSPOT.println("\t\t\t\tspotmsgsender.start(); \n");
                          
                            //pwSPOT.println("public static JSONObject getServDesc() {\n");
                            //pwSPOT.println("\t\t\t\tJSONObject jsSD = new JSONObject(new JSONTokener(ServiceDescription));\n");
                            //pwSPOT.println("\t\t\t\treturn jsSD;\n");
                            pwSPOT.println("\t\t}");
                            pwSPOT.println("protected void pauseApp() {");
				pwSPOT.println("}");
				pwSPOT.println("protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {");
				pwSPOT.println("}");
                                pwSPOT.println("}");
                        }
                        pwSPOT.flush();
                        
                        pwSPOT.close();
			
			//System.exit(1);
		
	}
        
        public void flushSDSPOT(){
		if(genSPOT){
			System.out.println("generated : "+filenameSPOT);
			
			//if(genSunspot) {
			//	this.println("protected void pauseApp() {");
			//	this.println("}");
			//	this.println("protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {");
			//	this.println("}");
			//}
                        
                        if (genSunspot){
                            pwSPOT.println("public static JSONObject getServDesc() {\n");
                            /*
                            String[] byteValues = response.substring(1, response.length() - 1).split(",");
                            byte[] bytes = new byte[byteValues.length];

                            for (int i=0, len=bytes.length; i<len; i++) {
                                bytes[i] = Byte.parseByte(byteValues[i].trim());     
                            }

                            String str = new String(bytes);
                            */
                            //pwSPOT.println("\t\t\t\tString[] byteValues = ServiceDescription.substring(1, response.length() - 1).split(\",\");\n");
                            //pwSPOT.println("\t\t\t\tbyte[] bytes = new byte[byteValues.length];\n");
                            //pwSPOT.println("\t\t\t\tfor (int i=0, len=bytes.length; i<len; i++) {\n");
                            pwSPOT.println("\t\t\t\tJSONObject jsSD = new JSONObject(new JSONTokener(new String(ServiceDescription)));\n");
                            pwSPOT.println("\t\t\t\tSystem.out.println(jsSD.toString());\t");
                            pwSPOT.println("\t\t\t\treturn jsSD;\n");
                            pwSPOT.println("\t\t}");
                        }
                        
			pwSPOT.println("}");
                        /*
                        pwSPOT.println("\t\tprotected void pauseApp() {");
				pwSPOT.println("\t\t}");
				pwSPOT.println("\t\tprotected void destroyApp(boolean unconditional) throws MIDletStateChangeException {");
				pwSPOT.println("\t\t}");
                        pwSPOT.println("}");
                                */
			pwSPOT.flush();
                        
			pwSPOT.close();
                        
			//System.exit(1);
		}
	}
	
	public boolean isGenerating(){ return gen;}
        public boolean SDSPOTisGenerating(){ return genSPOT;}
	public void toBuffer(String n){
		if(gen){
			if(n.endsWith("}"))
				indent--;
			for(int i=0;i<indent;i++)
				sb.append("\t");
			if(n.endsWith("{"))
				indent++;
			sb.append(n+"\n");
		}
	}
	public void clearBuffer(){
		if(gen)
			sb.setLength(0);
	}
	public void writeBuffer(){
		if(gen)
			pw.print(sb.toString());
	}
}
