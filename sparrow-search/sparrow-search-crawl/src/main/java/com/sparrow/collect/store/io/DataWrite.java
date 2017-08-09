package com.sparrow.collect.store.io;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/29.
 */
public interface DataWrite {

    void writeInt(int num) throws IOException;

    void writeLong(long num) throws IOException;

    void writeString(String str) throws IOException;

    void write(byte[] bytes) throws IOException;

    void write(byte[] bytes, int start, int size) throws IOException;

    int size();

    void destroy();
}
