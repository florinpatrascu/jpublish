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

package ca.flop.jpublish;

import org.jpublish.JPublishContext;
import org.jpublish.action.Action;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * For simplicity it may be a good idea to make a base class that extends JPublish Actions,
 * and make a method that will return the Spring ApplicationContext
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jul 17, 2006 10:21:29 PM)
 */
public abstract class SpringActionSupport  implements Action {
    public static final String SPRING = "JPublishSpring";

    /**
     * @param context the JPublish context
     * @return a Spring application context
     */
    protected final ApplicationContext getApplicationContext(JPublishContext context) {
        ServletContext sc = context.getApplication();
        return WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
    }
}
