package com.sparrow.collect.space;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuanzc on 2016/3/22.
 */
public class IndexController {
    protected List<IndexSpacer> initSpacer(List<IndexSpacer> iSpacers, String searchID, Properties config) {
        if (iSpacers==null) {
            return new ArrayList<IndexSpacer>(0);
        }
        for (IndexSpacer is : iSpacers) {
            is.initIndexSpacer(searchID, config);
        }
        return iSpacers;
    }
}
