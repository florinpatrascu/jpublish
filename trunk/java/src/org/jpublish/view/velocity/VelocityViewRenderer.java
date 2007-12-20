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

package org.jpublish.view.velocity;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.util.IOUtilities;
import com.atlassian.util.profiling.UtilTimerStack;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;
import org.jpublish.util.CharacterEncodingMap;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Properties;

/**
 * ViewRenderer which uses the Velocity template engine from the Apache
 * Jakarta group to render content.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 2.0
 */

public class VelocityViewRenderer implements ViewRenderer {

    private static final Log log = LogFactory.getLog(VelocityViewRenderer.class);
    private static final String DEFAULT_PROPERTIES_PATH = "WEB-INF/velocity.properties";

    // Here are the names of Velocity 1.2 properties that can contains paths.
    private static final String[] velocityKeys = {
            "runtime.log", "file.resource.loader.path", "velocimacro.library"
    };

    private SiteContext siteContext;
    private final VelocityEngine velocityEngine = new VelocityEngine();
    private Properties velocityProperties = new Properties();

    private boolean resourceCacheEnabled = false;
    private int resourceCacheInterval = 2;

    /**
     * Set the SiteContext.
     *
     * @param siteContext The SiteContext
     */

    public void setSiteContext(SiteContext siteContext) {
        log.debug("setSiteContext()");
        this.siteContext = siteContext;
    }

    public boolean isResourceCacheEnabled() {
        return resourceCacheEnabled;
    }

    public void setResourceCacheEnabled(boolean resourceCacheEnabled) {
        if (log.isDebugEnabled())
            log.debug("Resource cache enabled: " + resourceCacheEnabled);
        this.resourceCacheEnabled = resourceCacheEnabled;
    }

    public void setResourceCacheEnabled(String resourceCacheEnabled) {
        setResourceCacheEnabled("true".equals(resourceCacheEnabled));
    }

    public int getResourceCacheInterval() {
        return resourceCacheInterval;
    }

    public void setResourceCacheInterval(int resourceCacheInterval) {
        if (log.isDebugEnabled())
            log.debug("Resource cache interval: " + resourceCacheInterval);
        this.resourceCacheInterval = resourceCacheInterval;
    }

    public void setResourceCacheInterval(String resourceCacheInterval) {
        if (resourceCacheInterval != null) {
            setResourceCacheInterval(Integer.parseInt(resourceCacheInterval));
        }
    }

    /**
     * Initialize the ViewRenderer.
     *
     * @throws Exception Any Exception
     */

    public void init() throws Exception {
        log.debug("init()");

        // it may be necessary to put caching support here, in which case
        // the cache parameters should be specified in the view config.

        ExtendedProperties eprops = new ExtendedProperties();
        eprops.putAll(velocityProperties);
        eprops.addProperty(RuntimeConstants.RESOURCE_LOADER, "jpublish");

        eprops.setProperty("jpublish.resource.loader.description", "JPublish internal resource loader.");
        eprops.setProperty("jpublish.resource.loader.class", "org.jpublish.view.velocity.JPublishResourceLoader");
        eprops.setProperty("jpublish.resource.loader.siteContext", siteContext);

        if (resourceCacheEnabled) {
            eprops.setProperty("jpublish.resource.loader.cache", "true");
            eprops.setProperty("jpublish.resource.loader.modificationCheckInterval",
                    Integer.toString(getResourceCacheInterval()));
        }

        // Apply properties to VelocityEngine.
        velocityEngine.setExtendedProperties(eprops);
        try {
            velocityEngine.init();
            velocityEngine.setApplicationAttribute(ServletContext.class.getName(), siteContext.getServletContext());
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (VelocityException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            log.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
            throw new VelocityException(ex.getMessage());
        }
        log.info("Resource loader: " + velocityEngine.getProperty(VelocityEngine.RESOURCE_LOADER));

    }

    /**
     * Render the view.
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The Reader to read view template from
     * @param out     The Writer to write the rendered view
     * @throws IOException
     * @throws ViewRenderException
     */

    public void render(JPublishContext context, String path, Reader in, Writer out)
            throws IOException, ViewRenderException {

        CharacterEncodingMap characterEncodingMap = siteContext.getCharacterEncodingManager().getMap(path);
        String encoding = characterEncodingMap.getPageEncoding();

        if (log.isDebugEnabled()) {
            log.debug("render(" + path + ")");
            log.debug("Character encoding: " + encoding);
        }

        try {
            UtilTimerStack.push(" ==> /" + path);
            VelocityViewContext viewContext = new VelocityViewContext(context);
            if (context.get("evaluateVelocityTemplates") != null)
                //Florin 15 Feb 2005
                velocityEngine.evaluate(viewContext, out, path, in);
            else
                velocityEngine.mergeTemplate(path, encoding, viewContext, out);

        } catch (IOException e) {
            log.error(path + ", IO exception: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error(path + ", rendering exception: " + e.getMessage());
            throw new ViewRenderException(e);
        } finally {
            UtilTimerStack.pop(" ==> /" + path);
        }
    }

    /**
     * Render the view.
     *
     * @param context The JPublishContext
     * @param path    The path to the template
     * @param in      The InputStream to read view template from
     * @param out     The OutputStream to write the rendered view
     * @throws IOException
     * @throws ViewRenderException
     */

    public void render(JPublishContext context, String path, InputStream in, OutputStream out)
            throws IOException, ViewRenderException {

        render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));
    }

    /**
     * Load the configuration for the view.
     *
     * @param configuration The configuration object
     */

    public void loadConfiguration(Configuration configuration) throws ConfigurationException {

        setResourceCacheEnabled(configuration.getChildValue("resource-cache-enabled"));
        setResourceCacheInterval(configuration.getChildValue("resource-cache-interval"));

        try {
            String propertiesPath = configuration.getChildValue("velocity-properties");

            if (propertiesPath == null) {
                propertiesPath = DEFAULT_PROPERTIES_PATH;
            }

            File propertiesFile = new File(propertiesPath);
            if (!propertiesFile.exists()) {
                if (!propertiesFile.isAbsolute()) {
                    if (log.isDebugEnabled())
                        log.debug("Velocity properties path is not absolute.  " +
                                "Prepending root: " + siteContext.getRoot());

                    propertiesFile = new File(siteContext.getRoot(), propertiesFile.getPath());
                }
            }


            if (propertiesFile.exists()) {

                if (log.isDebugEnabled())
                    log.debug("Velocity properties file: " + propertiesFile);

                InputStream in = null;
                try {
                    in = new FileInputStream(propertiesFile);
                    velocityProperties.load(in);

                    Configuration config = configuration.getChild("velocity-properties");
                    if (config != null) {
                        String relativePaths = config.getAttribute("relative-paths");

                        if ("true".equalsIgnoreCase(relativePaths)) {

                            for (int i = 0; i < velocityKeys.length; i++) {
                                String value = velocityProperties.getProperty(velocityKeys[i]);

                                if (value != null && value.trim().length() > 0) {
                                    File file = new File(value);

                                    if (!file.isAbsolute()) {
                                        file = new File(siteContext.getRoot(), file.getPath());
                                        velocityProperties.setProperty(velocityKeys[i], file.toString());
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    IOUtilities.close(in);
                }
            } else {
                log.warn("No velocity.properties file found");
            }
        } catch (IOException e) {
            throw new ConfigurationException("IO error: " + e, e);
        }
    }

}
