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
import org.directwebremoting.extend.Remoter;
import org.directwebremoting.servlet.PathConstants;
import org.directwebremoting.util.LocalUtil;
import org.directwebremoting.util.MimeConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version $Revision:$ Apr 5, 2007, 10:43:43 AM
 */
public class DWRInterfaceHandler implements Handler {

    /**
     * What URL is this handler available on?
     */
    protected String interfaceHandlerUrl;

    /* (non-Javadoc)
    * @see org.directwebremoting.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    *
    */
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String scriptName = request.getPathInfo();

        //I will refactor this code
        String dwrPrefix = (String) request.getAttribute(DWRModule.DWR_PREFIX_REQUEST_TAG_NAME);
        if (dwrPrefix == null || dwrPrefix.trim().length() == 0) {
            dwrPrefix =DWRModule.DWR_DEFAULT_PREFIX;
        }

        scriptName = LocalUtil.replace(scriptName, dwrPrefix, "");
        scriptName = LocalUtil.replace(scriptName, interfaceHandlerUrl, "");
        scriptName = LocalUtil.replace(scriptName, PathConstants.EXTENSION_JS, "");
        String path = request.getContextPath() + request.getServletPath();

        String script = remoter.generateInterfaceScript(scriptName, path);

        // Officially we should use MimeConstants.MIME_JS, but if we cheat and
        // use MimeConstants.MIME_PLAIN then it will be easier to read in a
        // browser window, and will still work just fine.
        response.setContentType(MimeConstants.MIME_PLAIN);
        PrintWriter out = response.getWriter();
        out.print(script);
    }

    /**
     * Setter for the remoter
     *
     * @param remoter
     */
    public void setRemoter(Remoter remoter) {
        this.remoter = remoter;
    }

    /**
     * The bean to execute remote requests and generate interfaces
     */
    protected Remoter remoter = null;

    /**
     * Setter for the URL that this handler available on
     *
     * @param interfaceHandlerUrl the interfaceHandlerUrl to set
     */
    public void setInterfaceHandlerUrl(String interfaceHandlerUrl) {
        this.interfaceHandlerUrl = interfaceHandlerUrl;
    }
}