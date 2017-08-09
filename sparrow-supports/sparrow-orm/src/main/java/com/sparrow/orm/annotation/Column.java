package com.sparrow.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column 该注解描述object属性对应数据库表的字段信息
 * 
 * @author YZC (2013-10-10-下午1:58:29)
 */
@Target(value = { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
	/**
	 * 数据库表字段是否不为空
	 * 
	 * @return
	 */
	boolean notnull() default false;

	/**
	 * 数据库表字段sql type
	 * 
	 * @return
	 */
	int type() default 0;

	/**
	 * 数据库表字段名称
	 * 
	 * @return
	 */
	String column();

	/**
	 * 数据库表字段注释信息
	 * 
	 * @return
	 */
	String comment() default "";

	/**
	 * 数据库表字段长度
	 * 
	 * @return
	 */
	int length() default 0;

	/**
	 * 
	 * insert的时候忽略该字段，默认不忽略
	 * 
	 * @return
	 * @author YZC
	 */
	boolean ignoreInsert() default false;

	/**
	 * 
	 * update的时候忽略该字段，默认不忽略
	 * 
	 * @return
	 * @author YZC
	 */
	boolean ignoreUpdate() default false;
}
