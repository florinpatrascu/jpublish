/*
 * Copyright (c) 2009. Florin T.PATRASCU
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
 */

package org.jpublish.module.restpathactions;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.atlassian.util.profiling.UtilTimerStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.Action;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2010-11-27)
 */
public class RestPathActionsModule implements JPublishModule {
    protected static final Log log = LogFactory.getLog(RestPathActionsModule.class);
    public static final String RESPONSE = "rpa_response";

    private static final String CALLBACK = "callback";

    private static final String FORMAT = "format";

    private static final String NAME = "rpa";
    private static final String DESCRIPTION = "REST-like Path Actions support";
    private List<String> infoDetails = new ArrayList<String>();
    private String modulePath;
    private String formatParameter = FORMAT;
    private String callbackParameter = CALLBACK;
    private Map<String, Action> actions = new HashMap<String, Action>(1);
    private SiteContext site;
    private List<RestPathActionModel> restModels = new ArrayList<RestPathActionModel>();
    private String defaultRepository;
    private boolean debug;
    private boolean profiling;

    public void init(SiteContext site, Configuration configuration) throws Exception {
        this.site = site;
        infoDetails.add("*** Module: RestLikePathActions (RPA) initializing ...");

        if (configuration != null) {
            load(configuration);
        } else {
            log.warn("  No configuration specified; module disabled.");
        }
    }

    public Map getDefinedActions() {
        return new HashMap();
    }

    public void destroy() {
    }

    public String getName() {
        return NAME;
    }

    public String getInfo() {
        return DESCRIPTION;
    }

    public List<String> getInfoDetails() {
        return infoDetails;
    }

    public void load(Configuration configuration) throws ConfigurationException {
        modulePath = configuration.getChildValue("path", NAME);
        defaultRepository = configuration.getChildValue("repository", "content");
        List<Configuration> routesNodesOrConfigFiles = configuration.getChildren("routes");

        for (Configuration routesNodesOrConfigFile : routesNodesOrConfigFiles) {
            List<Configuration> routes = null;
            String routesConfig = routesNodesOrConfigFile.getAttribute("config");

            if (routesConfig != null) {
                File configFile = new File(site.getRoot(), routesConfig);
                Configuration routesConfiguration = new XMLConfiguration("RPAConfiguration", configFile);
                infoDetails.add("  Loading routes from: " + configFile.getAbsolutePath());

                List<Configuration> rc = routesConfiguration.getChildren();
                if (rc != null) {
                    routes = rc;
                } else {
                    infoDetails.add("    no routes defined.");
                }
            } else {
                routes = routesNodesOrConfigFile.getChildren();
            }

            if (routes != null && !routes.isEmpty()) {

                for (Configuration routeConfiguration : routes) {
                    RestPathActionModel restModel = new RestPathActionModel();
                    restModel.setPath(routeConfiguration.getAttribute("path", ""));
                    restModel.setAction(routeConfiguration.getAttribute("action"));
                    restModel.setPage(routeConfiguration.getAttribute("page"));
                    restModel.setMethods(routeConfiguration.getAttribute("method", "GET"));
                    restModel.setContentType(routeConfiguration.getAttribute("content-type"));
                    restModel.setConfiguration(routeConfiguration);

                    restModels.add(restModel);
                    infoDetails.add("  route: " + restModel);
                }
            }
        }


        try {
            formatParameter = configuration.getChild(FORMAT).getAttribute("parameter", FORMAT);
            callbackParameter = configuration.getChild(CALLBACK).getAttribute("parameter", CALLBACK);
            debug = configuration.getChild("debug").getValue("false").equalsIgnoreCase("true");
            profiling = configuration.getChild("profiling").getValue("false").equalsIgnoreCase("true");
        } catch (Exception e) {
            //will use the default
        }

        infoDetails.add(" format parameter .......: " + formatParameter);
        infoDetails.add(" jsonp callback parameter: " + callbackParameter);
        infoDetails.add(" module profiling .......: " + profiling);
        infoDetails.add(" debug ..................: " + debug);

        final PathAction restPathAction = new PathAction(modulePath, new RestPathAction(modulePath, this));
        site.getActionManager().getPathActions().add(new ActionWrapper(restPathAction, null));
        actions.put(NAME, restPathAction);

        infoDetails.add("RPA fully loaded. Good luck!");
        infoDetails.add("****************************");
        printInfoDetails(infoDetails);
        UtilTimerStack.setActive(this.isProfiling());
    }


    private void printInfoDetails(List<String> infoDetails) {
        if (infoDetails != null && !infoDetails.isEmpty()) {
            for (String info : infoDetails) {
                log.info(info);
            }
        }
    }

    public List<RestPathActionModel> getRestModels() {
        return restModels;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void execute(String action, JPublishContext context, Configuration configuration) throws Exception {
        if (action != null && context != null) {
            site.getActionManager().execute(action, context, configuration);
        }
    }

    public SiteContext getSite() {
        return site;
    }

    public String getDefaultRepository() {
        return defaultRepository;
    }

    public String getFormatParameter() {
        return formatParameter;
    }

    public String getCallbackParameter() {
        return callbackParameter;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isProfiling() {
        return profiling;
    }
}
