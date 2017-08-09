package com.sparrow.app.services.provider;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanzc on 2015/8/18.
 */
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceConfigWrapper {

    @XmlElement(name = "name")
    String name;

    @XmlAnyElement
    @XmlElementWrapper(name = "sources")
    @XmlElementRefs({@XmlElementRef(type = SourceConfig.class)})
    private List<SourceConfig> sources;

    @XmlTransient
    private boolean changed = false;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SourceConfig> getSources() {
        return sources;
    }

    public void setSources(List<SourceConfig> sources) {
        this.sources = sources;
    }

    public void addItem(SourceConfig item) {
        if (this.sources == null) {
            this.sources = new ArrayList<SourceConfig>();
            this.sources.add(item);
        } else if (this.hasItem(item.getName()))
            this.updateItem(item);
        else
            this.sources.add(item);
        this.changed = true;
    }

    boolean hasItem(String key) {
        if (key == null || "".equals(key))
            return false;
        if (this.sources == null || this.sources.isEmpty())
            return false;
        Iterator<SourceConfig> ite = this.sources.iterator();
        SourceConfig tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName()))
                return true;
        }
        return false;
    }

    public SourceConfig getItem(String key) {
        if (key == null || "".equals(key))
            return null;
        if (this.sources == null || this.sources.isEmpty())
            return null;
        Iterator<SourceConfig> ite = this.sources.iterator();
        SourceConfig tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName()))
                return tmp;
        }
        return null;
    }

    public void updateItem(SourceConfig item) {
        String key = item.getName();
        if (key == null || "".equals(key))
            return;
        if (this.sources == null || this.sources.isEmpty())
            return;
        Iterator<SourceConfig> ite = this.sources.iterator();
        SourceConfig tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName())) {
                tmp.setDesc(item.getDesc());
                tmp.setType(item.getType());
                tmp.setProps(item.getProps());
                this.changed = true;
            }
        }
    }

    public void remove(String name) {
        if (name == null || name.equals(""))
            return;
        if (this.sources == null)
            return;
        Iterator<SourceConfig> ite = this.sources.iterator();
        SourceConfig temp;
        while (ite.hasNext()) {
            temp = ite.next();
            if (name.equals(temp.getName())) {
                ite.remove();
                temp = null;
                this.changed = true;
                break;
            }
        }
    }
}