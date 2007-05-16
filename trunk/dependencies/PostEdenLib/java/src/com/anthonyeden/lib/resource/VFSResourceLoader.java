/*-- 

 Copyright (C) 2000-2003 Anthony Eden.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "EdenLib" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact me@anthonyeden.com.
 
 4. Products derived from this software may not be called "EdenLib", nor
    may "EdenLib" appear in their name, without prior written permission
    from Anthony Eden (me@anthonyeden.com).
 
 In addition, I request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 For more information on EdenLib, please see <http://edenlib.sf.net/>.
 
 */

package com.anthonyeden.lib.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.IOUtilities;

/** Implementation of the ResourceLoader interface which loads data from
    a virtual file system.
    
    @author Anthony Eden
*/

public class VFSResourceLoader extends AbstractResourceLoader{
    
    private static final Log log = LogFactory.getLog(VFSResourceLoader.class);
    
    private FileSystemManager fileSystemManager = null;
    
    /** Get the FileSystemManager.
    
        @return The FileSystemManager
        @throws FileSystemException
    */
    
    public synchronized FileSystemManager getFileSystemManager() 
    throws FileSystemException{
        if(fileSystemManager == null){
            fileSystemManager = VFS.getManager();
        }
        return fileSystemManager;
    }
    
    /** Set the FileSystemManager.
    
        @param fileSystemManager The new FileSystemManager
    */
    
    public void setFileSystemManager(FileSystemManager fileSystemManager){
        this.fileSystemManager = fileSystemManager;
    }

    /** Load the resource specified by the given path.  Calling this method will
        cause the resource to be loaded and monitored.
    
        @param path The path
        @param handler The ResourceReceipient callback
        @throws ResourceException
    */
    
    public void loadResource(String path, ResourceRecipient handler) throws 
    ResourceException{
        loadResource(path, handler, true);
    }
    
    /** Load the resource specified by the given path.  If monitor is true then
        the ResourceLoader implementation will monitor the resource and call the
        ResourceRecipient each time the resource is modified.
        
        @param path The path
        @param handler The ResourceRecipient callback
        @param monitor True to monitor the resource
        @throws ResourceException
    */

    public void loadResource(String path, ResourceRecipient handler, 
    boolean monitor) throws ResourceException{
        InputStream in = null;
        try{
            FileSystemManager fsManager = getFileSystemManager();
            log.debug("Resource path: " + path);
            FileObject file = fsManager.resolveFile(path);
            if(!file.exists()){
                throw new FileNotFoundException("File not found: " + file);
            }
            
            FileContent content = file.getContent();
            in = content.getInputStream();
            handler.load(in);
            
            if(monitor){
                ResourceVFSMonitor resourceMonitor = new ResourceVFSMonitor(
                    file, getDelay(), handler);
                getMonitors().add(resourceMonitor);
                resourceMonitor.startMonitor();
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(e);
        } finally {
            IOUtilities.close(in);
        }
    }
    
}
