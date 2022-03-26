package com.melluh.simplehttpserver.router;

import com.melluh.simplehttpserver.Request;
import com.melluh.simplehttpserver.response.Response;

import java.util.regex.Pattern;

public class RouteFilter {

    private final String method;
    private final Pattern uriPattern;
    private final Route route;

    protected RouteFilter(String method, String uri, Route route) {
        this.method = method;
        this.uriPattern = getUriPattern(uri);
        this.route = route;
    }

    public boolean matches(Request req) {
        if(!method.equals("*") && !method.equals(req.getMethod().name()))
            return false;

        return uriPattern.matcher(req.getLocation().toLowerCase()).matches();
    }

    public Response serve(Request req) {
        return route.serve(req);
    }

    private static Pattern getUriPattern(String uri) {
        String regex = uri.toLowerCase().replaceAll("[\\W]", "\\\\$0").replace("\\*", ".+");
        return Pattern.compile(regex);
    }

}
