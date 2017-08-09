package com.sparrow.service.context;

import com.sparrow.service.config.AnnotationConfig;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.config.ScanConfig;

public interface ContextLoadListener {
	int ADD_BEAN = 0;
	int ADD_PROXY = 1;
	int ADD_ANNOTATION = 2;

	public void activeAnnotationEvent(AnnotationConfig annotationConfig,
			ConfigurationWrapper wrapper);

	public void activeScanEvent(ScanConfig scanConfig,
			ConfigurationWrapper wrapper);
}
