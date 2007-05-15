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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishCacheException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 8-Jul-2006 8:06:33 PM)
 */
public class JPublishSimpleCacheImpl implements JPublishCache {
    protected static final Log log = LogFactory.getLog(JPublishSimpleCacheImpl.class);
    private Map cache;

    /**
     * Add or creates a new cache with a given name
     *
     * @param cacheName the name of the cache
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void addCache(String cacheName) throws JPublishCacheException {
        log.info(cacheName + " created.");
        cache = new HashMap(10);
    }

    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public Object get(Object key) throws JPublishCacheException {
        if (key == null)
            throw new JPublishCacheException("Invalid key specification: null");

        return cache.get(key);
    }

    /**
     * Puts an object into the cache.
     *
     * @param key   a serializable} key
     * @param value a Serializable value
     * @throws org.jpublish.JPublishCacheException
     *          if the parameters are not {@link java.io.Serializable} or another {@link Exception} occurs.
     */
    public void put(Object key, Object value) throws JPublishCacheException {
        if (key != null && value != null)
            cache.put(key, value);

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
        if (cache.containsKey(key))
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
        cache = null;
    }

    /**
     * define the cache flush interval
     *
     * @param interval
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public void setFlushInterval(long interval) throws JPublishCacheException {
    }

    /**
     * retrieves the flushing interval
     *
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public long getFlushInterval() throws JPublishCacheException {
        return 0;
    }

    /**
     * @return
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public List getKeys() throws JPublishCacheException {
        if (cache != null)
            return Arrays.asList(cache.keySet().toArray());
        else
            return null;
    }
}
