package au.server.ftpserver.cmd;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DWUDPBUF extends AbstractCommand {
	private final Logger LOG = LoggerFactory.getLogger(UDPPORT.class);

	@Override
	public void execute(FtpIoSession session, FtpServerContext context,
			FtpRequest request) throws IOException, FtpException {

		session.resetState();
		// get type from argument
		char type;
		if (request.hasArgument()) {
			type = request.getArgument().charAt(0);
		} else {
			// no type specified
			session.write(LocalizedFtpReply.translate(session, request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"TYPE", null));
			return;
		}
		// set type
		try {
			if (LOG.isDebugEnabled())
				LOG.debug(" DWUDPBUF TYPE : " + type);
			if (type == '0') {
				type = 'I';
			}
			session.setDataType(DataType.parseArgument(type));
			session
					.write(LocalizedFtpReply.translate(session, request,
							context, FtpReply.REPLY_200_COMMAND_OKAY,
							"DWUDPBUF", null));
			if (LOG.isDebugEnabled())
				LOG.debug("DWUDPBUF TYPE end: " + type);
		} catch (IllegalArgumentException e) {
			LOG.debug("Illegal type argument: " + request.getArgument(), e);
			session
					.write(LocalizedFtpReply
							.translate(
									session,
									request,
									context,
									FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER,
									"TYPE", null));
		}
	}

}
