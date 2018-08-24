package com.sparrow.collect.data.search.sort;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * @createTime 2014年6月20日 上午10:22:14
 * @author zhanglin
 */
public class SortBean {

    /**
     * sort字段
     */
    private String sortFieldName;

    /**
     * sort Reverse信息 reverse为false为升序 ，反之则为降序
     */
    private Boolean sortReverse;

    public String getSortField() {
        return sortFieldName;
    }

    public void setSortField(String sortField) {
        this.sortFieldName = sortField;
    }

    public Boolean getSortReverse() {
        return sortReverse;
    }

    public void setSortReverse(Boolean sortReverse) {
        this.sortReverse = sortReverse;
    }

}
