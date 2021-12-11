package com.melluh.simplehttpserver;

import com.melluh.simplehttpserver.response.Response;

public interface RequestHandler {

	/**
	 * Called to handle a request to the web server.
	 * 
	 * @param request the request to handle
	 * @return a response
	 * @see {@link Request}, {@link Response}
	 */
	public Response handle(Request request);
	
}
