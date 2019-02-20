package com.sparrow.collect.format;


import com.sparrow.collect.utils.StringKit;

public class SplitStringFormat implements StringFormat {

    private Splitter spliter;

    public SplitStringFormat() {
    }

    public SplitStringFormat(Splitter spliter) {
        this.spliter = spliter;
    }

    @Override
    public String format(String string) {
        if (spliter == null) {
            return StringKit.getStringFromStringsWithUnique(WordSplitter
                    .getInstance().split(string));
        }
        return StringKit.getStringFromStringsWithUnique(spliter.split(string));
    }

    public void setSpliter(Splitter spliter) {
        this.spliter = spliter;
    }
}
