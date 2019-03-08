package com.sparrow.collect.index.searcher.write;

import com.sparrow.collect.index.Constants;
import com.sparrow.collect.index.config.FieldSetting;
import com.sparrow.collect.index.searcher.PageResult;
import com.sparrow.collect.index.searcher.ResultWriter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.List;

/**
 * author: Yzc
 * - Date: 2019/3/6 19:51
 */

public abstract class PageResultWriter implements ResultWriter {
    private boolean firstRow = true;
    private boolean beginWrite = false;
    private boolean tailWrite = false;

    protected void writeBodyHeader() {
        if (!this.beginWrite) {
            this.writeChar(Constants.JSON_BEGIN);
            this.writeString(Constants.ROWS_FIELD);
            this.writeChar(Constants.JSON_COLON);
            this.writeChar(Constants.OPEN_BRACKET);
            this.beginWrite = true;
        }
    }

    protected void writeBodyTail() {
        this.writeChar(Constants.CLOSE_BRACKET);
        this.writeChar(Constants.JSON_END);
    }

    protected void writeRowHeader() {
        this.writeChar(Constants.JSON_BEGIN);
    }

    protected void writeRowTail() {
        this.writeChar(Constants.JSON_END);
    }

    protected void writeFieldBreakUp() {
        this.writeChar(Constants.JSON_COMMA);
    }

    @Override
    public final void writePageHeader(PageResult page) {
        if (page == null) {
            return;
        }
        if (!this.beginWrite) {
            this.writeLongField(Constants.TOTAL_FIELD, page.getTotal());
            this.writeFieldBreakUp();
            this.writeIntField(Constants.PAGE_FIELD, page.getPage());
            this.writeFieldBreakUp();
            this.writeIntField(Constants.SIZE_FIELD, page.getSize());
            this.writeFieldBreakUp();
            this.writeString(Constants.ROWS_FIELD);
            this.writeChar(Constants.JSON_COLON);
            this.writeChar(Constants.OPEN_BRACKET);
            this.beginWrite = true;
        }
    }

    private void begin() {
        this.writeBodyHeader();
    }

    private void end() {
        this.writeBodyTail();
        this.tailWrite = true;
    }

    @Override
    public final void writeRow(Document document) {
        if (!this.beginWrite) {
            this.begin();
        }
        if (this.firstRow) {
            this.firstRow = false;
        } else {
            this.writeFieldBreakUp();
        }
        this.writeRowHeader();
        this.writeRowData(document, this.getFields());
        this.writeRowTail();
    }

    private void writeRowData(Document document, List<FieldSetting> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        boolean firstMark = true;
        for (FieldSetting fieldSetting : fields) {
            if (firstMark) {
                firstMark = false;
            } else {
                this.writeFieldBreakUp();
            }
            this.writeField(document, fieldSetting);
        }
    }

    private void writeField(Document document, FieldSetting field) {
        switch (field.getType()) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                this.writeNumberField(field.getName(), document.get(field.getName()));
                break;
            case STRING:
            case KEYWORD:
            case TEXT:
                this.writeStringField(field.getName(), document.get(field.getName()));
                break;
            case NONE:
            default:
                this.writeStringField(field.getName(), null);
        }
    }

    @Override
    public final void close() throws IOException {
        if (!this.tailWrite) {
            this.end();
        }
        this.doClose();
    }

    private void writeIntField(String key, int number) {
        this.writeKeyValue(key, String.valueOf(number), false);
    }

    private void writeLongField(String key, long number) {
        this.writeKeyValue(key, String.valueOf(number), false);
    }

    private void writeNumberField(String key, String number) {
        this.writeKeyValue(key, number, false);
    }

    private void writeStringField(String key, String string) {
        this.writeKeyValue(key, string, true);
    }

    private void writeKeyValue(String key, String value, boolean hasQuot) {
        this.writeChar(Constants.JSON_QUOT);
        this.writeString(key);
        this.writeChar(Constants.JSON_QUOT);
        this.writeChar(Constants.JSON_COLON);
        if (value == null) {
            this.writeString(Constants.NULL);
        } else {
            this.writeChar(Constants.JSON_QUOT);
            this.writeString(value);
            this.writeChar(Constants.JSON_QUOT);
        }
    }

    protected abstract List<FieldSetting> getFields();

    protected abstract void writeString(String string);

    protected abstract void writeChar(char c);

    protected abstract void doClose();

}
