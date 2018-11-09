package systemj.common.SOAFacility.Mig;
import static systemj.common.SOAFacility.Mig.Utils.log;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class SJMethodVisitor extends MethodVisitor {

	private SJClassVisitor scv;

	public SJMethodVisitor(int api, SJClassVisitor scv) {
		super(api);
		this.scv = scv;
	}

	@Override
	public void visitTypeInsn(int opcode, String t) {
		Type type = Type.getObjectType(t);
		log.info("Tries to parse type instruction " + type.getClassName());
		scv.visitClass(type);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		Type type = Type.getObjectType(owner);
		log.info("Tries to parse method invoke instruction " + type.getClassName());
		scv.visitClass(type);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type) {
			scv.analyzeDescriptor((Type) cst);
		}
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		scv.analyzeDescriptor(Type.getType(desc));
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		if (type != null) {
			Type t = Type.getObjectType(type);
			log.info("Tries to parse exception in the catch block " + t.getClassName());
			scv.visitClass(t);
		}
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		scv.analyzeDescriptor(Type.getType(desc));
		return null;
	}

}


