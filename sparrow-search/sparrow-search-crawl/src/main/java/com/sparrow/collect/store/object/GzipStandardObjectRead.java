package com.sparrow.collect.store.object;

import com.sparrow.collect.store.deserializer.Deserializer;
import com.sparrow.collect.store.io.AbstractDataRead;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2016/12/2.
 */
public class GzipStandardObjectRead extends StandardObjectRead {

    public GzipStandardObjectRead(AbstractDataRead dataRead, Deserializer deserializer) {
        super(dataRead, deserializer);
    }

    protected Object doRead1(AbstractDataRead dataRead, int payloadSize, Class clazz) throws IOException {
        // dataRead.read(buffer, payloadSize);
        byte bytes[] = this.unCompress(dataRead.getBufferedInputStream(), payloadSize);
        return this.handleBytes(bytes, bytes.length, clazz);
    }

    protected Object handleBytes(byte[] bytes, int payloadSize, Class<?> clazz) {
        byte unzipBytes[] = this.unCompress(bytes, payloadSize);
        return super.handleBytes(unzipBytes, unzipBytes.length, clazz);
    }

    byte[] unCompress(InputStream inputStream, int size) {
        if (inputStream == null)
            return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPInputStream unGzip = new GZIPInputStream(inputStream, size);
            byte[] buffer = new byte[256];
            int n;
            while ((n = unGzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            unGzip.close();
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

    byte[] unCompress(byte[] bytes, int size) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes, 0, size);
        try {
            GZIPInputStream unGzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = unGzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            unGzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
