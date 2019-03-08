package com.sparrow.collect.index.space;

import com.sparrow.collect.index.SpaceType;
import com.sparrow.collect.website.utils.CloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DiskIndexSpacer extends IndexSpacer {

    public DiskIndexSpacer(String index, String dirPath, Analyzer analyzer) {
        super(index, dirPath, analyzer);
    }

    @Override
    protected SpaceType getSpaceType() {
        return SpaceType.DISK;
    }

    @Override
    public int mergeIndexAndReOpen() throws IOException {
        writer.maybeMerge();
        writer.waitForMerges();
        writer.forceMergeDeletes();
        int indexCount = writer.maxDoc();
        if (reader != null) {
            CloseUtil.closeAndNull(reader);
            reader = DirectoryReader.open(directory);
        }
        return indexCount;
    }

    @Override
    protected Directory initDirectory(File dir) {
        log.info("Mount index : {}  Disk Directory : {} ", this.index, this.dirPath);
        try {
            this.directory = new MMapDirectory(dir);
        } catch (IOException e) {
            log.error(" Directory init fialed...", e);
        }
        return null;
    }
}
