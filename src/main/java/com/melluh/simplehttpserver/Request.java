package com.melluh.simplehttpserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.melluh.simplehttpserver.ServerClient.ParseException;
import com.melluh.simplehttpserver.protocol.HttpHeader;
import com.melluh.simplehttpserver.protocol.Method;
import com.melluh.simplehttpserver.protocol.Status;

/**
 * A request from a HTTP client.
 */
public class Request {

	private static final int MAX_URI_LENGTH = 2048;
	
	private final HttpServer server;

	private final Method method;
	private final String protocolVersion;
	private String location;
	private String queryString;
	
	private final Map<String, String> headers = new HashMap<>();
	private Map<String, String> queryParams;
	private Map<String, String> cookies;

	private byte[] body;
	
	protected Request(HttpServer server, Method method, String uri, String protocolVersion) throws ParseException {
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
			this.location = HttpUtils.decodePercent(uri);
			return;
		}
		
		this.location = HttpUtils.decodePercent(uri.substring(0, paramsIndex));
		this.queryString = uri.substring(paramsIndex + 1);
	}
	
	protected void addHeader(String key, String value) {
		headers.put(key.toLowerCase(), value);
		
		if(key.equalsIgnoreCase(HttpHeader.COOKIE) && server.isParseCookies()) {
			this.parseCookieHeader(value);
		}
	}
	
	private void parseCookieHeader(String header) {
		this.cookies = new HashMap<>();
		for(String part : header.split("; ")) {
			int equalsIndex = part.indexOf('=');
			if(equalsIndex == -1)
				break;
			
			cookies.put(part.substring(0, equalsIndex), part.substring(equalsIndex + 1));
		}
	}

	private void parseQueryString() {
		if(queryParams != null)
			throw new IllegalStateException("Query string already parsed");

		String[] params = queryString.split("&");
		this.queryParams = new HashMap<>();
		for(String param : params) {
			int equalsIndex = param.indexOf('=');
			if(equalsIndex < 0)
				continue;

			String key = param.substring(0, equalsIndex);
			String value = param.substring(equalsIndex + 1);
			queryParams.put(HttpUtils.decodePercent(key.toLowerCase()), HttpUtils.decodePercent(value));
		}
	}
	
	protected void setBody(byte[] body) {
		this.body = body;
	}
	
	/**
	 * Returns this request's method.
	 * 
	 * @return the method
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
	 * Returns the value for the specified query parameter.
	 * If the parameter is not present on this request, this returns null.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the query param
	 * @return value of the query param, or null if it isn't present
	 */
	public String getQueryParam(String name) {
		if(queryParams == null)
			this.parseQueryString();
		return queryParams.get(name.toLowerCase());
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
	 * @see HttpServer#isParseCookies()
	 */
	public String getCookie(String name) {
		return cookies != null ? cookies.get(name) : null;
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
	 * Checks if this request contains the specified query param.
	 * The name is case-insensitive.
	 * 
	 * @param name name of the query param
	 * @return whether this request contains it
	 */
	public boolean hasQueryParam(String name) {
		if(queryParams == null)
			this.parseQueryString();
		return queryParams.containsKey(name.toLowerCase());
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
	 * @see HttpServer#isParseCookies()
	 */
	public boolean hasCookie(String name) {
		return cookies != null && cookies.containsKey(name);
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
	 * Returns a set of query params on this request.
	 * All names are in lowercase.
	 * 
	 * @return an immutable set of the query params
	 */
	public Set<String> getQueryParams() {
		if(queryParams == null)
			this.parseQueryString();
		return Collections.unmodifiableSet(queryParams.keySet());
	}
	
	/**
	 * Returns a set of cookies on this request.
	 * Cookie names are <i>case-sensitive</i>.
	 * 
	 * <br><br>
	 * <b>This will only work if cookie parsing is enabled.</b>
	 * 
	 * @return an immutable set of cookie names
	 * @see HttpServer#isParseCookies()
	 */
	public Set<String> getCookies() {
		return cookies != null ? Collections.unmodifiableSet(cookies.keySet()) : Collections.emptySet();
	}
	
	/**
	 * Gets this request's body. May be null.
	 * 
	 * @return the body, may be null
	 */
	public byte[] getBody() {
		return body;
	}
	
	/**
	 * Gets this request's body as a string.
	 * May be null.
	 * 
	 * @return the body, may be null
	 */
	public String getBodyAsString() {
		return body != null ? new String(body) : null;
	}
	
}
