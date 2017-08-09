package com.sparrow.collect.website.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PdfFileHandler extends FileHandler {

    protected boolean skipRow(String row) {
        if (StringUtils.isEmpty(row))
            return true;
        if (row.charAt(0) == '@')
            return true;
        int idx = row.indexOf(' ');
        if (idx == -1)
            return true;
        String firstToken = row.substring(0, idx);
        if(!this.isDit(firstToken))
            return true;
        return false;
    }

    @Override
    protected String getContent(File file) {
        PDDocument pdfdocument = null;
        try {
            pdfdocument = PDDocument.load(new FileInputStream(file));
        } catch (Exception e) {
            System.out.println(file.getPath() + ",文件太大或其他异常，系统加载出现错误！");
            return null;
        }
        // get page nums
        int totalPages = pdfdocument.getNumberOfPages();
        // judge whether pdf file is encrypted
        boolean bFlag = pdfdocument.isEncrypted();
        // String text = stripper.getText(pdfdocument.getDocument());
        // output.write(text);
        StringBuffer sb = new StringBuffer();
        String content = "";
        try {
            PDFTextStripper stripper = new PDFText2RawText("utf-8") {
                @Override
                protected String escape(String chars) {
                    return PdfFileHandler.this.formatString(chars);
                }

                protected void writeParagraphEnd() throws IOException {
                    super.writeParagraphEnd();
                }
            };
            if (bFlag) {
                stripper.setStartPage(0);
                stripper.setEndPage(totalPages);
                content = stripper.getText(pdfdocument);
//                stripper.writeText(pdfdocument, new StringWriter());
                sb.append(content);
            } else {
//                for (int i = 1; i < totalPages; i++) {
//                    stripper.setStartPage(i);
//                    stripper.setEndPage(i + 1);
//                    content = stripper.getText(pdfdocument);
//                    sb.append(content);
//                    if (i == 50)
//                        break;
//                }

                stripper.setStartPage(0);
                stripper.setEndPage(totalPages);
                content = stripper.getText(pdfdocument);
                sb.append(content);
            }

            if (pdfdocument != null)
                pdfdocument.close();
        } catch (Exception e) {
            System.out.println(file.getPath() + ",该pdf文件加密或其他异常，抽取文本失败！");
        }

        String contents = sb.toString();
        //contents = contents.replaceAll("\\s", "");
        if (contents.length() == 0 || contents.equals("")) {
            System.out.println(file.getPath() + ",文件格式异常，抽取文本失败！");
        }
        return contents;
    }

}
