package com.sparrow.data.tools.exports.writer;

import java.io.IOException;

import com.sparrow.data.tools.exports.ExpSetting;

public interface DataWriter {
    String EMPTY_VAL = "";

    void setExpSetting(ExpSetting expSetting);

    void open() throws IOException;

    void setHeaders(String columns[]);

    void writeRow(String columns[]) throws IOException;

    void close() throws IOException;
}
