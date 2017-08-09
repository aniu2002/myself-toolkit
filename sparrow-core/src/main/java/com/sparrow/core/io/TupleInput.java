package com.sparrow.core.io;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-9
 * Time: 下午1:29
 * To change this template use File | Settings | File Templates.
 */
public class TupleInput {
    final ByteArrayInputStream bins;

    public TupleInput(int len) {
        this(new byte[len]);
    }

    public TupleInput(byte[] buffer) {
        this.bins = new ByteArrayInputStream(buffer);
    }

    public int readInt() throws EOFException {
        int ch1 = bins.read();
        int ch2 = bins.read();
        int ch3 = bins.read();
        int ch4 = bins.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public String readString() throws IOException {
        int l = this.readInt();
        if (l > 0) {
            byte[] bytes = new byte[l];
            bins.read(bytes);
            return new String(bytes);
        } else
            return null;
    }
}
