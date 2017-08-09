package com.sparrow.core.aop.matcher;

import java.lang.reflect.Method;

public interface MethodMatcher {

	boolean match(Method method);

}