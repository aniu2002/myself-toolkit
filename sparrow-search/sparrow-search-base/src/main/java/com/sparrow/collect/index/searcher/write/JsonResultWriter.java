package com.sparrow.collect.index.searcher.write;

import com.sparrow.collect.index.Constants;
import com.sparrow.collect.index.config.FieldSetting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

/**
 * author: Yzc
 * - Date: 2019/3/6 19:51
 */
@Slf4j
public class JsonResultWriter extends PageResultWriter {
    private List<FieldSetting> fieldSettings;
    private BufferedWriter bufferedWriter;

    public JsonResultWriter(List<FieldSetting> fieldSettings, Writer writer) {
        this.fieldSettings = fieldSettings;
        this.bufferedWriter = new BufferedWriter(writer);
    }

    public JsonResultWriter(List<FieldSetting> fieldSettings, OutputStream outputStream) {
        this.fieldSettings = fieldSettings;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, Constants.DEFAULT_CHARSET));
    }

    @Override
    protected List<FieldSetting> getFields() {
        return fieldSettings;
    }

    @Override
    protected void writeString(String string) {
        try {
            this.bufferedWriter.write(string);
        } catch (IOException e) {
            log.error("Write String IOException : ", e);
        }
    }

    @Override
    protected void writeChar(char c) {
        try {
            this.bufferedWriter.write(c);
        } catch (IOException e) {
            log.error("Write Char IOException : ", e);
        }
    }

    @Override
    protected void doClose() {
        IOUtils.closeQuietly(this.bufferedWriter);
        this.bufferedWriter = null;
        this.fieldSettings = null;
    }
}
