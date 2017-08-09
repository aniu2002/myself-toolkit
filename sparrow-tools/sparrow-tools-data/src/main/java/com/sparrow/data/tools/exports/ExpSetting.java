package com.sparrow.data.tools.exports;

/**
 * 
 * 导出配置信息
 * 
 * @author YZC
 * @version 1.0 (2014-3-29)
 * @modify
 */
public class ExpSetting {
	public static final int EXPORT_MAX_ROWS = 65535;
	public static final int SHEET_MAX_ROWS = 3000;
	/** 导入导出时数据填充从哪个电子薄开始 */
	private int startSheet;
	/** 导入导出时数据填充从哪行开始 */
	private int startRow;
	/** 导出默认单个文件的总数据数， 默认是65535 */
	private int exportMax;
	/**
	 * 导出默认每个sheet的数据数 ，该值大于max的值 多余的无效，默认是65535,超过该值设定小于总数的话，多余的数据插入到 下一个sheet中
	 */
	private int sheetRows;
	/** 是否写入header信息 */
	private boolean writeHeader;

	public int getExportMax() {
		return exportMax;
	}

	public void setExportMax(int exportMax) {
		this.exportMax = exportMax;
	}

	public int getSheetRows() {
		return sheetRows;
	}

	public void setSheetRows(int sheetRows) {
		this.sheetRows = sheetRows;
	}

	public int getStartSheet() {
		return startSheet;
	}

	public void setStartSheet(int startSheet) {
		this.startSheet = startSheet;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public boolean isWriteHeader() {
		return writeHeader;
	}

	public void setWriteHeader(boolean writeHeader) {
		this.writeHeader = writeHeader;
	}
}
