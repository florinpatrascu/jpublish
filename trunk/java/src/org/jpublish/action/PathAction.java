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

package org.jpublish.action;

import com.anthonyeden.lib.config.Configuration;
import com.atlassian.util.profiling.UtilTimerStack;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;

/**
 * An action which is executed when a particular path is matched.  PathAction
 * actually wraps another action.  The current algorithm for matching is
 * contained in the <code>PathUtilities.match()</code> method.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @see org.jpublish.util.PathUtilities
 */

public class PathAction implements Action {

    private String path;
    private Action action;

    /**
     * Construct a new PathAction.
     *
     * @param path   The path to match for execution
     * @param action The action which will be executed
     */
    public PathAction(String path, Action action) {
        this.path = path;
        this.action = action;
    }

    /**
     * Get the path for matching.
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Execute the action using the given context.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        boolean scriptOrPathActionWrapper = action.getClass().getName()
                .indexOf(ActionManager.SCRIPT_ACTION) >= 0 || action.getClass().getName()
                .indexOf(ActionManager.PATH_ACTION) >=0;

        try {
            if (SiteContext.getProfiling() && !scriptOrPathActionWrapper) {
                UtilTimerStack.push(action.getClass().getName());
            }
            action.execute(context, configuration);

        } finally {
            if (SiteContext.getProfiling() && !scriptOrPathActionWrapper) {
                UtilTimerStack.pop(action.getClass().getName());
            }
        }
    }

}
