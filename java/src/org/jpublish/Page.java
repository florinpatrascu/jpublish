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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.page.PageInstance;
import org.jpublish.page.PageProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class represents the current Page.  A new Page object is created
 * for each request.  Each Page object actually wraps a shared PageInstance
 * object.  Methods which modify the Page's state are only effective for the
 * current request.  To change the shared PageInstance state use the
 * <code>getPageInstance()</code> method to retrieve the PageInstance object
 * and make changes.
 *
 * @author Anthony Eden
 */

public class Page {

    private static final Log log = LogFactory.getLog(Page.class);

    private PageInstance pageInstance;

    private Map properties;
    private String templateName;
    private Locale locale = Locale.getDefault();

    /**
     * Construct a new Page.
     *
     * @param pageInstance The wrapped PageInstance
     */

    public Page(PageInstance pageInstance) {
        this.pageInstance = pageInstance;

        this.properties = new HashMap();
        this.locale = Locale.getDefault();
    }

    /**
     * Get the request path.
     *
     * @return The request path
     */

    public String getPath() {
        return pageInstance.getPath();
    }

    /**
     * Get the page name.
     *
     * @return The page name
     */

    public String getPageName() {
        return pageInstance.getPageName();
    }

    /**
     * Get the page type.
     *
     * @return The page type
     */

    public String getPageType() {
        return pageInstance.getPageType();
    }

    /**
     * Get the wrapped PageInstance.
     *
     * @return The PageInstance
     */

    public PageInstance getPageInstance() {
        return pageInstance;
    }

    /**
     * Return the page title.  Initially the page title is extracted from
     * the page's definition document, however it can be set programtically
     * at runtime.
     *
     * <p>This method is deprecated.  Use getProperty("title") instead and
     * include a property named title in the page configuration file.  The
     * old &lt;title&gt; element and this method will be removed for the
     * 1.0 release.
     *
     * @return The page title
     * @deprecated Use getProperty("title") instead
     */

    public String getTitle() {
        return getProperty("title");
    }

    /**
     * Set the title.  This will temporarily alter the page's title.
     *
     * <p>This method is deprecated.  The old &lt;title&gt; element
     * and this method will be removed for the 1.0 release.
     *
     * @param title The page title
     * @deprecated
     */

    public void setTitle(String title) {
        setProperty("title", title);
    }

    /**
     * Get the full template file name, with the .suffix attached.
     *
     * @return The full template name
     */

    public String getFullTemplateName() {
        return getTemplateName() + "." + getPageType();
    }

    /**
     * Get the template name.  If the template name is not specified in the
     * page configuration then the default template as specified in the
     * SiteContext will be used.
     *
     * @return The template name
     */

    public String getTemplateName() {
        if (templateName == null) {
            templateName = pageInstance.getTemplateName();
        }
        return templateName;
    }

    /**
     * Set the template name.  Invoking this method with a null value will
     * reset the template to the default template as specified in the
     * SiteContext.
     *
     * @param templateName The new template name or null to reset
     */

    public void setTemplateName(String templateName) {
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
        return pageInstance.getPageActions();
    }

    /**
     * Get the named page property using the default Locale.  If the
     * property is not found then return null.
     *
     * @param name The property name
     * @return The value or null
     */

    public String getProperty(String name) {
        return getProperty(name, getLocale());
    }

    /**
     * Get the Locale-specific value for the given named property.  If
     * the property is not found then return null.  This method will try to find
     * the most suitable locale by searching the property values in the
     * following manner:
     *
     * <p>
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
            log.debug("getProperty(" + name + "," + locale + ")");

        PageProperty pageProperty = (PageProperty) properties.get(name);
        if (pageProperty == null) {
            log.debug("Looking for property in PageInstance");
            String value = pageInstance.getProperty(name, locale);
            if (value != null) {
                log.debug("Found property in page instance");
                setProperty(name, value, locale);
                return value;
            }
        }

        if (pageProperty == null) {
            return null;
        } else {
            return pageProperty.getValue(locale);
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
        return pageInstance.executeActions(context);
    }

    /**
     * Get the current page's locale.
     *
     * @return The Locale
     */

    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the current page's locale.  This locale is used when retrieving
     * page properties.
     *
     * @param locale
     */

    public void setLocale(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.locale = locale;
    }

    /**
     * Set the property value.
     *
     * @param name  The property name
     * @param value The property value
     */

    public void setProperty(String name, String value) {
        setProperty(name, value, getLocale());
    }

    /**
     * Set the property value.
     *
     * @param name   The property name
     * @param value  The property value
     * @param locale The Locale
     */

    public void setProperty(String name, String value, Locale locale) {
//        if(log.isDebugEnabled())
//            log.debug("setProperty(" + name + "," + value + "," + locale + ")");
//
        PageProperty property = (PageProperty) properties.get(name);
        if (property == null) {
            // named property not in property map
            property = new PageProperty(name);
            properties.put(name, property);
        }
        property.setValue(value, locale);
    }

}
