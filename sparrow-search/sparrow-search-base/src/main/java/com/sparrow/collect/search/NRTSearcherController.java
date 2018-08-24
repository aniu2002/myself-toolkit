package com.sparrow.collect.search;

import com.sparrow.collect.space.AdvanceIndexSpace;
import com.sparrow.collect.space.IndexSpaceController;

/**
 * 得到searcher
 * Created by yaobo on 2014/11/20.
 */
public class NRTSearcherController {

    public static NRTSearcher getSearcher(String searchId){
        AdvanceIndexSpace indexSpace = IndexSpaceController.getController().getSpace(searchId);
        return indexSpace.getSearcher();
    }

    public static void release(NRTSearcher searcher){
        if (searcher != null){
            searcher.release();
        }
    }
}
