package com.sparrow.data.tools.validate;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 导入导出每列数据校验管理器
 * 
 * @author YZC
 * @version 1.0 (2014-3-28)
 * @modify
 */
public class ValidatorManager {
	private Map<String, Validator> validators;

	public Map<String, Validator> getValidators() {
		return validators;
	}

	public void setValidators(Map<String, Validator> validators) {
		if (this.validators == null)
			this.validators = new ConcurrentHashMap<String, Validator>();
		if (validators != null && !validators.isEmpty()) {
			Iterator<Map.Entry<String, Validator>> iterator = validators
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Validator> entry = iterator.next();
				this.validators.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public Validator getValidator(String key) {
		if (StringUtils.isEmpty(key))
			return null;
		if (this.validators == null || this.validators.isEmpty())
			return null;
		return this.validators.get(key);
	}
}
