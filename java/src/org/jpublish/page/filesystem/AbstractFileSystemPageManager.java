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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jpublish.page.AbstractPageManager;
import org.jpublish.util.BreadthFirstFileTreeIterator;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;
import org.jpublish.util.vfs.provider.filesystem.FileSystemProvider;

/** Base abstract implementation for FileSystemPageManagers.  Implementations
    of PageManager can extend from this base class to minimize the amount of 
    implementation required.
    
    @author Anthony Eden
*/

public abstract class AbstractFileSystemPageManager extends AbstractPageManager{

    private static final Log log = LogFactory.getLog(
        AbstractFileSystemPageManager.class);
    
    /** The root directory to search for page definitions. */
    protected File root;
    
    /** The Virtual File System provider. */
    protected VFSProvider provider;
    
    /** Get the root directory for pages.  Page requests are appended
        to this root directory to locate the page's XML configuration
        file.  If the page root in the site context is an absolute path
        then that value is returned, otherwise the value of 
        <code>SiteContext.getRoot()</code> is prepended to the page
        root.
    
        @return The root directory for pages
    */

    public File getRoot(){
        return siteContext.getRealPageRoot();
    }
    
    /** Get an iterator which can be used to iterate through all pages
        known to the PageManager.
        
        @return An Iterator of Pages
        @throws Exception Any Exception
    */
    
    public Iterator getPages() throws Exception{
        return getPages("");
    }
    
    /** Get an iterator which can be used to iterate through all the
        pages at or below the specified path.  If the path refers
        to a file, then the file's parent directory is used.
        
        @param path The base path
        @return An Iterator of Pages
        @throws Exception Any Exception
    */
    
    public Iterator getPages(String path) throws Exception{
        if(log.isDebugEnabled())
            log.debug("getPages(" + path + ")");
        
        File file = new File(getRoot(), path);
        if(!file.exists()){
            throw new FileNotFoundException("Cannot find path: " + path);
        }
        
        return getPages(file);
    }
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the PageManager.
        
        @return The root VFSFile
        @throws Exception
    */
    
    public synchronized VFSFile getVFSRoot() throws Exception{
        if(provider == null){
            provider = new FileSystemProvider(getRoot());
        }
        return provider.getRoot();
    }
    
    /** Get an iterator which can be used to iterate through all the pages
        at or below the specified File.
        
        @param file The base file
        @return An Iterator of pages
        @throws Exception
    */
    
    protected Iterator getPages(File file) throws Exception{
        if(log.isDebugEnabled())
            log.debug("getPages(" + file + ")");
        
        if(!file.isDirectory()){
            file = file.getParentFile();
        }
        
        return new FileSystemPageIterator(this, 
            new BreadthFirstFileTreeIterator(file));
    }
    
}
