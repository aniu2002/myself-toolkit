package com.sparrow.core.aop.adapter;

import org.asm.MethodVisitor;
import static org.asm.Opcodes.*;

public class ConstructorMethodAdapter extends NullMethodAdapter {

	private String superClassName;

	public ConstructorMethodAdapter(MethodVisitor mv, String desc, int access,
			String superClassName) {
		super(mv, desc, access);
		this.superClassName = superClassName;
	}

	public void visitCode() {
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		loadArgs();
		mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", desc);
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

}
