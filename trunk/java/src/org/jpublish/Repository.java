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

import org.jpublish.util.vfs.VFSFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * A common interface which all repositories must implement.
 * A Repository represents a data store which contains content.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public interface Repository extends Configurable {

    /**
     * Get the name of the repository.  This name is used to expose the
     * Repository in the view renderer.
     *
     * @return The Repository name
     */

    public String getName();

    /**
     * Get the content from the given path.  Implementations of this method
     * should NOT merge the content using the view renderer.
     *
     * @param path The relative content path
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path) throws Exception;

    /**
     * Get the content from the given path and merge it with
     * the given context.
     *
     * @param path    The content path
     * @param context The current context
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path, JPublishContext context) throws Exception;

    /**
     * Remove the content at the specified path.
     *
     * @param path The path
     * @throws Exception
     */

    public void remove(String path) throws Exception;

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
     * Get the given content as an InputStream.  The InputStream will
     * return the raw content data.  Implementations should not attempt
     * to pass the data through the view renderer.
     *
     * @param path The path to the content
     * @return The InputStream
     * @throws Exception
     */

    public InputStream getInputStream(String path) throws Exception;

    /**
     * Get an OutputStream for writing content to the given path.
     * Repository implementations are not required to implement this
     * method if they provide read-only access.
     *
     * @param path The path to the content
     * @return The OutputStream
     * @throws Exception
     */

    public OutputStream getOutputStream(String path) throws Exception;

    /**
     * Get the last modified time in milliseconds for the given path.
     *
     * @param path The content path
     * @return The last modified time in milliseconds
     * @throws Exception Any Exception
     */

    public long getLastModified(String path) throws Exception;

    /**
     * Set the SiteContext.  The repository can then get information
     * about the site through the context.
     *
     * @param siteContext The siteContext
     */

    public void setSiteContext(SiteContext siteContext);

    /**
     * Get an Iterator of paths which are known to the repository.
     *
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths() throws Exception;

    /**
     * Get an Iterator of paths which are known to the repository, starting
     * from the specified base path.
     *
     * @param base The base path
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths(String base) throws Exception;

    /**
     * Get the Virtual File System root file.  The Virtual File System
     * provides a datasource-independent way of navigating through all
     * items known to the Repository.
     *
     * @return The root VFSFile
     * @throws Exception
     */

    public VFSFile getVFSRoot() throws Exception;

    /**
     * @param path the path to a content
     * @return a File path null
     */
    public File pathToFile(String path);
}
