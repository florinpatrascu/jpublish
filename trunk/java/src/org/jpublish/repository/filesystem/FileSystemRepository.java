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

package org.jpublish.repository.filesystem;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.Configurable;
import org.jpublish.JPublishCacheException;
import org.jpublish.JPublishContext;
import org.jpublish.util.*;
import org.jpublish.view.ViewRenderer;

import java.io.*;
import java.util.Iterator;

/**
 * An implementation of the Repository interface which pulls content from
 * the local file system.  This implementation supports reading and optionally
 * supports writing.  By default writing is not allowed.
 *
 * @author Anthony Eden
 */

public class FileSystemRepository extends AbstractFileSystemRepository implements Configurable {

    private static Log log = LogFactory.getLog(FileSystemRepository.class);
    private JPublishCache jPublishCache = null;

    /**
     * Get the content from the given path.  Implementations of this method
     * should NOT merge the content using view renderer.
     *
     * @param path The relative content path
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path) throws Exception {
        return loadContent(path);
    }

    /**
     * Get the content from the given path and merge it with
     * the given context.
     *
     * @param path    The content path
     * @param context The current context
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path, JPublishContext context) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Getting dynamic content element for path " + path);

        StringWriter writer = null;
        BufferedReader reader = null;
        Reader in = null;

        try {
            in = new StringReader(loadContent(path));
            reader = new BufferedReader(in);
            writer = new StringWriter();

            String name = PathUtilities.makeRepositoryURI(getName(), path);
            ViewRenderer renderer = siteContext.getViewRenderer();
            renderer.render(context, name, reader, writer);

            return writer.toString();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new FileNotFoundException("File not found: " + path);
        } finally {
            IOUtilities.close(reader);
            IOUtilities.close(writer);
            IOUtilities.close(in);
        }
    }

    public void remove(String path) throws Exception {
        pathToFile(path).delete();
    }

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path) {
        File file = new File(getRealRoot(), path);
        file.mkdirs();
    }

    /**
     * Remove the directory for the specified path.  The directory
     * must be empty.
     *
     * @param path The path
     * @throws Exception
     */

    public void removeDirectory(String path) throws Exception {
        log.info("Remove directory: " + path);
        File file = new File(getRealRoot(), path);
        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());
        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
    }

    /**
     * Get the given content as an InputStream.  The InputStream will
     * return the raw content data.
     *
     * @param path The path to the content
     * @return The InputStream
     * @throws Exception
     */

    public InputStream getInputStream(String path) throws Exception {
        return new FileInputStream(pathToFile(path));
    }

    /**
     * Get an OutputStream for writing content to the given path.
     *
     * @param path The path to the content
     * @return The OutputStream
     * @throws Exception
     */

    public OutputStream getOutputStream(String path) throws Exception {
        if (!isWriteAllowed()) {
            throw new SecurityException("Writing not allowed");
        }

        return new FileOutputStream(pathToFile(path));
    }

    /**
     * Get the last modified time in milliseconds for the given path.
     *
     * @param path The content path
     * @return The last modified time in milliseconds
     * @throws Exception Any Exception
     */

    public long getLastModified(String path) throws Exception {
        return pathToFile(path).lastModified();
    }

    /**
     * Load the repository's configuration from the given configuration
     * object.
     *
     * @param configuration The configuration object
     * @throws Exception
     */

    public void loadConfiguration(Configuration configuration) throws Exception {
        this.name = configuration.getAttribute("name");
        setRoot(configuration.getChildValue("root"));

        //Florin 18 Feb 2005
        setCache(configuration.getChildValue("cache"));

        setWriteAllowed(configuration.getChildValue("write-allowed"));
    }

    /**
     * Get an Iterator of paths which are known to the repository.
     *
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths() throws Exception {
        return getPaths("");
    }

    /**
     * Get an Iterator of paths which are known to the repository, starting
     * from the specified base path.
     *
     * @param base The base path
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths(String base) throws Exception {
        return new FileSystemPathIterator(
                new BreadthFirstFileTreeIterator(pathToFile(base)), this);
    }

    /**
     * Load the content from the given path.
     *
     * @param path The path
     * @return The String
     */

    private String loadContent(String path) throws Exception {
        CacheEntry cacheEntry = (CacheEntry) jPublishCache.get(path);
        long fileTimeStamp = pathToFile(path).lastModified();
        if (cacheEntry == null || cacheEntry.getLastModified() != fileTimeStamp) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(path)));
            cacheEntry = new CacheEntry(FileCopyUtils.copyToString(reader), fileTimeStamp);
            jPublishCache.put(path, cacheEntry);
        }
        return (String) cacheEntry.getObject();
    }

    /**
     * Convert a path to a file.  If the value of <code>getRoot()</code>
     * is null then the file will be relative to the value of
     * <code>SiteContext.getRoot()</code>.  If <code>getRoot()</code>
     * returns a non-null value and it is absolute then the file
     * will be relative to the root.  If <code>getRoot()</code> is
     * not absolute then the file will be relative to
     * <code>SiteContext.getRoot()</code> + <code>getRoot()</code>.
     *
     * @param path The path
     * @return The file
     */

    public File pathToFile(String path) {
        return new File(getRealRoot(), path);
    }

    /**
     * Installs local cache
     * todo: write the doc!
     *
     * @param cacheName the cache name as defined in jpublish.xml
     */
    private void setCache(String cacheName) {
        JPublishCacheManager jPublishCacheManager = siteContext.getJPublishCacheManager();

        try {
            jPublishCache = jPublishCacheManager.getCache(cacheName);

        } catch (Throwable e) {
            log.error("Cannot use JPUBLISH cache: " + cacheName);
            e.printStackTrace();
        }

    }

    public void clearCache(JPublishContext context) {
        try {
            if (jPublishCache != null) {
                if (context != null) {
                    context.put("old.cache.size", new Integer(jPublishCache.getKeys().size()));
                }
                jPublishCache.clear();
            }
        } catch (JPublishCacheException e) {
            e.printStackTrace();
        }
    }
}
