package com.sparrow.app.store.berkeley;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.io.Serializable;

@Entity
public class UrlData implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String url;

    private int siteId;

    private int docId;

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UrlData otherUrl = (UrlData) o;
        return url != null && url.equals(otherUrl.getUrl());

    }

    @Override
    public String toString() {
        return url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
}
