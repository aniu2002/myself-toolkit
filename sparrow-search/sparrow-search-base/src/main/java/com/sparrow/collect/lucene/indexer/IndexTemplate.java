package com.sparrow.collect.lucene.indexer;

import org.apache.lucene.index.IndexWriter;

public interface IndexTemplate {
	public void execute(IndexWriter writer, ProgressNotify notify);
}
