/*
 * Copyright 2004-2007 the original author or authors.
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

package org.jpublish.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishComponent;
import org.jpublish.JPublishContext;
import org.jpublish.Page;

/**
 * Runtime wrapper for a component.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 2.0
 */

public class ComponentWrapper {

    private static final Log log = LogFactory.getLog(ComponentWrapper.class);
    private static final String EMPTY_STRING = "";

    private JPublishComponent component;
    private JPublishContext context;

    /**
     * Construct a new ComponentWrapper.
     *
     * @param component The wrapped component
     * @param context   The context
     */

    public ComponentWrapper(JPublishComponent component, JPublishContext context) {
        this.component = component;
        this.context = context;
    }

    /**
     * Return the rendered component.
     *
     * @return The renderered component string
     * @deprecated use renderText or renderPath instead
     */

    public String toString() {
        try {
            Page page = (Page) context.get(JPublishContext.JPUBLISH_PAGE);
            return component.render(page.getPath(), context);
        } catch (Exception e) {
            log.error("Error rendering component " + component.getName());
            e.printStackTrace();
            return EMPTY_STRING;
        }
    }

    /**
     * Process the text parameter and Render the component
     *
     * @param text the string to process
     * @return The rendered component
     */
    public String renderText(String text) {
        try {
            return component.renderText(text, context);
        } catch (Exception e) {
            log.error("Error rendering the text:\n=====================\n" +
                    text +
                    "\n=====================\n, for the component " + component.getName());
            e.printStackTrace();
            return EMPTY_STRING;
        }
    }

    /**
     * Process the View from at the given path and Render the component
     *
     * @param path the View to process
     * @return The rendered component
     */
    public String renderPath(String path) {
        try {
            return component.renderPath(path, context);
        } catch (Exception e) {
            log.error("Error rendering the path: " + path + ", for the component "
                    + component.getName());
            e.printStackTrace();
            return EMPTY_STRING;
        }
    }

}
