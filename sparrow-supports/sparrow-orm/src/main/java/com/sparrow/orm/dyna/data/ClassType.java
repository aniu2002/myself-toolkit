package com.sparrow.orm.dyna.data;

import com.sparrow.orm.dyna.enums.ParamType;

public abstract class ClassType {
	private ParamType paramType;

	public ParamType getParamType() {
		return paramType;
	}

	public void setParamType(ParamType paramType) {
		this.paramType = paramType;
	}

	public boolean isBaseType() {
		return this.paramType == ParamType.BaseType;
	}

	public boolean isVoid() {
		return this.paramType == ParamType.Void;
	}

	public boolean isList() {
		return this.paramType == ParamType.List;
	}

	public boolean isMap() {
		return this.paramType == ParamType.Map;
	}

	public boolean isArray() {
		return this.paramType == ParamType.Array;
	}

	public boolean isCustom() {
		return this.paramType == ParamType.Custom;
	}

	public boolean isPojo() {
		return this.paramType == ParamType.Pojo;
	}
}
