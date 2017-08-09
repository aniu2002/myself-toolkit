package com.sparrow.orm.dyna.invoker;

import java.util.Map;

import com.sparrow.orm.dyna.data.MethodParam;
import com.sparrow.orm.sql.named.MapSqlParameterSource;


public class DynaMapParameterSource extends MapSqlParameterSource {

	public DynaMapParameterSource() {
		super();
	}

	public DynaMapParameterSource(Map<String, ?> values) {
		super(values);
	}

	public DynaMapParameterSource(String paramName, Object value) {
		super(paramName, value);
	}

	public DynaMapParameterSource(String[] paramNames, Object[] values) {
		if (this.isEmpty(paramNames) || this.isEmpty(values))
			throw new RuntimeException("参数名或者参数值为空");
		if (paramNames.length != values.length)
			throw new RuntimeException("参数名和参数值的长度不能为空");
		for (int i = 0; i < paramNames.length; i++)
			this.addValue(paramNames[i], values[i]);
	}

	public DynaMapParameterSource(MethodParam[] types, Object args[]) {
		if (this.isEmpty(types) || this.isEmpty(args))
			throw new RuntimeException("参数名或者参数值为空");
		if (types.length != args.length)
			throw new RuntimeException("参数名和参数值的长度不能为空");
		for (int i = 0; i < args.length; i++)
			this.addValue(types[i].getName(), args[i]);
	}

	boolean isEmpty(Object v[]) {
		if (v == null || v.length == 0)
			return true;
		return false;
	}
}
