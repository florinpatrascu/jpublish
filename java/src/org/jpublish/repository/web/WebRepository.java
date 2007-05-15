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
 
package org.jpublish.repository.web;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.File;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.IOUtilities;
import com.anthonyeden.lib.config.Configuration;

import org.jpublish.JPublishContext;
import org.jpublish.view.ViewRenderer;
import org.jpublish.util.PathUtilities;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.repository.AbstractRepository;

/** Repository implementation which pulls content from a URL. */

public class WebRepository extends AbstractRepository{
    
    private static final Log log = LogFactory.getLog(WebRepository.class);

    /** Get the content from the given path.  Implementations of this method
        should NOT merge the content using view renderer.

        @param path The relative content path
        @return The content as a String
        @throws Exception Any Exception
    */

    public String get(String path) throws Exception{
        InputStream in = null;
        StringWriter out = null;
        try{
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        
            in = conn.getInputStream();
            out = new StringWriter();
            
            int c = -1;
            while((c = in.read()) != -1){
                out.write((char)c);
            }
            
            return out.toString();
        } finally {
            IOUtilities.close(in);
            IOUtilities.close(out);
        }
    }
    
    /** Get the content from the given path and merge it with
        the given context.
        
        @param path The content path
        @param context The current context
        @return The content as a String
        @throws Exception Any Exception
    */

    public String get(String path, JPublishContext context) throws Exception{
        if(log.isDebugEnabled())
            log.debug("Getting dynamic content element for path " + path);
        
        StringWriter writer = null;
        StringReader reader = null;
        
        try{
            writer = new StringWriter();
            reader = new StringReader(get(path));
            
            String name = PathUtilities.makeRepositoryURI(getName(), path);
            ViewRenderer renderer = siteContext.getViewRenderer();
            renderer.render(context, name, reader, writer);
            
            return writer.toString();
        } finally {
            IOUtilities.close(writer);
            IOUtilities.close(reader);
        }
    }
    
    /** Remove the content at the specified path.
    
        @param path The path
    */
    
    public void remove(String path) throws Exception{
        throw new UnsupportedOperationException("Cannot remove web content");
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
        throw new UnsupportedOperationException("Remove directory not supported");
    }

    /** Get the last modified time in milliseconds for the given path.

        @param path The content path
        @return The last modified time in milliseconds
        @throws Exception Any exception
    */

    public long getLastModified(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        return conn.getLastModified();
    }
    
    /** Get an Iterator of paths which are known to the repository.
        
        @return An iterator of paths
        @throws Exception
    */
    
    public Iterator getPaths() throws Exception{
        throw new UnsupportedOperationException("getPaths() not supported");
    }
    
    /** Get an Iterator of paths which are known to the repository, starting
        from the specified base path.
        
        @param base The base path
        @return An iterator of paths
        @throws Exception
    */

    public Iterator getPaths(String path) throws Exception{
        throw new UnsupportedOperationException("getPaths() not supported");
    }
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the Repository.
        
        @return The root VFSFile
        @throws Exception
    */
    
    public VFSFile getVFSRoot() throws Exception{
        // NYI
        throw new UnsupportedOperationException();
    }

    public File pathToFile(String path) {
        return null;
    }

    /** Load the repository's configuration from the given configuration 
        object.

        @param element The configuration object
        @throws Exception
    */

    public void loadConfiguration(Configuration configuration) throws Exception{
        this.name = configuration.getAttribute("name");
    }

}
