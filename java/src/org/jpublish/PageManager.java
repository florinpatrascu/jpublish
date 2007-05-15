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

package org.jpublish;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.jpublish.page.PageInstance;
import org.jpublish.util.vfs.VFSFile;

import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * The PageManager is a central access point for locating pages.  Implementations of
 * the PageManager interface will provide the page loading behavior.
 *
 * @author Anthony Eden
 * @author Florin T.PATRASCU
 */

public interface PageManager {

    /**
     * Set the SiteContext.
     *
     * @param siteContext The site context
     */

    public void setSiteContext(SiteContext siteContext);

    /**
     * Get a PageInstance from the given path.  If no page can be found
     * then this method will throw an Exception.
     *
     * @param path The page path
     * @return The PageInstance
     * @throws Exception Any Exception
     */

    public PageInstance getPage(String path) throws Exception;

    /**
     * Put the page instance into the location specified by the given
     * path.
     *
     * @param path The page path
     * @param page The PageInstance object
     * @throws Exception
     */

    public void putPage(String path, PageInstance page) throws Exception;

    /**
     * Remove the page at the specified path.
     *
     * @param path The page path
     * @throws Exception
     */

    public void removePage(String path) throws Exception;

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path);

    /**
     * Remove the directory for the specified path.  The directory
     * must be empty.
     *
     * @param path The path
     * @throws Exception
     */

    public void removeDirectory(String path) throws Exception;

    /**
     * Get an iterator which can be used to iterate through all pages
     * known to the PageManager.
     *
     * @return An Iterator of Pages
     * @throws Exception Any Exception
     */

    public Iterator getPages() throws Exception;

    /**
     * Get an iterator which can be used to iterate through all the
     * pages at or below the specified path.  If the path refers
     * to a file, then the file's parent directory is used.
     *
     * @param path The base path
     * @return An Iterator of Pages
     * @throws Exception Any Exception
     */

    public Iterator getPages(String path) throws Exception;

    /**
     * Get the Virtual File System root file.  The Virtual File System
     * provides a datasource-independent way of navigating through all
     * items known to the PageManager.
     *
     * @return The root VFSFile
     * @throws Exception
     */

    public VFSFile getVFSRoot() throws Exception;

    /**
     * Load the PageManager's configuration.
     *
     * @param configuration The Configuration object
     * @throws ConfigurationException
     */

    public void loadConfiguration(Configuration configuration) throws
            ConfigurationException;

    /**
     * Get a page configuration reader for the page at the specified
     * path.  This method may throw an UnsupportedOperationException if
     * the PageManager implementation does not allow for direct reading
     * of a page's configuration.
     *
     * @param path The page path
     * @return The Reader
     * @throws Exception
     */

    public Reader getPageConfigurationReader(String path) throws Exception;

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

    public Writer getPageConfigurationWriter(String path) throws Exception;

}
