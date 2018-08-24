package com.sparrow.collect.space;

import org.apache.lucene.store.MMapDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by yaobo on 2014/11/11.
 */
public class DiskIndexSpace extends IndexSpace {

    public DiskIndexSpace(String indexPath) {
        super(indexPath);
    }

    @Override
    protected void initDirectory() throws IOException {
        File file = new File(this.indexPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.directory = new MMapDirectory(file);
    }
}
