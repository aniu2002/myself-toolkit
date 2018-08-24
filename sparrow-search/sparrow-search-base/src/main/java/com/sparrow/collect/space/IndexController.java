package com.sparrow.collect.space;

import com.sparrow.collect.index.DocIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuanzc on 2016/3/22.
 */
public class IndexController {
    protected List<DocIndex> initSpacer(List<DocIndex> iSpacers, String searchID, Properties config) {
        if (iSpacers == null) {
            return new ArrayList(0);
        }
        for (DocIndex is : iSpacers) {
            is.initIndexSpacer(searchID, config);
        }
        return iSpacers;
    }
}
