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

package org.jpublish.page.filesystem;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.page.PageDefinition;
import org.jpublish.page.PageDefinitionCacheEntry;
import org.jpublish.page.PageInstance;
import org.jpublish.util.PathUtilities;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The PageManager is a central access point for locating pages.  Pages
 * are loaded and cached automatically.  The cache will check the file's
 * last modification time and will update if the stored time does not match
 * the file system's time.
 * <p/>
 * <p>This PageManager implementation will fall back to the default.xml page
 * descriptor if the path's expected page descriptor is not found.</p>
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class FileSystemPageManagerWithDefault extends AbstractFileSystemPageManager {

    /**
     * The default path (default.xml).
     */
    public static final String DEFAULT_PATH = "default.xml";

    private static final Log log = LogFactory.getLog(FileSystemPageManager.class);

    protected String defaultPath = DEFAULT_PATH;

    protected Map cache;

    /**
     * Construct a new FileSystemPageManagerWithDefault.
     */

    public FileSystemPageManagerWithDefault() {
        this.cache = new HashMap();
    }


    /**
     * Get the path to the default descriptor.
     *
     * @return The default path
     */

    public String getDefaultPath() {
        return defaultPath;
    }

    /**
     * Set the path to the default descriptor.
     *
     * @param defaultPath
     */

    public void setDefaultPath(String defaultPath) {
        if (defaultPath == null) {
            this.defaultPath = DEFAULT_PATH;
        } else {
            this.defaultPath = defaultPath;
        }
    }

    /**
     * Get a PageInstance from the given path.  If no page can be found
     * then this method will throw a FileNotFoundException.
     *
     * @param path The page path
     * @return The Page
     * @throws Exception Any Exception
     */

    public synchronized PageInstance getPage(String path) throws Exception {
        File file = new File(path);
        File parentDirectory = file.getParentFile();

        String pageName = PathUtilities.extractPageName(path);
        String pagePath = PathUtilities.extractPagePath(path);

        File xmlFile = new File(getRoot(), new File(parentDirectory, pageName + ".xml").getPath());

        if (!xmlFile.exists()) {
            xmlFile = new File(getRoot(), getDefaultPath());
            if (!xmlFile.exists()) {
                throw new FileNotFoundException("Default page descriptor not found: " + xmlFile);
            }
        }

        if (log.isDebugEnabled())
            log.debug("Looking for page:" + xmlFile);

        // check the cache and load the page definition if necessary
        PageDefinitionCacheEntry cacheEntry = (PageDefinitionCacheEntry) cache.get(pagePath);

        PageInstance page = null;
        PageDefinition pageDefinition = null;
        FileInputStream in = null;

        try {
            if (cacheEntry == null) {

                if (log.isDebugEnabled()) {
                    log.debug("Page definition (" + pagePath + ") not found in cache.");
                    log.debug("Loading page definition configuration: " + xmlFile);
                }

                in = new FileInputStream(xmlFile);

                pageDefinition = new PageDefinition(siteContext, pagePath);
                pageDefinition.loadConfiguration(pagePath, in);

                cache.put(pagePath, new PageDefinitionCacheEntry(pageDefinition,
                        xmlFile.lastModified()));
            } else {
                if (log.isDebugEnabled())
                    log.debug("Page definition (" + pagePath +
                            ") found in cache.");
                pageDefinition = cacheEntry.getPageDefinition();

                if (cacheEntry.getLastModified() != xmlFile.lastModified()) {

                    if (log.isDebugEnabled()) {
                        log.debug("Page modification dates do not match.");
                        log.debug("Reloading page definition.");
                    }

                    long lastModified = xmlFile.lastModified();

                    in = new FileInputStream(xmlFile);

                    pageDefinition = new PageDefinition(siteContext, pagePath);
                    pageDefinition.loadConfiguration(pagePath, in);

                    cache.put(pagePath, new PageDefinitionCacheEntry(pageDefinition, lastModified));
                }
            }
        } catch (ConfigurationException e) {
            log.error("Error loading page [" + pagePath + "] definition: " + e.getMessage());
            throw e;

        } finally {
            IOUtilities.close(in);
        }

        if (pageDefinition != null) {
            if (log.isDebugEnabled())
                log.debug("Getting page instance for " + path);
            page = pageDefinition.getPageInstance(path);
        }

        return page;
    }

    /**
     * Put the page instance into the location specified by the given
     * path.
     *
     * @param path The page path
     * @param page The PageInstance object
     * @throws Exception
     */

    public void putPage(String path, PageInstance page) throws Exception {
        // NYI
    }

    /**
     * Remove the page at the specified path.
     *
     * @param path The page path
     * @throws Exception Any Exception
     */

    public void removePage(String path) throws Exception {
        pathToFile(path).delete();
    }

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path) {
        File file = new File(getRoot(), path);
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
        File file = new File(getRoot(), path);
        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
    }

    /**
     * Load the PageManager's configuration.
     *
     * @param configuration The Configuration object
     * @throws ConfigurationException
     */

    public void loadConfiguration(Configuration configuration) throws
            ConfigurationException {
        setDefaultPath(configuration.getChildValue("default-path"));
    }

    /**
     * Get a page configuration reader for the page at the specified
     * path.
     *
     * @param path The page path
     * @return The configuration reader
     * @throws Exception
     */

    public Reader getPageConfigurationReader(String path) throws Exception {
        File xmlFile = pathToFile(path);

        if (!xmlFile.exists()) {
            throw new FileNotFoundException("File not found: " + xmlFile);
        }

        return new FileReader(xmlFile);
    }

    /**
     * Get a page configuration writer for the page at the specified
     * path.  This method may throw an UnsupportedOperationException if
     * the PageManager implementation does not provide for updating
     * a page's configuration.
     *
     * @param path The page path
     * @return The Writer
     * @throws Exception
     */

    public Writer getPageConfigurationWriter(String path) throws Exception {
        return new FileWriter(pathToFile(path));
    }

    protected File pathToFile(String path) {
        if (path.endsWith("/")) {
            return new File(getRoot(), path);
        }

        File file = new File(path);
        File parentDirectory = file.getParentFile();

        String pageName = PathUtilities.extractPageName(path);

        return new File(getRoot(),
                new File(parentDirectory, pageName + ".xml").getPath());
    }

}
