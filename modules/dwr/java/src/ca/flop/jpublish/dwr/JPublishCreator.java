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
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.extend.Creator;
import org.jpublish.SiteContext;

/**
 * A very simple JPublish DWR Creator used for executing JPublish Actions
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 16, 2007 7:44:59 PM)
 */
public class JPublishCreator extends AbstractCreator implements Creator {
    private static final Log log = LogFactory.getLog(JPublishCreator.class);
    private Class clazz = DWRJPublishActionManager.class;
    private SiteContext site = null;
    private String actionName;

    /**
     * Access to the <code>java.lang.Class</code> that this Creator
     * allows access to.
     *
     * @return The type of this allowed class
     */
    public Class getType() {
        return clazz;
    }


    /**
     * obtain an instance of this Creator.
     *
     * @return the instance to use
     * @throws InstantiationException If for some reason the object can not be created
     */
    public Object getInstance() throws InstantiationException {

        WebContext dwrContext = WebContextFactory.get();
        if (site == null) {
            site = (SiteContext) dwrContext.getServletContext()
                    .getAttribute(SiteContext.NAME);
            if (site == null) {
                throw new InstantiationException("Wrong JPublish version or invalid configuration.");
            }
        }
        return new DWRJPublishActionManager(site, actionName);
    }

    /**
     * set the action name
     *
     * @param actionName a valid String containing a meaningful value
     */
    public void setActionName(String actionName) {
        if (actionName == null || actionName.trim().length() == 0) {
            log.error("the 'actionName' parameter must contain a meaningful String.");
        } else {
            this.actionName = actionName;
        }
    }
}
