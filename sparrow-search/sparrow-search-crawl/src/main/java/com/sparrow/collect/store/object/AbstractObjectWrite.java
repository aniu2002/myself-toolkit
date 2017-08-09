package com.sparrow.collect.store.object;

import com.sparrow.collect.store.io.DataWrite;
import com.sparrow.collect.utils.BeanUtils;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/12/2.
 */
public abstract class AbstractObjectWrite implements ObjectWrite {
    private final DataWrite dataWrite;
    static final byte[] STR_BYTES = "string".getBytes();
    private final Lock lock = new ReentrantLock();

    public AbstractObjectWrite(DataWrite dataWrite) {
        this.dataWrite = dataWrite;
    }

    @Override
    public void write(Object object) {
        if (object == null)
            return;
        lock.lock();
        try {
            this.writeClassMeta(object.getClass());
            this.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        lock.unlock();
    }

    void writeClassMeta(Class<?> clazz) throws IOException {
        byte bytes[];
        if (clazz == String.class)
            bytes = STR_BYTES;
        else
            bytes = clazz.getName().getBytes();
        this.dataWrite.writeInt(bytes.length);
        this.dataWrite.write(bytes);
    }

    void writeObject(Object object) throws IOException {
        byte bytes[];
        if (BeanUtils.isString(object.getClass()))
            bytes = object.toString().getBytes();
        else
            bytes = this.getSerializer().serialize(object);
        if (bytes.length == 0) {
            this.dataWrite.writeInt(0);
            return;
        }
        bytes = this.handleObject(bytes);
        this.dataWrite.writeInt(bytes.length);
        this.dataWrite.write(bytes);
    }

    protected byte[] handleObject(byte bytes[]) {
        return bytes;
    }

    @Override
    public void destroy() {
        this.dataWrite.destroy();
    }
}
