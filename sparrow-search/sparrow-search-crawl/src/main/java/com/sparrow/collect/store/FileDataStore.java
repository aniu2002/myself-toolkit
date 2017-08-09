package com.sparrow.collect.store;

import com.sparrow.collect.store.io.FileDataWrite;
import com.sparrow.collect.store.object.GzipStandardObjectWrite;
import com.sparrow.collect.store.object.StandardObjectWrite;
import com.sparrow.collect.store.serializer.JsonSerializer;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/12/5.
 */
public class FileDataStore implements DataStore {
    private StandardObjectWrite objectWrite;

    public FileDataStore(File file, boolean gzip) throws FileNotFoundException {
        if (gzip)
            this.objectWrite = new GzipStandardObjectWrite(new FileDataWrite(file), new JsonSerializer());
        else
            this.objectWrite = new StandardObjectWrite(new FileDataWrite(file), new JsonSerializer());
    }

    @Override
    public boolean exists(Object object) {
        return false;
    }

    @Override
    public void save(Object object) {
        this.objectWrite.write(object);
    }

    @Override
    public int checkAndSave(Object object) {
        if (this.exists(object))
            this.update(object);
        else
            this.save(object);
        return 1;
    }

    @Override
    public void update(Object object) {

    }

    @Override
    public void close() {
        this.objectWrite.destroy();
    }
}
