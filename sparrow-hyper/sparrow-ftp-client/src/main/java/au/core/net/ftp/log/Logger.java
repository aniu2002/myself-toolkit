package au.core.net.ftp.log;

public interface Logger {
	public void debug(String msg);

	public void debugRaw(String msg);

	public void debug(String msg, Throwable throwable);

	public void warn(String msg);

	public void warn(String msg, Throwable throwable);

	public void error(String msg);

	public void error(String msg, Throwable throwable);

	public void info(String msg);

	public void info(String msg, Throwable throwable);

	public void fatal(String msg);

	public void fatal(String msg, Throwable throwable);
}
