package com.sparrow.data.tools.exports.writer.excel;

import com.sparrow.data.tools.exports.ExpSetting;
import com.sparrow.data.tools.exports.writer.AbstractDataWriter;
import com.sparrow.data.tools.exports.writer.DataWriter;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;

public class ExcelDataWriter extends AbstractDataWriter {
    private final File excelFile;
    private WritableWorkbook workBook;
    private WritableSheet writableSheet;
    private WritableCellFormat cellFormat;
    private volatile int currentIdx;
    private int sheetIdx;
    private int exportMaxRows = ExpSetting.EXPORT_MAX_ROWS;
    private int sheetMaxRows = ExpSetting.SHEET_MAX_ROWS;

    public ExcelDataWriter(String excelFile) {
        this(new File(excelFile));
    }

    public ExcelDataWriter(File excelFile) {
        this.excelFile = excelFile;
    }

    @Override
    public void setExpSetting(ExpSetting expSetting) {
        this.exportMaxRows = expSetting.getExportMax();
        this.sheetMaxRows = expSetting.getSheetRows();
    }

    @Override
    protected void doOpen() throws IOException {
        if (this.workBook != null)
            return;
        this.workBook = Workbook.createWorkbook(this.excelFile);
        this.writableSheet = this.createWritableSheet();
        this.cellFormat = CellFormatTool.getCellFormat();
    }

    WritableSheet createWritableSheet() throws IOException {
        int i = this.sheetIdx + 1;
        // 创建Excel工作表 指定名称和位置
        WritableSheet writableSheet = this.workBook.createSheet("Sheet-" + i,
                this.sheetIdx);
        this.sheetIdx = i;
        return writableSheet;
    }

    protected void writeHeader(String headers[]) throws IOException {
        if (headers != null && headers.length > 0) {
            CellView cellView = new CellView();
            cellView.setAutosize(true); //设置自动大小
            for (int i = 0; i < headers.length; i++) {
                this.writableSheet.setColumnView(i, cellView);
            }
        }
        int row = this.currentIdx;
        try {
            this.doWriteRow(this.writableSheet, headers, row, CellFormatTool.getHeaderCellFormat());
            this.currentIdx = row + 1;
            this.exportMaxRows--;
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
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
                this.writableSheet = writableSheet = this.createWritableSheet();
                this.currentIdx = row = 0;
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
            throws IOException, WriteException {
        this.doWriteRow(writableSheet, columns, row, this.cellFormat);
    }

    void doWriteRow(WritableSheet writableSheet, String[] columns, int row, WritableCellFormat cellFormat)
            throws IOException, WriteException {
        for (int i = 0; i < columns.length; i++) {
            String str = columns[i];
            if (str == null)
                str = EMPTY_VAL;
            // 1.添加Label对象
            writableSheet.addCell(new Label(i, row, str, cellFormat));
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

    public static void main(String args[]) {
        DataWriter dataWriter = new ExcelDataWriter(new File("F:/test.xls"));
        try {
            dataWriter.open();
            dataWriter.writeRow(new String[]{"1", "1", "1", "1"});
            dataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
