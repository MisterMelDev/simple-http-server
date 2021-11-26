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
	private boolean isDaemon;
	
	private ClientHandler clientHandler;
	private Map<String, Route> routes = new HashMap<>();
	
	private ServerSocket socket;
	
	public HTTPServer(int port, boolean isDaemon) {
		this.port = port;
		this.isDaemon = isDaemon;
	}
	
	public void addRoute(String uri, Route route) {
		Objects.requireNonNull(uri, "uri is missing");
		Objects.requireNonNull(route, "route is missing");
		routes.put(uri.toLowerCase(), route);
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
	
	public void start() throws IOException {
		this.clientHandler = new ClientHandler();
		
		this.socket = new ServerSocket();
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(port));
		SocketListener socketListener = new SocketListener(this, socket);
		
		Thread thread = new Thread(socketListener);
		thread.setName("Socket Listener Thread");
		thread.setDaemon(isDaemon);
		thread.start();
	}
	
	public ClientHandler getClientHandler() {
		return clientHandler;
	}
	
	public int getPort() {
		return socket.getLocalPort();
	}
	
}
