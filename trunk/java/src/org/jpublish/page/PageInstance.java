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
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;
import org.jpublish.action.Action;
import org.jpublish.action.ActionWrapper;

import java.io.InputStream;
import java.util.*;

/**
 * A representation of a single web page.  A page is defined by an XML
 * document in the pages directory.  Each page has a template associated
 * with the page and can have 0 or more actions attached to the page.
 * <p/>
 * <p>Actions attached to a page will be triggered each time the page
 * is requested.  Actions will be triggered in the order that they are
 * listed within the Page's configuration.</p>
 * <p/>
 * <p>There should only be a single PageInstance in memory for each path.
 * Each PageInstance is actually wrapped in a Page class which provides
 * request-specific features.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class PageInstance {

    private static Log log = LogFactory.getLog(PageInstance.class);

    private SiteContext siteContext;
    private List pageActions;
    private String path;
    private String pageName;
    private String pageType;
    private String templateName;
    private Map properties;
    private Configuration configuration = null;

    /**
     * Construct a new Page for the given path.  The name of the page is
     * the last part of the path (i.e. the file component) without the
     * dot ending.  The page type is the dot-ending.
     *
     * @param siteContext The SiteContext
     * @param path        The request path
     * @param pageName    The name of the page
     * @param pageType    The page type
     */

    public PageInstance(SiteContext siteContext, String path, String pageName, String pageType) {
        this.siteContext = siteContext;
        this.path = path;
        this.pageName = pageName;
        this.pageType = pageType;
        this.pageActions = new ArrayList();
        this.templateName = siteContext.getDefaultTemplate();
        this.properties = new HashMap();
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
     * Get the page name.
     *
     * @return The page name
     */

    public String getPageName() {
        return pageName;
    }

    /**
     * Get the page type.
     *
     * @return The page type
     */

    public String getPageType() {
        return pageType;
    }

    /**
     * Return the page title.  Initially the page title is extracted from
     * the page's definition document, however it can be set programtically
     * at runtime.
     * <p/>
     * <p>This method is deprecated.  Use getProperty("title") instead and
     * include a property named title in the page configuration file.  The
     * old &lt;title&gt; element and this method will be removed for the
     * 1.0 release.
     *
     * @return The page title
     * @deprecated Use getProperty("title") instead
     */

    public String getTitle() {
        String title = getProperty("Title");
        if (title == null) {
            title = getProperty("title");
        }
        return title;
    }

    /**
     * Set the title.  This will temporarily alter the page's title.
     * <p/>
     * <p>This method is deprecated.  The old &lt;title&gt; element
     * and this method will be removed for the 1.0 release.
     *
     * @param title The page title
     * @deprecated
     */

    public void setTitle(String title) {
        if (title != null) {
            if (log.isDebugEnabled())
                log.debug("setTitle(" + title + ")");
            setProperty("title", title, null);
        }
    }

    /**
     * Get the full template file name, with the .suffix attached.
     *
     * @return The full template name
     */

    public String getFullTemplateName() {
        return templateName + "." + pageType;
    }

    /**
     * Get the template name.  If the template name is not specified in the
     * page configuration then the default template as specified in the
     * SiteContext will be used.
     *
     * @return The template name
     */

    public String getTemplateName() {
        return templateName;
    }

    /**
     * Set the template name.  Invoking this method with a null value will
     * reset the template to the default template as specified in the
     * SiteContext.
     *
     * @param templateName The new template name or null to reset
     */

    public synchronized void setTemplateName(String templateName) {
        if (log.isDebugEnabled())
            log.debug("setTemplateName(" + templateName + ")");
        if (templateName == null) {
            templateName = siteContext.getDefaultTemplate();
            if (log.isDebugEnabled())
                log.debug("Using default template: " + templateName);
        }
        this.templateName = templateName;
    }

    /**
     * Get a List of page actions.  To add an action to the page just add
     * the action to this List.  Page actions are triggered each time the
     * page is requested.
     *
     * @return A List of page actions
     */

    public List getPageActions() {
        return pageActions;
    }

    /**
     * Get the named page property using the default Locale.  If the
     * property is not found then return null.
     *
     * @param name The property name
     * @return The value or null
     */

    public String getProperty(String name) {
        return getProperty(name, Locale.getDefault());
    }

    /**
     * Get the Locale-specific value for the given named property.  If
     * the property is not found then return null.  This method will try to
     * find the most suitable locale by searching the property values in the
     * following manner:
     * <p/>
     * <p/>
     * language + "_" + country + "_" + variant<br>
     * language + "_" + country<br>
     * langauge<br>
     * ""
     * </p>
     *
     * @param name   The property name
     * @param locale The locale
     * @return The value
     */

    public String getProperty(String name, Locale locale) {
        if (log.isDebugEnabled())
            log.debug("Get property [name=" + name + ",locale=" + locale + "]");
        PageProperty property = (PageProperty) properties.get(name);
        if (property != null) {
            return property.getValue(locale);
        } else {
            return null;
        }
    }

    /**
     * Get the named property.  This method is equivilent to the
     * <code>getProperty(name)</code> method.  This method is provided
     * as a convenience to view code.
     *
     * @param name The property name
     * @return The value
     */

    public String get(String name) {
        return getProperty(name);
    }

    /**
     * Execute the page actions using the given context.
     *
     * @param context The current context
     * @return A redirection value or null if there is no redirection
     * @throws Exception Any Exception which occurs while executing the action
     */

    public String executeActions(JPublishContext context) throws Exception {
        Iterator pageActions = getPageActions().iterator();
        while (pageActions.hasNext()) {
            ((ActionWrapper) pageActions.next()).execute(context);

            String redirect = (String) context.get("redirect");
            if (redirect != null) {
                return redirect;
            }
        }
        return null;
    }

    /**
     * Load the page configuration from the page's XML stream.
     *
     * @param in The InputStream
     * @throws Exception Any exception
     */

    public synchronized void load(InputStream in) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Loading page: " + path);

        Configuration configuration = new XMLConfiguration(in);
        loadConfiguration(configuration);
    }

    /**
     * Load the page configuration from the given Configuration object.
     *
     * @param configuration The Configuration object
     * @throws ConfigurationException
     */

    public synchronized void loadConfiguration(Configuration configuration) throws ConfigurationException {

        if (log.isDebugEnabled())
            log.debug("Loading page: " + path);

        this.configuration = configuration;
        setTitle(configuration.getChildValue("title"));
        setTemplateName(configuration.getChildValue("template"));

        // load page actions
        if (log.isDebugEnabled())
            log.debug("Looping through page-action elements.");

        Iterator pageActionElements = configuration.getChildren("page-action").iterator();
        while (pageActionElements.hasNext()) {
            Configuration pageActionElement = (Configuration) pageActionElements.next();
            String name = pageActionElement.getAttribute("name");

            if (name == null) {
                throw new ConfigurationException("Error configuring page-action.");
            }

            Action action = siteContext.getActionManager().findAction(name);
            if (action == null) {
                throw new ConfigurationException("Action " + name + " not defined");
            }

            pageActions.add(new ActionWrapper(action, pageActionElement));
        }

        // load page properties
        if (log.isDebugEnabled())
            log.debug("Loading page properties");

        Iterator propertyElements = configuration.getChildren("property").iterator();
        String value = null;

        while (propertyElements.hasNext()) {
            Configuration propertyElement = (Configuration) propertyElements.next();
            value = propertyElement.getAttribute("value");
            setProperty(
                    propertyElement.getAttribute("name"),
                    value == null ? propertyElement.getValue() : value, propertyElement.getAttribute("locale"));
        }
    }

    /**
     * Set the property value.
     *
     * @param name   The property name
     * @param value  The value
     * @param locale The locale String or null
     */

    public void setProperty(String name, String value, String locale) {
        if (log.isDebugEnabled())
            log.debug("setProperty() [name=" + name + ",value=" + value +
                    ",locale=" + locale);
        PageProperty property = (PageProperty) properties.get(name);
        if (property == null) {
            // named property not in property map
            property = new PageProperty(name);
            properties.put(name, property);
        }
        property.setValue(value, locale);
    }

    /**
     * expose a readOnly view containing the current page instance properties
     *
     * @return a readonly view to properties [Florin]
     */
    public Map getProperties() {
        Map readOnlyProperties = null;

        if (properties != null && !properties.isEmpty())
            readOnlyProperties = Collections.unmodifiableMap(properties);

        return readOnlyProperties;
    }

    /**
     * offers access to the page configuration
     *
     * @return the page configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

}
