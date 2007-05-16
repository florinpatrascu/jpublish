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

/** Base interface for Cache implementations.

    @author Anthony Eden
    @since 1.1
*/

public interface Cache{
    
    /** Get the value for the given key from the cache.  This method may
        return null if there is no value or the value is expired.
    
        @param key The key
        @return The value or null
    */
    
    public Object get(Object key);
    
    /** Insert a value into the cache.
    
        @param key The key
        @param value The value
    */
    
    public void put(Object key, Object value);
    
    /** Insert a value into the cache with the specified time to live.
        The specified time to live overrides the default time to live.
        A value less than 0 is be considered as "infinate".
    
        @param key The key
        @param value The value
        @param ttl The time to live
    */
    
    public void put(Object key, Object value, int ttl);
    
    /** Remove a value from the cache.
    
        @param key The key
    */
    
    public void remove(Object key);
    
    /** Get the default time to live.
    
        @return The default time to live
    */
    
    public int getTTL();
    
    /** Set the default time to live.
    
        @param ttl The new default time to live
    */
    
    public void setTTL(int ttl);
    
    /** Load the Cache's configuration from the given Configuration object.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws ConfigurationException;
    
}
