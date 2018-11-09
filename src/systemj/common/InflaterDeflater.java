package systemj.common;  
 import java.io.ByteArrayOutputStream;  
 import java.io.File;  
 import java.io.FileInputStream;  
 import java.io.FileOutputStream;  
 import java.io.IOException;  
 import java.io.InputStream;  
 import java.util.List;  
 import java.util.Map;  
import java.util.logging.Logger;
 import java.util.zip.DataFormatException;  
 import java.util.zip.Deflater;  
 import java.util.zip.Inflater;  
 public class InflaterDeflater {  
 // private static final Logger LOG = Logger.getLogger(InflaterDeflater.class);  
  public static synchronized byte[] compress(byte[] data) throws IOException {  
   Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
   
   deflater.setInput(data);  
   ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
   deflater.finish();  
   byte[] buffer = new byte[1024];   
   while (!deflater.finished()) {  
    int count = deflater.deflate(buffer); // returns the generated code... index  
    outputStream.write(buffer, 0, count);   
   }  
   outputStream.close();  
   byte[] output = outputStream.toByteArray();  
   System.out.println("Original: " + data.length / 1024 + " Kb");  
   System.out.println("Compressed: " + output.length / 1024 + " Kb");  
   return output;  
  }  
  public static synchronized byte[] decompress(byte[] data) throws IOException, DataFormatException {  
   Inflater inflater = new Inflater();
   
   inflater.setInput(data);  
   ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
   byte[] buffer = new byte[1024];  
   while (!inflater.finished()) {  
    int count = inflater.inflate(buffer);  
    outputStream.write(buffer, 0, count);  
   }  
   outputStream.close();  
   byte[] output = outputStream.toByteArray();  
   System.out.println("Compressed: " + data.length);  
   System.out.println("Uncompressed: " + output.length);  
   return output;  
  }  
 }