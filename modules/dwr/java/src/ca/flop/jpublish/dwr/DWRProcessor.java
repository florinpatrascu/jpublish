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
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.extend.ServerLoadMonitor;
import org.directwebremoting.impl.ContainerUtil;
import org.directwebremoting.impl.DefaultContainer;
import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.servlet.PathConstants;
import org.directwebremoting.util.Logger;
import org.directwebremoting.util.ServletLoggingOutput;
import org.directwebremoting.util.VersionUtil;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 1, 2006 3:24:22 PM)
 */
public class DWRProcessor {

    /**
     * Our IoC container
     */
    protected DefaultContainer container;

    /**
     * The processor will actually handle the http requests
     */
    protected DWRUrlProcessor processor;

    /**
     * The WebContext that keeps http objects local to a thread
     */
    protected WebContextFactory.WebContextBuilder webContextBuilder;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DWRProcessor.class);

    /* (non-Javadoc)
    * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
    */

    private SiteContext site;
    private FakeServletConfig fakeServletConfig;

    public void init(SiteContext site) throws ServletException {

        this.site = site;
        fakeServletConfig = new FakeServletConfig(site.getServletContext());
        fakeServletConfig.setInitParameter("config", (String) site.getAttribute("dwrConfigFile"));

        Map dwrServletInitParamsMap = (Map) site.getAttribute("dwrServletInitParamsMap");
        if (dwrServletInitParamsMap != null) {
            Set keys = dwrServletInitParamsMap.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                fakeServletConfig.setInitParameter(key, (String) dwrServletInitParamsMap.get(key));
            }
        }

        try {
            // Setup logging
            ServletLoggingOutput.setExecutionContext(site.getJPublishServlet());
            ServletConfig config = site.getServletConfig();
            String logLevel = config.getInitParameter(ContainerUtil.INIT_LOGLEVEL);
            if (logLevel != null) {
                ServletLoggingOutput.setLevel(logLevel);
            }
            log.info("DWR Version " + VersionUtil.getVersion() + " starting."); //$NON-NLS-1$ //$NON-NLS-2$

            container = ContainerUtil.createDefaultContainer(fakeServletConfig);
            ContainerUtil.setupDefaultContainer(container, fakeServletConfig);
            // overwrite the interface and the test handler with a clone of Joe's code but where
            // the requestPath will obey to the dwrPrefix. I have to find a better solution [florin]
            container.addParameter(PathConstants.URL_PREFIX + "/interface/", DWRInterfaceHandler.class.getName());
            container.addParameter(PathConstants.URL_PREFIX + "/test/", DWRTestHandler.class.getName());
            container.addParameter(DWRUrlProcessor.class.getName(), DWRUrlProcessor.class.getName());
            container.setupFinished();

            webContextBuilder =
                    StartupUtil.initWebContext(fakeServletConfig,
                            site.getServletContext(), container);

            StartupUtil.initServerContext(fakeServletConfig, site.getServletContext(), container);
            ContainerUtil.prepareForWebContextFilter(
                    site.getServletContext(), fakeServletConfig,
                    container, webContextBuilder, site.getJPublishServlet());

            ContainerUtil.configureContainerFully(container, fakeServletConfig);
            ContainerUtil.publishContainer(container, fakeServletConfig);
        }
        catch (ExceptionInInitializerError ex) {
            log.fatal("ExceptionInInitializerError. Nested exception:", ex.getException());
            throw new ServletException(ex);
        }
        catch (Exception ex) {
            log.fatal("DwrServlet.init() failed", ex);
            throw new ServletException(ex);
        }
        finally {
            if (webContextBuilder != null) {
                webContextBuilder.unset();
            }

            ServletLoggingOutput.unsetExecutionContext();
        }

    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void execute(JPublishContext context, Configuration configuration) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        try {

            webContextBuilder.set(
                    request, response, fakeServletConfig, site.getServletContext(), container);
            ServletLoggingOutput.setExecutionContext(site.getJPublishServlet());

            DWRUrlProcessor processor = (DWRUrlProcessor) container.getBean(DWRUrlProcessor.class.getName());

            processor.handle(context, configuration);
        }
        finally {
            webContextBuilder.unset();
            ServletLoggingOutput.unsetExecutionContext();
        }
    }

    /**
     * Kill all comet polls.
     * <p>Technically a servlet engine ought to call this only when all the
     * threads are already removed, however at least Tomcat doesn't do this
     * properly (it waits for a while and then calls destroy anyway).
     * <p>It would be good if we could get destroy() to call this
     * method however destroy() is only called once all threads are done so it's
     * too late.
     */
    public void shutdown() {
        ServerLoadMonitor monitor = (ServerLoadMonitor) container.getBean(ServerLoadMonitor.class.getName());
        monitor.shutdown();
    }


}
