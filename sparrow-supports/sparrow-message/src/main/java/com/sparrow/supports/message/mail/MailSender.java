package com.sparrow.supports.message.mail;

import com.sparrow.supports.message.FreeMarkerTool;
import com.sparrow.supports.message.MessageSender;
import com.sparrow.supports.message.UserMessage;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.util.*;

public class MailSender extends MessageSender {
	String mailServer = "email";
	String from;
	String pass;

	public MailSender(String server, String user, String pass) {
		this.mailServer = server;
		this.from = user;
		this.pass = pass;
	}

	@Override
	public void doSend(List<UserMessage> messages) {
		if (messages != null && !messages.isEmpty()) {
			UserMessage message = messages.get(0);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("items", messages);
			map.put("title", "惠800站点改版探测情况通知");
			String msg = FreeMarkerTool.getInstance().writeString("mail", map);
			try {
				this.send(message.getMailTo(), null, "惠800站点改版探测情况通知", msg,
						null);
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String mailTo, String carbonCopy, String subject,
			String msgText, File files[]) throws Exception {
		String type = "text/html;charset='UTF-8'";
		boolean enableLogo = "true".equalsIgnoreCase(System
				.getProperty("email.logo.enable"));

		Properties p = new Properties();
		p.put("mail.smtp.auth", "true");
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.host", this.mailServer);
		p.put("mail.smtp.port", "25");
		// p.put("mail.smtp.starttls.enable", "true");

		Session mailsession = Session.getInstance(p);
		MimeMessage msg = new MimeMessage(mailsession);

		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(mailTo));
		if (!StringUtils.isEmpty(carbonCopy))
			msg.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(carbonCopy));
		msg.setSentDate(new Date());
		msg.setSubject(subject);

		// 设置邮件内容，作为Multipart对象的一部分
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(msgText);
		mbp.setHeader("Content-Type", type);

		MimeMultipart mulp = new MimeMultipart();

		if (enableLogo)
			mulp.setSubType("related");
		mulp.addBodyPart(mbp);
		if (enableLogo) {
			DataHandler dh = new DataHandler(new ByteArrayDataSource(
					ImageBuf.logoPngData, "application/octet-stream"));
			MimeBodyPart mdp = new MimeBodyPart();
			mdp.setDataHandler(dh);
			// 加上这句将作为附件发送,否则将作为信件的文本内容
			mdp.setFileName("1.jpg");
			mdp.setHeader("Content-ID", "IMG1");
			mulp.addBodyPart(mdp);
		}
		// 设置附件，作为Multipart对象的一部分
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.exists()) {
					mbp = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					mbp.setDataHandler(new DataHandler(source));
					mbp.setFileName(MimeUtility.encodeText(file.getName()));
					mulp.addBodyPart(mbp);
				}
			}
		}
		// 设置信息内容，将Multipart 对象加入信息中
		msg.setContent(mulp);
		msg.saveChanges();

		Transport tran = mailsession.getTransport("smtp");
		tran.connect(this.mailServer, 25, this.from, this.pass);
		tran.sendMessage(msg, msg.getAllRecipients());
		// msg.writeTo(System.out);
		tran.close();
	}

	public static void main(String args[]) {
		String mailServer = "smtp.163.com";
		// String mailFrom = "yuanzhengchu@sobey.com";
		String mailTo = "yuanzhengchu@sobey.com";
		String file = "E:\\eclipse\\JVM.txt";

		MailSender sender = new MailSender(mailServer, "yuanzhengchu2002",
				"123456789");
		try {
			sender.send(mailTo, "carbonCopy", "td", "admin test.",
					new File[] { new File(file) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
