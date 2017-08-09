package com.sparrow.app.system.service.pojo;

import java.util.List;

public interface PojoInterface {
	public List<String> getClassNames(String path);

	public Object getClassInfo(String className);

}
