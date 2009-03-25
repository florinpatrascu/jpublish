package org.jpublish.module.restlet;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.util.ClassUtilities;
import com.noelios.restlet.ext.servlet.ServletConverter;
import com.noelios.restlet.Engine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;
import org.restlet.Router;
import org.restlet.Context;
import org.restlet.Restlet;

import java.io.File;
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
    private static final String VERSION = "1.0";
    private static final String JPUBLISH_DEFAULT_REST_URL = "/rest";

    private static final String DESCRIPTION = "RESTLET (" + Engine.VERSION + ") support for JPublish";

    private SiteContext site;
    private Map actions = new HashMap(5);
    private ServletConverter converter;
    public boolean debug = false;
    private String restJPublishURL = JPUBLISH_DEFAULT_REST_URL;

    private String repository = "content";
    public static final String JP_RESTLET_ACTION_RESPONSE = "__jpRestletActionResponseValue__";
    public static final String JP_RESTLET_ACTION_RESPONSE_TYPE = "__jpRestletActionResponseType__";

    public void init(SiteContext site, Configuration configuration) throws Exception {
        log.info(NAME + " starting for: " + site.getServletContext().getServletContextName());
        this.site = site;

        String restletConfigPath = configuration.getChildValue("restlet-config", "WEB-INF/jprestlet-config.xml");
        restJPublishURL = configuration.getChildValue("url", JPUBLISH_DEFAULT_REST_URL);

        log.info("Mapping JPRestlet for all calls on: " + restJPublishURL);

        log.info("Initializing the " + this.toString() + " framework from: " + restletConfigPath);
        this.converter = new ServletConverter(site.getServletContext());
        initJPRestlet(restletConfigPath);

        site.getActionManager().getPathActions().add(
                new ActionWrapper(
                        new PathAction(restJPublishURL, new JPRestletAction(this)), configuration)
        );

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
            String jpublishRestletPathPrefix = restJPublishURL.substring(0, restJPublishURL.lastIndexOf("/"));

            for (int i = 0; i < routes.size(); i++) {
                Configuration route = (Configuration) routes.get(i);
                String map = route.getAttribute("map");

                if (map != null) {
                    String jpRestletPath = jpublishRestletPathPrefix + map;
                    String action = route.getAttribute("action", EMPTY_STRING);
                    String page = route.getAttribute("page", EMPTY_STRING);

                    Restlet restlet = null;
                    if (route.getAttribute("restlet") != null) {
                        restlet = (Restlet) ClassUtilities.loadClass(route.getAttribute("restlet")).newInstance();
                        router.attach(jpRestletPath, restlet);
                        log.info("... routing: " + jpRestletPath + " to: " + restlet);
                    } else{
                        router.attach(jpRestletPath, new JPSimpleRestlet(this, page, action));
                        log.info("... routing: " + jpRestletPath + " to action: " + action+", page: "+page);
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
