package au.core.net.ftp.task;

import java.io.File;

import au.core.net.ftp.basic.FtpUtils;

public class FtpTaskBuilder {

	FtpTaskBuilder() {
	}

	public static TransTask generateTransTask(String src, String target) {
		if (FtpUtils.isFtpServerPath(src) == FtpUtils.isFtpServerPath(target))
			return null;
		TransTask task = new TransTask();
		if (src != null && !"".equals(src.trim()))
			if (FtpUtils.isFtpServerPath(src))
				task.setUpload(false);
		task.setSrcPath(src);
		task.setDistPath(target);
		if (task.isUpload())
			task.setTotal(new File(src).length());
		return task;
	}
}
