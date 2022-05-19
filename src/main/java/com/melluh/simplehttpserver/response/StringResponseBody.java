package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Represents a response body in the form of a string
 * or byte array.
 */
public class StringResponseBody implements ResponseBody {

	private final byte[] body;
	
	/**
	 * Creates a new string response body, containing the specified bytes.
	 * 
	 * @param body the body
	 */
	public StringResponseBody(byte[] body) {
		Objects.requireNonNull(body, "body is missing");
		this.body = body;
	}
	
	/**
	 * Creates a new string response body, containing the specified string.
	 * The string is converted to bytes using the platform's default charset.
	 * 
	 * @param body the body
	 */
	public StringResponseBody(String body) {
		Objects.requireNonNull(body, "body is missing");
		this.body = body.getBytes();
	}
	
	/**
	 * Creates a new string response body, containing the specified string.
	 * The string is converted to bytes using the specified charset.
	 *
	 * @param body the body
	 * @param charset the charset used to convert to bytes
	 */
	public StringResponseBody(String body, Charset charset) {
		Objects.requireNonNull(body, "body is missing");
		Objects.requireNonNull(charset, "charset is missing");
		this.body = body.getBytes(charset);
	}
	
	@Override
	public void write(OutputStream out) throws IOException {
		out.write(body);
	}
	
	@Override
	public void close() {}
	
	@Override
	public long getLength() {
		return body.length;
	}

}
