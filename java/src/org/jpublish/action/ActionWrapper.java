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

/**
 * Wrap an Action allowing configuration information to be passed to the
 * action during the invocation of the execute() method.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class ActionWrapper {

    private Action action;
    private Configuration configuration;

    /**
     * Construct a new ActionWrapper for the given Action and configuration.
     *
     * @param action        The Action
     * @param configuration The configuration
     */

    public ActionWrapper(Action action, Configuration configuration) {
        this.action = action;
        this.configuration = configuration;
    }

    /**
     * Get the wrapped action.
     *
     * @return The wrapped Action
     */

    public Action getAction() {
        return action;
    }

    /**
     * Execute the action using the given context.
     *
     * @param context The current JPublish context
     * @throws Exception Any Exception
     */
    public void execute(JPublishContext context) throws Exception {
        try {
            UtilTimerStack.push(action.getClass().getName());
            action.execute(context, configuration);
        } finally {
            UtilTimerStack.pop(action.getClass().getName());
        }
    }

}
