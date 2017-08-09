package com.sparrow.data.tools.store;

public interface StoreFileGenerator {
	String generatePath(String name);

	String generateFileName(String name, String suffix);
}
