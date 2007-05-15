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

package org.jpublish.page.xml;

import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.resource.ResourceRecipient;
import com.anthonyeden.lib.resource.FileResourceLoader;

import org.jpublish.page.PageInstance;
import org.jpublish.page.PageDefinition;
import org.jpublish.page.AbstractPageManager;
import org.jpublish.util.PathUtilities;
import org.jpublish.util.vfs.VFSFile;

/** Implementation of the PageManager interface which pulls all page 
    definitions from a single XML file.  A default page can be specified
    as a failover in case a page is not defined.
    
    <p><b>Note:</b> The XMLPageManager does not support writing.  Any method
    which would modify pages in the page manager will throw an
    UnsupportedOperationException.</p>

    @author Anthony Eden
*/

public class XMLPageManager extends AbstractPageManager implements 
ResourceRecipient{
    
    /** The default file name for the configuration file (pages.xml). */
    public static final String DEFAULT_FILE_NAME = "pages.xml";
    
    private static final Log log = LogFactory.getLog(XMLPageManager.class);
    
    protected File file;
    protected List paths;
    protected Map pageDefinitions;
    protected PageDefinition defaultPageDefinition;
    
    private FileResourceLoader fileResourceLoader;

    /** Construct a new XMLPageManager. */

    public XMLPageManager(){
        paths = new ArrayList();
    }
    
    /** Get the configuration File object.
    
        @return The configuration file
    */
    
    public File getFile(){
        return file;
    }
    
    /** Set the configuration File object.
    
        @param file The configuration file
    */
    
    public void setFile(File file){
        this.file = file;
    }
    
    /** Set the configuration File object using the given File path String.
    
        @param file The configuration file path
    */
    
    public void setFile(String file){
        if(file != null){
            setFile(new File(file));
        } else {
            this.file = null;
        }
    }
    
    /** Get a PageInstance from the given path.  If no page can be found
        then this method will throw a FileNotFoundException.
    
        @param path The page path
        @return The Page
        @throws Exception Any Exception
    */

    public synchronized PageInstance getPage(String path) throws Exception{
        if(pageDefinitions == null){
            loadPageDefinitions();
        }
        
        String pagePath = PathUtilities.extractPagePath(path);
        if(log.isDebugEnabled())
            log.debug("Page path: " + pagePath);
        
        PageDefinition pageDefinition = (PageDefinition)pageDefinitions.get(
            pagePath);
        if(pageDefinition == null){
            
            if(defaultPageDefinition != null){
                return defaultPageDefinition.getPageInstance(path);
            }
            
            throw new FileNotFoundException("Page not found: " + path);
        }
        
        return pageDefinition.getPageInstance(path);
    }
    
    /** Put the page instance into the location specified by the given
        path.  The XMLPageManager does not currently support writing, so this
        method will throw an UnsupportedOperationException.
        
        @param path The page path
        @param page The Page object
        @throws Exception
    */
    
    public void putPage(String path, PageInstance page) throws Exception{
        throw new UnsupportedOperationException("Page put not supported");
    }
    
    /** Remove the page at the specified path.   The XMLPageManager does not 
        currently support writing, so this method will throw an 
        UnsupportedOperationException.
    
        @param path The page path
        @throws Exception
    */
    
    public void removePage(String path) throws Exception{
        throw new UnsupportedOperationException("Page remove not supported");
    }
    
    /** Make the directory for the specified path.  Parent directories
        will also be created if they do not exist.   The XMLPageManager 
        does not currently support writing, so this method will throw 
        an UnsupportedOperationException.
        
        @param path The directory path
    */
    
    public void makeDirectory(String path){
        throw new UnsupportedOperationException("Make directory not supported");
    }
    
    /** Remove the directory for the specified path.  The directory
        must be empty.  The XMLPageManager does not currently support 
        writing, so this method will throw an UnsupportedOperationException.
    
        @param path The path
        @throws Exception
    */
    
    public void removeDirectory(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Remove directory not supported");
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
        return new XMLPageIterator(this, paths, path);
    }   
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the PageManager.
        
        @return The root VFSFile
        @throws Exception
    */
    
    public synchronized VFSFile getVFSRoot() throws Exception{
        // NYI
        throw new UnsupportedOperationException();
    }
    
    /** Load the Page definitions from an XML configuration file. 
    
        @throws Exception Any Exception
    */
    
    protected void loadPageDefinitions() throws Exception{
        if(file == null){
            file = new File(DEFAULT_FILE_NAME);
        }
        
        if(!file.isAbsolute()){
            file = new File(siteContext.getWebInfPath(), file.getPath());
        }
        
        if(fileResourceLoader != null){
            fileResourceLoader.stopMonitors();
        }
        
        fileResourceLoader = new FileResourceLoader();
        fileResourceLoader.loadResource(file.getAbsolutePath(), this);
    }
    
    /** Load all page definitions from the given configuration InputStream.
    
        @param in The InputStream
        @throws Exception Any Exception
    */
    
    public void load(InputStream in) throws Exception{
        if(pageDefinitions == null){
            pageDefinitions = new HashMap();
        }
        
        paths.clear();
        pageDefinitions.clear();
        
        Configuration configuration = new XMLConfiguration(in);
        Iterator pageElements = configuration.getChildren("page").iterator();
        while(pageElements.hasNext()){
            Configuration pageElement = (Configuration)pageElements.next();
            String path = pageElement.getAttribute("path");
            if(path == null){
                throw new ConfigurationException("Path attribute required");
            }
            
            PageDefinition pageDefinition = new PageDefinition(siteContext, 
                path);
            pageDefinition.loadConfiguration(pageElement);
            
            paths.add(path);
            
            pageDefinitions.put(path, pageDefinition);
        }
        
        loadDefaultPageDefinition(configuration.getChild("default-page"));
    }
    
    /** Load the XMLPageManager configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws 
    ConfigurationException{
        setFile(configuration.getChildValue("file"));
    }
    
    /** Load the default PageDefinition from the given Configuration object.
        The default page definition is optional.
        
        @param configuration The Configuration object
    */
    
    private void loadDefaultPageDefinition(Configuration configuration) throws 
    Exception{
        if(configuration != null){
            defaultPageDefinition = new PageDefinition(siteContext, "");
            defaultPageDefinition.loadConfiguration(configuration);
        }
    }

}
