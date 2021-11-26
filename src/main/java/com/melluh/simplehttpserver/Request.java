package com.melluh.simplehttpserver;

import java.util.HashMap;
import java.util.Map;

import com.melluh.simplehttpserver.ServerClient.ParseException;
import com.melluh.simplehttpserver.protocol.Method;
import com.melluh.simplehttpserver.protocol.Status;

public class Request {

	private static final int MAX_URI_LENGTH = 2048;
	
	private Method method;
	private String location;
	private String protocolVersion;
	
	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> uriParams = new HashMap<>();
	
	public Request(Method method, String uri, String protocolVersion) throws ParseException {
		this.method = method;
		this.protocolVersion = protocolVersion;
		this.parseUri(uri);
	}
	
	private void parseUri(String uri) throws ParseException {
		if(uri.length() > MAX_URI_LENGTH)
			throw new ParseException(Status.URI_TOO_LONG, "URI too long (" + uri.length() + " > " + MAX_URI_LENGTH + ")");
		
		if(!uri.startsWith("/"))
			throw new ParseException(Status.BAD_REQUEST, "Invalid URI");
		
		int paramsIndex = uri.indexOf('?');
		if(paramsIndex < 0) {
			this.location = HTTPUtils.decodePercent(uri);
			return;
		}
		
		this.location = HTTPUtils.decodePercent(uri.substring(0, paramsIndex));
		
		String[] params = uri.substring(paramsIndex + 1).split("&");
		for(String param : params) {
			int equalsIndex = param.indexOf('=');
			if(equalsIndex < 0)
				continue;
			
			String key = param.substring(0, equalsIndex);
			String value = param.substring(equalsIndex + 1);
			uriParams.put(HTTPUtils.decodePercent(key.toLowerCase()), HTTPUtils.decodePercent(value));
		}
	}
	
	protected void addHeader(String key, String value) {
		headers.put(key.toLowerCase(), value);
	}
	
	public Method getMethod() {
		return method;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getProtocolVersion() {
		return protocolVersion;
	}
	
	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}
	
	public String getUriParam(String name) {
		return uriParams.get(name.toLowerCase());
	}
	
	public boolean hasHeader(String name) {
		return headers.containsKey(name.toLowerCase());
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public Map<String, String> getUriParams() {
		return uriParams;
	}
	
}
