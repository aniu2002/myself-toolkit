package com.sparrow.supports.message.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.httpclient.CrawlHttp;
import com.sparrow.httpclient.HttpReq;
import com.sparrow.httpclient.HttpResp;
import com.sparrow.supports.message.MessageSender;
import com.sparrow.supports.message.UserMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-22 Time: 上午10:12 To change
 * this template use File | Settings | File Templates.
 */
public class SmsMessageSender extends MessageSender {
	static final Map<String, String> headers;
	static final String cdKey = "9SDK-EMY-0229-JCSOO";
	static final String password = "319390";
	static final Properties props = PropertiesFileUtil
			.getPropertiesEl("classpath:dict/user_phone.txt");

	static {
		headers = new HashMap<String, String>();
		headers.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
	}

	@Override
	public void doSend(List<UserMessage> messages) {
		if (messages != null && !messages.isEmpty()) {
			UserMessage m = messages.get(0);
			UserMessage message = new UserMessage();
			message.setPhone(m.getPhone());
			message.setMessage(this.generateSiteMsg(messages));
			try {
				this.doSend(message);
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	String generateSiteMsg(List<UserMessage> list) {
		String s = "";
		String href = null;
		for (UserMessage msg : list) {
			s += "," + msg.getSiteName() + "(" + msg.getSite() + ")";
			if (href == null)
				href = msg.getDetailHref();
		}
		return s.substring(1) + "--详情查看：" + href;
	}

	public void doSend(UserMessage message) throws Exception {
		String phone = message.getPhone();
		if (StringUtils.isEmpty(phone))
			return;
		String[] items = phone.split(",");

		for (String a : items)
			this.doSend(a, message.getMessage());
	}

	public void send(String phone, String message) throws Exception {
		if (StringUtils.isEmpty(phone))
			return;
		String[] items = phone.split(",");
		for (String a : items)
			this.doSend(a, message);
	}

	public void doSend(String phone, String message) throws Exception {
		String url = "http://sdk229ws.eucp.b2m.cn:8080/sdkproxy/sendsms.action";
		CrawlHttp http = new CrawlHttp();
		HttpReq request = new HttpReq(url, "POST", "utf-8", headers);
		String msg = message + "，详细情况参考web管理端";
		String param = "cdkey=" + cdKey + "&password=" + password + "&phone="
				+ phone + "&services=" + msg;
		request.setParaStr(param);

		HttpResp resp = http.execute(request);
		if (resp.getStatus() != 200) {
			System.out.println(" -------- 短信发送失败");
		}
	}

	public static void main(String args[]) {
		UserMessage message = new UserMessage();
		message.setSite("a");
		message.setSiteName("dd");
		message.setPhone("13880923727");
		message.setMessage(" 出现异常情况");
		try {
			new SmsMessageSender().doSend(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
