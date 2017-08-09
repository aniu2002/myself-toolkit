package com.sparrow.app.services.source;


import com.sparrow.common.source.OptionItem;
import com.sparrow.common.source.SourceHandler;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.app.store.DataStore;
import com.sparrow.app.store.StoreFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanzc on 2015/8/19.
 */
public class DataSourceStore implements SourceHandler {
    private DataStore<SourceInfo> store = StoreFacade.createStore(SourceInfo.class);

    long longVal(String key) {
        if (StringUtils.isEmpty(key))
            return 0;
        //return System.nanoTime();
        return key.hashCode();
    }

    @Override
    public List<OptionItem> getSource(String query) {
        List<OptionItem> opts = new ArrayList<OptionItem>();
        List<SourceInfo> sources = this.list();
        if (sources != null && !sources.isEmpty()) {
            for (SourceInfo s : sources) {
                OptionItem op = new OptionItem();
                op.setCode(String.valueOf(s.getId()));
                op.setName(s.getName());
                op.setExtra(s.getName());
                opts.add(op);
            }
        }
        return opts;
    }

    public void addDataSource(SourceInfo sourceInfo) {
        sourceInfo.setId(this.longVal(sourceInfo.getName()));
        this.store.put(sourceInfo.getId(), sourceInfo);
        this.store.sync();
    }

    public void updateDataSource(SourceInfo sourceInfo) {
        this.store.put(sourceInfo.getId(), sourceInfo);
        this.store.sync();
    }

    public SourceInfo getDataSource(String name) {
        return this.store.get(this.longVal(name));
    }

    public SourceInfo getDataSource(Long id) {
        return this.store.get(id);
    }

    public void removeDataSource(String name) {
        this.store.remove(this.longVal(name));
    }

    public void removeDataSource(Long id) {
        this.store.remove(id);
    }

    public List<SourceInfo> list() {
        return this.store.list();
    }

    public void sync() {
        this.store.sync();
    }

    public void close() {
        this.store.close();
    }
}