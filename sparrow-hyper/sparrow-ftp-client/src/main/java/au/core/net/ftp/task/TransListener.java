package au.core.net.ftp.task;

public interface TransListener {
	short STARTED = 0;
	short FINISHED = 1;
	short FAILURE = -1;
	short RUN = 2;

	public void notice(short type, TransTask task);
}
