package com.sparrow.orm.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.sparrow.orm.type.Type;


public class LobUtil {
	public static final Class<?>[] EMPTY_CLASSES = null;
	public static final Object[] EMPTY_VALUES = null;

	public static String clobToString(Clob clob) {
		if (clob == null) {
			return null;
		}
		try {
			Reader r = clob.getCharacterStream();
			StringWriter sw = new StringWriter();
			char[] cs = new char[(int) clob.length()];
			try {
				r.read(cs);
				sw.write(cs);
				return sw.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] blobToBytes(Blob blob) {
		if (blob == null) {
			return null;
		}
		try {
			return blob.getBytes(1L, (int) blob.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setClob(Connection conn, PreparedStatement ps, int i,
			Object v, String type) throws SQLException {
		if (type.equals(Type.oracle)) {
			Class<?> clobClass = null;
			try {
				clobClass = Class.forName("oracle.sql.CLOB");
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
				return;
			}
			Object clob = null;
			Object oc = conn;
			try {
				Method m = clobClass.getMethod("createTemporary", new Class[] {
						java.sql.Connection.class, boolean.class, int.class });
				clob = m.invoke(null, new Object[] { oc, new Boolean(true),
						new Integer(1) });

				m = clobClass.getMethod("open", new Class[] { int.class });
				m.invoke(clob, new Object[] { new Integer(1) });// clob.open(1);

				m = clobClass.getMethod("setCharacterStream",
						new Class[] { long.class });
				Writer writer = (Writer) m.invoke(clob,
						new Object[] { new Long(0) });
				// Writer writer = clob.setCharacterStream(0L);
				writer.write(String.valueOf(v));
				writer.close();

				clobClass.getMethod("close", EMPTY_CLASSES).invoke(clob,
						EMPTY_VALUES);// clob.close();
				ps.setClob(i, (Clob) clob);
			} catch (Exception e) {
				try {
					if (clob != null) {
						Method m = clobClass.getMethod("freeTemporary",
								new Class[] { clobClass });
						m.invoke(null, new Object[] { clob });// CLOB.freeTemporary(clob);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		} else {
			ps.setObject(i, v);
		}
	}

	public static void setBlob(Connection conn, PreparedStatement ps, int i,
			byte[] v, String type) throws SQLException {
		if (type.equals(Type.oracle)) {
			Class<?> blobClass = null;
			try {
				blobClass = Class.forName("oracle.sql.BLOB");
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
				return;
			}
			Object blob = null;
			Object oc = conn;
			try {
				Method m = blobClass.getMethod("createTemporary", new Class[] {
						java.sql.Connection.class, boolean.class, int.class });
				blob = m.invoke(null, new Object[] { oc, new Boolean(true),
						new Integer(1) });

				m = blobClass.getMethod("open", new Class[] { int.class });
				m.invoke(blob, new Object[] { new Integer(1) });// �൱��blob.open(1);

				m = blobClass.getMethod("getBinaryOutputStream",
						new Class[] { long.class });
				OutputStream out = (OutputStream) m.invoke(blob,
						new Object[] { new Long(0) });
				out.write(v);
				out.close();

				blobClass.getMethod("close", EMPTY_CLASSES).invoke(blob,
						EMPTY_VALUES);// blob.close();
				ps.setBlob(i, (Blob) blob);
			} catch (Exception e) {
				try {
					if (blob != null) {
						Method m = blobClass.getMethod("freeTemporary",
								new Class[] { blobClass });
						m.invoke(null, new Object[] { blob });// BLOB.freeTemporary(clob);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		} else {
			ps.setObject(i, v);
		}
	}
}
