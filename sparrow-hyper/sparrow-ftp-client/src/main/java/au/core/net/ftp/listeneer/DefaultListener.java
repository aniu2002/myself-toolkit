package au.core.net.ftp.listeneer;

import au.core.net.ftp.connection.FtpConnection;

public class DefaultListener implements ConnectionListener {
    String lastMessage = null;
    private long start = 0l;
    private long oldTime = 0l;
    private long nowTime = 0l;
    private long offset = 0l;
    private double seconds = 0l;

    public void actionFinished(FtpConnection connection) {
        this.nowTime = System.currentTimeMillis();
        this.offset = this.nowTime - this.start;
        this.seconds = this.offset / 1000.0;
        System.out.println("total seconds:" + this.seconds + "(s)");
        connection.disconnect();
    }

    public void connectionFailed(String why) {
        // TODO Auto-generated method stub

    }

    public void connectionInitialized() {
        // TODO Auto-generated method stub

    }

    public void updateProgress(String file, String type, long bytes) {
        if (this.oldTime == 0) {
            this.start = this.oldTime = System.currentTimeMillis();
            return;
        }
        this.nowTime = System.currentTimeMillis();
        this.offset = this.nowTime - this.oldTime;
        this.seconds = this.offset / 1000.0;
        int m = (int) bytes / (1024 * 1024);
        m = (int) (m / seconds);
        this.oldTime = this.nowTime;
        System.out.println("file:" + file + " type:" + type + "  bytes:" + m
                + "m/s");
    }

    public void updateRemoteDirectory(String pwd) {
        // TODO Auto-generated method stub

    }

    public void upateSize(long complete, long total, int tid, boolean completed) {
        if (completed) {
            System.out.print("\nCompleted task:" + tid);
            return;
        }
        String str = getMbString(complete) + "/" + getMbString(total);

        if (lastMessage != null) {
            for (int i = 0; i < lastMessage.length(); i++)
                System.out.print("\b");
        }
        System.out.print(str);
        lastMessage = str;
    }

    /**
     * @param size
     * @return long字节的数据转化成MB的string
     */
    private String getMbString(long size) {
        String str = "MB", spstr;
        float dat = size / (float) (1024 * 1024);
        int pos = -1;

		/*
         * if (dat > 1024) { dat = dat / 1024; str = "GB"; }
		 */

        spstr = dat + "";
        pos = spstr.indexOf(".");
        if (pos != -1 && spstr.length() > pos) {
            if ((spstr.length() - pos) > 3)
                spstr = spstr.substring(0, pos + 3);
            else
                spstr = spstr.substring(0);
        }
        spstr += str;
        return spstr;
    }

}
