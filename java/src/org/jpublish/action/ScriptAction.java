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
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.SiteContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileReader;

/**
 * An action which is implemented in a BSF supported scripting language.
 * Script actions have access to several varibles:
 * <p/>
 * <p>These are always available:</p>
 * <p/>
 * <p/>
 * <b>site</b> - The SiteContext<br>
 * <b>syslog</b> - Standard logging stream (Log4J Category)<br>
 * </p>
 * <p/>
 * <p>If there is a context defined when the action is executed (all
 * actions excluding startup actions):</p>
 * <p/>
 * <p/>
 * <b>context</b> - The current context<br>
 * <b>application</b> - The ServletContext<br>
 * <b>request</b> - The HTTP request<br>
 * <b>response</b> - The HTTP response<br>
 * <b>session</b> - The HTTP session<br>
 * <b>page</b> - The Page object<br>
 * </p>
 * <p/>
 * <p>If the action is executed with a configuration object then that object
 * will be included as <code>configuration</code>.</p>
 *
 * @author Anthony Eden
 * @author David Jones
 */

public class ScriptAction implements Action {

    private static Log log = LogFactory.getLog(ScriptAction.class);

    private SiteContext siteContext;
    private File script;
    private String scriptLang = null;
    private long timeLastLoaded = 0;
    private String scriptString = null;

    /**
     * Construct a new ScriptAction for the given script.  The path to the
     * script should be an absolute path.
     *
     * @param siteContext The SiteContext
     * @param script      The path to the script
     */

    public ScriptAction(SiteContext siteContext, String script) {
        this(siteContext, new File(script));
    }

    /**
     * Construct a new ScriptAction for the given script.
     *
     * @param siteContext The SiteContext
     * @param script      The file representing the script
     */

    public ScriptAction(SiteContext siteContext, File script) {
        this.siteContext = siteContext;
        this.script = script;
        if (log.isDebugEnabled())
            log.debug("Creating new ScriptAction for " + script.getName());
    }

    /**
     * Execute the action script represented by this ScriptAction.
     *
     * @param context       The current context
     * @param configuration The configuration object
     * @throws Exception
     */

    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Executing script: " + script);

        BSFManager bsfManager = new BSFManager();

        // expose standard items in the context
        if (context != null) {
            ServletContext application = (ServletContext) context.get("application");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpSession session = (HttpSession) context.get("session");
            Page page = (Page) context.get("page");

            // expose the context
            bsfManager.declareBean("context", context, JPublishContext.class);

            // expose the context.  This variable has been removed as of JP2.
            //bsfManager.declareBean("vc", context, JPublishContext.class);

            // expose the page object.
            if (page == null) {
                if (log.isDebugEnabled())
                    log.debug("Page request is null");
            } else {
                bsfManager.declareBean("page", page, Page.class);
            }

            // expose standard HttpServlet objects
            if (request == null) {
                if (log.isDebugEnabled())
                    log.debug("HTTP request is null");
            } else {
                bsfManager.declareBean("request", request, HttpServletRequest.class);
            }

            if (response == null) {
                if (log.isDebugEnabled())
                    log.debug("HTTP response is null");
            } else {
                bsfManager.declareBean("response", response, HttpServletResponse.class);
            }

            if (session == null) {
                if (log.isDebugEnabled())
                    log.debug("HTTP session is null");
            } else {
                bsfManager.declareBean("session", session, HttpSession.class);
            }

            if (application == null) {
                if (log.isDebugEnabled())
                    log.debug("ServletContext is null");
            } else {
                bsfManager.declareBean("application", application, ServletContext.class);
            }
        }

        // these objects are exposed regardless if there is a context
        // object or not.  In other words they are accesible to startup
        // actions
        bsfManager.declareBean("syslog", SiteContext.syslog, Log.class);

        if (siteContext == null) {
            if (log.isDebugEnabled())
                log.debug("SiteContext is null");
        } else {
            bsfManager.declareBean("site", siteContext, SiteContext.class);
        }

        if (configuration == null) {
            if (log.isDebugEnabled())
                log.debug("Configuration is null");
        } else {
            bsfManager.declareBean("configuration", configuration, Configuration.class);
        }

        // The following code was added by David Jones.  This will have
        // to be rewritten to use an ActionContent object for getting the
        // last modified time once I get to writing it. -AE
        if (scriptLang == null) {
            scriptLang = BSFManager.getLangFromFilename(script.getName());
        }

        boolean reloadScript = false;
        long scriptLastModified = script.lastModified();
        if (scriptLastModified > timeLastLoaded) {
            if (log.isDebugEnabled())
                log.debug("Loading updated or new script: " + script.getName());
            reloadScript = true;
        }

        if (reloadScript || scriptString == null) {
            synchronized (this) {
                if (reloadScript || scriptString == null) {
                    timeLastLoaded = System.currentTimeMillis();
                    scriptString = IOUtils.getStringFromReader(new FileReader(script));
                }
            }
        }
        try {
            UtilTimerStack.push(script.getName());
            bsfManager.exec(scriptLang, script.getCanonicalPath(), 0, 0, scriptString);
        } finally {
            UtilTimerStack.pop(script.getName());
        }
    }
}
