package com.au.cache.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ByteStreamTools {
	public static ByteArrayOutputStream serialize(Serializable serializable, int estimatedPayloadSize) throws IOException {
        ByteArrayOutputStream outstr = new ByteArrayOutputStream(estimatedPayloadSize);
        ObjectOutputStream objstr = new ObjectOutputStream(outstr);
        objstr.writeObject(serializable);
        objstr.close();
        return outstr;
    }
}
