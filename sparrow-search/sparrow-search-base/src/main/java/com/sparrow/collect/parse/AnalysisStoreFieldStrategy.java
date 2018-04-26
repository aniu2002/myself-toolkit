package com.dili.dd.searcher.basesearch.common.field.parse;


import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;


/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 按照空格分词
 * @createTime 2014年5月29日 下午7:49:24
 * @author tanghongjun
 */
public class AnalysisStoreFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID, Configuration config, String fieldName, String fieldValue) {
        IndexableField blank = new TextField(fieldName, fieldValue, Store.YES);
        return blank;
    }

}
