package com.sparrow.collect.lucene.extractor;

import org.apache.lucene.document.Document;

public interface ResultExtractor {
	public final String RESULTLIST = "list";
	public final String RESULTTOTAL = "total";

	Object wrapHit(int id, Document document, float score, IRender render);
}
