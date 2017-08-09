package com.sparrow.netty.command.resp;

import com.sparrow.netty.command.Response;

public class FileResponse implements Response {
	String file;

	public FileResponse(String file) {
		this.file = file;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return file;
	}

}
