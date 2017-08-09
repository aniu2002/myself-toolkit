package com.sparrow.collect.extract;

import org.jchmlib.ChmEnumerator;
import org.jchmlib.ChmFile;
import org.jchmlib.ChmUnitInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-8-8 Time: 上午10:22 To change this
 * template use File | Settings | File Templates.
 */
public class ChmExtract {
	public static void main(String[] argv) throws IOException {
		ChmFile chmFile = null;
		String basePath = "F:/xx";

		long time = System.currentTimeMillis(), time_prev = time;

		chmFile = new ChmFile("F:/99BT.chm");

		System.out.println("/F:99BT.chm");
		chmFile.enumerate(ChmFile.CHM_ENUMERATE_ALL, new Extractor(chmFile,
				basePath));
		time = System.currentTimeMillis();
		System.out.println("    finished in " + (time - time_prev) + " ms");
		System.out.println();
	}

	public void extract(String chmFileName, String extractPath)
			throws IOException {
		ChmFile chmFile = null;
		String basePath = extractPath;
		long time = System.currentTimeMillis(), time_prev = time;
		chmFile = new ChmFile(chmFileName);
		System.out.println(chmFileName);
		chmFile.enumerate(ChmFile.CHM_ENUMERATE_ALL, new Extractor(chmFile,
				basePath));
		time = System.currentTimeMillis();
		System.out.println("    finished in " + (time - time_prev) + " ms");
	}
}

class Extractor implements ChmEnumerator {

	String basePath;
	ChmFile chmFile;

	public Extractor(ChmFile chmFile, String basePath) {
		this.chmFile = chmFile;
		if (basePath.endsWith("/")) {
			this.basePath = basePath.substring(0, basePath.length() - 1);
		} else {
			this.basePath = basePath;
		}
	}

	public void enumerate(ChmUnitInfo ui) {
		PrintStream out = null;
		ByteBuffer buffer = null;
		byte[] bytes = null;
		int gotLen;
		String fullPath = basePath;

		if (!ui.path.startsWith("/"))
			return;
		String pt = ui.path;
		char n = pt.length() > 1 ? pt.charAt(1) : 0;
		if (n == '$' || n == '#')
			return;
		else if (pt.endsWith("HHK") || pt.endsWith("HHC"))
			return;

		fullPath = fullPath.concat(ui.path);

		if (ui.length != 0) {
			try {
				out = new PrintStream(fullPath);
			} catch (IOException e) {
				System.out.println("   fail while opening the newly created file " + ui.path);
			}
			if (out == null) {
				System.out.println("   fail to open the newly created file " + ui.path);
				return;
			}

			buffer = chmFile.retrieveObject(ui, 0, ui.length);
			if (buffer == null) {
				System.out.println("    extract failed on " + ui.path);
				return;
			}
			gotLen = buffer.limit() - buffer.position();
			bytes = new byte[gotLen];

			buffer.mark();
			while (buffer.hasRemaining()) {
				buffer.get(bytes);
				out.write(bytes, 0, gotLen);
			}
			buffer.reset();
			out.close();
		} else {
			if (fullPath.endsWith("/")) {
				new File(fullPath).mkdirs();
			} else {
				new File(fullPath).delete();
			}
		}
	}
}
