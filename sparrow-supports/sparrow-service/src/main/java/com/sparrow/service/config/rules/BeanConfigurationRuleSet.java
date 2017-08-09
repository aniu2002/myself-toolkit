package com.sparrow.service.config.rules;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.config.InterceptorConfig;
import com.sparrow.service.config.ProxyConfig;
import com.sparrow.service.config.SetterConfig;


public class BeanConfigurationRuleSet extends RuleSetBase {

	public void addRuleInstances(Digester digester) {
		digester.addObjectCreate("define/interceptor", InterceptorConfig.class);
		digester.addSetProperties("define/interceptor");
		digester.addSetNext("define/interceptor", "addBeanConfig",
				"com.sparrow.core.service.conf.BeanConfig");

		digester.addObjectCreate("define/interceptor/setter",
				SetterConfig.class);
		digester.addSetProperties("define/interceptor/setter");
		digester.addSetNext("define/interceptor/setter", "addSetterConfig",
				"com.sparrow.core.service.conf.SetterConfig");

		digester.addObjectCreate("define/bean", BeanConfig.class);
		digester.addSetProperties("define/bean");
		digester.addSetProperty("define/bean/ref", "property", "rid");
		digester.addSetProperty("define/bean/set", "property", "value");
		digester.addSetNext("define/bean", "addBeanConfig",
				"com.sparrow.core.service.conf.BeanConfig");

		digester.addObjectCreate("define/bean/setter", SetterConfig.class);
		digester.addSetProperties("define/bean/setter");
		digester.addSetNext("define/bean/setter", "addSetterConfig",
				"com.sparrow.core.service.conf.SetterConfig");

		digester.addObjectCreate("define/proxy", ProxyConfig.class);
		digester.addSetProperties("define/proxy");
		digester.addSetNext("define/proxy", "addProxyConfig",
				"com.sparrow.core.service.conf.ProxyConfig");
	}
}
