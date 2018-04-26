package com.dili.dd.searcher.basesearch.common.analyze;

import java.util.List;

/**
 * <B>Description</B>分词 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * @createTime 2014年6月19日 下午7:23:10
 * @author zhanglin
 */
public interface IAnalyze {

    List<String> split(String s);
}
