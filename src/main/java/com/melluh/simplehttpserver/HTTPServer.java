package com.melluh.simplehttpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.logging.Logger;

import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;

public class HttpServer {
	
	public static final Logger LOGGER = Logger.getLogger("HTTPServer");
	
	private int port;
	private boolean parseCookies = true;
	
	private ClientHandler clientHandler;
	private RequestHandler requestHandler;
	
	private ServerSocket socket;
	
	/**
	 * Creates a new HTTP server, running on the
	 * specified port.
	 * 
	 * @param port the port to run on
	 */
	public HttpServer(int port) {
		this.port = port;
	}
	
	/**
	 * Enables or disables parsing the Cookie header in requests.
	 * 
	 * @param parseCookies whether to parse cookies
	 * @return a reference to this, so the API can be used fluently
	 */
	public HttpServer parseCookies(boolean parseCookies) {
		this.parseCookies = parseCookies;
		return this;
	}
	
	/**
	 * Adds a request handler to the server. Only a single request
	 * handler can be used per server.
	 * 
	 * @param requestHandler the request handler
	 * @return a reference to this, so the API can be used fluently
	 * @see {@link RequestHandler}
	 */
	public HttpServer requestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
		return this;
	}
	
	/**
	 * Starts the server.
	 * 
	 * @return a reference to this, so the API can be used fluently
	 * @throws IOException if an error occurs opening the socket server
	 */
	public HttpServer start() throws IOException {
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
		if(requestHandler == null) {
			return new Response(Status.NO_CONTENT);
		}
		
		Response resp = requestHandler.handle(request);
		if(resp == null) {
			LOGGER.severe("Request handler returned null response");
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
