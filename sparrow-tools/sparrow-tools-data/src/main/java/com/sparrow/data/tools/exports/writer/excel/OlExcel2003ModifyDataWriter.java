package com.sparrow.data.tools.exports.writer.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.sparrow.data.tools.exports.ExpSetting;
import com.sparrow.data.tools.exports.writer.AbstractDataWriter;

/**
 * 根据模板创建excel文件导出，指定数据填充的位置，包括：startSheet，startRow，startCol，columnLimit<br/>
 * 1】从模板dao获取模板excel文件<br/>
 * 2】获取workbook <br/>
 * 3】创建workbook，并导入模板的sheet <br/>
 * 4】如果数据超过MAX_ROWS，根据设置startSheet,copy到新的sheet中去
 *
 * @author YZC
 * @version 1.0 (2014-3-29)
 * @modify
 */
public class OlExcel2003ModifyDataWriter extends AbstractDataWriter {
    private final File templateFile;
    private final File excelFile;
    private WritableWorkbook workBook;
    private WritableSheet writableSheet;
    private WritableCellFormat cellFormat;
    private String sheetName = "数据导出";
    private int origSheets;
    private volatile int currentIdx;
    private int sheetIdx;
    private int startSheet;
    private int startRow;
    private int insertNo;
    private int exportMaxRows = ExpSetting.EXPORT_MAX_ROWS;
    private int sheetMaxRows = ExpSetting.SHEET_MAX_ROWS;

    public OlExcel2003ModifyDataWriter(String template, String excelFile) {
        this(new File(template), new File(excelFile));
    }

    public OlExcel2003ModifyDataWriter(File templateFile, File excelFile) {
        this.templateFile = templateFile;
        this.excelFile = excelFile;
    }

    @Override
    public void setExpSetting(ExpSetting expSetting) {
        this.sheetIdx = this.startSheet = expSetting.getStartSheet();
        this.startRow = expSetting.getStartSheet();
        this.exportMaxRows = expSetting.getExportMax();
        this.sheetMaxRows = expSetting.getSheetRows();
    }

    @Override
    protected void doOpen() throws IOException {
        if (this.workBook != null)
            return;
        try {
            WorkbookSettings newSettings = new WorkbookSettings();
            newSettings.setPropertySets(true);
            WorkbookSettings setting = new WorkbookSettings();
            setting.setSuppressWarnings(true);
            InputStream ins = new FileInputStream(this.templateFile);
            Workbook baseBook = Workbook.getWorkbook(ins, setting);
            ins.close();

            this.origSheets = baseBook.getSheets().length;
            // 打开一个文件的副本，并且指定数据写回到原文件
            this.workBook = Workbook
                    .createWorkbook(this.excelFile, newSettings);
            // this.workBook .copySheet("", "", this.sheetIdx);
            this.importSheet(baseBook, this.workBook);
            this.writableSheet = this.getNextWritableSheet();
            this.cellFormat = CellFormatTool.getCellFormat();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从模板的baseWork导入sheet到新的workbook中
     *
     * @param baseBook 模板
     * @param workBook 新的excel的工作薄
     * @author YZC
     */
    void importSheet(Workbook baseBook, WritableWorkbook workBook) {
        int numberOfSheets = baseBook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = baseBook.getSheet(i);
            if (i == this.startSheet)
                this.sheetName = sheet.getName();
            workBook.importSheet(sheet.getName(), i, sheet);
        }
        baseBook.close();
    }

    /**
     * 获取或者创建下一个sheet
     *
     * @return sheet
     * @throws IOException
     * @author YZC
     */
    WritableSheet getNextWritableSheet() throws IOException {
        WritableSheet writableSheet = null;
        int i = this.sheetIdx + 1;
        if (this.sheetIdx < this.origSheets)
            writableSheet = this.workBook.getSheet(this.sheetIdx);
        else if (this.startSheet >= this.origSheets) {
            this.insertNo++;
            String name = this.sheetName + "-" + this.insertNo;
            // 创建Excel工作表 指定名称和位置
            writableSheet = this.workBook.createSheet(name, this.sheetIdx);
        } else {
            this.insertNo++;
            String name = this.sheetName + "-" + this.insertNo;
            this.workBook.copySheet(this.startSheet, name, this.sheetIdx);
            writableSheet = this.workBook.getSheet(this.sheetIdx);
        }
        this.sheetIdx = i;
        return writableSheet;
    }

    @Override
    public synchronized void writeRow(String[] columns) throws IOException {
        if (this.exportMaxRows <= 0)
            throw new RuntimeException("不能写入更多的数据了，已经超过阀值");
        WritableSheet writableSheet = this.writableSheet;
        int row = this.currentIdx;
        try {
            // 创建新的sheet
            if (row >= this.sheetMaxRows) {
                this.writableSheet = writableSheet = this
                        .getNextWritableSheet();
                this.currentIdx = row = this.startRow;
            }
            this.doWriteRow(writableSheet, columns, row);
            this.currentIdx = row + 1;
            this.exportMaxRows--;
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    void doWriteRow(WritableSheet writableSheet, String[] columns, int row)
            throws IOException, RowsExceededException, WriteException {
        WritableCellFormat cellFormat = this.cellFormat;
        for (int i = 0; i < columns.length; i++) {
            // 1.添加Label对象
            writableSheet.addCell(new Label(i, row, columns[i], cellFormat));
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.workBook.write();
            this.workBook.close();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }
}
