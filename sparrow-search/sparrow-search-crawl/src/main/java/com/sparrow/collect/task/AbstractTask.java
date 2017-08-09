package com.sparrow.collect.task;

public abstract class AbstractTask implements Task {

    public void start() {
        this.execute(this.initContext());
    }

    protected Context initContext() {
        return null;
    }
}
