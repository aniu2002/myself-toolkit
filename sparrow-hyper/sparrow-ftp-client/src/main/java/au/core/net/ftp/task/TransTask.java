package au.core.net.ftp.task;

import au.core.net.ftp.connection.FtpConnection;
import au.core.net.ftp.listeneer.ConnectionListener;
import org.apache.commons.lang3.StringUtils;

public class TransTask implements ConnectionListener {
    private static int couter = 1;
    private TransListener listener;
    private String guid;
    private int percent = 1;
    private String srcPath;
    private String distPath;
    private String lastMessage = null;
    private String speed;
    private boolean upload = true;
    private long start = 0l;
    private long endTime = 0l;
    private long complete;
    private long total;
    private double seconds = 0l;
    private boolean finished;
    private boolean failed;

    public TransTask() {
        this.guid = "2002" + String.valueOf(couter++);
    }

    public void setListener(TransListener listener) {
        this.listener = listener;
    }

    public String getGuid() {
        return guid;
    }

    public int getPercent() {
        return percent;
    }

    @Override
    public void updateRemoteDirectory(String pwd) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateProgress(String file, String type, long bytes) {
        if (bytes == -1)
            return;
        this.complete += bytes;
        long nowTime = System.currentTimeMillis();
        long offset = nowTime - this.endTime;
        // System.out.println("offset:" + offset + "  bytes:" + bytes);
        float seconds = offset / 1000.0f;
        double m = bytes / (1024.0 * 1024);
        // System.out.println("offset:" + seconds + "  bytes:" + m);
        double m1 = m / seconds;
        m1 = Math.round(m1 * 100) / 100.0;
        this.speed = m1 + "m/s";
        // System.out.println("=========" + this.speed);
        this.updatePercent(this.complete, this.total);
        if (this.listener != null)
            this.listener.notice(TransListener.RUN, this);
        this.endTime = nowTime;
    }

    void updateSpeed(long bytes) {
        long offset;
        if (this.endTime < 1000)
            offset = 1000;
        else {
            long nowTime = System.currentTimeMillis();
            offset = nowTime - this.endTime;
            this.endTime = nowTime;
        }
        if (offset < 1000)
            offset = 1000;
        float seconds = offset / 1000.0f;
        double m = bytes / (1024.0 * 1024);
        // System.out.println("offset:" + seconds + "  bytes:" + m);
        double m1 = m / seconds;
        m1 = Math.round(m1 * 100) / 100.0;
        this.speed = m1 + "m/s";
    }

    public String getSpeed() {
        return speed;
    }

    void updatePercent(long completed, long total) {
        if (this.total == 0)
            return;
        this.percent = (int) (completed * 100.0 / total);
        this.upateSize(completed, total);
    }

    @Override
    public void connectionInitialized() {
        this.start = this.endTime = System.currentTimeMillis();
        if (this.listener != null)
            this.listener.notice(TransListener.STARTED, this);
    }

    void upateSize(long completed, long total) {
        String str = getMbString(completed) + "/" + getMbString(total);
        this.lastMessage = str;
    }

    private String getMbString(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < mbSize) {
            int dat = (int) (size / kbSize);
            return dat + "KB";
        } else if (size < gbSize) {
            double dat = size / mbSize;
            dat = Math.round(dat * 100) / 100.0;
            return dat + "MB";
        } else {
            double dat = size / gbSize;
            dat = Math.round(dat * 100) / 100.0;
            return dat + "GB";
        }
    }

    static float gbSize = 1024 * 1024 * 1024;
    static float mbSize = 1024 * 1024;
    static int kbSize = 1024;

    @Override
    public void connectionFailed(String why) {
        this.failed = true;
        this.lastMessage = why;
        if (this.listener != null)
            this.listener.notice(TransListener.FAILURE, this);
    }

    @Override
    public void actionFinished(FtpConnection connection) {
        this.endTime = System.currentTimeMillis();
        long offset = this.endTime - this.start;
        this.seconds = offset / 1000.0;
        // System.out.println("total seconds:" + this.seconds + "(s)");
        this.upateSize(total, total);
        if (StringUtils.isEmpty(this.speed))
            this.updateSpeed(total);
        this.percent = 100;
        this.finished = true;
        connection.disconnect();
        if (this.listener != null)
            this.listener.notice(TransListener.FINISHED, this);
        // TransferJob.remove(this);
    }

    public long getStart() {
        return start;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public double getSeconds() {
        return seconds;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDistPath() {
        return distPath;
    }

    public void setDistPath(String distPath) {
        this.distPath = distPath;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getComplete() {
        return complete;
    }

}
