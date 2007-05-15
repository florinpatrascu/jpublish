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

package org.jpublish.component;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.SiteContext;
import org.jpublish.ComponentManager;
import org.jpublish.JPublishComponent;

/** Implementation of the ComponentManager interface which maintains
    a map of components in memory.
    
    @author Anthony Eden
    @since 2.0
*/

public class InMemoryComponentManager implements ComponentManager{
    
    private static final Log log = LogFactory.getLog(
        InMemoryComponentManager.class);
    
    private Map components = new HashMap();
    private SiteContext siteContext;
    
    /** Set the SiteContext.
    
        @param siteContext The site context
    */

    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Get a component.
    
        @param id The id of the component
        @return The ComponentInstance
        @throws Exception
    */
    
    public JPublishComponent getComponent(String id) throws Exception{
        return (JPublishComponent)components.get(id);
    }
    
    /** Return an Iterator of all components.
    
        @return An Iterator of Components
    */
    
    public Iterator getComponents(){
        return components.values().iterator();
    }
    
     /** Load the ComponentManager's configuration.
    
        @param configuration The Configuration object
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration) throws 
    ConfigurationException{
        Configuration componentsElement = configuration.getChild("components");
        Iterator componentElements = 
            componentsElement.getChildren("component").iterator();
        while(componentElements.hasNext()){
            try{
                Configuration componentElement = 
                    (Configuration)componentElements.next();
                
                String componentId = componentElement.getAttribute("id");
                String componentClassName = 
                    componentElement.getAttribute("classname");
                
                JPublishComponent comp = 
                    (JPublishComponent)ClassUtilities.loadClass(
                    componentClassName).newInstance();
                comp.setSiteContext(siteContext);
                
                log.info("Loaded component " + componentId + " [" + 
                    componentClassName + "]");
                    
                comp.loadConfiguration(componentElement);
                if(log.isDebugEnabled())
                    log.debug("Component " + componentId + 
                    " configuration loaded");
                
                components.put(componentId, comp);
            } catch(Exception e){
                log.error("Error loading component");
                e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }

}
