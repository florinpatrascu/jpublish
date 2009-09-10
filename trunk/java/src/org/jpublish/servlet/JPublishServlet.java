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

package org.jpublish.servlet;

import org.apache.bsf.BSFManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.*;
import org.jpublish.action.ActionManager;
import org.jpublish.component.ComponentMap;
import org.jpublish.page.PageInstance;
import org.jpublish.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

/**
 * This class is the entry point for requests to the JPublish framework.
 * The servlet should be configured to handler all requests for its context.
 * In addition, a init-parameter called config must be included in the
 * <code>web.xml</code> file.  The value of this parameter must be the
 * full path to the site's configuration file.
 * <p/>
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class JPublishServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(JPublishServlet.class);

    private SiteContext siteContext;
    public static final String JPUBLISH_CONTEXT = "jpublishContext";
    private boolean formatParameterSupported = false;
    private static final int INITIAL_BUFFER_SIZE = 4 * 1024;


    /**
     * Initialize the servlet.
     *
     * @param servletConfig The Servlet configuration
     * @throws ServletException
     */

    public void init(ServletConfig servletConfig) throws ServletException {

        super.init(servletConfig);

        log.info("Initializing JPublish servlet; " + SiteContext.JPUBLISH_VERSION);

        ServletContext servletContext = getServletContext();

        String configLocation = servletConfig.getInitParameter("config");
        if (log.isDebugEnabled())
            log.debug("Config location: " + configLocation);

        // find the WEB-INF root
        String rootDir = servletContext.getRealPath("/");
        File contextRoot = new File(rootDir);
        File webInfPath = new File(contextRoot, "WEB-INF");

        // create the site context
        try {
            siteContext = new SiteContext(contextRoot, configLocation);
            servletContext.setAttribute(SiteContext.NAME, siteContext);
            siteContext.setWebInfPath(webInfPath);
            siteContext.setServletContext(servletContext);
            siteContext.setServletConfig(servletConfig);
            siteContext.setJPublishServlet(this);
            siteContext.init();
            formatParameterSupported = siteContext.getFormatChangeParameterName() != null
                    && siteContext.getFormatChangeParameterName().trim().length() > 0;
        } catch (Exception e) {
            log.error("Error creating SiteContext: " + e.getMessage());
            throw new ServletException(e);
        }

        // set the ActionManager classpath
        ActionManager actionManager = siteContext.getActionManager();

        // configure BSF
        configureBSF();

        // construct the classpath for scripting support
        String classPath = constructClasspath(webInfPath);

        // configure classpath
        configureClasspath(classPath);

        // execute startup actions
        try {
            actionManager.executeStartupActions();
        } catch (Exception e) {
            log.error("Error executing startup actions: " + e.getMessage());
            throw new ServletException(e);
        }

        log.info("JPublish servlet initialized.");
    }

    /**
     * Called when the Servlet is destroyed.
     */

    public void destroy() {
        // execute shutdown actions
        try {
            ActionManager actionManager = siteContext.getActionManager();
            actionManager.executeShutdownActions();
        } catch (Exception e) {
            log.error("Error executing shutdown actions: " + e.getMessage());
            e.printStackTrace();
        }

        // destroy all modules
        try {
            Iterator modules = siteContext.getModules().iterator();
            while (modules.hasNext()) {
                ((JPublishModule) modules.next()).destroy();
            }
        } catch (Exception e) {
            log.error("Error destroying modules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the HTTP request method is GET.  This method just calls the
     * doPost() method.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @throws ServletException
     * @throws IOException
     */

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    public void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doPost(httpServletRequest, httpServletResponse);
    }

    public void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doPost(httpServletRequest, httpServletResponse);
    }

    /**
     * Called when the HTTP request method is POST.  This method provides the
     * main control logic.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @throws ServletException
     * @throws IOException
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();

        String pathInfo = request.getPathInfo();
        if (log.isDebugEnabled())
            log.debug("Path info: " + pathInfo);

        //OLAT: PATCH BEGIN
        if (pathInfo == null) {
            // we have a direct mapping rule in web.xml like /kurs/bridge.html -> jpublish servlet
            // so we use servletPath
            //String servletPath = request.getServletPath();
            pathInfo = request.getServletPath();
        } //TODO: make more generic (use JPublish with other servlets..) and post it.

        if (log.isDebugEnabled())
            log.debug("calculated Path info: " + pathInfo);
        //OLAT: PATCH END

        String contextPath = request.getContextPath();

        if (log.isDebugEnabled())
            log.debug("Context path: " + contextPath);

        // get the real path
        String path = getRealPath(pathInfo);

        if (siteContext.getPathDispatcherManager().canDispatch(path)) {

            //only one from below ones should react
            String dispatcherName = siteContext.getPathDispatcherManager().canForwardDispatch(path);

            if (dispatcherName != null) {
                siteContext.getPathDispatcherManager().forward(request, response, dispatcherName);
            }

            dispatcherName = siteContext.getPathDispatcherManager().canIncludeDispatch(path);
            if (dispatcherName != null) {
                siteContext.getPathDispatcherManager().include(request, response, dispatcherName);
            }

            return;
        }

        if (log.isDebugEnabled())
            log.debug("Real path: " + path);

        // create the session if enabled
        HttpSession session = null;

        if (isSessionEnabled(path)) {
            session = request.getSession(true);
        }

        // get the character encoding map
        CharacterEncodingMap characterEncodingMap =
                siteContext.getCharacterEncodingManager().getMap(path);

        // set the request character encoding for parameter data
        if (requireVersion(2, 3)) {
            request.setCharacterEncoding(characterEncodingMap.getRequestEncoding());
        }


        // put standard servlet stuff into the context
        JPublishContext context = new JPublishContext(this);
        context.put("request", request);
        context.put("response", response);
        context.put("session", session);
        context.put("application", servletContext);

        // add the character encoding map to the context
        context.put("characterEncodingMap", characterEncodingMap);

        // add the URLUtilities to the context
        URLUtilities urlUtilities = new URLUtilities(request, response);
        context.put("urlUtilities", urlUtilities);
        // deprecated
        context.put("url_util", urlUtilities);
        context.put("url_utils", urlUtilities);

        // add the DateUtilities to the context
        context.put("dateUtilities", DateUtilities.getInstance());

        // add the NumberUtilities to the context
        context.put("numberUtilities", NumberUtilities.getInstance());

        // add the messages log to the context
        context.put("syslog", SiteContext.syslog);

        // expose the SiteContext
        context.put("site", siteContext);

        // expose the context itself for debugging purposes
        if (siteContext.isDebug()) {
            context.put("context", context);
        }

        // switch from merge to eval, while rendering Velocity templates [hack for utf8]
        if (siteContext.isEval()) {
            context.put("evaluateVelocityTemplates", "true");
        }

        if (siteContext.isProtectReservedNames()) {
            context.enableCheckReservedNames(this);
        }

        // add the repositories to the context
        Iterator repositories = siteContext.getRepositories().iterator();
        while (repositories.hasNext()) {
            Repository repository = (Repository) repositories.next();
            if (log.isDebugEnabled())
                log.debug("Adding " + repository.getClass().getName() + " as " + repository.getName());

            context.put(repository.getName(), new RepositoryWrapper(repository, context));
        }

        Writer out = null;
        try {

            if (executePreEvaluationActions(request, response, context, path))
                return;

            if (context.getStopProcessing() != null) {
                return;
            }

            // if the page is static
            StaticResourceManager staticResourceManager = siteContext.getStaticResourceManager();

            if (log.isDebugEnabled())
                log.debug("Checking if static resource exists: " + path);

            if (staticResourceManager.resourceExists(path)) {
                // execute the global actions
                if (executeGlobalActions(request, response, context, path))
                    return;

                if (context.getStopProcessing() != null) return;

                // execute path actions
                if (executePathActions(request, response, context, path)) return;
                if (context.getStopProcessing() != null) return;

                // execute parameter actions
                if (executeParameterActions(request, response, context, path))
                    return;
                if (context.getStopProcessing() != null) return;

                // load and return the static resource
                if (log.isDebugEnabled())
                    log.debug("Loading static resource");

                OutputStream o = response.getOutputStream();
                setResponseContentType(request, response, path, characterEncodingMap);
                response.setDateHeader("Last-Modified", staticResourceManager.getLastModified(path));
                response.setContentLength((int) staticResourceManager.getContentLength(path));

                try {
                    staticResourceManager.load(path, o);
                } catch (SocketException e) {
                    log.warn("Error writing to output stream: " + e.getMessage());
                }
                // OLAT: PATCH: Ignore org.apache.catalina.connector.ClientAbortException
                // that is produced by InternetExplorer (6.0) browser caching.
                catch (IOException e) {
                    if (e instanceof FileNotFoundException) {
                        throw e;
                    }
                }
                return;
            } else {
                if (log.isDebugEnabled())
                    log.debug("Static resource '" + path + "' not found");
            }

            // load the page
            if (log.isDebugEnabled())
                log.debug("Loading the page.");

            PageInstance pageInstance = siteContext.getPageManager().getPage(path);
            Page page = new Page(pageInstance);

            context.disableCheckReservedNames(this);

            // expose the page in the context
            context.put("page", page);

            // expose components in the context
            context.put("components", new ComponentMap(context));

            if (siteContext.isProtectReservedNames()) {
                context.enableCheckReservedNames(this);
            }

            request.setAttribute(JPUBLISH_CONTEXT, context);

            // execute the global actions
            if (executeGlobalActions(request, response, context, path)) return;
            if (context.getStopProcessing() != null) return;

            // execute path actions
            if (executePathActions(request, response, context, path)) return;
            if (context.getStopProcessing() != null) return;

            // execute parameter actions
            if (executeParameterActions(request, response, context, path))
                return;
            if (context.getStopProcessing() != null) return;

            // execute the page actions
            if (log.isDebugEnabled())
                log.debug("Executing page actions.");

            if (optionalRedirect(page.executeActions(context), path, response))
                return;
            if (context.getStopProcessing() != null) {
                return;
            }
            setResponseContentType(request, response, path, characterEncodingMap);

            // get the template
            // OLAT: PATCH using context.getPage() instead of page object
            // since page can be changed in internal forward and page points
            // still to the original page
            Template template = siteContext.getTemplateManager().getTemplate(
                    context.getPage().getFullTemplateName());

            // merge the template
            if (log.isDebugEnabled()){
                log.debug("Merging with template " + template.getPath());
            }
            Writer writer = new StringWriter();
            template.merge(context, context.getPage(), writer);
            String mergedContent = writer.toString();

            response.setContentLength(mergedContent.getBytes().length);
            FileCopyUtils.copy(mergedContent, response.getWriter());

        } catch (FileNotFoundException e) {
            log.error("[404] " + path);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
            }
        } catch (Exception e) {
            // Allow Jetty RequestRetry exception to propogate to container!
            if ("org.mortbay.jetty.RetryRequest".equals(e.getClass().getName())) {
                throw (RuntimeException) e;
            }

            JPublishError error = new JPublishError(e, context);
            Iterator errorHandlers = siteContext.getErrorHandlers(path).iterator();
            while (errorHandlers.hasNext()) {
                ((ErrorHandler) errorHandlers.next()).handleError(error);
            }

            if (!error.isConsumed()) {
                log.error("Execution error: " + MessageUtilities.format(e.getMessage()));
                if (!response.isCommitted()) {
                    throw new ServletException(e);
                }
            }
        } finally {
            try {
                executePostEvaluationActions(request, response, context, path);
            } catch (Exception e) {
                log.error("Error executing post evaluation actions: " +
                        MessageUtilities.format(e.getMessage()));
            }
        }
    }

    private void setResponseContentType(HttpServletRequest request, HttpServletResponse response, String path, CharacterEncodingMap characterEncodingMap) {
        // set the response content type
        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            String extension = path.substring(lastDotIndex + 1);
            //introduce the format parameter
            String format = null;
            if (formatParameterSupported) {
                format = request.getParameter(siteContext.getFormatChangeParameterName());
            }

            if (format == null || format.trim().length() == 0) {
                format = extension; // fall -back on the filename extension
            }

            String mimeType = siteContext.getMimeTypeMap().getMimeType(format);
            String contentType = getMimeTypeWithCharset(mimeType,
                    characterEncodingMap.getResponseEncoding());
            response.setContentType(contentType);

            if (log.isDebugEnabled())
                log.debug("Content type for extension " + format + " is " + contentType);
        } else {
            response.setContentType(getMimeTypeWithCharset(MimeTypeMap.DEFAULT_MIME_TYPE,
                    characterEncodingMap.getResponseEncoding()));

            if (log.isDebugEnabled())
                log.debug("No extension found, using default content type.");
        }
    }

    /**
     * Configure the classpath for use by scripting languages.  This will
     * add the WEB-INF/classes directory and all JAR and ZIP files in the
     * WEB-INF/lib directory to the classpath.
     *
     * @param classPath
     */
    protected void configureClasspath(String classPath) {
        AccessController.doPrivileged(new SetClassPathAction(classPath));
        //System.setProperty("java.class.path", classPath.toString());
    }

    protected String constructClasspath(File webInfPath) {
        File webLibPath = new File(webInfPath, "lib");
        File webClassPath = new File(webInfPath, "classes");

        // add WEB-INF/classes to the classpath
        StringBuffer classPath = new StringBuffer();
        classPath.append(System.getProperty("java.class.path"));

        if (webClassPath.exists()) {
            classPath.append(System.getProperty("path.separator"));
            classPath.append(webClassPath);
        }

        // add WEB-INF/lib files to the classpath
        if (webLibPath.exists()) {
            File[] files = webLibPath.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().toLowerCase().endsWith(".jar") ||
                        files[i].getName().toLowerCase().endsWith(".zip")) {
                    classPath.append(System.getProperty("path.separator"));
                    classPath.append(files[i]);
                }
            }
        }
        if (log.isDebugEnabled())
            log.debug("Classpath used: " + classPath.toString());
        return classPath.toString();
    }

    private void configureBSF() {
        if (log.isDebugEnabled())
            log.debug("Adding Beanshell to BSF");
        String[] beanshellExtensions = {"bsh"};
        BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", beanshellExtensions);

        if (log.isDebugEnabled())
            log.debug("Adding Groovy to BSF");
        String[] groovyExtensions = {"groovy", "gy"};
        BSFManager.registerScriptingEngine("groovy", "org.codehaus.groovy.bsf.GroovyEngine", groovyExtensions);

        if (log.isDebugEnabled())
            log.debug("Adding Ruby to BSF");
        String[] rubyExtensions = {"ruby", "rb"};
        BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", rubyExtensions);
    }

    /**
     * Execute the gloabl actions for the current request.
     *
     * @param request  The HttpServletRequest
     * @param response The HttpServletResponse
     * @param context  The current context
     * @param path     The request path
     * @return True if the result is a redirect
     * @throws Exception
     */

    protected boolean executeGlobalActions(HttpServletRequest request,
                                           HttpServletResponse response,
                                           JPublishContext context,
                                           String path)
            throws Exception {

        ActionManager actionManager = siteContext.getActionManager();
        if (log.isDebugEnabled())
            log.debug("Executing global actions.");
        return optionalRedirect(actionManager.executeGlobalActions(context), path, response);
    }

    /**
     * Execute the path actions for the current request.
     *
     * @param request  The HttpServletRequest
     * @param response The HttpServletResponse
     * @param context  The current context
     * @param path     The request path
     * @return True if the result is a redirect
     * @throws Exception
     */

    protected boolean executePathActions(HttpServletRequest request,
                                         HttpServletResponse response,
                                         JPublishContext context,
                                         String path)
            throws Exception {

        ActionManager actionManager = siteContext.getActionManager();
        if (log.isDebugEnabled())
            log.debug("Executing path actions.");
        return optionalRedirect(actionManager.executePathActions(path, context), path, response);
    }

    /**
     * Execute the parameter actions for the current request.
     *
     * @param request  The HttpServletRequest
     * @param response The HttpServletResponse
     * @param context  The current context
     * @param path     The request path
     * @return True if the result is a redirect
     * @throws Exception
     */

    protected boolean executeParameterActions(HttpServletRequest request,
                                              HttpServletResponse response,
                                              JPublishContext context,
                                              String path)
            throws Exception {

        if (!siteContext.isParameterActionsEnabled()) {
            return false;
        }

        ActionManager actionManager = siteContext.getActionManager();
        if (log.isDebugEnabled())
            log.debug("Executing parameter actions.");

        String[] actionNames = request.getParameterValues(siteContext.getActionIdentifier());
        if (actionNames != null && actionNames.length > 0) {
            for (int i = 0; i < actionNames.length; i++) {
                if (log.isDebugEnabled())
                    log.debug("Executing paramater action: " + actionNames[i]);
                if (optionalRedirect(actionManager.execute(actionNames[i], context), path, response))
                    return true;
            }
        }
        return false;
    }

    /**
     * Execute pre-evaluation action.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param context  The current JPublish context
     * @param path     The request path
     * @return True if processing should stop
     * @throws Exception
     * @since 1.3
     */

    protected boolean executePreEvaluationActions(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  JPublishContext context,
                                                  String path)
            throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        if (log.isDebugEnabled())
            log.debug("Executing pre-evaluation actions.");
        return actionManager.executePreEvaluationActions(path, context);
    }

    /**
     * Execute post-evaluation action.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param context  The current JPublish context
     * @param path     The request path
     * @return True if processing should stop
     * @throws Exception
     * @since 1.3
     */

    protected boolean executePostEvaluationActions(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   JPublishContext context,
                                                   String path)
            throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        if (log.isDebugEnabled())
            log.debug("Executing post-evaluation actions.");
        actionManager.executePostEvaluationActions(path, context);
        return false;
    }

    /**
     * Get the "real" path for the given request path.  This method will
     * append the default page to the request path if the path does not
     * include a suffix.
     *
     * @param path The request path
     * @return The real path
     */

    private String getRealPath(String path) {
        if (path == null) {
            path = "";
        }

        if (path.lastIndexOf(".") == -1) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            path = path + siteContext.getDefaultPage();
        }

        return path;
    }

    /**
     * Redirect if the <code>redirect</code> argument is not null.  The
     * redirect argument is obtained from the context using the key "redirect".
     *
     * @param redirect The redirect path
     * @param path     The request path
     * @param response The HTTP response object
     * @return
     * @throws IOException
     */

    private boolean optionalRedirect(String redirect, String path,
                                     HttpServletResponse response) throws IOException {
        if (log.isDebugEnabled())
            log.debug("Optional redirect: " + redirect);

        if (redirect == null) {
            return false;
        }

        response.sendRedirect(redirect);
        return true;
/*
        if (redirect.endsWith("/")) {
            //log.debug("Redirect ends with a '/'");
            response.sendRedirect(redirect);
            return true;
        }

        if (redirect.lastIndexOf(".") == -1) {
            response.sendRedirect(redirect +
                    path.substring(path.lastIndexOf(".")));
            return true;
        } else {
            response.sendRedirect(redirect);
            return true;
        }
*/
    }

    /**
     * Append the charset to the given mime type.
     *
     * @param mimeType The mime type
     * @param charSet  The character set
     * @return The full mimetype + character set
     */

    private String getMimeTypeWithCharset(String mimeType, String charSet) {
        // currently the charset is appended to all mime types.  This could
        // cause problems for non-text mime types.  Thus if the problem
        // does manifest in the future it should be possible to specify
        // which mime types support character sets in the config. -AE

        StringBuffer buffer = new StringBuffer();
        buffer.append(mimeType).append("; charset=").append(charSet);
        return buffer.toString();
    }

    /**
     * Return true if the ServletContext major and minor version are greater
     * than or equal to the given arguments.
     *
     * @param majorVersion The required major version
     * @param minorVersion The required minor version
     * @return True if the version is equal to or greater than the given args
     */

    private boolean requireVersion(int majorVersion, int minorVersion) {
        ServletContext servletContext = getServletContext();
        return ((servletContext.getMajorVersion() > majorVersion) ||
                (servletContext.getMajorVersion() == majorVersion &&
                        servletContext.getMinorVersion() >= minorVersion)
        );
    }

    /**
     * @return the siteContext aka "site"
     */
    public SiteContext getSiteContext() {
        return siteContext;
    }

    /**
     * Return true if session is enabled for the specified path.
     *
     * @param path The path
     * @return True if session is enabled
     */

    private boolean isSessionEnabled(String path) {
        Iterator iter = siteContext.getDisableSessionPaths().iterator();
        while (iter.hasNext()) {
            if (PathUtilities.match(path, iter.next().toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Privleged action for setting the class path.  This is used to get around
     * the Java security system to set the class path so scripts have full
     * access to all loaded Java classes.
     * <p/>
     * <p>Note: This functionality is untested.</p>
     *
     * @author Anthony Eden
     */

    class SetClassPathAction implements PrivilegedAction {

        /**
         * Construct the action to set the class path.
         *
         * @param classPath The new class path
         */

        public SetClassPathAction(String classPath) {
            this.classPath = classPath;
        }

        /**
         * Set the "java.class.path" property.
         *
         * @return Returns null
         */

        public Object run() {
            System.setProperty("java.class.path", classPath);
            return null; // nothing to return
        }

        private String classPath;

    }

}
