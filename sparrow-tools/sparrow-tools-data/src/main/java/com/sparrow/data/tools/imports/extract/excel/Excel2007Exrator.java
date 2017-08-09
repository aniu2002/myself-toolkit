package com.sparrow.data.tools.imports.extract.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractCallback;

/**
 * 
 * 抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析 xml，<br/>
 * 需要继承DefaultHandler，在遇到文件内容时，事件会触发，<br/>
 * 这种做法可以大大降低 内存的耗费，特别使用于大数据量的文件。
 * 
 * @author YZC
 * @version 1.0 (2014-3-25)
 * @modify
 */
public class Excel2007Exrator extends DefaultHandler implements DataExtractor {
	private final File excelFile;
	private ExtractCallback callback;
	// 共享字符串表
	private SharedStringsTable sst;
	private List<String> rowlist = new ArrayList<String>();
	// 上一次的内容
	private String lastContents;
	private int startSheet;
	private boolean nextIsString;

	private int sheetIdx;
	private int rowIdx;
	// 当前列
	private int curCol = 0;
	// 日期标志
	private boolean dateFlag;
	// 数字标志
	private boolean numberFlag;
	private boolean isTElement;

	public Excel2007Exrator(File excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public void setImpSetting(ImpSetting impSetting) {
		this.startSheet = impSetting.getStartSheet();
	}

	/**
	 * 
	 * 参照父类说明 - 遍历工作簿中所有的电子表格
	 * 
	 * @see com.sparrow.data.tools.imports.extract.DataExtractor#extract()
	 */
	@Override
	public void extract() throws SQLException {
		OPCPackage pkg;
		try {
			pkg = OPCPackage.open(this.excelFile);
			XSSFReader r = new XSSFReader(pkg);
			SharedStringsTable sst = r.getSharedStringsTable();
			XMLReader parser = fetchSheetParser(sst);
			Iterator<InputStream> sheets = r.getSheetsData();
			int i = 0;
			while (sheets.hasNext()) {
				if (i < this.startSheet) {
					sheetIdx = i;
					i++;
					continue;
				}
				sheetIdx++;
				InputStream sheet = sheets.next();
				InputSource sheetSource = new InputSource(sheet);
				parser.parse(sheetSource);
				sheet.close();
				this.rowIdx = 0;
			}
			pkg.close();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setExtractCallback(ExtractCallback callback) {
		this.callback = callback;
	}

	/**
	 * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
	 * 
	 * @param filename
	 * @param sheetId
	 * @throws Exception
	 */
	void processOneSheet(String filename, int sheetId) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		// 根据 rId# 或 rSheet# 查找sheet
		InputStream sheet2 = r.getSheet("rId" + sheetId);
		InputSource sheetSource = new InputSource(sheet2);
		parser.parse(sheetSource);
		sheet2.close();
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst)
			throws SAXException {
		XMLReader parser = XMLReaderFactory
				.createXMLReader("org.apache.xerces.parsers.SAXParser");
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		// c => 单元格
		if ("c".equals(name)) {
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
			String cellType = attributes.getValue("t");
			if ("s".equals(cellType)) {
				nextIsString = true;
			} else {
				nextIsString = false;
			}
			// 日期格式
			String cellDateType = attributes.getValue("s");
			if ("1".equals(cellDateType)) {
				dateFlag = true;
			} else {
				dateFlag = false;
			}
			String cellNumberType = attributes.getValue("s");
			if ("2".equals(cellNumberType)) {
				numberFlag = true;
			} else {
				numberFlag = false;
			}

		}
		// 当元素为t时
		if ("t".equals(name)) {
			isTElement = true;
		} else {
			isTElement = false;
		}

		// 置空
		lastContents = "";
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		try {
			// 根据SST的索引值的到单元格的真正要存储的字符串
			// 这时characters()方法可能会被调用多次
			if (nextIsString) {
				try {
					int idx = Integer.parseInt(lastContents);
					lastContents = new XSSFRichTextString(sst.getEntryAt(idx))
							.toString();
				} catch (Exception e) {

				}
			}
			// t元素也包含字符串
			if (isTElement) {
				String value = lastContents.trim();
				rowlist.add(curCol, value);
				curCol++;
				isTElement = false;
				// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
				// 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
			} else if ("v".equals(name)) {
				String value = lastContents.trim();
				value = value.equals("") ? " " : value;
				// 日期格式处理
				if (dateFlag) {
					Date date = HSSFDateUtil.getJavaDate(Double.valueOf(value));
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					value = dateFormat.format(date);
				}
				// 数字类型处理
				if (numberFlag) {
					BigDecimal bd = new BigDecimal(value);
					value = bd.setScale(3, BigDecimal.ROUND_UP).toString();
				}
				rowlist.add(curCol, value);
				curCol++;
			} else {
				this.rowIdx++;
				// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
				if (name.equals("row")) {
					this.callback.handle(
							rowlist.toArray(new String[rowlist.size()]),
							this.sheetIdx, this.rowIdx);

					rowlist.clear();
					curCol = 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// 得到单元格内容的值
		lastContents += new String(ch, start, length);
	}
}
