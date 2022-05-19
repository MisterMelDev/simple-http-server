package com.melluh.simplehttpserver.protocol;

/**
 * Contains all possible HTTP response status codes.<br>
 * See <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6">RFC 7231</a>.
 */
public enum Status {

	/**
	 * This interim response indicates that the client should continue the request or ignore the response
	 * if the request is already finished.
	 */
	CONTINUE(100, "Continue"),

	/**
	 * This code is sent in response to an <code>Upgrade</code> header from the client and indicates the
	 * protocol the server is switching to.
	 */
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	
	/**
	 * The request succeeded.
	 */
	OK(200, "OK"),
	
	/**
	 * The request succeeded, and a new resource was created as a result. This is typically the response
	 * sent after <code>POST</code> requests, or some <code>PUT</code> requests.
	 */
	CREATED(201, "Created"),
	
	/**
	 * The request has been received but not yet acted upon. It is noncommittal, since there is no way in
	 * HTTP to later send an asynchronous response indicating the outcome of the request. It is intended
	 * for cases where another process or server handles the request, or for batch processing.
	 */
	ACCEPTED(202, "Accepted"),
	
	/**
	 * This response code means the returned metadata is not exactly the same as is available from the origin
	 * server, but is collected from a local or a third-party copy. This is mostly used for mirrors or backups
	 * of another resource. Except for that specific case, the 200 OK response is preferred to this status.
	 */
	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
	
	/**
	 * There is no content to send for this request, but the headers may be useful. The user agent may update
	 * its cached headers for this resource with the new ones.
	 */
	NO_CONTENT(204, "No Content"),
	
	/**
	 * Tells the user agent to reset the document which sent this request.
	 */
	RESET_CONTENT(205, "Reset Content"),
	
	/**
	 * This response code is used when the <code>Range</code> header is sent from the client to request only
	 * part of a resource.
	 */
	PARTIAL_CONTENT(206, "Partial Content"),
	
	/**
	 * The request has more than one possible response. The user agent or user should choose one of them.
	 * (There is no standardized way of choosing one of the responses, but HTML links to the possibilities are
	 * recommended so the user can pick.)
	 */
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	
	/**
	 * The URL of the requested resource has been changed permanently. The new URL is given in the response.
	 */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	
	/**
	 * This response code means that the URI of requested resource has been changed temporarily. Further changes
	 * in the URI might be made in the future. Therefore, this same URI should be used by the client in future
	 * requests.
	 */
	FOUND(302, "Found"),
	
	/**
	 * The server sent this response to direct the client to get the requested resource at another URI with
	 * a GET request.
	 */
	SEE_OTHER(303, "See Other"),
	
	/**
	 * This is used for caching purposes. It tells the client that the response has not been modified, so the
	 * client can continue to use the same cached version of the response.
	 */
	NOT_MODIFIED(304, "Not Modified"),
	
	/**
	 * Defined in a previous version of the HTTP specification to indicate that a requested response must be
	 * accessed by a proxy. It has been deprecated due to security concerns regarding in-band configuration
	 * of a proxy.
	 */
	@Deprecated
	USE_PROXY(305, "Use Proxy"),
	
	/**
	 * The server sends this response to direct the client to get the requested resource at another URI with same
	 * method that was used in the prior request. This has the same semantics as the 302 Found HTTP response code,
	 * with the exception that the user agent must not change the HTTP method used: if a POST was used in the
	 * first request, a POST must be used in the second request.
	 */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	
	/**
	 * This means that the resource is now permanently located at another URI, specified by the
	 * <code>Location:</code> HTTP Response header. This has the same semantics as the <code>301 Moved Permanently</code>
	 * HTTP response code, with the exception that the user agent must not change the HTTP method
	 * used: if a POST was used in the first request, a POST must be used in the second request.
	 */
	PERMANENT_REDIRECT(308, "Permanent Redirect"),
	
	/**
	 * The server could not understand the request due to invalid syntax.
	 */
	BAD_REQUEST(400, "Bad Request"),
	
	/**
	 * Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated".
	 * That is, the client must authenticate itself to get the requested response.
	 */
	UNAUTHORIZED(401, "Unauthorized"),
	
	/**
	 * This response code is reserved for future use. The initial aim for creating this code was using it
	 * for digital payment systems, however this status code is used very rarely and no standard convention exists.
	 */
	PAYMENT_REQUIRED(402, "Payment Required"),
	
	/**
	 * The client does not have access rights to the content; that is, it is unauthorized, so the server
	 * is refusing to give the requested resource. Unlike <code>401 Unauthorized</code>, the client's
	 * identity is known to the server.
	 */
	FORBIDDEN(403, "Forbidden"),
	
	/**
	 * The server can not find the requested resource. In the browser, this means the URL is not recognized.
	 * In an API, this can also mean that the endpoint is valid but the resource itself does not exist.
	 * Servers may also send this response instead of 403 Forbidden to hide the existence of a resource from
	 * an unauthorized client. This response code is probably the most well known due to its frequent
	 * occurrence on the web.
	 */
	NOT_FOUND(404, "Not Found"),
	
	/**
	 * The request method is known by the server but is not supported by the target resource. For
	 * example, an API may not allow calling DELETE to remove a resource.
	 */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	
	/**
	 * This response is sent when the web server, after performing server-driven content negotiation, doesn't
	 * find any content that conforms to the criteria given by the user agent.
	 */
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	
	/**
	 * This is similar to <code>401 Unauthorized</code> but authentication is needed to be done by a proxy.
	 */
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	
	/**
	 * This response is sent on an idle connection by some servers, even without any previous request by the client.
	 * It means that the server would like to shut down this unused connection. This response is used much more
	 * since some browsers, like Chrome, Firefox 27+, or IE9, use HTTP pre-connection mechanisms to speed up
	 * surfing. Also note that some servers merely shut down the connection without sending this message.
	 */
	REQUEST_TIMEOUT(408, "Request Timeout"),
	
	/**
	 * This response is sent when a request conflicts with the current state of the server.
	 */
	CONFLICT(409, "Conflict"),
	
	/**
	 * This response is sent when the requested content has been permanently deleted from server, with no
	 * forwarding address. Clients are expected to remove their caches and links to the resource. The HTTP
	 * specification intends this status code to be used for "limited-time, promotional services". APIs
	 * should not feel compelled to indicate resources that have been deleted with this status code.
	 */
	GONE(410, "Gone"),
	
	/**
	 * Server rejected the request because the Content-Length header field is not defined and the server
	 * requires it.
	 */
	LENGTH_REQUIRED(411, "Length Required"),
	
	/**
	 * The client has indicated preconditions in its headers which the server does not meet.
	 */
	PRECONDITION_FAILED(412, "Precondition Failed"),
	
	/**
	 * Request entity is larger than limits defined by server. The server might close the connection or
	 * return an <code>Retry-After</code> header field.
	 */
	PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
	
	/**
	 * The URI requested by the client is longer than the server is willing to interpret.
	 */
	URI_TOO_LONG(414, "URI Too Long"),
	
	/**
	 * The media format of the requested data is not supported by the server, so the server is
	 * rejecting the request.
	 */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	
	/**
	 * The range specified by the <code>Range</code> header field in the request cannot be fulfilled.
	 * It's possible that the range is outside the size of the target URI's data.
	 */
	RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
	
	/**
	 * This response code means the expectation indicated by the <code>Expect</code> request header field
	 * cannot be met by the server.
	 */
	EXPECTATION_FAILED(417, "Expectation Failed"),
	
	/**
	 * Indicates that the server is unwilling to risk processing a request that might be replayed.
	 */
	TOO_EARLY(425, "Too Early"),
	
	/**
	 * The server refuses to perform the request using the current protocol but might be willing
	 * to do so after the client upgrades to a different protocol. The server sends an <code>Upgrade</code>
	 * header in a 426 response to indicate the required protocol(s).
	 */
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	
	/**
	 * The origin server requires the request to be conditional. This response is intended to prevent the
	 * 'lost update' problem, where a client GETs a resource's state, modifies it and PUTs it back to the
	 * server, when meanwhile a third party has modified the state on the server, leading to a conflict.
	 */
	PRECONDITION_REQUIRED(428, "Precondition Required"),
	
	/**
	 * The user has sent too many requests in a given amount of time ("rate limiting").
	 */
	TOO_MANY_REQUESTS(429, "Too Many Requests"),
	
	/**
	 * The server is unwilling to process the request because its header fields are too large. The request
	 * may be resubmitted after reducing the size of the request header fields.
	 */
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
	
	/**
	 * The user agent requested a resource that cannot legally be provided, such as a web page censored
	 * by a government.
	 */
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
	
	/**
	 * The server has encountered a situation it does not know how to handle.
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	
	/**
	 * The request method is not supported by the server and cannot be handled. The only methods that servers
	 * are required to support (and therefore that must not return this code) are GET and HEAD.
	 */
	NOT_IMPLEMENTED(501, "Not Implemented"),
	
	/**
	 * This error response means that the server, while working as a gateway to get a response needed to
	 * handle the request, got an invalid response.
	 */
	BAD_GATEWAY(502, "Bad Gateway"),
	
	/**
	 * The server is not ready to handle the request. Common causes are a server that is down for maintenance
	 * or that is overloaded. Note that together with this response, a user-friendly page explaining the problem
	 * should be sent. This response should be used for temporary conditions and the <code>Retry-After</code>
	 * HTTP header should, if possible, contain the estimated time before the recovery of the service. The
	 * webmaster must also take care about the caching-related headers that are sent along with this response,
	 * as these temporary condition responses should usually not be cached.
	 */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	
	/**
	 * This error response is given when the server is acting as a gateway and cannot get a response in time.
	 */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	
	/**
	 * The HTTP version used in the request is not supported by the server.
	 */
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
	
	/**
	 * The server has an internal configuration error: the chosen variant resource is configured to engage in
	 * transparent content negotiation itself, and is therefore not a proper end point in the negotiation process.
	 */
	VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
	
	/**
	 * Further extensions to the request are required for the server to fulfill it.
	 */
	NOT_EXTENDED(510, "Not Extended"),
	
	/**
	 * Indicates that the client needs to authenticate to gain network access.
	 */
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
	
	private final int code;
	private final String description;
	
	Status(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return code + " " + description;
	}
	
}
