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

package ca.flop.jpublish.i18n;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.Action;
import org.jpublish.action.ActionWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jul 26, 2006 2:54:38 PM)
 */
public class I18nModule implements JPublishModule {
    private static Log log = LogFactory.getLog(I18nModule.class);

    private static final String NAME = "JPublishI18n Module";
    private static final String DESCRIPTION = "i18n support for JPublish.";

    private SiteContext site;
    private Map actions = new HashMap(1);
    /**
     * The time on the script files
     */
    private static final long moduleStartTime;

    /**
     * The etag (=time for us) on the script files
     */
    private static final String etag;

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

        Action i18nAction = new SpringI18nSupport();
        actions.put("I18nModule.i18nAction", i18nAction);
        site.getActionManager().getGlobalActions().add(new ActionWrapper(i18nAction, configuration));
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

    public SiteContext getSite() {
        return site;
    }

    public static long getModuleStartTime() {
        return moduleStartTime;
    }

    public static String getEtag() {
        return etag;
    }

}
