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
import org.directwebremoting.servlet.HttpConstants;
import org.directwebremoting.servlet.PathConstants;
import org.directwebremoting.util.LocalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * update: [2008.06.16] - added last modified header and
 * check if modified since header, implementation inspired from:
 * http://blogs.dekoh.com/dev/2007/07/24/caching-dwr-interface-java-script-files/
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version $Revision:$ Apr 5, 2007, 10:43:43 AM
 */
public class DWRInterfaceHandler implements Handler {
    //Store the application startup time. This will be the time we will set
    //as the Last-Modified time for all the interface scripts
    private final long lastUpdatedTime = (System.currentTimeMillis() / 1000) * 1000;

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
            dwrPrefix = DWRModule.DWR_DEFAULT_PREFIX;
        }

        long ifModifiedSince = request.getDateHeader(HttpConstants.HEADER_IF_MODIFIED);
        if (ifModifiedSince < lastUpdatedTime) {
            //If the browser does not have the script in the cache or the cached copy is stale
            //set the Last-Modified date header and send the new script file
            //Note: If the browser does not have the script in its cache ifModifiedSince will be -1

            scriptName = LocalUtil.replace(scriptName, dwrPrefix, DWRProcessor.EMPTY_STRING);
            scriptName = LocalUtil.replace(scriptName, interfaceHandlerUrl, DWRProcessor.EMPTY_STRING);
            scriptName = LocalUtil.replace(scriptName, PathConstants.EXTENSION_JS, DWRProcessor.EMPTY_STRING);
            String path = request.getContextPath() + request.getServletPath();

            String script = remoter.generateInterfaceScript(scriptName, path);

            // Officially we should use MimeConstants.MIME_JS, but if we cheat and
            // use MimeConstants.MIME_PLAIN then it will be easier to read in a
            // browser window, and will still work just fine.
            //response.setContentType(MimeConstants.MIME_PLAIN);

            // [Florin] I believe the best way is to let the web admin to decide about the MIME. In our
            // case the control is delegated via the mime-mapping options from jpublish.xml
            response.setDateHeader(HttpConstants.HEADER_LAST_MODIFIED, lastUpdatedTime);
            PrintWriter out = response.getWriter();
            out.print(script);
        } else {
            //If the browser has current version of the file, dont send the script. Just say it has not changed
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
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