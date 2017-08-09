package au.core.net.ftp.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StreamTokenizer;
import java.net.ServerSocket;
import java.net.Socket;

import au.core.net.ftp.basic.FtpUtils;
import au.core.net.ftp.conf.Settings;
import au.core.net.ftp.listeneer.ListenerSupport;
import au.core.net.ftp.log.Logger;
import au.core.net.ftp.log.SystemLogger;

public class DataConnection implements Runnable {
    public final static String GET = "GET";
    public final static String PUT = "PUT";
    public static final String PUTDIR = "putdir";
    public static final String DFINISHED = null;

    private Socket sock = null;
    private ServerSocket ssock = null;
    private BufferedInputStream in = null;
    private ListenerSupport suport;

    private Thread reciever;

    private String host;
    private String file;
    private String localfile = null;

    private String type = "GET";
    private int port = 7000;
    private final long offset = 4 * 1024 * 1024;// 20M feed back
    private long skiplen = 0;

    private boolean ok = true;
    private boolean isThere = false;
    private boolean justStream = false;
    private boolean getStream = false;
    private boolean isStop = false;
    private boolean resume = false;
    private boolean finished = false;

    private Logger logger = new SystemLogger();

    public DataConnection(String host, int p, String type, boolean justStream) {
        this.host = host;
        this.type = type;
        this.port = p;
        this.justStream = justStream;

        reciever = new Thread(this);
        reciever.start();
    }

    public DataConnection(String host, int p, String type, String path,
                          boolean justStream) {
        this.host = host;
        this.type = type;
        this.port = p;
        this.justStream = justStream;
        this.file = type;

        reciever = new Thread(this);
        reciever.start();
    }

    public DataConnection(String host, int port, String type, String file,
                          boolean enableResume, long len, ListenerSupport suport) {
        this.suport = suport;
        this.host = host;
        this.type = type;
        this.port = port;
        this.resume = enableResume;
        this.file = file;
        this.skiplen = len;
        if (!this.resume)
            this.skiplen = 0;
        reciever = new Thread(this);
        reciever.start();
    }

    public void run() {
        try {
            if (Settings.FTP_PASV_MODE) {
                try {
                    sock = new Socket(host, port);
                    sock.setSoTimeout(Settings.TIME_OUT);
                } catch (Exception ex) {
                    ok = false;
                    System.out.println("Can't open Socket on port " + port);
                }
            } else {
                try {
                    ssock = new ServerSocket(port);
                    System.out.println("  [ServerSocket on port " + port);
                    // sock = ssock.accept();
                    // System.out.println("Accept one client ... ");
                } catch (Exception ex) {
                    ok = false;
                    System.out.println("Can't open ServerSocket on port "
                            + port);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        isThere = true;

        if (!Settings.FTP_PASV_MODE) {
            int retry = 0;
            while ((retry++ < 5) && (sock == null)) {
                try {
                    ssock.setSoTimeout(Settings.TIME_OUT);
                    sock = ssock.accept();
                } catch (IOException e) {
                    sock = null;
                    System.out
                            .println("Got IOException while trying to open a socket!");
                } finally {
                    try {
                        ssock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(" [Attempt timed out, retrying...");
            }
            if (retry == 5) {
                ok = false;
                finished = true;
                System.out.println("Connection failed, tried 5 times ");
                return;
            }
        }
        if (ok) {
            if (this.suport != null)
                this.suport.fireConnectionInitialized();
            if (type.equals(GET)) {
                try {
                    in = new BufferedInputStream(sock.getInputStream(),
                            Settings.BUFFER_SIZE);
                    if (justStream) {
                        getStream = true;
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ok = false;
                    System.out.println("Can't get InputStream");
                }
                if (ok)
                    doGetData();
            } else if (type.equals(PUT)) {
                doPutData();
            }
        }
        this.finished = true;
    }

    /**
     * 从ftp服务器上获取下载数据
     */
    private void doGetData() {
        RandomAccessFile fOut = null;
        BufferedOutputStream bOut = null;
        byte[] buf = new byte[Settings.BUFFER_SIZE];

        try {
            if (resume) {
                File f = new File(file);
                fOut = new RandomAccessFile(file, "rw");
                fOut.skipBytes((int) f.length());
            } else {
                if (localfile == null) {
                    localfile = file;
                }
                File f = new File(localfile);
                if (f.exists()) {
                    f.delete();
                } else {
                    String dir = FtpUtils.getPath(localfile);
                    File fi = new File(dir);
                    if (!fi.exists())
                        fi.mkdirs();
                }
                f = new File(localfile);
                bOut = new BufferedOutputStream(
                        new FileOutputStream(localfile), Settings.BUFFER_SIZE);
            }
        } catch (Exception ex) {
            System.out.println("Can't create outputfile: " + file);
            ok = false;
        }
        if (ok) {
            try {
                long len = 0;
                if (fOut != null) {
                    while (true) {
                        // resuming
                        int read = -2;
                        if (isStop)
                            break;
                        try {
                            read = in.read(buf);
                        } catch (IOException es) {
                            logger.error("got a IOException");
                            ok = false;
                            fOut.close();
                            fireProgressUpdate(file, "failed", -1);
                            logger.error("last read: " + read + ", len: "
                                    + (len + read));
                            es.printStackTrace();
                            return;
                        }
                        len += read;
                        if (read == -1) {
                            break;
                        }
                        fOut.write(buf, 0, read);
                        if (read == StreamTokenizer.TT_EOF) {
                            break;
                        }
                        if (len >= this.offset) {
                            fireProgressUpdate(file, type, len - len);
                            len = 0;
                        }
                    }
                } else {
                    while (true) {
                        int read = -2;
                        if (isStop)
                            break;
                        try {
                            read = in.read(buf);
                        } catch (IOException es) {
                            logger.error("got a IOException");
                            ok = false;
                            bOut.close();
                            fireProgressUpdate(file, "FAILED", -1);
                            logger.error("last read: " + read + ", len: "
                                    + (len + read));
                            es.printStackTrace();
                            return;
                        }
                        len += read;
                        if (read == -1) {
                            break;
                        }
                        bOut.write(buf, 0, read);
                        if (read == StreamTokenizer.TT_EOF) {
                            break;
                        }
                        if (len >= this.offset) {
                            fireProgressUpdate(file, type, len);
                            len = 0;
                        }
                    }
                }
            } catch (IOException ex) {
                ok = false;
                System.out.println("Old connection removed");
                fireProgressUpdate(file, "FAILED", -1);
            } finally {
                try {
                    if (bOut != null) {
                        bOut.flush();
                        bOut.close();
                    }
                    if (fOut != null) {
                        fOut.close();
                    }
                    if (in != null && !justStream) {
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向ftp服务写上载数据流
     */
    private void doPutData() {
        BufferedOutputStream out = null;
        RandomAccessFile fIn = null;
        if (in == null) {
            try {
                fIn = new RandomAccessFile(file, "r");
                if (resume) {
                    System.out.println("length:" + this.skiplen);
                    fIn.seek(skiplen);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ok = false;
            }
        }

        if (ok) {
            byte[] buf = new byte[Settings.BUFFER_SIZE];
            try {
                out = new BufferedOutputStream(sock.getOutputStream());
            } catch (Exception ex) {
                ex.printStackTrace();
                ok = false;
                fireProgressUpdate(file, "FAILED", -1);
                System.out.println("Can't get OutputStream");
            }

            if (ok) {
                try {
                    // long len = skiplen;
                    // long oldLen = skiplen;
                    long offs = 0l;
                    while (!sock.isClosed()) {
                        int read;
                        if (isStop)
                            break;
                        if (in != null) {
                            read = in.read(buf);
                        } else {
                            read = fIn.read(buf);
                        }
                        // System.out.println("read:" + read);
                        offs += read;
                        if (read == -1) {
                            break;
                        }
                        out.write(buf, 0, read);
                        if (read == StreamTokenizer.TT_EOF) {
                            break;
                        }
                        // offs = len - oldLen;
                        if (offs >= this.offset) {
                            fireProgressUpdate(file, type, offs);
                            offs = 0;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ok = false;
                    fireProgressUpdate(file, "FAILED", -1);
                    System.out.println("Error: Data connection closed.");
                } finally {
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (fIn != null) {
                            fIn.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public InputStream getInputStream() {
        return in;
    }

    public void close() {
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Description: data connection has connected
     * </p>
     *
     * @return
     * @author Yzc
     */
    public boolean isThere() {
        if (finished) {
            return true;
        }
        return isThere;
    }

    public boolean isOk() {
        return this.ok;
    }

    /**
     * @param supor 设置监视器
     */
    public void setListenerSupport(ListenerSupport supor) {
        this.suport = supor;
    }

    private void fireProgressUpdate(String file, String type, long bytes) {
        if (this.suport != null)
            this.suport.fireProgressUpdate(file, type, bytes);
    }

    /**
     * <p>
     * Description: data connection has connected
     * </p>
     *
     * @return
     * @author Yzc
     */
    public boolean isFinished() {
        return finished;
    }

    public boolean isGetStream() {
        return getStream;
    }
}
