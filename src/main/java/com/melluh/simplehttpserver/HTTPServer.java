package com.melluh.simplehttpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.melluh.simplehttpserver.protocol.MimeType;
import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;

public class HTTPServer {
	
	public static final Logger LOGGER = Logger.getLogger("HTTPServer");
	
	private int port;
	private Map<String, Route> routes = new HashMap<>();
	
	private ClientHandler clientHandler;
	private ServerSocket socket;
	
	private boolean parseCookies = true;
	
	/**
	 * Creates a new HTTP server, running on the
	 * specified port.
	 * 
	 * @param port the port to run on
	 */
	public HTTPServer(int port) {
		this.port = port;
	}
	
	/**
	 * Adds a route to the server at the specified uri.
	 * 
	 * @param uri the uri
	 * @param route the route
	 * @return a reference to this, so the API can be used fluently
	 * @see {@link Route}
	 */
	public HTTPServer route(String uri, Route route) {
		Objects.requireNonNull(uri, "uri is missing");
		Objects.requireNonNull(route, "route is missing");
		routes.put(uri.toLowerCase(), route);
		return this;
	}
	
	/**
	 * Enables or disables parsing the Cookie header in requests.
	 * 
	 * @param parseCookies whether to parse cookies
	 * @return a reference to this, so the API can be used fluently
	 */
	public HTTPServer parseCookies(boolean parseCookies) {
		this.parseCookies = parseCookies;
		return this;
	}
	
	/**
	 * Starts the server.
	 * 
	 * @return a reference to this, so the API can be used fluently
	 * @throws IOException if an error occurs opening the socket server
	 */
	public HTTPServer start() throws IOException {
		this.clientHandler = new ClientHandler();
		
		this.socket = new ServerSocket();
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(port));
		SocketListener socketListener = new SocketListener(this, socket);
		
		Thread thread = new Thread(socketListener);
		thread.setName("Socket Listener Thread");
		thread.start();
		
		return this;
	}
	
	protected Response handleRequest(Request request) {
		Route route = routes.get(request.getLocation());
		if(route == null) {
			return new Response(Status.NOT_FOUND)
					.contentType(MimeType.PLAIN_TEXT)
					.body("Not found");
		}
		
		Response resp = route.handle(request);
		if(resp == null) {
			LOGGER.severe("Route " + request.getLocation() + " returned null response");
			return new Response(Status.INTERNAL_SERVER_ERROR);
		}
		
		return resp;
	}
	
	/**
	 * Returns this server's client handler.
	 * 
	 * @return the client handler
	 * @see {@link ClientHandler}
	 */
	public ClientHandler getClientHandler() {
		return clientHandler;
	}
	
	/**
	 * Returns the the port the server is listening on.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns whether cookie parsing is enabled.
	 * 
	 * @return true if cookie parsing is enabled
	 */
	public boolean isParseCookies() {
		return parseCookies;
	}
	
}
