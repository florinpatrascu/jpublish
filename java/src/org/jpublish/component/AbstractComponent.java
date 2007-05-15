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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.Repository;
import org.jpublish.SiteContext;
import org.jpublish.JPublishContext;
import org.jpublish.JPublishComponent;
import org.jpublish.view.ViewRenderer;

/** Abstract base implementation of the JPublishComponent interface.
    Component implementations can extend this base class to reduce the
    amount of work necessary to develop components.
    
    @author Anthony Eden
    @since 2.0
*/

public abstract class AbstractComponent implements JPublishComponent{
    
    private static final Log log = LogFactory.getLog(AbstractComponent.class);
    
    protected String name;
    protected String description;
    protected Map properties;
    
    protected Repository viewRepository;
    protected ViewRenderer viewRenderer;
    
    protected SiteContext siteContext;
    
    /** Construct a new AbstractComponent. */
    
    protected AbstractComponent(){
        this.properties = new HashMap();
    }
    
    /** Get the component's name.
    
        @return The name
    */
    
    public String getName(){
        return name;
    }
    
    /** Get a description of the component.
    
        @return The component description
    */
    
    public String getDescription(){
        return description;
    }
    
    /** Get a Map of component properties.
    
        @return A Map of properties
    */
    
    public Map getProperties(){
        return properties;
    }
    
    /** Get the SiteContext.
    
        @return The SiteContext
    */
    
    public SiteContext getSiteContext(){
        return siteContext;
    }
    
    /** Set the SiteContext.
    
        @param siteContext The SiteContext
    */
    
    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Get the component's view repository.
    
        @return The view repository
    */
    
    public Repository getViewRepository(){
        return viewRepository;
    }
    
    /** Set the component's view repository.
    
        @param viewRepository The new view repository
    */
    
    public void setViewRepository(Repository viewRepository){
        this.viewRepository = viewRepository;
    }
    
    /** Get the component's view renderer.  This method may return
        null which indicates that the component should use the default
        view renderer specified in the SiteContext.
    
        @return The view renderer
    */
    
    public ViewRenderer getViewRenderer(){
        return viewRenderer;
    }
    
    /** Set the component's view renderer.  Set the value to null
        to indicate that the component should use the default
        view renderer specified in the SiteContext.
    
        @param viewRenderer The new view renderer
    */
    
    public void setViewRenderer(ViewRenderer viewRenderer){
        this.viewRenderer = viewRenderer;
    }
    
    /** Load the component's configuration data.  Implementations should
        override this method if they require configuration.  If an
        implementation does override this method the implementation
        should call <code>super.loadConfiguration(configuration)</code>
        first.
    
        @param configuration The configuration data
        @throws ConfigurationException
    */
    
    public void loadConfiguration(Configuration configuration)
    throws ConfigurationException{
        // Load the ViewRenderer for this component
        Configuration viewRendererConfiguration = 
            configuration.getChild("view-renderer");
        if(viewRendererConfiguration != null){
            String viewRendererClass = 
                viewRendererConfiguration.getAttribute("classname");
             
            try{
                viewRenderer = (ViewRenderer)ClassUtilities.loadClass(
                    viewRendererClass).newInstance();
                viewRenderer.setSiteContext(siteContext);
            } catch(Exception e){
                throw new ConfigurationException(
                    "Error loading view renderer: " + e.getMessage(), e);
            }
            viewRenderer.loadConfiguration(viewRendererConfiguration);
            try{
                viewRenderer.init();
            } catch(Exception e){
                throw new ConfigurationException(
                    "Error initializing view renderer: " + e.getMessage(), e);
            }
        }
        
        if(viewRenderer == null){
            viewRenderer = siteContext.getViewRenderer();
        }
        
        // Select the repository to use
        Configuration repositoryConfiguration = 
            configuration.getChild("repository");
        if(repositoryConfiguration != null){
            String name = repositoryConfiguration.getAttribute("name");
            if(name == null){
                throw new ConfigurationException(
                    "Repository name attribute required");
            } else {
                if(log.isDebugEnabled())
                    log.debug("Using repository name: " + name);
                viewRepository = siteContext.getRepository(name);
            }
        } else {
            log.error("Repository configuration element not found");
        }
        
        if(viewRepository == null){
            throw new ConfigurationException(
                "View repository must be specified for component " + getName());
        }
    }
    
    /** Internal method which can be used by subclasses to pass the view through
        the component's view renderer.
        
        @param text The raw view
        @param context The context
        @return The merged view
    */
    
    protected String renderView(String text, String path, 
    JPublishContext context) throws Exception{
        ViewRenderer viewRenderer = getViewRenderer();
        StringWriter out = new StringWriter();
        viewRenderer.render(context, path, new StringReader(text), out);
        return out.toString();
    }
    
}
