package au.core.net.ftp.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.core.net.ftp.basic.FtpConstants;
import au.core.net.ftp.basic.FtpFile;
import au.core.net.ftp.basic.FtpUtils;
import au.core.net.ftp.basic.StringUtil;
import au.core.net.ftp.conf.Settings;
import au.core.net.ftp.listeneer.DefaultListener;
import au.core.net.ftp.listeneer.ListenerSupport;

/**
 * ftp command connection
 */
public class FtpConnection implements FtpConstants {
    private static boolean useStream = true;
    private SocketConnection scon;
    private ListenerSupport suport;
    private String host;
    private String osType;
    private String initCWD = null;
    private String pwd = "";
    private String crlf = null;
    private String localPath = "";
    private char typeNow = ' '; // 数据连接类型

    private int port;
    private int fileCount;
    private boolean ok = true;
    private boolean connected = false;

    /**
     * 设置host和port
     */
    public FtpConnection(String hos, int por) {
        this.host = hos;
        this.port = por;
    }

    /**
     * @param supor 设置监视器
     */
    public void setListenerSupport(ListenerSupport supor) {
        this.suport = supor;
    }

    /**
     * set local path
     */
    public void setLocalPath(String newPath) {
        localPath = newPath;
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }
    }

    /**
     * 向ftp服务器发送目录向上切换操作命令
     */
    public boolean cdup() {
        scon.send(CDUP);
        return success(POSITIVE);// FTP200_OK);
    }

    /**
     * 根据指定的用户名和密码登陆ftp远程服务器
     */
    public int login(String username, String password) {
        int status = LOGIN_OK;
        scon = new SocketConnection(host, port);
        if (scon.isThere()) {
            if (getStatusChar() != POSITIVE) {// 新连接的用户的服务已就绪,准备接收请求FTP220_SERVICE_READY)
                ok = false;
                status = OFFLINE;
            }
            scon.send(USER + " " + username);
            if (getStatusChar() != POSITIVE) {// FTP230_LOGGED_IN))
                scon.send(PASS + " " + password);
                if (success(POSITIVE)) {// FTP230_LOGGED_IN))
                } else {
                    ok = false;
                    status = WRONG_LOGIN_DATA;
                }
            }
        } else {
            ok = false;
            status = GENERIC_FAILED;
        }

        if (ok) {
            connected = true;
            this.optsUtf8();
            this.system(); // 获取ftp服务器系统类型
            // binary(); // 使用字节传输
            this.ascii();
            if (initCWD == null) {
                updatePWD();
            } else {
                chdirNoRefresh(initCWD);
            }
            // fireDirectoryUpdate(this);
            // fireConnectionInitialized(this);
        }
        // fireConnectionFailed(this, new Integer(status).toString());
        return status;
    }

    private void fireActionFinished(FtpConnection commandConnection) {
        if (this.suport != null) {
            this.suport.fireActionFinished(commandConnection);
        }
    }

    /**
     * 检测用户是否登陆成功
     */
    private boolean success(char op) {
        char tmp = this.getStatusChar();
        if (tmp == op)
            return true;
        else
            return false;
    }

    /**
     * @throws IOException get FTP server echo status
     */
    private String getEchoLine() {
        if (this.scon != null && !this.scon.isClosed()) {
            try {
                return this.scon.readEchoLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * get FTP server response coding char on first position
     */
    public char getStatusChar() {
        char status = ' ', more_line;
        String tmp = this.getEchoLine();
        if (tmp != null) {
            status = tmp.charAt(0);
            more_line = tmp.charAt(3);
            // waiting next command
            while (status != PROCEED && more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                status = tmp.charAt(0);
                more_line = tmp.charAt(3);
            }
        }
        return status;
    }

    /**
     * get right response status from FTP server
     */
    private void loop(char op) {
        char tmp;
        while (true) {
            tmp = getStatusChar();
            if (tmp == ' ')
                break;
            if (tmp == op)
                return;
        }
    }

    /**
     * according to the status char to get response line, has no match
     * return null,loop search
     */
    public String getSpecialLine(String statusStr) {
        char status = ' ', more_line;
        String tmp = this.getEchoLine();
        if (tmp != null) {
            if (tmp.startsWith(statusStr))
                return tmp;
            status = tmp.charAt(0);
            more_line = tmp.charAt(3);
            // has waiting next command
            while (status != PROCEED && more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                more_line = tmp.charAt(3);
                if (tmp.startsWith(statusStr) && more_line != MORE_LINE)
                    return tmp;
                status = tmp.charAt(0);
            }
        }
        return null;
    }

    public char getSpecialChar(char op) {
        char status = ' ', more_line;
        String tmp = this.getEchoLine();
        if (tmp != null) {
            status = tmp.charAt(0);
            more_line = tmp.charAt(3);
            if (status == op)
                return op;
            // has waiting next command
            while (status != PROCEED || more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                more_line = tmp.charAt(3);
                status = tmp.charAt(0);

                if (status == NEGATIVE)
                    return NEGATIVE;
                else if (status == NEGATIVE2)
                    return NEGATIVE2;
                else if (status == op && more_line != MORE_LINE)
                    return op;
            }
        }
        return NEGATIVE2;
    }

    public String noopLine(String statusStr) {
        char more_line;
        String tmp = this.getEchoLine();
        while (tmp != null && !tmp.startsWith(statusStr)) {
            tmp = this.getEchoLine();
        }
        if (tmp != null) {
            more_line = tmp.charAt(3);
            // has waiting next command
            while (more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                more_line = tmp.charAt(3);
                if (more_line != MORE_LINE)
                    return tmp;
            }
        }
        return tmp;
    }

    /**
     * according to the status char to get response line, has no match
     * return null
     */
    public String getSpecialLine(char op) {
        char status = ' ', more_line;
        String tmp = this.getEchoLine();
        if (tmp != null) {
            status = tmp.charAt(0);
            more_line = tmp.charAt(3);
            if (status == op)
                return tmp;
            // has waiting next command
            while (status != PROCEED || more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                more_line = tmp.charAt(3);
                status = tmp.charAt(0);

                if (status == NEGATIVE)
                    return null;
                if (status == op && more_line != MORE_LINE)
                    return tmp;
            }
        }
        return null;
    }

    public boolean checkListReturn(char op) {
        char status = ' ', more_line;
        String tmp = this.getEchoLine();
        if (tmp != null) {
            status = tmp.charAt(0);
            more_line = tmp.charAt(3);
            if (status == NEGATIVE)
                return false;
            if (status == op)
                return true;
            // has waiting next command
            while (status != PROCEED || more_line == MORE_LINE) {
                tmp = this.getEchoLine();
                if (tmp == null)
                    break;
                more_line = tmp.charAt(3);
                status = tmp.charAt(0);
                if (status == NEGATIVE)
                    return false;
                if (status == op && more_line != MORE_LINE)
                    return true;
            }
        }
        return false;
    }

    /**
     * 服务器,目录路径切换
     */
    public boolean chdir(String p) {
        boolean tmp = chdirWork(p);
        if (!tmp) {
            return false;
        } else {
            if (suport != null)
                suport.fireDirectoryUpdate(pwd);
            return true;
        }
    }

    /**
     * 设置初始化当前目录路径
     */
    public boolean chdirNoRefresh(String p) {
        if (getOsType().indexOf("OS/2") >= 0) {
            return chdirRaw(p);
        }
        p = FtpUtils.genPwdPath(p, pwd);
        return this.chdirRaw(p);
    }

    /**
     * 进入ftp服务器的指定工作目录,发送命令,解析目录
     */
    private boolean chdirWork(String p) {
        if (Settings.SAFE_MODE) {
            noop();
        }
        p = FtpUtils.parseSymlinkBack(p);
        if (getOsType().indexOf("OS/2") >= 0) {
            return chdirRaw(p);
        }
        try {
            p = FtpUtils.genPwdPath(p, pwd);
            if (!chdirRaw(p))
                return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        updatePWD();
        return true;
    }

    /**
     * 注销,退出用户会话
     */
    public void disconnect() {
        if (!connected)
            return;
        scon.send(QUIT);
        loop(POSITIVE);// FTP221_SERVICE_CLOSING);
        connected = false;
        scon.close();
    }

    public void close() {
        if (!connected)
            return;
        connected = false;
        scon.close();
    }

    /**
     * get current path on FTP server
     */
    public String getPWD() {
        return pwd;
    }

    /**
     * ftp connection is stop?
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * change the transfer type ,set transfer type as stream
     */
    public void modeStream() {
        if (useStream) {
            char ret = mode(STREAM);
            if (ret == NEGATIVE) {
                useStream = false;
            }
        }
    }

    /**
     * 设置模式
     */
    private char mode(char code) {
        scon.send(MODE + " " + code);
        char ret = ' ';
        try {
            ret = getStatusChar();// FTP200_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * @param offset 设置暂停时间
     */
    private void pause(int offset) {
        try {
            Thread.sleep(offset);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @throws IOException 获取数据传输端口
     */
    private int negotiatePort() throws IOException {
        String tmp = "";
        if (Settings.FTP_PASV_MODE) {
            scon.send(PASV);
            tmp = getSpecialLine(FTP227_ENTERING_PASSIVE_MODE);
            if (tmp != null && tmp.charAt(0) != NEGATIVE) {
                return FtpUtils.getPasvPort(tmp);
            }
        }
        tmp = FtpUtils.getActivePortCmd(scon.getLocalAddress());
        scon.send(tmp);
        getSpecialLine(FTP200_OK);
        return FtpUtils.getActivePort();
    }

    /**
     * @return 获取当前使用的传输类型
     */
    public char getTypeNow() {
        return typeNow;
    }

    /**
     * make directory on FTP server
     */
    public boolean mkdir(String dirName) {
        scon.send(MKD + " " + dirName);
        boolean ret = success(POSITIVE);
        return ret;
    }

    public char mkdirEl(String dirName) {
        scon.send(MKD + " " + dirName);
        char tmp = this.getStatusChar();
        // boolean ret = success(POSITIVE);
        return tmp;
    }

    /**
     * remove file or directory on FTP server
     */
    public int removeFileOrDir(String file) {
        if (file == null)
            return 0;
        if (file.trim().equals(".") || file.trim().equals("..")) {
            System.out
                    .println("ERROR: Catching attempt to delete . or .. directories");
            return GENERIC_FAILED;
        }
        if (file.endsWith("/")) {
            scon.send(RMD + " " + file);
        } else {
            scon.send(DELE + " " + file);
        }
        if (success(POSITIVE)) {// FTP250_COMPLETED))
            return REMOVE_SUCCESSFUL;
        } else {
            return REMOVE_FAILED;
        }
    }

    /**
     * chang the file name on FTP server
     */
    public boolean rename(String from, String to) {
        scon.send("RNFR " + from);
        String echoLine = this.getSpecialLine(RC350);
        if (echoLine != null) {
            scon.send("RNTO " + to);
            if (success(POSITIVE)) {// FTP250_COMPLETED))
                return true;
            }
        }
        return false;
    }

    /**
     * @return 获取ftp服务器是 操作系统类型
     */
    public String system() { // possible responses 215, 500, 501, 502, and
        scon.send(SYST);
        String response = this.getSpecialLine(POSITIVE);// FTP215_SYSTEM_TYPE);
        if (response != null) {
            setOsType(response.substring(4));
        } else {
            setOsType("UNIX");
        }
        return response;
    }

    /**
     * @param os 设置操作系统的类型
     */
    private void setOsType(String os) {
        osType = os.toUpperCase();
    }

    /**
     * 告诉ftp服务器,要使用binary的数据连接
     */
    public void binary() { // possible responses 200, 500, 501, 504, 421 and
        type(BINARY);
    }

    /**
     * 告诉ftp服务器,ascii
     */
    public void ascii() {
        type(ASCII);
    }

    public void optsUtf8() {
        scon.send(OPTS_UTF8_ON);
        boolean flag = checkListReturn(POSITIVE);
        if (flag) {
            scon.useUtf8Wrapper();
            useUTF8 = true;
        }
    }

    boolean useUTF8 = false;

    /**
     * @param code
     * @return 向ftp服务器发送连接类型的命令
     */
    public boolean type(char code) {
        scon.send(TYPE + " " + code);
        String line = getSpecialLine("200");
        // char tmp = this.getStatusChar();// FTP200_OK);
        if (line != null) {// FTP200_OK))
            typeNow = code;
            return true;
        }
        return false;
    }

    /**
     * Try to abort the transfer.
     */
    public void abort() {
        scon.send(ABOR);
        char tmp = getStatusChar(); // 226
        if (tmp == POSITIVE)
            return;
    }

    /**
     * @return 返回操作系统类型
     */
    public String getOsType() {
        return osType;
    }

    /**
     * updates PWD
     */
    private void updatePWD() {
        scon.send(PWD);
        String tmp = this.getSpecialLine(POSITIVE);// FTP257_PATH_CREATED);
        String x1 = FtpUtils.checkPath(tmp);
        if (tmp == null)
            return;
        pwd = x1;
    }

    /**
     * 什么操作都不做,只是强制清空缓冲区
     */
    public void noop() {
        scon.send(NOOP);
        this.getStatusChar();
    }

    /**
     * @param dirName
     * @return 使用未解析过的path, 设置ftp工作目录
     */
    public boolean chdirRaw(String dirName) {
        scon.send(CWD + " " + dirName);
        return success(POSITIVE);// FTP250_COMPLETED);
    }

    public boolean deleteFolder(String path) {
        String name = path;
        int idx = path.lastIndexOf('/');
        if (idx != -1) {
            name = path.substring(idx + 1);
            if (idx != 0)
                this.chdir(path.substring(0, idx));
        }
        scon.send(RMD + " " + name); // DELE
        return success(POSITIVE);// FTP250_COMPLETED);
    }

    public boolean deleteFile(String path) {
        String name = path;
        int idx = path.lastIndexOf('/');
        if (idx != -1) {
            name = path.substring(idx + 1);
            if (idx != 0)
                this.chdir(path.substring(0, idx));
        }
        scon.send(DELE + " " + name); // DELE
        return success(POSITIVE);// FTP250_COMPLETED);
    }

    /**
     * get file length from FTP server
     */
    public long size(String file) {
        scon.send("SIZE " + file);
        String line = this.getSpecialLine(RC213);// getLine(POSITIVE);
        if (line != null) {
            String si = line.substring(4);
            return Long.parseLong(si);
        }
        return -1l;
    }

    /**
     * get line splitor
     */
    public String getCRLF() {
        return crlf;
    }

    /**
     * @param file The file to upload
     * @return An integer return code
     */
    public int upload(String file, boolean enableResume) {
        return upload(file, null, enableResume);
    }

    /**
     * upload file
     */
    public int upload(String file, String realName, boolean enableResume) {
        return rawUpload(file, realName, enableResume);
    }

    private int rawUpload(String file) {
        return rawUpload(file, null, false);
    }

    /**
     * correct local path
     */
    private String correctPath(String file) {
        String path = file;
        if (FtpUtils.isRelative(file))
            path = localPath + file;
        return path;
    }

    /**
     * correct local path
     */
    private String correctFtpFile(String file) {
        if (file == null)
            return null;
        String filePath = FtpUtils.getPath(file);
        String fileName = FtpUtils.getFileName(file);
        if (!"".equals(filePath)) {
            if (!this.chdirNoRefresh(filePath)) {
                this.mkdir(filePath);
                this.chdirNoRefresh(filePath);
            }
        }
        return fileName;
    }

    /**
     * get file resume position
     */
    private long getResumePosition(File f, String storeName) {
        long uploadedSize = this.size(storeName);
        if (uploadedSize == -1l) {
            return -2l;
        }
        if (f.length() == uploadedSize)
            return -1l;
        else if (uploadedSize < f.length())
            return uploadedSize;
        return -2l;
    }

    /**
     * uploads a file
     */
    private int rawUpload(String filePath, String realName, boolean enableResume) {
        String fileName, storeName;
        filePath = correctPath(filePath);
        fileName = FtpUtils.getFileName(filePath);
        if (fileName == null || fileName.equals("")) {
            System.out.println("Can't find source file name ! ");
            return TRANSFER_FAILED;
        }
        realName = correctFtpFile(realName);
        if (realName != null) {
            storeName = realName;
        } else {
            storeName = fileName;
        }
        File srcfile = new File(filePath);
        if (!srcfile.exists())
            return TRANSFER_STOPPED;
        try {
            int port = 0;
            long position = 0;
            modeStream();
            port = negotiatePort();
            System.out.println("  [Port: " + port);
            if (enableResume) {// resume
                position = this.getResumePosition(srcfile, storeName);
                // System.out.println("Position:" + position);
                if (position > 0) {
                    String size = String.valueOf(position);
                    scon.send(REST + " " + size);
                    char state = this.getStatusChar();
                    if (state != PROCEED)
                        enableResume = false;
                } else if (position == -1) {
                    return TRANSFER_SUCCESSFUL;
                } else
                    enableResume = false;
            }
            DataConnection dcon = new DataConnection(host, port,
                    DataConnection.PUT, filePath, enableResume, position,
                    this.suport);
            // dcon.setListenerSupport(this.suport);
            // new DataConnection(this, p, host, path, dataType, resume,
            // uploadedSize, in);
            while (!dcon.isThere()) {
                pause(10);
            }
            scon.send(STOR + " " + storeName);
            char tmp = this.getSpecialChar(POSITIVE);
            if (tmp == NEGATIVE || tmp == NEGATIVE2) {
                dcon.close();
                return PERMISSION_DENIED;
            }
            while (!dcon.isFinished()) {
                pause(10);
            }
            if (!dcon.isOk()) {
                this.suport.fireConnectionFailed("dd");
                return TRANSFER_FAILED;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString() + " @FtpConnection::upload");
            return TRANSFER_FAILED;
        }
        this.suport.fireActionFinished(this);
        return TRANSFER_SUCCESSFUL;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    String currentPath;

    /**
     * <p>
     * Description: upload a directory
     * </p>
     *
     * @param dir
     * @return
     * @author Yzc
     */
    public int uploadDir(String dir) {
        dir = dir.replace(File.separatorChar, '/');
        if (dir.endsWith("\\")) {
            System.out.println("directory error ! ");
            return -1;
        }
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        fileCount = 0;
        if (FtpUtils.isRelative(dir)) {
            dir = localPath + dir;
        }
        // String single = FtpUtils.getLastPath(dir);
        // String remoteDir = this.getPWD() + single; //
        // StringUtils.removeStart(dir,path);
        String oldDir = this.getPWD();
        if (Settings.SAFE_MODE) {
            noop();
        }
        doUploadDir(dir, oldDir);
        chdirNoRefresh(oldDir);
        return 0;
    }

    public int uploadSubDir(String dir) {
        if (FtpUtils.isRelative(dir))
            dir = localPath + dir;
        File dirFile = new File(dir);
        if (!dirFile.exists())
            return -3;
        fileCount = 0;
        String oldDir = this.getPWD();
        if (Settings.SAFE_MODE)
            noop();
        File[] tmp = dirFile.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (!connected)
                return -2;
            if (tmp[i].isDirectory())
                doUploadDir(tmp[i].getPath(), oldDir);
            else {
                fileCount++;
                currentPath = tmp[i].getPath();
                if (rawUpload(tmp[i].getPath()) < 0)
                    break;
            }
        }
        chdirNoRefresh(oldDir);
        return 0;
    }

    private int doUploadDir(String dir, String parentDir) {
        dir = dir.replace(File.separatorChar, '/');
        if (dir.endsWith("\\")) {
            System.out.println("directory error ! ");
            return -1;
        }
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }

        String single = FtpUtils.getLastPath(dir);
        if (single.charAt(0) == '/')
            single = single.substring(1);
        String remoteDir = parentDir + single;
        char st = mkdirEl(remoteDir);
        // boolean successful = mkdir(remoteDir);
        if (st != '2' && st != '5')
            return -3;
        // if (!successful) {
        // return -3;
        // }
        if (Settings.SAFE_MODE) {
            noop();
        }
        chdirNoRefresh(remoteDir);
        File f2 = new File(dir);
        String[] tmp = f2.list();
        for (int i = 0; i < tmp.length; i++) {
            String res = dir + tmp[i];
            File f3 = new File(res);
            if (f3.isDirectory()) {
                if (!connected)
                    return -2;
                doUploadDir(res, remoteDir);
            } else {
                if (!connected) {
                    return -2;
                }
                fileCount++;
                currentPath = res;
                if (rawUpload(res) < 0)
                    break;
            }
        }
        return 0;
    }

    public int getFileCount() {
        return fileCount;
    }

    /**
     * down load file
     */
    public int download(String srcfile, String fileName, boolean enableResume) {
        int stat;
        stat = rawDownload(srcfile, fileName, enableResume);
        return stat;
    }

    public String sendCmd(String cmd) {
        this.scon.send(cmd);
        try {
            return this.scon.readEchoLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private short fillItem(FtpFile file, String tmp, int index) {
        switch (index) {
            case 0:
                if (tmp.charAt(0) == 'd')
                    file.setDirectory(true);
                break;
            case 1:
                file.setSubfiles(Integer.parseInt(tmp));
            case 2:
            case 3:
                break;
            case 4:
                if (!file.isDirectory())
                    file.setSize(Long.parseLong(tmp));
                break;
            case 5:
            case 6:
                return SKIP;
            case 7:
                file.setDate(tmp);
                return BREAK;
            case 8:
                file.setName(tmp);
                break;
        }
        return NORMAL;
    }

    static final short SKIP = 0;
    static final short NORMAL = -1;
    static final short BREAK = 1;

    private FtpFile parseLine(String line) {
        char arr[] = line.toCharArray();
        char cha;
        short flag = NORMAL;
        int idx = 0, i = 0, len = arr.length, start = 0, cur = 0;
        boolean isSp = false;

        FtpFile file = new FtpFile();
        file.setPath(this.getPWD());
        while (i < len) {
            cha = arr[i];
            cur = i;
            i++;
            if (cha == ' ') {
                if (isSp)
                    continue;
                flag = this.fillItem(file, line.substring(start, cur), idx++);
                isSp = true;
                continue;
            }
            if (isSp) {
                isSp = false;
                if (flag != SKIP) {
                    start = cur;
                    if (flag == BREAK) {
                        cur = len;
                        break;
                    }
                }
            }
        }
        if (start < cur)
            this.fillItem(file, line.substring(start), idx++);
        return file;
    }

    public List<FtpFile> list() {
        char oldType = 'I';
        try {
            // BufferedReader in = jcon.getReader();
            int p = 0;
            modeStream();
            oldType = getTypeNow();
            ascii();
            p = negotiatePort();
            DataConnection dcon = new DataConnection(host, p,
                    DataConnection.GET, true); // ,null);
            while (dcon.getInputStream() == null) {
                pause(10);
            }

            scon.send(LIST);

            boolean flag = checkListReturn(POSITIVE);
            if (!flag)
                return null;
            InputStreamReader inp;
            if (useUTF8)
                inp = new InputStreamReader(dcon.getInputStream(), "UTF-8");
            else
                inp = new InputStreamReader(dcon.getInputStream());

            BufferedReader input = new BufferedReader(inp);
            // noopLine("226"); // FTP226_CLOSING_DATA_REQUEST_SUCCESSFUL);
            String line;
            List<FtpFile> list = new ArrayList<FtpFile>();
            while ((line = input.readLine()) != null) {
                System.out.println("-> " + line);
                if (!line.trim().equals("")) {
                    list.add(parseLine(line));
                    // currentListing.add(line);
                }
            }
            input.close();
            if (oldType != ASCII) {
                type(oldType);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (oldType != ASCII) {
                type(oldType);
            }
        }
        return null;
    }

    /**
     * @param temp The file to download
     * @return An InputStream
     */
    public InputStream getDownloadInputStream(String temp) {
        System.out.println("ftp stream download started:");
        String file = temp;
        try {
            int port = 0;
            file = FtpUtils.getFileName(file);
            String path = localPath + file;
            modeStream();
            port = negotiatePort();
            DataConnection dcon = new DataConnection(host, port,
                    DataConnection.GET, path, true);
            dcon.setListenerSupport(this.suport);
            while (!dcon.isThere() || !dcon.isGetStream()) {
                pause(10);
            }
            if (file == null)
                file = temp;
            scon.send(RETR + " " + file);
            return dcon.getInputStream();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void correctDPath(String filePath) {
        if (StringUtil.isNullOrEmpty(filePath) || filePath.equals("/"))
            return;

        if (filePath.charAt(0) == '/') {
            this.chdirRaw(filePath);
            this.pwd = filePath;
        }
    }

    /**
     * down load file
     */
    private int rawDownload(String file, String realName, boolean enableResume) {
        try {
            int port = 0;
            correctDPath(FtpUtils.getPath(file));
            String file1 = FtpUtils.getFileName(file);
            if (file1 != null)
                file = file1;
            if (realName == null)
                realName = file;
            String path = localPath + realName;
            modeStream();
            port = negotiatePort();
            File f = new File(path);
            if (f.exists() && enableResume) {
                long size = this.size(file);
                if (size == f.length())
                    return 0;
                scon.send(REST + " " + f.length());
                if (this.getStatusChar() != PROCEED) {
                    enableResume = false;
                }
            }
            DataConnection dcon = new DataConnection(host, port,
                    DataConnection.GET, path, enableResume, f.length(),
                    this.suport); // ,
            // dcon.setListenerSupport(this.suport);
            while (!dcon.isThere()) {
                pause(10);
            }
            scon.send(RETR + " " + file);
            char state = this.getStatusChar();
            if (state == '1')
                state = this.getStatusChar();
            if (state == NEGATIVE) {
                File f2 = new File(path);
                if (f2.exists() && (f2.length() == 0)) {
                    f2.delete();
                }
                return -2;
            } else if (state != POSITIVE && state != PROCEED) {
                return -1;
            }
            while (!dcon.isFinished()) {
                pause(10);
            }
            fireActionFinished(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static void main(String args[]) {
        // File dir = new File("D:\\test\\test\\temp");
        // for (int i = 500; i < 1500; i++) {
        // File file = new File(dir, "file" + i + ".txt");
        // try {
        // file.delete();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }

        FtpConnection con = new FtpConnection("127.0.0.1", 21);
        ListenerSupport sup = new ListenerSupport();
        sup.addConnectionListener(new DefaultListener());
        con.setListenerSupport(sup);
        con.login("admin", "admin");
        while (!con.isConnected()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        con.mkdir("/test1213/");
        // con.sendCmd("UPUDPBUF 0");
        // con.sendCmd("LIST");
        // con.chdir("/test/"); // con.send("ALLO", "/test/");
        // con.size("/aniu/test.mtv");
        int nam = con.download("/tes00t/local-lib.rar",
                "E:\\test\\local-lib.rar", false);
        // int nam = con.upload("f:\\local-lib.rar", "/tes00t/local-lib.rar",
        // false);
        if (nam == TRANSFER_STOPPED) {
            System.out.println("file is not exist");
        }
        // con.disconnect();
    }
}