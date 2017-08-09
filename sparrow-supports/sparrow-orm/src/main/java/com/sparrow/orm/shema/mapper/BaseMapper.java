package com.sparrow.orm.shema.mapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseMapper<T> {
	static final Map<Class<?>, PropertyDescriptor[]> descriptorsCache = new ConcurrentHashMap<Class<?>, PropertyDescriptor[]>();

	public abstract FieldItem[] getMappingFields();

	public abstract Class<T> getTarget();

	public abstract String getInsertSql();

	public abstract String getSelectSql();

	public abstract String getDeleteSql();

	public final void toBean() {

	}

	public final void insert(T object) {

	}

	static PropertyDescriptor[] propertyDescriptors(Class<?> c)
			throws SQLException {
		PropertyDescriptor[] props = descriptorsCache.get(c);
		if (props == null) {
			BeanInfo beanInfo = null;
			try {
				beanInfo = Introspector.getBeanInfo(c);
			} catch (IntrospectionException e) {
				throw new SQLException("Bean introspection failed: "
						+ e.getMessage());
			}
			props = beanInfo.getPropertyDescriptors();
			descriptorsCache.put(c, props);
		}
		return props;
	}

	protected PropertyDescriptor getPropertyDescriptor(String name) {
		try {
			PropertyDescriptor[] props = propertyDescriptors(this.getTarget());
			for (int i = 0; i < props.length; i++) {
				if (name.equalsIgnoreCase(props[i].getName()))
					return props[i];
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
