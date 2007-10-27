/*
 * Copyright (c) 2007 Florin T.PATRASCU.
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
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;

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

        JPublishContext context = new JPublishContext(null);
        Map response = new HashMap();

        if (params != null && !params.values().isEmpty()) {
            for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                context.put((String) entry.getKey(), entry.getValue());
            }
        }
        // execute the Action ...
        site.getActionManager().execute(actionName, context);

        // ... and collect any context parameter
        Object[] keys = context.getKeys();

        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            response.put(key, context.get(key));
        }
        return response;
    }
}

