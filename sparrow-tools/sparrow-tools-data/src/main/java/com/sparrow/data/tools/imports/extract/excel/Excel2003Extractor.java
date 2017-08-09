package com.sparrow.data.tools.imports.extract.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractCallback;

/**
 * 
 * 针对2003excel的处理，基于事件的方式一行行的读取处理，解决内存小的问题。但无法从哪个sheet定位
 * 抽象Excel2003读取器，通过实现HSSFListener监听器，采用事件驱动模式解析excel2003
 * 中的内容，遇到特定事件才会触发，大大减少了内存的使用。
 * 
 * @author YZC
 * @version 1.0 (2014-3-25)
 * @modify
 */
public class Excel2003Extractor implements HSSFListener, DataExtractor {
	private final File excelFile;
	private FormatTrackingHSSFListener formatListener;
	private SSTRecord sstRecord;
	private List<String> rowlist = new ArrayList<String>();;
	private ExtractCallback callback;
	private int minColumns = -1;
	private int lastRowNumber;
	private int lastColumnNumber;
	private int nextRow;
	private int nextColumn;
	private boolean outputNextStringRecord;

	public Excel2003Extractor(File excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public void setImpSetting(ImpSetting impSetting) {

	}

	/**
	 * 参照父类说明 ----遍历excel下所有的sheet
	 * 
	 * @see com.sparrow.data.tools.imports.extract.DataExtractor#extract()
	 */
	@Override
	public void extract() throws SQLException {
		try {
			InputStream ins = new FileInputStream(this.excelFile);
			POIFSFileSystem fs = new POIFSFileSystem(ins);
			MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(
					this);
			this.formatListener = new FormatTrackingHSSFListener(listener);
			HSSFEventFactory factory = new HSSFEventFactory();
			HSSFRequest request = new HSSFRequest();
			request.addListenerForAllRecords(this.formatListener);
			factory.processWorkbookEvents(request, fs);
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setExtractCallback(ExtractCallback callback) {
		this.callback = callback;
	}

	/**
	 * HSSFListener 监听方法，处理 Record
	 */
	@Override
	public void processRecord(Record record) {
		int thisRow = -1;
		int thisColumn = -1;
		String thisStr = null;
		String value = null;
		switch (record.getSid()) {
		case BoundSheetRecord.sid:
			// sheet工作薄开始
			break;
		case BOFRecord.sid:
			// BOFRecord br = (BOFRecord) record;
			// if (br.getType() == BOFRecord.TYPE_WORKSHEET)
			// sheetIndex++;
			break;
		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;
		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;
			thisRow = brec.getRow();
			thisColumn = brec.getColumn();
			thisStr = "";
			rowlist.add(thisColumn, thisStr);
			break;
		case BoolErrRecord.sid: // 单元格为布尔类型
			BoolErrRecord berec = (BoolErrRecord) record;
			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			thisStr = berec.getBooleanValue() + "";
			rowlist.add(thisColumn, thisStr);
			break;
		case FormulaRecord.sid: // 单元格为公式类型
			FormulaRecord frec = (FormulaRecord) record;
			thisRow = frec.getRow();
			thisColumn = frec.getColumn();
			if (Double.isNaN(frec.getValue())) {
				outputNextStringRecord = true;
				nextRow = frec.getRow();
				nextColumn = frec.getColumn();
			} else {
				thisStr = formatListener.formatNumberDateCell(frec);
			}
			rowlist.add(thisColumn, thisStr);
			break;
		case StringRecord.sid:// 单元格中公式的字符串
			if (outputNextStringRecord) {
				StringRecord srec = (StringRecord) record;
				thisStr = srec.getString();
				thisRow = nextRow;
				thisColumn = nextColumn;
				outputNextStringRecord = false;
			}
			break;
		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;
			thisColumn = lrec.getColumn();
			value = lrec.getValue().trim();
			value = value.equals("") ? " " : value;
			this.rowlist.add(thisColumn, value);
			break;
		case LabelSSTRecord.sid: // 单元格为字符串类型
			LabelSSTRecord lsrec = (LabelSSTRecord) record;
			thisColumn = lsrec.getColumn();
			if (sstRecord == null) {
				rowlist.add(thisColumn, " ");
			} else {
				value = sstRecord.getString(lsrec.getSSTIndex()).toString()
						.trim();
				value = value.equals("") ? " " : value;
				rowlist.add(thisColumn, value);
			}
			break;
		case NumberRecord.sid: // 单元格为数字类型
			NumberRecord numrec = (NumberRecord) record;
			thisColumn = numrec.getColumn();
			value = formatListener.formatNumberDateCell(numrec).trim();
			value = value.equals("") ? " " : value;
			// 向容器加入列值
			rowlist.add(thisColumn, value);
			break;
		default:
			break;
		}
		// 遇到新行的操作
		if (thisRow != -1 && thisRow != lastRowNumber)
			lastColumnNumber = -1;
		// 空值的操作
		if (record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
			thisColumn = mc.getColumn();
			rowlist.add(thisColumn, " ");
		}
		// 更新行和列的值
		if (thisRow > -1)
			lastRowNumber = thisRow;
		if (thisColumn > -1)
			lastColumnNumber = thisColumn;
		// 行结束时的操作
		if (record instanceof LastCellOfRowDummyRecord) {
			if (minColumns > 0) {
				// 列值重新置空
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
			}
			lastColumnNumber = -1;
			// 每行结束时， 调用getRows() 方法
			try {
				this.callback
						.handle(rowlist.toArray(new String[rowlist.size()]), 1,
								thisRow);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 清空容器
			rowlist.clear();
		}
	}
}
