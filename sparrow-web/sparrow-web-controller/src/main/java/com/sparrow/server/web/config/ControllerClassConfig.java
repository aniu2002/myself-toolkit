package com.sparrow.server.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.server.web.meta.RequestInvoker;

public class ControllerClassConfig {
	private String beanId;
	private String module;
	private String beanClazz;
	private Class<?> controllerClazz;
	private Object controllerInst;

	private String input;
	private String scope = "request";
	private String parameter = null;

	private Map<String, ControllerMethodConfig> actionMethods = new HashMap<String, ControllerMethodConfig>();
	private List<UrlMatcherMethodConfig> actionRegMethods = new ArrayList<UrlMatcherMethodConfig>();
	private UrlMatcherMethodConfig methodArray[];

	public void destroy() {
		this.controllerClazz = null;
		this.controllerInst = null;
		this.remove(this.actionMethods.values().iterator());
		this.removeE(this.actionRegMethods.iterator());
		if (this.methodArray != null) {
			RequestInvoker ri;
			for (UrlMatcherMethodConfig cfg : this.methodArray) {
				ri = cfg.getMethodInvoker();
				if (ri != null) {
					ri.destroy();
					ri = null;
				}
				cfg = null;
			}
			this.methodArray = null;
		}
		this.actionMethods.clear();
	}

	void removeE(Iterator<UrlMatcherMethodConfig> iterator) {
		UrlMatcherMethodConfig cfg;
		RequestInvoker ri;
		while (iterator.hasNext()) {
			cfg = iterator.next();
			ri = cfg.getMethodInvoker();
			if (ri != null) {
				ri.destroy();
				ri = null;
			}
			iterator.remove();
			cfg = null;
		}
	}

	void remove(Iterator<ControllerMethodConfig> it) {
		ControllerMethodConfig cfg;
		RequestInvoker ri;
		while (it.hasNext()) {
			cfg = it.next();
			ri = cfg.getMethodInvoker();
			if (ri != null) {
				ri.destroy();
				ri = null;
			}
			it.remove();
			cfg = null;
		}
	}

	public void add(String path, ControllerMethodConfig methodCfg) {
		actionMethods.put(path + '_' + methodCfg.getReqMethod(), methodCfg);
	}

	public void add(UrlMatcherMethodConfig cfg) {
		actionRegMethods.add(cfg);
	}

	public void initialize() {
		if (this.methodArray == null) {
			if (this.actionRegMethods == null
					|| this.actionRegMethods.isEmpty())
				return;
			int size = this.actionRegMethods.size();
			this.methodArray = this.actionRegMethods
					.toArray(new UrlMatcherMethodConfig[size]);
		}
	}

	public MatchedHandler getMatched(String path, String reqMethod) {
		String reqpath = path + '_' + reqMethod;
		ControllerMethodConfig cfg = actionMethods.get(reqpath);
		if (cfg != null) {
			MatchedHandler handler = new MatchedHandler();
			handler.setMethodcfg(cfg);
			return handler;
		}
		UrlMatcherMethodConfig[] arrar = this.methodArray;
		if (arrar == null)
			return null;
		else {
			for (int i = 0; i < arrar.length; i++) {
				MatcherResult result = arrar[i].match(reqpath);
				if (result != null) {
					MatchedHandler handler = new MatchedHandler();
					handler.setMethodcfg(arrar[i]);
					handler.setParakeys(result.getParakeys());
					handler.setRegmatched(true);
					handler.setValues(result.getValues());
					return handler;
				}
			}
		}
		return null;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String getBeanClazz() {
		return beanClazz;
	}

	public void setBeanClazz(String beanClazz) {
		this.beanClazz = beanClazz;
	}

	public String getScope() {
		return (this.scope);
	}

	public String getModule() {
		return module;
	}

	public void setModule(String model) {
		this.module = model;
	}

	public Class<?> getControllerClazz() {
		return controllerClazz;
	}

	public void setControllerClazz(Class<?> controllerClazz) {
		this.controllerClazz = controllerClazz;
	}

	public Object getControllerInst() {
		return controllerInst;
	}

	public void setControllerInst(Object controllerInst) {
		this.controllerInst = controllerInst;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Return a String representation of this object.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("ControllerConfig[");
		sb.append("module=");
		sb.append(module);
		if (input != null) {
			sb.append(",input=");
			sb.append(input);
		}
		if (controllerClazz != null) {
			sb.append(",name=");
			sb.append(controllerClazz.getName());
		}
		if (parameter != null) {
			sb.append(",parameter=");
			sb.append(parameter);
		}
		return (sb.toString());
	}

	public int getModuleLen() {
		if (StringUtils.isEmpty(this.module))
			return 0;
		return this.module.length();
	}
}
