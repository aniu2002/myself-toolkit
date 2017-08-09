package com.sparrow.data.tools.imports.extract.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractCallback;
import com.sparrow.data.tools.imports.reader.csv.CsvReader;

public class CsvExtractor implements DataExtractor {
	private final File csvFile;
	private int startRow;

	private ExtractCallback callback;

	public CsvExtractor(File csvFile) {
		this.csvFile = csvFile;
	}

	@Override
	public void setExtractCallback(ExtractCallback callback) {
		this.callback = callback;
	}

	@Override
	public void extract() throws SQLException {
		// 从输入流创建
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(this.csvFile), "UTF-8"));
			CsvReader csvReader = new CsvReader(reader);
			int c = 0;
			while (csvReader.readRecord() && c < this.startRow) {
				c++;
			}
			String data[];
			while (csvReader.readRecord()) {
				c++;
				data = csvReader.getValues();
				this.callback.handle(data, 1, c);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setImpSetting(ImpSetting impSetting) {
		this.startRow = impSetting.getStartRow();
	}
}
