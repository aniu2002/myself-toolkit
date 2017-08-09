package com.sparrow.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Key 该注解描述object属性对应数据库表的主键字段信息
 * 
 * @author YZC (2013-10-10-下午1:51:02)
 */
@Target(value = { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Key {
	/**
	 * 数据库表字段是否不为空
	 * 
	 * @return
	 */
	boolean notnull() default true;

	/**
	 * 数据库表字段sql type
	 * 
	 * @return
	 */
	int type() default 0;

	/**
	 * 数据库表字段约束
	 * 
	 * @return
	 */
	String constraint() default "primary key";

	/**
	 * 数据库表字段id生成器 如： uuid,auto
	 * 
	 * @return
	 */
	String generator() default "";

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
}
