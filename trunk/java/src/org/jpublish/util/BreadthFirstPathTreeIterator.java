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

import java.util.Set;
import java.util.Stack;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

/** Breadth first iterator which iterates through all paths which are decendants
    of the specified base path.  This iterator is used for iterating through
    the paths returned by the ServletContext.getResourcePaths() method.
    
    @author Anthony Eden
    @since 2.0
*/

public class BreadthFirstPathTreeIterator implements Iterator{
    
    private String root;
    private int currentIndex = 0;
    private String[] currentList;
    private String nextFile;
    private Stack directories;
    private boolean endOfTree = false;
    private ServletContext servletContext;
    
    /** Construct a new BreadthFirstPathTreeIterator with the specified root.
    
        @param root The root directory
        @param servletContext The ServletContext
    */
    
    public BreadthFirstPathTreeIterator(String root, 
    ServletContext servletContext){
        this.root = root;
        this.servletContext = servletContext;
        this.currentList = getPathArray(root);
        this.directories = new Stack();
    }
    
    /** Returns true if the iteration has more elements. (In other words, 
        returns true if next would return an element rather than throwing 
        an exception.)
        
        @return True if the iteration has more elements
    */

    public boolean hasNext(){
        if(endOfTree)
            return false;
        return getNextFile() != null;
    }
    
    /** Returns the next element in the iteration.
    
        @return The next element in the iteration
    */
    
    public Object next(){
        if(endOfTree)
            throw new NoSuchElementException();
        
        String file = getNextFile();
        if(file == null){
            throw new NoSuchElementException();
        }
        this.nextFile = null;
        return file;
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
    
    /** Get the next file.  If the value for the next file is null then the
        findNextFile() method is invoked to locate the next file.  A call
        to next() will return the next file and will null out the next file
        variable.
    
        @return The next file
    */
    
    protected String getNextFile(){
        if(nextFile == null){
            nextFile = findNextFile();
        }
        return nextFile;
    }
    
    /** Find the next file.
    
        @return The next file
    */
    
    protected String findNextFile(){
        while(currentIndex < currentList.length){
            if(currentList[currentIndex].endsWith("/")){
                directories.push(currentList[currentIndex]);
                currentIndex++;
            } else {
                String file = currentList[currentIndex];
                currentIndex++;
                return file;
            }
        }
        
        while(!directories.empty()){
            String directory = (String)directories.remove(0);
            currentList = getPathArray(directory);
            currentIndex = 0;
            String file = findNextFile();
            if(file != null){
                return file;
            }
        }
        
        endOfTree = true;
        
        return null;
    }
    
    protected String[] getPathArray(String path){
        Set pathSet = servletContext.getResourcePaths(path);
        return (String[])pathSet.toArray(new String[pathSet.size()]);
    }
    
}
