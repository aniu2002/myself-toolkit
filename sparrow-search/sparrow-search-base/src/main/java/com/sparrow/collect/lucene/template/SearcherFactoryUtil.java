package com.sparrow.collect.lucene.template;

import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

public class SearcherFactoryUtil {
	/**
	 * 
	 * <p>
	 * Title: doGetSearcher
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param searcherFactory
	 * @return
	 * @throws IOException
	 * @author Yzc
	 */
	public static IndexSearcher doGetSearcher(SearcherFactory searcherFactory)
			throws IOException {
		SearcherHolder searcherHolder = (SearcherHolder) ResourceBinding
				.getResource(searcherFactory);
		if (searcherHolder != null && searcherHolder.getSearcher() != null) {
			return searcherHolder.getSearcher();
		}
		IndexSearcher searcher = searcherFactory.createSearcher( );
		if (searcherHolder != null) {
			// Lazily open the search if there is an SearcherHolder
			searcherHolder.setSearcher(searcher);
		}

		return searcher;
	}
}
