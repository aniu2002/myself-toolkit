package com.sparrow.data.tools.imports.reader;

import java.io.File;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.reader.csv.CsvDataReader;
import com.sparrow.data.tools.imports.reader.excel.V97ExcelDataReader;
import com.sparrow.data.tools.store.FileType;

/**
 * 建造者模式，构造负责对象，结构清晰明了
 * 
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class DataReaderBuilder {
	private final File file;
	private ImpSetting impSetting;
	private FileType type = FileType.Csv;

	private DataReaderBuilder(File file) {
		this.file = file;
	}

	public static DataReaderBuilder create(String file) {
		return create(new File(file));
	}

	public static DataReaderBuilder create(File file) {
		return new DataReaderBuilder(file);
	}

	public DataReaderBuilder setType(FileType type) {
		this.type = type;
		return this;
	}

	void checkAndInitImpSetting() {
		if (this.impSetting == null)
			this.impSetting = new ImpSetting();
	}

	public DataReaderBuilder setStartSheet(int startSheet) {
		this.checkAndInitImpSetting();
		this.impSetting.setStartSheet(startSheet);
		return this;
	}

	public DataReaderBuilder setStartRow(int startRow) {
		this.checkAndInitImpSetting();
		this.impSetting.setStartRow(startRow);
		return this;
	}

	public DataReaderBuilder setStartCol(int startCol) {
		this.checkAndInitImpSetting();
		this.impSetting.setStartCol(startCol);
		return this;
	}

	public DataReaderBuilder setColumnLimit(int limit) {
		this.checkAndInitImpSetting();
		this.impSetting.setLimit(limit);
		return this;
	}

	public DataReaderBuilder setSetting(ImpSetting impSetting) {
		if (impSetting != null)
			this.impSetting = impSetting;
		return this;
	}

	public DataReader build() {
		DataReader reader;
		FileType ty = this.type;
		switch (ty) {
		case Csv:
			reader = new CsvDataReader(file);
			break;
		case Excel:
			reader = new V97ExcelDataReader(file);
			break;
		default:
			reader = new CsvDataReader(file);
		}
		if (this.impSetting != null)
			reader.setImpSetting(this.impSetting);
		return reader;
	}
}
