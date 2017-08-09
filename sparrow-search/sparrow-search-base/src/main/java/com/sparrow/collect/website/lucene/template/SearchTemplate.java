package com.sparrow.collect.website.lucene.template;

import com.sparrow.collect.website.lucene.extractor.ResultExtractor;
import com.sparrow.collect.website.utils.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchTemplate {
    private String[] directories;
    private IndexSearcher indexSearcher;
    private ResultExtractor extractor;

    public String[] getDirectories() {
        return directories;
    }

    public void setDirectories(String directories) throws IOException {
        if (StringUtils.isNullOrEmpty(directories))
            return;
        this.directories = directories.split(";");
        this.indexSearcher = SearcherFactory.createSearcher(this.directories);
    }

    public ResultExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(ResultExtractor extractor) {
        this.extractor = extractor;
    }

    /**
     * <p>
     * Title: thread safe
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param callback
     * @author Yzc
     */
    public void doSearch(CallBack callback) {
        //  SearcherHolder holder = new SearcherHolder(this.indexSearcher);
        callback.execute();
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
    public Map doSearch(Query query, Filter filter, Sort sort, int startPos,
                        int endPos) throws IOException {
        IndexSearcher searcher = this.indexSearcher;
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
        List list = this.extractHits(topDocs, extractor, startPos, endPos);
        result.put(ResultExtractor.RESULTLIST, list);
        result.put(ResultExtractor.RESULTTOTAL, String.valueOf(total));
        return result;
    }

    private List extractHits(TopDocs topDocs, ResultExtractor extractor,
                             int startPos, int endPos) throws IOException {
        ScoreDoc[] sds = topDocs.scoreDocs;
        List list = new ArrayList();
        for (int cpt = startPos; cpt < endPos; cpt++) {
            Document doc;
            ScoreDoc scoreDoc = sds[cpt];
            doc = this.indexSearcher.doc(scoreDoc.doc);
            list.add(extractor.wrapHit(scoreDoc.doc, doc, scoreDoc.score, null));
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
    private List extractHits(TopDocs topDocs, ResultExtractor extractor)
            throws IOException {
        ScoreDoc[] sds = topDocs.scoreDocs;
        List list = new ArrayList();
        for (int cpt = 0; cpt < topDocs.totalHits; cpt++) {
            Document doc;
            ScoreDoc scoreDoc = sds[cpt];
            doc = this.indexSearcher.doc(scoreDoc.doc);
            list.add(extractor.wrapHit(scoreDoc.doc, doc, scoreDoc.score, null));
        }
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
     * @param startPos
     * @param endPos
     * @return
     * @throws IOException
     * @author Yzc
     */
    public Map doSearch(Query query, int startPos, int endPos)
            throws IOException {
        return this.doSearch(query, null, null, startPos, endPos);
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
    public List doSearch(Query query, Filter filter, Sort sort)
            throws IOException {
        IndexSearcher searcher = this.indexSearcher;
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
        List list = this.extractHits(topDocs, this.extractor);
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
    public List doSearch(Query query) throws IOException {
        return this.doSearch(query, null, null);
    }
}
