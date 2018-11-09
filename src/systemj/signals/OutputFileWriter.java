package systemj.signals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalSender;

public class OutputFileWriter extends GenericSignalSender implements Serializable {
	String signalName = "";
	String dir;
	File file;
	BufferedWriter writer;
	
	public OutputFileWriter(){
		super();
	}
	
	@Override
	public void configure(Hashtable data) throws RuntimeException {
		if(data.containsKey("Name")){
			signalName = (String)data.get("Name");
		} else throw new RuntimeException("The configuration parameter 'Name' is required!");
		
		if(data.containsKey("Path")){
			dir = (String) data.get("Path");
		} else throw new RuntimeException("The configuration parameter 'Path' is required!");
	
		try{
			String fileName = new String(dir + String.valueOf(System.currentTimeMillis()));
			file =  new File(fileName);
			file.createNewFile();			
		}catch(IOException ioe){ioe.printStackTrace();}
	
	}

	@Override
	public void run() {
		Object[] obj = super.buffer;
		String data = (String) obj[1];
		
		try{
			writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(data,0,data.length());
			writer.flush();		
			writer.close();
		} catch(IOException e){
			e.printStackTrace();
		}		
	}
}
