package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a response body.
 */
public interface ResponseBody {

	public void write(OutputStream out) throws IOException;
	public void close() throws IOException;
	public long getLength();
	
}
