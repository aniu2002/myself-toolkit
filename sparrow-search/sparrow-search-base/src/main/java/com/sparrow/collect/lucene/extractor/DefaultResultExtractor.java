package com.sparrow.collect.lucene.extractor;

import org.apache.lucene.document.Document;

public class DefaultResultExtractor implements ResultExtractor {

	@Override
	public Object wrapHit(int id, Document document, float score, IRender render) {
		return null;
	}

}
