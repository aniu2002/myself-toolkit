package com.sparrow.data.tools.exports.writer.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.sparrow.data.tools.exports.ExpSetting;
import com.sparrow.data.tools.exports.writer.AbstractDataWriter;

public class CsvDataWriter extends AbstractDataWriter {
	private static final String ENCODING = "GBK";
	private final File csvFile;
	private CsvWriter csvWriter;

	public CsvDataWriter(File csvFile) {
		this.csvFile = csvFile;
	}

	@Override
	public void setExpSetting(ExpSetting expSetting) {

	}

	@Override
	protected void doOpen() throws IOException {
		if (this.csvWriter != null)
			return;
		this.csvWriter = this.createCsvWrite();
	}

	CsvWriter createCsvWrite() {
		try {
			// 从输入流创建
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.csvFile), ENCODING));
			// , "UTF-16LE"
			// writer.write("\uFEFF");
			CsvWriter csvWriter = new CsvWriter(writer);
			return csvWriter;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void writeRow(String[] columns) throws IOException {
		this.csvWriter.writeRecord(columns);
		// this.csvWriter.endRecord();
	}

	@Override
	public void close() throws IOException {
		this.csvWriter.close();
	}
}
