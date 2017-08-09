package com.sparrow.collect.store.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/29.
 */
public abstract class AbstractDataRead implements DataRead {
    private byte[] readBuffer = new byte[8];

    public abstract BufferedInputStream getBufferedInputStream();

    @Override
    public int readInt() throws IOException {
        return readInt(this.getBufferedInputStream());
    }

    protected int readInt(BufferedInputStream in) throws IOException {
        int var1 = in.read();
        int var2 = in.read();
        int var3 = in.read();
        int var4 = in.read();
        if ((var1 | var2 | var3 | var4) < 0) {
            throw new EOFException();
        } else {
            return (var1 << 24) + (var2 << 16) + (var3 << 8) + (var4 << 0);
        }
    }

    @Override
    public long readLong() throws IOException {
        this.readFully(this.getBufferedInputStream(), this.readBuffer, 0, 8);
        return ((long) this.readBuffer[0] << 56) + ((long) (this.readBuffer[1] & 255) << 48) + ((long) (this.readBuffer[2] & 255) << 40) + ((long) (this.readBuffer[3] & 255) << 32) + ((long) (this.readBuffer[4] & 255) << 24) + (long) ((this.readBuffer[5] & 255) << 16) + (long) ((this.readBuffer[6] & 255) << 8) + (long) ((this.readBuffer[7] & 255) << 0);
    }

    protected void readFully(BufferedInputStream in, byte[] var1) throws IOException {
        this.readFully(in, var1, 0, var1.length);
    }

    protected void readFully(BufferedInputStream in, byte[] var1, int var2, int var3) throws IOException {
        if (var3 < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            int var5;
            for (int var4 = 0; var4 < var3; var4 += var5) {
                var5 = in.read(var1, var2 + var4, var3 - var4);
                if (var5 < 0) {
                    throw new EOFException();
                }
            }

        }
    }

    @Override
    public String readString() throws IOException {
        return null;
    }

    @Override
    public int read() throws IOException {
        return this.getBufferedInputStream().read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return this.getBufferedInputStream().read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int size) throws IOException {
        return this.getBufferedInputStream().read(bytes, 0, size);
    }
}
