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
import org.apache.cayenne.conf.ServletUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 23, 2007 2:53:29 PM)
 */
public class CayenneReadOnlySupportAction extends CayenneSupportAction {
    private static final Log log = LogFactory.getLog(CayenneReadOnlySupportAction.class);
    private static JPCayenneService jpCayenneService = new JPCayenneService();


    public CayenneReadOnlySupportAction(JPCayenneModule jpCayenneModule) {
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
        DataContext dataContext = (DataContext) context.getSiteContext().getAttribute(ServletUtil.DATA_CONTEXT_KEY);

        if (dataContext == null) {
            dataContext = DataContext.createDataContext(jpCayenneModule.isSharedCache());
            context.getSiteContext().setAttribute(ServletUtil.DATA_CONTEXT_KEY, dataContext);
            if (jpCayenneModule.isDebugEnabled()) {
                String msg = "[DEBUG] created GLOBAL DataContex with shared-cache=" + jpCayenneModule.isSharedCache();
                log.info(msg);
            }
        }

        DataContext.bindThreadDataContext(dataContext);

        context.put(JPCayenneModule.JPCAYENNE_SERVICE_NAME, jpCayenneService);
        context.getRequest().setAttribute(JPCayenneModule.JPCAYENNE_SERVICE_NAME, jpCayenneService);
        context.getRequest().setAttribute(JPCayenneModule.JPCAYENNE_DATA_CONTEXT, dataContext);
        if (jpCayenneModule.isDebugEnabled()) {
            context.put(JPCayenneModule.JPCAYENNE_DATA_CONTEXT, dataContext);
        }
    }
}
