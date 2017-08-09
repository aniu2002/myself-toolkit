package com.sparrow.data.tools.exports.writer.excel;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * 在写入器中写入电子表格
 * 
 * @author YZC
 * @version 1.0 (2014-3-25)
 * @modify
 */
public class SpreadsheetWriter {
	private final Writer _out;
	private int _rownum;
	private static String LINE_SEPARATOR = System.getProperty("line.separator");

	public SpreadsheetWriter(Writer out) {
		_out = out;
	}

	public void beginSheet() throws IOException {
		_out.write("<?xml version=\"1.0\" encoding=\"GB2312\"?>"
				+ "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
		_out.write("<sheetData>" + LINE_SEPARATOR);
	}

	public void endSheet() throws IOException {
		_out.write("</sheetData>");
		_out.write("</worksheet>");
	}

	/**
	 * 插入新行
	 * 
	 * @param rownum
	 *            以0开始
	 */
	public void insertRow(int rownum) throws IOException {
		_out.write("<row r=\"" + (rownum + 1) + "\">" + LINE_SEPARATOR);
		this._rownum = rownum;
	}

	/**
	 * 插入行结束标志
	 */
	public void endRow() throws IOException {
		_out.write("</row>" + LINE_SEPARATOR);
	}

	/**
	 * 插入新列
	 * 
	 * @param columnIndex
	 * @param value
	 * @param styleIndex
	 * @throws IOException
	 */
	public void createCell(int columnIndex, String value, int styleIndex)
			throws IOException {
		String ref = new CellReference(_rownum, columnIndex).formatAsString();
		_out.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
		if (styleIndex != -1)
			_out.write(" s=\"" + styleIndex + "\"");
		_out.write(">");
		_out.write("<is><t>" + XMLEncoder.encode(value) + "</t></is>");
		_out.write("</c>");
	}

	public void createCell(int columnIndex, String value) throws IOException {
		createCell(columnIndex, value, -1);
	}

	public void createCell(int columnIndex, double value, int styleIndex)
			throws IOException {
		String ref = new CellReference(_rownum, columnIndex).formatAsString();
		_out.write("<c r=\"" + ref + "\" t=\"n\"");
		if (styleIndex != -1)
			_out.write(" s=\"" + styleIndex + "\"");
		_out.write(">");
		_out.write("<v>" + value + "</v>");
		_out.write("</c>");
	}

	public void createCell(int columnIndex, double value) throws IOException {
		createCell(columnIndex, value, -1);
	}

	public void createCell(int columnIndex, Calendar value, int styleIndex)
			throws IOException {
		createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
	}
}
