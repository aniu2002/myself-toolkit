package com.sparrow.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Table 注解描述object对应的数据表信息
 * 
 * @author YZC (2013-10-10-下午1:47:36)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
	/**
	 * 数据库表名称
	 * 
	 * @return
	 */
	String table();

	/**
	 * 数据库表的其他描述信息
	 * 
	 * @return
	 */
	String desc();
}
