package com.sparrow.app.shell;

import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SingleAppStarter extends Thread {
    private Process process;
    private String appName;
    private String appHome;
    private String webRoot;
    private int webPort;

    public SingleAppStarter(String appName, String appHome, String webRoot, int webPort) {
        this.appHome = appHome;
        this.appName = appName;
        this.webPort = webPort;
        this.webRoot = webRoot;
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("-Dweb.server.port=").append(this.webPort)
                .append(" -Dweb.root.path=").append(this.webRoot)
                .append(" -Dbean.cfg.path=").append(this.appHome + "/conf/eggs/beanConfig.xml")
                .append(" -Dprovider.cfg.path=").append(this.appHome + "/conf/eggs/providerConfig.xml")
                .append(" -Dprovider.store.path=").append(this.appHome + "/conf/data-provider.xml")
                .append(" -Dsource.config.path=").append(this.appHome + "/conf/source-config.xml")
                .append(" -Dapp.name=").append(this.appName)
                .append(" -Duse.system.props=true")
                .append(" -Dsecurity.enable=true")
                .append(" -Dadmin.pwd=123456")
                .append(" -Dconsole.host=").append(SystemConfig.getProperty("web.server.host",
                "127.0.0.1"))
                .append(" -Dconsole.port=").append(SystemConfig.getSysInt("web.server.port", 9097))
                .append(" -Dapp.home=").append(this.appHome);

        this.exec(this.appName, this.appHome, "com.sparrow.server.WebCmdServer", sb.toString(), null);
    }

    public void stopNow() {
        if (this.process != null)
            this.process.destroy();
    }

    void logParas(List<String> p) {
        if (p == null)
            return;
        Iterator<String> iterator = p.iterator();
        System.out.println(" paras : ");
        while (iterator.hasNext()) {
            System.out.println("    " + iterator.next());
        }
    }

    /**
     * 获取进程id
     *
     * @return
     */
    void exec(String appName, String appHome, String javaClass, String opts, String args) {
        List<String> list = new ArrayList<String>();
        list.add("java");
        this.setJavaClassPath(appHome, list);
        list.add("-Dfile.encoding=utf-8");
        if (StringUtils.isNotEmpty(opts))
            this.addArguments(list, opts);
        list.add(javaClass);
        this.addArguments(list, args);

        try {
            logParas(list);
            // 替换命令中占位符
            // "echo -E \"{0}\" | -e -kfile {1} -base64";
            // encryption = MessageFormat.format(encryption, data, commonKey);
            // Process process = Runtime.getRuntime().exec(shellPath, args,
            // null);
            File file = new File(appHome, "logs");
            if (!file.exists())
                file.mkdirs();
            ProcessBuilder pb = new ProcessBuilder(list).directory(new File(appHome));
            this.process = pb.start();
            OutputStream out = this.getLogOutStream(appHome + "/logs/out.log");
            OutputStream err = this.getLogOutStream(appHome + "/logs/error.log");
            this.capture(this.process, err, out);
            // n==0 成功 n==1失败
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    int capture(Process process, OutputStream err, OutputStream out) throws InterruptedException {
        // process.getOutputStream()
        StreamErrGobbler errorGobbler = new StreamErrGobbler(
                process.getErrorStream(), "ERROR", out, err);
        errorGobbler.setDaemon(true);
        // kick off stderr
        errorGobbler.start();
        // StreamOutGobbler outGobbler = new StreamOutGobbler(
        // process.getInputStream(), "STDOUT", out);
        StreamOutGobbler outGobbler = new StreamOutGobbler(
                process.getInputStream(), "STDOUT", out);

        outGobbler.setDaemon(true);
        // kick off stdout
        outGobbler.start();

        try {
            int n = process.waitFor();
            // int n = process.exitValue();
            int nn = process.exitValue();
            if (nn == 130)
                return -110;
            else
                return n;
        } finally {
            process.destroy();
            errorGobbler.stopNow();
            outGobbler.stopNow();
        }
    }

    OutputStream getLogOutStream(String logPath) {
        try {
            return new FileOutputStream(logPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return System.out;
    }

    void addArguments(List<String> list, String str) {
        if (StringUtils.isEmpty(str))
            return;
        String args[] = StringUtils.tokenizeToStringArray(str, " ");
        if (args != null && args.length > 0) {
            for (String arg : args)
                list.add(arg);
        }
    }

    protected void setJavaClassPath(String appHome, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("java.class.path"));
        File conf = new File(appHome, "conf");
        if (conf.exists())
            sb.append(File.pathSeparatorChar).append(conf.getPath());
        // this.generateDepPath(appHome + "/conf", sb);
        File clazzDir = new File(appHome, "classes");
        if (clazzDir.exists())
            sb.append(File.pathSeparatorChar).append(clazzDir.getPath());
        this.generateDepPath(appHome + "/libs", sb);
        String s = sb.toString();
        args.add("-cp");
        args.add(s);
    }

    protected void generateDepPath(String path, StringBuilder sb) {
        if (!StringUtils.isEmpty(path)) {
            String jars[] = StringUtils.tokenizeToStringArray(path, ";");
            for (String jar : jars) {
                String suffix = PathResolver.getExtension(jar);
                String pt = jar;
                if ("zip".equals(suffix))
                    pt = PathResolver.trimExtension(jar);
                File f = new File(pt);
                if (f.exists())
                    this.buildPath(f, sb);
            }
        }
    }

    /**
     * build path for class path
     *
     * @param libs
     * @param sb
     */
    protected void buildPath(File libs, StringBuilder sb) {
        if (!libs.exists())
            return;
        if (libs.isDirectory()) {
            File files[] = libs.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("jar");
                }
            });
            for (File file : files) {
                sb.append(File.pathSeparatorChar).append(file.getPath());
            }
        } else {
            sb.append(File.pathSeparatorChar).append(libs.getPath());
        }
    }
}
