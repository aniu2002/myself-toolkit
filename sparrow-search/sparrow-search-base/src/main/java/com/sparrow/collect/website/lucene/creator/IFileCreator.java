package com.sparrow.collect.website.lucene.creator;

import org.apache.lucene.document.Document;

import com.sparrow.collect.website.lucene.data.FileIndexItem;


public interface IFileCreator {
	public Document createDocument(FileIndexItem item);
}
