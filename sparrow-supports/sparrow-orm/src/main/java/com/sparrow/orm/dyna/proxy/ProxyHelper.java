package com.sparrow.orm.dyna.proxy;

import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.config.SqlMapManager;
import com.sparrow.orm.config.TableMapping;
import com.sparrow.orm.dyna.annotation.*;
import com.sparrow.orm.dyna.common.*;
import com.sparrow.orm.dyna.data.InvokeMeta;
import com.sparrow.orm.dyna.data.MethodMeta;
import com.sparrow.orm.dyna.data.MethodParam;
import com.sparrow.orm.dyna.enums.ParamType;
import com.sparrow.orm.dyna.parser.NamedParameter;
import com.sparrow.orm.dyna.parser.ParsedSql;
import com.sparrow.orm.dyna.parser.SqlTool;
import com.sparrow.orm.dyna.sql.SqlBuilder;
import com.sparrow.orm.dyna.sql.SqlHelper;
import com.sparrow.orm.dyna.sql.SqlInsertBuilder;
import com.sparrow.orm.dyna.sql.SqlUpdateBuilder;
import com.sparrow.orm.dyna.visitor.ExpressParser;
import com.sparrow.orm.meta.MappingField;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.sparrow.orm.dyna.common.Constants.*;


public class ProxyHelper {

	static void tryToCorrect(GrammarMeta gmeta, MethodMeta mmeta) {
		int cmd = gmeta.getCommand();
		switch (cmd) {
		// get开头的认为选单个,如果result是list 那么就select
		case COMMAND_SINGLE:
			// !mmeta.hasArguments() && , 有pojo或者vo作为查询参数
			if (mmeta.isList()) {
				gmeta.setCommand(COMMAND_SELECT);
			}
			break;
		case COMMAND_SELECT:
			if (!mmeta.isList() && !mmeta.isVoid()) {
				gmeta.setCommand(COMMAND_SELECT);
			}
			break;
		case COMMAND_INSERT:
		case COMMAND_UPDATE:
		case COMMAND_DELETE:

			break;
		}
	}

	static InvokeMeta parseAnnotation(Method method, MethodMeta mmeta) {
		String sql;
		int cmd = COMMAND_UNKNOW;
		int type = -1;
		if (method.isAnnotationPresent(Select.class)) {
			cmd = COMMAND_SELECT;
			Select select = method.getAnnotation(Select.class);
			sql = select.value();
			type = select.type();
		} else if (method.isAnnotationPresent(Update.class)) {
			cmd = COMMAND_UPDATE;
			Update update = method.getAnnotation(Update.class);
			sql = update.value();
			type = update.type();
		} else if (method.isAnnotationPresent(Insert.class)) {
			cmd = COMMAND_INSERT;
			Insert insert = method.getAnnotation(Insert.class);
			sql = insert.value();
			type = insert.type();
		} else if (method.isAnnotationPresent(Delete.class)) {
			cmd = COMMAND_DELETE;
			Delete delete = method.getAnnotation(Delete.class);
			sql = delete.value();
			type = delete.type();
		} else
			return null;
		InvokeMeta meta = new InvokeMeta();
		// 数组下标参数方式的sql, id=#1
		if (type == 1) {
			ParsedSql parsedSql = SqlTool.parseSqlStatement(sql);
			// 只有一个参数的,多个参数 ,都是一种类型,批量插入
			if (parsedSql.hasParas()) {
				NamedParameter[] paras = parsedSql.getParameters();
				MethodParam relations[] = new MethodParam[paras.length];
				NamedParameter namedParameter;
				MethodParam mParam;
				int idx = 0;
				boolean isNamedIdx = parsedSql.isNamed();
				for (int i = 0; i < relations.length; i++) {
					namedParameter = paras[i];
					if (isNamedIdx) {
						mParam = mmeta.findByParaName(namedParameter.getName());
						if (mParam == null)
							throw new RuntimeException("方法参数中未找到与sql中对应的参数名:"
									+ namedParameter.getName());
					} else {
						idx = namedParameter.getIndex();
						mParam = (idx == -1) ? null : mmeta.getMethodParam(idx);
						if (mParam == null)
							throw new RuntimeException("方法参数中未找到与sql中对应的索引值:"
									+ namedParameter.getName());
					}
					relations[i] = mParam;
				}
				meta.setRelations(relations);
			}
			meta.setSql(parsedSql.getActualSql());
		} else {
			MethodParam[] paras = mmeta.getMethodParams();
			MethodParam relations[] = new MethodParam[mmeta.getParamNames()];
			MethodParam para;
			int j = 0;
			for (int i = 0; i < paras.length; i++) {
				para = paras[i];
				if (para.isNamedParam())
					relations[j++] = para;
			}
			meta.setSql(sql);
			meta.setRelations(relations);
		}
		meta.setReturnClass(mmeta.getReturnClass());
		meta.setWrapClass(mmeta.getWrapClass());
		meta.setNamedParams(mmeta.hasParamNames());
		// 设置返回类型
		meta.setParamType(mmeta.getParamType());
		meta.setCommand(cmd);
		meta.setHasMethodArguments(mmeta.hasArguments());

		return meta;
	}

	public static InvokeMeta parse(Method method, SqlMapManager sqlMapManager) {
		MethodMeta mmeta = parseMethodMeta(method);
		InvokeMeta invokeMeta = parseAnnotation(method, mmeta);
		if (invokeMeta == null)
			invokeMeta = parseMethod(mmeta, sqlMapManager);
		return invokeMeta;
	}

	static InvokeMeta parseMethod(MethodMeta mmeta, SqlMapManager sqlMapManager) {
		GrammarMeta gmeta = parseMethodName(mmeta.getMethodName());
		tryToCorrect(gmeta, mmeta);
		InvokeMeta meta = new InvokeMeta();
		if (gmeta.hasParams()) {
			if (!mmeta.hasArguments())
				throw new RuntimeException("方法语义上存在参数设置，然而方法未包含任何参数字段");
			else {
				int paramCount = mmeta.hasParamNames() ? mmeta.getParamNames()
						: mmeta.getParamCount();
				if (gmeta.getParamCount() > paramCount
						&& mmeta.getMethodParam(0).isBaseType())
					throw new RuntimeException("方法语义上存在参数设置个数，大于方法上实际提供的参数字段数");
			}
			MethodParam relations[] = new MethodParam[gmeta.getParamCount()];
			if (mmeta.hasParamNames()) {
				// 寻找方法参数里配置 param的 val匹配建立映射
				for (int i = 0; i < relations.length; i++) {
					ParamItem itm = gmeta.getItem(i);
					MethodParam mParam = mmeta.findByParaName(itm.getParam());
					if (mParam == null)
						throw new RuntimeException("在方法参数里未找到Param注解定义："
								+ itm.getParam());
					relations[i] = mParam;
				}
			} else {
				// 采用默认的方法参数的自然顺序
				for (int i = 0; i < relations.length; i++)
					relations[i] = mmeta.getMethodParam(i);
			}
			meta.setRelations(relations);
		}
		if (gmeta.getCommand() == COMMAND_UNKNOW) {
			throw new RuntimeException("方法语义-无法获知是什么数据操作");
		}
		// if (!mmeta.isListReturnType()) {
		// throw new RuntimeException("方法语义-上无参数设置，然而方法返回类型非list");
		// }
		meta.setReturnClass(mmeta.getReturnClass());
		meta.setWrapClass(mmeta.getWrapClass());
		// 设置返回类型
		meta.setParamType(mmeta.getParamType());
		meta.setCommand(gmeta.getCommand());
		meta.setHasMethodArguments(mmeta.hasArguments());
		meta.setSql(generateSql(gmeta, mmeta, sqlMapManager, meta));
		return meta;
	}

	static boolean isPojo(Class<?> entityClass) {
		return entityClass.isAnnotationPresent(Table.class);
	}

	public static boolean isCustomDefine(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	static boolean isSelect(GrammarMeta gmeta) {
		return gmeta.getCommand() == COMMAND_SELECT
				|| gmeta.getCommand() == COMMAND_SINGLE;
	}

	static boolean isUpdate(GrammarMeta gmeta) {
		return gmeta.getCommand() == COMMAND_UPDATE
				|| gmeta.getCommand() == COMMAND_INSERT;
	}

	static boolean isDelete(GrammarMeta gmeta) {
		return gmeta.getCommand() == COMMAND_DELETE;
	}

	static TableMapping guessEntity(GrammarMeta gmeta, MethodMeta mmeta,
			SqlMapManager sqlMapManager) {
		Class<?> entityClass = null;
		String entity = gmeta.getEntity();
		TableMapping tm;
		// 方法上带有entity
		if (StringUtils.isNotEmpty(entity)) {
			tm = sqlMapManager.getTableMapping(entity);
			if (tm != null)
				return tm;
		} else {
			// 方法上没带有entity,对select的操作,判断返回的class,试图找到sqlMap对应的查询语句
			// List<Pojo> get(); 方法上没有entity，通过返回类型找pojo
			// Pojo get(Sring id); 方法上没有entity，通过返回类型找pojo
			// 1、 save update delete
			// int save(Pojo)
			// int save(List<Pojo)
			// int save(Pojo... pojos) 找第一个参数pojo类型
			// 仅仅在
			if (!mmeta.isVoid() && isSelect(gmeta)) {
				entityClass = mmeta.getWrapClass();
			} else if (mmeta.hasArguments()) {
				// 是update和insert的情况
				if (isUpdate(gmeta))
					entityClass = mmeta.getMethodParam(0).getWrapClass();
				// else //delete
				// entityClassde
			}
			// delete 只能删除 primitive的对象或者 列表
			// 限制只能是pojo
			if (!isPojo(entityClass)) {
				// System.out.println(entityClass.getName());
				throw new RuntimeException("无法分析和获取pojo对象类型,无Table注解");
			}
		}
		if (entityClass == null)
			throw new RuntimeException("不能找到对应的实体映射:" + entity);
		return sqlMapManager.getTableMapping(entityClass);

	}

	static void checkBestParameters(MethodMeta mmeta, String s, int n) {
		if (!mmeta.hasArguments())
			throw new RuntimeException("方法上无参数，entity无法执行[" + s + "]操作");
		else if (mmeta.getParamCount() != n)
			throw new RuntimeException("方法上必须有" + n + "个参数-"
					+ mmeta.getMethodName());
	}

	static void checkNoParameters(MethodMeta mmeta, String s) {
		if (!mmeta.hasArguments())
			throw new RuntimeException("方法上无参数，entity无法执行[" + s + "]操作");
	}

	static void checkHasMethodArguments(InvokeMeta invokeMeta, String s) {
		if (!invokeMeta.hasMethodArguments())
			throw new RuntimeException("方法上无参数，entity无法执行[" + s + "]操作");
	}

	static void checkNoParameters(MethodMeta mmeta, String s, int n) {
		if (!mmeta.hasArguments())
			throw new RuntimeException("方法上无参数，entity无法执行操作:" + s);
		else if (mmeta.getParamCount() < n)
			throw new RuntimeException("方法上无参数，entity无法执行操作:" + s + ",至少需要" + n
					+ "个参数");
	}

	static void resetInokeMeta(GrammarMeta gmeta, MethodMeta mmeta,
			InvokeMeta invokeMeta) {
		if (mmeta.hasArguments()) {
			MethodParam methodParam = mmeta.getMethodParam(0);
			Class<?> wrapClass = methodParam.getWrapClass();
			// 针对update和save时, 如果第一个参数是pojo,那么使用sqlMap的 insert sql
			if (isPojo(wrapClass) || isCustomDefine(wrapClass))
				invokeMeta.setNamedParams(true);
		}
	}

	public static boolean isEmpty(Object[] args) {
		return args == null || args.length == 0;
	}

	static void appendWhere(SqlBuilder sqlBuilder, MappingField[] feilds) {
		sqlBuilder.where();
		StringBuilder sb = sqlBuilder.getStringBuilder();
		boolean first = true;
		for (int i = 0; i < feilds.length; i++) {
			MappingField itm = feilds[i];
			if (first) {
				first = false;
			} else
				sb.append(SqlBuilder.OP_AND);
			sqlBuilder.appendString(sb, itm.getColumn(), SqlBuilder.EQUAL,
					SqlBuilder.QUESTION);
		}
	}

	static String generatePojoSql(int command, GrammarMeta gmeta,
			MethodMeta mmeta, TableMapping sqlMap, InvokeMeta invokeMeta) {
		SqlBuilder sqlBuilder;
		String table = sqlMap.getTableName();
		// 语义上判断无参数,根据sqlMap的主键操作sql,取第一个参数
		if (mmeta.hasArguments())
			invokeMeta.setRelations(getRelations(mmeta));
		MappingField[] feilds = sqlMap.getPrimaryKey();
		switch (command) {
		case COMMAND_DELETE:
			checkNoParameters(mmeta, "删除");
			sqlBuilder = SqlHelper.delete(table);
			if (!isEmpty(feilds))
				appendWhere(sqlBuilder, feilds);
			return sqlBuilder.sql();
		case COMMAND_INSERT:
			checkNoParameters(mmeta, "新增");
			// insert 如果存在sqlMap,那必定是pojo参数
			if (mmeta.hasArguments())
				invokeMeta.setNamedParams(true);
			// resetInokeMeta(gmeta, mmeta, invokeMeta);
			return sqlMap.getInsertSql();
		case COMMAND_SELECT: // query select
			sqlBuilder = SqlHelper.select("*").from(table);
			if (gmeta.hasParams())
				appendWhere(sqlBuilder, gmeta, 0);
			else if (mmeta.hasArguments()) {
				MethodParam m = mmeta.getMethodParam(0);
				if (m.isPojo() || m.isCustom()) {
					invokeMeta.setNamedParams(true);
					invokeMeta.setDynaQuery(true);
					invokeMeta.setTableMapping(sqlMap);
				}
			}
			return sqlBuilder.sql();
		case COMMAND_SINGLE: // get
			checkBestParameters(mmeta, "选择实体", 1);
			sqlBuilder = SqlHelper.select(SqlBuilder.STAR).from(table);
			if (!isEmpty(feilds))
				appendWhere(sqlBuilder, feilds);
			return sqlBuilder.sql();
		case COMMAND_UPDATE:
			if (mmeta.hasArguments())
				invokeMeta.setNamedParams(true);
			checkNoParameters(mmeta, "更新实体(参数应是pojo)");
			return sqlMap.getUpdateSql();
		case COMMAND_UNKNOW:
			throw new RuntimeException("无法识别方法:" + gmeta.getMethodName());
		}
		return null;
	}

	static String generateMethodSql(int command, GrammarMeta gmeta,
			MethodMeta mmeta, String table) {
		SqlBuilder sqlBuilder = null;
		MethodParam p;
		Class<?> wc;
		switch (command) {
		case COMMAND_DELETE:
			checkNoParameters(mmeta, "删除");
			sqlBuilder = SqlHelper.delete(table);
			break;
		case COMMAND_INSERT:
			checkNoParameters(mmeta, "新增");
			p = mmeta.getMethodParam(0);
			wc = p.getWrapClass();
			if (!Tool.isSimpleProperty(wc)) {
				// 解析class的参数
				SqlInsertBuilder insertBuilder = SqlHelper.insert(table);
				appendInsertColumn(insertBuilder, wc);
				return insertBuilder.sql();
			} else
				throw new RuntimeException("参数类型不是pojo类型，不能做为数据表数据插入");
		case COMMAND_SELECT:
			sqlBuilder = SqlHelper.select("*").from(table);
			break;
		case COMMAND_SINGLE:
			checkNoParameters(mmeta, "选择实体");
			sqlBuilder = SqlHelper.select("*").from(table);
			break;
		case COMMAND_UPDATE:
			checkNoParameters(mmeta, "选择实体", 2);
			p = mmeta.getMethodParam(0);
			wc = p.getWrapClass();
			if (!Tool.isSimpleProperty(wc)) {
				SqlUpdateBuilder builder = SqlHelper.update(table);
				appendUpdateColumn(builder, wc);
				appendWhere(sqlBuilder, gmeta, 1);
				return builder.sql();
			} else
				return null;
		case COMMAND_UNKNOW:
			throw new RuntimeException("无法识别方法:" + gmeta.getMethodName());
		}
		if (gmeta.hasParams())
			appendWhere(sqlBuilder, gmeta, 0);
		return sqlBuilder.sql();
	}

	static MethodParam[] getRelations(MethodMeta mmeta) {
		MethodParam relations[] = new MethodParam[mmeta.getParamCount()];
		for (int i = 0; i < relations.length; i++) {
			relations[i] = mmeta.getMethodParam(i);
		}
		return relations;
	}

	static String generateSql(GrammarMeta gmeta, MethodMeta mmeta,
			SqlMapManager sqlMapManager, InvokeMeta invokeMeta) {
		TableMapping sqlMap = guessEntity(gmeta, mmeta, sqlMapManager);
		int cmd = gmeta.getCommand();
		// sqlMap不为空，但解析方法名无参数，试图通过参数类型和返回类型来猜测是根据主键来增三改查
		if (sqlMap != null) {
			if (!gmeta.hasParams()) {
				return generatePojoSql(cmd, gmeta, mmeta, sqlMap, invokeMeta);
			} else
				return generateMethodSql(cmd, gmeta, mmeta,
						sqlMap.getTableName());
		} else {
			String table = underScoreName(gmeta.getEntity());
			return generateMethodSql(cmd, gmeta, mmeta, table);
		}
	}

	static void appendUpdateColumn(SqlUpdateBuilder updateBuilder,
			Class<?> clazz) {
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(clazz);
		StringBuilder sb = updateBuilder.getStringBuilder();
		boolean notFirst = false;
		for (PropertyDescriptor descriptor : descriptors) {
			if (notFirst)
				sb.append(',');
			else {
				sb.append(" SET ");
				notFirst = true;
			}
			appendUnderScoreName(sb, descriptor.getName());
			sb.append(SqlBuilder.EQUAL).append(SqlBuilder.QUESTION);
		}
	}

	static void appendInsertColumn(SqlInsertBuilder insertBuilder,
			Class<?> clazz) {
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(clazz);
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		boolean notFirst = false;
		for (PropertyDescriptor descriptor : descriptors) {
			if (notFirst) {
				sb.append(',');
				sb1.append(',');
			} else
				notFirst = true;
			appendUnderScoreName(sb, descriptor.getName());
			sb1.append('?');
			// insertBuilder.values(columns, values);
		}
		insertBuilder.values(sb.toString(), sb1.toString());
	}

	static void appendWhere(SqlBuilder sqlBuilder, GrammarMeta gmeta, int start) {
		if (gmeta.hasParams()) {
			ParamItem[] itms = gmeta.getItems();
			sqlBuilder.where();
			StringBuilder sb = sqlBuilder.getStringBuilder();
			boolean first = true;
			for (int i = start; i < itms.length; i++) {
				ParamItem itm = itms[i];
				if (first) {
					first = false;
				} else
					sb.append(itm.getFilter());
				appendUnderScoreName(sb, itm.getParam());
				sqlBuilder.appendString(sb, itm.getOperate(),
						SqlBuilder.QUESTION);
			}
		}
	}

	static void appendUnderScoreName(StringBuilder sb, String name) {
		if (StringUtils.isEmpty(name))
			return;
		char charArr[] = name.toCharArray();
		char ch = charArr[0];
		sb.append(Character.toUpperCase(ch));
		for (int i = 1; i < charArr.length; i++) {
			ch = charArr[i];
			if (Character.isUpperCase(ch)) {
				sb.append("_").append(ch);
			} else {
				sb.append(Character.toUpperCase(ch));
			}
		}
	}

	static String underScoreName(String name) {
		if (StringUtils.isEmpty(name))
			return null;
		char charArr[] = name.toCharArray();
		char ch = charArr[0];
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(ch));
		for (int i = 1; i < charArr.length; i++) {
			ch = charArr[i];
			if (Character.isUpperCase(ch)) {
				sb.append("_").append(ch);
			} else {
				sb.append(Character.toUpperCase(ch));
			}
		}
		return sb.toString();
	}

	static GrammarMeta parseMethodName(String method) {
		ExpressParser parser = new ExpressParser(method);
		QueryExpressVisitor visitor = new QueryExpressVisitor();
		parser.accept(visitor);
		GrammarMeta meta = visitor.getGrammarMeta();
		return meta;
	}

	static MethodMeta parseMethodMeta(Method method) {
		Class<?> returnClass = method.getReturnType();
		MethodMeta meta = new MethodMeta(returnClass);
		meta.setParamType(getParamType(returnClass));
		meta.setMethodName(method.getName());
		if (meta.isList())
			meta.setWrapClass(getWrapClass(method.getGenericReturnType()));

		Class<?> types[] = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Type[] paramTypes = method.getGenericParameterTypes();
		MethodParam[] params = null;
		if (types != null && types.length > 0) {
			params = new MethodParam[types.length];
			int n = 0;
			for (int i = 0; i < types.length; i++) {
				Class<?> paraType = types[i];
				MethodParam metaParam = new MethodParam(paraType);
				String paraName = getParamName(parameterAnnotations[i]);
				if (StringUtils.isNotEmpty(paraName)) {
					metaParam.setNamedParam(true);
					n++;
				}
				// 接口类编译的时候 方法的参数未写入class文件，所以无法获取
				metaParam.setName(paraName);
				metaParam.setParamType(getParamType(paraType));
				metaParam.setIndex(i);
				if (metaParam.isList())
					metaParam.setWrapClass(getWrapClass(paramTypes[i]));
				else if (metaParam.isArray())
					metaParam.setWrapClass(paraType.getComponentType());
				params[i] = metaParam;
			}
			meta.setParamCount(types.length);
			meta.setParamNames(n);
			meta.setMethodParams(params);
		}
		return meta;
	}

	static Class<?> getWrapClass(Type paramType) {
		/** 如果是泛型类型 */
		if (paramType instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) paramType)
					.getActualTypeArguments();
			return (Class<?>) types[0];
		}
		return Object.class;
	}

	/**
	 * 
	 * 获取注解名
	 * 
	 * @param annotations
	 *            注解信息
	 * @return
	 * @author YZC
	 */
	static String getParamName(Annotation annotations[]) {
		if (annotations == null || annotations.length == 0) {
			return null;
		}
		Annotation annotation = annotations[0];
		if (annotation instanceof Param) {
			Param rp = (Param) annotation;
			return rp.value();
		}
		return null;
	}

	static boolean isInArray(String str, String[] arrays) {
		for (int i = 0; i < arrays.length; i++) {
			if (StringUtils.equals(str, arrays[i]))
				return true;
		}
		return false;
	}

	static ParamType getParamType(Class<?> type) {
		ParamType pt;
		if (Tool.isVoid(type))
			pt = ParamType.Void;
		else if (Tool.isBaseType(type))
			pt = ParamType.BaseType;
		else if (Tool.isList(type))
			pt = ParamType.List;
		else if (Tool.isArray(type))
			pt = ParamType.Array;
		else if (Tool.isMap(type))
			pt = ParamType.Map;
		else if (Tool.isPrimitiveOrWrapper(type))
			pt = ParamType.Primitive;
		else if (isPojo(type))
			pt = ParamType.Pojo;
		else if (Tool.isJavaClass(type))
			pt = ParamType.Custom;
		else
			pt = ParamType.Object;
		return pt;
	}

	public static int parseCommand(String cmdStr) {
		if (StringUtils.isEmpty(cmdStr))
			return COMMAND_UNKNOW;
		else if (isInArray(cmdStr, Constants.INSERTS))
			return COMMAND_INSERT;
		else if (isInArray(cmdStr, Constants.UPDATES))
			return COMMAND_UPDATE;
		else if (isInArray(cmdStr, Constants.SELECTS)) {
			if ("get".equalsIgnoreCase(cmdStr))
				return COMMAND_SINGLE;
			else
				return COMMAND_SELECT;
		} else if (isInArray(cmdStr, Constants.DELETES))
			return COMMAND_DELETE;
		else
			return COMMAND_UNKNOW;
	}

	public static String parseOperator(String operator) {
		// Between, LessThan, GreaterThan, IsNotNull,
		// IsNull, NotLike, Like, NotIn, In, NotNull, Not;
		if (StringUtils.isEmpty(operator))
			return SqlBuilder.EQUAL;
		else if ("Equals".equals(operator))
			return SqlBuilder.EQUAL;
		else if ("Between".equals(operator))
			return SqlBuilder.BETWEEN;
		else if ("LessThan".equals(operator))
			return SqlBuilder.LT;
		else if ("GreaterThan".equals(operator))
			return SqlBuilder.GT;
		else if ("IsNotNull".equals(operator))
			return SqlBuilder.NOT_NULL;
		else if ("IsNull".equals(operator))
			return SqlBuilder.IS_NULL;
		else if ("NotLike".equals(operator))
			return SqlBuilder.NOT_LIKE;
		else if ("Like".equals(operator))
			return SqlBuilder.LIKE;
		else if ("NotIn".equals(operator))
			return SqlBuilder.NOT_IN;
		else if ("In".equals(operator))
			return SqlBuilder.IN;
		else if ("NotNull".equals(operator))
			return SqlBuilder.NOT_NULL;
		else if ("Not".equals(operator))
			return SqlBuilder.IS_NOT;
		else
			throw new RuntimeException("无法识别该类过滤操作：" + operator);
	}

	public static Object getValue(PropertyDescriptor prop, Object obj) {
		Method read = prop.getReadMethod();
		try {
			Object result = read.invoke(obj, VOID_PARAS);
			return result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	static final Object[] VOID_PARAS = new Object[0];
	static final Class<?>[] NO_ARGUMENTS = new Class<?>[0];
}
