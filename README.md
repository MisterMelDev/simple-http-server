# Simple-HTTP-Server
A super simple and lightweight HTTP server written in Java.

### Features
- HTTP/1.1
- Supports cookies
- File serving/streaming
  - Supports [partial content responses](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/206) (`Range` header)
  - Supports [ETags](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/ETag)
  - Sends large files without memory overhead (e.g. ``mp4``)


### Basic Example
This starts a server on port 8080, with a single route returning plain text.
```java
HttpServer server = new HttpServer(8080)
    .use(new Router()
        .get("/hello", req -> new Response(Status.OK).body("Hello, world!"))
    )
    .start();

System.out.println("Web server listening on port " + server.getPort());
```
