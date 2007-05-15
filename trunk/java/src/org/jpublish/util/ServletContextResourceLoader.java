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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.resource.ResourceRecipient;
import com.anthonyeden.lib.resource.ResourceException;
import com.anthonyeden.lib.resource.AbstractResourceLoader;

/** Implementation of the ResourceLoader interface which loads data from
    the servlet context.  This ResourceLoader does not monitor the resource.
    
    @author Anthony Eden
*/

public class ServletContextResourceLoader extends AbstractResourceLoader{
    
    private static final Log log = 
        LogFactory.getLog(ServletContextResourceLoader.class);
        
    private ServletContext servletContext;
    
    /** Get the ServletContext.
    
        @return The ServletContext
    */
        
    public ServletContext getServletContext(){
        return servletContext;
    }
    
    /** Set the ServletContext.
    
        @param servletContext The ServletContext
    */
    
    public void setServletContext(ServletContext servletContext){
        this.servletContext = servletContext;
    }
    
    /** Load the resource specified by the given path.  This ResourceLoader
        does not monitor the resource even if the value of monitor is true.
        
        @param path The path
        @param handler The ResourceRecipient callback
        @param monitor Ignored by this resource loader
        @throws ResourceException
    */

    public void loadResource(String path, ResourceRecipient handler, 
    boolean monitor) throws ResourceException{
        try{
            if(log.isDebugEnabled())
                log.debug("Resource path: " + path);
            handler.load(servletContext.getResourceAsStream(path));
        } catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(e);
        }
    }

}
