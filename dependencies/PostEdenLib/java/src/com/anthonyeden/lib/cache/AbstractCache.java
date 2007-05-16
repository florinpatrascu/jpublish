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

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

/** Abstract implementation of the Cache interface.  Cache implementations
    can use this class as a base class for their implementation.
    
    @author Anthony Eden
    @since 1.1
*/

public abstract class AbstractCache implements Cache{
    
    /** The default TTL (-1 infinate). */
    public static final int DEFAULT_TTL = -1;
    
    /** The default time to live. */
    protected int ttl = DEFAULT_TTL;
    
    /** Get the default time to live for the cache.  A value less than 0 is
        be considered as "infinate".
    
        @return The default time to live
    */
    
    public int getTTL(){
        return ttl;
    }
    
    /** Set the default time to live for the cache.  A value less than 0 is
        be considered as "infinate".
    
        @param ttl The default time to live
    */
    
    public void setTTL(int ttl){
        this.ttl = ttl;
    }
    
    /** Set the default time to live of the Cache.  This method is useful if
        the time to live is coming from a configuration String.  If the String
        is null then the time to live is set to the value of 
        <code>AbstractCache.DEFAULT_TTL</code>.
        
        @param ttl The default time to live as a String
    */
    
    public void setTTL(String ttl){
        if(ttl == null){
            setTTL(DEFAULT_TTL);
        } else {
            setTTL(Integer.parseInt(ttl));
        }
    }
    
    /** Check to see if the given CacheEntry is expired.
    
        @param cacheEntry The CacheEntry
    */
    
    protected synchronized boolean isExpired(CacheEntry cacheEntry){
        // use the default TTL initially
        int entryTTL = ttl;
        
        // check if the CacheEntry has specified the TTL
        int cacheTTL = cacheEntry.getTTL();
        if(cacheTTL >= 0){
            entryTTL = cacheTTL;
        }
        
        // if the TTL is less then 0 then it is considered infinate
        if(entryTTL < 0){
            return false;
        }
        
        return System.currentTimeMillis() - cacheEntry.getLastRequestTime() > entryTTL;
    }
    
    /** Load the Cache's configuration from the given Configuration object.
        This is a no-op implementation.  Subclasses which require configuration
        can override it.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws ConfigurationException{
        // no-op
    }

}
