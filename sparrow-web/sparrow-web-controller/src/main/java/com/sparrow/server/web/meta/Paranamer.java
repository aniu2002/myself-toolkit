package com.sparrow.server.web.meta;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class Paranamer {
	static final String[] EMPTY_NAMES = new String[0];
	static final Map<String, String> primitives = new HashMap<String, String>();
	static final Map<String, ClassReader> readers = new HashMap<String, ClassReader>();
	static {
		primitives.put("int", "I");
		primitives.put("boolean", "Z");
		primitives.put("byte", "B");
		primitives.put("char", "C");
		primitives.put("short", "S");
		primitives.put("float", "F");
		primitives.put("long", "J");
		primitives.put("double", "D");
	};

	private Paranamer() {
	}

	public static void clear() {
		readers.clear();
	}

	public static String[] lookupParameterNames(
			AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	public static String[] lookupParameterNames(AccessibleObject methodOrCtor,
			boolean throwExceptionIfMissing) {
		Class<?>[] types = null;
		Class<?> declaringClass = null;
		String name = null;
		if (methodOrCtor instanceof Method) {
			Method method = (Method) methodOrCtor;
			types = method.getParameterTypes();
			name = method.getName();
			declaringClass = method.getDeclaringClass();
		} else {
			Constructor<?> constructor = (Constructor<?>) methodOrCtor;
			types = constructor.getParameterTypes();
			declaringClass = constructor.getDeclaringClass();
			name = "<init>";
		}

		if (types.length == 0) {
			return EMPTY_NAMES;
		}
		String clzName = declaringClass.getName();
		ClassReader reader = readers.get(clzName);
		if (reader == null) {
			InputStream byteCodeStream = getClassAsStream(declaringClass);
			if (byteCodeStream == null) {
				if (throwExceptionIfMissing)
					throw new RuntimeException("Unable to get class bytes");
				else
					return EMPTY_NAMES;
			}
			try {
				reader = new ClassReader(byteCodeStream);
				byteCodeStream.close();
			} catch (IOException e) {
				if (throwExceptionIfMissing) {
					throw new RuntimeException(
							"IoException while reading class bytes", e);
				} else {
					return EMPTY_NAMES;
				}
			}
			readers.put(clzName, reader);
		}
		TypeCollector visitor = new TypeCollector(name, types,
				throwExceptionIfMissing);
		reader.accept(visitor);
		String[] parameterNamesForMethod = visitor.getParameterNamesForMethod();
		return parameterNamesForMethod;

	}

	private static InputStream getClassAsStream(Class<?> clazz) {
		ClassLoader classLoader = clazz.getClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		return getClassAsStream(classLoader, clazz.getName());
	}

	private static InputStream getClassAsStream(ClassLoader classLoader,
			String className) {
		String name = className.replace('.', '/') + ".class";
		// better pre-cache all methods otherwise this content will be loaded
		// multiple times
		InputStream asStream = classLoader.getResourceAsStream(name);
		if (asStream == null) {
			asStream = Paranamer.class.getResourceAsStream(name);
		}
		return asStream;
	}

	/**
	 * The type collector waits for an specific method in order to start a
	 * method collector.
	 * 
	 * @author Guilherme Silveira
	 */
	private static class TypeCollector {

		private static final String COMMA = ",";

		private final String methodName;

		private final Class<?>[] parameterTypes;
		private final boolean throwExceptionIfMissing;

		private MethodCollector collector;

		private TypeCollector(String methodName, Class<?>[] parameterTypes,
				boolean throwExceptionIfMissing) {
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.throwExceptionIfMissing = throwExceptionIfMissing;
			this.collector = null;
		}

		public MethodCollector visitMethod(int access, String name, String desc) {
			// already found the method, skip any processing
			if (collector != null) {
				return null;
			}
			// not the same name
			if (!name.equals(methodName)) {
				return null;
			}
			Type[] argumentTypes = Type.getArgumentTypes(desc);
			int longOrDoubleQuantity = 0;
			for (Type t : argumentTypes) {
				if (t.getClassName().equals("long")
						|| t.getClassName().equals("double")) {
					longOrDoubleQuantity++;
				}
			}
			int paramCount = argumentTypes.length;
			// not the same quantity of parameters
			if (paramCount != this.parameterTypes.length) {
				return null;
			}
			for (int i = 0; i < argumentTypes.length; i++) {
				if (!correctTypeName(argumentTypes, i).equals(
						this.parameterTypes[i].getName())) {
					return null;
				}
			}
			this.collector = new MethodCollector((Modifier.isStatic(access) ? 0
					: 1), argumentTypes.length + longOrDoubleQuantity);
			return collector;
		}

		private String correctTypeName(Type[] argumentTypes, int i) {
			String s = argumentTypes[i].getClassName();
			// array notation needs cleanup.
			String braces = "";
			while (s.endsWith("[]")) {
				braces = braces + "[";
				s = s.substring(0, s.length() - 2);
			}
			if (!braces.equals("")) {
				if (primitives.containsKey(s)) {
					s = braces + primitives.get(s);
				} else {
					s = braces + "L" + s + ";";
				}
			}
			return s;
		}

		private String[] getParameterNamesForMethod() {
			if (collector == null) {
				return EMPTY_NAMES;
			}
			if (!collector.isDebugInfoPresent()) {
				if (throwExceptionIfMissing) {
					throw new RuntimeException("Parameter names not found for "
							+ methodName);
				} else {
					return EMPTY_NAMES;
				}
			}
			return collector.getResult().split(COMMA);
		}

	}

	/**
	 * Objects of this class collects information from a specific method.
	 * 
	 * @author Guilherme Silveira
	 */
	private static class MethodCollector {

		private final int paramCount;

		private final int ignoreCount;

		private int currentParameter;

		private final StringBuffer result;

		private boolean debugInfoPresent;

		private MethodCollector(int ignoreCount, int paramCount) {
			this.ignoreCount = ignoreCount;
			this.paramCount = paramCount;
			this.result = new StringBuffer();
			this.currentParameter = 0;
			// if there are 0 parameters, there is no need for debug info
			this.debugInfoPresent = paramCount == 0;
		}

		public void visitLocalVariable(String name, int index) {
			if (index >= ignoreCount && index < ignoreCount + paramCount) {
				if (!name.equals("arg" + currentParameter)) {
					debugInfoPresent = true;
				}
				result.append(',');
				result.append(name);
				currentParameter++;
			}
		}

		private String getResult() {
			return result.length() != 0 ? result.substring(1) : "";
		}

		private boolean isDebugInfoPresent() {
			return debugInfoPresent;
		}

	}

	private static class ClassReader {
		public final byte[] b;
		private final int[] items;
		private final String[] strings;
		private final int maxStringLength;
		public final int header;
		final static int FIELD = 9;
		final static int METH = 10;
		final static int IMETH = 11;
		final static int INT = 3;
		final static int FLOAT = 4;

		final static int LONG = 5;
		final static int DOUBLE = 6;

		final static int NAME_TYPE = 12;
		final static int UTF8 = 1;

		private ClassReader(final byte[] b) {
			this(b, 0);
		}

		private ClassReader(final byte[] b, final int off) {
			this.b = b;
			// parses the constant pool
			items = new int[readUnsignedShort(off + 8)];
			int n = items.length;
			strings = new String[n];
			int max = 0;
			int index = off + 10;
			for (int i = 1; i < n; ++i) {
				items[i] = index + 1;
				int size;
				switch (b[index]) {
				case FIELD:
				case METH:
				case IMETH:
				case INT:
				case FLOAT:
				case NAME_TYPE:
					size = 5;
					break;
				case LONG:
				case DOUBLE:
					size = 9;
					++i;
					break;
				case UTF8:
					size = 3 + readUnsignedShort(index + 1);
					if (size > max) {
						max = size;
					}
					break;
				// case HamConstants.CLASS:
				// case HamConstants.STR:
				default:
					size = 3;
					break;
				}
				index += size;
			}
			maxStringLength = max;
			// the class header information starts just after the constant pool
			header = index;
		}

		private ClassReader(final InputStream is) throws IOException {
			this(readClass(is));
		}

		private static byte[] readClass(final InputStream is)
				throws IOException {
			if (is == null) {
				throw new IOException("Class not found");
			}
			try {
				byte[] b = new byte[is.available()];
				int len = 0;
				while (true) {
					int n = is.read(b, len, b.length - len);
					if (n == -1) {
						if (len < b.length) {
							byte[] c = new byte[len];
							System.arraycopy(b, 0, c, 0, len);
							b = c;
						}
						return b;
					}
					len += n;
					if (len == b.length) {
						int last = is.read();
						if (last < 0) {
							return b;
						}
						byte[] c = new byte[b.length + 1000];
						System.arraycopy(b, 0, c, 0, len);
						c[len++] = (byte) last;
						b = c;
					}
				}
			} finally {
				try {
					is.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}

		private void accept(final TypeCollector classVisitor) {
			char[] c = new char[maxStringLength]; // buffer used to read strings
			int i, j; // loop variables
			int u, v; // indexes in b

			// visits the header
			u = header;
			v = items[readUnsignedShort(u + 4)];
			int len = readUnsignedShort(u + 6);
			u += 8;
			for (i = 0; i < len; ++i) {
				u += 2;
			}
			v = u;
			i = readUnsignedShort(v);
			v += 2;
			for (; i > 0; --i) {
				j = readUnsignedShort(v + 6);
				v += 8;
				for (; j > 0; --j) {
					v += 6 + readInt(v + 2);
				}
			}
			i = readUnsignedShort(v);
			v += 2;
			for (; i > 0; --i) {
				j = readUnsignedShort(v + 6);
				v += 8;
				for (; j > 0; --j) {
					v += 6 + readInt(v + 2);
				}
			}

			i = readUnsignedShort(v);
			v += 2;
			for (; i > 0; --i) {
				v += 6 + readInt(v + 2);
			}

			i = readUnsignedShort(u);
			u += 2;
			for (; i > 0; --i) {
				j = readUnsignedShort(u + 6);
				u += 8;
				for (; j > 0; --j) {
					u += 6 + readInt(u + 2);
				}
			}

			// visits the methods
			i = readUnsignedShort(u);
			u += 2;
			for (; i > 0; --i) {
				// inlined in original ASM source, now a method call
				u = readMethod(classVisitor, c, u);
			}

		}

		private int readMethod(TypeCollector classVisitor, char[] c, int u) {
			int v;
			int w;
			int j;
			String attrName;
			int k;
			int access = readUnsignedShort(u);
			String name = readUTF8(u + 2, c);
			String desc = readUTF8(u + 4, c);
			v = 0;
			w = 0;

			// looks for Code and Exceptions attributes
			j = readUnsignedShort(u + 6);
			u += 8;
			for (; j > 0; --j) {
				attrName = readUTF8(u, c);
				int attrSize = readInt(u + 2);
				u += 6;
				// tests are sorted in decreasing frequency order
				// (based on frequencies observed on typical classes)
				if (attrName.equals("Code")) {
					v = u;
				}
				u += attrSize;
			}
			// reads declared exceptions
			if (w == 0) {
			} else {
				w += 2;
				for (j = 0; j < readUnsignedShort(w); ++j) {
					w += 2;
				}
			}

			// visits the method's code, if any
			MethodCollector mv = classVisitor.visitMethod(access, name, desc);

			if (mv != null && v != 0) {
				int codeLength = readInt(v + 4);
				v += 8;

				int codeStart = v;
				int codeEnd = v + codeLength;
				v = codeEnd;

				j = readUnsignedShort(v);
				v += 2;
				for (; j > 0; --j) {
					v += 8;
				}
				// parses the local variable, line number tables, and code
				// attributes
				int varTable = 0;
				int varTypeTable = 0;
				j = readUnsignedShort(v);
				v += 2;
				for (; j > 0; --j) {
					attrName = readUTF8(v, c);
					if (attrName.equals("LocalVariableTable")) {
						varTable = v + 6;
					} else if (attrName.equals("LocalVariableTypeTable")) {
						varTypeTable = v + 6;
					}
					v += 6 + readInt(v + 2);
				}

				v = codeStart;
				// visits the local variable tables
				if (varTable != 0) {
					if (varTypeTable != 0) {
						k = readUnsignedShort(varTypeTable) * 3;
						w = varTypeTable + 2;
						int[] typeTable = new int[k];
						while (k > 0) {
							typeTable[--k] = w + 6; // signature
							typeTable[--k] = readUnsignedShort(w + 8); // index
							typeTable[--k] = readUnsignedShort(w); // start
							w += 10;
						}
					}
					k = readUnsignedShort(varTable);
					w = varTable + 2;
					for (; k > 0; --k) {
						int index = readUnsignedShort(w + 8);
						mv.visitLocalVariable(readUTF8(w + 4, c), index);
						w += 10;
					}
				}
			}
			return u;
		}

		private int readUnsignedShort(final int index) {
			byte[] b = this.b;
			return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
		}

		private int readInt(final int index) {
			byte[] b = this.b;
			return ((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16)
					| ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF);
		}

		private String readUTF8(int index, final char[] buf) {
			int item = readUnsignedShort(index);
			String s = strings[item];
			if (s != null) {
				return s;
			}
			index = items[item];
			return strings[item] = readUTF(index + 2, readUnsignedShort(index),
					buf);
		}

		private String readUTF(int index, final int utfLen, final char[] buf) {
			int endIndex = index + utfLen;
			byte[] b = this.b;
			int strLen = 0;
			int c;
			int st = 0;
			char cc = 0;
			while (index < endIndex) {
				c = b[index++];
				switch (st) {
				case 0:
					c = c & 0xFF;
					if (c < 0x80) { // 0xxxxxxx
						buf[strLen++] = (char) c;
					} else if (c < 0xE0 && c > 0xBF) { // 110x xxxx 10xx xxxx
						cc = (char) (c & 0x1F);
						st = 1;
					} else { // 1110 xxxx 10xx xxxx 10xx xxxx
						cc = (char) (c & 0x0F);
						st = 2;
					}
					break;

				case 1: // byte 2 of 2-byte char or byte 3 of 3-byte char
					buf[strLen++] = (char) ((cc << 6) | (c & 0x3F));
					st = 0;
					break;

				case 2: // byte 2 of 3-byte char
					cc = (char) ((cc << 6) | (c & 0x3F));
					st = 1;
					break;
				}
			}
			return new String(buf, 0, strLen);
		}

	}

	private static class Type {
		private final static int VOID = 0;
		private final static int BOOLEAN = 1;
		private final static int CHAR = 2;
		private final static int BYTE = 3;
		private final static int SHORT = 4;
		private final static int INT = 5;
		private final static int FLOAT = 6;
		private final static int LONG = 7;
		private final static int DOUBLE = 8;
		private final static int ARRAY = 9;
		private final static int OBJECT = 10;
		private final static Type VOID_TYPE = new Type(VOID, null, ('V' << 24)
				| (5 << 16) | (0 << 8) | 0, 1);
		private final static Type BOOLEAN_TYPE = new Type(BOOLEAN, null,
				('Z' << 24) | (0 << 16) | (5 << 8) | 1, 1);
		private final static Type CHAR_TYPE = new Type(CHAR, null, ('C' << 24)
				| (0 << 16) | (6 << 8) | 1, 1);
		private final static Type BYTE_TYPE = new Type(BYTE, null, ('B' << 24)
				| (0 << 16) | (5 << 8) | 1, 1);
		private final static Type SHORT_TYPE = new Type(SHORT, null,
				('S' << 24) | (0 << 16) | (7 << 8) | 1, 1);
		private final static Type INT_TYPE = new Type(INT, null, ('I' << 24)
				| (0 << 16) | (0 << 8) | 1, 1);
		private final static Type FLOAT_TYPE = new Type(FLOAT, null,
				('F' << 24) | (2 << 16) | (2 << 8) | 1, 1);
		private final static Type LONG_TYPE = new Type(LONG, null, ('J' << 24)
				| (1 << 16) | (1 << 8) | 2, 1);
		private final static Type DOUBLE_TYPE = new Type(DOUBLE, null,
				('D' << 24) | (3 << 16) | (3 << 8) | 2, 1);
		private final int sort;
		private char[] buf;
		private int off;
		private final int len;

		private Type(final int sort) {
			this.sort = sort;
			this.len = 1;
		}

		private Type(final int sort, final char[] buf, final int off,
				final int len) {
			this.sort = sort;
			this.buf = buf;
			this.off = off;
			this.len = len;
		}

		private static Type[] getArgumentTypes(final String methodDescriptor) {
			char[] buf = methodDescriptor.toCharArray();
			int off = 1;
			int size = 0;
			while (true) {
				char car = buf[off++];
				if (car == ')') {
					break;
				} else if (car == 'L') {
					while (buf[off++] != ';') {
					}
					++size;
				} else if (car != '[') {
					++size;
				}
			}

			Type[] args = new Type[size];
			off = 1;
			size = 0;
			while (buf[off] != ')') {
				args[size] = getType(buf, off);
				off += args[size].len + (args[size].sort == OBJECT ? 2 : 0);
				size += 1;
			}
			return args;
		}

		private static Type getType(final char[] buf, final int off) {
			int len;
			switch (buf[off]) {
			case 'V':
				return VOID_TYPE;
			case 'Z':
				return BOOLEAN_TYPE;
			case 'C':
				return CHAR_TYPE;
			case 'B':
				return BYTE_TYPE;
			case 'S':
				return SHORT_TYPE;
			case 'I':
				return INT_TYPE;
			case 'F':
				return FLOAT_TYPE;
			case 'J':
				return LONG_TYPE;
			case 'D':
				return DOUBLE_TYPE;
			case '[':
				len = 1;
				while (buf[off + len] == '[') {
					++len;
				}
				if (buf[off + len] == 'L') {
					++len;
					while (buf[off + len] != ';') {
						++len;
					}
				}
				return new Type(ARRAY, buf, off, len + 1);
				// case 'L':
			default:
				len = 1;
				while (buf[off + len] != ';') {
					++len;
				}
				return new Type(OBJECT, buf, off + 1, len - 1);
			}
		}

		private int getDimensions() {
			int i = 1;
			while (buf[off + i] == '[') {
				++i;
			}
			return i;
		}

		private Type getElementType() {
			return getType(buf, off + getDimensions());
		}

		private String getClassName() {
			switch (sort) {
			case VOID:
				return "void";
			case BOOLEAN:
				return "boolean";
			case CHAR:
				return "char";
			case BYTE:
				return "byte";
			case SHORT:
				return "short";
			case INT:
				return "int";
			case FLOAT:
				return "float";
			case LONG:
				return "long";
			case DOUBLE:
				return "double";
			case ARRAY:
				StringBuffer b = new StringBuffer(getElementType()
						.getClassName());
				for (int i = getDimensions(); i > 0; --i) {
					b.append("[]");
				}
				return b.toString();
				// case OBJECT:
			default:
				return new String(buf, off, len).replace('/', '.');
			}
		}
	}
}
