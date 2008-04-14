package sneer.lego.impl.classloader;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MakeSerializable implements Enhancer {
	
	static class MakeSerializableVisitor extends ClassAdapter {

		private boolean _containsSerialVersionUID;

		public MakeSerializableVisitor(ClassVisitor classVisitor) {
			super(classVisitor);
		}
		
		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			super.visit(version, access, name, signature, superName, addToArray(interfaces, "java/io/Serializable"));
		}
		
		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			if (name.equals("serialVersionUID")) {
				_containsSerialVersionUID = true;
			}
			return super.visitField(access, name, desc, signature, value);
		}
		
		@Override
		public void visitEnd() {
			if (!_containsSerialVersionUID) {
				super.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "serialVersionUID", Type.LONG_TYPE.getDescriptor(), null, new Long(1L));
			}
			super.visitEnd();
		}

		private String[] addToArray(String[] array, String value) {
			int length = array.length;
			String[] result = new String[length + 1];
			System.arraycopy(array, 0, result, 0, length);
			result[length] = value;
			return result;
		}
	}

	@Override
	public ClassVisitor enhance(ClassVisitor visitor) {
		return new MakeSerializableVisitor(visitor);
	}

}
