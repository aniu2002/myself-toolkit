package com.sparrow.collect.website.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;

import com.sparrow.collect.website.lucene.extractor.IRender;
import com.sparrow.collect.website.lucene.extractor.ResultExtractor;
import com.sparrow.collect.website.utils.HighLightTool;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneService {
    private Analyzer analyzer;
    private String[] directories;
    private ResultExtractor extractor;
    private IndexSearcher indexSearcher;

    public ResultExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(ResultExtractor extractor) {
        this.extractor = extractor;
    }

    public void initialize() throws CorruptIndexException, IOException {
        this.directories = InstanceCache.INDEX_DIRECTORIES;
    }

    /**
     * <p>
     * Title: getSerarcher
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @return
     * @throws CorruptIndexException
     * @throws IOException
     * @author Yzc
     */
    protected IndexSearcher getSearcher() throws IOException {
        if (this.indexSearcher == null) {
            IndexReader[] readers = this.createIndexReader();
            MultiReader multiReader = new MultiReader(readers);
            this.indexSearcher = new IndexSearcher(multiReader);
        }
        return this.indexSearcher;
    }

    /**
     * <p>
     * Title: extractHits
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param topDocs
     * @param extractor
     * @param startPos
     * @param endPos
     * @return
     * @throws IOException
     * @author Yzc
     */
    private List extractHits(TopDocs topDocs, ResultExtractor extractor,
                             int startPos, int endPos, IRender render) throws IOException {
        ScoreDoc[] sds = topDocs.scoreDocs;
        List list = new ArrayList();
        for (int cpt = startPos; cpt < endPos; cpt++) {
            Document doc;
            ScoreDoc scoreDoc = sds[cpt];
            doc = this.indexSearcher.doc(scoreDoc.doc);
            list.add(extractor.wrapHit(scoreDoc.doc, doc, scoreDoc.score, render));
        }
        return list;
    }

    /**
     * <p>
     * Title: extractHits
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param topDocs
     * @param extractor
     * @return
     * @throws IOException
     * @author Yzc
     */
    private List extractHits(TopDocs topDocs, ResultExtractor extractor,
                             IRender render) throws IOException {
        List list = new ArrayList();
        ScoreDoc[] sds = topDocs.scoreDocs;
        for (int cpt = 0; cpt < topDocs.totalHits; cpt++) {
            Document doc;
            ScoreDoc scoreDoc = sds[cpt];
            doc = this.indexSearcher.doc(scoreDoc.doc);
            list.add(extractor.wrapHit(scoreDoc.doc, doc, scoreDoc.score, render));
        }
        return list;
    }

    /**
     * <p>
     * Title: createSearchers
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @return
     * @throws IOException
     * @author Yzc
     */
    private IndexReader[] createIndexReader() throws IOException {
        if (directories == null || directories.length == 0) {
            throw new IOException(" the directories must be specified.");
        }
        IndexReader indexReaders[] = new IndexReader[directories.length];
        IndexReader src;
        if (directories != null) {
            for (int index = 0; index < directories.length; index++) {
                try {
                    Directory directory = FSDirectory.open(new File(directories[index]));
                    src = DirectoryReader.open(directory);
                    indexReaders[index] = src;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return indexReaders;
    }

    /**
     * <p>
     * Title: doSearch
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param fields
     * @param text
     * @param startPos
     * @param endPos
     * @return
     * @author Yzc
     */
    public Map doSearch(String fields[], String text, int startPos, int endPos) {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_46, fields, analyzer);
        try {
            final Query query = parser.parse(text);
            IRender render = new IRender() {
                public String render(String text, String field) {
                    try {
                        return HighLightTool.highLight(analyzer, query, text,
                                field);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidTokenOffsetsException e) {
                        e.printStackTrace();
                    }
                    return text;
                }
            };
            return this.doSearch(query, startPos, endPos, render);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>
     * Title: doSearch
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param query
     * @param filter
     * @param sort
     * @param startPos
     * @param endPos
     * @return
     * @throws IOException
     * @author Yzc
     */
    protected Map doSearch(Query query, Filter filter, Sort sort, int startPos,
                           int endPos, IRender render) throws IOException {
        IndexSearcher searcher = this.getSearcher();
        TopDocs topDocs;
        if (filter != null && sort != null) {
            topDocs = searcher.search(query, filter, 1000, sort);
        } else if (filter != null) {
            topDocs = searcher.search(query, filter, 1000);
        } else if (sort != null) {
            topDocs = searcher.search(query, 1000, sort);
        } else {
            topDocs = searcher.search(query, 1000);
        }
        int total = topDocs.totalHits;
        if (endPos > total) {
            endPos = total;
        }
        Map result = new HashMap();
        List list = this.extractHits(topDocs, extractor, startPos, endPos, render);
        // close
        result.put(ResultExtractor.RESULTLIST, list);
        result.put(ResultExtractor.RESULTTOTAL, new Integer(total));
        return result;
    }

    /**
     * <p>
     * Title: doSearch
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param query
     * @param startPos
     * @param endPos
     * @return
     * @throws IOException
     * @author Yzc
     */
    protected Map doSearch(Query query, int startPos, int endPos, IRender render)
            throws IOException {
        return this.doSearch(query, null, null, startPos, endPos, render);
    }

    /**
     * <p>
     * Title: doSearch
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param query
     * @param filter
     * @param sort
     * @return
     * @throws IOException
     * @author Yzc
     */
    protected List doSearch(Query query, Filter filter, Sort sort,
                            IRender render) throws IOException {
        IndexSearcher searcher = this.getSearcher();
        TopDocs topDocs;
        if (filter != null && sort != null) {
            topDocs = searcher.search(query, filter, 1000, sort);
        } else if (filter != null) {
            topDocs = searcher.search(query, filter, 1000);
        } else if (sort != null) {
            topDocs = searcher.search(query, 1000, sort);
        } else {
            topDocs = searcher.search(query, 1000);
        }
        List list = this.extractHits(topDocs, this.extractor, render);
        return list;
    }

    /**
     * <p>
     * Title: doSearch
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param query
     * @return
     * @throws IOException
     * @author Yzc
     */
    protected List doSearch(Query query, IRender render) throws IOException {
        return this.doSearch(query, null, null, render);
    }

    public String[] getDirectories() {
        return directories;
    }

    public void setDirectories(String[] directories) {
        this.directories = directories;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }
}
