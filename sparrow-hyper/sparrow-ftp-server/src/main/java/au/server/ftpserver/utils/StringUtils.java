package au.server.ftpserver.utils;

public final class StringUtils {
	/**
	 * 
	 * <p>
	 * Description: judge the string is null or empty
	 * </p>
	 * 
	 * @param str
	 * @return
	 * @author Yzc
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str.trim()))
			return true;
		return false;
	}
}
