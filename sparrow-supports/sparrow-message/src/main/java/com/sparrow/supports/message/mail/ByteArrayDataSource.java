package com.sparrow.supports.message.mail;

import javax.activation.DataSource;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-1-9
 * Time: 下午7:20
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayDataSource implements DataSource {
    /**
     * Data to write.
     */
    private byte[] _data;
    /**
     * Content-Type.
     */
    private String _type;

    /* Create a datasource from an input stream */
    public ByteArrayDataSource(InputStream is, String type) {
        _type = type;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;
            // doing buffered reads, rather than one byte reads
            while ((ch = is.read()) != -1)
                os.write(ch);
            _data = os.toByteArray();
        } catch (IOException ioe) {
        }
    }

    /* Create a datasource from a byte array */
    public ByteArrayDataSource(byte[] data, String type) {
        _data = data;
        _type = type;
    }

    /* Create a datasource from a String */
    public ByteArrayDataSource(String data, String type) {
        try {
            // Assumption that the string contains only ascii
            // characters ! Else just pass in a charset into this
            // constructor and use it in getBytes()
            _data = data.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException uee) {
        }
        _type = type;
    }

    public InputStream getInputStream()
            throws IOException {
        if (_data == null)
            throw new IOException("no data");
        return new ByteArrayInputStream(_data);
    }

    public OutputStream getOutputStream()
            throws IOException {
        throw new IOException("cannot do this");
    }

    public String getContentType() {
        return _type;
    }

    public String getName() {
        return "dummy";
    }
}
