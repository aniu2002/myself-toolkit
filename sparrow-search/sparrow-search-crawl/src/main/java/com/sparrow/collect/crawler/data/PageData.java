package com.sparrow.collect.crawler.data;

import java.util.ArrayList;
import java.util.List;

public class PageData {
    private String url;
    private String title;
    private String text;
    private List<EntryData> entries;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<EntryData> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryData> entries) {
        this.entries = entries;
    }

    public void adEntry(EntryData entry) {
        if (this.entries == null)
            this.entries = new ArrayList<EntryData>();
        this.entries.add(entry);
    }

    public void clear() {
        if (this.entries == null || this.entries.isEmpty())
            return;
        for (EntryData entry : this.entries) {
            entry.clear();
            entry = null;
        }
        this.entries.clear();
        this.entries = null;
    }
}
