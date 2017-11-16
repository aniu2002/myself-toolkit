package com.szl.icu.miner.tools.test;

/**
 * Created by Administrator on 2016/11/4.
 */
public class DataMessages {
    class FtpInfo {
        int port;
        String server;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }
    }

    class Status {
        int status;
        String msg;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    class MyRequest {
        String name;
        String sex;
        FtpInfo ftpInfo;

        public FtpInfo getFtpInfo() {
            return ftpInfo;
        }

        public void setFtpInfo(FtpInfo ftpInfo) {
            this.ftpInfo = ftpInfo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }

    class SimpleRequest {
        String name;
        String sex;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }

    class MyResponse {
        String name;
        String sex;
        Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}
