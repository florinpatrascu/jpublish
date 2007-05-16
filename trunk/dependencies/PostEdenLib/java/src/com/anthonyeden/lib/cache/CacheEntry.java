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

package com.anthonyeden.lib.cache;

/** A single entry in a cache.  CacheEntry objects can include a specific
    time to live which overrides the default time to live.  If the time
    to live is -1 then the default is used.

    @author Anthony Eden
    @since 1.1
*/

public class CacheEntry{
    
    private Object object;
    private int ttl;
    private long insertTime;
    private long lastRequestTime;
    
    /** Construct a new CacheEntry.
    
        @param object The cached object
        @param insertTime The time when the object was inserted
    */
    
    public CacheEntry(Object object, long insertTime){
        this(object, insertTime, -1);
    }
    
    /** Construct a new CacheEntry with the given time to live.
    
        @param object The cached object
        @param insertTime The time when the object was inserted
        @param ttl The time to live of the entry
    */
    
    public CacheEntry(Object object, long insertTime, int ttl){
        this.object = object;
        this.insertTime = insertTime;
        this.ttl = ttl;
        
        setLastRequestTime(insertTime);
    }
    
    /** Get the cached object.
    
        @return The cached object
    */
    
    public Object getObject(){
        return object;
    }
    
    /** Get the insert time.
    
        @return The insert time
    */
    
    public long getInsertTime(){
        return insertTime;
    }
    
    /** Get the time to live.
    
        @return The time to live
    */
    
    public int getTTL(){
        return ttl;
    }
    
    /** Set the time to live
    
        @param ttl The time to live
    */
    
    public void setTTL(int ttl){
        this.ttl = ttl;
    }
    
    /** Get the last request time.
    
        @return The last request time
    */
    
    public long getLastRequestTime(){
        return lastRequestTime;
    }
    
    /** Set the last request time.
    
        @param lastRequestTime The last request time
    */
    
    public void setLastRequestTime(long lastRequestTime){
        this.lastRequestTime = lastRequestTime;
    }
    
}
