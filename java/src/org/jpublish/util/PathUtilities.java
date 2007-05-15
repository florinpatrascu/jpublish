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

import java.io.File;

/**
 * Utility class for working with request paths.
 *
 * @author Anthony Eden
 */

public final class PathUtilities {

    public static final String WILDCARD = "*";
    public static final String TEMPLATE_PROTOCOL = "template";
    public static final String REPOSITORY_PROTOCOL = "repository";

    private static final Log log = LogFactory.getLog(PathUtilities.class);

    /**
     * Internal constructor.
     */

    private PathUtilities() {
        // no op
    }

    /**
     * Match a path which may contain a wildcard.
     *
     * @param requestPath The request path submitted by the client
     * @param exPath      The match path
     */

    public static boolean match(String requestPath, String exPath) {
        int wildcardIndex = exPath.indexOf(WILDCARD);
        if (wildcardIndex == -1) {
            return requestPath.equals(exPath);
        } else {
            if (log.isDebugEnabled())
                log.debug("Wildcard index: " + wildcardIndex);
            if (wildcardIndex == (exPath.length() - 1)) {
                String checkString = exPath.substring(0, exPath.length() - 1);
                return requestPath.startsWith(checkString);
            } else {
                String preMatch = exPath.substring(0, wildcardIndex);
                String postMatch = exPath.substring(wildcardIndex + 1);

                return requestPath.startsWith(preMatch) && requestPath.endsWith(postMatch);
            }
        }
    }

    /**
     * Extract the page name from the given path.  The page name is the
     * name of the file in the path without its suffix.
     *
     * @param path The request path
     * @return The page name
     */

    public static String extractPageName(String path) {
        File file = new File(path);

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }

        return fileName.substring(0, dotIndex);
    }

    /**
     * Extract the page path from the given request path.  This method
     * will return the path from the page root to the page descriptor
     * file.
     *
     * @param path The request path
     * @return The page path
     */

    public static String extractPagePath(String path) {
        File file = new File(path);
        File parentDirectory = file.getParentFile();

        String pagePath;

        if (parentDirectory == null) {
            pagePath = extractPageName(path);
        } else {
            pagePath = new File(parentDirectory.getPath(), extractPageName(path)).getPath();
            pagePath = pagePath.replace(File.separatorChar, '/');
        }

        return pagePath;
    }

    /**
     * Return the page type extracting it from the path.  For example:
     * index.html would return "html" as the page type.  If the type
     * cannot be determined then this method returns null.
     *
     * @param path The path
     * @return The page type
     */

    public static String extractPageType(String path) {
        File file = new File(path);

        String fileName = file.getName();

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }

        return fileName.substring(dotIndex + 1);
    }

    /**
     * Return a path String which includes a starting slash so that it
     * matches the requirements of the Servlet API's getResource() methods.
     *
     * @param path The path
     * @return The correct resource path
     * @since 2.0
     */

    public static String toResourcePath(String path) {
        if (path.startsWith("/")) {
            return path;
        } else {
            return "/" + path;
        }
    }

    /**
     * Make a URI path for a template.
     *
     * @param path The relative template path
     * @return The URI
     * @since 2.0
     */

    // Note: maybe move this into the TemplateManager?
    public static String makeTemplateURI(String path) {
        InternalURI uri = new InternalURI();
        uri.setProtocol(TEMPLATE_PROTOCOL);
        uri.setPath(path);
        return uri.toURI();
    }

    /**
     * Make a URI path for a repository item.
     *
     * @param repositoryName The repository name
     * @param path           The relative path
     * @return The URI
     * @since 2.0
     */

    // Note: maybe move this into the Repository?
    public static String makeRepositoryURI(String repositoryName, String path) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(REPOSITORY_PROTOCOL);
        buffer.append(":");
        buffer.append(repositoryName);
        buffer.append("://");
        buffer.append(path);
        return buffer.toString();
    }

}
