package com.melluh.simplehttpserver.protocol;

public enum Method {

	GET,
	HEAD,
	POST,
	PUT,
	DELETE,
	CONNECT,
	OPTIONS,
	TRACE,
	PATCH;
	
	public static Method getMethod(String name) {
		for(Method method : values()) {
			if(method.name().equals(name))
				return method;
		}
		return null;
	}
	
}
