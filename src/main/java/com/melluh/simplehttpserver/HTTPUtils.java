package com.melluh.simplehttpserver;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;

import com.melluh.simplehttpserver.protocol.MimeType;
import com.melluh.simplehttpserver.protocol.Status;
import com.melluh.simplehttpserver.response.Response;
import com.melluh.simplehttpserver.response.StreamResponseBody;

public class HTTPUtils {

	private HTTPUtils() {}
	
	public static String decodePercent(String str) {
		return URLDecoder.decode(str, StandardCharsets.UTF_8);
	}
	
	private static final DateTimeFormatter HTTP_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("GMT"));
	
	public static String getFormattedTime() {
		return HTTP_TIME_FORMATTER.format(Instant.now());
	}
	
	public static String getFormattedTime(long timestamp) {
		return HTTP_TIME_FORMATTER.format(Instant.ofEpochMilli(timestamp));
	}
	
	public static void close(Closeable closeable) {
		if(closeable == null)
			return;
		try {
			closeable.close();
		} catch (IOException ex) {
			HTTPServer.LOGGER.log(Level.SEVERE, "Failed to close", ex);
		}
	}
	
	public static Response serveFile(Request request, File file) throws IOException {
		String mimeType = MimeType.getMimeType(file.getName());
		String lastModified = getFormattedTime(file.lastModified());
		String etag = getETag(file);
		
		long from = -1;
		long to = -1;
		
		String ifRange = request.getHeader("if-range");
		boolean ifRangeMatches = ifRange == null || ifRange.equals(etag);
		
		String range = request.getHeader("range");
		if(range != null && range.startsWith("bytes=") && ifRangeMatches) {
			range = range.substring("bytes=".length());
			int minusIndex = range.indexOf('-');
			if(minusIndex >= 0) {
				try {
					from = Long.parseLong(range.substring(0, minusIndex));
					to = Long.parseLong(range.substring(minusIndex + 1));
				} catch (NumberFormatException ignored) {}
			}
		}
		
		String ifNoneMatch = request.getHeader("if-none-match");
		if(ifNoneMatch != null && (ifNoneMatch.equals("*") || ifNoneMatch.equals(etag))) {
			return new Response(Status.NOT_MODIFIED)
					.header("etag", etag);
		}
		
		long fileLength = file.length();
		if(from >= 0 && from < fileLength) {
			if(to < 0) {
				to = fileLength - 1;
			}
			
			long newLength = to - from + 1;
			FileInputStream in = new FileInputStream(file);
			in.skip(from);
			
			return new Response(Status.PARTIAL_CONTENT)
					.header("content-type", mimeType)
					.header("last-modified", lastModified)
					.header("accept-ranges", "bytes")
					.header("content-range", "bytes " + from + "-" + to + "/" + fileLength)
					.header("cache-control", "private, max-age=3600")
					.header("etag", etag)
					.body(new StreamResponseBody(in, newLength));
		}
		
		FileInputStream in = new FileInputStream(file);
		return new Response(Status.OK)
				.header("content-type", mimeType)
				.header("last-modified", lastModified)
				.header("accept-ranges", "bytes")
				.header("cache-control", "private, max-age=3600")
				.header("etag", etag)
				.body(new StreamResponseBody(in, fileLength));
	}
	
	private static String getETag(File file) {
		return Integer.toHexString((file.getAbsolutePath() + file.lastModified() + file.length()).hashCode());
	}
	
	public static int safeParseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}
	
}
