package com.sparrow.data.service.exports.handler;

import java.util.Map;

public class MapExportHandler extends ExportHandler<Map<String, Object>> {
	/** 对于对象列表导入时，记录对象property信息 */
	String[] keyIndexes;

	public String[] getKeyIndexes() {
		return keyIndexes;
	}

	public void setKeyIndexes(String[] keyIndexes) {
		this.keyIndexes = keyIndexes;
	}

	@Override
	public String fetchValue(Map<String, Object> map, int dataIndex) {
		String idxKey = this.keyIndexes[dataIndex];
		return (String) getValue(map.get(idxKey));
	}

	static String getValue(Object obj) {
		Object result = obj;
		if (result != null) {
			if (result instanceof String)
				return (String) result;
			else
				return result.toString();
		} else
			return null;
	}
}
