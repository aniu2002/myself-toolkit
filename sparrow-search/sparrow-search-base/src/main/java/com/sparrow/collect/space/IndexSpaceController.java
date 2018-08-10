package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexSpaceController  extends ConfigIniter{

    private Map<String, AdvanceIndexSpace> spacers = new ConcurrentHashMap<>();
    private Log log = LogFactory.getLog(IndexSpaceController.class);
    private SearchConfig config = null;
    private static final IndexSpaceController INDEX_SPACER_CONTROLLER = new IndexSpaceController();

    private IndexSpaceController() {
        if (INDEX_SPACER_CONTROLLER != null) {
            try {
                throw new Exception("duplicate instance create error!" + IndexSpaceController.class.getName());
            } catch (Exception e) {
                log.warn("duplicate instance create error!" + IndexSpaceController.class.getName());
            }
        }
    }

    public static IndexSpaceController getController() {
        return INDEX_SPACER_CONTROLLER;
    }

    public AdvanceIndexSpace getSpace(String searchID) {
        return spacers.get(searchID);
    }

    public void parserConf(SearchConfig config) throws IOException {
        String[] searchIDS = this.getSearchIDs(config);
        this.config = config;
        log.info("start init space:");
        for (String searchID : searchIDS) {
            String controller = config.get(("searcher.basesearch.") + searchID + (".space.controller.cla"));
            if (this.getClass().getName().equals(controller)){
                List<AdvanceIndexSpace> instances = config.getInstances("searcher.basesearch." + searchID + ".space.cla", AdvanceIndexSpace.class);
                if (CollectionUtils.isNotEmpty(instances)) {
                    AdvanceIndexSpace is = instances.get(0);
                    is.init(searchID, config);
                    spacers.put(searchID, is);
                }
            }
        }
    }
}
