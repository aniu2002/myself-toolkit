package com.sparrow.weixin.common;

import com.sparrow.core.utils.FileIOUtil;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.httpclient.CrawlHttp;
import com.sparrow.httpclient.HttpReq;
import com.sparrow.httpclient.HttpResp;
import com.sparrow.weixin.entity.BaseResponse;
import com.sparrow.weixin.entity.SnsUserInfo;
import com.sparrow.weixin.entity.SnsUserList;
import com.sparrow.weixin.entity.UserInfoList;
import com.sparrow.weixin.httpcli.HttpRequester;
import com.sparrow.weixin.httpcli.HttpResponse;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanzc on 2015/6/1.
 */
public class WeiXinTool {
    static final String JSON_CONTENT_TYPE = "application/json";
    static final Map<String, String> headers;
    static String accessToken;
    static final Object syn = new Object();

    static X509TrustManager x509m = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    };

    static {
        headers = new HashMap<String, String>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
    }

    public static final String getToken() {
        if (accessToken == null)
            synchronized (syn) {
                if (accessToken == null)
                    accessToken = doGetToken();
            }
        return accessToken;
    }

    static final String reGetToken() {
        synchronized (syn) {
            accessToken = doGetToken();
        }
        return accessToken;
    }

    static final String doGetToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + SystemConfig.getProperty("weixin.appid") + "&secret=" + SystemConfig.getProperty("weixin.secret");
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                BaseResponse response = JsonMapper.readJson(resp.getHtml(), BaseResponse.class);
                if (response.getErrcode() == 0)
                    return response.getAccess_token();
                else
                    throw new RuntimeException("调用微信接口异常：" + response.getErrcode() + "-" + response.getErrmsg());
            }
            throw new RuntimeException("调用微信接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCodeRequest(String action) {
        if (StringUtils.isEmpty(action))
            action = "ft";
        String url = null;
        try {
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + SystemConfig.getProperty("weixin.appid")
                    + "&redirect_uri=" +
                    java.net.URLEncoder.encode(SystemConfig.getProperty("weixin.code.url"), "utf-8") + action
                    + "&response_type=code&scope=" + SystemConfig.getProperty("weixin.code.scope")
                    + "&state=1233#wechat_redirect";
            //scope   snsapi_base / snsapi_userinfo
            // snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
            // snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String[] getUserList() {
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + getToken();
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                System.out.println(resp.getHtml());
                SnsUserList list = JsonMapper.readJson(resp.getHtml(), SnsUserList.class);
                return list.getData().getOpenid();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setUserRemark(String openId, String remark) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=" + getToken();
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
        String content = "{\"openid\":\"" + openId + "\",\"remark\":\"" + remark + "\"}";
        request.setBody(content, JSON_CONTENT_TYPE);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                BaseResponse response = JsonMapper.readJson(resp.getHtml(), BaseResponse.class);
                System.out.println(response.getErrcode() + " -- " + response.getErrmsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final BaseResponse getAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + SystemConfig.getProperty("weixin.appid") + "&secret=" + SystemConfig.getProperty("weixin.secret") + "&code=" + code + "&grant_type=authorization_code";
        //  String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + SysConfig.getProperty("weixin.appid") + "&secret=" + SysConfig.getProperty("weixin.secret");
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                BaseResponse response = JsonMapper.readJson(resp.getHtml(), BaseResponse.class);
                if (response.getErrcode() == 0)
                    return response;
                else
                    throw new RuntimeException("调用微信getAccessToken接口异常：" + response.getErrcode() + "-" + response.getErrmsg());
            }
            throw new RuntimeException("调用微信getAccessToken接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getUserInfo(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                return resp.getHtml();
            }
            throw new RuntimeException("调用微信getUserInfo接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SnsUserInfo[] batchGetUser(String openIds[]) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=" + getToken() + "&lang=zh_CN";
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
        StringBuilder sb = new StringBuilder("{\"user_list\": [");
        boolean isFirst = true;
        for (String openId : openIds) {
            if (isFirst)
                isFirst = false;
            else
                sb.append(",");
            sb.append("{\"openid\": \"").append(openId).append("\",\"lang\": \"zh-CN\"}");
        }
        sb.append("]}");
        String content = sb.toString();
        System.out.println("content : " + content);
        request.setBody(content, JSON_CONTENT_TYPE);

        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                System.out.println("result : " + resp.getHtml());
                UserInfoList list = JsonMapper.readJson(resp.getHtml(), UserInfoList.class);
                return list.getUser_info_list();
            }
            throw new RuntimeException("调用微信getUserInfo接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUserInfo(String openId) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + getToken() + "&openid=" + openId + "&lang=zh_CN";
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                return resp.getHtml();
            }
            throw new RuntimeException("调用微信getUserInfo接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSnsUserInfo(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                return resp.getHtml();
            }
            throw new RuntimeException("调用微信getUserInfo接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final boolean configMenu() {
        String token = getToken();
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token;
        String content = FileIOUtil.readString("classpath:weixin/menu.json");
        CrawlHttp http = new CrawlHttp(false, false, true);
        HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
        request.setBody(content, JSON_CONTENT_TYPE);
        HttpResp resp;
        try {
            resp = http.execute(request);
            if (resp.getStatus() == 200) {
                BaseResponse response = JsonMapper.readJson(resp.getHtml(), BaseResponse.class);
                int n = checkAndResetToken(response);
                // 重新获取token后再执行一次
                if (n == -2)
                    return configMenu();
                else if (n == 0)
                    return true;
                else {
                    System.out.println(response.getErrcode() + " -- " + response.getErrmsg());
                    return false;
                }
            }
            throw new RuntimeException("调用微信接口异常：" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final boolean configMenux() {
        String token = getToken();
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token;
        String content = FileIOUtil.readString("classpath:weixin/menu.json");
        try {
            HttpResponse httpResponse = HttpRequester.sendPost(url, content);
            System.out.println(httpResponse.getHtml());
            if (httpResponse.getStatus() == 200) {
                BaseResponse baseResponse = JsonMapper.readJson(httpResponse.getHtml(), BaseResponse.class);
                int n = checkAndResetToken(baseResponse);
                // 重新获取token后再执行一次
                if (n == -2)
                    return configMenu();
                else if (n == 0)
                    return true;
                else
                    return false;
            } else
                throw new RuntimeException(httpResponse.getStatus() + ":" + httpResponse.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static int checkAndResetToken(BaseResponse baseResponse) {
        if (baseResponse.getErrcode() == 40014 || baseResponse.getErrcode() == 42001) {
            reGetToken();
            return -2;
        } else if (baseResponse.getErrcode() == 0)
            return 0;
        return baseResponse.getErrcode();
    }

    public static void sslSocket2() throws Exception {
        SSLContext context = SSLContext.getInstance("SSL");
        // 初始化
        context.init(null,
                new TrustManager[]{x509m},
                new SecureRandom());
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket s = (SSLSocket) factory.createSocket("localhost", 10002);
        System.out.println("ok");

        OutputStream output = s.getOutputStream();
        InputStream input = s.getInputStream();

        output.write("alert".getBytes());
        System.out.println("sent: alert");
        output.flush();

        byte[] buf = new byte[1024];
        int len = input.read(buf);
        System.out.println("received:" + new String(buf, 0, len));
    }

    static void doSendX() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            // 初始化
            context.init(null,
                    new TrustManager[]{x509m},
                    new SecureRandom());
            SSLSocketFactory factory = context.getSocketFactory();
            SSLSocket s = (SSLSocket) factory.createSocket("10.28.3.62", 8443);

            String line = "\r\n";
            //  socket.
            StringBuilder sb = new StringBuilder();
            sb.append("GET /searcher-manage/search/direct/searchAppGoods HTTP/1.1").append(line);

            sb.append("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").append(line);
            sb.append("Accept-Encoding:gzip,deflate").append(line);
            sb.append("Accept-Language:zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3").append(line);
            // sb.append("Q-Refer:030000").append(line);
            sb.append("Connection:keep-alive").append(line);
            // sb.append("Accept-Encoding:gzip,deflate").append(line).append(line);
            sb.append("Cookie:JSESSIONID=qc5et2rzb4padgp0j0c34m3h").append(line);
            sb.append("Host:10.28.3.222:8443").append(line);
            sb.append("User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0").append(line);
            sb.append(line);

            InputStream ins = s.getInputStream();
            OutputStream ops = s.getOutputStream();

            ops.write(sb.toString().getBytes());
            // ops.close();

            byte b[] = new byte[8192];
            int len = ins.read(b);
            System.out.println(len);
            System.out.println(new String(b, 0, len));
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    static void doSendY(boolean ssl) {
        try {
            Socket socket;
            if (ssl)
                socket = new Socket("10.28.3.62", 8443);
            else
                socket = new Socket("10.28.3.62", 9090);
            System.out.println(socket.getRemoteSocketAddress().toString());
            String line = "\r\n";
            //  socket.
            StringBuilder sb = new StringBuilder();
            sb.append("GET /searcher-manage/search/direct/searchAppGoods HTTP/1.1").append(line);

            sb.append("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").append(line);
            sb.append("Accept-Encoding:gzip,deflate").append(line);
            sb.append("Accept-Language:zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3").append(line);
            // sb.append("Q-Refer:030000").append(line);
            sb.append("Connection:keep-alive").append(line);
            // sb.append("Accept-Encoding:gzip,deflate").append(line).append(line);
            sb.append("Cookie:JSESSIONID=qc5et2rzb4padgp0j0c34m3h").append(line);
            sb.append("Host:10.28.3.62:").append((ssl ? 8443 : 9090)).append(line);
            sb.append("User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0").append(line);
            sb.append(line);

            InputStream ins = socket.getInputStream();
            OutputStream ops = socket.getOutputStream();

            ops.write(sb.toString().getBytes());
            // ops.close();

            byte b[] = new byte[8192];
            int len = ins.read(b);
            System.out.println(len);
            System.out.println(new String(b, 0, len));
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void doSendIOS() {
        try {
            Socket socket = new Socket("weixin.qq.com", 80);
            System.out.println(socket.getRemoteSocketAddress().toString());
            String line = "\r\n";
            //  socket.
            StringBuilder sb = new StringBuilder();
            sb.append("GET /r/MUxYQAvExFa7rU8j9xkj HTTP/1.1").append(line);
            sb.append("Host: weixin.qq.com").append(line);
            sb.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").append(line);
            sb.append("Accept-Language: zh-cn").append(line);
            sb.append("Connection: keep-alive").append(line);
            //sb.append("Q-Refer:030000").append(line);
            sb.append("Accept-Encoding: gzip,deflate").append(line);
            sb.append("User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 8_1_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12B440 Safari/600.1.4").append(line);
            sb.append(line);

            InputStream ins = socket.getInputStream();
            OutputStream ops = socket.getOutputStream();

            String s = sb.toString();
            System.out.println(s);
            ops.write(s.getBytes());
            // ops.close();

            byte b[] = new byte[2048];
            int len = ins.read(b);
            System.out.println(new String(b, 0, len));
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void doSendAndroid() {
        try {
            Socket socket = new Socket("weixin.qq.com", 80);
            System.out.println(socket.getInetAddress().toString());
            String line = "\r\n";
            //  socket.
            StringBuilder sb = new StringBuilder();
            sb.append("GET /r/MUxYQAvExFa7rU8j9xkj HTTP/1.1").append(line);
            sb.append("Accept-Language: zh-CN").append(line);
            sb.append("User-Agent: Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.8 Mobile Safari/537.36").append(line);
            sb.append("Q-Refer:030000").append(line);
            sb.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").append(line);
            sb.append("Accept-Charset: utf-8, iso-8859-1, utf-16, *;q=0.7").append(line);
            sb.append("Accept-Encoding: gzip").append(line);
            sb.append("Connection: keep-alive").append(line);
            sb.append("Host: ").append(socket.getInetAddress().getHostAddress()).append(line);
            sb.append(line);

            InputStream ins = socket.getInputStream();
            OutputStream ops = socket.getOutputStream();

            String s = sb.toString();
            System.out.println(s);
            ops.write(s.getBytes());
            // ops.close();

            byte b[] = new byte[2048];
            int len = ins.read(b);
            System.out.println(new String(b, 0, len));
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String doSendWX(String s) {
        String ss = null;
        try {
            Socket socket = new Socket("weixin.qq.com", 80);
            System.out.println(socket.getRemoteSocketAddress().toString());
            String line = "\r\n";
            //  socket.

            InputStream ins = socket.getInputStream();
            OutputStream ops = socket.getOutputStream();

            ops.write(s.toString().getBytes());
            // ops.close();

            byte b[] = new byte[8192];
            int len = ins.read(b);
            ss = new String(b, 0, len);
            System.out.println(ss);
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ss;
    }

    static void doMessage() {
        String url = "http://wechat.com/cgi-bin/readtemplate?t=market_redirect";
        CrawlHttp http = new CrawlHttp(true, true);
        Map<String, String> headers = new HashMap<String, String>();

        // headers.put("Host", "10.28.3.157");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-cn");
        // headers.put("Connection", "keep-alive");
        headers.put("Q-Refer", "030000");
        //headers.put("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
        // headers.put("User-Agent","Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; Lenovo S820 Build/JDQ39) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.8 Mobile Safari/537.36");
        // headers.put("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate");
        HttpReq request = new HttpReq(url, "GET", "utf-8", headers);
        //  request.setParaStr("action=confMenu");
        HttpResp resp;
        try {
            resp = http.execute(request);
            System.out.println("调用接口：" + resp.getStatus() + "-" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void confMenu() {
        String url = "http://127.0.0.1:9097/weixin/config";
        CrawlHttp http = new CrawlHttp();
        HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
        request.setParaStr("action=confMenu");
        HttpResp resp;
        try {
            resp = http.execute(request);
            System.out.println("调用接口：" + resp.getStatus() + "-" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void synUsers() {
        String url = "http://127.0.0.1:9097/weixin/config";
        CrawlHttp http = new CrawlHttp();
        HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
        request.setParaStr("action=syn");
        HttpResp resp;
        try {
            resp = http.execute(request);
            System.out.println("调用接口：" + resp.getStatus() + "-" + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String renderString(String s) {
        BufferedReader reader = new BufferedReader(new StringReader(s));
        StringBuilder sb = new StringBuilder();
        String CL = "\r\n";
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Host:")) {
                    sb.append("Host:").append("weixin.qq.com").append(CL);
                    continue;
                }
                sb.append(line).append(CL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    static void doListen() {
        try {
            Socket socket = new ServerSocket(80).accept();
            InputStream ins = socket.getInputStream();
            OutputStream ops = socket.getOutputStream();
            byte b[] = new byte[2048];
            int len = ins.read(b);
            System.out.println(socket.getInetAddress().getHostAddress());
            String s = renderString(new String(b, 0, len));
            System.out.println(s);
            s = doSendWX(s);
            ops.write(s.getBytes());
            ins.close();
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        //  doSendIOS();
        //doSendAndroid();
        //confMenu();
        doSendY(false);
        doSendX();
        // batchGetUser(getUserList());
        // System.out.println(getUserList().length);
        // doListen();
    }
}

