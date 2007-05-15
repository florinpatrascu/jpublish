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

package org.jpublish.view.velocity;

import com.anthonyeden.lib.util.ClassUtilities;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.jpublish.Content;
import org.jpublish.SiteContext;

import java.io.InputStream;

/**
 * Custom resource loader for the Velocity engine.
 *
 * @author Anthony Eden
 * @author Florin
 * @since 2.0
 */

public class JPublishResourceLoader extends ResourceLoader {

    private static final String VM_GLOBAL_LIBRARY = "VM_global_library.vm";
    private static final Log log = LogFactory.getLog(JPublishResourceLoader.class);
    /**
     * cache interim solutin until we'll unify the concept of page cache
     */
    private SiteContext siteContext;

    /**
     * Set the SiteContext.
     *
     * @param siteContext The SiteContext
     */

    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;

    }

    /**
     * Initialize the ResourceLoader.
     *
     * @param configuration The ExtendedProperties object
     */

    public void init(ExtendedProperties configuration) {
        if (log.isDebugEnabled())
            log.debug("init()");
    }


    /**
     * Common init method for ResourceLoaders.
     *
     * @param rs            The RuntimeServices
     * @param configuration The configuration
     */

    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {

        if (log.isDebugEnabled())
            log.debug("commonInit()");

        super.commonInit(rs, configuration);

        Object siteContext = rs.getProperty("jpublish.resource.loader.siteContext");
        if (log.isDebugEnabled())
            log.debug("SiteContext from rs: " + siteContext);

        if (siteContext == null) {
            siteContext = configuration.get("jpublish.resource.loader.siteContext");

            if (log.isDebugEnabled())
                log.debug("SiteContext from configuration: " + siteContext);
        }

        setSiteContext((SiteContext) siteContext);
    }

    /**
     * Get the InputStream for the resource.
     * added synchronizatin for tests; Florin
     *
     * @param name The resource name
     * @return The InputStream
     * @throws ResourceNotFoundException
     */

    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (VM_GLOBAL_LIBRARY.equals(name)) {
            if (log.isDebugEnabled())
                log.debug("Loading global library");

            return ClassUtilities.getResourceAsStream(name);
        }

        if (log.isDebugEnabled())
            log.debug("getResourceStream(" + name + ")");

        return getContent(name).getInputStream();
    }

    /**
     * Return true if the source is modified.
     *
     * @param resource The Resource
     * @return True if it is modified
     */

    public boolean isSourceModified(Resource resource) {
        return resource.getLastModified() != getLastModified(resource);
    }

    /**
     * Return get the last modified time for the resource.
     *
     * @param resource The Resource
     * @return The last modified time
     */

    public long getLastModified(Resource resource) {
        if (VM_GLOBAL_LIBRARY.equals(resource.getName())) {
            return -1;
        }

        try {
            return siteContext.getLastModified(resource.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get the named content object.
     *
     * @param name The content name
     * @return The Content object
     */

    protected Content getContent(String name) {
        return siteContext.getContent(name);
    }

}
