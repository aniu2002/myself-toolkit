package com.sparrow.orm.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.annotation.Column;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.config.TableConfig;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.config.TableItem;
import com.sparrow.orm.pool.DbConfig;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.trans.Transaction;
import com.sparrow.orm.type.DefaultType;
import com.sparrow.orm.type.Type;

public class SchemaUtil {
	public static final String TYPE_PACKAGE_PATH_PREFIX = "com.sparrow.orm.type.";
	public static final String TYPE_PACKAGE_PATH_SUFFIX = "Type";

	public static void generateDDL(SessionFactory factory, String file) {
		if (factory == null)
			return;
		String dd = factory.getDatabaseType();
		char stc = dd.charAt(0);
		stc = (char) (stc - 32);
		String targetClass = TYPE_PACKAGE_PATH_PREFIX + stc + dd.substring(1)
				+ TYPE_PACKAGE_PATH_SUFFIX;
		Type type = (Type) BeanForceUtil.createInstance(targetClass);
		if (type == null)
			type = new DefaultType();
		TableConfiguration tabcfg = factory.getTableConfiguration();
		List<TableConfig> cfgs = tabcfg.getTableConfigs();
		OutputStream ops = null;
		try {
			File f = new File(file);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			ops = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String sql;
		Session session = factory.openSession();
		Transaction tx = session.beginTranscation();
		for (TableConfig cfg : cfgs) {
			sql = generateCreateTableSql(cfg, type);
			session.executeUpdate(sql);
			if (ops != null)
				try {
					ops.write((sql + "\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		tx.commit();
		session.close();

		try {
			ops.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection(DbConfig dbc) {
		Connection conn = null;
		try {
			Class.forName(dbc.driver);
			conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void generateDDL(TableConfiguration tabcfg, Connection con,
			String file, String stype) throws SQLException {
		if (tabcfg == null)
			return;
		Type type = getDataBaseTypeDefine(stype);
		if (type == null)
			type = new DefaultType();
		List<TableConfig> cfgs = tabcfg.getTableConfigs();
		OutputStream ops = null;
		try {
			ops = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String sql;
		Connection dbCon = con;
		Statement dbStmt = dbCon.createStatement();

		// accessStmt
		// .executeUpdate("create table msg(tid integer not null primary key)");
		// accessStmt.execute("drop table msg"); //
		for (TableConfig cfg : cfgs) {
			sql = generateCreateTableSql(cfg, type);
			System.out.println(sql);
			dbStmt.executeUpdate(sql);
			if (ops != null)
				try {
					ops.write((sql + "\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		dbStmt.close();
		if (!dbCon.getAutoCommit())
			dbCon.commit();
		dbCon.close();
		try {
			ops.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String generateCreateTableSql(TableConfig cfg, Type type) {
		StringBuffer sb = new StringBuffer();
		sb.append("create table ").append(cfg.getName()).append("(");
		Collection<TableItem> itms = cfg.getAllItems();
		TableItem item;
		String ctype;
		if (itms != null && !itms.isEmpty()) {
			Iterator<TableItem> iterator = itms.iterator();
			boolean needSign = false;
			while (iterator.hasNext()) {
				item = iterator.next();
				if (needSign)
					sb.append(",");
				else
					needSign = true;
				ctype = type.getColumnType(item.getType());
				sb.append(item.getColumn()).append(" ").append(ctype);

				if ("string".equalsIgnoreCase(item.getType())
						|| "text".equalsIgnoreCase(item.getType())) {
					if (item.getLength() > 0)
						sb.append("(").append(item.getLength()).append(")");
					else
						sb.append("(20)");
				} else if ("boolean".equals(item.getType()))
					sb.append("(1)");

				if (item.isNotnull())
					sb.append(" not null");

				if (StringUtils.isNotEmpty(item.getComment()))
					sb.append(" COMMENT '").append(item.getComment())
							.append("'");
				// else
				// sb.append(" null");
				if (item.isKey())
					sb.append(" primary key");
			}
		}
		sb.append(") COMMENT='").append(cfg.getComment()).append("';");
		return sb.toString();
	}

	public static String generateTableCfg(Class<?> claz, Type dbtype) {
		String className = claz.getName();
		String tableName = claz.getSimpleName();
		StringBuilder sb = new StringBuilder();

		Field[] fields = claz.getDeclaredFields();
		Class<?> paraType;
		String attrName, paramType;
		boolean isKey, isColumn;

		Table di;
		Key ky;
		Column dc;

		if (claz.isAnnotationPresent(Table.class)) {
			di = (Table) claz.getAnnotation(Table.class);
			tableName = di.table();
		} else
			return null;
		sb.append(" <table claz=\"").append(className).append("\" name=\"")
				.append(tableName).append("\" comment=\"").append(di.desc())
				.append("\">").append("\r\n");
		int pos = sb.length();
		for (Field field : fields) {
			attrName = field.getName();
			if ("class".equals(attrName))
				continue;
			isKey = field.isAnnotationPresent(Key.class);
			isColumn = field.isAnnotationPresent(Column.class);
			paraType = field.getType();
			paramType = dbtype.getJavaTypeX(paraType);
			if (isKey) {
				ky = field.getAnnotation(Key.class);
				String str = "   <key column=\"" + ky.column()
						+ "\" property=\"" + attrName + "\" type=";
				if ("string".equals(paramType)) {
					str += "\"string\" length=\"" + ky.length() + "\"";
				} else
					str += "\"" + paramType + "\"";
				if (ky.notnull())
					str += " notnull=\"true\"";
				if (StringUtils.isNotEmpty(ky.comment()))
					str += " comment=\"" + ky.comment() + "\"";
				str += " constraint=\"" + ky.constraint() + "\" />\r\n";
				sb.insert(pos, str);
			} else if (isColumn) {
				dc = field.getAnnotation(Column.class);
				sb.append("   <item column=\"").append(dc.column())
						.append("\" property=\"").append(attrName)
						.append("\" type=");
				if ("string".equals(paramType)) {
					sb.append("\"string\" length=\"").append(dc.length())
							.append("\"");
				} else
					sb.append("\"").append(paramType).append("\"");
				if (dc.notnull())
					sb.append(" notnull=\"true\" ");
				if (StringUtils.isNotEmpty(dc.comment()))
					sb.append(" comment=\"").append(dc.comment()).append("\"");
				sb.append("/>").append("\r\n");
			}
		}
		sb.append(" </table>").append("\r\n");
		return sb.toString();
	}

	public static String generateTableCfg_EX_(Class<?> claz, Type dbtype) {
		String className = claz.getName();
		String tableName = claz.getSimpleName();
		StringBuilder sb = new StringBuilder();

		PropertyDescriptor[] descriptors = PropsUtils
				.getPropertyDescriptors(claz);
		Method method;
		Class<?> paraType;
		String attrName, paramType;
		boolean isKey, isColumn;

		Key ky;
		Column dc;
		Table di;

		if (claz.isAnnotationPresent(Table.class)) {
			di = (Table) claz.getAnnotation(Table.class);
			tableName = di.table();
		}
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append("<cfg>").append("\r\n");
		sb.append(" <table claz=\"").append(className).append("\" name=\"")
				.append(tableName).append("\">").append("\r\n");
		int pos = sb.length();
		for (PropertyDescriptor descriptor : descriptors) {
			attrName = descriptor.getName();
			if ("class".equals(attrName))
				continue;
			method = PropsUtils.getReadMethod(descriptor);
			isKey = method.isAnnotationPresent(Key.class);
			isColumn = method.isAnnotationPresent(Column.class);
			paraType = method.getReturnType();
			paramType = dbtype.getJavaTypeX(paraType);
			if (isKey) {
				ky = method.getAnnotation(Key.class);
				String str = "   <key column=\"" + ky.column()
						+ "\" property=\"" + attrName + "\" type=";
				if ("string".equals(paramType)) {
					str += "\"string\" length=\"" + ky.length() + "\"";
				} else
					str += "\"" + paramType + "\"";
				if (ky.notnull())
					str += " notnull=\"true\"";
				str += " constraint=\"" + ky.constraint() + "\" />\r\n";
				sb.insert(pos, str);
			} else if (isColumn) {
				dc = method.getAnnotation(Column.class);
				sb.append("   <item column=\"").append(dc.column())
						.append("\" property=\"").append(attrName)
						.append("\" type=");
				if ("string".equals(paramType)) {
					sb.append("\"string\" length=\"").append(dc.length())
							.append("\"");
				} else
					sb.append("\"").append(paramType).append("\"");
				if (dc.notnull())
					sb.append(" notnull=\"true\" ");
				sb.append("/>").append("\r\n");
			} else {
				sb.append("   <item column=\"")
						.append(attrName)
						.append("\" property=\"")
						.append(attrName)
						.append("\" type=\"")
						.append("string".equals(paramType) ? "string\" length=\"20"
								: paramType).append("\"/>").append("\r\n");
			}
		}
		sb.append(" </table>").append("\r\n");
		sb.append("</cfg>").append("\r\n");
		return sb.toString();
	}

	public static void generateTableCfg(Class<?>[] clazs, Type dbtype, File file) {
		Writer writer;
		String tbstr;
		writer = getTableFileWriter(file, "table-conf"); // clz.getSimpleName()
		if (writer != null) {
			try {
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n <cfg>\r\n");
				for (Class<?> clz : clazs) {
					tbstr = generateTableCfg(clz, dbtype);
					if (tbstr == null)
						continue;
					System.out.println(tbstr);
					writer.write(tbstr);
				}
				writer.write("</cfg>\r\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Writer getTableFileWriter(File file, String name) {
		OutputStreamWriter writer;
		try {
			if (!file.exists())
				file.mkdirs();
			if (file.isFile()) {
				writer = new OutputStreamWriter(new FileOutputStream(file),
						"utf-8");
			} else {
				writer = new OutputStreamWriter(new FileOutputStream(new File(
						file, name + ".xml")), "utf-8");
			}
			return writer;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void generateTableCfg(Class<?>[] claz, String dbtype,
			File file) {
		System.out
				.println("Table Configuration Path:" + file.getAbsolutePath());
		generateTableCfg(claz, getDataBaseTypeDefine(dbtype), new File(file,
				dbtype));
	}

	public static Type getDataBaseTypeDefine(String stype) {
		if (StringUtils.isEmpty(stype))
			return null;
		char stc = stype.charAt(0);
		stc = (char) (stc - 32);
		String targetClass = TYPE_PACKAGE_PATH_PREFIX + stc
				+ stype.substring(1) + TYPE_PACKAGE_PATH_SUFFIX;
		Type type = (Type) BeanForceUtil.createInstance(targetClass);
		if (type == null)
			type = new DefaultType();
		return type;
	}

	public static void main(String args[]) {
		generateTableCfg(
				ClassSearch.getInstance().searchClass(
						"classpath:au/app/**/domain/*.class"), "mysql",
				new File("conf"));

		SessionFactory factory = SessionFactory
				.configureFactory(PropertiesFileUtil
						.getPropertiesEl("conf/config4mysql.properties"));
		generateDDL(factory, "D:/conf/mysql/sql.txt");

	}
}
