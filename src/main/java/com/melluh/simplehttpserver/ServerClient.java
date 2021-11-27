package com.melluh.simplehttpserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;

import com.melluh.simplehttpserver.protocol.HTTPHeader;
import com.melluh.simplehttpserver.protocol.Method;
import com.melluh.simplehttpserver.protocol.MimeType;
import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;

public class ServerClient implements Runnable {

	private static final String SERVER_HEADER = "simple-http-server";
	private static final int HEADER_BUFFER_SIZE = 8192;
	
	private HTTPServer server;
	private Socket socket;
	private InputStream in;
	
	private Request request;
	
	public ServerClient(HTTPServer server, Socket socket, InputStream in) {
		this.server = server;
		this.socket = socket;
		this.in = in;
	}
	
	@Override
	public void run() {
		try {
			byte[] header = new byte[HEADER_BUFFER_SIZE];
			int position = 0;
			
			while(true) {
				int read = in.read();
				if(read == -1)
					break;
				
				header[position] = (byte) read;
				
				// look for \r\n\r\n
				if(position >= 4 && header[position - 3] == '\r' && header[position - 2] == '\n' && header[position - 1] == '\r' && header[position] == '\n')
					break;
				
				position++;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(header, 0, position)));
			
			// Read status line
			String statusLine = reader.readLine();
			if(statusLine == null) {
				this.sendResponse(new Response(Status.BAD_REQUEST).contentType(MimeType.PLAIN_TEXT).body("Missing status line"));
				return;
			}
			
			String[] statusParams = statusLine.split(" ");
			if(statusParams.length != 3) {
				this.sendResponse(new Response(Status.BAD_REQUEST).contentType(MimeType.PLAIN_TEXT).body("Malformed status line"));
				return;
			}
			
			Method method = Method.getMethod(statusParams[0]);
			if(method == null) {
				this.sendResponse(new Response(Status.METHOD_NOT_ALLOWED).contentType(MimeType.PLAIN_TEXT).body("Method not supported by server implementation"));
				return;
			}
			
			try {
				this.request = new Request(server, method, statusParams[1], statusParams[2]);
			} catch (ParseException ex) {
				this.sendResponse(ex.createResponse());
				return;
			}
			
			if(!request.getProtocolVersion().equals("HTTP/1.0") && !request.getProtocolVersion().equals("HTTP/1.1")) {
				this.sendResponse(new Response(Status.HTTP_VERSION_NOT_SUPPORTED));
				return;
			}
			
			// Read headers
			String headerLine;
			while((headerLine = reader.readLine()) != null) {
				if(headerLine.isEmpty()) // end of request
					break;
				
				int colonIndex = headerLine.indexOf(':');
				if(colonIndex < 0) {
					this.sendResponse(new Response(Status.BAD_REQUEST).contentType(MimeType.PLAIN_TEXT).body("Malformed headers"));
					return;
				}
				
				String name = headerLine.substring(0, colonIndex).trim();
				String value = headerLine.substring(colonIndex + 1).trim();
				request.addHeader(name.toLowerCase(), value);
			}
			
			reader.close();
			
			// read body, if it's present
			if(request.hasHeader(HTTPHeader.CONTENT_LENGTH)) {
				int bodyLength = HTTPUtils.safeParseInt(request.getHeader(HTTPHeader.CONTENT_LENGTH));
				if(bodyLength > 0) {
					byte[] body = new byte[bodyLength];
					int read, totalRead = 0;
					while(bodyLength - totalRead > 0 && (read = in.read(body, totalRead, bodyLength - totalRead)) > -1) {
						totalRead += read;
					}
					
					if(totalRead < bodyLength) {
						this.sendResponse(new Response(Status.BAD_REQUEST).contentType(MimeType.PLAIN_TEXT).body("Unable to read complete body"));
						return;
					}
					
					request.setBody(body);
				}
			}
			
			this.sendResponse(server.handleRequest(request));
		} catch (IOException ex) {
			HTTPServer.LOGGER.log(Level.SEVERE, "Error handling client connection", ex);
		} finally {
			server.getClientHandler().closed(this);
		}
	}
	
	private void sendResponse(Response response) throws IOException {
		boolean sendBody = request.getMethod() != Method.HEAD && response.getStatus() != Status.NO_CONTENT && response.hasBody();
		if(response.hasBody() && !sendBody) {
			response.getBody().close();
		}
		
		if(sendBody) {
			response.optHeader(HTTPHeader.CONTENT_LENGTH, String.valueOf(response.getBody().getLength()));
		}
		
		response.optHeader(HTTPHeader.SERVER, SERVER_HEADER);
		response.header(HTTPHeader.CONNECTION, "close"); // Implementation does not support keep-alive
		
		OutputStream out = socket.getOutputStream();
		
		StringBuilder header = new StringBuilder();
		header.append("HTTP/1.1 ").append(response.getStatus().toString()).append("\r\n");
		response.getHeaders().forEach((name, value) -> header.append(name).append(": ").append(value).append("\r\n"));
		response.getCookies().forEach(cookie -> header.append(HTTPHeader.SET_COOKIE).append(": ").append(cookie.getHeaderValue()).append("\r\n"));
		header.append("\r\n");
		out.write(header.toString().getBytes());
		
		if(sendBody) {
			response.getBody().write(out);
		}
		
		HTTPUtils.close(out);
		HTTPUtils.close(socket);
	}
	
	public static class ParseException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		private Status status;
		
		public ParseException(Status status, String msg) {
			super(msg);
			this.status = status;
		}
		
		public Status getStatus() {
			return status;
		}
		
		public Response createResponse() {
			return new Response(status).body(this.getMessage());
		}
		
	}
	
}
