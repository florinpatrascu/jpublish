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

package org.jpublish.page;

import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.PageManager;
import org.jpublish.SiteContext;

/** An abstract implementation of the PageManager interface which provides
    behavior common to all PageManager implementations.

    @author Anthony Eden
*/

public abstract class AbstractPageManager implements PageManager{
    
    private static Log log = LogFactory.getLog(AbstractPageManager.class);
    
    protected SiteContext siteContext;

    /** Get the SiteContext.
    
        @return The SiteContext
    */
    
    public SiteContext getSiteContext(){
        return siteContext;
    }
    
    /** Set the SiteContext.
    
        @param siteContext The site context
    */

    public void setSiteContext(SiteContext siteContext){
        log.debug("Site context set.");
        this.siteContext = siteContext;
    }
    
    /** Load the PageManager's configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws 
    ConfigurationException{
        // no op
    }
    
    /** Put the page instance into the location specified by the given
        path.
        
        @param path The page path
        @param page The PageInstance object
        @throws Exception
    */
    
    public void putPage(String path, PageInstance page) throws Exception{
        throw new UnsupportedOperationException("Put page not supported");
    }
    
    /** Remove the page at the specified path.
    
        @param path The page path
        @throws Exception
    */
    
    public void removePage(String path) throws Exception{
        throw new UnsupportedOperationException("Remove page not supported");
    }
    
     /** Make the directory for the specified path.  Parent directories
        will also be created if they do not exist.
        
        @param path The directory path
    */
    
    public void makeDirectory(String path){
        throw new UnsupportedOperationException("Make directory not supported");
    }
    
    /** Remove the directory for the specified path.  The directory
        must be empty.
    
        @param path The path
        @throws Exception
    */
    
    public void removeDirectory(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Remove directory not supported");
    }
    
    /** Get a page configuration reader for the page at the specified
        path.  This method may throw an UnsupportedOperationException if
        the PageManager implementation does not allow for direct reading
        of a page's configuration.
        
        @param path The page path
        @return The Reader
        @throws Exception
    */
    
    public Reader getPageConfigurationReader(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Configuration read not supported");
    }
    
    /** Get a page configuration writer for the page at the specified
        path.  This method may throw an UnsupportedOperationException if
        the PageManager implementation does not provide for updating
        a page's configuration.
        
        @param path The page path
        @return The Writer
        @throws Exception
    */
    
    public Writer getPageConfigurationWriter(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Configuration write not supported");
    }

}
