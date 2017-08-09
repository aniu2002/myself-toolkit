package com.sparrow.app.shell;

import java.io.*;

/**
 * Created by yuanzc on 2014/7/22.
 */
public class StreamOutGobbler extends Thread {
    InputStream is;
    String type;
    OutputStream os;
    boolean stopped = false;

    public void stopNow() {
        this.stopped = true;
    }

    StreamOutGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    StreamOutGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            if (os != null)
                pw = new PrintWriter(os);

            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    pw.flush();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            close(pw);
            close(br);
            close(isr);
        }
    }

    static void close(Reader reader) {
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    static void close(Writer writer) {
        if (writer != null)
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}