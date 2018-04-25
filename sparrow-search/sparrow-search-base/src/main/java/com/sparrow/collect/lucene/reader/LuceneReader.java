package com.sparrow.collect.lucene.reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/3/25 0025.
 */
public class LuceneReader {
    private String indexPath;
    private Analyzer analyzer;
    private Directory indexDirectory;
    private IndexReader indexReader;
    private IndexWriter indexWriter;

    public LuceneReader() {
        this("./temp");
    }

    public LuceneReader(String indexPath) {
        this(indexPath, new StandardAnalyzer(Version.LUCENE_46));
    }

    public LuceneReader(String indexPath, Analyzer analyzer) {
        try {
            this.createLuceneSearcher(indexPath, analyzer);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can't create lucene search for index path : %s", indexPath));
        }
    }

    void createLuceneSearcher(String indexPath, Analyzer analyzer) throws IOException {
        Directory directory = FSDirectory.open(new File(indexPath));
        //new IKAnalyzer(true)
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        indexWriterConfig.setMaxBufferedDocs(500);
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
        IndexReader reader = DirectoryReader.open(writer, true);

        this.analyzer = analyzer;
        this.indexPath = indexPath;
        this.indexDirectory = directory;
        this.indexWriter = writer;
        this.indexReader = reader;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public Directory getIndexDirectory() {
        return indexDirectory;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public IndexReader getIndexReader() {
        return indexReader;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }
}
