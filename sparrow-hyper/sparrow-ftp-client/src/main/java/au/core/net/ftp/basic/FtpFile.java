package au.core.net.ftp.basic;

public class FtpFile {
	private String name;
	private String date;
	private String path;
	private long size;
	private int subfiles;
	private boolean directory;

	public boolean isDirectory() {
		return directory;
	}

	public boolean isFile() {
		return !directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getSubfiles() {
		return subfiles;
	}

	public void setSubfiles(int subfiles) {
		this.subfiles = subfiles;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
