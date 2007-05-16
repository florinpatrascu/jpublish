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

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.IOUtilities;

/** Monitors the given File for changes.

    @author Anthony Eden
*/

public class ResourceFileMonitor implements Runnable, ResourceMonitor{
    
    private static final Log log = LogFactory.getLog(ResourceFileMonitor.class);
    
    private File file;
    private int delay;
    private long lastModified;
    private ResourceRecipient handler;
    
    private Thread thread;
    private boolean running = false;

    /** Construct a ResourceFileMonitor for the given file.
    
        @param file The file
        @param delay The delay
        @param handler The handler
    ?*/
    
    public ResourceFileMonitor(File file, int delay, ResourceRecipient handler){
        this.file = file;
        this.delay = delay;
        this.handler = handler;
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
    
    /** The thread's run method.  This method should not be executed directly
        nor should a thread be manually created, rather the <code>startMonitor()</code>
        and <code>stopMonitor()</code> methods should be used to start and stop 
        the monitor.
    */
    
    public void run(){
        log.debug("FileMonitor start.");
        lastModified = file.lastModified();
        while(running){
            long currentLastModified = file.lastModified();
            if(currentLastModified != lastModified){
                log.info("Reloading " + file);
                
                FileInputStream in = null;
                try{
                    in = new FileInputStream(file);
                    handler.load(in);
                } catch(Exception e){
                    e.printStackTrace();
                } finally {
                    IOUtilities.close(in);
                }
                
                lastModified = currentLastModified;
            }
            
            try{
                Thread.sleep(delay);
            } catch(InterruptedException e){
                // do nothing
            }
        }
        log.debug("FileMonitor stopped.");
    }

}
