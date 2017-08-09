package com.sparrow.orm.util;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;

import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.orm.annotation.Column;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.config.TableConfig;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.config.TableItem;
import com.sparrow.orm.pool.DbConfig;
import com.sparrow.orm.type.Type;


public class AnnotationScanUtil {
	public static TableConfiguration scanTableConfigure(String clazzPath,
			String type) {
		Class<?>[] clazs = ClassSearch.getInstance().searchClass(clazzPath);
		// "classpath:com/**/data/*.class"
		Type databaseType = SchemaUtil.getDataBaseTypeDefine(type);
		return generateTableCfg(clazs, databaseType);
	}

	public static TableConfiguration generateTableCfg(Class<?>[] clazs,
			Type dbtype) {
		TableConfiguration configuration = new TableConfiguration();
		for (Class<?> clz : clazs) {
			configuration.addTableCfg(clz);
		}
		return configuration;
	}

	public static TableConfiguration generateTableCfgx(Class<?>[] clazs,
			Type dbtype) {
		TableConfig tablecfg;
		TableConfiguration configuration = new TableConfiguration();

		for (Class<?> clz : clazs) {
			tablecfg = clazz2TableConfig(clz, dbtype);
			if (tablecfg == null)
				continue;
			configuration.addTableCfg(tablecfg);
		}
		return configuration;
	}

	public static TableConfig clazz2TableConfig(Class<?> claz, Type dbtype) {
		if (claz == null)
			return null;
		String tableName = claz.getSimpleName();
		Table di;
		if (claz.isAnnotationPresent(Table.class)) {
			di = (Table) claz.getAnnotation(Table.class);
			tableName = di.table();
		} else
			return null;
		String className = claz.getName();
		TableConfig cfg = new TableConfig();
		cfg.setClaz(className);
		cfg.setName(tableName);
		cfg.setBeanName(className.substring(className.lastIndexOf('.') + 1));

		Field[] fields = claz.getDeclaredFields();
		TableItem item;
		Class<?> paraType;
		Key ky;
		Column dc;
		String attrName, paramType;
		boolean isKey, isColumn;

		for (Field field : fields) {
			attrName = field.getName();
			if ("class".equals(attrName))
				continue;
			isKey = field.isAnnotationPresent(Key.class);
			isColumn = field.isAnnotationPresent(Column.class);
			if (!isKey && !isColumn)
				continue;
			item = new TableItem();
			paraType = field.getType();
			if (dbtype != null)
				paramType = dbtype.getJavaTypeX(paraType);
			else
				paramType = JdbcUtil.getJavaTypeString(paraType);
			if (isKey) {
				ky = field.getAnnotation(Key.class);
				item.setKey(true);
				item.setColumn(ky.column());
				item.setProperty(attrName);

				if ("string".equals(paramType)) {
					item.setLength(ky.length());
					item.setType("string");
				} else
					item.setType(paramType);

				item.setNotnull(ky.notnull());
				item.setConstraint(ky.constraint());
				cfg.addKey(item);
			} else if (isColumn) {
				dc = field.getAnnotation(Column.class);
				item.setColumn(dc.column());
				item.setProperty(attrName);

				if ("string".equals(paramType)) {
					item.setLength(dc.length());
					item.setType("string");
				} else
					item.setType(paramType);
				item.setNotnull(dc.notnull());
				cfg.addItem(item);
			}
		}

		return cfg;
	}

	public static void main(String args[]) {
		String clazzPath = "classpath:au/**/data/*.class";
		DbConfig dbconfig = ConfigUtil
				.getDbConfig("conf/config4mysql.properties");
		try {
			SchemaUtil.generateDDL(
					scanTableConfigure(clazzPath, dbconfig.dbType),
					SchemaUtil.getConnection(dbconfig), "D:/data.sql",
					dbconfig.dbType);
			SchemaUtil.generateTableCfg(
					ClassSearch.getInstance().searchClass(
							"classpath:au/**/data/*.class"), "mysql", new File(
                            "conf"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
