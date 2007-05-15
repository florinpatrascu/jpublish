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

package org.jpublish.util.vfs;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/** A virutal file in a virtual file system.  JPublish provides a virtual
    file system as a means for navigating through content hierarchies.
    
    @author Anthony Eden
    @since 1.3
*/

public class VFSFile{
    
    private String name;
    private VFSFile parent;
    private List children;
    private boolean directoryFlag;
    private VFSProvider provider;
    
    public VFSFile(String name, VFSFile parent, boolean directoryFlag){
        this.name = name;
        this.parent = parent;
        this.directoryFlag = directoryFlag;
        this.children = new ArrayList();
        
        if(parent != null){
            this.provider = parent.getProvider();
        }
    }
    
    /** Construct a new VFSFile with the given name and parent.
    
        @param name The file name
        @param parent The file parent
        @param directoryFlag Set to true if this virtual file is a directory
    */
    
    public VFSFile(String name, VFSFile parent, boolean directoryFlag, 
    VFSProvider provider){
        this.name = name;
        this.parent = parent;
        this.directoryFlag = directoryFlag;
        this.children = new ArrayList();
        this.provider = provider;
    }
    
    /** Get the file name.
    
        @return The file name
    */
    
    public String getName(){
        return name;
    }
    
    /** Get the file's parent directory.  This method may return null if the
        file is the root.
        
        @return The virtual file's parent directory
    */
    
    public VFSFile getParent(){
        return parent;
    }
    
    /** Get a List of all children.
    
        @return All children
    */
    
    public List getChildren(){
        return children;
    }
    
    /** Return true if this virutal file represents a directory.
    
        @return True if this virtual file is a directory
    */
    
    public boolean isDirectory(){
        return directoryFlag;
    }
    
    /** Get the Virtual File System provider.
    
        @return The VFSProvider
    */
    
    public VFSProvider getProvider(){
        return provider;
    }
    
    /** Return a String representation of the file which is the full path
        for the file.
        
        @return The path String
    */
    
    public String toPathString(){
        StringBuffer buffer = new StringBuffer();
        
        VFSFile currentParent = parent;
        
        if(currentParent == null){
            return "/";
        }
        
        while(currentParent != null){
            if(currentParent.getParent() != null){
                buffer.insert(0, "/");
                buffer.insert(0, currentParent.getName());
            }
            
            currentParent = currentParent.getParent();
        }
        
        // insert the root
        buffer.insert(0, "/");
        
        // append the file name
        buffer.append(getName());
        
        if(isDirectory()){
            buffer.append("/");
        }
        
        return buffer.toString();
    }
    
    /** Get an Iterator which provides a means for iterating through
        each path part in the file's path.
        
        @return The path Iterator
    */
    
    public Iterator getPathIterator(){
        VFSFile currentDirectory = null;
        ArrayList path = new ArrayList();
        if(isDirectory()){
            currentDirectory = this;
        } else {
            currentDirectory = getParent();
        }
        
        while(currentDirectory != null){
            path.add(0, currentDirectory);
            currentDirectory = currentDirectory.getParent();
        }
        
        return path.iterator();
    }
    
    /** Return a String representation of the virtual file.
    
        @return A String
    */
    
    public String toString(){
        return toPathString();
    }
    
}
