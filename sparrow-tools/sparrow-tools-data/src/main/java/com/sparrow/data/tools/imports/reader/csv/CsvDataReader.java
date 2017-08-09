package com.sparrow.data.tools.imports.reader.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.reader.DataReader;

public class CsvDataReader implements DataReader {
	private final File csvFile;
	private CsvReader csvReader;
	private int startRow;

	public CsvDataReader(String csvFile) {
		this(new File(csvFile));
	}

	public CsvDataReader(File csvFile) {
		this.csvFile = csvFile;
	}

	public void setImpSetting(ImpSetting impSetting) {
		this.startRow = impSetting.getStartRow();
	}

	@Override
	public void open() throws IOException {
		if (this.csvReader != null)
			return;
		this.csvReader = this.createCsvReader();
		this.skipRows(this.startRow);
	}

	CsvReader createCsvReader() {
		try {
			// 从输入流创建
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(this.csvFile), "UTF-8"));
			CsvReader csvReader = new CsvReader(reader);
			return csvReader;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	void skipRows(int i) throws IOException {
		int c = 0;
		while (csvReader.readRecord() && c < i) {
			c++;
		}
	}

	@Override
	public synchronized Object read() throws IOException {
		if (this.csvReader.readRecord())
			return this.csvReader.getValues();
		return null;
	}

	@Override
	public synchronized List<Object> read(int size) throws IOException {
		CsvReader reader = this.csvReader;
		List<Object> list = null;
		if (reader.readRecord()) {
			list = new ArrayList<Object>(size);
			list.add(reader.getValues());
			int c = 1;
			while (reader.readRecord() && c < size) {
				list.add(reader.getValues());
				c++;
			}
		}
		return list;
	}

	@Override
	public void close() throws IOException {
		this.csvReader.close();
	}
}
