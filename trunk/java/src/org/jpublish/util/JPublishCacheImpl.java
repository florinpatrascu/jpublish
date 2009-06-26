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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishCacheException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/**
 * JPublish default cache implementation.
 * The default cache used is ehcache,
 * <p/>
 * kudos to Greg Luck
 *
 * @author Florin T.PATRASCU <me@flop.ca>
 * @since $Revision$ (created: Feb 18, 2005 9:38:07 PM)
 */
public class JPublishCacheImpl implements JPublishCache {
    private static final Log log = LogFactory.getLog(JPublishCacheImpl.class);

    private Cache cache;
    private long flushInterval;

    /**
     * Cache constructor
     */
    public JPublishCacheImpl() throws JPublishCacheException {

    }


    public void addCache(String cacheName) throws JPublishCacheException {
        try {

            CacheManager manager = CacheManager.getInstance();
            cache = manager.getCache(cacheName);

            if (cache == null) {
                log.warn("Could not find a valid configuration for: " + cacheName
                        + ", using the 'defaultCache' definition.");

                manager.addCache(cacheName);
                cache = manager.getCache(cacheName);
            }

        } catch (CacheException e) {
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
    public Object get(Object key) throws JPublishCacheException {

        try {

            if (key == null)
                throw new JPublishCacheException("Invalid key specification: null");

            else {
                Element element = cache.get((Serializable) key);

                if (element == null) {
                    if (log.isDebugEnabled())
                        log.debug("null Element for key: [" + key + "]; (re)loading.");
                    return null;
                } else {
                    return element.getValue();
                }
            }
        } catch (CacheException e) {
            throw new JPublishCacheException(e);

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
    public void put(Object key, Object value) throws JPublishCacheException {
        if (key != null && value != null)
            try {
                Element element = new Element((Serializable) key, (Serializable) value);
                //cache.putQuiet(element);
                cache.put(element);
            } catch (ClassCastException cce) {
                throw new JPublishCacheException("(404) ", cce);
            } catch (IllegalArgumentException e) {
                throw new JPublishCacheException(e);
            } catch (IllegalStateException e) {
                throw new JPublishCacheException(e);
            }
        else {
            if (log.isDebugEnabled()) {
                log.debug("null key: " + key);
            }
        }
    }

    /**
     * Removes the element which matches the key.
     * <p/>
     * If no element matches, nothing is removed and no Exception is thrown.
     *
     * @param key the key of the element to remove
     * @throws JPublishCacheException
     */
    public void remove(Object key) throws JPublishCacheException {
        try {
            cache.remove((Serializable) key);
        } catch (ClassCastException e) {
            throw new JPublishCacheException(e);
        } catch (IllegalStateException e) {
            throw new JPublishCacheException(e);
        }
    }


    /**
     * Remove all elements in the cache, but leave the cache
     * <p/>
     * in a useable state.
     *
     * @throws JPublishCacheException
     */
    public void clear() throws JPublishCacheException {

        try {
            cache.removeAll();
        } catch (IllegalStateException e) {
            throw new JPublishCacheException(e);
        }
    }

    /**
     * Remove the cache and make it unuseable.
     *
     * @throws JPublishCacheException
     */
    public void destroy() throws JPublishCacheException {
        try {
            CacheManager.getInstance().removeCache(cache.getName());
        } catch (IllegalStateException e) {
            throw new JPublishCacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new JPublishCacheException(e);
        }
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

    /**
     * @return
     * @throws org.jpublish.JPublishCacheException
     *
     */
    public List getKeys() throws JPublishCacheException {
        try {
            return cache.getKeys();
        } catch (CacheException e) {
            log.error("Cannot get the keys of the elements stored in this cache");
            throw new JPublishCacheException(e);
        }
    }

    public Object getStatistics() throws JPublishCacheException {
        return cache.getStatistics();
    }
}
