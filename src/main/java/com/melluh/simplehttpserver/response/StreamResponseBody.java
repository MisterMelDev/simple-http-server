package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.melluh.simplehttpserver.HttpUtils;

public class StreamResponseBody implements ResponseBody {

	private InputStream in;
	private long length;
	
	public StreamResponseBody(InputStream in, long length) {
		this.in = in;
		this.length = length;
	}
	
	@Override
	public void write(OutputStream out) throws IOException {
		byte[] buffer = new byte[32 * 1024];
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
	public void close() throws IOException {
		HttpUtils.close(in);
	}

	@Override
	public long getLength() {
		return length;
	}

}
