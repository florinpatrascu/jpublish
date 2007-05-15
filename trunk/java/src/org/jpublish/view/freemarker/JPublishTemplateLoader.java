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

package org.jpublish.view.freemarker;

import freemarker.cache.TemplateLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.Content;
import org.jpublish.SiteContext;

import java.io.Reader;

/** Implementation of the FreeMarker TemplateLoader interface.

    @author Anthony Eden
    @since 2.0
*/

public class JPublishTemplateLoader implements TemplateLoader{
    
    private static final Log log = 
        LogFactory.getLog(JPublishTemplateLoader.class);
        
    private SiteContext siteContext;
    
    /** Set the SiteContext.
    
        @param siteContext The SiteContext
    */
    
    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Find the template source.
    
        @param name The template name
        @return The source object
    */
    
    public Object findTemplateSource(String name){
        if (log.isDebugEnabled())
            log.debug("findTemplateSource(" + name + ")");
        
        Object content = siteContext.getContent(name);
        if (log.isDebugEnabled())
            log.debug("findTemplateSource() content: " + content);
        return content;
    }
    
    /** Get the last modified time of the template source.
    
        @param templateSource The source
        @return The last modified time
    */
    
    public long getLastModified(Object templateSource){
        log.debug("getLastModified() invoked");
        long lastModified = ((Content)templateSource).getLastModified();
        if(log.isDebugEnabled())
            log.debug("Last modified time: " + lastModified);
        return lastModified;
    }
    
    /** Get the template source reader.
    
        @param templateSource The source
        @param encoding The character encoding
        @return The Reader
    */
    
    public Reader getReader(Object templateSource, String encoding){
        if(log.isDebugEnabled())
            log.debug("getReader() invoked [encoding=" + encoding + "]");
        return ((Content)templateSource).getReader(encoding);
    }
    
    /** Close the specified template source.
    
        @param templateSource The template source
    */
    
    public void closeTemplateSource(Object templateSource){
        // currently no-op.  Cleanup needed? -AE
    }
    
}
