package com.melluh.simplehttpserver.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.melluh.simplehttpserver.HTTPUtils;
import com.melluh.simplehttpserver.Request;

/**
 * Represents a cookie that will be sent using the <code>Set-Cookie</code> header.
 * @see {@link Request#getCookie(String)}
 */
public class Cookie {

	private String name;
	private String value;
	
	private Long expires;
	private Long maxAge;
	
	private String domain;
	private String path;
	
	private boolean secure;
	private boolean httpOnly;
	
	private SameSiteMode sameSite;
	
	public Cookie(String name, String value) {
		Objects.requireNonNull(name, "name is missing");
		Objects.requireNonNull(value, "value is missing");
		
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Sets the timestamp the cookie will expire at.
	 * If unspecified, the cookie becomes a session cookie. A session
	 * finishes when the client shuts down, after which the session cookie
	 * is removed.
	 * 
	 * @param expires expiry time (unix time)
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie expires(long expires) {
		this.expires = expires;
		return this;
	}
	
	/**
	 * Sets the number of seconds until the cookie expires.
	 * A zero or negative number will expire the cookie immediately.
	 * If both <code>Expires</code> and <code>Max-Age</code> are set,
	 * <code>Max-Age</code> has precedence.
	 * 
	 * @param maxAge max age in seconds
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie maxAge(long maxAge) {
		this.maxAge = maxAge;
		return this;
	}
	
	/**
	 * Defines the host to which the cookie will be sent.
	 * If omitted, this attribute defaults to the host of the current
	 * document URL, not including subdomains. Contrary to earlier
	 * specifications, leading dots in domain names
	 * (<code>.example.com</code>) are ignored. Multiple host/domain
	 * values are <i>not</i> allowed, but if a domain <i>is</i>
	 * specified, then subdomains are always included.
	 * 
	 * @param domain the host to which the cookie will be sent
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie domain(String domain) {
		Objects.requireNonNull(domain, "domain is missing");
		this.domain = domain;
		return this;
	}
	
	/**
	 * Indicates the path that <i>must</i> exist in the requested
	 * URL for the browser to send the <code>Cookie</code> header.
	 * The forward slash character is interpreted as a directory
	 * seperator, and subdirectories are matched as well.
	 * 
	 * @param path the path for this cookie
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie path(String path) {
		Objects.requireNonNull(path, "path is missing");
		this.path = path;
		return this;
	}
	
	/**
	 * Sets whether this cookie has the <code>Secure</code> attribute.
	 * This indicates that the cookie is sent to the server only when
	 * a request is made with the <code>https:</code> scheme (except
	 * on localhost), and therefore, is more resistant to MITM attacks.
	 * 
	 * <br><br>
	 * This is required when using {@link SameSiteMode#NONE} on modern
	 * browsers.
	 * 
	 * @param secure whether this cookie has the Secure attribute
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie secure(boolean secure) {
		this.secure = secure;
		return this;
	}
	
	/**
	 * Sets whether this cookie has the <code>HttpOnly</code> attribute.
	 * This means JavaScript in the browser is forbidden from accessing
	 * the cookie, for example, through the Document.cookie property.
	 * Note that a cookie that has been created with <code>HttpOnly</code>
	 * will still be sent with JavaScript-initiated requests, for
	 * example, when calling <code>XMLHttpRequest.send()</code> or
	 * <code>fetch()</code>. This mitigates attacks against XSS.
	 * 
	 * @param httpOnly whether this cookie has the HttpOnly attribute
	 * @return a reference to this, so the API can be used fluently
	 */
	public Cookie httpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}
	
	/**
	 * Controls whether or not a cookie is sent with cross-origin
	 * requests, providing some protection againt cross-site request
	 * forgery attacks (CSRF).
	 * 
	 * @param sameSite the same site mode
	 * @return a reference to this, so the API can be used fluently
	 * @see {@link SameSiteMode}
	 */
	public Cookie sameSite(SameSiteMode sameSite) {
		Objects.requireNonNull(sameSite, "sameSite is missing");
		this.sameSite = sameSite;
		return this;
	}
	
	/**
	 * Method for internal use.
	 * Gets the header representation of this cookie.
	 * 
	 * @return header value
	 */
	public String getHeaderValue() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append("=").append(value);
		
		List<String> attributes = this.getAttributes();
		if(!attributes.isEmpty()) {
			builder.append("; ");
			builder.append(String.join("; ", attributes));
		}
		
		return builder.toString();
	}
	
	private List<String> getAttributes() {
		List<String> result = new ArrayList<>();
		
		if(expires != null)
			result.add("Expires=" + HTTPUtils.getFormattedTime(expires));
		
		if(maxAge != null)
			result.add("Max-Age=" + maxAge);
		
		if(domain != null)
			result.add("Domain=" + domain);
		
		if(path != null)
			result.add("Path=" + path);
		
		if(secure)
			result.add("Secure");
		
		if(httpOnly)
			result.add("HttpOnly");
		
		if(sameSite != null)
			result.add("SameSite=" + sameSite.getProtocolName());
		
		return result;
	}
	
	public static enum SameSiteMode {
		
		/**
		 * The browser sends the cookie only for same-site requests, that is,
		 * requests originating from the same site that set the cookie. If a
		 * request originates from a URL different from the current one, no
		 * cookies with the <code>SameSite=Strict</code> attribute are sent.
		 */
		STRICT("Strict"),
		
		/**
		 * The cookie is not sent on cross-site requests, such as on requests
		 * to load images or frames, but is sent when a user is navigating to
		 * the origin site from an external site (for example, when following
		 * a link). This is the default behaviour if the <code>SameSite</code>
		 * attribute is not specified.
		 */
		LAX("Lax"),
		
		/**
		 * The browser sends the cookie with both cross-site and same-site
		 * requests. The <code>Secure</code> attribute must also be set when
		 * setting this value.
		 * @see {@link Cookie#secure(boolean)}
		 */
		NONE("None");
		
		private String protocolName;
		
		private SameSiteMode(String protocolName) {
			this.protocolName = protocolName;
		}
		
		public String getProtocolName() {
			return protocolName;
		}
	}
	
	
}
