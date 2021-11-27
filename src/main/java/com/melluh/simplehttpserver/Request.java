package com.melluh.simplehttpserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.melluh.simplehttpserver.ServerClient.ParseException;
import com.melluh.simplehttpserver.protocol.HTTPHeader;
import com.melluh.simplehttpserver.protocol.Method;
import com.melluh.simplehttpserver.protocol.Status;

public class Request {

	private static final int MAX_URI_LENGTH = 2048;
	
	private HTTPServer server;
	
	private Method method;
	private String location;
	private String protocolVersion;
	
	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> uriParams = new HashMap<>();
	private Map<String, String> cookies = new HashMap<>();
	
	protected Request(HTTPServer server, Method method, String uri, String protocolVersion) throws ParseException {
		this.server = server;
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
		
		if(key.equalsIgnoreCase(HTTPHeader.COOKIE) && server.isParseCookies()) {
			this.parseCookieHeader(value);
		}
	}
	
	private void parseCookieHeader(String header) {
		for(String part : header.split("; ")) {
			int equalsIndex = part.indexOf('=');
			if(equalsIndex == -1)
				break;
			
			cookies.put(part.substring(0, equalsIndex), part.substring(equalsIndex + 1));
		}
	}
	
	/**
	 * Returns this request's method.
	 * 
	 * @return the method
	 * @see {@link Method}
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * Returns this request's location, aka URI.
	 * 
	 * @return the uri
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Returns this request's protocol version (e.g. HTTP/1.1)
	 * 
	 * @return the protocol version
	 */
	public String getProtocolVersion() {
		return protocolVersion;
	}
	
	/**
	 * Returns the value for the specified header.
	 * If the header is not present on this request, this returns null.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the header
	 * @return value of the header, or null if it isn't present
	 */
	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}
	
	/**
	 * Returns the value for the specified URI parameter.
	 * If the parameter is not present on this request, this returns null.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the uri param
	 * @return value of the uri param, or null if it isn't present
	 */
	public String getUriParam(String name) {
		return uriParams.get(name.toLowerCase());
	}
	
	/**
	 * Returns the value for the specified cookie.
	 * If the cookie is not present on this request, this returns null.
	 * The name is <i>case-sensitive</i>.
	 * 
	 * <br><br>
	 * <b>This will only work if cookie parsing is enabled.</b>
	 * 
	 * @param name name of the cookie
	 * @return value of the cookie, or null if it isn't present
	 * @see {@link HTTPServer#isParseCookies()}
	 */
	public String getCookie(String name) {
		return cookies.get(name);
	}
	
	/**
	 * Checks if this request contains the specified header.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the header
	 * @return whether this request contains it
	 */
	public boolean hasHeader(String name) {
		return headers.containsKey(name.toLowerCase());
	}
	
	/**
	 * Checks if this request contains the specified uri param.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the uri param
	 * @return whether this request contains it
	 */
	public boolean hasUriParam(String name) {
		return uriParams.containsKey(name.toLowerCase());
	}
	
	/**
	 * Checks if this request contains the specified cookie.
	 * Cookie names are <i>case-sensitive</i>.
	 * 
	 * <br><br>
	 * <b>This will only work if cookie parsing is enabled.</b>
	 * 
	 * @param name name of the cookie
	 * @return whether this request contains it
	 * @see {@link HTTPServer#isParseCookies()}
	 */
	public boolean hasCookie(String name) {
		return cookies.containsKey(name);
	}
	
	/**
	 * Returns a set of all headers on this request.
	 * All names are in lowercase.
	 * 
	 * @return an immutable set of the headers
	 */
	public Set<String> getHeaders() {
		return Collections.unmodifiableSet(headers.keySet());	
	}
	
	/**
	 * Returns a set of URI params on this request.
	 * All names are in lowercase.
	 * 
	 * @return an immutable set of the uri params
	 */
	public Set<String> getUriParams() {
		return Collections.unmodifiableSet(uriParams.keySet());
	}
	
	/**
	 * Returns a set of cookies on this request.
	 * Cookie names are <i>case-sensitive</i>.
	 * 
	 * <br><br>
	 * <b>This will only work if cookie parsing is enabled.</b>
	 * 
	 * @return an immutable set of cookie names
	 * @see {@link HTTPServer#isParseCookies()}
	 */
	public Set<String> getCookies() {
		return Collections.unmodifiableSet(cookies.keySet());
	}
	
}
