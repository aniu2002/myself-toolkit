package com.sparrow.collect.store.io;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/29.
 */
public abstract class AbstractDataWrite implements DataWrite {
    private byte[] writeBuffer = new byte[8];
    protected int written;

    protected abstract BufferedOutputStream getBufferedOutputStream();

    private void incCount(int var1) {
        int var2 = this.written + var1;
        if (var2 < 0) {
            var2 = 2147483647;
        }

        this.written = var2;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        write(getBufferedOutputStream(), bytes);
    }

    protected void write(BufferedOutputStream out, byte[] bytes) throws IOException {
        write(out, bytes, 0, bytes.length);
    }

    protected void write(BufferedOutputStream out, byte[] bytes, int start, int size) throws IOException {
        out.write(bytes, start, size);
        incCount(size);
    }

    public void writeInt(int var1) throws IOException {
        writeInt(this.getBufferedOutputStream(), var1);
    }

    final void writeInt(BufferedOutputStream out, int var1) throws IOException {
        out.write(var1 >>> 24 & 255);
        out.write(var1 >>> 16 & 255);
        out.write(var1 >>> 8 & 255);
        out.write(var1 >>> 0 & 255);
        incCount(4);
    }

    @Override
    public void writeLong(long num) throws IOException {
        writeBuffer[0] = (byte) ((int) (num >>> 56));
        writeBuffer[1] = (byte) ((int) (num >>> 48));
        writeBuffer[2] = (byte) ((int) (num >>> 40));
        writeBuffer[3] = (byte) ((int) (num >>> 32));
        writeBuffer[4] = (byte) ((int) (num >>> 24));
        writeBuffer[5] = (byte) ((int) (num >>> 16));
        writeBuffer[6] = (byte) ((int) (num >>> 8));
        writeBuffer[7] = (byte) ((int) (num >>> 0));
        write(this.getBufferedOutputStream(), writeBuffer, 0, 8);
        this.incCount(8);
    }

    public final int size() {
        return this.written;
    }


    @Override
    public void writeString(String str) throws IOException {
        writeString(getBufferedOutputStream(), str);
    }

    void writeString(BufferedOutputStream out, String str) throws IOException {
        int var2 = str.length();
        for (int var3 = 0; var3 < var2; ++var3) {
            char var4 = str.charAt(var3);
            out.write(var4 >>> 8 & 255);
            out.write(var4 >>> 0 & 255);
        }
        this.incCount(var2 * 2);
    }

    @Override
    public void write(byte[] bytes, int start, int size) throws IOException {
        write(getBufferedOutputStream(), bytes, start, size);
    }
}
