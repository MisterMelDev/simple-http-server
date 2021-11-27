# Simple-HTTP-Server
A super simple and lightweight HTTP server written in Java.

### Features
- HTTP 1.1
- Supports cookies
- File serving/streaming
  - Supports [partial content responses](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/206) (`Range` header)
  - Supports [ETags](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/ETag)
  - Sends large files without memory overhead (e.g. ``mp4``)


### Basic Example
This starts a server on port 8080, with a single route returning plain text.
```java
HTTPServer server = new HTTPServer(8080)
  .route("/test", new Route() {
    @Override
    public Response handle(Request request) {
      return new Response(Status.OK)
        .contentType(MimeType.PLAIN_TEXT)
        .body("You requested /test! Method: " + request.getMethod());
    }
  })
  .start();
		
System.out.println("Server listening on port " + server.getPort());
```
