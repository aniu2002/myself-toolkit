package com.sparrow.collect.website.lucene.creator;

import org.apache.lucene.document.Document;

public interface IDomCreator {
	public Document createDocument(Object obj);
}
