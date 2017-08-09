package com.sparrow.orm.dyna.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sparrow.orm.dyna.enums.SqlType;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SqlScript {

	String value();

	SqlType type() default SqlType.Select;
}
