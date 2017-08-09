package com.sparrow.service.config;

import com.sparrow.service.context.ContextLoadListener;

public class DefaultConfigEventListener implements ContextLoadListener {

	@Override
	public void activeAnnotationEvent(AnnotationConfig annotationConfig,
			ConfigurationWrapper wrapper) {
		System.out.println(" BeanCfg : " + annotationConfig);
	}

	@Override
	public void activeScanEvent(ScanConfig scanConfig,
			ConfigurationWrapper wrapper) {
		
	}
}
