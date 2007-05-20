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

import com.anthonyeden.lib.config.Configuration;
import org.jpublish.JPublishContext;
import org.jpublish.action.Action;

import javax.servlet.http.HttpServletRequest;

/**
 * The DWRAction is used to pass the control to the DWRProcessor
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 1, 2006 5:17:57 PM)
 */

public class DWRAction implements Action {
    private DWRModule dwrModule;

    public DWRAction(DWRModule dwrModule) {
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
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        //SiteContext site = (SiteContext) context.get("site");

        context.put("dwrPathPrefix", dwrModule.getDwrPathPrefix());
        request.setAttribute(DWRModule.DWR_PREFIX_REQUEST_TAG_NAME, dwrModule.getDwrPathPrefix());

        DWRProcessor dwrProcessor = dwrModule.getDwrProcessor();
        dwrProcessor.execute(context, configuration);
        // if the DWR processor consumed the call (specifi url, like: /engine.js, etc)
        // then context will already contain the Stop, so next line is not appropriate
        //context.setStopProcessing();
    }

}
