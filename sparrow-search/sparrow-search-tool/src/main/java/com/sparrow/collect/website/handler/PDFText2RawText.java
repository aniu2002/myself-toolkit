package com.sparrow.collect.website.handler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.IOException;

/**
 * Created by yuanzc on 2016/3/24.
 */
public class PDFText2RawText extends PDFTextStripper {
    private boolean onFirstPage = true;

    /**
     * Constructor.
     *
     * @param encoding The encoding to be used
     * @throws java.io.IOException If there is an error during initialization.
     */
    public PDFText2RawText(String encoding) throws IOException {
        super(encoding);
        setLineSeparator(systemLineSeparator);
       // setParagraphStart("-");
       // setParagraphEnd(systemLineSeparator);
       setWordSeparator(" ");
       // setPageStart("--");
        //setPageEnd(systemLineSeparator);
       // setArticleStart(systemLineSeparator);
      //  setArticleEnd(systemLineSeparator);
    }

    protected void writeHeader() throws IOException {

    }

    /**
     * {@inheritDoc}
     */
    protected void writePage() throws IOException {
        if (onFirstPage) {
            writeHeader();
            onFirstPage = false;
        }
        super.writePage();
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument(PDDocument pdf) throws IOException {
    }

    protected void startArticle(boolean isltr) throws IOException {
        if (isltr) {
            super.writeString("@");
        } else {
            super.writeString("@@@");
        }
    }

    protected void endArticle() throws IOException {
        super.endArticle();
        super.writeString(systemLineSeparator);
    }

    protected void writeString(String chars) throws IOException {
        super.writeString(escape(chars));
    }

    protected String escape(String chars) {
        return chars;
    }
}
