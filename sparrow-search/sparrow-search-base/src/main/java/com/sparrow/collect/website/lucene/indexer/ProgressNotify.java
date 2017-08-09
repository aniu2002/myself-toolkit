package com.sparrow.collect.website.lucene.indexer;

public interface ProgressNotify {
	public void begin(int records);

	public void progress(int items, String msg);

	public void end(String msg);
}
