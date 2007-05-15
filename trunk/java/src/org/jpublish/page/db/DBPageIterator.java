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
import java.util.Iterator;

import org.jpublish.page.PageInstance;
import org.jpublish.JPublishRuntimeException;
import org.jpublish.page.PageDefinition;

/** Page iterator which uses a database for retrieving Pages.  This iterator
    requires a valid ResultSet from a page table.

    @author Anthony Eden
    @since 1.1
*/

public class DBPageIterator implements Iterator{
    
    private DBPageManager pageManager;
    private ResultSet resultSet;
    
    private PageInstance nextPage;
    
    /** Construct a new DBPageIterator for the given DBPageManager with the
        specified ResultSet.
        
        @param pageManager The page manager
        @param resultSet The result set
    */

    public DBPageIterator(DBPageManager pageManager, ResultSet resultSet){
        this.pageManager = pageManager;
        
        if(resultSet == null){
            throw new IllegalArgumentException("ResultSet can not be null");
        }
        this.resultSet = resultSet;
    }
    
    /** Returns true if the iteration has more elements. (In other words, 
        returns true if next would return an element rather than throwing 
        an exception.)
        
        @return True if the iteration has more elements
    */
    
    public boolean hasNext(){
        return getNext() != null;
    }
    
    /** Returns the next element in the iteration.
    
        @return The next element in the iteration
    */
    
    public Object next(){
        try{
            return getNext();
        } finally {
            nextPage = null;
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
    
    protected PageInstance getNext(){
        if(nextPage == null){
            try{
                if(resultSet.next()){
                
                    String pageXML = resultSet.getString("data");
                    String pagePath = resultSet.getString("path");
                    
                    PageDefinition pageDefinition = new PageDefinition(
                        pageManager.getSiteContext(), pagePath);
                    pageDefinition.loadConfiguration(new StringReader(pageXML));
                    
                    // note that the type will be set to XML.  This is because
                    // the actual page type depends on the request URL, which
                    // is not available to the iterator, so the page path is
                    // used instead.
                    
                    nextPage = pageDefinition.getPageInstance(pagePath);
                } else {
                    return null;
                }
            } catch(Exception e){
                    throw new JPublishRuntimeException(
                        "Error getting page instance", e);
            }
        }
        return nextPage;
    }
    
}
