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

package org.jpublish.repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.jpublish.SiteContext;
import org.jpublish.Repository;

/** The AbstractRepository base class can be used as a superclass for
    repository implementations.  It provides convenience methods which
    are standard for all repositories.

    @author Anthony Eden
*/

public abstract class AbstractRepository implements Repository{
    
    protected String name;
    protected String root;
    protected SiteContext siteContext;
    
    /** Get the name of the repository.  This name is used to expose the
        Repository in the view renderer.

        @return The Repository name
    */

    public String getName(){
        return name;
    }
    
    /** Get the root for locating content.  This method may return null if the
        repository implementation does not require or understand the root field.

        @return The root
    */
    
    public String getRoot(){
        return root;
    }
    
    /** Set the root for locating content.

        @param root The new root
    */
    
    public void setRoot(String root){
        this.root = root;
    }

    /** Set the repository's reference to the current SiteContext.
    
        @param siteContext The SiteContext
    */

    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Get the given content as an InputStream.  The InputStream will
        return the raw content data.  This default implementation throws 
        an UnsupportedOperationException if the method is invoked.
    
        @param path The path to the content
        @return The InputStream
        @throws Exception
    */
    
    public InputStream getInputStream(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Reading from content stream not supported");
    }
    
    /** Get an OutputStream for writing content to the given path.
        This default implementation throws an UnsupportedOperationException
        if the method is invoked.
        
        @param path The path to the content
        @return The OutputStream
        @throws Exception
    */
    
    public OutputStream getOutputStream(String path) throws Exception{
        throw new UnsupportedOperationException(
            "Writing to content stream not supported");
    }
    
    /** Get an Iterator of paths which are known to the repository.
        The default behavior of this implementation is to throw an
        <code>UnsupportedOperationException</code>.
        
        @return An iterator of paths
        @throws Exception
    */
    
    public Iterator getPaths() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /** Get an Iterator of paths which are known to the repository, starting
        from the specified base path.  The default behavior of this 
        implementation is to throw an 
        <code>UnsupportedOperationException</code>.
        
        @param base The base path
        @return An iterator of paths
        @throws Exception
    */
    
    public Iterator getPaths(String base) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /** Get the input encoding for the given path or the default if the path
        is not found.  This method returns the value of
        CharacterEncodingManager.getPageEncoding(path).
    
        @param The path
        @return The input encoding
    */
    
    protected String getInputEncoding(String path){
        return siteContext.getCharacterEncodingManager().
            getMap(path).getPageEncoding();
    }

}
