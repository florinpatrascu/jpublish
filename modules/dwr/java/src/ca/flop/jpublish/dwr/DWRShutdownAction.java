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

package ca.flop.jpublish.dwr;

import org.jpublish.action.Action;
import org.jpublish.JPublishContext;
import com.anthonyeden.lib.config.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * The DWRShutdownAction is used to pass the control to the DWRProcessor
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Monday; May 21, 2007)
 */

public class DWRShutdownAction implements Action {
    private DWRModule dwrModule;

    public DWRShutdownAction(DWRModule dwrModule) {
        this.dwrModule = dwrModule;
    }

    /**
     * Execute the action using the given context.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */

    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        DWRProcessor dwrProcessor = dwrModule.getDwrProcessor();
        dwrProcessor.shutdown();
    }

}
