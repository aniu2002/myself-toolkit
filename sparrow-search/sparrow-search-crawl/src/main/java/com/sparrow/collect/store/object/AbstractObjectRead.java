package com.sparrow.collect.store.object;

import com.sparrow.collect.store.io.AbstractDataRead;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.EOFException;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/2.
 */
public abstract class AbstractObjectRead implements ObjectRead {
    private final AbstractDataRead dataRead;
    private final byte[] buffer = new byte[8192];
    private int curMetaSize = -1;

    public AbstractObjectRead(AbstractDataRead dataRead) {
        this.dataRead = dataRead;
    }

    @Override
    public Object read() {
        try {
            if (this.curMetaSize > 0) {
                Class<?> clazz = this.readClassMeta(this.curMetaSize);
                int payloadSize = this.dataRead.readInt();
                if (payloadSize == 0)
                    return null;
                return this.doRead(this.dataRead, payloadSize, clazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object doRead(AbstractDataRead dataRead, int payloadSize, Class clazz) throws IOException {
        if (payloadSize > buffer.length) {
            byte buf[] = new byte[payloadSize];
            dataRead.read(buf, payloadSize);
            return this.handleBytes(buf, payloadSize, clazz);
        } else {
            dataRead.read(buffer, payloadSize);
            return this.handleBytes(buffer, payloadSize, clazz);
        }
    }

    protected Object handleBytes(byte bytes[], int payloadSize, Class<?> clazz) {
        if (clazz == String.class)
            return new String(bytes, 0, payloadSize);
        return this.getDeserializer().deserialize(bytes, 0, payloadSize, clazz);
    }

    int readPayloadSize() throws IOException {
        return this.dataRead.readInt();
    }

    Class<?> readClassMeta(int size) throws IOException, ClassNotFoundException {
        this.dataRead.read(buffer, size);
        String clazzName = new String(buffer, 0, size);
        if (StringUtils.equals(clazzName, "string"))
            return String.class;
        return ClassUtils.getClass(clazzName);
    }

    @Override
    public boolean hasNext() {
        try {
            int size = this.readPayloadSize();
            if (size > 0) {
                this.curMetaSize = size;
                return true;
            } else
                return false;
        } catch (EOFException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void destroy() {
        this.dataRead.destroy();
    }
}
