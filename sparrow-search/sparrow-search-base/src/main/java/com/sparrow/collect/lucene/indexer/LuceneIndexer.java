package com.sparrow.collect.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import com.sparrow.collect.lucene.creator.IDomCreator;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class LuceneIndexer {
    private String indexPath;
    private Analyzer analyzer;
    private Directory indexDirectory;
    private IndexWriter indexWriter;

    public LuceneIndexer() {
        this.indexPath = ("./temp");
        this.analyzer = new StandardAnalyzer(Version.LUCENE_46);
    }

    public LuceneIndexer(String indexPath) {
        this.indexPath = indexPath;
        this.analyzer = new StandardAnalyzer(Version.LUCENE_46);
    }

    public LuceneIndexer(String indexPath, Analyzer analyzer) {
        this.indexPath = indexPath;
        this.analyzer = analyzer;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public void initialize() {
        if (this.indexWriter != null)
            return;
        Directory directory = null;
        try {
            directory = FSDirectory.open(new File(this.indexPath));
            this.indexDirectory = directory;
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, new IKAnalyzer(true));
            indexWriterConfig.setMaxBufferedDocs(500);
            IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
            this.indexWriter = writer;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void destroy() throws IOException {
        if (this.analyzer != null)
            this.analyzer.close();
        if (this.indexWriter != null)
            this.indexWriter.close();
        if (this.indexDirectory != null)
            this.indexDirectory.close();

        this.analyzer = null;
        this.indexWriter = null;
        this.indexDirectory = null;
    }

    /**
     * <p>
     * Title: addIndexer
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param field
     * @param content
     * @author Yzc
     */
    public void addIndexer(String field, Object content) {
        try {
            if (content == null)
                return;
            Document doc = new Document();

            FieldType ft = new FieldType();
            ft.setTokenized(true);
            ft.setIndexed(true);
            ft.setStored(true);
            ft.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
            ft.setStoreTermVectorOffsets(true);
            ft.freeze();
            doc.add(new Field(field, content.toString(), ft));
            this.indexWriter.addDocument(doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Title: addIndexer
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param fields
     * @param contents
     * @param cover    覆盖索引文件
     * @author Yzc
     */
    public void addIndexer(String fields[], Object contents[], boolean cover) {
        // 不覆盖索引
        if (fields == null || contents == null)
            return;
        int len = fields.length;
        if (len > contents.length)
            len = contents.length;
        if (len == 0)
            return;
        try {
            IndexWriter writer = this.indexWriter;
            Document doc = new Document();
            for (int i = 0; i < len; i++) {
                if (contents[i] == null)
                    continue;
                FieldType ft = new FieldType();
                ft.setTokenized(true);
                ft.setIndexed(true);
                ft.setStored(true);
                ft.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
                ft.setStoreTermVectorOffsets(true);
                ft.freeze();
                doc.add(new Field(fields[i], contents[i].toString(), ft));
            }
            writer.addDocument(doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Title: create
     * </p>
     * <p>
     * Description: create index
     * </p>
     *
     * @author Yzc
     */
    public void doExecute(IndexTemplate a, ProgressNotify notify) {
        if (a != null)
            doExecute(a, notify, false);
    }

    public void doExecute(IndexTemplate a, ProgressNotify notify, boolean cover) {
        if (a != null) {
            IndexWriter writer = this.indexWriter;
            a.execute(writer, notify);
        }
    }

    public void delete() {
        this.rebuild();
    }

    public void append(List list, IDomCreator creator) {
        if (list == null || list.size() == 0)
            return;
        if (creator == null)
            return;
        IndexWriter writer = this.getIndexWriter(false);
        // 不覆盖
        if (writer == null)
            return;
        try {
            Document doc;
            for (int i = 0; i < list.size(); i++) {
                doc = creator.createDocument(list.get(i));
                if (doc != null)
                    writer.addDocument(doc);
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Title: append
     * </p>
     * <p>
     * Description: append index
     * </p>
     *
     * @param obj
     * @param creator
     * @author Yzc
     */
    public void append(Object obj, IDomCreator creator) {
        if (obj == null)
            return;
        if (creator == null)
            return;
        IndexWriter writer = this.indexWriter;
        // 不覆盖
        if (writer == null)
            return;
        try {
            Document doc;
            doc = creator.createDocument(obj);
            if (doc != null)
                writer.addDocument(doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void rebuild() {
/*        IndexWriter writer1;
        try {
            // 创建索引初始化，执行这些语句将创建或清空,目录下所有索引
            writer1 = new IndexWriter(this.indexPath, this.analyzer, true);
            writer1.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * <p>
     * Title: getIndexWriter
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param cover
     * @return
     * @author Yzc
     */
    public IndexWriter getIndexWriter(boolean cover) {
        return this.indexWriter;
    }

    /**
     * <p>
     * Title: delIndexer
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param key
     * @param value
     * @author Yzc
     */
    public void delIndexer(String key, String value) {
        try {
            this.indexWriter.deleteDocuments(new Term(key, value));
        } catch (Exception e) {
            System.out.println("delIndexer(String,String)" + e.getMessage());
        }
    }
}
