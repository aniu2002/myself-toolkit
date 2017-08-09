package com.sparrow.collect.store.object;

import com.sparrow.collect.store.io.DataWrite;
import com.sparrow.collect.store.serializer.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Administrator on 2016/12/2.
 */
public class GzipStandardObjectWrite extends StandardObjectWrite {

    public GzipStandardObjectWrite(DataWrite dataWrite, Serializer serializer) {
        super(dataWrite, serializer);
    }

    @Override
    protected byte[] handleObject(byte buffer[]) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(buffer);
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
