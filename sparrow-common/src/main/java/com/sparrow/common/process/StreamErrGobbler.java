package com.sparrow.common.process;

import java.io.*;

/**
 * Created by yuanzc on 2014/7/22.
 */
public class StreamErrGobbler extends Thread {
    InputStream is;
    String type;
    OutputStream os;
    OutputStream err;
    boolean stopped = false;

    public void stopNow() {
        this.stopped = true;
    }

    StreamErrGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    StreamErrGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }

    StreamErrGobbler(InputStream is, String type, OutputStream out,
                     OutputStream err) {
        this.is = is;
        this.type = type;
        this.os = out;
        this.err = err;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        PrintWriter epw = null;
        try {
            if (os != null)
                pw = new PrintWriter(os);
            if (err != null)
                epw = new PrintWriter(err);
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String line = null;
            while ((!this.stopped) && (line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    pw.flush();
                }
                if (epw != null) {
                    epw.println(line);
                    epw.flush();
                }

                // System.out.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            close(pw);
            close(epw);
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