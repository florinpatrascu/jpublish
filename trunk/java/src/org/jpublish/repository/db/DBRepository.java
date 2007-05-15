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

package org.jpublish.repository.db;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.IOUtilities;
import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.config.Configuration;

import org.jpublish.JPublishContext;
import org.jpublish.view.ViewRenderer;
import org.jpublish.util.PathUtilities;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.repository.AbstractRepository;

/** Repository implementation which pulls content from a database.

    <p>Here is an example configuration connecting to a MySql database:
    
    <pre>
    &lt;name&gt;db_repository&lt;/name&gt;
    &lt;driver&gt;org.gjt.mm.mysql.Driver&lt;/name&gt;
    &lt;url&gt;jdbc:mysql://myserver.com/database&lt;/url&gt;
    &lt;username&gt;username&lt;/username&gt;
    &lt;password&gt;password&lt;/password&gt;
    &lt;content-query&gt;select body from content where path = ?&lt;/content-query&gt;
    &lt;last-modified-query&gt;select last_modified from content where path = ?&lt;/last-modified-query&gt;
    &lt;paths-query&gt;select * from content where path like ?&lt;/paths-query&gt;
    &lt;delete-query&gt;delete from content where path = ?&lt;/delete-query&gt;
    </pre>
    
    <p>The database setup would be:</p>
    
    <pre>
    create table content (
        path varchar(255) not null,
        body text not null,
        last_modified datetime not null
    );
    </pre>
    
    <p><b>Note:</b> This implementation does not currently implement all of the
    required respository methods properly.  In particular, the 
    <code>makeDirectory()</code> and <code>removeDirectory()</code> methods are 
    not implemented, nor is the <code>getVFSRoot()</code> method implemented 
    (it will through an UnsupportedOperationException).</p>
*/

public class DBRepository extends AbstractRepository{
    
    private static final Log log = LogFactory.getLog(DBRepository.class);

    private String driver;
    private String url;
    private String username;
    private String password;
    
    private String contentQuery;
    private String lastModifiedQuery;
    private String pathsQuery;
    private String deleteQuery;

    /** Get the content from the given path.  Implementations of this method
        should NOT merge the content using view renderer.

        @param path The relative content path
        @return The content as a String
        @throws Exception Any Exception
    */

    public String get(String path) throws Exception{
        log.debug("Getting static content element.");
    
        Connection c = null;
        try{
            log.debug("Connecting to database.");
            c = DriverManager.getConnection(url, username, password);
            
            PreparedStatement s = c.prepareStatement(contentQuery);
            s.setString(1, path);
            
            log.debug("Executing query.");
            ResultSet r = s.executeQuery();
            if(r.next()){
                return r.getString(1);
            } else {
                throw new Exception("Path not found: " + path);
            }
        } finally {
            c.close();
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
        Connection c = null;
        try{
            c = DriverManager.getConnection(url, username, password);
            PreparedStatement s = c.prepareStatement(deleteQuery);
            s.setString(1, path);
            
            s.executeUpdate();
        } finally {
            c.close();
        }
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
        Connection c = null;
        try{
            log.debug("Connecting to database.");
            c = DriverManager.getConnection(url, username, password);
            
            PreparedStatement s = c.prepareStatement(lastModifiedQuery);
            s.setString(1, path);
            
            log.debug("Executing query.");
            ResultSet r = s.executeQuery();
            if(r.next()){
                return r.getLong(1);
            } else {
                throw new Exception("Path not found: " + path);
            }
        } finally {
            c.close();
        }
    }
    
    /** Get an Iterator of paths which are known to the repository.
        
        @return An iterator of paths
        @throws Exception
    */
    
    public Iterator getPaths() throws Exception{
        return getPaths("");
    }
    
    /** Get an Iterator of paths which are known to the repository, starting
        from the specified base path.
        
        @param base The base path
        @return An iterator of paths
        @throws Exception
    */

    public Iterator getPaths(String path) throws Exception{
        ArrayList paths = new ArrayList();
        Connection c = null;
        try{
            log.debug("Connecting to database.");
            c = DriverManager.getConnection(url, username, password);
            
            PreparedStatement s = c.prepareStatement(pathsQuery);
            s.setString(1, path + "%");
            
            log.debug("Executing query.");
            ResultSet r = s.executeQuery();
            while(r.next()){
                paths.add(r.getString("path"));
            }
        } finally {
            c.close();
        }
        
        return paths.iterator();
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
        setDriver(configuration.getChildValue("driver"));
        setURL(configuration.getChildValue("url"));
        setUsername(configuration.getChildValue("username"));
        setPassword(configuration.getChildValue("password"));

        setContentQuery(configuration.getChildValue("content-query"));
        setLastModifiedQuery(configuration.getChildValue("last-modified-query"));
        setPathsQuery(configuration.getChildValue("paths-query"));
        setDeleteQuery(configuration.getChildValue("delete-query"));
    }
    
    /** Set the driver class.
    
        @param driver The fully-qualified driver class
        @throws Exception Any Exception
    */
    
    protected void setDriver(String driver) throws Exception{
        this.driver = driver;
        ClassUtilities.loadClass(this.driver);
    }
    
    /** Set the URL.
    
        @param url The new URL
    */
    
    protected void setURL(String url){
        this.url = url;
    }
    
    /** Set the username.
    
        @param username The new username
    */
    
    protected void setUsername(String username){
        this.username = username;
    }
    
    /** Set the password.
    
        @param password The new password
    */
    
    protected void setPassword(String password){
        this.password = password;
    }
    
    /** Set the content query.  This query will pull the content body from
        the database.  The first column must be the content
        body.
        
        @param contentQuery The content query
    */
    
    protected void setContentQuery(String contentQuery){
        this.contentQuery = contentQuery;
    }
    
    /** Set the "last modified" query.  This query will pull the last modified
        timestamp from the database.  The first column must be the last 
        modified timestamp.
        
        @param lastModifiedQuery The "last modified" query
    */
    
    protected void setLastModifiedQuery(String lastModifiedQuery){
        this.lastModifiedQuery = lastModifiedQuery;
    }
    
    /** Set the "getPaths()" query.  This query will pull a list of all paths
        from the database.
        
        @param pathQuery The "getPaths()" query
    */
    
    protected void setPathsQuery(String pathsQuery){
        this.pathsQuery = pathsQuery;
    }
    
    /** Set the query for deleting a piece of content from the repository.
    
        @param deleteQuery The new delete query
    */
    
    protected void setDeleteQuery(String deleteQuery){
        this.deleteQuery = deleteQuery;
    }

}
