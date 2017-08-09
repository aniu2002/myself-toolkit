package com.sparrow.orm.template.simple;

public abstract class TemplateFactory {
	private static OperateTemplate operateTemplate;

	public static final OperateTemplate getOperateTemplate() {
		if (operateTemplate == null) {
			synchronized (TemplateFactory.class) {
				if (operateTemplate == null) {
					operateTemplate = new DefaultOperateTemplate(
							"classpath:jdbc.properties");
				}
			}
		}
		return operateTemplate;
	}
}
