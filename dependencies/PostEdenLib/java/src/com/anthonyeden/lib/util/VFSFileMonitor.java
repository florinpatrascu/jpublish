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

package com.anthonyeden.lib.util;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileSystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.Reloadable;
import com.anthonyeden.lib.ChainedRuntimeException;

/** Class which monitors a file in a thread separate from the main application.
    If the file changes then the reload() method of the given target will be
    executed.
    
    @author Anthony Eden
*/

public class VFSFileMonitor implements Runnable{
    
    /** The default delay value (5 seconds) for checking for modifications. */
    public static final int DEFAULT_DELAY = 5000;
    
    private static final Log log = LogFactory.getLog(VFSFileMonitor.class);
    
    private FileObject file;
    private long delay;
    private Reloadable target;
    private long lastModified;
    
    private Thread thread;
    private boolean running = false;

    /** Construct a new VFSFileMonitor for the given file and target.  The 
        default delay time will be used.
        
        @param file The file to monitor
        @param target The target
        @throws FileSystemException
    */

    public VFSFileMonitor(FileObject file, Reloadable target)
    throws FileSystemException{
        this(file, DEFAULT_DELAY, target);
    }
    
    /** Construct a new VFSFileMonitor for the given file and target.  The given
        delay time will be used.
        
        @param file The file to monitor
        @param delay The delay time
        @param target The target
        @throws FileSystemException
    */

    public VFSFileMonitor(FileObject file, long delay, Reloadable target)
    throws FileSystemException{
        this.file = file;
        this.delay = delay;
        this.target = target;
        
        lastModified = file.getContent().getLastModifiedTime();
    }
    
    /** Start the monitoring thread.  If the thread is already running then 
        this method will not attempt to start a new thread.
    */
    
    public void startMonitor(){
        if(!running){
            log.debug("Starting FileMonitor");
        
            running = true;
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    /** Stop the monitoring thread.  If the monitoring thread is not running
        then this method will do nothing.
    */
    
    public void stopMonitor(){
        if(thread != null && running){
            log.debug("Stopping FileMonitor");
        
            running = false;
            thread.interrupt();
        }
    }
    
    /** Get the file which is being monitored.
    
        @return The monitored file
    */
    
    public FileObject getFile(){
        return file;
    }
    
    /** Get the delay between file checks.
    
        @return The delay in milliseconds
    */
    
    public long getDelay(){
        return delay;
    }
    
    /** Set the delay between checks.  If the thread is already sleeping then
        this delay will take effect during the next sleep.
        
        @param delay The delay in milliseconds
    */
    
    public void setDelay(long delay){
        this.delay = delay;
    }
    
    /** The thread's run method.  This method should not be executed directly
        nor should a thread be manually created, rather the <code>startMonitor()</code>
        and <code>stopMonitor()</code> methods should be used to start and stop 
        the monitor.
    */
    
    public void run(){
        log.debug("FileMonitor running.");
        while(running){
            try{
                Thread.sleep(delay);
            } catch(InterruptedException e){
                // do nothing
            }
            
            try{
                FileContent content = file.getContent();
                long currentLastModified = content.getLastModifiedTime();
                if(currentLastModified != lastModified){
                    log.info("Reloading " + file);
                    target.reload();
                    lastModified = currentLastModified;
                }
            } catch(FileSystemException e){
                throw new ChainedRuntimeException(e);
            }
        }
        log.debug("FileMonitor stopped.");
    }

}
