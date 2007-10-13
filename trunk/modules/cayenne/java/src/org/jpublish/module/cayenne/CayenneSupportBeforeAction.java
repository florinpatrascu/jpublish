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

import javax.servlet.http.HttpSession;

/**
 * Action executed before rendering a page, so the Cayenne support can be added
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 23, 2007 2:43:42 PM)
 */
public class CayenneSupportBeforeAction extends CayenneSupportAction {
    private static final Log log = LogFactory.getLog(CayenneSupportBeforeAction.class);
    private static JPCayenneService jpCayenneService = new JPCayenneService();

    public CayenneSupportBeforeAction(JPCayenneModule jpCayenneModule) {
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
        // Bind DataContext to the request thread
        DataContext dc = getDataContext(context.getSession());
        DataContext.bindThreadDataContext(dc);

        context.put(JPCayenneModule.JPCAYENNE_SERVICE_NAME, jpCayenneService);
        context.getRequest().setAttribute(JPCayenneModule.JPCAYENNE_SERVICE_NAME, jpCayenneService);
        context.getRequest().setAttribute(JPCayenneModule.JPCAYENNE_DATA_CONTEXT, dc);
        context.put(JPCayenneModule.JPCAYENNE__UTILS, jpCayenneModule.getJPCayenneUtils());
        context.getRequest().setAttribute(JPCayenneModule.JPCAYENNE__UTILS, jpCayenneModule.getJPCayenneUtils());

        if (jpCayenneModule.isDebugEnabled()) {
            context.put(JPCayenneModule.JPCAYENNE_DATA_CONTEXT, dc);
        }
    }

    /**
     * Return a DataContext instance. If the DataContextFilter is configured
     * to associate the DataContext with the session (which is the default
     * behaviour), the DataContext will be bound to the users session. If
     * the DataContext is already available, the existing DataContext will be
     * used otherwise a new DataContex object will be created.
     * <p/>
     * If this filter is configured with <tt>create-each-request</tt> to be true
     * then a new DataContext will be created for each request and the DataContext
     * will not be bound to the session.
     * <p/>
     * If this filter is configured with <tt>use-shared-cache</tt> to be true
     * (which is the default behaviour) this method will create DataContext objects
     * using the Cayenne shared cache, otherwise they will not use the shared cache.
     *
     * @param session the users session
     * @return the session DataContext object
     */
    protected synchronized DataContext getDataContext(HttpSession session) {

        DataContext dataContext = null;

        if (jpCayenneModule.isSessionScope()) {
            dataContext = (DataContext) session.getAttribute(ServletUtil.DATA_CONTEXT_KEY);
        }

        if (dataContext == null) {
            dataContext = DataContext.createDataContext(jpCayenneModule.isSharedCache());

            if (jpCayenneModule.isDebugEnabled()) {
                String msg = "[DEBUG] created DataContex with shared-cache=" + jpCayenneModule.isSharedCache();
                log.info(msg);
            }

            if (jpCayenneModule.isSessionScope()) {
                session.setAttribute(ServletUtil.DATA_CONTEXT_KEY, dataContext);
            }
        }

        return dataContext;
    }
}
