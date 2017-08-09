package com.sparrow.service.config.rules;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import com.sparrow.service.config.AnnotationConfig;
import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.config.InterceptorConfig;
import com.sparrow.service.config.ProxyConfig;
import com.sparrow.service.config.ScanConfig;
import com.sparrow.service.config.SetterConfig;


public class AnnotationBeanRuleSet extends RuleSetBase {

	public void addRuleInstances(Digester digester) {
		digester.addObjectCreate("define/annotation", AnnotationConfig.class);
		digester.addSetProperties("define/annotation");
		digester.addObjectCreate("define/scan", ScanConfig.class);
		digester.addSetProperties("define/scan");
		// 添加annotation的setter
		digester.addObjectCreate("define/annotation/setter", SetterConfig.class);
		digester.addSetProperties("define/annotation/setter");
		digester.addSetNext("define/annotation/setter", "addSetterConfig",
				"com.sparrow.service.config.SetterConfig");
		// 添加scan的setter
		digester.addObjectCreate("define/scan/setter", SetterConfig.class);
		digester.addSetProperties("define/scan/setter");
		digester.addSetNext("define/scan/setter", "addSetterConfig",
				"com.sparrow.service.config.SetterConfig");
		// 加入配置
		digester.addSetNext("define/annotation", "addAnnotationConfig",
				"com.sparrow.service.config.AnnotationConfig");
		// 加入scan配置
		digester.addSetNext("define/scan", "addScanConfig",
				"com.sparrow.service.config.ScanConfig");

		digester.addObjectCreate("define/interceptor", InterceptorConfig.class);
		digester.addSetProperties("define/interceptor");
		digester.addSetNext("define/interceptor", "addBeanConfig",
				"com.sparrow.service.config.BeanConfig");

		digester.addObjectCreate("define/interceptor/setter",
				SetterConfig.class);
		digester.addSetProperties("define/interceptor/setter");
		digester.addSetNext("define/interceptor/setter", "addSetterConfig",
				"com.sparrow.service.config.SetterConfig");

		digester.addObjectCreate("define/bean", BeanConfig.class);
		digester.addSetProperties("define/bean");
		digester.addSetProperty("define/bean/ref", "property", "rid");
		digester.addSetProperty("define/bean/set", "property", "value");
		digester.addSetNext("define/bean", "addBeanConfig",
				"com.sparrow.service.config.BeanConfig");

		digester.addObjectCreate("define/bean/setter", SetterConfig.class);
		digester.addSetProperties("define/bean/setter");
		digester.addSetNext("define/bean/setter", "addSetterConfig",
				"com.sparrow.service.config.SetterConfig");

		digester.addObjectCreate("define/proxy", ProxyConfig.class);
		digester.addSetProperties("define/proxy");
		digester.addSetNext("define/proxy", "addProxyConfig",
				"com.sparrow.service.config.ProxyConfig");
	}
}
