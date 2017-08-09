package com.sparrow.server.web.meta;

import com.sparrow.http.common.MimeType;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.ConvertUtils;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqParameter;
import com.sparrow.server.web.annotation.RequestBody;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.converter.MessageConverter;
import com.sparrow.server.web.converter.MessageConverterFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 获取method的参数名称 javassist和asm可以实现，jdk确实没有内置这个，大概是觉得参数名不重要吧
 * 
 */
public class RequestInvoker {
	private Method method;
	private MetaType[] types;
	/** 0 - default, 1 - text, 2 - json, */
	private MessageConverter converter;

	public RequestInvoker(Method method) {
		this.method = method;
		String paras[] = Paranamer.lookupParameterNames(method);
		Class<?> types[] = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		MetaType[] gTypes = null;
		if (paras != null && paras.length > 0) {
			gTypes = new MetaType[paras.length];
			for (int i = 0; i < paras.length; i++) {
				Class<?> paraType = types[i];
				MetaType metaType = new MetaType(paraType);
				this.setParamName(metaType, parameterAnnotations[i], paras[i]);
				gTypes[i] = metaType;
			}
		}
		MessageConverter sConverter = null;
		if (method.isAnnotationPresent(ResponseBody.class)) {
			Class<?> type = method.getReturnType();
			if (type == String.class)
				sConverter = MessageConverterFactory.TEXT_CONVERTER;
			else if (Object.class.isAssignableFrom(type)) {
				sConverter = MessageConverterFactory.JSON_CONVERTER;
			}
		}
		this.converter = sConverter;
		this.types = gTypes;
	}

	public void destroy() {
		this.converter = null;
		this.method = null;
		if (this.types != null) {
			for (MetaType mt : this.types)
				mt.destroy();
			this.types = null;
		}
	}

	public MessageConverter getConverter() {
		return converter;
	}

	public Object invoke(Object target, ValueGetter getter) throws Throwable {
		MetaType[] pTypes = this.types;
		Method invoker = this.method;
		try {
			if (pTypes == null || pTypes.length == 0)
				return invoker.invoke(target);
			else {
				int len = pTypes.length;
				Object args[] = new Object[len];
				for (int i = 0; i < len; i++) {
					args[i] = this.instanceBean(pTypes[i], getter);
				}
				return invoker.invoke(target, args);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// SysLogger.error(" -- Request invoker error : {}",
			// e.getMessage());
			throw e.getTargetException();
		}
		// Void.class
		return null;
	}

	public Method getMethod() {
		return method;
	}

	private Object instanceBean(MetaType metaType, ValueGetter getter) {
		int bind = metaType.getBind();
		String name = metaType.getName();
		Class<?> type = metaType.getType();
		if (metaType.getBind() == 3) {
			String content = getter.getRequestText();
			if (StringUtils.isEmpty(content))
				return null;
			if (type == String.class)
				return content;
			else if (getter.getMimeType().compare(MimeType.JSON_TYPE)) {
				try {
					return JsonMapper.mapper.readValue(getter.getRequestText(),
							type);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (getter.getMimeType().compare(MimeType.XML_TYPE)) {

			}
			return null;
		}
		String value = (bind == 1 ? getter.getRequestParameter(name) : getter
				.getPathVariable(name));
		if (Map.class.isAssignableFrom(type))
			return getter.getParas();
		else if (type.isPrimitive() || Number.class.isAssignableFrom(type))
			return ConvertUtils.convert(value, type);
		else if (String.class == type)
			return value;
		else {
			Object instance = ClassUtils.born(type);
			PropertyDescriptor[] props = PropertyUtils
					.getPropertyDescriptors(type);
			PropertyDescriptor pd;
			Object args[] = new Object[1];
			for (int i = 0; i < props.length; i++) {
				pd = props[i];
				value = getter.getRequestParameter(pd.getName());
				if (value == null)
					continue;
				Object nValue = ConvertUtils.convert(value,
						pd.getPropertyType());
				args[0] = nValue;
				try {
					pd.getWriteMethod().invoke(instance, args);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return instance;
		}
	}

	public void setParamName(MetaType metaType, Annotation annotations[],
			String defaultName) {
		if (annotations == null || annotations.length == 0) {
			metaType.setBind(1);
			metaType.setName(defaultName);
			return;
		}
		Annotation annotation = annotations[0];
		if (annotation instanceof PathVariable) {
			PathVariable pv = (PathVariable) annotation;
			metaType.setBind(2);
			metaType.setName(pv.value());
		} else if (annotation instanceof ReqParameter) {
			ReqParameter rp = (ReqParameter) annotation;
			metaType.setBind(1);
			metaType.setName(rp.value());
		} else if (annotation instanceof RequestBody) {
			metaType.setBind(3);
			metaType.setName(null);
		} else {
			metaType.setBind(1);
			metaType.setName(defaultName);
		}
	}
}
