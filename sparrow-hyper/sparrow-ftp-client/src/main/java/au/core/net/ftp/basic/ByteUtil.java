package au.core.net.ftp.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class ByteUtil {
	public static int byteToInt(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static byte[] intToBytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	// c 大小尾相反
	public static byte[] intToByte(int i) {
		byte[] bt = new byte[4];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		bt[2] = (byte) ((0xff0000 & i) >> 16);
		bt[3] = (byte) ((0xff000000 & i) >> 24);
		return bt;
	}

	// java
	public static byte[] intToByteArray1(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static byte[] intToByteArray2(int i) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		out.writeInt(i);
		byte[] b = buf.toByteArray();
		out.close();
		buf.close();
		return b;
	}

	public static void main(String args[]) {
		try {
			printt(intToByte(1000));
			printt(intToByteArray1(1000));
			printt(intToByteArray2(1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printt(byte bytes[]) {
		for (int i = 0; i < bytes.length; i++)
			System.out.print(bytes[i]);
		System.out.println();
	}

	public static int bytesToInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

	public static byte[] stringToUnicodeByte(String src) {
		try {
			return src.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src.getBytes();
	}

	public static String unicodeByteToString(byte[] src) {
		String s = "";
		try {
			s = new String(src, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s.trim();

	}

	public static long bytesToInts(byte[] bytes) {
		long num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

	public static long bytesToLong(byte[] bytes) {
		long num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		long num2 = bytes[4] & 0xFF;
		num2 |= ((bytes[5] << 8) & 0xFF00);
		num2 |= ((bytes[6] << 16) & 0xFF0000);
		num2 |= ((bytes[7] << 24) & 0xFF000000);
		long lNum = num;
		lNum = lNum << 32;
		lNum = lNum | num2;
		return lNum;
	}

	public static long byteToLong(byte[] b) {
		int mask = 0xFF;
		int temp = 0;
		long res = 0;
		for (int i = 0; i < 8; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static byte[] longToBytes(long num) {
		// byte[] b = new byte[8];
		byte[] bt = new byte[8];
		byte[] b4 = new byte[4];
		b4[0] = (byte) (0xff & num);
		b4[1] = (byte) ((0xff00 & num) >> 8);
		b4[2] = (byte) ((0xff0000 & num) >> 16);
		b4[3] = (byte) ((0xff000000 & num) >> 24);
		ByteUtil.arrayCopy(b4, 0, bt, 4, 4);
		num = num - bytesToInts(b4);
		b4 = new byte[4];
		b4[0] = (byte) (0xff & num);
		b4[1] = (byte) ((0xff00 & num) >> 8);
		b4[2] = (byte) ((0xff0000 & num) >> 16);
		b4[3] = (byte) ((0xff000000 & num) >> 24);
		ByteUtil.arrayCopy(b4, 0, bt, 0, 4);
		/*
		 * System.arraycopy(bt, 0, b, 0, 4); bt = new byte[4]; bt[0] = (byte)
		 * (0xff & num); bt[1] = (byte) ((0xff00 & num) >> 8); bt[2] = (byte)
		 * ((0xff0000 & num) >> 16); bt[3] = (byte) ((0xff000000 & num) >> 24);
		 * System.arraycopy(bt, 0, b, 4, 4);
		 */
		return bt;
	}

	/**
	 * 字符串转换成字节数组
	 * 
	 * @param src
	 * @param length
	 *            ：转换成字节数组的数组长度
	 * @return
	 */
	public static byte[] stringToByte(String src, int length) {
		byte[] tartget = new byte[length];
		if (src == null)
			return tartget;
		byte[] bytesrc = stringToUnicodeByte(src);
		if (bytesrc.length == length)
			return bytesrc;
		else if (bytesrc.length <= length) {
			ByteUtil.arrayCopy(bytesrc, 0, tartget, 0, bytesrc.length);
		} else if (bytesrc.length > length) {
			ByteUtil.arrayCopy(bytesrc, 0, tartget, 0, length);
		}
		return tartget;
	}

	public static int readByteToInt(ByteArrayInputStream input, int length) {
		byte[] s1 = new byte[length];
		int read = input.read(s1, 0, length);
		if (read != -1)
			return ByteUtil.bytesToInt(s1);
		return -1;
	}

	public static long readByteToLong(ByteArrayInputStream input, int length) {
		byte[] s1 = new byte[length];
		int read = input.read(s1, 0, length);
		if (read != -1)
			return ByteUtil.bytesToLong(s1);
		return -1;
	}

	public static String readByteToString(ByteArrayInputStream input, int length) {
		byte[] s1 = new byte[length];
		int read = input.read(s1, 0, length);
		if (read != -1) {
			return unicodeByteToString(s1).trim();
		}
		return "";
	}

	/*
	 * public static String readByteToStrings(ByteArrayInputStream input, int
	 * length) { byte[] s1 = new byte[length]; int read = input.read(s1, 0,
	 * length); if (read != -1) { StringBuilder buffer = new StringBuilder();
	 * int var = s1.length / 2;// 两个字节代表一个字符 byte[] returnbyte = null; int
	 * offset = 0; for (int i = 0; i < var; i++) { int off = 2 * i; byte[]
	 * databyte = new byte[] { s1[off], s1[off + 1] }; if (s1[off] == s1[off +
	 * 1] && s1[off] == 0) { break; } byte[] tempByte = new byte[2 + offset]; if
	 * (returnbyte != null) ByteUtil.arraycopy(returnbyte, 0, tempByte, 0,
	 * returnbyte.length); ByteUtil.arraycopy(databyte, 0, tempByte, offset, 2);
	 * returnbyte = tempByte; offset += 2; } if (returnbyte == null) return "";
	 * buffer.append(unicodeByteToString(returnbyte)); return
	 * buffer.toString().trim(); }
	 * 
	 * return ""; }
	 */

	/**
	 * 数组拷贝
	 * 
	 * @param srcbyte
	 * @param targetbyte
	 * @param offset
	 */
	public static void arrayCopy(byte[] srcbyte, byte[] targetbyte, int offset) {
		int len = Math.min(srcbyte.length, targetbyte.length);
		for (int i = 0; i < len; i++) {
			targetbyte[offset] = srcbyte[i];
			offset++;
		}
	}

	/**
	 * @see
	 * @param srcbyte
	 * @param targetbyte
	 */
	public static void arrayCopy(byte[] srcbyte, byte[] targetbyte) {
		int len = Math.min(srcbyte.length, targetbyte.length);
		for (int i = 0; i < len; i++) {
			targetbyte[i] = srcbyte[i];
		}
	}

	/**
	 * 数组拷贝
	 * 
	 * @param src
	 * @param srcPos
	 * @param dest
	 * @param destPos
	 * @param length
	 */
	public static void arrayCopy(byte[] src, int srcPos, byte[] dest,
			int destPos, int length) {
		int count = 0;
		for (int i = srcPos; i < src.length; i++) {
			if (count >= length)// 计数长度大于拷贝长度 直接跳出循环
				break;
			dest[destPos] = src[i];
			destPos++;
			count++;
		}
	}

	/**
	 * @param size
	 * @return
	 */
	public static String getFormatString(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (size < 1024 * 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "MB");
		}
	}
}
