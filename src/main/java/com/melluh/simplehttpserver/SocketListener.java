package com.melluh.simplehttpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class SocketListener implements Runnable {

	private HttpServer server;
	private ServerSocket socket;
	
	public SocketListener(HttpServer server, ServerSocket socket) {
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		while(!socket.isClosed()) {
			try {
				Socket clientSocket = socket.accept();
				InputStream in = clientSocket.getInputStream();
				server.getClientHandler().acceptClient(new ServerClient(server, clientSocket, in));
			} catch (IOException ex) {
				HttpServer.LOGGER.log(Level.SEVERE, "Error handling client connection", ex);
			}
		}
	}
	
}
