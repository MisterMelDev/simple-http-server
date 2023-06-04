package examples;

import java.io.IOException;

import com.melluh.simplehttpserver.HttpServer;
import com.melluh.simplehttpserver.Request;
import com.melluh.simplehttpserver.RequestHandler;
import com.melluh.simplehttpserver.protocol.Method;
import com.melluh.simplehttpserver.protocol.MimeType;
import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;
import com.melluh.simplehttpserver.router.Router;

public class WebServer {
	
	public void start(int port) {
		try {
			HttpServer server = new HttpServer(port)
					.use(new LoggingFilter())
					.use(new Router()
							.get("/hello", req -> new Response(Status.OK).body("Hello, world!"))
							.get("/testing/:id", req -> new Response(Status.OK).body("Hello, " + req.getUriParam("id")))
							.get("/testing/:id/:test", req -> new Response(Status.OK).body("Hello, " + req.getUriParam("id") + "! " + req.getUriParam("test")))
							.get("/*", req -> new Response(Status.OK).body("Wildcard!"))
							.get("/test/*", req -> new Response(Status.OK).body("Wildcard 2!"))
					)
					.start();

			System.out.println("Web server listening on port " + port);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static class LoggingFilter implements RequestHandler {
		@Override
		public Response serve(Request request) {
			System.out.println(request.getMethod() + " " + request.getLocation());
			return null;
		}
	}
	
	public static void main(String[] args) {
		new WebServer().start(8085);
	}
	
}
