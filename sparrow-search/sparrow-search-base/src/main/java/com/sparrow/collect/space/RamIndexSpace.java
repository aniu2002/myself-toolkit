package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by yaobo on 2014/11/19.
 */
public class RamIndexSpace extends DefaultIndexSpace {

    public RamIndexSpace(String name, String indexPath, SearchConfig config) {
        super(name, indexPath, config);
    }

    public RamIndexSpace(String name, String indexPath, SearchConfig config, String alias) {
        super(name, indexPath, config, alias);
    }

    @Override
    protected Directory initDirectory() throws IOException {
        File file = new File(this.getIndexPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        MMapDirectory diskDirectory = new MMapDirectory(file);
        Directory directory = new RAMDirectory(diskDirectory, IOContext.DEFAULT);
        diskDirectory.close();
        return directory;
    }
}
