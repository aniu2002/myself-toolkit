package au.server.ftpserver;

import au.server.ftpserver.log.LogLevelManager;
import au.server.ftpserver.main.FtpInstance;
import au.server.ftpserver.main.FtpServerManager;
import au.server.ftpserver.main.Starter;
import au.server.ftpserver.utils.ConfigCopyUtil;
import au.server.ftpserver.utils.PathUtil;
import org.apache.ftpserver.ftplet.FtpException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SamFtpServer {


    public String getName() {
        return "Ftp-Server";
    }

    public int postStop() {
        FtpServerManager.getInstance().stopFtpSession();
        FtpServerManager.getInstance().getFtpServer().stop();
        return 0;
    }

    public void postStart(Map<String, Object> arguments) {
        Iterator<Map.Entry<String, Object>> iter = arguments.entrySet()
                .iterator();
        Map.Entry<String, Object> entry;
        while (iter.hasNext()) {
            entry = iter.next();
            System.setProperty(entry.getKey(), entry.getValue().toString());
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        Map<String, Object> nmp = new HashMap<String, Object>();
        ConfigCopyUtil.configureFileSetup(Starter.class);
        System.setProperty("log4j.configuration", PathUtil.getLogPath());
        LogLevelManager.getInstance().getLogLevel().startLog();
        FtpInstance ftpInstance = FtpServerManager.getInstance().getFtpServer();
        try {
            ftpInstance.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

}
