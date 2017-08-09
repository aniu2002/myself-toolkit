package com.sparrow.core.io;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-9
 * Time: 下午1:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class TupleBinding<T> {

    public abstract T readData(TupleInput input);

    public abstract void writeData(T data, TupleOutput output);

    public final T readData(byte[] bytes) {
        TupleInput ti = getTupleInput(bytes);
        return readData(ti);
    }

    public final byte[] getBytes(T data) {
        TupleOutput output = this.getTupleOutput();
        this.writeData(data, output);
        return output.getBytes();
    }

    private TupleOutput getTupleOutput() {
        return new TupleOutput(512);
    }

    private TupleInput getTupleInput(byte[] bytes) {
        return new TupleInput(bytes);
    }
}

