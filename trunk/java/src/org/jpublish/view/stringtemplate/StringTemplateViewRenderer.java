/*
 *
 * Copyright 2007 Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jpublish.view.stringtemplate;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.util.ClassUtilities;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.Repository;
import org.jpublish.SiteContext;
import org.jpublish.util.*;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple StringTemplate viewer
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Aug 18, 2007 1:56:15 PM)
 */
public class StringTemplateViewRenderer implements ViewRenderer {
    public static final Log log = LogFactory.getLog(StringTemplateViewRenderer.class);
    public static final String EMPTY_STRING = "";

    private SiteContext siteContext;
    private int refreshInterval = 0;
    private Class lexer;
    private boolean initialized;
    private Map stGroups = new HashMap();
    private static final String TEMPLATE_PROTOCOL_NAME = "template";
    private static final String DEFAULT_TEMPLATE_LEXER = "org.antlr.stringtemplate.language.DefaultTemplateLexer";

    /**
     * Set the SiteContext.
     *
     * @param siteContext The SiteContext
     */
    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;
    }

    /**
     * Initialize the StringTemplate ViewRenderer.
     *
     * @throws Exception Any Exception
     */
    public void init() throws Exception {

    }

    /**
     * Render the view.
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The Reader to read view template from
     * @param out     The Writer to write the rendered view
     * @throws java.io.IOException
     * @throws org.jpublish.view.ViewRenderException
     *
     */
    public void render(JPublishContext context, String path, Reader in, Writer out)
            throws ViewRenderException, IOException {

        CharacterEncodingMap characterEncodingMap = siteContext.getCharacterEncodingManager().getMap(path);
        String encoding = characterEncodingMap.getPageEncoding();

        if (!initialized) {
            preInit();
            initialized = true;
        }

        if (log.isDebugEnabled()) {
            log.debug("render(" + path + ")");
            log.debug("Character encoding: " + encoding);
        }

        Map model = new HashMap();
        Object[] keys = context.getKeys();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            model.put(key, context.get(key));
        }

        String stgName = TEMPLATE_PROTOCOL_NAME;
        String stPath = EMPTY_STRING;

        try {
            InternalURI uri = InternalURIParser.getInstance().parse(path);
            String protocol = uri.getProtocol();

            if (protocol.equalsIgnoreCase(TEMPLATE_PROTOCOL_NAME)) {
                // nothing for now
            } else if (protocol.equalsIgnoreCase("repository")) {
                stgName = ((RepositoryURI) uri).getRepositoryName();
            } else {
                throw new Exception("Protocol " + protocol + " not supported");
            }

            stPath = uri.getPath();
            int delim = stPath.indexOf(".");
            if (delim >= 0) {
                stPath = stPath.substring(0, delim);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringTemplateGroup stg = (StringTemplateGroup) stGroups.get(stgName);
        StringTemplate st = stg.getInstanceOf(stPath, model);
        FileCopyUtils.copy(st.toString(), out);
    }

    /**
     * Render the view.
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The InputStream to read view template from
     * @param out     The OutputStream to write the rendered view
     * @throws java.io.IOException
     * @throws org.jpublish.view.ViewRenderException
     *
     */
    public void render(JPublishContext context, String path, InputStream in, OutputStream out)
            throws IOException, ViewRenderException {
        render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));
    }

    /**
     * Load the configuration for the Stringtemplate Viewer.
     *
     * @param configuration The configuration object
     */
    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException {

        String lexerClassName = EMPTY_STRING;
        refreshInterval = Integer.parseInt(configuration.getChildValue("refresh-interval", "0"));

        if (configuration.getChild("lexer") == null) {
            lexerClassName = DEFAULT_TEMPLATE_LEXER;
        } else {
            lexerClassName = configuration.getChild("lexer").getAttribute("class", DEFAULT_TEMPLATE_LEXER);
        }

        try {
            lexer = ClassUtilities.loadClass(lexerClassName);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Stringtemplate Lexer class not found: " + lexerClassName);
        }
    }

    public boolean isCacheEnabled() {
        return refreshInterval > 0;
    }

    /**
     * this method will create one StringTemplateGroup for every JPublish repository and it will also
     * create one for the JPublish template repository.
     */
    public void preInit() {
        stGroups.put(TEMPLATE_PROTOCOL_NAME,
                new StringTemplateGroup(
                        TEMPLATE_PROTOCOL_NAME,
                        siteContext.getRealTemplateRoot().getAbsolutePath()));

        List repositories = siteContext.getRepositories();
        log.info("StringTemplate initialization begins ...");
        for (int i = 0; i < repositories.size(); i++) {
            Repository repository = (Repository) repositories.get(i);
            StringTemplateGroup stg = null;
            try {
                stg = new StringTemplateGroup(repository.getName(),
                        repository.pathToFile(EMPTY_STRING).getAbsolutePath(), lexer);
                stg.setRefreshInterval(refreshInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stGroups.put(stg.getName(), stg);
        }

        log.info("Available template groups:");
        Iterator it = stGroups.values().iterator();
        while (it.hasNext()) {
            StringTemplateGroup stg = (StringTemplateGroup) it.next();
            log.info("- group: '" + stg.getName() +
                    "',  location: " + stg.getRootDir());
        }

        log.info(" Lexer used: " + lexer);
        log.info(" templates cache refresh interval: " + refreshInterval);
        log.info("StringTemplate Viewer is now available.");

    }
}
