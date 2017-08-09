package au.server.ftpserver.cmd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.udp.IOUdpDataConnectionFactory;
import org.apache.ftpserver.udp.UdpReply;
import org.apache.ftpserver.util.IllegalInetAddressException;
import org.apache.ftpserver.util.IllegalPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPPORT extends AbstractCommand {
	private final Logger LOG = LoggerFactory.getLogger(UDPPORT.class);

	@Override
	public void execute(FtpIoSession session, FtpServerContext context,
			FtpRequest request) throws IOException, FtpException {
		// reset state variables
		session.resetState();

		// argument check
		if (!request.hasArgument()) {
			session.write(LocalizedFtpReply.translate(session, request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"PORT", null));
			return;
		}

		// is port enabled
		DataConnectionConfiguration dataCfg = session.getListener()
				.getDataConnectionConfiguration();
		if (!dataCfg.isActiveEnabled()) {
			session.write(LocalizedFtpReply.translate(session, request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"PORT.disabled", null));
			return;
		}

		InetSocketAddress address;
		try {
			InetSocketAddress add = (InetSocketAddress) session
					.getRemoteAddress();
			address = new InetSocketAddress(add.getHostName(), Integer
					.valueOf(request.getArgument().trim()));
			if (LOG.isDebugEnabled())
				LOG.debug("UDPPORT COMMAND PORT:"
						+ request.getArgument().trim());
			// port must not be 0
			if (address.getPort() == 0) {
				throw new IllegalPortException("PORT port must not be 0");
			}
		} catch (IllegalInetAddressException e) {
			session.write(LocalizedFtpReply.translate(session, request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"PORT", null));
			return;
		} catch (IllegalPortException e) {
			LOG.debug("Invalid data port: " + request.getArgument(), e);
			session.write(LocalizedFtpReply.translate(session, request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"PORT.invalid", null));
			return;
		}

		if (LOG.isDebugEnabled())
			LOG.debug("UDPPORT isAactive:" + dataCfg.isActiveIpCheck());
		// check IP
		if (dataCfg.isActiveIpCheck()) {
			if (session.getRemoteAddress() instanceof InetSocketAddress) {
				InetAddress clientAddr = ((InetSocketAddress) session
						.getRemoteAddress()).getAddress();
				if (!address.getAddress().equals(clientAddr)) {
					session
							.write(LocalizedFtpReply
									.translate(
											session,
											request,
											context,
											FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
											"PORT.mismatch", null));
					return;
				}
			}
		}
		ServerDataConnectionFactory dataConnectFactory = session
				.getUdpDataConnection();
		dataConnectFactory.initActiveDataConnection(address);
		dataConnectFactory.initPassiveDataConnection();
		int activePort = dataConnectFactory.getPort();
		if (LOG.isDebugEnabled()) {
			LOG.debug("UDPPORT Local Port:" + activePort
					+ "\r\nUDPPORT Client Port:" + address.getPort());
		}
		try {
			((IOUdpDataConnectionFactory) dataConnectFactory)
					.setClientPort(address.getPort());
			dataConnectFactory.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.write(LocalizedFtpReply.translateUdp(session, request, context,
				UdpReply.REPLY_200_COMMAND_OKAY, "UDPPORT", null, activePort));
	}
}
