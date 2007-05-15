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

package org.jpublish.page.db;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.SQLUtilities;
import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.page.PageInstance;
import org.jpublish.page.PageDefinition;
import org.jpublish.page.AbstractPageManager;
import org.jpublish.page.PageNotFoundException;
import org.jpublish.util.PathUtilities;
import org.jpublish.util.vfs.VFSFile;

/** Implementation of the PageManager interface which pulls all page 
    definitions from a database.  The DBPageManager class currently does
    not cache any Page objects.

    @author Anthony Eden
    @since 1.1
*/

public class DBPageManager extends AbstractPageManager{
    
    private static Log log = LogFactory.getLog(DBPageManager.class);
    
    private static final String SELECT_PAGE_STMT = 
        "select * from page where path = ?";
    private static final String SELECT_PAGES_STMT = 
        "select * from page where path like ?";
    private static final String INSERT_PAGE_STMT = 
        "insert into page set (path, data) values (?, ?)";
    private static final String UPDATE_PAGE_STMT =
        "update page set data = ? where path = ?";
    private static final String DELETE_PAGE_STMT = 
        "delete from page where path = ?";
    
    protected String driver;
    protected String url;
    protected String username;
    protected String password;
    
    protected Map pageDefinitions;

    /** Construct a new DBPageManager. */

    public DBPageManager(){
        pageDefinitions = new HashMap();
    }
    
    /** Get the SQL driver.
    
        @return The driver class name
    */
    
    public String getDriver(){
        return driver;
    }
    
    /** Set the SQL driver.
    
        @param driver The driver class name
    */
    
    public void setDriver(String driver){
        this.driver = driver;
    }
    
    /** Get the database URL.
    
        @return The database URL
    */
    
    public String getURL(){
        return url;
    }
    
    /** Set the database URL.
    
        @param url The URL
    */
    
    public void setURL(String url){
        this.url = url;
    }
    
    /** Get the database username.
    
        @return The database username
    */
    
    public String getUsername(){
        return username;
    }
    
    /** Set the database username.
    
        @param username The username
    */
    
    public void setUsername(String username){
        this.username = username;
    }
    
    /** Set the database password.
    
        @param password The password
    */
    
    public void setPassword(String password){
        this.password = password;
    }
    
    /** Get a PageInstance from the given path.  If no page can be found
        then this method will throw a PageFoundException.
    
        @param path The page path
        @return The Page
        @throws Exception Any Exception
    */

    public synchronized PageInstance getPage(String path) throws Exception{
        String pagePath = PathUtilities.extractPagePath(path);
        if(log.isDebugEnabled())
            log.debug("Page path: " + pagePath);
        
        PageInstance page = null;
        PageDefinition pageDefinition = null;
        
        Connection c = null;
        try{
            
            log.debug("Loading page definition configuration from DB");
                
            // select the data from the database
            c = getConnection();
            PreparedStatement ps = c.prepareStatement(SELECT_PAGE_STMT);
            ps.setString(1, pagePath);
            ResultSet resultSet = ps.executeQuery();
            
            if(resultSet.next()){   
                String pageXML = resultSet.getString("data");
                
                pageDefinition = new PageDefinition(siteContext, pagePath);
                pageDefinition.loadConfiguration(new StringReader(pageXML));
            }
        } catch(Exception e){
            log.error("Error loading page definition: " + e.getMessage());
            throw e;
        } finally {
            SQLUtilities.close(c);
        }
        
        if(pageDefinition != null){
            if(log.isDebugEnabled())
                log.debug("Getting page instance for " + path);
            page = pageDefinition.getPageInstance(path);
        } else {
            throw new PageNotFoundException("Page not found: " + path);
        }
        
        return page;
    }
    
    /** Put the page instance into the location specified by the given
        path.
        
        @param path The page path
        @param page The Page object
        @throws Exception
    */
    
    public void putPage(String path, PageInstance page) throws Exception{
        // NYI
    }
    
    /** Remove the page at the specified path.
    
        @param path The page path
    */
    
    public void removePage(String path) throws Exception{
        // NYI
    }
    
    /** Make the directory for the specified path.  Parent directories
        will also be created if they do not exist.
        
        @param path The directory path
    */
    
    public void makeDirectory(String path){
        
    }
    
    /** Remove the directory for the specified path.  The directory
        must be empty.
    
        @param path The path
        @throws Exception
    */
    
    public void removeDirectory(String path) throws Exception{
        
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
        Connection c = null;
        
        try{
            c = getConnection();
            PreparedStatement ps = c.prepareStatement(SELECT_PAGES_STMT);
            ps.setString(1, path + "%");
            ResultSet resultSet = ps.executeQuery();
            
            return new DBPageIterator(this, resultSet);
        } finally {
            SQLUtilities.close(c);
        }
    }
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the PageManager.
        
        <p>The DBPageManager does not currently support a virtual file
        system.  This method will through an UnsupportedOperationException.</p>
        
        @return The root VFSFile
        @throws Exception
    */
    
    public synchronized VFSFile getVFSRoot() throws Exception{
        // NYI
        throw new UnsupportedOperationException();
    }
    
    /** Load the DBPageManager configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws 
    ConfigurationException{
        // load database configuration
        Configuration dbConfiguration = configuration.getChild("database");
        setURL(dbConfiguration.getChildValue("url"));
        setDriver(dbConfiguration.getChildValue("driver"));
        setUsername(dbConfiguration.getChildValue("username"));
        setPassword(dbConfiguration.getChildValue("password"));
    }
    
    protected Connection getConnection() throws SQLException{
        return DriverManager.getConnection(
            getURL(), getUsername(), getPassword());
    }
    
    protected String getPassword(){
        return password;
    }

}
