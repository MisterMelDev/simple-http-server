package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import com.melluh.simplehttpserver.HttpUtils;

/**
 * Represents a body in the form of an {@link InputStream}.
 * The response is sent to the client by buffering the input stream.
 */
public class StreamResponseBody implements ResponseBody {

	private static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32kb
	
	private final InputStream in;
	private final long length;
	private final int bufferSize;
	
	/**
	 * Creates a new stream response body, with a 32KB buffer.
	 * The length parameter is used for the <code>Content-Length</code> header.
	 * 
	 * @param in the input stream
	 * @param length length of the data
	 */
	public StreamResponseBody(InputStream in, long length) {
		Objects.requireNonNull(in, "in is missing");
		this.in = in;
		this.length = length;
		this.bufferSize = DEFAULT_BUFFER_SIZE;
	}
	
	/**
	 * Creates a new stream response body, with the specified buffer size.
	 * The length parameter is used for the <code>Content-Length</code> header.
	 * 
	 * @param in the input stream
	 * @param length length of the data
	 * @param bufferSize buffer size in bytes
	 */
	public StreamResponseBody(InputStream in, long length, int bufferSize) {
		Objects.requireNonNull(in, "in is missing");
		this.in = in;
		this.length = length;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public void write(OutputStream out) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int len;
		while((len = in.read(buffer)) != -1) {
			try {
				out.write(buffer, 0, len);
			} catch (IOException ex) {
				HttpUtils.close(in);
				break;
			}
		}
	}
	
	@Override
	public void close() {
		HttpUtils.close(in);
	}

	@Override
	public long getLength() {
		return length;
	}

}
