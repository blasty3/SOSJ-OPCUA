package systemj.signals;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Hashtable;
import systemj.interfaces.GenericSignalReceiver;

public class InputFileReader extends GenericSignalReceiver implements Serializable{
	String filename;
	BufferedReader br;
	Object[] ob = new Object[2];
	@Override
	public void configure(Hashtable data) throws RuntimeException{
		filename = (String)data.get("File");
		try{
			br = new BufferedReader(new FileReader(filename));
		}catch(FileNotFoundException e){e.printStackTrace();}
	}
	@Override
	public void getBuffer(Object[] obj){
		try{
			String line = br.readLine();
			if(line == null){
				br.close();
				br = new BufferedReader(new FileReader(filename));
				line = br.readLine();
			}
			if(line.trim().equals(";")){
				obj[0] = Boolean.FALSE;
			}
			else{
				obj[0] = Boolean.TRUE;
				obj[1] = line;
			}
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	public void run() { } 
	public InputFileReader(){
		super(); // Initializes the buffer
	}
}
