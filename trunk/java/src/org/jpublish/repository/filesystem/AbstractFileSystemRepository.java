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

import org.jpublish.repository.AbstractRepository;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;
import org.jpublish.util.vfs.provider.filesystem.FileSystemProvider;

import java.io.File;

/** Abstract base class for file system repository implementations.

    @author Anthony Eden
    @since 1.2
*/

public abstract class AbstractFileSystemRepository extends AbstractRepository{
    
    protected boolean writeAllowed = false;
    protected VFSProvider provider;
    
    /** Returns true if writing is allowed.
    
        @return True if writing is allowed
    */
    
    public boolean isWriteAllowed(){
        return writeAllowed;
    }
    
    /** Set to true to enable writing of data to content paths.
    
        @param writeAllowed True to allow writing
    */
    
    public void setWriteAllowed(boolean writeAllowed){
        this.writeAllowed = writeAllowed;
    }
    
    /** Set to the String "true" to enable writing of data to content paths.
    
        @param writeAllowed True to allow writing
    */
    
    public void setWriteAllowed(String writeAllowed){
        if(writeAllowed != null){
            setWriteAllowed(new Boolean(writeAllowed).booleanValue());
        }
    }
    
    /** Get the Virtual File System root file.  The Virtual File System
        provides a datasource-independent way of navigating through all
        items known to the Repository.
        
        @return The root VFSFile
        @throws Exception
    */
    
    public synchronized VFSFile getVFSRoot() throws Exception{
        if(provider == null){
            provider = new FileSystemProvider(getRealRoot());
        }
        return provider.getRoot();
    }
    
    /** Get the content root.
    
        @return The content root
    */
    
    File getRealRoot(){
        if(root != null){
            File rootFile = new File(root);
            if(!rootFile.isAbsolute()){
                rootFile = new File(siteContext.getRoot(), rootFile.getPath());
            }
            return rootFile;
        } else {
            return siteContext.getRoot();
        }
    }
    
}
