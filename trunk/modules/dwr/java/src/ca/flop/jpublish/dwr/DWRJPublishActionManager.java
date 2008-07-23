/*
 * Copyright (c) 2008 Florin T.PATRASCU.
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

package ca.flop.jpublish.dwr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;
import org.jpublish.util.DateUtilities;
import org.jpublish.util.NumberUtilities;
import org.jpublish.util.URLUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple Action Manager dispatching the DWR execute requests to the JPublish Action Manager.
 * This version expects a Map containing parameters sent from the UI. The Map can be empty.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 17, 2007 5:52:46 PM)
 */

public class DWRJPublishActionManager {
    protected static final Log log = LogFactory.getLog(DWRJPublishActionManager.class);
    public static final String JPUBLISH_CONTEXT_NAME = "context";

    private SiteContext site;
    private String actionName;

    /**
     * @param site       the JPublish siteContext object
     * @param actionName a String containing a valid actionName. The Action is expected to
     *                   be registered with the JPublish framework
     * @throws InstantiationException if the site or the actionName are null or contains invalid values
     */
    public DWRJPublishActionManager(SiteContext site, String actionName) throws InstantiationException {
        if (site == null)
            throw new InstantiationException("The JPublish siteContext cannot be null!");

        if (actionName == null || actionName.trim().length() == 0)
            throw new InstantiationException("The Action name cannot be null or empty!");

        this.site = site;
        this.actionName = actionName;
    }

    /**
     * execute a JPublish action specified by the actionName variable. A future version
     * will use a refactored JPublishContext in order to avoid the multiple iterations
     * required to put in and get out the parameters in and out the context todo
     *
     * @param params a Map containing the parameters transmitted by the Ajax code
     * @return a new Map containing any data defined by the Action
     * @throws Exception if anything wrong happens
     */
    public Map execute(Map params) throws Exception {
        WebContext dwrContext = WebContextFactory.get();
        JPublishContext context = newContextForAction(dwrContext);
        Map response = new HashMap();

        // expose the context itself for debugging purposes
        if (site.isDebug()) {
            context.put(JPUBLISH_CONTEXT_NAME, context);
            // various gifts from DWR ;)
            context.put(JPublishCreator.DWR_CURRENT_PAGE, dwrContext.getCurrentPage());
            context.put(JPublishCreator.DWR_SCRIPT_SESSION, dwrContext.getScriptSession());
        }

        if (params != null && !params.values().isEmpty()) {
            for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                context.put((String) entry.getKey(), entry.getValue());
            }
        }
        // execute the Action ...
        site.getActionManager().execute(actionName, context);
        removeJPublishContextObjects(context);

        // ... and collect any context parameter
        Object[] keys = context.getKeys();

        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            response.put(key, context.get(key));
        }
        return response;
    }

    /**
     * create a new mini-JPublish context
     *
     * @param dwrContext the WebContext created by the DWR
     * @return a new JPublish context
     */
    private JPublishContext newContextForAction(WebContext dwrContext) {
        JPublishContext context = new JPublishContext(this);
        context.disableCheckReservedNames(this);

        context.put(JPublishContext.JPUBLISH_REQUEST, dwrContext.getHttpServletRequest());
        context.put(JPublishContext.JPUBLISH_RESPONSE, dwrContext.getHttpServletResponse());
        context.put(JPublishContext.JPUBLISH_SESSION, dwrContext.getSession());
        context.put(JPublishCreator.APPLICATION, site.getServletContext());
        // add the character encoding map to the context
        context.put(JPublishContext.JPUBLISH_CHARACTER_ENCODING_MAP, site.getCharacterEncodingManager().getDefaultMap());

        // add the URLUtilities to the context
        URLUtilities urlUtilities = new URLUtilities(dwrContext.getHttpServletRequest(),
                dwrContext.getHttpServletResponse());
        context.put(JPublishContext.JPUBLISH_URL_UTILITIES, urlUtilities);

        // add the DateUtilities to the context
        context.put(JPublishContext.JPUBLISH_DATE_UTILITIES, DateUtilities.getInstance());

        // add the NumberUtilities to the context
        context.put(JPublishContext.JPUBLISH_NUMBER_UTILITIES, NumberUtilities.getInstance());

        // add the messages log to the context
        context.put(JPublishContext.JPUBLISH_SYSLOG, SiteContext.syslog);

        // expose the SiteContext
        context.put(JPublishContext.JPUBLISH_SITE, site);
        return context;
    }

    /**
     * remove any references to internal JPublish objects
     *
     * @param context a clean context containing only what needs to be returned to the dwr client
     */
    private void removeJPublishContextObjects(JPublishContext context) {
        context.remove(JPublishContext.JPUBLISH_REQUEST);
        context.remove(JPublishContext.JPUBLISH_RESPONSE);
        context.remove(JPublishContext.JPUBLISH_SESSION);
        context.remove(JPublishCreator.APPLICATION);
        // add the character encoding map to the context
        context.remove(JPublishContext.JPUBLISH_CHARACTER_ENCODING_MAP);


        context.remove(JPublishContext.JPUBLISH_URL_UTILITIES);

        // add the DateUtilities to the context
        context.remove(JPublishContext.JPUBLISH_DATE_UTILITIES);

        // add the NumberUtilities to the context
        context.remove(JPublishContext.JPUBLISH_NUMBER_UTILITIES);

        // add the messages log to the context
        context.remove(JPublishContext.JPUBLISH_SYSLOG);

        // expose the SiteContext
        context.remove(JPublishContext.JPUBLISH_SITE);

    }
}

