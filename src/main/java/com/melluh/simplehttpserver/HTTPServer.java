package com.melluh.simplehttpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;

public class HttpServer {
	
	public static final Logger LOGGER = Logger.getLogger("HTTPServer");
	
	private final int port;
	private boolean parseCookies = true;

	private final List<RequestHandler> requestHandlers = new ArrayList<>();
	private ClientHandler clientHandler;

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
		Response resp = null;
		try {
			for(RequestHandler handler : requestHandlers) {
				resp = handler.serve(request);
				if(resp != null)
					break;
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error in request handler", ex);
			return new Response(Status.INTERNAL_SERVER_ERROR);
		}
		
		if(resp == null) {
			resp = new Response(Status.NOT_FOUND);
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

	public HttpServer use(RequestHandler handler) {
		requestHandlers.add(0, handler);
		return this;
	}

	
}
