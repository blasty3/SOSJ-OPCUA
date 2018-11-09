package systemj.common.SOAFacility.Mig;

import java.io.IOException;
import java.net.URI;
import javax.tools.JavaFileObject;

import javax.tools.SimpleJavaFileObject;

public class JavaSource extends SimpleJavaFileObject {
	final String code;
	
	JavaSource(String name, String code){
		super(URI.create("string:///"+name.replace('.', '/')+JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return code;
	}
}
