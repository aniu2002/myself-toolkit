package com.sparrow.core.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.asm.ClassWriter;
import org.asm.MethodVisitor;
import org.asm.Opcodes;
import org.asm.Type;

import com.sparrow.core.aop.adapter.AopMethodAdapter;
import com.sparrow.core.aop.adapter.ConstructorMethodAdapter;


public class ClassGenerator<T> implements Opcodes {
	private ClassWriter cw;
	private String myName;
	private String superClassName;
	private Method[] methodArray;
	private Constructor<T>[] constructors;

	protected ClassGenerator(Class<T> klass, String myName,
			Method[] methodArray, Constructor<T>[] constructors) {
		this.myName = myName.replace('.', '/');
		this.superClassName = klass.getName().replace('.', '/');
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cw.visit(V1_6, ACC_PUBLIC, this.myName, "", this.superClassName,
				getParentInterfaces(klass));
		this.methodArray = methodArray;
		this.constructors = constructors;
	}

	public void addAopMethods() {
		AopToolkit.addMethods(cw, myName);
	}

	protected static String[] getParentInterfaces(Class<?> xClass) {
		Class<?> its[] = xClass.getInterfaces();
		if (its == null || its.length == 0)
			return new String[] {};
		else {
			String[] iii = new String[its.length];
			for (int i = 0; i < iii.length; i++)
				iii[i] = its[i].getName().replace('.', '/');
			return iii;
		}
	}

	protected void addConstructors() {
		for (Constructor<T> constructor : constructors) {
			String[] expClasses = convertExp(constructor.getExceptionTypes());
			String desc = Type.getConstructorDescriptor(constructor);
			int access = getAccess(constructor.getModifiers());
			MethodVisitor mv = cw.visitMethod(access, "<init>", desc, null,
					expClasses);
			new ConstructorMethodAdapter(mv, desc, access, superClassName)
					.visitCode();
		}
	}

	private String[] convertExp(Class<?>[] expClasses) {
		if (expClasses.length == 0)
			return null;
		String[] results = new String[expClasses.length];
		for (int i = 0; i < results.length; i++)
			results[i] = expClasses[i].getName().replace('.', '/');
		return results;
	}

	protected int getAccess(int modify) {
		if (Modifier.isProtected(modify))
			return ACC_PROTECTED;
		if (Modifier.isPublic(modify))
			return ACC_PUBLIC;
		return 0x00;
	}

	public void addFields() {
		AopToolkit.addFields(cw);
	}

	public void enhandMethod() {
		for (Method method : methodArray) {
			String methodName = method.getName();
			String methodDesc = Type.getMethodDescriptor(method);
			int methodAccess = getAccess(method.getModifiers());
			MethodVisitor mv = cw.visitMethod(methodAccess, methodName,
					methodDesc, null, convertExp(method.getExceptionTypes()));
			int methodIndex = findMethodIndex(methodName, methodDesc,
					methodArray);
			new AopMethodAdapter(mv, methodAccess, methodName, methodDesc,
					methodIndex, myName, superClassName).visitCode();
		}
	}

	protected static int findMethodIndex(String name, String desc,
			Method[] methods) {
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (Type.getMethodDescriptor(method).equals(desc)
					&& method.getName().equals(name))
				return i;
		}
		return -1;// 是否应该抛出异常呢?应该不可能发生的
	}

	protected byte[] toByteArray() {
		addFields();
		addConstructors();
		addAopMethods();
		enhandMethod();
		return cw.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> byte[] enhandClass(Class<T> kclass, String myName,
			Method[] methodArray, Constructor<?>[] constructors) {
		return new ClassGenerator<T>(kclass, myName, methodArray,
				(Constructor<T>[]) constructors).toByteArray();
	}

}
