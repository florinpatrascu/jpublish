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

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/** Abstract implementation of the ResourceLoader interface.  Provides
    default implementations of some methods.

    @author Anthony Eden
*/

public abstract class AbstractResourceLoader implements ResourceLoader{
    
    /** The default delay time in milliseconds (5 seconds). */
    public static final int DEFAULT_DELAY = 5000;
    
    private int delay = DEFAULT_DELAY;
    private ArrayList monitors = new ArrayList();
    
    /** Load the resource specified by the given path.  Calling this method will
        cause the resource to be loaded and monitored.
    
        @param path The path
        @param handler The ResourceReceipient callback
        @throws ResourceException
    */
    
    public void loadResource(String path, ResourceRecipient handler) throws ResourceException{
        loadResource(path, handler, true);
    }
    
    /** Get the monitoring delay.
    
        @return The monitoring delay in milliseconds
    */

    public int getDelay(){
        return delay;
    }
    
    /** Set the monitoring delay in milliseconds.
    
        @param delay The delay in milliseconds
    */
    
    public void setDelay(int delay){
        this.delay = delay;
    }
    
    /** Start all stopped ResourceMonitors. */
    
    public void startMonitors(){
        Iterator iter = monitors.iterator();
        while(iter.hasNext()){
            ResourceMonitor monitor = (ResourceMonitor)iter.next();
            monitor.startMonitor();
        }
    }
    
    /** Stop all running ResourceMonitors. */
    
    public void stopMonitors(){
        Iterator iter = monitors.iterator();
        while(iter.hasNext()){
            ResourceMonitor monitor = (ResourceMonitor)iter.next();
            monitor.stopMonitor();
        }
    }
    
    /** Get a List of all registered monitors.
    
        @return A List of monitors
    */
    
    protected List getMonitors(){
        return monitors;
    }

}
