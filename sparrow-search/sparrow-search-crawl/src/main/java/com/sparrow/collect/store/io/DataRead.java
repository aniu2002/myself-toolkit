package com.sparrow.collect.store.io;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/29.
 */
public interface DataRead {
    int readInt() throws IOException;

    long readLong() throws IOException;

    String readString() throws IOException;

    int read() throws IOException;

    int read(byte[] bytes) throws IOException;

    int read(byte[] bytes, int size) throws IOException;

    void destroy();
}
