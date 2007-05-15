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

package org.jpublish.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.SiteContext;
import org.jpublish.StaticResourceManager;

/** An abstract implementation of the StaticResourceManager interface which 
    provides behavior common to all StaticResourceManager implementations.

    @author Anthony Eden
    @since 1.3
*/

public abstract class AbstractStaticResourceManager implements 
StaticResourceManager{
    
    private static Log log = LogFactory.getLog(
        AbstractStaticResourceManager.class);
    
    protected SiteContext siteContext;

    /** Get the SiteContext.
    
        @return The SiteContext
    */
    
    public SiteContext getSiteContext(){
        return siteContext;
    }
    
    /** Set the SiteContext.
    
        @param siteContext The site context
    */

    public void setSiteContext(SiteContext siteContext){
        log.debug("Site context set.");
        this.siteContext = siteContext;
    }
    
    /** Load the PageManager's configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws ConfigurationException{
        // no op
    }

}
