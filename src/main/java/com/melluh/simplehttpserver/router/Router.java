package com.melluh.simplehttpserver.router;

import com.melluh.simplehttpserver.Request;
import com.melluh.simplehttpserver.RequestHandler;
import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;

import java.util.ArrayList;
import java.util.List;

public class Router implements RequestHandler {

    private final List<RouteFilter> filters = new ArrayList<>();

    public Router all(Route route) {
        this.addRoute("*", "*", route);
        return this;
    }

    public Router all(String uri, Route route) {
        this.addRoute("*", uri, route);
        return this;
    }

    public Router get(String uri, Route route) {
        this.addRoute("GET", uri, route);
        return this;
    }

    public Router post(String uri, Route route) {
        this.addRoute("POST", uri, route);
        return this;
    }

    public Router put(String uri, Route route) {
        this.addRoute("PUT", uri, route);
        return this;
    }

    public Router delete(String uri, Route route) {
        this.addRoute("DELETE", uri, route);
        return this;
    }

    private void addRoute(String method, String uri, Route route) {
        filters.add(new RouteFilter(method, uri, route));
    }

    @Override
    public Response serve(Request req) {
        for(RouteFilter filter : filters) {
            if(filter.matches(req))
                return filter.serve(req);
        }

        return null;
    }

}
