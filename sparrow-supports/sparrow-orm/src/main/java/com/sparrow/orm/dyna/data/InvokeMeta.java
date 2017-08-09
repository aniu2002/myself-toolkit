package com.sparrow.orm.dyna.data;

import com.sparrow.orm.config.TableMapping;

public class InvokeMeta extends ClassType {
	private static final String EMPTY_STR = "";
	private Class<?> returnClass;
	private Class<?> wrapClass;
	private MethodParam relations[];
	private TableMapping tableMapping;
	private String methodName;
	private String sql;
	private String error = EMPTY_STR;
	private int command;
	private boolean hasVarArgs;
	private boolean isNamedParams;
	private boolean dynaQuery;

	public TableMapping getTableMapping() {
		return tableMapping;
	}

	public void setTableMapping(TableMapping tableMapping) {
		this.tableMapping = tableMapping;
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isNamedParams() {
		return isNamedParams;
	}

	public void setNamedParams(boolean isNamedParams) {
		this.isNamedParams = isNamedParams;
	}

	public boolean isDynaQuery() {
		return dynaQuery;
	}

	public void setDynaQuery(boolean dynaQuery) {
		this.dynaQuery = dynaQuery;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setReturnClass(Class<?> returnClass) {
		this.returnClass = returnClass;
	}

	public Class<?> getWrapClass() {
		return wrapClass;
	}

	public void setWrapClass(Class<?> wrapClass) {
		this.wrapClass = wrapClass;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public MethodParam[] getRelations() {
		return relations;
	}

	public void setRelations(MethodParam[] relations) {
		this.relations = relations;
	}

	public Class<?> getReturnClass() {
		return returnClass;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public boolean hasMethodArguments() {
		return hasVarArgs;
	}

	public void setHasMethodArguments(boolean hasVarArgs) {
		this.hasVarArgs = hasVarArgs;
	}
}
