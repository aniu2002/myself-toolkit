package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

public abstract class ConfigIniter {

    public void update(SearchConfig o) {
        SearchConfig config = (SearchConfig) o;
        try {
            parserConf(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void parserConf(SearchConfig config) throws IOException;

    public String[] getSearchIDs(SearchConfig config) {

        String[] searchIDS = config.get(Contants.SEARCH_LIST_TAG).split(",");
        return searchIDS;
    }

}
