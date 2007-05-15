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

import org.jpublish.JPublishCacheException;

import java.util.List;

/**
 * Simple cache interface
 *
 * @author Florin T.PATRASCU <me@flop.ca>
 * @since $Revision$ (created: Feb 18, 2005 9:31:43 PM)
 */
public interface JPublishCache {


    /**
     * Add or creates a new cache with a given name
     *
     * @param cacheName the name of the cache
     * @throws JPublishCacheException
     */
    public void addCache(String cacheName) throws JPublishCacheException;


    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     * @throws JPublishCacheException
     */
    public Object get(Object key) throws JPublishCacheException;


    /**
     * Puts an object into the cache.
     *
     * @param key   a serializable} key
     * @param value a Serializable value
     * @throws JPublishCacheException if the parameters are not {@link java.io.Serializable} or another {@link Exception} occurs.
     */
    public void put(Object key, Object value) throws JPublishCacheException;


    /**
     * Removes the element which matches the key.
     * <p/>
     * If no element matches, nothing is removed and no Exception is thrown.
     *
     * @param key the key of the element to remove
     * @throws JPublishCacheException
     */
    public void remove(Object key) throws JPublishCacheException;

    /**
     * Remove all elements in the cache, but leave the cache
     * <p/>
     * in a useable state.
     *
     * @throws JPublishCacheException
     */
    public void clear() throws JPublishCacheException;


    /**
     * Remove the cache and make it unuseable.
     *
     * @throws JPublishCacheException
     */
    public void destroy() throws JPublishCacheException;

    /**
     * define the cache flush interval
     *
     * @param interval
     * @throws JPublishCacheException
     */
    public void setFlushInterval(long interval) throws JPublishCacheException;

    /**
     * retrieves the flushing interval
     *
     * @throws JPublishCacheException
     */
    public long getFlushInterval() throws JPublishCacheException;

    /**
     *
     *
     * @return
     * @throws JPublishCacheException
     */
    public List getKeys() throws JPublishCacheException;


}
