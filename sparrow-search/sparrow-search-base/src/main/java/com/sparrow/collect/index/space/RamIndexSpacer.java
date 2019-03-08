package com.sparrow.collect.index.space;

import com.sparrow.collect.index.SpaceType;
import com.sparrow.collect.index.config.DefaultAnalyzers;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;

@Slf4j
public class RamIndexSpacer extends IndexSpacer {

    public RamIndexSpacer(String index, String dirPath, Analyzer analyzer) {
        super(index, dirPath, analyzer);
        //DefaultAnalyzers.getPerFieldAnalyzer()
    }

    @Override
    protected SpaceType getSpaceType() {
        return SpaceType.RAM;
    }

    @Override
    protected Directory initDirectory(File dir) {
        try {
            return new RAMDirectory(FSDirectory.open(dir), IOContext.DEFAULT);
        } catch (IOException e) {
            log.error("Open directory error : ", e);
        }
        return null;
    }
}
