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

package org.jpublish.page;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.XMLConfiguration;
import org.jpublish.SiteContext;
import org.jpublish.util.PathUtilities;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * A class which represents a page definition.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class PageDefinition {

    private SiteContext siteContext;
    private String path;
    private Configuration configuration;
    private Map pageCache;

    /**
     * Construct a new PageDefinition for the given path.
     *
     * @param siteContext The SiteContext
     * @param path        The definition path
     */

    public PageDefinition(SiteContext siteContext, String path) {
        this.siteContext = siteContext;
        this.path = path;

        pageCache = new HashMap();
    }

    /**
     * Get the request path.
     *
     * @return The request path
     */

    public String getPath() {
        return path;
    }

    /**
     * Return a Page instance for the given path.  The path is used to
     * determine the page name and page type.  The PageDefinition will
     * cache all Page instances.
     *
     * @param path The request path
     * @return The Page instance
     * @throws Exception Any Exception
     */

    public synchronized PageInstance getPageInstance(String path) throws Exception {

        PageInstance page = (PageInstance) pageCache.get(path);
        if (page == null) {
            page = new PageInstance(siteContext, path,
                    PathUtilities.extractPageName(path),
                    PathUtilities.extractPageType(path));
            page.loadConfiguration(configuration);
            pageCache.put(path, page);
        }
        return page;
    }

    /**
     * Load the configuration from the specified Reader.
     *
     * @param in The Reader
     * @throws Exception
     * @since 1.1
     */

    public void loadConfiguration(Reader in) throws Exception {
        loadConfiguration(new XMLConfiguration(in));
    }

    /**
     * Load the configuration from the specified InputStream.
     *
     * @param in The InputStream
     * @throws Exception
     */

    public void loadConfiguration(InputStream in) throws Exception {
        loadConfiguration(new XMLConfiguration(in));
    }

    /**
     * Load the configuration from the specified InputStream.
     *
     * @param id id used to offer more details about the errors occured at parsing
     * @param in The InputStream
     * @throws Exception
     */

    public void loadConfiguration(String id, InputStream in) throws Exception {
        loadConfiguration(new XMLConfiguration(id, in));
    }

    /**
     * Load the configuration.
     *
     * @param configuration The Configuration object
     * @throws Exception Any Exception
     */

    public void loadConfiguration(Configuration configuration) throws Exception {
        this.configuration = configuration;
        pageCache.clear();
    }

}
