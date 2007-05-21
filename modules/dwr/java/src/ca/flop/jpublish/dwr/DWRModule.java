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

package ca.flop.jpublish.dwr;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.Action;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jul 26, 2006 2:54:38 PM)
 */
public class DWRModule implements JPublishModule {
    private static Log log = LogFactory.getLog(DWRModule.class);
    private static final String NAME = "JPublishDWR Module";
    private static final String DESCRIPTION = "This module offers Ajax to JPublish applications.";

    private SiteContext site;
    private Map actions = new HashMap(1);
    private DWRProcessor dwrProcessor;
    private String dwrPathPrefix = DWRModule.DWR_DEFAULT_PREFIX;
    /**
     * The time on the script files
     */
    private static final long moduleStartTime;

    /**
     * The etag (=time for us) on the script files
     */
    private static final String etag;
    public static final String DWR_PREFIX_REQUEST_TAG_NAME = "__dwrPrefix__";
    public static final String DWR_DEFAULT_PREFIX = "dwr";

    /**
     * Initialize the container start time
     */
    static {
        // Browsers are only accurate to the second
        long now = System.currentTimeMillis();
        moduleStartTime = now - (now % 1000);

        etag = "\"" + moduleStartTime + '\"'; //$NON-NLS-1$
    }

    /**
     * Initialize the module.  This corresponds with the
     * Servlet.init() method, except that it includes a reference
     * to the JPublish SiteContext.
     *
     * @param site          The SiteContext
     * @param configuration The configuration object
     * @throws Exception
     */

    public void init(SiteContext site, Configuration configuration) throws Exception {
        log.info(NAME + " starting for: " + site.getServletContext().getServletContextName());

        this.site = site;
        String dwrConfigFile = configuration.getChildValue("dwr-config-file", "WEB-INF/dwr.xml");
        dwrPathPrefix = configuration.getChildValue("dwr-path-prefix", DWR_DEFAULT_PREFIX);
        String[] dwrUrls = new String[]{
                dwrPathPrefix + "/*", "/call/*"
        };
        site.setAttribute("dwrConfigFile", dwrConfigFile);

        List dwrServletInitParams = configuration.getChild("dwr-servlet-parameters").getChildren();
        Map dwrServletInitParamsMap = new HashMap(5);
        site.setAttribute("dwrServletInitParamsMap", dwrServletInitParamsMap);

        for (int i = 0; i < dwrServletInitParams.size(); i++) {
            Configuration dwrInitParam = (Configuration) dwrServletInitParams.get(i);
            site.setAttribute(dwrInitParam.getName(), dwrInitParam.getValue("false"));
            dwrServletInitParamsMap.put(dwrInitParam.getName(), dwrInitParam.getValue("false"));
        }

        log.info(" loading the DWR/Ajax support from: " + dwrConfigFile);
        dwrProcessor = new DWRProcessor();
        dwrProcessor.init(site);

        Action dwrAction = new DWRAction(this);
        actions.put("DwrModule.dwrAction", dwrAction);
        List pathActions = site.getActionManager().getPathActions();
        for (int i = 0; i < dwrUrls.length; i++) {
            String dwrUrl = dwrUrls[i];
            pathActions.add(
                    new ActionWrapper(
                            new PathAction(dwrUrl, dwrAction), configuration));
        }

        //adding a hook for the DWR shutdown event:
        site.getActionManager().getShutdownActions().add(
                new ActionWrapper(new DWRShutdownAction(this), configuration));

        log.info(NAME + " started.");

    }

    /**
     * Get a map of defined actions.
     *
     * @return The defined actions
     */

    public Map getDefinedActions() {
        return actions;
    }

    /**
     * Invoked when the module is destroyed.  This corresponds to the
     * Servlet.destroy() method.
     */

    public void destroy() {
    }

    public static String getModuleDescription() {
        return DESCRIPTION;
    }

    public DWRProcessor getDwrProcessor() {
        return dwrProcessor;
    }

    public SiteContext getSite() {
        return site;
    }

    public String getDwrPathPrefix() {
        return dwrPathPrefix;
    }

    public static long getModuleStartTime() {
        return moduleStartTime;
    }

    public static String getEtag() {
        return etag;
    }

}
