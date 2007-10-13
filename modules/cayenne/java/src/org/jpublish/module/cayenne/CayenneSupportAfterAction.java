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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;

import java.util.Collection;
import java.util.Iterator;

/**
 * Action executed after a request was fulfilled and the page rendered. There will be
 * no page context at the time of executing this Action.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 23, 2007 2:45:20 PM)
 */
public class CayenneSupportAfterAction extends CayenneSupportAction {
    protected static final Log log = LogFactory.getLog(CayenneSupportAfterAction.class);

    public CayenneSupportAfterAction(JPCayenneModule jpCayenneModule) {
        super(jpCayenneModule);
    }

    /**
     * Execute the action using the given context.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        DataContext dc = (DataContext) context.getRequest().getAttribute(JPCayenneModule.JPCAYENNE_DATA_CONTEXT);

        if (dc != null) {
            if (jpCayenneModule.isDebugEnabled() && dc.hasChanges()) {
                log.info("[DEBUG] Uncommitted data objects:");

                Collection uncommitted = dc.uncommittedObjects();
                for (Iterator i = uncommitted.iterator(); i.hasNext();) {
                    log.info("[DEBUG]   " + i.next());
                }
            }

            if (jpCayenneModule.isAutoRollback()) {
                dc.rollbackChanges();
            }

            DataContext.bindThreadDataContext(null);
        }
    }
}
