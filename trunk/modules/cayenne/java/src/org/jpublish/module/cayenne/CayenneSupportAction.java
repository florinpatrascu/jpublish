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
import org.jpublish.JPublishContext;
import org.jpublish.action.Action;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 22, 2007 10:57:20 AM)
 */
public abstract class CayenneSupportAction implements Action {
    protected JPCayenneModule jpCayenneModule;

    public CayenneSupportAction(JPCayenneModule jpCayenneModule) {
        this.jpCayenneModule = jpCayenneModule;
    }

    /**
     * Execute the action using the given context.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */
    public abstract void execute(JPublishContext context, Configuration configuration) throws Exception;

}
