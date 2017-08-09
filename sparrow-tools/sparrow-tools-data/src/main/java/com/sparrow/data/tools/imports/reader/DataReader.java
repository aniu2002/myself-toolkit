package com.sparrow.data.tools.imports.reader;

import java.io.IOException;
import java.util.List;

import com.sparrow.data.tools.imports.ImpSetting;

public interface DataReader {

	void setImpSetting(ImpSetting impSetting);

	void open() throws IOException;

	Object read() throws IOException;

	List<Object> read(int size) throws IOException;

	void close() throws IOException;
}
