package com.melluh.simplehttpserver.response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.melluh.simplehttpserver.protocol.MimeType;
import com.melluh.simplehttpserver.protocol.Status;

/**
 * A response which will be sent to a HTTP client.
 */
public class Response {
	
	private Status status;
	private Map<String, String> headers = new HashMap<>();
	private Set<Cookie> cookies = new HashSet<>();
	private ResponseBody body;
	
	/**
	 * Creates a new response, with the specified status.
	 * 
	 * @param status the response status
	 * @see {@link Status}
	 */
	public Response(Status status) {
		this.status = status;
	}
	
	/**
	 * Adds a new header to the response, overwriting the existing value if present.
	 * 
	 * @param name name of the header
	 * @param value value of the header
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response header(String name, String value) {
		Objects.requireNonNull(name, "name is missing");
		Objects.requireNonNull(value, "value is missing");
		
		headers.put(name.toLowerCase(), value);
		return this;
	}
	
	/**
	 * Adds a new header to the response, if it doesn't exist already.
	 * 
	 * @param name name of the header
	 * @param value value of the header
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response optHeader(String name, String value) {
		Objects.requireNonNull(name, "name is missing");
		Objects.requireNonNull(value, "value is missing");
		
		name = name.toLowerCase();
		if(headers.containsKey(name))
			return this;
		headers.put(name, value);
		return this;
	}
	
	/**
	 * Adds a Content-Type header to the response, overwriting the existing value if present.
	 * 
	 * @param mimeType the mime type
	 * @return a reference to this, so the API can be used fluently
	 * @see {@link MimeType}, {@link #header(String, String)}
	 */
	public Response contentType(String mimeType) {
		Objects.requireNonNull(mimeType, "mimeType is missing");	
		headers.put("content-type", mimeType);
		return this;
	}
	
	/**
	 * Adds a body to the response, overwriting the existing body if present.
	 * Uses a {@link StringResponseBody} internally.
	 * 
	 * @param body the body
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response body(String body) {
		this.body(new StringResponseBody(body));
		return this;
	}
	
	/**
	 * Adds a body to the response, overwriting the existing body if present.
	 * 
	 * @param body the body
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response body(byte[] body) {
		this.body(new StringResponseBody(body));
		return this;
	}
	
	/**
	 * Adds a body to the response, overwriting the existing body if present.
	 * 
	 * @param body the body
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response body(ResponseBody body) {
		Objects.requireNonNull(body, "body is missing");
		this.body = body;
		return this;
	}
	
	/**
	 * Adds a cookie to the response. This can be called multiple times to add multiple cookies.
	 * 
	 * @param cookie cookie to add
	 * @return a reference to this, so the API can be used fluently
	 */
	public Response cookie(Cookie cookie) {
		Objects.requireNonNull(cookie, "cookie is missing");
		cookies.add(cookie);
		return this;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public Set<Cookie> getCookies() {
		return cookies;
	}
	
	public boolean hasBody() {
		return body != null;
	}
	
	public ResponseBody getBody() {
		return body;
	}
	
}
