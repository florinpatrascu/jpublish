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

package org.jpublish.repository.filesystem;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Iterator which iterates through the Files in the file system and converts
    them to path Strings.
    
    @author Anthony Eden
    @since 1.2
*/

public class FileSystemPathIterator implements Iterator{
    
    private static final Log log = LogFactory.getLog(
        FileSystemPathIterator.class);
    
    private AbstractFileSystemRepository repository;
    private Iterator iter;

    /** Construct a new FileSystemPathIterator which is backed by the specified
        iterator and AbstractFileSystemRepository.
        
        @param iter The Iterator
        @param repository The AbstractFileSystemRepository
    */
    
    public FileSystemPathIterator(Iterator iter, 
    AbstractFileSystemRepository repository){
        this.iter = iter;
        this.repository = repository;
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
        String root = repository.getRoot().toString();
        if(log.isDebugEnabled())
            log.debug("Root: " + root);
        String realRoot = repository.getRealRoot().toString();
        if(log.isDebugEnabled())
            log.debug("Real root: " + realRoot);
        File file = (File)iter.next();
        String path = file.toString().substring(realRoot.length());
        if(log.isDebugEnabled())
            log.debug("Next path: " + path);
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
