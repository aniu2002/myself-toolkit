package com.sparrow.collect.website.space;

/**
 * Created by Administrator on 2016/3/22 0022.
 */
public class MSBean<T> {
    private T master;
    private T slave;

    public MSBean() {

    }

    public MSBean(T master, T slave) {
        this.master = master;
        this.slave = slave;
    }

    public T getMaster() {
        return master;
    }

    public T getSlave() {
        return slave;
    }

    public void setMaster(T master) {
        this.master = master;
    }

    public void setSlave(T slave) {
        this.slave = slave;
    }

    public void switchOver() {
        if (this.slave != null) {
            T newMaster = this.slave;
            this.slave = this.master;
            this.master = newMaster;
        }
    }
}
