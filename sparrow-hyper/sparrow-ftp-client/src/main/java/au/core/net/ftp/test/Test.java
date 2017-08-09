package au.core.net.ftp.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Test {

	public static void main(String args[]) {
		try {
			RandomAccessFile fc = new RandomAccessFile("e:/test/myfile.dat",
					"rw");
			long len = 5L,en=0x8fffffff;
			len = len * 1024 * 1024 * 1024;
			fc.setLength(len);
			fc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
