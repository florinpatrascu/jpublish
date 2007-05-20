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

package org.jpublish;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.jpublish.view.ViewRenderer;

import java.util.Map;

/**
 * Standard interface which must be implemented by all JPublish components.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (updated: Sunday; May 20, 2007)
 */

public interface JPublishComponent {

    /**
     * Get the component's name.
     *
     * @return The name
     */

    public String getName();

    /**
     * Get a description of the component.
     *
     * @return The component description
     */

    public String getDescription();

    /**
     * Get a Map of component properties.
     *
     * @return A Map of properties
     */

    public Map getProperties();

    /**
     * Get the SiteContext.
     *
     * @return The SiteContext
     */

    public SiteContext getSiteContext();

    /**
     * Set the SiteContext.
     *
     * @param siteContext The SiteContext
     */

    public void setSiteContext(SiteContext siteContext);

    /**
     * Get the component's view repository.
     *
     * @return The view repository
     */

    public Repository getViewRepository();

    /**
     * Set the component's view repository.
     *
     * @param viewRepository The new view repository
     */

    public void setViewRepository(Repository viewRepository);

    /**
     * Get the component's view renderer.  This method may return
     * null which indicates that the component should use the default
     * view renderer specified in the SiteContext.
     *
     * @return The view renderer
     */

    public ViewRenderer getViewRenderer();

    /**
     * Set the component's view renderer.  Set the value to null
     * to indicate that the component should use the default
     * view renderer specified in the SiteContext.
     *
     * @param viewRenderer The new view renderer
     */

    public void setViewRenderer(ViewRenderer viewRenderer);

    /**
     * Load the component's configuration data.
     *
     * @param configuration The configuration data
     * @throws ConfigurationException
     */

    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException;

    /**
     * Render the component for the specified path and return the result.
     *
     * @param path    The request path
     * @param context The context
     * @return The rendered component
     * @throws Exception
     */

    public String render(String path, JPublishContext context) throws Exception;

    /**
     * Process the text parameter and Render the component
     *
     * @param text    the string to process
     * @param context The context
     * @return The rendered component
     * @throws Exception any Exception
     */
    public String renderText(String text, JPublishContext context) throws Exception;


    /**
     * Process the View from at the given path and Render the component
     *
     * @param path    the View to process
     * @param context The context
     * @return The rendered component
     * @throws Exception any Exception
     */
    public String renderPath(String path, JPublishContext context) throws Exception;

}
