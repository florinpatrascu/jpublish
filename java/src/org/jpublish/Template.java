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
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.action.Action;
import org.jpublish.action.ActionWrapper;
import org.jpublish.util.CharacterEncodingMap;
import org.jpublish.util.PathUtilities;
import org.jpublish.view.ViewRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Template class represents a reusable design template which is applied
 * to one or more pages.
 *
 * @author Anthony Eden
 * @since 1.1
 */

public class Template {

    private static final Log log = LogFactory.getLog(Template.class);

    private SiteContext siteContext;
    private String path;
    private String templateText;
    private long lastModified = -1;
    private List templateActions = new ArrayList();

    /**
     * Construct a new Template.
     *
     * @param siteContext The current SiteContext
     */

    public Template(SiteContext siteContext, String path) {
        this.siteContext = siteContext;
        this.path = path;
    }

    /**
     * Get the path to the template.
     *
     * @return The path
     */

    public String getPath() {
        return path;
    }

    /**
     * Get the template text.
     *
     * @return The template text
     */

    public String getText() {
        return templateText;
    }

    /**
     * Set the template text.
     *
     * @param templateText
     */

    public void setText(String templateText) {
        this.templateText = templateText;
    }

    /**
     * Get the last modified time of this template.
     *
     * @return The last modified time
     */

    public long getLastModified() {
        return lastModified;
    }

    /**
     * Set the last modified time of this template.
     *
     * @param lastModified The new last modified time
     */

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Get a List of actions associated with this template.
     *
     * @return A list of actions
     * @since 2.0
     */

    public List getTemplateActions() {
        return templateActions;
    }

    /**
     * Execute any template actions.
     *
     * @param context The context
     * @return The redirect path or null
     * @throws Exception Any exception
     * @since 2.0
     */

    public String executeActions(JPublishContext context) throws Exception {
        Iterator templateActions = getTemplateActions().iterator();
        while (templateActions.hasNext()) {
            ((ActionWrapper) templateActions.next()).execute(context);

            String redirect = (String) context.get("redirect");
            if (redirect != null) {
                return redirect;
            }
        }
        return null;
    }

    /**
     * Merge the given page with the template.
     *
     * @param context The current context
     * @param page    The page
     * @param out     The OutputStream
     * @throws TemplateMergeException Exception while merging the template
     *                                and the page.
     */

    public void merge(JPublishContext context, Page page, Writer out)
            throws TemplateMergeException {
        Reader reader = null;
        try {
            executeActions(context);

            CharacterEncodingMap characterEncodingMap = (CharacterEncodingMap) context.get("characterEncodingMap");
            String templateEncoding = characterEncodingMap.getTemplateEncoding();

            String path = PathUtilities.makeTemplateURI(getPath());
            ViewRenderer renderer = siteContext.getViewRenderer();
            reader = getReader(templateEncoding);
            renderer.render(context, path, reader, out);

            if (log.isDebugEnabled()) {
                log.debug("Merge complete.");
            }

        } catch (Exception e) {
            throw new TemplateMergeException(e.getMessage(), e);
        } finally {
            IOUtilities.close(reader);
        }
    }

    /**
     * Load the template's additional configuration information from
     * the specified InputStream.
     *
     * @param in The InputStream
     * @throws ConfigurationException Any configuration exception
     * @since 2.0
     */

    public void loadConfiguration(String id, File in) throws ConfigurationException {
        loadConfiguration(new XMLConfiguration(id, in));
    }

    /**
     * Load the template's configuration information.
     *
     * @param configuration The Configuration object
     * @throws ConfigurationException Any configuration exception
     * @since 2.0
     */

    public void loadConfiguration(Configuration configuration) throws ConfigurationException {
        // load template actions
        if (log.isDebugEnabled())
            log.debug("Looping through template-action elements.");

        Iterator actionElements = configuration.getChildren("template-action").iterator();
        while (actionElements.hasNext()) {
            Configuration actionElement = (Configuration) actionElements.next();
            String name = actionElement.getAttribute("name");

            if (name == null) {
                name = actionElement.getValue();
            }

            if (name == null) {
                throw new ConfigurationException(
                        "Error configuring page-action.");
            }

            Action action = siteContext.getActionManager().findAction(name);
            if (action == null) {
                throw new ConfigurationException(
                        "Action " + name + " not defined");
            }

            templateActions.add(new ActionWrapper(action, actionElement));
        }
    }

    /**
     * Get a Reader which returns the contents of this template to
     * be used for rendering.
     *
     * @return The Reader
     */

    protected Reader getReader(String templateEncoding) {
        String result="";
        try {
            result = new String(templateText.getBytes(), templateEncoding);
        } catch (UnsupportedEncodingException e) {
            log.info("Unsupported encoding, using default encoding");
            result = templateText;
        }
        return new StringReader(result);
    }

}
