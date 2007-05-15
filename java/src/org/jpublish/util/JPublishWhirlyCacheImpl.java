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

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishCacheException;

import java.util.List;

/**
 * WirlyCache implementation
 *
 * @author Florin T.PATRASCU <me@flop.ca>
 * @since $Revision$ (created: Feb 20, 2005 1:12:05 PM)
 */
public class JPublishWhirlyCacheImpl implements JPublishCache {
    private static Log log = LogFactory.getLog(JPublishWhirlyCacheImpl.class);
    private Cache cache;
    private long flushInterval = 0l;

    /**
     * Add or creates a new cache with a given name
     *
     * @param cacheName the name of the cache
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void addCache(String cacheName) throws JPublishCacheException {
        try {
            cache = CacheManager.getInstance().getCache();
        } catch (CacheException e) {
            log.error("error creating cache: " + cacheName);
            throw new JPublishCacheException(e);
        }
    }

    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public synchronized Object get(Object key) throws JPublishCacheException {
        try {
            return cache.retrieve(key);
        } catch (Exception e) {
            throw new JPublishCacheException(e.getMessage());
        }
    }

    /**
     * Puts an object into the cache.
     *
     * @param key   a serializable} key
     * @param value a Serializable value
     * @throws org.jpublish.JPublishCacheException
     *          if the parameters are not {@link java.io.Serializable} or another {@link Exception} occurs.
     */
    public synchronized void put(Object key, Object value) throws JPublishCacheException {
        if (key != null)
            try {
                cache.store(key, value);
            } catch (Exception e) {
                throw new JPublishCacheException("cache error: " + e.getMessage());
            }
    }

    /**
     * Removes the element which matches the key.
     * <p/>
     * If no element matches, nothing is removed and no Exception is thrown.
     *
     * @param key the key of the element to remove
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void remove(Object key) throws JPublishCacheException {
        if (key != null)
            cache.remove(key);
    }

    /**
     * Remove all elements in the cache, but leave the cache
     * <p/>
     * in a useable state.
     *
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void clear() throws JPublishCacheException {
        cache.clear();
    }

    /**
     * Remove the cache and make it unuseable.
     *
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void destroy() throws JPublishCacheException {
/*
        try {
            CacheManager.getInstance().shutdown();
        } catch (CacheException e) {
            e.printStackTrace();
        }
*/
    }

    /**
     * define the cache flush interval
     *
     * @param interval
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void setFlushInterval(long interval) throws JPublishCacheException {
        flushInterval = interval;
    }

    /**
     * retrieves the flushing interval
     *
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public long getFlushInterval() throws JPublishCacheException {
        return flushInterval;
    }


    public List getKeys() throws JPublishCacheException {
        //throw new JPublishCacheException("Not supported");
        log.warn("'getKeys()' not supported by WhirlyCache. Sorry!");
        return null;
    }
}
