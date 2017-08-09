package com.sparrow.data.tools.exports.writer;

import java.io.IOException;

public abstract class AbstractDataWriter implements DataWriter {
	private String headers[];

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String headers[]) {
		this.headers = headers;
	}

	protected final void writeHeaders() throws IOException {
		if (this.headers != null)
			this.writeHeader(this.headers);
	}

    protected void writeHeader(String[] headers) throws IOException {

    }

    @Override
	public final void open() throws IOException {
		this.doOpen();
		this.writeHeaders();
	}

	protected abstract void doOpen() throws IOException;
}
