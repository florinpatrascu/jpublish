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

import org.directwebremoting.extend.Handler;
import org.directwebremoting.extend.DebugPageGenerator;
import org.directwebremoting.util.LocalUtil;
import org.directwebremoting.util.MimeConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version $Revision:$ Apr 5, 2007, 10:47:57 AM
 */
public class DWRTestHandler  implements Handler {
    /* (non-Javadoc)
     * @see org.directwebremoting.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String scriptName = request.getPathInfo();
        String dwrPrefix = (String) request.getAttribute( DWRModule.DWR_PREFIX_REQUEST_TAG_NAME);
        if (dwrPrefix == null || dwrPrefix.trim().length() == 0) {
            dwrPrefix = DWRModule.DWR_DEFAULT_PREFIX;
        }
        scriptName = LocalUtil.replace(scriptName, dwrPrefix, "");
        scriptName = LocalUtil.replace(scriptName, testHandlerUrl, "");
        scriptName = LocalUtil.replace(scriptName, "/", "");

        String page = debugPageGenerator.generateTestPage(request.getContextPath() + request.getServletPath(), scriptName);

        response.setContentType(MimeConstants.MIME_HTML);
        PrintWriter out = response.getWriter();
        out.print(page);
    }

    /**
     * Setter for the debug page generator
     * @param debugPageGenerator
     */
    public void setDebugPageGenerator(DebugPageGenerator debugPageGenerator)
    {
        this.debugPageGenerator = debugPageGenerator;
    }

    /**
     * The bean to handle debug page requests
     */
    protected DebugPageGenerator debugPageGenerator = null;

    /**
     * Setter for the URL that this handler available on
     * @param testHandlerUrl the testHandlerUrl to set
     */
    public void setTestHandlerUrl(String testHandlerUrl)
    {
        this.testHandlerUrl = testHandlerUrl;
    }

    /**
     * What URL is this handler available on?
     */
    protected String testHandlerUrl;
}
