package com.sparrow.data.tools.exports.writer.excel;

import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

/**
 * Created by yuanzc on 2016/3/14.
 */
public abstract class CellFormatTool {

    public static WritableCellFormat getCellFormat() {
        return getCellFormat(false);
    }

    /**
     * 单元格样式的设定
     *
     * @return 样式设置对象
     * @author YZC
     */
    public static WritableCellFormat getHeaderCellFormat() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE);
        WritableCellFormat bodyFormat = new WritableCellFormat(font);
        try {
            // 设置单元格背景色：表体为白色
            bodyFormat.setBackground(Colour.WHITE);
            // 设置表头表格边框样式
            // 整个表格线为细线、黑色
            bodyFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            // 把水平对齐方式指定为居中
            bodyFormat.setAlignment(jxl.format.Alignment.CENTRE);
        } catch (WriteException e) {
            System.out.println("表体单元格样式设置失败！");
        }
        return bodyFormat;
    }

    /**
     * 单元格样式的设定
     *
     * @return 样式设置对象
     * @author YZC
     */
    public static WritableCellFormat getCellFormat(boolean isBold, boolean wrapped) {
        /*
         * WritableFont.createFont("宋体")：设置字体为宋体 10：设置字体大小
		 * WritableFont.NO_BOLD:设置字体非加粗（BOLD：加粗 NO_BOLD：不加粗） false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10,
                isBold ? WritableFont.BOLD : WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);
        WritableCellFormat bodyFormat = new WritableCellFormat(font);
        try {
            // 设置单元格背景色：表体为白色
            bodyFormat.setBackground(Colour.WHITE);
            // 设置表头表格边框样式
            // 整个表格线为细线、黑色
            bodyFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            // 设置自动换行
            if (wrapped)
                bodyFormat.setWrap(true);
            // 把水平对齐方式指定为居中
            // bodyFormat.setAlignment(jxl.format.Alignment.CENTRE);
            // 把垂直对齐方式指定为居中
            // bodyFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
        } catch (WriteException e) {
            System.out.println("表体单元格样式设置失败！");
        }
        return bodyFormat;
    }

    /**
     * 单元格样式的设定
     *
     * @return 样式设置对象
     * @author YZC
     */
    public static WritableCellFormat getCellFormat(boolean isBold) {
        return getCellFormat(isBold, false);
    }
}
