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
import org.jpublish.util.vfs.VFSFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/** Interface for accessing static resources.

    @author Anthony Eden
    @since 1.3
*/

public interface StaticResourceManager{

    /** Set the StaticResourceManager's SiteContext.

        @param siteContext The SiteContext
    */

    public void setSiteContext(SiteContext siteContext);

    /** Return true if the specified resource exists.

        @param path The path
        @return True if the resource exists
    */

    public boolean resourceExists(String path);

    /** Get the last modified time for the specified resource path.

        @param path The resource path
        @return The last modified time
    */

    public long getLastModified(String path);

    /** Get the content length for the specified resource path.

        @param path The resource path
        @return The content length or -1
    */

    public long getContentLength(String path);

    /** Load the static resource data at the given path and write it to
        the specified OutputStream.

        @param path The resource path
        @param out The OutputStream
        @throws Exception Any Exception
    */

    public void load(String path, OutputStream out) throws Exception;

    /** Read the static resource data from the given InputStream and write
        it to the specified path location.

        @param path The resource path
        @param in The InputStream
        @throws Exception Any Exception
    */

    public void save(String path, InputStream in) throws Exception;

    /** Remove the static resource at the given path.

        @param path The resource path
        @throws Exception
    */

    public void remove(String path) throws Exception;

    /** Make the directory for the specified path.  Parent directories
        will also be created if they do not exist.

        @param path The directory path
    */

    public void makeDirectory(String path);

    /** Remove the directory for the specified path.  The directory
        must be empty.

        @param path The path
        @throws Exception
    */

    public void removeDirectory(String path) throws Exception;

    /** Get an Iterator of paths which are known to the static resource loader.

        @return An iterator of paths
        @throws Exception
    */

    public Iterator getPaths() throws Exception;

    /** Get an Iterator of paths which are known to the static resource loader,
        starting from the specified base path.

        @param base The base path
        @return An iterator of paths
        @throws Exception
    */

    public Iterator getPaths(String base) throws Exception;

    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the StaticResourceManager.

        @return The root VFSFile
        @throws Exception
    */

    public VFSFile getVFSRoot() throws Exception;

    /** Load the configuration.

        @param configuration The Configuration object
        @throws ConfigurationException
    */

    public void loadConfiguration(Configuration configuration) throws
    ConfigurationException;

}
