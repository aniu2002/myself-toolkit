package com.sparrow.collect.lucene.creator;

import org.apache.lucene.document.Document;

import com.sparrow.collect.lucene.data.FileIndexItem;


public interface IFileCreator {
	public Document createDocument(FileIndexItem item);
}
