package com.sparrow.core.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-9
 * Time: 下午1:29
 * To change this template use File | Settings | File Templates.
 */
public class TupleOutput {
    final ByteArrayOutputStream bos;

    public TupleOutput() {
        this.bos = new ByteArrayOutputStream();
    }

    public TupleOutput(int len) {
        this.bos = new ByteArrayOutputStream(len);
    }

    public void writeInt(int v) throws EOFException {
        bos.write((v >>> 24) & 0xFF);
        bos.write((v >>> 16) & 0xFF);
        bos.write((v >>> 8) & 0xFF);
        bos.write((v >>> 0) & 0xFF);
    }

    public void writeString(String s) throws IOException {
        if (this.isEmpty(s)) {
            this.writeInt(0);
        } else {
            byte bytes[] = s.getBytes();
            this.writeInt(bytes.length);
            bos.write(bytes);
        }
    }


    public byte[] getBytes() {
        return bos.toByteArray();
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
