package com.sparrow.collect.store.io;

import java.io.*;

/**
 * Created by Administrator on 2016/11/29.
 */
public class FileDataWrite extends AbstractDataWrite {
    private final BufferedOutputStream bufferedOutputStream;

    public FileDataWrite(File file) throws FileNotFoundException {
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    @Override
    protected BufferedOutputStream getBufferedOutputStream() {
        return bufferedOutputStream;
    }

    @Override
    public void destroy() {
        if (this.bufferedOutputStream != null) {
            try {
                this.bufferedOutputStream.flush();
                this.bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
