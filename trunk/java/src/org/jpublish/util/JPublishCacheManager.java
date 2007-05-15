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

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishCacheException;
import org.jpublish.SiteContext;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple cache manager
 *
 * @author Florin T.PATRASCU <florin.patrascu@fmail.com>
 * @since $Revision$ (created: Feb 18, 2005 8:38:26 PM)
 */

public class JPublishCacheManager {
    private static Log log = LogFactory.getLog(JPublishCacheManager.class);
    private static final String EMPTY_STRING = "";

    private SiteContext siteContext;
    private Map cacheImplementations = new TreeMap();

    public JPublishCacheManager(SiteContext siteContext) {
        this.siteContext = siteContext;
    }

    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException {

        // load cache manager definitions
        Iterator defineCacheElements = configuration.getChild("cache-manager")
                .getChildren("jpublish.cache.provider").iterator();

        while (defineCacheElements.hasNext()) {
            String clazz = EMPTY_STRING;
            String name = EMPTY_STRING;

            try {
                Configuration defineCacheElement = (Configuration) defineCacheElements.next();
                name = defineCacheElement.getChild("implementation").getAttribute("name");
                clazz = defineCacheElement.getChild("implementation").getAttribute("class");
                log.info("New cache definition: " + name);

                Class aClass = Class.forName(clazz);
                JPublishCache jPublishCache = (JPublishCache) aClass.newInstance();
                jPublishCache.addCache(name);
                jPublishCache.setFlushInterval(calculateFlushIntervalFromConfig(defineCacheElement));

                cacheImplementations.put(name, jPublishCache);
                log.info("cache created...");

            } catch (ClassNotFoundException e) {
                log.error("Implementation not found: " + clazz, e);
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();

            } catch (InstantiationException e) {
                e.printStackTrace();

            } catch (JPublishCacheException e) {
                log.error("Cannot instantiate the cache: " + name, e);
                e.printStackTrace();
            }

        }
        siteContext.setAttribute("JPublishCacheManager", this);
    }

    /**
     * calculating the interval required for an unused object to live in the cache
     *
     * @param configuration
     * @return a long representing the flush interval
     */
    private long calculateFlushIntervalFromConfig(Configuration configuration) {
        long t = 0;
        try {
            if (log.isDebugEnabled())
                log.debug("Check the cache model flush interval.");

            Configuration c = configuration.getChild("flushInterval");
            if (c != null) {
                String milliseconds = c.getAttribute("milliseconds");
                String seconds = c.getAttribute("seconds");
                String minutes = c.getAttribute("minutes");
                String hours = c.getAttribute("hours");
                if (milliseconds != null) t += Integer.parseInt(milliseconds);
                if (seconds != null) t += Integer.parseInt(seconds) * 1000;
                if (minutes != null) t += Integer.parseInt(minutes) * 60 * 1000;
                if (hours != null) t += Integer.parseInt(hours) * 60 * 60 * 1000;
                if (t < 1) throw new IllegalArgumentException(
                        "A flush interval must specify one or more of milliseconds, seconds, minutes or hours.");
            } else {
                log.warn("no cache flush interval defined for this cache space! Zero flush interval assumed;");
            }
        } catch (NumberFormatException e) {
            //throw new NestedRuntimeException("Error building cache '" + vars.currentCacheModel.getId() + "' in '" + "resourceNAME" + "'.  Flush interval milliseconds must be a valid long integer value.  Cause: " + e, e);
        }
        log.info("flushing cache after: " + t + "ms. (not yet implemented)");
        return t;
    }

    /**
     * returns a cache storage by name
     *
     * @param cacheName
     * @return the cache implementation or null if the cache name doesn't exists
     */
    public JPublishCache getCache(String cacheName) {
        if (cacheName == null || cacheName.trim().length() == 0 || !cacheImplementations.containsKey(cacheName)) {
            return null;
        }

        return (JPublishCache) cacheImplementations.get(cacheName);
    }

    /**
     *
     * @return the names of the cache currently instantiated
     */
    public String[] getAvailableCacheNames() {
        return (String[]) cacheImplementations.keySet().toArray(new String[cacheImplementations.keySet().size()]);
    }

}
