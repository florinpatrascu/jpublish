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
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.util.IOUtilities;
import com.atlassian.util.profiling.UtilTimerStack;
import com.atlassian.util.profiling.object.ObjectProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.util.*;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;
import org.jpublish.util.vfs.provider.filesystem.FileSystemProvider;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class which manages all actions in the JPublish framework.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class ActionManager {

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PATH = "path";

    private static Log log = LogFactory.getLog(ActionManager.class);

    private Map definedActions;
    private List classPathElements;
    private List startupActions;
    private List shutdownActions;
    private List globalActions;
    private List pathActions;
    private List preEvaluationActions;
    private List postEvaluationActions;
    private SiteContext siteContext;
    private VFSProvider provider;
    private JPublishContext startupContext;

    private Map cachedScriptActions = new HashMap();
    public static final String SCRIPT_ACTION = "ScriptAction";
    public static final String EXECUTE_METHOD_NAME = "execute";
    public static final String PATH_ACTION = "PathAction";

    /**
     * Construct a new ActionManager with the given SiteContext.
     *
     * @param siteContext The SiteContext
     */

    public ActionManager(SiteContext siteContext) {
        this.siteContext = siteContext;
        this.definedActions = new HashMap();
        this.startupActions = new ArrayList();
        this.shutdownActions = new ArrayList();
        this.globalActions = new ArrayList();
        this.pathActions = new ArrayList();
        this.preEvaluationActions = new ArrayList();
        this.postEvaluationActions = new ArrayList();
        this.classPathElements = new ArrayList();
        this.startupContext = new JPublishContext(null);

        // add the DateUtilities to the context
        this.startupContext.put(JPublishContext.JPUBLISH_DATE_UTILITIES, DateUtilities.getInstance());

        // add the NumberUtilities to the context
        this.startupContext.put(JPublishContext.JPUBLISH_NUMBER_UTILITIES, NumberUtilities.getInstance());

        // add the messages log to the context
        this.startupContext.put(JPublishContext.JPUBLISH_SYSLOG, SiteContext.syslog);

        // expose the SiteContext
        this.startupContext.put(JPublishContext.JPUBLISH_SITE, siteContext);

        //todo: What else do we need in the startupContext?

    }

    /**
     * Get a Map of all defined actions.
     *
     * @return A Map of defined actions
     */

    public Map getDefinedActions() {
        return definedActions;
    }

    /**
     * Add an action.
     *
     * @param name   The action name
     * @param action The Action implementation
     */

    public void addAction(String name, Action action) {
        definedActions.put(name, action);
    }

    /**
     * Remove an action.
     *
     * @param name The action name
     */

    public void removeAction(String name) {
        definedActions.remove(name);
    }

    /**
     * Get the class path used by script actions for loading classes.
     *
     * @return The class path
     */

    public synchronized String getClassPath() {
        StringBuffer buffer = new StringBuffer();
        Iterator classPathElements = getClassPathElements().iterator();
        while (classPathElements.hasNext()) {
            buffer.append(classPathElements.next().toString());
            if (classPathElements.hasNext()) {
                buffer.append(System.getProperty("path.separator"));
            }
        }
        return buffer.toString();
    }

    /**
     * Get a list of all elements in the ActionManager class path.
     *
     * @return The class path elements list
     */

    public List getClassPathElements() {
        return classPathElements;
    }

    /**
     * Get a List of startup actions.
     *
     * @return List of startup actions
     */

    public List getStartupActions() {
        return startupActions;
    }

    /**
     * Get a List of shutdown actions.
     *
     * @return List of shutdown actions
     */

    public List getShutdownActions() {
        return shutdownActions;
    }

    /**
     * Get a List of global actions.
     *
     * @return List of global actions
     */

    public List getGlobalActions() {
        return globalActions;
    }

    /**
     * Get the List of path actions.
     *
     * @return The path actions
     */

    public List getPathActions() {
        return pathActions;
    }

    /**
     * Get the List of actions which are executed immediately
     * upon receipt of any request.  These actions are
     * executed before a page search occurs.
     *
     * @return The pre-evaluation actions
     * @since 1.3
     */

    public List getPreEvaluationActions() {
        return preEvaluationActions;
    }

    /**
     * Get the List of actions which are executed after the
     * HTTP request has been completed, but before the response
     * is sent back to the client.
     *
     * @return The post-evaluation actions
     * @since 1.3
     */

    public List getPostEvaluationActions() {
        return postEvaluationActions;
    }

    /**
     * Execute all startup actions.
     *
     * @throws Exception Any Exception
     */

    public void executeStartupActions() throws Exception {
        if (log.isDebugEnabled())
            log.debug("Executing startup actions");

        Iterator actions = getStartupActions().iterator();
        while (actions.hasNext()) {
            ActionWrapper action = (ActionWrapper) actions.next();
            action.execute(this.startupContext);
        }
    }

    /**
     * Execute all shutdown actions.
     *
     * @throws Exception Any Exception
     */

    public void executeShutdownActions() throws Exception {
        if (log.isDebugEnabled())
            log.debug("Executing shutdown actions");

        Iterator actions = getShutdownActions().iterator();
        while (actions.hasNext()) {
            ActionWrapper action = (ActionWrapper) actions.next();
            action.execute(this.startupContext);
        }
    }

    /**
     * Execute all global actions using the given context.
     *
     * @param context The current context
     * @return Redirection URL or null
     * @throws Exception
     */

    public String executeGlobalActions(JPublishContext context) throws Exception {
        List globalActions = getGlobalActions();

        if (globalActions == null) {
            log.error("Global actions list is null");
            throw new NullPointerException("Global actions is null");
        }

        Iterator actions = globalActions.iterator();
        while (actions.hasNext()) {
            ActionWrapper action = (ActionWrapper) actions.next();

            if (action != null) {
                action.execute(context);
                String redirect = (String) context.get("redirect");
                if (redirect != null) {
                    return redirect;
                }

            } else {
                log.error("Action retrieved from iterator is null");
            }
        }
        return null;
    }

    /**
     * Execute the path actions with the given context.  If any of the
     * actions sets the value redirect in the context then that signals that
     * the servlet should redirect the request to the specified URL.
     *
     * @param path    The request path
     * @param context The current context
     * @return The redirect value or null
     * @throws Exception
     */

    public String executePathActions(String path, JPublishContext context) throws Exception {
        Iterator actions = getPathActions().iterator();
        while (actions.hasNext()) {
            ActionWrapper actionWrapper = (ActionWrapper) actions.next();
            PathAction action = (PathAction) actionWrapper.getAction();
            if (PathUtilities.match(path, action.getPath())) {
                actionWrapper.execute(context);

                String redirect = (String) context.get("redirect");
                if (redirect != null) {
                    return redirect;
                }
            }
        }
        return null;
    }

    /**
     * Execute pre-evaluation actions.  Pre-evaluation actions are only
     * executed if their path argument matches the current path.
     * <p/>
     * <p><b>Note:</b> Since these actions are executed prior to
     * locating the page the page variable is not in the context.
     * <p/>
     * <p>To stop processing and return immediately, set the value
     * <code>stop-processing</code> in the context to a non-null value.
     *
     * @param path    The request path
     * @param context The current request context
     * @return True if the processing should stop
     * @throws Exception
     * @since 1.3
     */

    public boolean executePreEvaluationActions(String path, JPublishContext context) throws Exception {
        Iterator actions = getPreEvaluationActions().iterator();
        while (actions.hasNext()) {
            ActionWrapper actionWrapper = (ActionWrapper) actions.next();
            PathAction action = (PathAction) actionWrapper.getAction();
            if (PathUtilities.match(path, action.getPath())) {
                actionWrapper.execute(context);

                String stopProcessingFlag = (String) context.get("stop-processing");
                if (stopProcessingFlag != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Execute post-evaluation actions.
     *
     * @param path    The request path
     * @param context The request context
     * @throws Exception
     * @since 1.3
     */

    public void executePostEvaluationActions(String path, JPublishContext context) throws Exception {
        Iterator actions = getPostEvaluationActions().iterator();
        while (actions.hasNext()) {
            ActionWrapper actionWrapper = (ActionWrapper) actions.next();
            PathAction action = (PathAction) actionWrapper.getAction();
            if (PathUtilities.match(path, action.getPath())) {
                actionWrapper.execute(context);

                //String redirect = (String)context.get("redirect");
                //if(redirect != null){
                //  return redirect;
                //}
            }
        }
    }

    /**
     * Execute the named action with the given context.  If the action sets
     * the value redirect in the context then that signals that the servlet
     * should redirect the request to the specified URL.
     *
     * @param name    The action name
     * @param context The current context
     * @return The redirect value or null
     * @throws Exception
     */

    public String execute(String name, JPublishContext context) throws Exception {
        return execute(name, context, null);
    }

    /**
     * Execute the named action with the given context.  If the action sets
     * the value redirect in the context then that signals that the servlet
     * should redirect the request to the specified URL.
     *
     * @param name          The action name
     * @param context       The current context
     * @param configuration The Configuration object
     * @return The redirect value or null
     * @throws Exception
     */

    public String execute(String name, JPublishContext context,
                          Configuration configuration) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Executing action: " + name);

        Action action = findAction(name);
        final String className = action.getClass().getName();
        boolean scriptActionWrapper = action.getClass().getName().indexOf(SCRIPT_ACTION) >= 0;

        if (SiteContext.getProfiling() && !scriptActionWrapper) {

            try {

                UtilTimerStack.push(className);
                Class[] paramTypes = {JPublishContext.class, Configuration.class};
                Object[] params = {context, configuration};
                Method execute = action.getClass().getMethod(EXECUTE_METHOD_NAME, paramTypes);
                ObjectProfiler.profiledInvoke(execute, action, params);

            } finally {
                UtilTimerStack.pop(className);
            }
        } else {
            action.execute(context, configuration);
        }

        String redirect = (String) context.get("redirect");
        if (redirect != null) {
            return redirect;
        }

        return null;
    }

    /**
     * Return the text for the specified script action.
     *
     * @param name The script name
     * @return The script action text
     * @throws IOException
     */

    public String getScriptActionText(String name) throws IOException {
        File actionRoot = siteContext.getRealActionRoot();
        File actionFile = new File(actionRoot, name);
        return FileCopyUtils.copyToString(new FileReader(actionFile));
    }

    /**
     * Set the text for the specified script action.
     *
     * @param name             The script action name
     * @param scriptActionText The script action text
     * @throws IOException
     */

    public void setScriptActionText(String name, String scriptActionText)
            throws IOException {
        File actionRoot = siteContext.getRealActionRoot();
        File actionFile = new File(actionRoot, name);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(actionFile));
            writer.print(scriptActionText);
        } finally {
            IOUtilities.close(writer);
        }
    }

    /**
     * Remove the named script action.
     *
     * @param name The name
     */

    public void removeScriptAction(String name) {
        File actionRoot = siteContext.getRealActionRoot();
        File actionFile = new File(actionRoot, name);
        actionFile.delete();
    }

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path) {
        File file = new File(siteContext.getRealActionRoot(), path);
        file.mkdirs();
    }

    /**
     * Remove the directory for the specified path.  The directory
     * must be empty.
     *
     * @param path The path
     * @throws Exception
     */

    public void removeDirectory(String path) throws Exception {
        if (log.isInfoEnabled())
            log.info("Remove directory: " + path);
        File file = new File(siteContext.getRealActionRoot(), path);
        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());
        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
    }

    /**
     * Get the Virtual File System root file.  The Virtual File System
     * provides a datasource-independent way of navigating through all
     * items known to the StaticResourceManager.
     *
     * @return The root VFSFile
     * @throws Exception
     */

    public VFSFile getVFSRoot() throws Exception {
        if (provider == null) {
            provider = new FileSystemProvider(siteContext.getRealActionRoot());
        }
        return provider.getRoot();
    }

    /**
     * Find an action with the given name.  The name may be the name of an
     * action registered with the ActionManager at startup, an action from a
     * module, a partial file path rooted in the action root directory or a
     * fully qualified Java class.
     *
     * @param name The name of the action
     * @return The action
     * @throws ActionNotFoundException If the action is not found
     */

    public Action findAction(String name) {
        // look in registered classes first
        if (log.isDebugEnabled())
            log.debug("Looking for registered action: " + name);
        Action action = (Action) definedActions.get(name);
        if (action != null) {
            return action;
        }

        // look in modules
        if (log.isDebugEnabled())
            log.debug("Looking for action in modules.");
        Iterator modules = siteContext.getModules().iterator();
        while (modules.hasNext()) {
            JPublishModule module = (JPublishModule) modules.next();
            if (module.getDefinedActions() != null && !module.getDefinedActions().isEmpty()) {
                action = (Action) (module.getDefinedActions().get(name));
                if (action != null) {
                    return action;
                }
            }
        }

        // look in the action directory for scripts
        action = (Action) cachedScriptActions.get(name);
        if (action != null) {
            if (log.isDebugEnabled())
                log.debug("Action: " + name + ", found in script actions cache.");
            return action;
        }

        if (log.isDebugEnabled())
            log.debug("Looking for action in action root.");

        File actionRoot = siteContext.getRealActionRoot();

        if (log.isDebugEnabled())
            log.debug("Action root: " + actionRoot);

        File actionFile = new File(actionRoot, name);
        if (actionFile.exists()) {
            if (log.isDebugEnabled())
                log.debug("Action found [" + actionFile + "]");
            action = new ScriptAction(siteContext, actionFile);
            cachedScriptActions.put(name, action);
            return action;
        }

        // look in classpath
        try {
            if (log.isDebugEnabled())
                log.debug("Looking for action in the classpath.");
            action = (Action) ClassUtilities.loadClass(name).newInstance();
            return action;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionNotFoundException(e, name);
        }

    }

    /**
     * Load the ActionManager's configuration from the given configuration
     * object.
     *
     * @param configuration The configuration object
     * @throws ConfigurationException
     */

    public void loadConfiguration(Configuration configuration) throws ConfigurationException {
        try {
            // load action definitions
            Iterator defineActionElements = configuration.getChildren("define-action").iterator();
            while (defineActionElements.hasNext()) {
                Configuration defineActionElement = (Configuration) defineActionElements.next();
                String name = defineActionElement.getAttribute(ATTRIBUTE_NAME);
                String className = defineActionElement.getAttribute("classname");
                Action action = (Action) ClassUtilities.loadClass(className).newInstance();
                if (log.isDebugEnabled())
                    log.debug("Defined action: " + name + " [" + className + "]");
                definedActions.put(name, action);
            }

            // load startup actions
            Iterator startupActionElements = configuration.getChildren("startup-action").iterator();
            while (startupActionElements.hasNext()) {
                Configuration startupActionElement =
                        (Configuration) startupActionElements.next();
                String name = startupActionElement.getAttribute(ATTRIBUTE_NAME);
                startupActions.add(new ActionWrapper(findAction(name),
                        startupActionElement));
            }

            // load shutdown actions
            Iterator shutdownActionElements = configuration.getChildren("shutdown-action").iterator();
            while (shutdownActionElements.hasNext()) {
                Configuration shutdownActionElement =
                        (Configuration) shutdownActionElements.next();
                String name = shutdownActionElement.getAttribute(ATTRIBUTE_NAME);
                shutdownActions.add(new ActionWrapper(findAction(name),
                        shutdownActionElement));
            }

            // load global actions
            if (log.isDebugEnabled())
                log.debug("Configuring global actions");

            Iterator globalActionElements = configuration.getChildren("global-action").iterator();
            while (globalActionElements.hasNext()) {
                Configuration globalActionElement =
                        (Configuration) globalActionElements.next();
                String name = globalActionElement.getAttribute(ATTRIBUTE_NAME);
                if (log.isDebugEnabled())
                    log.debug("Finding global action '" + name + "'");
                Action action = findAction(name);
                if (action == null) {
                    log.error("No action '" + name + "' found");
                } else {
                    if (log.isDebugEnabled())
                        log.debug("Action '" + name + "' found");
                    globalActions.add(new ActionWrapper(action,
                            globalActionElement));
                }
            }

            // load path actions
            Iterator pathActionElements = configuration.getChildren("path-action").iterator();
            while (pathActionElements.hasNext()) {
                Configuration pathActionElement =
                        (Configuration) pathActionElements.next();
                String name = pathActionElement.getAttribute(ATTRIBUTE_NAME);
                String path = pathActionElement.getAttribute(ATTRIBUTE_PATH);

                pathActions.add(new ActionWrapper(
                        new PathAction(path, findAction(name)), pathActionElement));
            }

            // load pre-evaluation actions
            Iterator preEvaluationActionElements = configuration.getChildren("pre-evaluation-action").iterator();
            while (preEvaluationActionElements.hasNext()) {
                Configuration preEvaluationActionElement = (Configuration) preEvaluationActionElements.next();
                String name = preEvaluationActionElement.getAttribute(ATTRIBUTE_NAME);
                String path = preEvaluationActionElement.getAttribute(ATTRIBUTE_PATH);

                preEvaluationActions.add(new ActionWrapper(
                        new PathAction(path, findAction(name)),
                        preEvaluationActionElement));
            }

            // load post-evaluation actions
            Iterator postEvaluationActionElements = configuration.getChildren("post-evaluation-action").iterator();
            while (postEvaluationActionElements.hasNext()) {
                Configuration postEvaluationActionElement =
                        (Configuration) postEvaluationActionElements.next();
                String name = postEvaluationActionElement.getAttribute(ATTRIBUTE_NAME);
                String path = postEvaluationActionElement.getAttribute(ATTRIBUTE_PATH);

                postEvaluationActions.add(new ActionWrapper(
                        new PathAction(path, findAction(name)),
                        postEvaluationActionElement));
            }
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Get an Iterator of paths of action scripts which are known to the
     * ActionManager.
     *
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths() throws Exception {
        return getPaths("");
    }

    /**
     * Get an Iterator of paths of action scripts which are known to the
     * ActionManager, starting from the specified base path.
     *
     * @param base The base path
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths(String base) throws Exception {
        File actionRoot = siteContext.getRealActionRoot();
        File baseFile = new File(actionRoot, base);
        return new FileToPathIterator(baseFile.toString(),
                new BreadthFirstFileTreeIterator(baseFile));
    }

}
