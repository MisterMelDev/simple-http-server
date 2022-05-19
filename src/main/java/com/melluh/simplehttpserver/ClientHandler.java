package com.melluh.simplehttpserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
	
	private final List<ServerClient> clients = Collections.synchronizedList(new ArrayList<>());
	private final ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * Returns a list of all server clients with an active connection.
	 * 
	 * @return an immutable list of active clients
	 * @see ServerClient
	 */
	public List<ServerClient> getClients() {
		return Collections.unmodifiableList(clients);
	}
	
	protected void acceptClient(ServerClient client) {
		clients.add(client);
		executor.submit(client);
	}
	
	protected void closed(ServerClient client) {
		clients.remove(client);
	}
	
}
