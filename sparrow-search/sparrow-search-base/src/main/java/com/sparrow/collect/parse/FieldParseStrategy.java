package com.sparrow.collect.parse;


import org.apache.lucene.index.IndexableField;


/**
 * 包括，区特殊字符，完全相同，分词-空格分词，停用词
 * 
 * @createTime 2014年5月29日 下午7:28:51
 */
public interface FieldParseStrategy {
    
    public IndexableField parse(String searchID, String fieldName, String fieldValue);

}
