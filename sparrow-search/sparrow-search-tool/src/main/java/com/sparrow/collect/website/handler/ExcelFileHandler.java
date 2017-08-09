package com.sparrow.collect.website.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelFileHandler extends FileHandler {
    // 设置Cell之间以空格分割
    private static String EXCEL_LINE_DELIMITER = " ";


    private HSSFWorkbook createWorkbook(File file) {
        // 创建HSSFWorkbook读取
        try {
            InputStream is = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            return workbook;
        } catch (Exception e) {
            System.out.println(file.getPath() + ",文件格式有误,加载文件出现异常");
            return null;
        }
    }

    private void readSheet(HSSFSheet sheet, StringBuilder sb)
            throws IOException {
        // 如果是XLS文件则通过POI提供的API读取文件
        // 判断当前行是否到但前Sheet的结尾
        int currPosition = sheet.getFirstRowNum(), rows = sheet.getLastRowNum() + 1;
        while (currPosition < rows) {
            readLine(sheet, currPosition, sb);
            sb.append(LINE_SEPARATOR);
            currPosition++;
        }
    }

    private void readLine(HSSFSheet sheet, int row, StringBuilder sb) {
        // 根据行数取得Sheet的一行
        HSSFRow rowline = sheet.getRow(row);
        if (rowline != null) {
            // 获取当前行的列数
            int filledColumns = rowline.getLastCellNum();
            HSSFCell cell = null;
            // 循环遍历所有列
            for (int i = 0; i < filledColumns; i++) {
                // 取得当前Cell
                cell = rowline.getCell(i);
                String cellvalue = null;
                if (cell != null) {
                    // 判断当前Cell的Type
                    switch (cell.getCellType()) {
                        // 如果当前Cell的Type为NUMERIC
                        case HSSFCell.CELL_TYPE_NUMERIC: {
                            // 判断当前的cell是否为Date
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                // 如果是Date类型则，取得该Cell的Date值
                                Date date = cell.getDateCellValue();
                                // 把Date转换成本地格式的字符串
                                cellvalue = date.toString();
                            }
                            // 如果是纯数字
                            else {
                                // 取得当前Cell的数值
                                Integer num = new Integer((int) cell
                                        .getNumericCellValue());
                                cellvalue = String.valueOf(num);
                            }
                            break;
                        }
                        // 如果当前Cell的Type为STRIN
                        case HSSFCell.CELL_TYPE_STRING:
                            // 取得当前的Cell字符串
                            cellvalue = cell.getStringCellValue().replaceAll("'",
                                    "''");
                            break;
                        // 默认的Cell值
                        default:
                            cellvalue = " ";
                    }
                } else {
                    cellvalue = "";
                }
                // 在每个字段之间插入分割符
                sb.append(this.formatString(cellvalue)).append(EXCEL_LINE_DELIMITER);
            }
        }
    }

    @Override
    protected String getContent(File file) {
        HSSFWorkbook workbook = createWorkbook(file);
        if (workbook == null)
            return null;
        int numOfSheets = workbook.getNumberOfSheets();
        int pos = 0;
        StringBuilder sb = new StringBuilder();
        try {
            while (pos < numOfSheets) {
                readSheet(workbook.getSheetAt(pos), sb);
                pos++;
            }
        } catch (IOException e) {
            System.out.println(file.getPath() + ",文件格式有误,加载文件出现异常;"
                    + e.getMessage());
            return null;
        }
        return sb.toString();
    }
}
