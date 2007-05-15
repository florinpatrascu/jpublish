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
 *
 * @author Anthony Eden
 */

public class FileSystemPageManager extends AbstractFileSystemPageManager {

    private static Log log = LogFactory.getLog(FileSystemPageManager.class);

    /**
     * A cache of page definitions.
     */
    protected Map cache;

    /**
     * Construct a new FileSystemPageManager.
     */

    public FileSystemPageManager() {
        this.cache = new HashMap();
    }

    /**
     * Get a Page instance from the given path.  If no page can be found
     * then this method will throw a FileNotFoundException.
     *
     * @param path The page path
     * @return The Page
     * @throws Exception Any Exception
     */

    public synchronized PageInstance getPage(String path) throws Exception {
        //File file = new File(path);
        //File parentDirectory = file.getParentFile();

        //String pageName = PathUtilities.extractPageName(path);
        //String pageType = PathUtilities.extractPageType(path);
        String pagePath = PathUtilities.extractPagePath(path);

        /*
        if(log.isDebugEnabled()){
            log.debug("Page name: " + pageName);
            log.debug("Page type: " + pageType);
            log.debug("Page path: " + pagePath);
        }
        */

        File xmlFile = pathToFile(path);

        if (!xmlFile.exists()) {
            throw new FileNotFoundException("File not found: " + xmlFile);
        }

        if (log.isDebugEnabled())
            log.debug("Looking for page:" + xmlFile);

        // check the cache and load the page definition if necessary
        PageDefinitionCacheEntry cacheEntry =
                (PageDefinitionCacheEntry) cache.get(pagePath);

        PageInstance page = null;
        PageDefinition pageDefinition = null;
        FileInputStream in = null;
        try {
            if (cacheEntry == null) {
                if (log.isDebugEnabled())
                    log.debug("Page definition (" + pagePath + ") not found in cache.");

                if (log.isDebugEnabled())
                    log.debug("Loading page definition configuration: " + xmlFile);

                in = new FileInputStream(xmlFile);

                pageDefinition = new PageDefinition(siteContext, pagePath);
                pageDefinition.loadConfiguration(in);

                cache.put(pagePath, new PageDefinitionCacheEntry(pageDefinition,
                        xmlFile.lastModified()));
            } else {
                if (log.isDebugEnabled())
                    log.debug("Page definition (" + pagePath +
                            ") found in cache.");
                pageDefinition = cacheEntry.getPageDefinition();
                if (cacheEntry.getLastModified() != xmlFile.lastModified()) {
                    log.debug("Page modification dates do not match.");
                    log.debug("Reloading page definition.");
                    long lastModified = xmlFile.lastModified();

                    in = new FileInputStream(xmlFile);

                    pageDefinition.loadConfiguration(in);

                    cache.put(pagePath, new PageDefinitionCacheEntry(
                            pageDefinition, lastModified));
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
     * @throws Exception
     */

    public void removePage(String path) throws Exception {
        log.info("Delete: " + path);
        File file = pathToFile(path);
        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());
        file.delete();
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
        log.info("Remove directory: " + path);
        File file = new File(getRoot(), path);
        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());
        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
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
        File file = new File(path);
        File parentDirectory = file.getParentFile();

        String pageName = PathUtilities.extractPageName(path);

        return new File(getRoot(),
                new File(parentDirectory, pageName + ".xml").getPath());
    }

}
