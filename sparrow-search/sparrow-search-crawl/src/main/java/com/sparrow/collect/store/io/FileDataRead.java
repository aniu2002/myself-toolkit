package com.sparrow.collect.store.io;

import java.io.*;

/**
 * Created by Administrator on 2016/11/29.
 */
public class FileDataRead extends AbstractDataRead {
    private final BufferedInputStream bufferedInputStream;

    public FileDataRead(File file) throws FileNotFoundException {
        bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    public BufferedInputStream getBufferedInputStream() {
        return bufferedInputStream;
    }

    @Override
    public void destroy() {
        if (this.bufferedInputStream != null) {
            try {
                this.bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
