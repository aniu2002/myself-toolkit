package org.apache.ftpserver.udp;

import org.apache.ftpserver.ftplet.DefaultFtpReply;

public class UdpReply extends DefaultFtpReply {
	
    private int activePort = 0;
    
    public static final int REPLY_203_COMMAND_OKAY = 203;
    public static final int REPLY_533_COMMAND_WARN = 533;
    
	public UdpReply(int code, String message,int activePort) {
		super(code, message);
		this.activePort = activePort;
	}

	@Override
	public String toString() {
		int code = getCode();
		String notNullMessage = getMessage();
		if (notNullMessage == null)
			notNullMessage = "";
		StringBuffer sb = new StringBuffer();
		if (notNullMessage.indexOf('\n') == -1) {
			sb.append("UDPPORT");
			sb.append(" ");
			sb.append(activePort);
			//sb.append(notNullMessage);
			sb.append("\r\n");
		} else {
			String lines[] = notNullMessage.split("\n");
			sb.append(code);
			sb.append("-");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (i + 1 == lines.length) {
					sb.append(code);
					sb.append(" ");
				}
				if (i > 0 && i + 1 < lines.length && line.length() > 2
						&& isDigit(line.charAt(0)) && isDigit(line.charAt(1))
						&& isDigit(line.charAt(2)))
					sb.append("  ");
				sb.append(line);
				sb.append("\r\n");
			}

		}
		return sb.toString();
	}

    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

}
