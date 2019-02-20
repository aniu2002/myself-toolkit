package com.sparrow.collect.lucene.extractor;

import org.apache.lucene.document.Document;

public interface ResultExtractor {
	    String RESULTLIST = "list";
	    String RESULTTOTAL = "total";

	Object wrapHit(int id, Document document, float score, IRender render);
}
