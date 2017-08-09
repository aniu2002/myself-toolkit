package com.sparrow.core.utils.file;

import java.io.File;
import java.util.regex.Pattern;

import com.sparrow.core.utils.StringUtils;


public class DeleteFileFilter implements SFileFilter {
	Pattern dirPattern;
	Pattern filePattern;
	String dirExpress;
	String fileExpress;

	public DeleteFileFilter(String dirExpress, String fileExpress) {
		if (!"*".equals(dirExpress)) {
			this.dirExpress = dirExpress;
			this.dirPattern = this.generateRegularExpress(dirExpress);
		}
		if (!"*".equals(fileExpress)) {
			this.filePattern = this.generateRegularExpress(fileExpress);
			this.fileExpress = fileExpress;
		}
	}

	private Pattern generateRegularExpress(String expr) {
		if (StringUtils.isEmpty(expr))
			return null;
		if (expr.indexOf('*') != -1) {
			String express[] = expr.split(",");
			String strs = "";
			for (String tmp : express) {
				strs += "|(" + tmp + ")";
			}

			strs = strs.substring(1);
			strs = strs.replaceAll("\\*", "[\\\\d\\\\D]*").replaceAll("\\.",
					"\\\\.");
			return Pattern.compile(strs);
		}
		return null;
	}

	@Override
	public boolean testDir(File file) {
		boolean flag;
		if (StringUtils.isEmpty(this.dirExpress))
			if (StringUtils.isEmpty(this.fileExpress))
				flag = true;
			else
				flag = false;
		else {
			String name = file.getName();
			if (this.dirPattern != null)
				flag = this.dirPattern.matcher(name).matches();
			else
				flag = name.equalsIgnoreCase(this.dirExpress);
		}
		return flag;
	}

	@Override
	public boolean testFile(File file) {
		boolean flag;
		if (StringUtils.isEmpty(this.fileExpress))
			flag = true;
		else {
			String name = file.getName();
			if (this.filePattern != null)
				flag = this.filePattern.matcher(name).matches();
			else
				flag = name.equalsIgnoreCase(this.fileExpress);
		}
		return flag;
	}
}
