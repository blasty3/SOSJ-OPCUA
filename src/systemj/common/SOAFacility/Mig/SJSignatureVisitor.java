package systemj.common.SOAFacility.Mig;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

public class SJSignatureVisitor extends SignatureVisitor {

	private SJClassVisitor scv;

	public SJSignatureVisitor(int api, SJClassVisitor scv) {
		super(api);
		this.scv = scv;
	}

	@Override
	public void visitClassType(String name) {
		scv.visitClass(Type.getObjectType(name));
	}

}
