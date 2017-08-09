package com.sparrow.app.lucene;

import java.io.IOException;
import java.util.Map;

import com.sparrow.collect.lucene.LuceneService;
import com.sparrow.collect.lucene.extractor.IRender;
import com.sparrow.collect.utils.HighLightTool;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.util.Version;


public class NewLuceneService extends LuceneService {

    @Override
    public void initialize() throws CorruptIndexException, IOException {
        super.initialize();
    }

    public Map doSearch(String fields[], String text, int startPos, int endPos) {
        final Analyzer analyzer = this.getAnalyzer();
        try {
            // String[] keywords = { text, text, "1" };
            // String[] nfields = { "name", "content", "hasImg" };
            // BooleanClause.Occur[] flags = { BooleanClause.Occur.SHOULD,
            // BooleanClause.Occur.MUST, BooleanClause.Occur.MUST };
            // final Query query = MultiFieldQueryParser.parse(keywords,
            // nfields,
            // flags, analyzer);

            final BooleanQuery query = new BooleanQuery();
            BooleanQuery q1 = new BooleanQuery();

            QueryParser parser1 = new QueryParser(Version.LUCENE_46, "name", analyzer);
            q1.add(parser1.parse(text), BooleanClause.Occur.SHOULD);

            QueryParser parser2 = new QueryParser(Version.LUCENE_46, "content", analyzer);
            q1.add(parser2.parse(text), BooleanClause.Occur.SHOULD);

            query.add(q1, BooleanClause.Occur.MUST);

            BooleanQuery q2 = new BooleanQuery();

            TermQuery tq1 = new TermQuery(new Term("hasImg", "1"));
            q2.add(tq1, BooleanClause.Occur.MUST);

            query.add(tq1, BooleanClause.Occur.MUST);

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

}
