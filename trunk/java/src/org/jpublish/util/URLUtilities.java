/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jpublish.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility class for building URLs.
 *
 * @author Anthony Eden
 */

public class URLUtilities {

    /**
     * The URL path separator.
     */

    public static final String URL_PATH_SEPARATOR = "/";

    private static Log log = LogFactory.getLog(URLUtilities.class);

    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * Construct a new URLUtilities class which can use the given request
     * and response objects to build URLs.
     *
     * @param request  The request object
     * @param response The response object
     */

    public URLUtilities(HttpServletRequest request,
                        HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Build an HTTP URL relative to the application context using the given
     * path.
     *
     * @param path The path
     */

    public String buildStandard(String path) {
        return buildStandard(path, 0);
    }

    /**
     * Build an HTTP URL relative to the application context using the given
     * path.  This version of the <code>buildStandard</code> method allows you
     * to specify the port number.  A port number of 0 will cause the port
     * argument to be ignored.
     *
     * @param path The path
     * @param port The port
     */

    public String buildStandard(String path, int port) {
        return build(path, "http", port);
    }

    /**
     * Build an HTTPS (Secure Socket Layer) method relative to the application
     * context using the given path.
     *
     * @param path The path
     */

    public String buildSecure(String path) {
        return buildSecure(path, 0);
    }

    /**
     * Build an HTTPS (Secure Socket Layer) method relative to the application
     * context using the given path.  This version of the
     * <code>buildSecure</code> method allows you to specify the port number.
     * A port number of 0 will cause the port argument to be ignored.
     *
     * @param path The path
     * @param port The port
     */

    public String buildSecure(String path, int port) {
        return build(path, "https", port);
    }

    /**
     * Build a URL using the given path, protocol and port.  The path will be
     * relative to the current context.
     *
     * @param path     The path
     * @param protocol (i.e. http or https)
     * @param port     The port (0 to ignore the port argument)
     * @return The URL as a String
     */

    protected String build(String path, String protocol, int port) {
        String serverName = request.getServerName();
        String contextPath = request.getContextPath();

        if (log.isDebugEnabled()) {
            log.debug("Server name: " + serverName);
            log.debug("Context path: " + contextPath);
        }

        if (!contextPath.endsWith(URL_PATH_SEPARATOR)) {
            contextPath = contextPath + URL_PATH_SEPARATOR;
        }

        if (path.startsWith(URL_PATH_SEPARATOR)) {
            path = path.substring(1);
        }

        String requestPath = contextPath + path;

        if (log.isDebugEnabled())
            log.debug("Request path: " + requestPath);

        StringBuffer buffer = new StringBuffer();
        buffer.append(protocol);
        buffer.append("://");
        buffer.append(serverName);

        if (port > 0) {
            buffer.append(":");
            buffer.append(port);
        }

        if (!requestPath.startsWith(URL_PATH_SEPARATOR)) {
            buffer.append(URL_PATH_SEPARATOR);
        }

        buffer.append(requestPath);

        if (log.isDebugEnabled())
            log.debug("URL: '" + buffer + "'");

        return buffer.toString();
    }

    /**
     * Percent-encode the given String.  This method delegates to
     * the URLEncoder.encode() method.
     *
     * @param s The String to encode
     * @return The encoded String
     * @see java.net.URLEncoder
     */

    public String encode(String s) {
        try {
            return URLEncoder.encode(s, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return s + ", Unsupported Encoding Exception: " + e.getMessage();
        }
    }

    public String encode(String s, String encoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, encoding);
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
