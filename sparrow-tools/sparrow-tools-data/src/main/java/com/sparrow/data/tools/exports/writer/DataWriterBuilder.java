package com.sparrow.data.tools.exports.writer;

import java.io.File;

import com.sparrow.data.tools.exports.ExpSetting;
import com.sparrow.data.tools.exports.writer.csv.CsvDataWriter;
import com.sparrow.data.tools.exports.writer.excel.Excel2003DataWriter;
import com.sparrow.data.tools.exports.writer.excel.Excel2003ModifyDataWriter;
import com.sparrow.data.tools.exports.writer.excel.Excel2007DataWriter;
import com.sparrow.data.tools.store.FileType;

/**
 * 建造者模式，构造复杂对象，结构清晰明了
 * 
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class DataWriterBuilder {
	private final File file;
	private File templateFile;
	private ExpSetting expSetting;
	private FileType type;
	private String headers[];
	private boolean update;

	private DataWriterBuilder(File file) {
		this.file = file;
	}

	public static DataWriterBuilder create(String file) {
		return create(new File(file));
	}

	public static DataWriterBuilder create(File file) {
		return new DataWriterBuilder(file);
	}

	public DataWriterBuilder fileType(FileType type) {
		this.type = type;
		return this;
	}

	public DataWriterBuilder excelTemplateFile(File templateFile) {
		this.templateFile = templateFile;
		return this;
	}

	void checkAndInitImpSetting() {
		if (this.expSetting == null)
			this.expSetting = new ExpSetting();
	}

	public DataWriterBuilder excelStartSheet(int startSheet) {
		this.checkAndInitImpSetting();
		this.expSetting.setStartSheet(startSheet);
		return this;
	}

	public DataWriterBuilder excelStartRow(int startRow) {
		this.checkAndInitImpSetting();
		this.expSetting.setStartRow(startRow);
		return this;
	}

	public DataWriterBuilder exportSetting(ExpSetting expSetting) {
		if (expSetting != null)
			this.expSetting = expSetting;
		return this;
	}

	public DataWriterBuilder headers(String[] headers) {
		if (headers != null)
			this.headers = headers;
		return this;
	}

	/**
	 * 
	 * excel根据模板导出的时候使用
	 * 
	 * @param update
	 *            是否是update，如果有模板，则根据模板导出更新模板内容
	 * @return
	 * @author YZC
	 */
	public DataWriterBuilder excelModify(boolean update) {
		this.update = update;
		return this;
	}

	/** 导出默认单个文件的总数据数， 默认是65535 */
	public DataWriterBuilder exportMax(int exportMax) {
		this.checkAndInitImpSetting();
		this.expSetting.setExportMax(exportMax);
		return this;
	}

	/**
	 * 导出默认每个sheet的数据数 ，该值大于max的值 多余的无效，默认是65535,超过该值设定小于总数的话，多余的数据插入到 下一个sheet中
	 */
	public DataWriterBuilder excelSheetRows(int sheetRows) {
		this.checkAndInitImpSetting();
		this.expSetting.setSheetRows(sheetRows);
		return this;
	}

	/**
	 * 
	 * 构建dataWriter
	 * 
	 * @return 数据导出写入对象
	 * @author YZC
	 */
	public DataWriter build() {
		DataWriter extractor;
		FileType ty = this.type;
		File file = this.file;
		switch (ty) {
		case Csv:
			extractor = new CsvDataWriter(file);
			break;
		case Excel:
			if (this.update && this.templateFile != null
					&& this.templateFile.exists())
				extractor = new Excel2003ModifyDataWriter(this.templateFile,
						file);
			else
				extractor = new Excel2003DataWriter(file);
			break;
		case Excel2003:
			if (this.update && this.templateFile != null
					&& this.templateFile.exists())
				extractor = new Excel2003ModifyDataWriter(this.templateFile,
						file);
			else
				extractor = new Excel2003DataWriter(file);
			break;
		case Excel2007:
			extractor = new Excel2007DataWriter(file);
			break;
		default:
			extractor = new CsvDataWriter(file);
		}
		if (this.expSetting != null)
			extractor.setExpSetting(this.expSetting);
		extractor.setHeaders(this.headers);
		return extractor;
	}
}
