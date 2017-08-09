package com.sparrow.data.tools.imports.reader.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.reader.DataReader;

public class V97ExcelDataReader implements DataReader {
	private final File excelFile;
	private ImpSetting impSetting;
	private Workbook book;
	private Sheet[] sheets;
	private Sheet curSheet;
	private int sheetIdx;
	private int rowIdx;
	private boolean hasNext = true;

	public V97ExcelDataReader(File excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public void setImpSetting(ImpSetting impSetting) {
		this.sheetIdx = impSetting.getStartSheet();
		this.book = this.createWorkbook();
		this.rowIdx = impSetting.getStartRow();
		this.impSetting = impSetting;
	}

	@Override
	public void open() throws IOException {
		if (this.book != null)
			return;
		Workbook workbook = this.createWorkbook();
		this.book = workbook;
		this.sheets = (workbook == null ? null : workbook.getSheets());
		this.curSheet = this.sheets[this.sheetIdx];
	}

	Workbook createWorkbook() {
		// 从输入流创建Workbook
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(this.excelFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Workbook book = null;
		try {
			WorkbookSettings setting = new WorkbookSettings();
			setting.setSuppressWarnings(true);
			book = Workbook.getWorkbook(is, setting);
			is.close();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book;
	}

	int getAbsoluteRow(int relativeIndex) {
		return this.impSetting.getStartRow() + relativeIndex;
	}

	int getAbsoluteColumn(int relativeIndex) {
		return this.impSetting.getStartCol() + relativeIndex;
	}

	int getMaxCols(Sheet sheet) {
		int limit = this.impSetting.getLimit();
		int maxCols = sheet.getColumns() - this.impSetting.getStartCol();
		if (limit == 0 || limit > maxCols)
			limit = maxCols;
		return limit;
	}

	Sheet getSheet(int rowIdx) {
		Sheet sheet = this.curSheet;
		int rows = sheet.getRows();
		if (rowIdx >= rows) {
			this.sheetIdx++;
			if (this.sheetIdx >= this.sheets.length)
				return null;
			sheet = this.sheets[this.sheetIdx];
			this.curSheet = sheet;
			this.rowIdx = this.impSetting.getStartRow();
		}
		return sheet;
	}

	Object getNextRow() {
		Sheet sheet = null;
		String[] data = null;

		int row, limit;

		sheet = this.getSheet(this.rowIdx);
		if (sheet == null) {
			this.hasNext = false;
			return sheet;
		}
		limit = this.getMaxCols(sheet);
		row = this.rowIdx;

		data = this.extractRow(sheet, row, limit);
		row++;
		this.rowIdx = row;
		return data;
	}

	String[] extractRow(Sheet sheet, int rowIdx, int limit) {
		String data[] = new String[limit];
		int row = this.getAbsoluteRow(rowIdx);
		int col = 0;
		for (int i = 0; i < limit; i++) {
			col = this.getAbsoluteColumn(i);
			data[i] = StringUtils.trim(sheet.getCell(col, row).getContents());
		}
		return data;
	}

	@Override
	public synchronized Object read() {
		if (this.hasNext)
			return this.getNextRow();
		return null;
	}

	@Override
	public synchronized List<Object> read(int size) {
		List<Object> list = null;
		if (this.hasNext) {
			list = new ArrayList<Object>(size);
			list.add(this.getNextRow());
			int c = 1;
			while (this.hasNext && c < size) {
				list.add(this.getNextRow());
				c++;
			}
		}
		return list;
	}

	@Override
	public void close() throws IOException {
		this.book.close();
	}
}
