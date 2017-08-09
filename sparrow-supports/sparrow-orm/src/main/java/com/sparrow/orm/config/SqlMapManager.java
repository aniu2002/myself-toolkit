package com.sparrow.orm.config;

import com.sparrow.orm.exceptions.SqlMappingException;
import com.sparrow.orm.session.SessionFactory;

/**
 * 
 * SqlMap管理器
 * 
 * @author YZC
 * @version 1.0 (2014-4-2)
 * @modify
 */
public class SqlMapManager {
	private SessionFactory sessionFactory;
	private TableConfiguration tableConfiguration;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.tableConfiguration = sessionFactory.getTableConfiguration();
	}

	/**
	 * 提供po的mapper获取,不可重写
	 * 
	 * @param clazz
	 *            具体某个po类的class
	 * @return 返回某个po类对应的mapper
	 * @throws SqlMappingException
	 *             po的sql映射异常（系统异常）
	 */
	public final TableMapping getTableMapping(Class<?> clazz)
			throws SqlMappingException {
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(clazz);
		if (tableMapping == null)
			throw new SqlMappingException("未找到与类对应的数据表映射:" + clazz.getName());
		return tableMapping;
	}

	/**
	 * 
	 * 获取entity对应的class信息
	 * 
	 * @param name
	 *            实体名称
	 * @return 实体对应clas信息
	 * @author YZC
	 */
	public final TableMapping getTableMapping(String name) {
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(name);
		if (tableMapping == null)
			throw new SqlMappingException("未找到与类对应的数据表映射:" + name);
		return tableMapping;
	}

	/**
	 * 
	 * 判断是否存在这样pojo - class
	 * 
	 * @param name
	 *            实体名
	 * @return 存在与否
	 * @author YZC
	 */
	public final boolean hasTableMapping(String name) {
		return this.tableConfiguration.hasTableMapping(name);
	}

	public final boolean hasTableMapping(Class<?> clazz) {
		return this.tableConfiguration.hasTableMapping(clazz);
	}

	public final void registryPojo(Class<?> clazz) {
		this.tableConfiguration.addTableCfg(clazz);
	}
}
