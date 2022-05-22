package com.melluh.simplehttpserver.router;

import com.melluh.simplehttpserver.Request;
import com.melluh.simplehttpserver.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteFilter {

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\W]");

    private final String method;
    private final Pattern uriPattern;
    private final Route route;
    private final List<String> uriParams = new ArrayList<>();

    protected RouteFilter(String method, String uri, Route route) {
        this.method = method;
        this.uriPattern = getUriPattern(uri);
        this.route = route;
    }

    public Response serve(Request req) {
        if(!method.equals("*") && !method.equals(req.getMethod().name()))
            return null;

        Matcher uriMatcher = uriPattern.matcher(discardTrailingSlash(req.getLocation()));
        if(!uriMatcher.matches())
            return null;

        for(String uriParam : uriParams) {
            req.addUriParam(uriParam, uriMatcher.group(uriParam));
        }

        return route.serve(req);
    }

    private String discardTrailingSlash(String location) {
        if(location.endsWith("/"))
            location = location.substring(0, location.length() - 1);
        return location;
    }

    // This is a mess, but it works.
    private Pattern getUriPattern(String uri) {
        StringBuilder builder = new StringBuilder();
        for(String uriPart : uri.split("/")) {
            if(uriPart.startsWith(":")) {
                String paramName = uriPart.substring(1);
                uriParams.add(paramName);
                builder.append("(?<").append(paramName).append(">[^\\/]+)\\/");
                continue;
            }

            if(uriPart.equals("*")) {
                builder.append(".+\\/");
                continue;
            }

            builder.append(escape(uriPart)).append("\\/");
        }

        String regex = builder.substring(0, builder.length() - 2);
        return Pattern.compile(regex);
    }

    private static String escape(String uri) {
        return ESCAPE_PATTERN.matcher(uri).replaceAll("\\\\$0");
    }

}
