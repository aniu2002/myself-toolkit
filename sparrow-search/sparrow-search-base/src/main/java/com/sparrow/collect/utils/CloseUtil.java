package com.sparrow.collect.utils;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/22 0022.
 */
public abstract class CloseUtil {
    public static void closeAndNull(IndexReader indexReader) {
        if (indexReader != null)
            try {
                indexReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void closeAndNull(Directory directory) {
        if (directory != null)
            try {
                directory.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void closeAndNull(IndexWriter indexWriter) {
        if (indexWriter != null)
            try {
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
