package com.sparrow.orm.dyna.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.orm.dyna.data.InvokeMeta;
import com.sparrow.orm.dyna.invoker.DynaParamInvokeMethod;
import com.sparrow.orm.dyna.invoker.InvokeMethod;
import com.sparrow.orm.config.SqlMapManager;
import com.sparrow.orm.template.HitTemplate;


public class MapperProxy implements InvocationHandler {
	private static final Set<String> OBJECT_METHODS = new HashSet<String>() {
		private static final long serialVersionUID = -1782950882770203582L;
		{
			add("toString");
			add("getClass");
			add("hashCode");
			add("equals");
			add("wait");
			add("notify");
			add("notifyAll");
		}
	};
	/** SqlMap cache 保存类 */
	private static final Map<Method, InvokeMethod> methodCache = new ConcurrentHashMap<Method, InvokeMethod>();
	final HitTemplate hitTemplate;
	final SqlMapManager sqlMapManager;

	public MapperProxy(HitTemplate hitTemplate, SqlMapManager sqlMapManager) {
		this.hitTemplate = hitTemplate;
		this.sqlMapManager = sqlMapManager;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (!OBJECT_METHODS.contains(method.getName())) {
			// final Class<?> declaringInterface = findDeclaringInterface(proxy,
			// method);
			final InvokeMethod invokeMethod = this.getInvokeMethod(method);
			final Object result = invokeMethod.execute(args);
			if (result == null && method.getReturnType().isPrimitive()
					&& !method.getReturnType().equals(Void.TYPE)) {
				throw new RuntimeException(
						"Mapper method '"
								+ method.getName()
								+ "' ("
								+ method.getDeclaringClass()
								+ ") attempted to return null from a method with a primitive return type ("
								+ method.getReturnType() + ").");
			}
			return result;
		}
		return null;
	}

	Class<?> findDeclaringInterface(Object proxy, Method method) {
		Class<?> declaringInterface = null;
		for (Class<?> iface : proxy.getClass().getInterfaces()) {
			try {
				Method m = iface.getMethod(method.getName(),
						method.getParameterTypes());
				if (declaringInterface != null) {
					throw new RuntimeException(
							"Ambiguous method mapping.  Two mapper interfaces contain the identical method signature for "
									+ method);
				} else if (m != null) {
					declaringInterface = iface;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (declaringInterface == null) {
			throw new RuntimeException(
					"Could not find interface with the given method " + method);
		}
		return declaringInterface;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newMapperProxy(Class<T> mapperInterface,
			HitTemplate hitTemplate, SqlMapManager sqlMapManager) {
		ClassLoader classLoader = mapperInterface.getClassLoader();
		Class<?>[] interfaces = new Class[] { mapperInterface };
		MapperProxy proxy = new MapperProxy(hitTemplate, sqlMapManager);
		return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
	}

	InvokeMethod getInvokeMethod(Method method) {
		InvokeMethod invokeMethod = methodCache.get(method);
		if (invokeMethod != null)
			return invokeMethod;
		InvokeMeta invokeMeta = ProxyHelper.parse(method, this.sqlMapManager);
		if (invokeMeta.isNamedParams())
			invokeMethod = new DynaParamInvokeMethod(invokeMeta,
					this.hitTemplate);
		else
			invokeMethod = new InvokeMethod(invokeMeta, this.hitTemplate);
		methodCache.put(method, invokeMethod);
		return invokeMethod;
	}
}
