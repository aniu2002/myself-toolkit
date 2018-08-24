package com.sparrow.collect.space;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by yaobo on 2014/11/19.
 */
public class RamIndexSpace extends IndexSpace {

    public RamIndexSpace(String indexPath) {
        super(indexPath);
    }

    @Override
    protected void initDirectory() throws IOException {
        File file = new File(this.indexPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        MMapDirectory diskDirectory = new MMapDirectory(file);
        this.directory = new RAMDirectory(diskDirectory, IOContext.DEFAULT);
        diskDirectory.close();
    }
}
