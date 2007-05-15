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

package org.jpublish.page.servletcontext;

import java.io.InputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.IOUtilities;
import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.page.PageInstance;
import org.jpublish.page.PageDefinition;
import org.jpublish.page.AbstractPageManager;
import org.jpublish.page.PageNotFoundException;
import org.jpublish.util.PathUtilities;
import org.jpublish.util.BreadthFirstPathTreeIterator;
import org.jpublish.util.vfs.VFSFile;

/** Implementation of the PageManager interface which pulls all page 
    definitions from the ServletContext.

    @author Anthony Eden
    @since 2.0
*/

public class ServletContextPageManager extends AbstractPageManager{
    
    private static Log log = LogFactory.getLog(ServletContextPageManager.class);
    
    protected String root;
    protected Map pageDefinitions;

    /** Construct a new ServletContextPageManager. */

    public ServletContextPageManager(){
        pageDefinitions = new HashMap();
    }
    
    /** Get the page manager root.
    
        @return The page manager root
    */
    
    public String getRoot(){
        if(root == null){
            root = siteContext.getPageRoot().getPath();
        }
        return root;
    }
    
    /** Set the page manager root.
    
        @param root The page manager root
    */
    
    public void setRoot(String root){
        this.root = root;
    }
    
    /** Get a PageInstance from the given path.  If no page can be found
        then this method will throw a PageFoundException.
    
        @param path The page path
        @return The Page
        @throws Exception Any Exception
    */

    public synchronized PageInstance getPage(String path) throws Exception{
        String pagePath = PathUtilities.extractPagePath(path);
        String resourcePath = pagePath + siteContext.getPageSuffix();
        
        if(log.isDebugEnabled())
            log.debug("Page path: " + pagePath);
        
        PageInstance page = null;
        PageDefinition pageDefinition = null;
        
        InputStream in = null;
        try{
            resourcePath = PathUtilities.toResourcePath(resourcePath);
            resourcePath = PathUtilities.toResourcePath(
                getRoot()) + resourcePath;
            
            if(log.isDebugEnabled())
                log.debug("Resource path: " + resourcePath);
            
            in = siteContext.getServletContext().getResourceAsStream(
                resourcePath);
            if(in != null){
                pageDefinition = new PageDefinition(siteContext, pagePath);
                pageDefinition.loadConfiguration(in);
            }
            
            if(pageDefinition != null){
                if(log.isDebugEnabled())
                    log.debug("Getting page instance for " + path);
                page = pageDefinition.getPageInstance(path);
            } else {
                throw new PageNotFoundException("Page not found: " + path);
            }
            
            return page;
        } finally {
            IOUtilities.close(in);
        }
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
        String root = PathUtilities.toResourcePath(getRoot());
        String basePath = PathUtilities.toResourcePath(path);
        return new ServletContextPageIterator(this, 
            new BreadthFirstPathTreeIterator(root + basePath, 
            siteContext.getServletContext()));
    }
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the PageManager.
        
        <p>The ServletContextPageManager does not currently support a virtual 
        file system.  This method will through an 
        UnsupportedOperationException.</p>
        
        @return The root VFSFile
        @throws Exception
    */
    
    public synchronized VFSFile getVFSRoot() throws Exception{
        // NYI
        throw new UnsupportedOperationException();
    }
    
    /** Load the configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws 
    ConfigurationException{
        
    }

}
