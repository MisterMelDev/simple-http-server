package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Represents a response body in the form of a string
 * or byte array.
 */
public class StringResponseBody implements ResponseBody {

	private byte[] body;
	
	public StringResponseBody(byte[] body) {
		Objects.requireNonNull(body, "body is missing");
		this.body = body;
	}
	
	public StringResponseBody(String body) {
		Objects.requireNonNull(body, "body is missing");
		this.body = body.getBytes();
	}
	
	@Override
	public void write(OutputStream out) throws IOException {
		out.write(body);
	}
	
	@Override
	public void close() throws IOException {}
	
	@Override
	public long getLength() {
		return body.length;
	}

}
