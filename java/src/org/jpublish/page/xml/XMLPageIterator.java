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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jpublish.JPublishRuntimeException;

/** Iterator which iterates through the Page objects known to the specified
    PageManager.
    
    @author Anthony Eden
    @since 1.1
*/

public class XMLPageIterator implements Iterator{
    
    private static final Log log = LogFactory.getLog(XMLPageIterator.class);
    
    private XMLPageManager pageManager;
    private Iterator paths;
    private String basePath;
    
    /** Construct a new XMLPageIterator.
    
        @param pageManager The XMLPageManager
        @param paths The list of paths to iterate through
        @param basePath The required base path
    */

    public XMLPageIterator(XMLPageManager pageManager, List paths, 
    String basePath){
        this.pageManager = pageManager;
        this.paths = paths.iterator();
        this.basePath = basePath;
    }
    
    /** Returns true if the iteration has more elements. (In other words, 
        returns true if next would return an element rather than throwing 
        an exception.)
        
        @return True if the iteration has more elements
    */
    
    public boolean hasNext(){
        return paths.hasNext();
    }
    
    /** Returns the next element in the iteration.
    
        @return The next element in the iteration
    */
    
    public Object next(){
        String path = (String)paths.next();
        try{
            return pageManager.getPage(path);
        } catch(FileNotFoundException e){
            log.warn("FileNotFoundException while retrieving path: " + path);
            return next();
        } catch(Exception e){
            throw new JPublishRuntimeException(
                "Error loading page: " + e.getMessage(), e);
        }
    }
    
    /** Removes from the underlying collection the last element returned by 
        the iterator (optional operation). This method can be called only 
        once per call to next. The behavior of an iterator is unspecified 
        if the underlying collection is modified while the iteration is in 
        progress in any way other than by calling this method. 

        @throws UnsupportedOperationException
    */
    
    public void remove(){
        throw new UnsupportedOperationException();
    }
    
}
