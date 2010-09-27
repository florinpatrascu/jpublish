/*
 * Copyright (c) 2010, Florin T.PATRASCU
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
 */

package org.jpublish.view.haml;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;
import org.jpublish.util.FileCopyUtils;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;
import org.jruby.RubyInstanceConfig;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewRenderer which renders haml templates using support from JRuby
 * <p/>
 * See: http://github.com/nex3/haml for more details about haml
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 1.0
 */

public class HamlViewRenderer implements ViewRenderer {
    protected static final Log log = LogFactory.getLog(HamlViewRenderer.class);

    private ScriptingContainer container;
    private Configuration scriptingContainerConfiguration;

    private String jrubyhome = "";  // will be addressed before the final version
    private String haml_rb = "require 'java'\n" +
            "require 'rubygems'\n" +
            "require 'date'\n" +
            "require 'haml'\n" +
            "Haml::Engine.new( haml_template).render.to_s";

    private JavaEmbedUtils.EvalUnit haml_rb_unit;

    private SiteContext siteContext;
    public static final String UTF_8 = "utf-8";
    public static final String $HAML_TEMPLATE_KEY = "$haml_template";
    public static final String $ = "$";
    public static final String $CONTEXT = $ + "context";
    private static final String JRUBY_CONTEXT_INSTANCE = "$jruby_context_instance";

    /**
     * Set the SiteContext.
     *
     * @param siteContext The SiteContext
     */

    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;
    }

    /**
     * Initialize the ViewRenderer.
     *
     * @throws Exception Any Exception
     */

    public void init() throws Exception {
    }

    /**
     * Render the haml template
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The Reader to read view template from
     * @param out     The Writer to write the rendered view
     * @throws java.io.IOException
     * @throws org.jpublish.view.ViewRenderException
     *
     */
    public void render(JPublishContext context, String path, Reader in,
                       Writer out) throws IOException, ViewRenderException {

        String template = FileCopyUtils.copyToString(in); //faster than processing the Reader inside the script
        // thread safe, right?? Promise??
        Object keys[] = context.getKeys(); // the keys must be Strings only

        // transfer the JPublish context to JRuby
        for (Object key : keys) {
            container.put($ + key, context.get((String) key));
        }

        container.put($CONTEXT, context); // pass our jpublish context too
        container.put($HAML_TEMPLATE_KEY, template); // put our template too

        try {
            FileCopyUtils.copy(haml_rb_unit.run().asJavaString(), out); //send it
            container.clear();
        } catch (EvalFailedException e) {
            FileCopyUtils.copy(String.format("[EvalFailedException] %s", e.getMessage()), out);
            e.printStackTrace(); //will be disabled in the final version
        }
    }

    /**
     * Render the view. note to self: I'll deprecate this method
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The InputStream to read view template from
     * @param out     The OutputStream to write the rendered view
     * @throws java.io.IOException
     * @throws org.jpublish.view.ViewRenderException
     *
     */
    public void render(JPublishContext context, String path, InputStream in,
                       OutputStream out) throws IOException, ViewRenderException {

        render(context, path, new InputStreamReader(in, UTF_8), new OutputStreamWriter(out, UTF_8));
    }

    /**
     * Load the Haml viewer configuration for the view.
     *
     * @param configuration The configuration object
     */
    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException {

        List<String> loadPaths = new ArrayList<String>();
        // SINGLETHREAD is not threadsafe: http://www.ruby-forum.com/topic/206056#new
        container = new ScriptingContainer(LocalContextScope.THREADSAFE);
        scriptingContainerConfiguration = configuration;

        RubyInstanceConfig config = container.getProvider().getRubyInstanceConfig();

        if (scriptingContainerConfiguration != null) {

            jrubyhome = scriptingContainerConfiguration.getChildValue("jruby_home");

            if (jrubyhome != null) {
                config.setJRubyHome(jrubyhome);
                loadPaths.add(jrubyhome);
            }

            String userHamlScript = scriptingContainerConfiguration.getChildValue("haml");
            if (userHamlScript != null) {
                haml_rb = userHamlScript; //just a bit of inversion of control
            }
        }

        haml_rb_unit = container.parse(haml_rb);

        log.info("haml enabled ... have fun!");
    }

}
