package com.sparrow.collect.lucene.creator;

import org.apache.lucene.document.Document;

public interface IDomCreator {
	public Document createDocument(Object obj);
}
