package com.melluh.simplehttpserver;

import com.melluh.simplehttpserver.response.Response;

public interface Route {

	public Response handle(Request request);
	
}
