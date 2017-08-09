package com.sparrow.data.tools.imports.extract.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractCallback;

public class ExcelExtractor implements DataExtractor {
    private final File excelFile;
    private int startRow = 0;
    private int startCol = 0;
    private int startSheet;
    private int maxRows = -1;
    private int colLimit = -1;

    private ExtractCallback callback;

    public ExcelExtractor(File excelFile) {
        this.excelFile = excelFile;
    }

    @Override
    public void setImpSetting(ImpSetting impSetting) {
        this.startRow = impSetting.getStartRow();
        this.startSheet = impSetting.getStartSheet();
        if (impSetting.getMaxRows() > 0) this.maxRows = impSetting.getMaxRows();
        if (impSetting.getLimit() > 0) this.colLimit = impSetting.getLimit();
        if (impSetting.getStartCol() > 0) this.startCol = impSetting.getStartCol();
    }

    int getAbsoluteRow(int relativeIndex) {
        return this.startRow + relativeIndex;
    }

    int getAbsoluteColumn(int relativeIndex) {
        return this.startCol + relativeIndex;
    }

    @Override
    public void setExtractCallback(ExtractCallback callback) {
        this.callback = callback;
    }

    @Override
    public void extract() throws SQLException {
        // 从输入流创建Workbook
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(this.excelFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Workbook book = null;
        try {
            WorkbookSettings setting = new WorkbookSettings();
            setting.setSuppressWarnings(true);
            book = Workbook.getWorkbook(is, setting);
            is.close();
        } catch (BiffException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Sheet[] sheets = book.getSheets();
        int i = this.startSheet;
        for (; i < sheets.length; i++) {
            this.extractSheet(sheets[i], i);
        }
        if (book != null) {
            book.close();
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    void extractSheet(Sheet sheet, int idx) throws SQLException {
        int rows = sheet.getRows() - this.startRow;
        int cols = sheet.getColumns();

        if (this.maxRows > 0 && this.maxRows < rows)
            rows = this.maxRows;

        if (this.colLimit > 0 && this.colLimit < cols)
            cols = this.colLimit;

        String data[];
        for (int i = 0; i < rows; i++) {
            data = this.extractRow(sheet, i, cols);
            if (this.isEmpty(data))
                break;
            this.callback.handle(data, idx, i);
        }
    }

    String[] extractRow(Sheet sheet, int rowIdx, int limit) {
        String data[] = new String[limit];
        int row = this.getAbsoluteRow(rowIdx);
        int col = 0;
        for (int i = 0; i < limit; i++) {
            col = this.getAbsoluteColumn(i);
            data[i] = this.trim(sheet.getCell(col, row).getContents());
        }
        return data;
    }

    String trim(String t) {
        return StringUtils.trim(t);
    }

    boolean isEmpty(String[] data) {
        if (data.length > 0 && StringUtils.isEmpty(data[0]))
            return true;
        return false;
    }

}
