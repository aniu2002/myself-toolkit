package com.sparrow.data.tools.exports.writer.excel;

import com.sparrow.data.tools.exports.ExpSetting;
import com.sparrow.data.tools.exports.writer.AbstractDataWriter;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 抽象excel2007读入器，先构建.xlsx一张模板，改写模板中的sheet.xml,使用这种方法<br/>
 * 写入.xlsx文件，不需要太大的内存
 *
 * @author YZC
 * @version 1.0 (2014-3-25)
 * @modify
 */
public class Excel2007DataWriter extends AbstractDataWriter {
    private final File excelFile;
    private SpreadsheetWriter sw;
    private Writer tempWriter;
    private File tempFile;
    private String sheetTemplate;
    private String sheetRef;
    private volatile int records;

    public Excel2007DataWriter(String excelFile) {
        this(new File(excelFile));
    }

    public Excel2007DataWriter(File excelFile) {
        this.excelFile = excelFile;
    }

    @Override
    public void setExpSetting(ExpSetting expSetting) {

    }

    @Override
    protected void doOpen() throws IOException {
        String uuid = UUID.randomUUID().toString();
        this.sheetTemplate = this.getTempFile(uuid);
        this.sheetRef = this.createTempFile(this.sheetTemplate);
        // 生成xml文件
        this.tempFile = File.createTempFile("sheet-" + uuid, ".xml");
        this.tempWriter = new FileWriter(this.tempFile);
        this.sw = new SpreadsheetWriter(this.tempWriter);
        // 开始写入表格
        this.beginSheet();
    }

    @Override
    public void close() throws IOException {
        // 结束电子表格
        this.endSheet();

        this.tempWriter.close();
        // 使用产生的数据替换模板
        File templateFile = new File(this.sheetTemplate);
        FileOutputStream out = new FileOutputStream(this.excelFile);
        substitute(templateFile, this.tempFile, this.sheetRef.substring(1), out);
        out.close();
        // 删除文件之前调用一下垃圾回收器，否则无法删除模板文件
        System.gc();
        // 删除临时模板文件
        if (templateFile.isFile() && templateFile.exists()) {
            templateFile.delete();
        }
    }

    /**
     * 获得临时文件名称
     *
     * @return 临时文件名称
     * @author YZC
     */
    String getTempFile(String uuid) {
        return System.getProperty("user.home") + "\\template-"
                + UUID.randomUUID().toString() + ".xlsx";
    }

    @Override
    protected void writeHeader(String[] headers) throws IOException {
        this.writeRow(headers);
    }

    /**
     * 创建临时的工作薄和电子表格
     *
     * @author YZC
     */
    String createTempFile(String tempFile) {
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        // 持有电子表格数据的xml文件名 例如 /xl/worksheets/sheet1.xml
        String sheetRef = sheet.getPackagePart().getPartName().getName();
        try {
            // 保存模板
            FileOutputStream os = new FileOutputStream(tempFile);
            wb.write(os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sheetRef;
    }

    @Override
    public void writeRow(String columns[]) throws IOException {
        // 插入新行
        this.insertRow(this.records);
        // 建立新单元格,索引值从0开始,表示第一列
        for (int i = 0; i < columns.length; i++) {
            this.createCell(i, columns[i]);
        }
        // 结束行
        this.endRow();
        this.records++;
    }

    void beginSheet() throws IOException {
        sw.beginSheet();
    }

    void insertRow(int rowNum) throws IOException {
        sw.insertRow(rowNum);
    }

    void createCell(int columnIndex, String value) throws IOException {
        sw.createCell(columnIndex, value, -1);
    }

    void createCell(int columnIndex, double value) throws IOException {
        sw.createCell(columnIndex, value, -1);
    }

    void endRow() throws IOException {
        sw.endRow();
    }

    public void endSheet() throws IOException {
        sw.endSheet();
    }

    /**
     * @param zipfile the template file
     * @param tmpfile the XML file with the sheet data
     * @param entry   the name of the sheet entry to substitute, e.g.
     *                xl/worksheets/sheet1.xml
     * @param out     the stream to write the result to
     */
    static void substitute(File zipfile, File tmpfile, String entry,
                           OutputStream out) throws IOException {
        ZipFile zip = new ZipFile(zipfile);
        ZipOutputStream zos = new ZipOutputStream(out);

        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry ze = en.nextElement();
            if (!ze.getName().equals(entry)) {
                zos.putNextEntry(new ZipEntry(ze.getName()));
                InputStream is = zip.getInputStream(ze);
                copyStream(is, zos);
                is.close();
            }
        }
        zos.putNextEntry(new ZipEntry(entry));
        InputStream is = new FileInputStream(tmpfile);
        copyStream(is, zos);
        is.close();
        zos.close();
    }

    static void substitute(File tmpfile, File targetFile, String entry)
            throws IOException {
        OutputStream out = new FileOutputStream(targetFile);
        ZipOutputStream zos = new ZipOutputStream(out);
        zos.putNextEntry(new ZipEntry(entry));
        InputStream is = new FileInputStream(tmpfile);
        copyStream(is, zos);
        is.close();
        zos.close();
    }

    private static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] chunk = new byte[1024];
        int count;
        while ((count = in.read(chunk)) > 0) {
            out.write(chunk, 0, count);
        }
    }

}
