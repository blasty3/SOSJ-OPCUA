package systemj.common.SOAFacility.Mig;

import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

public class SJClassLoader extends ClassLoader {
	
	private HashMap<String, String> binaries  = new HashMap<String, String>();
	
	public SJClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public void putBinary(String name, String bin){
		binaries.put(name, bin);
	}
	
	public void copyBinaries(SJClassLoader bin){
		this.binaries = new HashMap<String, String>(bin.binaries);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (binaries.containsKey(name)) {
			String bin = binaries.get(name);
			byte[] b = DatatypeConverter.parseBase64Binary(bin);
			return defineClass(name, b, 0, b.length);
		}
		
		return super.findClass(name);
	}
	
	
}
