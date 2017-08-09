package com.sparrow.app.services.provider;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanzc on 2015/8/24.
 */
@XmlRootElement(name = "ProviderSource")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderSource {

    @XmlElement(name = "name")
    String name;

    @XmlAnyElement
    @XmlElementWrapper(name = "items")
    @XmlElementRefs({@XmlElementRef(type = ProviderItem.class)})
    List<ProviderItem> items;

    @XmlTransient
    private boolean changed = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void addItem(ProviderItem providerItem) {
        if (this.items == null) {
            this.items = new ArrayList<ProviderItem>();
            this.items.add(providerItem);
        } else if (this.hasItem(providerItem.getName()))
            this.updateItem(providerItem);
        else
            this.items.add(providerItem);
        this.changed = true;
    }

    boolean hasItem(String key) {
        if (key == null || "".equals(key))
            return false;
        if (this.items == null || this.items.isEmpty())
            return false;
        Iterator<ProviderItem> ite = this.items.iterator();
        ProviderItem tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName()))
                return true;
        }
        return false;
    }

    public ProviderItem getItem(String key) {
        if (key == null || "".equals(key))
            return null;
        if (this.items == null || this.items.isEmpty())
            return null;
        Iterator<ProviderItem> ite = this.items.iterator();
        ProviderItem tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName()))
                return tmp;
        }
        return null;
    }

    public void updateItem(ProviderItem item) {
        String key = item.getName();
        if (key == null || "".equals(key))
            return;
        if (this.items == null || this.items.isEmpty())
            return;
        Iterator<ProviderItem> ite = this.items.iterator();
        ProviderItem tmp;
        while (ite.hasNext()) {
            tmp = ite.next();
            if (key.equals(tmp.getName())) {
                tmp.setDesc(item.getDesc());
                tmp.setScript(item.getScript());
                tmp.setSource(item.getSource());
                tmp.setType(item.getType());
                this.changed = true;
            }
        }
    }

    public void remove(String name) {
        if (name == null || name.equals(""))
            return;
        if (this.items == null)
            return;
        Iterator<ProviderItem> ite = this.items.iterator();
        ProviderItem temp;
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

    public List<ProviderItem> getItems() {
        return items;
    }

    public void setItems(List<ProviderItem> items) {
        this.items = items;
        this.changed = true;
    }
}
