package com.melluh.simplehttpserver;

import com.melluh.simplehttpserver.response.Response;

public interface Route {

	/**
	 * Called to handle a request to this route.
	 * 
	 * @param request the request to handle
	 * @return a response
	 * @see {@link Request}, {@link Response}
	 */
	public Response handle(Request request);
	
}
