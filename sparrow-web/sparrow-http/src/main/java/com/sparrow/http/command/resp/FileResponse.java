package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

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
