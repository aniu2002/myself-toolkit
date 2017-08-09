package com.sparrow.collect.toolkit;

import com.sparrow.collect.store.deserializer.JsonDeserializer;
import com.sparrow.collect.store.io.FileDataRead;
import com.sparrow.collect.store.object.GzipStandardObjectRead;
import com.sparrow.collect.store.object.StandardObjectRead;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
public class ReadStoreKit {

    static void read(String file) {
        try {
            StandardObjectRead objectRead = new GzipStandardObjectRead(new FileDataRead(new File(file)), new JsonDeserializer());
            int i = 0;
            while (objectRead.hasNext()) {
                Object obj = objectRead.read();
                System.out.println(obj.getClass().getName() + " -- ");
                System.out.print("\t");
                System.out.println(obj.toString());
                i++;
            }
            System.out.println("total : " + i);
            objectRead.destroy();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) {
        //read("D:\\_crawler\\test.store");
        read("D:\\_crawler\\tmp\\diaoyu_123.crawl");
    }
}
