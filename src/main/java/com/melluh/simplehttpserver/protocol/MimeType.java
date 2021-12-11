package com.melluh.simplehttpserver.protocol;

import java.util.HashMap;
import java.util.Map;

import com.melluh.simplehttpserver.response.Response;

/**
 * Contains some commonly used MIME types, to be used in the <code>Content-Type</code> header of a response.
 * @see {@link Response#contentType(String)}
 */
public class MimeType {

	private MimeType() {}
	
	public static String PLAIN_TEXT = "text/plain";
	public static String CSS = "text/css";
	public static String HTML = "text/html";
	public static String JAVASCRIPT = "text/javascript";
	public static String OCTET_STREAM = "application/octet-stream";
	public static String JSON = "application/json";
	public static String IMAGE_PNG = "image/png";
	public static String IMAGE_GIF = "image/gif";
	public static String IMAGE_JPEG = "image/jpeg";
	public static String IMAGE_WEBP = "image/webp";
	public static String AUDIO_WAV = "audio/wav";
	public static String AUDIO_WEBM = "audio/webm";
	public static String VIDEO_WEBM = "video/webm";
	public static String VIDEO_MP4 = "video/mp4";
	
	private static Map<String, String> FILE_EXTENSIONS = new HashMap<>();
	
	static {
		FILE_EXTENSIONS.put("txt", PLAIN_TEXT);
		FILE_EXTENSIONS.put("html", HTML);
		FILE_EXTENSIONS.put("css", CSS);
		FILE_EXTENSIONS.put("js", JAVASCRIPT);
		FILE_EXTENSIONS.put("json", JSON);
		FILE_EXTENSIONS.put("png", IMAGE_PNG);
		FILE_EXTENSIONS.put("gif", IMAGE_GIF);
		FILE_EXTENSIONS.put("jpg", IMAGE_JPEG);
		FILE_EXTENSIONS.put("jpeg", IMAGE_JPEG);
		FILE_EXTENSIONS.put("webp", IMAGE_WEBP);
		FILE_EXTENSIONS.put("wav", AUDIO_WAV);
		FILE_EXTENSIONS.put("webm", VIDEO_WEBM);
		FILE_EXTENSIONS.put("mp4", VIDEO_MP4);
	}
	
	/**
	 * Returns the MIME type corresponding to a file extension.
	 * If the extension is unknown, <code>application/octet-stream</code> is returned.
	 * 
	 * @param fileName complete file name
	 * @return MIME type corresponding to the file extension
	 */
	public static String getMimeType(String fileName) {
		int dotIndex = fileName.indexOf('.');
		if(dotIndex < 0)
			return OCTET_STREAM;
		String extension = fileName.substring(dotIndex + 1);
		return FILE_EXTENSIONS.getOrDefault(extension, OCTET_STREAM);
	}
	
}
