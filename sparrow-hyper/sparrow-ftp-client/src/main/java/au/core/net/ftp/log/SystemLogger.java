package au.core.net.ftp.log;

public class SystemLogger implements Logger {
	private void log(String msg) {
		System.out.println(msg);
	}

	private void log(String msg, Throwable throwable) {
		System.out.println(msg);
		throwable.printStackTrace();
	}

	public void debug(String msg) {
		log(msg + "\n");
	}

	public void debugRaw(String msg) {
		log(msg);
	}

	public void debug(String msg, Throwable throwable) {
		log(msg, throwable);
	}

	public void warn(String msg) {
		log(msg);
	}

	public void warn(String msg, Throwable throwable) {
		log(msg, throwable);
	}

	public void error(String msg) {
		log(msg);
	}

	public void error(String msg, Throwable throwable) {
		log(msg, throwable);
	}

	public void info(String msg) {
		log(msg);
	}

	public void info(String msg, Throwable throwable) {
		log(msg, throwable);
	}

	public void fatal(String msg) {
		log(msg);
	}

	public void fatal(String msg, Throwable throwable) {
		log(msg, throwable);
	}
}
