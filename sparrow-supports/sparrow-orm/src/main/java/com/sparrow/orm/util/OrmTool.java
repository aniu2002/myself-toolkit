package com.sparrow.orm.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.ReflectHelper;
import com.sparrow.orm.annotation.Column;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.config.TableConfig;
import com.sparrow.orm.config.TableItem;
import com.sparrow.orm.exceptions.SqlMappingException;
import com.sparrow.orm.id.IdentifierGenerator;
import com.sparrow.orm.id.IdentifierGeneratorFactory;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.meta.MappingKeyField;


/**
 * OrmTool 对象与table映射工具，主要是根据 class的注解以及 field的注解，完成对象映射的解析
 * 
 * @author YZC (2013-10-15-上午10:29:09)
 */
public class OrmTool {
	/**
	 * 检测class是否存在 Table.class 注解
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean hasTableMap(Class<?> clazz) {
		return clazz.isAnnotationPresent(Table.class);
	}

	/**
	 * 根据 class 信息获取 table 注解信息
	 * 
	 * @param clazz
	 * @return
	 * @throws SqlMappingException
	 */
	public static String tableName(Class<?> clazz) throws SqlMappingException {
		if (!hasTableMap(clazz))
			throw new SqlMappingException(clazz.getName()
					+ " has not define table mapping .. ");
		Table table = clazz.getAnnotation(Table.class);
		return table.table();
	}

	/**
	 * 解析 class 与 table 的mapping信息，通过 Key 和 Column 注解，用 MappingField包装 <br/>
	 * Mapping的元数据信息.
	 * 
	 * @param clazz 根据提供的dbType来转换 class field对应的 sql type，参照Type.class
	 * @return
	 */
	public static MappingFieldsWrap parseMapping(Class<?> clazz) {
		List<MappingField> columns = new ArrayList<MappingField>();
		List<MappingField> primary = new ArrayList<MappingField>();
		MappingField mapFeild;
		Field[] fields = clazz.getDeclaredFields();
		Field f;
		IdentifierGenerator generator = null;
		MappingField keyFeild = null;
		int n = 0, t = 0;
		for (int i = 0; i < fields.length; i++) {
			mapFeild = null;
			f = fields[i];
			if (f.isAnnotationPresent(Key.class)) {
				MappingKeyField mapKeyFeild = parseMappingKeyField(clazz, f,
						f.getAnnotation(Key.class));
				if (!mapKeyFeild.isIgnoreInsert()
						&& mapKeyFeild.getIdGenerator() != null) {
					generator = mapKeyFeild.getIdGenerator();
					// keyFeild = mapKeyFeild; 设置了generator的 认为是唯一
				}
				// 自增的id ，不设置generator，keyField就为空，包异常
				keyFeild = mapKeyFeild;
				mapFeild = mapKeyFeild;
			} else if (f.isAnnotationPresent(Column.class)) {
				mapFeild = parseMappingColumnField(clazz, f,
						f.getAnnotation(Column.class));
			}
			if (mapFeild != null) {
				if (mapFeild.isPrimary()) {
					if (mapFeild.isIgnoreInsert())
						n++;
					primary.add(mapFeild);
				} else
					columns.add(mapFeild);
				t++;
			}
		}
		MappingFieldsWrap mappingFieldWrap = new MappingFieldsWrap(clazz,
				primary.toArray(new MappingField[primary.size()]),
				columns.toArray(new MappingField[columns.size()]), t, n);
		mappingFieldWrap.setGenerator(generator);
		mappingFieldWrap.setKeyFeild(keyFeild);
		return mappingFieldWrap;
	}

	public static MappingFieldsWrap parseMapping(TableConfig config) {
		List<MappingField> columns = new ArrayList<MappingField>();
		List<MappingField> primary = new ArrayList<MappingField>();
		MappingField mapField;
		Class<?> clazz;
		try {
			clazz = ReflectHelper.classForName(config.getClaz());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("不能加载类:" + config.getClaz());
		}

		IdentifierGenerator generator = null;
		MappingField keyFeild = null;
		List<TableItem> items = config.getKeys();
		int n = 0, t = 0;

		if (items != null && !items.isEmpty()) {
			for (TableItem item : items) {
				MappingKeyField mapKeyFeild = parseMappingKeyField(clazz, item);
				if (mapKeyFeild != null) {
					if (!mapKeyFeild.isIgnoreInsert()
							&& mapKeyFeild.getIdGenerator() != null) {
						generator = mapKeyFeild.getIdGenerator();
						// keyFeild = mapKeyFeild; 设置了generator的 认为是唯一
					}
					if (mapKeyFeild.isIgnoreInsert())
						n++;
					// 自增的id ，不设置generator，keyField就为空，包异常
					keyFeild = mapKeyFeild;
					primary.add(mapKeyFeild);
					t++;
				}
			}
		}
		items = config.getItems();
		if (items != null && !items.isEmpty()) {
			for (TableItem item : items) {
				mapField = parseMappingColumnField(clazz, item);
				if (mapField != null) {
					primary.add(mapField);
					t++;
				}
			}
		}
		MappingFieldsWrap mappingFieldWrap = new MappingFieldsWrap(clazz,
				primary.toArray(new MappingField[primary.size()]),
				columns.toArray(new MappingField[columns.size()]), t, n);
		mappingFieldWrap.setGenerator(generator);
		mappingFieldWrap.setKeyFeild(keyFeild);
		return mappingFieldWrap;
	}

	/**
	 * 解析 Field 的 Key 注解
	 * 
	 * @param clazz
	 *            table映射class
	 * @param field
	 *            class的field
	 * @param key
	 *            Key注解
	 * @return
	 */
	static MappingKeyField parseMappingKeyField(Class<?> clazz, Field field,
			Key key) {
		PropertyDescriptor pd;
		try {
			// 记录 Key 对应的 PO field 的 PropertyDescriptor，为以后获取PO 属性值时使用
			pd = new PropertyDescriptor(field.getName(), clazz);
			// 记录 Key 对应的 PO field 的 PropertyDescriptor，为以后获取PO 属性值时使用
			String generator = key.generator();
			IdentifierGenerator idGenerator = null;
			String fillChar = null;
			boolean auto = false;
			if (StringUtils.isNotEmpty(generator)) {
				if ("auto".equalsIgnoreCase(generator)) {
					auto = true;
				} else {
					idGenerator = IdentifierGeneratorFactory.get(generator);
					auto = idGenerator.isAuto();
					fillChar = idGenerator.getFillChar();
				}
			}
			// dbType.getSqlType(field.getType())
			MappingKeyField mField = new MappingKeyField(pd, field.getType(),
					key.type(), field.getName(), key.column(), true, auto);
			mField.setIdGenerator(idGenerator);
			mField.setFillChar(fillChar);
			return mField;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	static MappingKeyField parseMappingKeyField(Class<?> clazz, TableItem item) {
		PropertyDescriptor pd;
		try {
			Field field = ReflectHelper.getField(clazz, item.getProperty());
			// 记录 Key 对应的 PO field 的 PropertyDescriptor，为以后获取PO 属性值时使用
			pd = new PropertyDescriptor(field.getName(), clazz);
			String generator = item.getGenerator();
			IdentifierGenerator idGenerator = null;
			boolean auto = false;
			if (StringUtils.isNotEmpty(generator)) {
				idGenerator = IdentifierGeneratorFactory.get(generator);
				auto = idGenerator.isAuto();
			}
			// dbType.getSqlType(field.getType())
			MappingKeyField mField = new MappingKeyField(pd, field.getType(),
					JdbcUtil.getSqlType(item.getType()), field.getName(),
					item.getColumn(), true, auto);
			mField.setIdGenerator(idGenerator);
			return mField;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	static MappingField parseMappingColumnField(Class<?> clazz, TableItem item) {
		PropertyDescriptor pd;
		try {
			Field field = ReflectHelper.getField(clazz, item.getProperty());
			// 记录 Key 对应的 PO field 的 PropertyDescriptor，为以后获取PO 属性值时使用
			pd = new PropertyDescriptor(field.getName(), clazz);
			// dbType.getSqlType(field.getType())
			MappingField mField = new MappingField(pd, field.getType(),
					JdbcUtil.getSqlType(item.getType()), field.getName(),
					item.getColumn(), false, false);
			return mField;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析 Field 的 Column 注解
	 * 
	 * @param clazz
	 *            table映射class
	 * @param field
	 *            class的field
	 * @param column
	 *            Column注解
	 * @return
	 */
	static MappingField parseMappingColumnField(Class<?> clazz, Field field,
			Column column) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(field.getName(), clazz);
			// dbType.getSqlType(field.getType())
			MappingField mField = new MappingField(pd, field.getType(),
					column.type(), field.getName(), column.column(),
					column.ignoreInsert(), column.ignoreUpdate());
			return mField;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据 PO 的 MappingFieldsWrap ，构建基础的增删改查语句
	 * 
	 * @param table
	 *            PO对应的表名
	 * @param mappingFieldWrap
	 *            PO 的 MappingFieldsWrap 信息
	 * @param namedParameter
	 *            参数填充工具（ '?' , ':id' ）
	 * @return
	 */
	public static String[] buildBaseSql(String table,
			MappingFieldsWrap mappingFieldWrap, boolean namedParameter) {
		StringBuilder select = new StringBuilder();
		StringBuilder update = new StringBuilder();
		StringBuilder delete = new StringBuilder();
		String fillWhereChar = null;

		// build where condition for sql ，主键where条件语句
		fillWhereChar = buildPrimaryWhereSql(mappingFieldWrap, namedParameter);

		// build columns for sql ， 构建字段 选取 段("id,name,sex")
		// select sql no where condition 构建select语句
		select.append("SELECT ");
		appendColumn(mappingFieldWrap.getPrimary(), select, false);
		appendColumn(mappingFieldWrap.getColumns(), select, select.length() > 0);
		select.append(" FROM ").append(table);

		// build values sets for insert SQL，构建insert的values信息 ('?,?,?,?')

		// insert sql 构建insert SQL
		String insert = buildInsertSql(table, mappingFieldWrap);

		// update sql no where condition 构建 update SQL
		update.append("UPDATE ").append(table).append(" SET ");
		appendUpdateColumn(mappingFieldWrap.getColumns(), update,
				namedParameter, false);
		// delete sql no where condition 构建delete语句
		delete.append("DELETE FROM ").append(table);

		String selectNoWhere = select.toString();
		if (fillWhereChar != null) {
			select.append(fillWhereChar);
			update.append(fillWhereChar);
			delete.append(fillWhereChar);
		}

		return new String[] { insert, select.toString(), update.toString(),
				delete.toString(), selectNoWhere };
	}

	static String buildInsertSql(String table,
			MappingFieldsWrap mappingFieldWrap) {
		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ").append(table).append('(');
		// build columns for sql ， 构建字段 选取 段("id,name,sex")
		appendInsertColumn(mappingFieldWrap, insert);
		insert.append(") VALUES(");
		// build values sets for insert SQL，构建insert的values信息 ('?,?,?,?')
		appendInsertParas(mappingFieldWrap, insert);
		insert.append(")");
		return insert.toString();
	}

	/**
	 * 
	 * 追加insert sql 的 column字段名
	 * 
	 * @param mappingFieldWrap
	 *            pojo映射字段包装类
	 * @param sb
	 *            字符动态追加器
	 * @author YZC
	 */
	static void appendInsertColumn(MappingFieldsWrap mappingFieldWrap,
			StringBuilder sb) {
		boolean notFirst = appendInsertColumn(mappingFieldWrap.getPrimary(),
				sb, false);
		appendInsertColumn(mappingFieldWrap.getColumns(), sb, notFirst);
	}

	/**
	 * 
	 * 追加pojo对应字段 insert的values参数命名
	 * 
	 * @param mappingFieldWrap
	 *            pojo映射字段包装类
	 * @param sb
	 *            StringBuilder
	 * @author YZC
	 */
	static void appendInsertParas(MappingFieldsWrap mappingFieldWrap,
			StringBuilder sb) {
		boolean notFirst = appendInsertParas(mappingFieldWrap.getPrimary(), sb,
				false);
		appendInsertParas(mappingFieldWrap.getColumns(), sb, notFirst);
	}

	/**
	 * 构建 insert sql 语句 中的 values 片段
	 * 
	 * @param fields
	 * @param sb
	 * @param nFirst
	 */
	static boolean appendInsertParas(MappingField[] fields, StringBuilder sb,
			boolean nFirst) {
		MappingField mapField;
		boolean notFirst = nFirst;
		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (mapField.isIgnoreInsert())
				continue;
			if (notFirst) {
				sb.append(",");
			} else
				notFirst = true;
			if (mapField.isPrimary() && mapField.getFillChar() != null)
				sb.append(mapField.getFillChar());
			else
				fillParamName(mapField, sb);
		}
		return notFirst;
	}

	/**
	 * 构建 insert sql 语句 中的 columns 片段
	 * 
	 * @param fields
	 * @param sb
	 * @param nFirst
	 */
	static boolean appendInsertColumn(MappingField[] fields, StringBuilder sb,
			boolean nFirst) {
		MappingField mapField;
		boolean notFirst = nFirst;
		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (mapField.isIgnoreInsert())
				continue;
			if (notFirst) {
				sb.append(",");
			} else
				notFirst = true;
			sb.append(mapField.getColumn());
		}
		return notFirst;
	}

	/**
	 * 构建 insert sql 语句 中的 values 片段
	 * 
	 * @param fields
	 * @param sb
	 * @param nFirst
	 */
	public static void appendParas(MappingField[] fields, StringBuilder sb,
			boolean namedParameter, boolean nFirst) {
		MappingField mapField;
		boolean notFirst = nFirst;

		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (notFirst) {
				sb.append(",");
			} else
				notFirst = true;
			// sb.append(mapField.getColumn()).append("=");
			if (namedParameter)
				sb.append(':').append(mapField.getField());
			else
				sb.append('?');
		}
	}

	/**
	 * 构建 insert sql 语句 中的 columns 片段
	 * 
	 * @param fields
	 * @param sb
	 * @param nFirst
	 */
	public static void appendColumn(MappingField[] fields, StringBuilder sb,
			boolean nFirst) {
		MappingField mapField;
		boolean notFirst = nFirst;
		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (notFirst) {
				sb.append(",");
			} else
				notFirst = true;
			sb.append(mapField.getColumn());
		}
	}

	/**
	 * 构建 update sql 语句 中的 set columns 片段
	 * 
	 * @param fields
	 * @param sb
	 * @param nFirst
	 */
	public static void appendUpdateColumn(MappingField[] fields,
			StringBuilder sb, boolean namedParameter, boolean nFirst) {
		MappingField mapField;
		boolean notFirst = nFirst;
		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (notFirst) {
				sb.append(",");
			} else
				notFirst = true;
			sb.append(mapField.getColumn()).append("=");
			if (namedParameter)
				sb.append(':').append(mapField.getField());
			else
				sb.append('?');
		}
	}

	/**
	 * 构建 select|update|delete sql 语句 中的 where 片段, 如果无primary条件，就将所有的column作为条件
	 * 
	 * @param mappingFieldWrap
	 * @return
	 */
	public static String buildPrimaryWhereSql(
			MappingFieldsWrap mappingFieldWrap, boolean namedParameter) {
		MappingField[] fields = mappingFieldWrap.getPrimary();
		if (fields != null && fields.length > 0) {
			return buildPrimaryWhereSql(fields, namedParameter);
		} else {
			return buildPrimaryWhereSql(mappingFieldWrap.getColumns(),
					namedParameter);
		}
	}

	/**
	 * 构建 select|update|delete sql 语句 中的 where 片段
	 * 
	 * @param fields
	 * @return
	 */
	static String buildPrimaryWhereSql(MappingField[] fields,
			boolean namedParameter) {
		StringBuilder w = new StringBuilder(" WHERE ");
		MappingField mapField;
		boolean notFirst = false;

		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (notFirst) {
				w.append(" AND ");
			} else
				notFirst = true;
			w.append(mapField.getColumn()).append("=");
			if (namedParameter)
				w.append(':').append(mapField.getField());
			else
				w.append('?');
		}
		return w.toString();
	}

	static void fillParamName(MappingField mapField, StringBuilder sb) {
		sb.append(':').append(mapField.getField());
	}
}
