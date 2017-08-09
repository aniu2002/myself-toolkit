package com.snmp.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * <p>
 * Title: FileLogger
 * </p>
 * <p>
 * Description: for file log
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Sobey
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-6
 */
public class FileLogger extends Logger {
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			YYYY_MM_DD_TIME);
	private String logFile;
	private String errFile;

	public FileLogger() {
		this(null, null);
	}

	public FileLogger(String logFile) {
		this(logFile, logFile);
	}

	public FileLogger(String logFile, String errFile) {
		if (logFile == null && !logFile.equals("")) {
			this.logFile = logFile;
			this.out = this.getFilePrintStream(this.logFile);
		}
		if (errFile != null && errFile.equals(logFile)) {
			this.errFile = errFile;
			this.err = this.out;
		} else if (errFile != null) {
			this.errFile = errFile;
			this.err = this.getFilePrintStream(this.errFile);
		}
		this.checkLogStream();
	}

	/**
	 * 
	 * <p>
	 * Description: get file print stream
	 * </p>
	 * 
	 * @param fileName
	 * @return
	 * @author Yzc
	 */
	private PrintStream getFilePrintStream(String fileName) {
		File file = new File(fileName);
		PrintStream prtStream = null;
		try {
			file = getFile(fileName);
			FileOutputStream fos = new FileOutputStream(file, true);
			prtStream = new PrintStream(fos, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prtStream;
	}

	/**
	 * @return out
	 */
	public PrintStream getOut() {
		return out;
	}

	/**
	 * @return err
	 */
	public PrintStream getErr() {
		return err;
	}

	@Override
	public String formatTime(Date time) {
		if (time == null)
			time = new java.util.Date();
		String timestr = sdf.format(time);
		return timestr;
	}

	private static File getFile(String filePath) throws IOException {
		String path = null;
		File file = null;
		if (filePath == null)
			return null;
		file = new File(filePath);
		if (file.exists())
			return file;
		path = getPath(filePath);
		if (path != null) {
			file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		file = new File(filePath);
		file.createNewFile();
		return file;
	}

	private static String getPath(String file) {
		if (file == null)
			return null;
		int x = file.lastIndexOf("/");
		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		}
		x = file.lastIndexOf("\\");

		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		} else
			return null;
	}
}
