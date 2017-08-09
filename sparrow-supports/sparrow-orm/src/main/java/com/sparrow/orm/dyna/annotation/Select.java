package com.sparrow.orm.dyna.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {

	String value();

	/**
	 * 
	 * 0 代表使用named parameter 的方式 <br/>
	 * 1 代表使用数组下标索引的方式 如: #1 取方法参数第二个参数
	 * 
	 * @return
	 * @author YZC
	 */
	int type() default 0;
}
