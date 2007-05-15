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

package org.jpublish.resource.filesystem;

import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.resource.AbstractStaticResourceManager;
import org.jpublish.util.BreadthFirstFileTreeIterator;
import org.jpublish.util.FileCopyUtils;
import org.jpublish.util.FileToPathIterator;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;
import org.jpublish.util.vfs.provider.filesystem.FileSystemProvider;

import java.io.*;
import java.util.Iterator;

/**
 * Implementation of the StaticResourceManager interface which pulls static
 * data from the file system.
 *
 * @author Anthony Eden
 * @author Florin T.PATRASCU
 * @since 2
 */

public class FileSystemStaticResourceManager extends AbstractStaticResourceManager {

    private static final Log log = LogFactory.getLog(FileSystemStaticResourceManager.class);
    public static final int BUFFER_SIZE = 4096;


    protected VFSProvider provider;

    /**
     * Return true if the resource for the specified path exists.
     *
     * @param path The resource path
     * @return True if the resource exists
     */

    public boolean resourceExists(String path) {
        return pathToFile(path).exists();
    }

    /**
     * Get the last modified time for the specified resource path.
     *
     * @param path The resource path
     * @return The last modified time
     */

    public long getLastModified(String path) {
        return pathToFile(path).lastModified();
    }

    /**
     * Get the content length for the specified resource path.
     *
     * @param path The resource path
     * @return The content length or -1
     */

    public long getContentLength(String path) {
        return pathToFile(path).length();
    }

    /**
     * Get the root directory for finding static content.  If the value of
     * <code>SiteContext.getStaticRoot()</code> is an absolute path then
     * that will be returned, otherwise the value of
     * <code>SiteContext.getRoot()</code> will be prepended and the
     * resulting value is returned.
     *
     * @return The root directory
     */

    public File getRoot() {
        return siteContext.getRealStaticRoot();
    }

    /**
     * Load the static resource data at the given path and write it to
     * the specified OutputStream.
     *
     * @param path The resource path
     * @param out  The OutputStream
     * @throws Exception Any Exception
     */
    public void load(String path, OutputStream out) throws Exception {
        if (path == null || path.trim().length() == 0)
            return;

        InputStream in = new BufferedInputStream(new FileInputStream(pathToFile(path)));
        FileCopyUtils.copy(in, out);
    }

    /**
     * Read the static resource data from the given InputStream and write
     * it to the specified path location.
     *
     * @param path The resource path
     * @param in   The InputStream
     * @throws Exception Any Exception
     */

    public void save(String path, InputStream in) throws Exception {
        OutputStream out = null;
        try {
            out = new FileOutputStream(pathToFile(path));
            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            IOUtilities.close(out);
        }
    }

    /**
     * Remove the static resource at the given path.
     *
     * @param path The resource path
     * @throws Exception
     */

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
     * Get an Iterator of paths which are known to the static resource loader.
     *
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths() throws Exception {
        return getPaths("");
    }

    /**
     * Get an Iterator of paths which are known to the static resource loader,
     * starting from the specified base path.
     *
     * @param base The base path
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths(String base) throws Exception {
        File baseFile = pathToFile(base);
        return new FileToPathIterator(baseFile.toString(),
                new BreadthFirstFileTreeIterator(baseFile));
    }

    /**
     * Get the Virtual File System root file.  The Virtual File System
     * provides a datasource-independent way of navigating through all
     * items known to the StaticResourceManager.
     *
     * @return The root VFSFile
     * @throws Exception
     */

    public VFSFile getVFSRoot() throws Exception {
        if (provider == null) {
            provider = new FileSystemProvider(getRoot());
        }
        return provider.getRoot();
    }

    /**
     * Convert a path String to a File object by prepending the
     * value of <code>getRoot()</code>
     *
     * @param path The path
     * @return A File
     */

    private File pathToFile(String path) {
        File file = new File(getRoot(), path);
        if (log.isDebugEnabled())
            log.debug("pathToFile [path=" + path + ",file=" + file + "]");
        return file;
    }

}
