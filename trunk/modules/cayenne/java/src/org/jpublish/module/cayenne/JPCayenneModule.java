/*
 * Copyright (c) 2007 the original author or authors.
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

package org.jpublish.module.cayenne;

import com.anthonyeden.lib.config.Configuration;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.util.ResourceLocator;
import org.apache.cayenne.util.WebApplicationResourceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Cayenne module
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 22, 2007 10:50:57 AM)
 */
public class JPCayenneModule implements JPublishModule {
    public static final String JPCAYENNE_SERVICE_NAME = "jpCayenneService";
    public static final String JPCAYENNE_DATA_CONTEXT = "jpCayenne_DC";
    public static final String JPCAYENNE_OBJECT_CONTEXT = "jpCayenne_OC";
    public static final String JPCAYENNE_DATA_OBJECT_UTILS = "DataObjectUtils"; // I am not sure we need this one
    public static final String JPCAYENNE__UTILS = "jpCayenneUtils";

    private static final Log log = LogFactory.getLog(JPCayenneModule.class);

    private static final String NAME = "JPCayenne Module";
    private static final String VERSION = "v0.1b";
    private static final String DESCRIPTION = "Cayenne support for JPublish.";

    private SiteContext site;
    private Map actions = new HashMap(5);

    private Map jpCayenneUtils = new HashMap(5);
    /**
     * Automatically rollback any changes to the DataContext at the end of
     * each request, the default value is true.
     * <p/>
     * This option is only useful for sessionScope DataObjects.
     */
    private boolean autoRollback = true;

    /**
     * Maintain user HttpSession scope DataContext object, the default value is
     * true. If sessionScope is false then a new DataContext object will be
     * created for each request.
     */
    private boolean sessionScope = true;

    /**
     * Create DataContext objects using the shared cache.
     */
    private boolean sharedCache = true;

    private boolean debugEnabled = false;

    protected org.apache.cayenne.conf.Configuration configuration;
    protected DataSource dataSource;


    /**
     * Initialize the module.  This corresponds with the
     * Servlet.init() method, except that it includes a reference
     * to the JPublish SiteContext.
     *
     * @param site   The SiteContext
     * @param config The configuration object
     * @throws Exception
     */
    public void init(SiteContext site, Configuration config) throws Exception {
        log.info(NAME + " starting for: " + site.getServletContext().getServletContextName());
        this.site = site;

        String cayenneConfigPath = config.getChildValue("cayenne-config-path", "WEB-INF");
        log.info("Initializing the Cayenne framework from: " + cayenneConfigPath);

        // create new shared configuration
        DefaultConfiguration conf = new DefaultConfiguration(
                org.apache.cayenne.conf.Configuration.DEFAULT_DOMAIN_FILE,
                createLocator(site.getServletContext(), cayenneConfigPath));
        org.apache.cayenne.conf.Configuration.initializeSharedConfiguration(conf);


        String value = config.getChildValue("auto-rollback", "true");
        autoRollback = "true".equalsIgnoreCase(value);

        value = config.getChildValue("session-scope", "true");
        sessionScope = "true".equalsIgnoreCase(value);

        value = config.getChildValue("shared-cache", "true");
        sharedCache = "true".equalsIgnoreCase(value);

        value = config.getChildValue("debug", "false");
        debugEnabled = "true".equalsIgnoreCase(value);

        String msg = "Cayenne DataContext initialized with: auto-rollback="
                + autoRollback + ", session-scope=" + sessionScope
                + ", shared-cache=" + sharedCache;

        log.info(msg);

        //jpCayenneUtils.put(JPCAYENNE_DATA_OBJECT_UTILS,
        //        ClassUtilities.loadClass("org.apache.cayenne.DataObjectUtils"));

        log.info("Mapping Cayenne support...");
        List urls = config.getChild("cayenne-enabled-urls").getChildren();
        if (urls != null && !urls.isEmpty()) {
            for (int i = 0; i < urls.size(); i++) {
                Configuration url = (Configuration) urls.get(i);
                String path = url.getAttribute("path");
                if (path != null) {
                    ActionWrapper aw;
                    boolean ro = url.getAttribute("readonly", "false").equalsIgnoreCase("true");
                    if (ro) {
                        aw = new ActionWrapper(
                                new PathAction(path, new CayenneReadOnlySupportAction(this)), config);
                    } else {
                        aw = new ActionWrapper(
                                new PathAction(path, new CayenneSupportBeforeAction(this)), config);
                        // add a post-evaluate action for all the R/W paths
                        site.getActionManager().getPostEvaluationActions()
                                .add(new ActionWrapper(
                                        new PathAction(path, new CayenneSupportAfterAction(this)), config)
                                );
                    }

                    site.getActionManager().getPathActions().add(aw);
                    log.info("... added " + (ro ? "read-only" : "read-write") + " support for: " + path);
                }
            }
        } else {
            log.info(" ... the 'cayenne-enabled-urls' node contains no mappings. " +
                    "Cayenne support will be unavailable to the web requests.");

        }
        //todo implement a ShutDown action

        //populate the Application with a shared DataContext
        DataContext dataContext = (DataContext) site.getAttribute(ServletUtil.DATA_CONTEXT_KEY);

        if (dataContext == null) {
            dataContext = DataContext.createDataContext(isSharedCache());
            site.setAttribute(ServletUtil.DATA_CONTEXT_KEY, dataContext);
            if (isDebugEnabled()) {
                log.info("Created an Application wide DataContext with shared-cache="
                        + isSharedCache());
            }
        }

        DataContext.bindThreadDataContext(dataContext);

        site.setAttribute(JPCayenneModule.JPCAYENNE_SERVICE_NAME, new JPCayenneService());
        log.info(this.toString() + " started.");
    }

    /**
     * Get a map of defined actions.
     *
     * @return The defined actions
     */
    public Map getDefinedActions() {
        return actions;
    }

    public static String getModuleDescription() {
        return DESCRIPTION;
    }

    public SiteContext getSite() {
        return site;
    }

    /**
     * Invoked when the module is destroyed.  This corresponds to the
     * Servlet.destroy() method.
     */
    public void destroy() {
        if (configuration != null) {
            configuration.shutdown();
        }
    }


    /**
     * A helper method to create default ResourceLocator.
     *
     * @param context           the Servlet context
     * @param cayenneConfigPath the path to the cayenne configuration file (e.g: WEB-INF)
     * @return a resourceLocator object
     */
    protected static ResourceLocator createLocator(ServletContext context, String cayenneConfigPath) {
        WebApplicationResourceLocator locator = new WebApplicationResourceLocator();
        locator.setSkipAbsolutePath(true);
        locator.setSkipClasspath(false);
        locator.setSkipCurrentDirectory(true);
        locator.setSkipHomeDirectory(true);

        locator.setServletContext(context);
        if (cayenneConfigPath != null && cayenneConfigPath.trim().length() > 0) {
            locator.addFilesystemPath(cayenneConfigPath.trim());
        }
        return locator;
    }


    public boolean isAutoRollback() {
        return autoRollback;
    }

    public boolean isSessionScope() {
        return sessionScope;
    }

    public boolean isSharedCache() {
        return sharedCache;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public Map getJPCayenneUtils() {
        return jpCayenneUtils;
    }

    public String toString() {
        return NAME + " " + VERSION;
    }
}
