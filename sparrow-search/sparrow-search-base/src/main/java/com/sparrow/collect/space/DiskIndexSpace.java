package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by yaobo on 2014/11/11.
 */
public class DiskIndexSpace extends DefaultIndexSpace {

    public DiskIndexSpace(String name, String indexPath, SearchConfig config) {
        super(name, indexPath, config);
    }

    public DiskIndexSpace(String name, String indexPath, SearchConfig config, String alias) {
        super(name, indexPath, config, alias);
    }

    @Override
    protected Directory initDirectory() throws IOException {
        File file = new File(this.getIndexPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new MMapDirectory(file);
    }
}
