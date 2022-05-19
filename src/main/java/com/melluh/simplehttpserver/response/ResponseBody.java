package com.melluh.simplehttpserver.response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a response body.
 */
public interface ResponseBody {

	/**
	 * Writes the body to an output stream.
	 * 
	 * @param out the output stream
	 * @throws IOException if an I/O error occurs
	 */
	void write(OutputStream out) throws IOException;
	
	/**
	 * Closes any resources this response body is using.
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	void close() throws IOException;
	
	/**
	 * Returns the length of this response.
	 * 
	 * @return the length in bytes
	 */
	long getLength();
	
}
