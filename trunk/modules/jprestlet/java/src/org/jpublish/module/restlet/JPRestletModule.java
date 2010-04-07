package org.jpublish.module.restlet;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.util.ClassUtilities;
import com.noelios.restlet.Engine;
import com.noelios.restlet.ext.servlet.ServletConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JPublish module offering integration support with the Restlet framework.
 * We are using the ServletConverter for passing through the REST requests intercepted by JPublish
 * <p/>
 * See:
 * http://www.restlet.org/documentation/1.0/ext/com/noelios/restlet/ext/servlet/ServletConverter.html
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2008.10.31 9:24:41 PM)
 */
public class JPRestletModule implements JPublishModule {
    public static final String JPRESTLET_MODULE_TAG = "__JPRestletModule";
    public static final String JPCONTEXT_RESTLET_TAG = "__JPRestletJPContext";
    public static final String EMPTY_STRING = "";

    private static final Log log = LogFactory.getLog(JPRestletModule.class);

    private static final String NAME = "JPRestlet Module";
    private static final String VERSION = "1.0.2";
    private static final String JPUBLISH_DEFAULT_REST_URL = "/rest";

    private static final String DESCRIPTION = "RESTLET (" + Engine.VERSION_HEADER + ") support for JPublish";

    private SiteContext site;
    private Map actions = new HashMap(5);
    private ServletConverter converter;
    public boolean debug = false;
    private String restJPublishURL = JPUBLISH_DEFAULT_REST_URL;
    private List restJPublishURLS = new ArrayList();

    private String repository = "content";
    public static final String JP_RESTLET_ACTION_RESPONSE = "__jpRestletActionResponseValue__";
    public static final String JP_RESTLET_ACTION_RESPONSE_TYPE = "__jpRestletActionResponseType__";
    private static final String RESTLET_TAG = "restlet";
    private static final String RESOURCE_TAG = "resource";

    public void init(SiteContext site, Configuration configuration) throws Exception {
        log.info(NAME + " starting for: " + site.getServletContext().getServletContextName());
        this.site = site;

        String restletConfigPath = configuration.getChildValue("restlet-config", "WEB-INF/jprestlet-config.xml");
        //legacy parameter
        if (configuration.getChildValue("url") != null) {
            restJPublishURLS.add(
                    configuration.getChildValue("url", JPUBLISH_DEFAULT_REST_URL));
        }

        if (configuration.getChild("urls") != null) {
            List restURLSConfig = configuration.getChild("urls").getChildren();
            for (int i = 0; restURLSConfig != null && i < restURLSConfig.size(); i++) {
                Configuration urlConfig = (Configuration) restURLSConfig.get(i);
                restJPublishURLS.add(urlConfig.getValue(JPUBLISH_DEFAULT_REST_URL));
            }
        }

        log.info("Initializing the " + this.toString() + " framework from: " + restletConfigPath);
        this.converter = new ServletConverter(site.getServletContext());
        initJPRestlet(restletConfigPath);

        log.info("Mapping JPRestlet for all calls on: ");

        //map JPRestletModule as a pth-action
        for (int i = 0; i < restJPublishURLS.size(); i++) {
            String url = (String) restJPublishURLS.get(i);
            site.getActionManager().getPathActions().add(
                    new ActionWrapper(
                            new PathAction(url, new JPRestletAction(this)), configuration)
            );
            log.info("    " + url);
        }

        log.info(this.toString() + " is ready.");
    }

    public Map getDefinedActions() {
        return actions;
    }

    public void destroy() {
        log.info(this.toString() + ", destroyed.");
    }

    private void initJPRestlet(String restletConfigurationPath) throws Exception {
        Context restletContext = converter.getContext();
        Router router = new Router(restletContext);
        File restletConfigurationFile = new File(site.getRoot(), restletConfigurationPath);
        Configuration restletConfiguration = new XMLConfiguration("restletConfiguration", restletConfigurationFile);

        List routes = restletConfiguration.getChild("routes").getChildren();
        repository = restletConfiguration.getChild("repository").getAttribute("name", "content");
        log.info("Default restlet repository: " + repository);
        log.info("Adding routes to RESTLET support...");
        if (routes != null && !routes.isEmpty()) {
            for (int j = 0; j < restJPublishURLS.size(); j++) {
                String restJPublishURL = (String) restJPublishURLS.get(j);
                String jpublishRestletPathPrefix = restJPublishURL.substring(0, restJPublishURL.lastIndexOf("/"));
                for (int i = 0; i < routes.size(); i++) {
                    Configuration route = (Configuration) routes.get(i);
                    String map = route.getAttribute("map");

                    if (map != null) {
                        String jpRestletPath = jpublishRestletPathPrefix + map;
                        String action = route.getAttribute("action", EMPTY_STRING);
                        String page = route.getAttribute("page", EMPTY_STRING);

                        if (route.getAttribute(RESTLET_TAG) != null) {
                            Restlet restlet = (Restlet) ClassUtilities.loadClass(route.getAttribute(RESTLET_TAG)).newInstance();
                            router.attach(jpRestletPath, restlet);
                            log.info("... restlet routing: " + jpRestletPath + " to: " + restlet);
                        } else {
                            final String resourceName = route.getAttribute(RESOURCE_TAG);
                            if (resourceName != null) {
                                router.attach(jpRestletPath, ClassUtilities.loadClass(resourceName));
                                log.info("... resource routing: " + jpRestletPath + " to: " + resourceName);
                            } else {
                                router.attach(jpRestletPath, new JPSimpleRestlet(this, page, action));
                                log.info("... action routing: " + jpRestletPath + " to action: " + action + ", page: " + page);
                            }
                        }

                    }
                }
            }

        } else {
            log.info(" ... no REST mappings? REST support will be unavailable for some of the web requests.");

        }
        debug = restletConfiguration.getChildValue("debug", "false").equalsIgnoreCase("true");
        converter.setTarget(router);
    }

    public String toString() {
        return NAME + " version: " + VERSION + ", description: " + DESCRIPTION;
    }

    public boolean isDebug() {
        return debug;
    }

    public ServletConverter getConverter() {
        return converter;
    }

    public String getRepository() {
        return repository;
    }

    public SiteContext getSite() {
        return site;
    }
}
