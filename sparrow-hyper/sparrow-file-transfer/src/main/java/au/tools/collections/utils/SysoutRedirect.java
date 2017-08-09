package au.tools.collections.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

public class SysoutRedirect {
	public static final String outFile = "logs/out.log";
	public static final String errFile = "logs/err.log";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static String getCurrentTime() {
		String timestr;
		java.util.Date cday = new java.util.Date();
		timestr = sdf.format(cday);
		return timestr;
	}

	public static void resetSystemStream() {
		File outf = new File(outFile);
		// File errf = new File(errFile);
		if (outf.exists()) {
			outf.renameTo(new File(outFile + "." + getCurrentTime()));
			// outf.delete();
		}
		// if (errf.exists()) {
		// errf.renameTo(new File(errFile + "." + getCurrentTime()));
		// // errf.delete();
		// }
		setit(outFile);
	}

	private static void setit(String outFile) {
		File outf = new File(outFile);
		// File errf = new File(errorFile);
		if (!outf.exists())
			createFile(outFile);
		// if (!errf.exists())
		// createFile(errorFile);
		PrintStream outStream = null;// , errStream = null
		try {
			FileOutputStream fos = new FileOutputStream(outf, true);
			// FileOutputStream fos1 = new FileOutputStream(errf, true);
			outStream = new PrintStream(fos, true);
			// errStream = new PrintStream(fos1, true);

			System.setOut(outStream);
			System.setErr(outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void createFile(String file) {
		String path = getPath(file);
		File f = null;

		if (file == null)
			return;
		if (path != null) {
			f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		f = new File(file);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
