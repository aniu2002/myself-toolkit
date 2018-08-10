package com.sparrow.collect.website.query;

/**
 * <B>Description</B>范围<br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * @createTime 2014年6月13日 上午10:00:10
 * @author zhanglin
 */
public class Ranger<T> {

    /**
     * 范围下界
     */
    private T lowerValue=null;

    /**
     * 范围上界
     */
    private T upperValue=null;

    /**
     * 否包含下边界标识
     */
    boolean includeLower=true;

    /**
     * 是否包含上边界
     */
    boolean includeUpper=true;
    

    public T getLowerValue() {
        return lowerValue;
    }

    public void setLowerValue(T lowerValue) {
        this.lowerValue = lowerValue;
    }

    public T getUpperValue() {
        return upperValue;
    }

    public void setUpperValue(T upperValue) {
        this.upperValue = upperValue;
    }

    public boolean isIncludeLower() {
        return includeLower;
    }

    public void setIncludeLower(boolean includeLower) {
        this.includeLower = includeLower;
    }

    public boolean isIncludeUpper() {
        return includeUpper;
    }

    public void setIncludeUpper(boolean includeUpper) {
        this.includeUpper = includeUpper;
    }
}
