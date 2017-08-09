package com.frostwire.jlibtorrent.demo;

import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.SessionManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author gubatron
 * @author aldenml
 */
public final class GetMagnet {
    private static void loadLib(String path, String name) {
        name = System.mapLibraryName(name); // extends name with .dll, .so or .dylib
        try {
            String p= path +"/x86_64/"+ name;
            System.out.println(p);
            InputStream in = ClassLoader.getSystemResourceAsStream(p);
            File fileOut = new File("d:/tt.dll");
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());//loading goes here
        } catch (Exception e) {
            //handle
        }
    }
    public static void main(String[] args) throws Throwable {
       // System.loadLibrary("x86/jlibtorrent");
        //loadLib("lib","jlibtorrent");
        //String uri = "magnet:?xt=urn:btih:86d0502ead28e495c9e67665340f72aa72fe304e&dn=Frostwire.5.3.6.+%5BWindows%5D&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.istole.it%3A6969&tr=udp%3A%2F%2Fopen.demonii.com%3A1337";
        String uri = "magnet:?xt=urn:btih:a83cc13bf4a07e85b938dcf06aa707955687ca7c";

        final SessionManager s = new SessionManager();

        s.start();

        final CountDownLatch signal = new CountDownLatch(1);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long nodes = s.stats().dhtNodes();
                // wait for at least 10 nodes in the DHT.
                if (nodes >= 10) {
                    System.out.println("DHT contains " + nodes + " nodes");
                    signal.countDown();
                    timer.cancel();
                }
            }
        }, 0, 1000);

        System.out.println("Waiting for nodes in DHT (10 seconds)...");
        boolean r = signal.await(10, TimeUnit.SECONDS);
        if (!r) {
            System.out.println("DHT bootstrap timeout");
            System.exit(0);
        }

        System.out.println("Fetching the magnet uri, please wait...");
        byte[] data = s.fetchMagnet(uri, 30);

        if (data != null) {
            System.out.println(Entry.bdecode(data));
        } else {
            System.out.println("Failed to retrieve the magnet");
        }

        s.stop();
    }
}
