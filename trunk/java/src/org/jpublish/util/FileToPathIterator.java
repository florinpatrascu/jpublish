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

package org.jpublish.util;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Iterator which converts Files from the specified Iterator to a path
    relative to the specified root.
    
    @author Anthony Eden
    @since 1.3
*/

public class FileToPathIterator implements Iterator{
    
    private static final Log log = LogFactory.getLog(FileToPathIterator.class);
    
    private String root;
    private Iterator iter;
    
    /** Construct a new FileToPathIterator which is backed by
        the specified Iterator.
        
        @param root Root file
        @param iter The wrapped Iterator
    */

    public FileToPathIterator(String root, Iterator iter){
        this.root = root;
        this.iter = iter;
    }
    
    /** Returns true if the iteration has more elements. (In other words, 
        returns true if next would return an element rather than throwing 
        an exception.)
        
        @return True if the iteration has more elements
    */

    public boolean hasNext(){
        return iter.hasNext();
    }
    
    /** Returns the next element in the iteration.
    
        @return The next element in the iteration
    */
    
    public Object next(){
        String file = iter.next().toString();
        String path = file.substring(root.length());
        log.info("Next path: " + path);
        return path;
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
