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

package org.jpublish;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.resource.FileResourceLoader;
import com.anthonyeden.lib.resource.ResourceException;
import com.anthonyeden.lib.resource.ResourceRecipient;
import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.action.ActionManager;
import org.jpublish.repository.RepositoryContent;
import org.jpublish.template.TemplateContent;
import org.jpublish.util.*;
import org.jpublish.view.ViewRenderer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * This class contains configuration information for a particular
 * site.  Pages are loaded and actions are executed within the
 * context of the site and have access to all of the methods within
 * this class.
 * <p/>
 * <p>Instances of the SiteContext class will also reload themselves
 * automatically when the underlying configuration file changes.</p>
 * <p/>
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class SiteContext implements ResourceRecipient {
    private static final Log log = LogFactory.getLog(SiteContext.class);
    public static final Log syslog = LogFactory.getLog("syslog");
    public static final String JPUBLISH_VERSION = "4.0";
    public static final String NAME = "jpublishSiteContext";

    public static final String DEFAULT_PAGE_MANAGER = "org.jpublish.page.filesystem.FileSystemPageManager";
    public static final String DEFAULT_TEMPLATE_MANAGER = "org.jpublish.template.filesystem.FileSystemTemplateManager";
    public static final String DEFAULT_STATIC_RESOURCE_MANAGER = "org.jpublish.resource.filesystem.FileSystemStaticResourceManager";
    public static final String DEFAULT_VIEW_RENDERER = "org.jpublish.view.velocity.VelocityViewRenderer";
    public static final String DEFAULT_COMPONENT_MANAGER = "org.jpublish.component.InMemoryComponentManager";
    public static final String DEFAULT_PAGE_ROOT = "pages";
    public static final String DEFAULT_TEMPLATE_ROOT = "templates";
    public static final String DEFAULT_ACTION_ROOT = "actions";
    public static final String DEFAULT_STATIC_ROOT = "static";
    public static final String DEFAULT_ACTION_IDENTIFIER = "action";
    public static final String DEFAULT_PAGE = "index.html";
    public static final String DEFAULT_TEMPLATE = "basic";
    public static final String DEFAULT_PAGE_SUFFIX = ".xml";
    public static final String FILE_SYSTEM_SEPARATOR = System.getProperty("file.separator");


    private static final String ATTRIBUTE_CLASSNAME = "classname";

    private File configurationFile;

    private File root;
    private File contextRoot;
    private String pageSuffix = DEFAULT_PAGE_SUFFIX;

    private File pageRoot;
    private File templateRoot;
    private File actionRoot;
    private File staticRoot;
    private File webInfPath;
    private ServletContext servletContext;
    private String actionIdentifier;
    private String defaultPage = DEFAULT_PAGE;
    private String defaultTemplate = DEFAULT_TEMPLATE;
    private boolean protectReservedNames = false;
    private boolean parameterActionsEnabled = false;
    private boolean debug = false;
    private List modules;
    private List repositories;
    private List defaultErrorHandlers;
    private List disableSessionPaths;
    private Map errorHandlerMap;
    private Map cachedErrorHandlers;

    private PathDispatcherManager pathDispatcherManager;
    private JPublishCacheManager jPublishCacheManager;

    private PageManager pageManager;
    private ActionManager actionManager;
    private TemplateManager templateManager;
    private StaticResourceManager staticResourceManager;
    private ViewRenderer viewRenderer;
    private ComponentManager componentManager;

    private MimeTypeMap mimeTypeMap;
    private CharacterEncodingManager characterEncodingManager;

    private String evaluateVelocityTemplates;
    private Map attributes;
    private ServletConfig servletConfig;
    private HttpServlet jPublishServlet;

    /**
     * Construct a new SiteContext using the given File to load the
     * context's configuration.
     *
     * @param contextRoot Get the application context root
     * @param configPath  JPublish configuration file path
     * @throws Exception an Exception is thrown if anything goes wrong
     */

    public SiteContext(File contextRoot, String configPath) throws Exception {
        setContextRoot(contextRoot);

        File configurationFile = new File(configPath);
        if (!configurationFile.isAbsolute()) {
            configurationFile = new File(contextRoot, configurationFile.getPath());
        }

        this.configurationFile = configurationFile;

        modules = new ArrayList();
        repositories = new ArrayList();
        defaultErrorHandlers = new ArrayList();
        errorHandlerMap = new HashMap();
        cachedErrorHandlers = new HashMap();
        mimeTypeMap = new MimeTypeMap();
        characterEncodingManager = new CharacterEncodingManager();

        disableSessionPaths = new ArrayList();

        attributes = new HashMap();
    }

    public void init() throws ResourceException {
        FileResourceLoader fileResourceLoader = new FileResourceLoader();
        fileResourceLoader.loadResource(configurationFile.getAbsolutePath(), this);
    }

    /**
     * Get the configuration File.
     *
     * @return The configuration File
     */

    public File getConfigurationFile() {
        return configurationFile;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getPageSuffix() {
        return pageSuffix;
    }

    public void setPageSuffix(String pageSuffix) {
        this.pageSuffix = pageSuffix;
    }

    /**
     * Return the root directory.  This directory may be used as
     * a base path for all of the other directories required by
     * JPublish.  If the root is not set then this method will
     * return the context root.
     *
     * @return The root directory
     */

    public File getRoot() {
        if (root == null) {
            if (log.isDebugEnabled())
                log.debug("Root is null - using context root instead");
            return getContextRoot();
        }
        return root;
    }

    /**
     * Set the root directory.
     *
     * @param root The new root directory
     */

    public void setRoot(File root) {
        this.root = root;
    }

    /**
     * Set the root directory.
     *
     * @param root The new root directory
     */

    public void setRoot(String root) {
        if (root != null) {
            setRoot(new File(root));
        }
    }

    /**
     * Get the context root.  The context root is set by the
     * JPublishServlet.  The value is the root of the web
     * application context.
     *
     * @return The webapp context root
     */

    public File getContextRoot() {
        return contextRoot;
    }

    /**
     * Set the context root.
     *
     * @param contextRoot The new context root
     */

    public void setContextRoot(File contextRoot) {
        if (log.isDebugEnabled())
            log.debug("setContextRoot(" + contextRoot + ")");
        this.contextRoot = contextRoot;
    }

    /**
     * Set the context root.
     *
     * @param contextRoot The new context root
     */

    public void setContextRoot(String contextRoot) {
        if (contextRoot != null) {
            setContextRoot(new File(contextRoot));
        }
    }

    /**
     * Get the directory where page configuration files are stored.  Page
     * configurations are stored as XML files and are cached to improve
     * performance.  The page will automatically be reloaded if it is modified.
     *
     * @return The page root
     */

    public File getPageRoot() {
        return pageRoot;
    }

    /**
     * Set the directory where page configuration files are stored.  Page
     * configurations are stored as XML files and are cached to improve
     * performance.  The page will automatically be reloaded if it is modified.
     *
     * @param pageRoot The new page root
     */

    public void setPageRoot(File pageRoot) {
        this.pageRoot = pageRoot;
    }

    /**
     * Set the directory where page configuration files are stored.  Page
     * configurations are stored as XML files and are cached to improve
     * performance.  The page will automatically be reloaded if it is modified.
     *
     * @param pageRoot The new page root
     */

    public void setPageRoot(String pageRoot) {
        if (pageRoot != null) {
            setPageRoot(new File(pageRoot));
        }
    }

    /**
     * If the value of <code>getPageRoot()</code> is an absolute file
     * path then that value is returned, otherwise the return value is
     * is <code>new File(getRoot(), getPageRoot())</code>.
     *
     * @return The real page root
     */

    public File getRealPageRoot() {
        File pageRoot = getPageRoot();
        if (!pageRoot.isAbsolute()) {
            pageRoot = new File(getRoot(), pageRoot.getPath());
        }
        return pageRoot;
    }

    /**
     * Get the directory where templates are stored.  Templates are merged
     * with the runtime context to produce a final document.
     *
     * @return The template root
     */

    public File getTemplateRoot() {
        return templateRoot;
    }

    /**
     * Set the directory where templates are stored.  Templates are merged
     * with the runtime context to produce a final document.
     *
     * @param templateRoot The template root
     */

    public void setTemplateRoot(File templateRoot) {
        this.templateRoot = templateRoot;
    }

    /**
     * Set the directory where templates are stored.  Templates are merged
     * with the runtime context to produce a final document.
     *
     * @param templateRoot The template root
     */

    public void setTemplateRoot(String templateRoot) {
        if (templateRoot != null) {
            setTemplateRoot(new File(templateRoot));
        }
    }

    /**
     * If the value of <code>getTemplateRoot()</code> is an absolute file
     * path then that value is returned, otherwise the return value is
     * is <code>new File(getRoot(), getTemplateRoot())</code>.
     *
     * @return The real template root
     */

    public File getRealTemplateRoot() {
        File templateRoot = getTemplateRoot();
        if (!templateRoot.isAbsolute()) {
            templateRoot = new File(getRoot(), templateRoot.getPath());
        }
        return templateRoot;
    }

    /**
     * Get the directory where action scripts are stored.  Actions stored
     * here are scripts written in a language supported by the BSF library.
     * The language's Java implementation must be included in the classpath.
     *
     * @return The action root
     */

    public File getActionRoot() {
        return actionRoot;
    }

    /**
     * Set the directory where action scripts are stored.  Actions stored
     * here are scripts written in a language supported by the BSF library.
     * The language's Java implementation must be included in the classpath.
     *
     * @param actionRoot The new action root
     */

    public void setActionRoot(File actionRoot) {
        this.actionRoot = actionRoot;
    }

    /**
     * Set the directory where action scripts are stored.  Actions stored
     * here are scripts written in a language supported by the BSF library.
     * The language's Java implementation must be included in the classpath.
     *
     * @param actionRoot The new action root
     */

    public void setActionRoot(String actionRoot) {
        if (actionRoot != null) {
            setActionRoot(new File(actionRoot));
        }
    }

    /**
     * If the value of <code>getActionRoot()</code> is an absolute file
     * path then that value is returned, otherwise the return value is
     * is <code>new File(getRoot(), getActionRoot())</code>.
     *
     * @return The real action root
     */

    public File getRealActionRoot() {
        File actionRoot = getActionRoot();
        if (!actionRoot.isAbsolute()) {
            actionRoot = new File(getRoot(), actionRoot.getPath());
        }
        return actionRoot;
    }

    /**
     * Get the directory where static files are stored.  Static files are
     * files which are not processed by JPublish but are returned byte for
     * byte.
     *
     * @return The root directory where static files are stored
     */

    public File getStaticRoot() {
        return staticRoot;
    }

    /**
     * Set the directory where static files are stored.  Static files are
     * files which are not processed by JPublish but are returned byte for
     * byte.
     *
     * @param staticRoot The new root directory where static files are stored
     */

    public void setStaticRoot(File staticRoot) {
        this.staticRoot = staticRoot;
    }

    /**
     * Set the directory where static files are stored.  Static files are
     * files which are not processed by JPublish but are returned byte for
     * byte.
     *
     * @param staticRoot The path of the new root directory where static
     *                   files are stored
     */

    public void setStaticRoot(String staticRoot) {
        if (staticRoot != null) {
            setStaticRoot(new File(staticRoot));
        }
    }

    /**
     * If the value of <code>getStaticRoot()</code> is an absolute file
     * path then that value is returned, otherwise the return value is
     * is <code>new File(getRoot(), getStaticRoot())</code>.
     *
     * @return The real static root
     */

    public File getRealStaticRoot() {
        File staticRoot = getStaticRoot();
        if (!staticRoot.isAbsolute()) {
            staticRoot = new File(getRoot(), staticRoot.getPath());
        }
        return staticRoot;
    }

    /**
     * Get the File for the WEB-INF directory.  This is used to locate the
     * classes and JARs so they are accessible to scripting languages.
     *
     * @return The WEB-INF file
     */

    public File getWebInfPath() {
        return webInfPath;
    }

    /**
     * Set the File for the WEB-INF directory.
     *
     * @param webInfPath The WEB-INF file
     */

    public void setWebInfPath(File webInfPath) {
        this.webInfPath = webInfPath;
    }

    /**
     * Get the action identifier which is used to trigger parameter actions.
     * The default value is 'action'.
     *
     * @return The action identifier
     */

    public String getActionIdentifier() {
        return actionIdentifier;
    }

    /**
     * Set the action identifier.  If this method is invoked with a null
     * argument then the action identifier will be reset to the default
     * value.
     *
     * @param actionIdentifier The new action identifer or null to reset
     */

    public synchronized void setActionIdentifier(String actionIdentifier) {
        if (actionIdentifier == null) {
            this.actionIdentifier = DEFAULT_ACTION_IDENTIFIER;
        } else {
            this.actionIdentifier = actionIdentifier;
        }
    }

    /**
     * Return true if parameter actions are enabled.
     *
     * @return True if parameter actions are enabled
     * @since 1.4.1
     */

    public boolean isParameterActionsEnabled() {
        return parameterActionsEnabled;
    }

    /**
     * Set to true to enable parameter actions.  Parameter actions are disabled
     * by default because of the inherent security risks involved in using
     * them.
     *
     * @param parameterActionsEnabled True to enabled parameter actions
     * @since 1.4.1
     */

    public void setParameterActionsEnabled(boolean parameterActionsEnabled) {
        this.parameterActionsEnabled = parameterActionsEnabled;
    }

    /**
     * Set to true to enable parameter actions.  Parameter actions are disabled
     * by default because of the inherent security risks involved in using
     * them.
     *
     * @param parameterActionsEnabled True to enabled parameter actions
     * @since 1.4.1
     */

    public void setParameterActionsEnabled(String parameterActionsEnabled) {
        setParameterActionsEnabled("true".equals(parameterActionsEnabled));
    }

    /**
     * Get the default page.  This value will be used when directories
     * are requested.  By default this returns 'index.html'.
     *
     * @return The default page
     */

    public String getDefaultPage() {
        return defaultPage;
    }

    /**
     * Set the default page.  This value will be used when directories
     * are requested.
     *
     * @param defaultPage The new default page
     */

    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    /**
     * Get the default template.  This method returns the name of the
     * template which will be used when no template is specified in
     * a page's configuration.
     *
     * @return The default template
     */

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    /**
     * Set the default template. The default template will be used when
     * no template is specified in a page's configuration.
     *
     * @param defaultTemplate The new default template
     */

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    /**
     * Get the default mime type.  This method delegates to the current
     * MimeTypeMap.
     *
     * @return The default mime type
     */

    public String getDefaultMimeType() {
        return getMimeTypeMap().getDefaultMimeType();
    }

    /**
     * Set the default mime type.  This method delegates to the current
     * MimeTypeMap.
     *
     * @param defaultMimeType The new default mime type
     */

    public void setDefaultMimeType(String defaultMimeType) {
        getMimeTypeMap().setDefaultMimeType(defaultMimeType);
    }

    /**
     * Returns true if reserved names should be protected in the
     * JPublishContext.  This method returns false by default.
     *
     * @return True if reserved names should be protected
     */

    public boolean isProtectReservedNames() {
        return protectReservedNames;
    }

    /**
     * Set to true to protect reserved names in the JPublishContext.
     *
     * @param protectReservedNames True to protect reserved names
     */

    public void setProtectReservedNames(boolean protectReservedNames) {
        if (protectReservedNames)
            log.info("Protect reserved names enabled");
        this.protectReservedNames = protectReservedNames;
    }

    /**
     * Set to "true" to protect reserved names in the JPublishContext.
     *
     * @param protectReservedNames "true" to protect reserved names
     */

    public void setProtectReservedNames(String protectReservedNames) {
        setProtectReservedNames("true".equals(protectReservedNames));
    }

    /**
     * Return true if debugging is enabled.
     *
     * @return True if debugging is enabled
     */

    public boolean isDebug() {
        return debug;
    }

    /**
     * Set to true to enable debugging.
     *
     * @param debug True to enable debugging
     */

    public void setDebug(boolean debug) {
        if (debug)
            log.info("JPublish debugging enabled.");
        this.debug = debug;
    }

    /**
     * Set to "true" to enable debugging.
     *
     * @param debug "true" to enable debugging
     */

    public void setDebug(String debug) {
        setDebug("true".equals(debug));
    }

    /**
     * Return a list of paths where the session object should NOT be
     * created.
     *
     * @return A List of paths where sessions should not be created
     */

    public List getDisableSessionPaths() {
        return disableSessionPaths;
    }

    /**
     * Get a List of all loaded modules.
     *
     * @return List of loaded modules
     */

    public List getModules() {
        return modules;
    }

    /**
     * Get a list of all registered repositories.
     *
     * @return A list of all registered Repository objects
     */

    public List getRepositories() {
        return repositories;
    }

    /**
     * Get a List of all error handlers for the given path.  The path can
     * include the '*' wildcard.  If there are no error handlers for the
     * given path then this method will return the default handlers.  If
     * you do not want any handlers then define a error handler mapping
     * with no defined error handlers.
     *
     * @param path The path
     * @return A List of error handlers
     */

    public List getErrorHandlers(String path) {
        List errorHandlers = (List) cachedErrorHandlers.get(path);

        if (errorHandlers == null) {
            Iterator keys = errorHandlerMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (PathUtilities.match(path, key)) {
                    errorHandlers = (List) errorHandlerMap.get(key);
                    cachedErrorHandlers.put(path, errorHandlers);
                    return errorHandlers;
                }
            }
            return getDefaultErrorHandlers();
        } else {
            return errorHandlers;
        }
    }

    /**
     * Get the List of default error handlers.  These error handlers should
     * be used whenever no error handlers are defined.
     *
     * @return The default error handlers
     */

    public List getDefaultErrorHandlers() {
        return defaultErrorHandlers;
    }

    /**
     * Get a Repository by name.  If the repository was not registered
     * then this method will return null.
     *
     * @param name The name of the Repository
     * @return The repository or null
     */

    public Repository getRepository(String name) {
        Iterator iter = getRepositories().iterator();
        while (iter.hasNext()) {
            Repository repository = (Repository) iter.next();
            if (repository.getName().equals(name)) {
                return repository;
            }
        }
        return null;
    }

    /**
     * Get the site's ActionManager.
     *
     * @return The ActionManager
     * @see org.jpublish.action.ActionManager
     */

    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * Get the site's PageManager.
     *
     * @return The PageManager
     * @see PageManager
     */

    public PageManager getPageManager() {
        return pageManager;
    }

    /**
     * Get the site's TemplateManager.
     *
     * @return The TemplateManager
     * @see TemplateManager
     */

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    /**
     * Get the site's StaticResourceManager.
     *
     * @return The StaticResourceManager
     * @see StaticResourceManager
     */

    public StaticResourceManager getStaticResourceManager() {
        return staticResourceManager;
    }

    /**
     * Get the site's ViewRenderer which is used to render content.
     *
     * @return The ViewRenderer
     */

    public ViewRenderer getViewRenderer() {
        return viewRenderer;
    }

    /**
     * Get the site's ComponentManager.
     *
     * @return The ComponentManager
     * @since 2.0
     */

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    /**
     * Get the site's MimeType map.  Mime types can be mapped to file
     * suffixes.
     *
     * @return The MimeTypeMap
     */

    public MimeTypeMap getMimeTypeMap() {
        return mimeTypeMap;
    }

    /**
     * Return the CharacterEncodingManager.
     *
     * @return The CharacterEncodingManager
     */

    public CharacterEncodingManager getCharacterEncodingManager() {
        return characterEncodingManager;
    }

    /**
     * Retrieve a Content object for the specified named content.  The content
     * name must include the origin prefix.  In the case of content pulled from
     * a repository this would be:
     * <p/>
     * <blockquote>
     * <code>repository:repository_name://path/to/content</code>
     * </blockquote>
     * <p/>
     * <p>In the case of templates, this would be:</p>
     * <p/>
     * <blockquote>
     * <code>template:/path/to/template</code>
     * </blockquote>
     * <p/>
     * <p>Ultimately there should be a common repository system for all content
     * whether it be text content, templates, binary content, etc.</p>
     * <p/>
     * <p>This method must return null if the named content can not be
     * found.</p>
     *
     * @param name The content name
     * @return The Content object
     */

    public Content getContent(String name) {
        String path;

        try {
            if (log.isDebugEnabled())
                log.debug("getContent(" + name + ")");

            InternalURI uri = InternalURIParser.getInstance().parse(name);
            String protocol = uri.getProtocol();

            if (protocol.equalsIgnoreCase("template")) {
                path = uri.getPath();
                if (log.isDebugEnabled())
                    log.debug("Looking for template: " + path);

                return new TemplateContent(templateManager.getTemplate(path));

            } else if (protocol.equalsIgnoreCase("repository")) {
                String repositoryName = ((RepositoryURI) uri).getRepositoryName();
                Repository r = getRepository(repositoryName);
                path = uri.getPath();
                if (log.isDebugEnabled())
                    log.debug("Looking for content: " + path);

                if (r == null) {
                    throw new Exception(
                            "Repository for " + repositoryName + " is null. Cannot get content for: " + path);
                }
                return new RepositoryContent(r.get(path), r.getLastModified(path));

            } else {
                throw new Exception("Protocol " + protocol + " not supported");
            }
        } catch (Throwable t) {
            // this is necessary to support FreeMarker for the moment.
            // FreeMarker adds localized parts to the file path and thus
            // the file will not be found on the first try, but FreeMarker
            // requires that this method return null, so voila!  When they
            // fix the setLocalizedLookup() method so false works I will
            // probably remove this.

            log.error("Error getting content: " + t.getMessage(), t);
            t.printStackTrace();
            return null;
        }
    }

    public long getLastModified(String name) throws Exception {
        InternalURI uri = InternalURIParser.getInstance().parse(name);
        String protocol = uri.getProtocol();
        String path = uri.getPath();
        long lastModified = 0;

        if (protocol.equalsIgnoreCase("template")) {
            lastModified = templateManager.getTemplate(path).getLastModified();

        } else if (protocol.equalsIgnoreCase("repository")) {
            String repositoryName = ((RepositoryURI) uri).getRepositoryName();
            Repository r = getRepository(repositoryName);
            lastModified = r.getLastModified(path);
        }
        return lastModified;
    }

    // Start Attribute support

    /**
     * Get the named site attribute.  Returns null if there is no
     * site attribute for the specified name.
     *
     * @param name The attribute name
     * @return The site attribute or null
     */

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Set the named site attribute.
     *
     * @param name  The site attribute name
     * @param value The site attribute value
     */

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Remove the named site attribute.
     *
     * @param name The site attribute name
     */

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * Get an Iterator of all names for site attributes.
     *
     * @return Iterator of attribute names
     */

    public Iterator getAttributeNames() {
        return attributes.keySet().iterator();
    }

    // End Attribute support

    /**
     * Reload the site configuration.
     */

    public void reload() {
        try {
            log.info("Loading site configuration.");
            loadConfiguration();
            log.info("Configuration loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the site configuration from the given InputStream.  The
     * InputStream must be attached to an XML document.
     *
     * @param in The InputStream
     * @throws Exception
     */

    public void load(InputStream in) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Loading configuration");

        // construct the Configuration object from the stream
        Configuration configuration = new XMLConfiguration(in);

        // construct the ActionManager
        actionManager = new ActionManager(this);

        // setup the ActionManager
        Configuration actionManagerConfiguration = configuration.getChild("action-manager");
        if (actionManagerConfiguration != null) {
            List classPathElements = actionManager.getClassPathElements();
            Configuration classpathConfiguration = actionManagerConfiguration.getChild("classpath");
            Iterator pathElements = classpathConfiguration.getChildren("pathelement").iterator();
            while (pathElements.hasNext()) {
                Configuration pathElement = (Configuration) pathElements.next();
                classPathElements.add(pathElement.getValue());
            }
        }
        // setup alien dispatcher
        // load path actions

        pathDispatcherManager = new PathDispatcherManager(this);
        Configuration pathDispatcherManagerConfiguration = configuration.getChild("path-dispatcher");
        if (pathDispatcherManagerConfiguration != null) {
            if (log.isDebugEnabled()) {
                log.debug("loading path-dispatcher elements");
            }
            pathDispatcherManager.loadConfiguration(configuration);
        }

        /**
         * Create a default Cache manager
         *
         */

        jPublishCacheManager = new JPublishCacheManager(this);
        Configuration cacheManagerConfiguration = configuration.getChild("cache-manager");

        if (cacheManagerConfiguration != null) {
            if (log.isDebugEnabled()) {
                log.debug("laoding cache-manager elements");
            }
            jPublishCacheManager.loadConfiguration(configuration);
        }

        // load the PageManager
        log.debug("Creating PageManager");
        Configuration pageManagerConfiguration = configuration.getChild("page-manager");

        if (pageManagerConfiguration != null) {
            String pageManagerClass = pageManagerConfiguration.getAttribute(ATTRIBUTE_CLASSNAME);
            if (pageManagerClass == null) {
                // handle using page-manager value as class name
                pageManagerClass = pageManagerConfiguration.getValue();
                log.warn("The page-manager class should now be specified " +
                        "using the classname attribute");
            }

            pageManager = (PageManager) ClassUtilities.loadClass(pageManagerClass).newInstance();
            pageManager.setSiteContext(this);
            pageManager.loadConfiguration(pageManagerConfiguration);

        } else {
            pageManager = (PageManager) ClassUtilities.loadClass(DEFAULT_PAGE_MANAGER).newInstance();
            pageManager.setSiteContext(this);
        }

        // load the TemplateManager
        if (log.isDebugEnabled())
            log.debug("Creating TemplateManager");

        Configuration templateManagerConfiguration = configuration.getChild("template-manager");
        if (templateManagerConfiguration != null) {
            String templateManagerClass = templateManagerConfiguration.getAttribute(ATTRIBUTE_CLASSNAME);
            if (templateManagerClass == null) {
                // handle using element value as class name
                templateManagerClass = templateManagerConfiguration.getValue();
                log.warn("The template-manager class should now be specified " +
                        "using the classname attribute");
            }
            templateManager = (TemplateManager) ClassUtilities.loadClass(templateManagerClass).newInstance();
            templateManager.setSiteContext(this);
            templateManager.loadConfiguration(templateManagerConfiguration);

        } else {

            templateManager = (TemplateManager) ClassUtilities.loadClass(DEFAULT_TEMPLATE_MANAGER).newInstance();
            templateManager.setSiteContext(this);
        }

        if (jPublishCacheManager.getCache("default") != null) {
            templateManager.setCache(jPublishCacheManager.getCache("default"));
        } else {
            log.warn("Template caching will be disabled unless you'll define a 'default' cache in jpublish.xml");
        }

        // load the StaticResourceManager
        log.debug("Creating StaticResourceManager");
        Configuration staticResourceManagerConfiguration = configuration.getChild("static-resource-manager");
        if (staticResourceManagerConfiguration != null) {
            String staticResourceManagerClass = staticResourceManagerConfiguration.getAttribute(ATTRIBUTE_CLASSNAME);

            if (staticResourceManagerClass == null) {
                // handle using element value as class name
                staticResourceManagerClass = staticResourceManagerConfiguration.getValue();
                log.warn("The static-resource-manager class should be " +
                        "specified using the classname attribute");
            }
            staticResourceManager = (StaticResourceManager) ClassUtilities.loadClass(staticResourceManagerClass).newInstance();
            staticResourceManager.setSiteContext(this);
            staticResourceManager.loadConfiguration(staticResourceManagerConfiguration);
        } else {
            staticResourceManager =
                    (StaticResourceManager) ClassUtilities.loadClass(
                            DEFAULT_STATIC_RESOURCE_MANAGER).newInstance();
            staticResourceManager.setSiteContext(this);
        }

        // load the ViewRenderer
        if (log.isDebugEnabled())
            log.debug("Creating ViewRenderer");

        Configuration viewRendererConfiguration = configuration.getChild("view-renderer");

        if (viewRendererConfiguration != null) {
            String viewRendererClass = viewRendererConfiguration.getAttribute(ATTRIBUTE_CLASSNAME);
            if (viewRendererClass == null) {
                // handle using element value as class name
                viewRendererClass = viewRendererConfiguration.getValue();
                log.warn("The view-renderer class should be " +
                        "specified using the classname attribute");
            }
            viewRenderer = (ViewRenderer) ClassUtilities.loadClass(viewRendererClass).newInstance();
            viewRenderer.setSiteContext(this);
            viewRenderer.loadConfiguration(viewRendererConfiguration);

        } else {

            viewRenderer = (ViewRenderer) ClassUtilities.loadClass(DEFAULT_VIEW_RENDERER).newInstance();
            viewRenderer.setSiteContext(this);
        }

        if (log.isDebugEnabled())
            log.debug("View renderer: " + viewRenderer.getClass());

        viewRenderer.init();

        // load modules
        Iterator moduleElements = configuration.getChildren("module").iterator();
        while (moduleElements.hasNext()) {
            Configuration moduleElement = (Configuration) moduleElements.next();
            String className = moduleElement.getAttribute(ATTRIBUTE_CLASSNAME);
            try {
                JPublishModule module = (JPublishModule) ClassUtilities.loadClass(className).newInstance();
                module.init(this, moduleElement);
                modules.add(module);
            } catch (Exception e) {
                log.error("Unable to load module " + className);
                e.printStackTrace();
            }
        }

        // load repository references
        Iterator repositoryElements = configuration.getChildren("repository").iterator();
        while (repositoryElements.hasNext()) {
            Configuration repositoryElement = (Configuration) repositoryElements.next();
            String className = repositoryElement.getAttribute(ATTRIBUTE_CLASSNAME);
            Repository repository = (Repository) ClassUtilities.loadClass(className).newInstance();
            repository.setSiteContext(this);
            repository.loadConfiguration(repositoryElement);
            getRepositories().add(repository);
        }

        // load the ComponentManager
        if (log.isDebugEnabled())
            log.debug("Creating ComponentManager");

        Configuration componentManagerConfiguration = configuration.getChild("component-manager");
        String componentManagerClass = null;
        if (componentManagerConfiguration != null) {
            componentManagerClass = componentManagerConfiguration.getAttribute(ATTRIBUTE_CLASSNAME);
        }

        if (componentManagerClass == null) {
            componentManagerClass = DEFAULT_COMPONENT_MANAGER;
        }

        if (log.isDebugEnabled())
            log.debug("Component manager class: " + componentManagerClass);

        componentManager = (ComponentManager) ClassUtilities.loadClass(componentManagerClass).newInstance();
        componentManager.setSiteContext(this);
        if (componentManagerConfiguration != null) {
            componentManager.loadConfiguration(componentManagerConfiguration);
        }

        // Configure root paths
        setRoot(configuration.getChildValue("root"));

        // eventually these should be moved into the configuration
        // methods for the actual managers since having roots only
        // makes sense there
        setPageRoot(configuration.getChildValue("page-root", DEFAULT_PAGE_ROOT));
        setTemplateRoot(configuration.getChildValue("template-root", DEFAULT_TEMPLATE_ROOT));
        setActionRoot(configuration.getChildValue("action-root", DEFAULT_ACTION_ROOT));
        setStaticRoot(configuration.getChildValue("static-root", DEFAULT_STATIC_ROOT));
        setActionIdentifier(configuration.getChildValue("action-identifier"));

        // Set defaults
        setDefaultPage(configuration.getChildValue("default-page", DEFAULT_PAGE));
        setDefaultTemplate(configuration.getChildValue("default-template", DEFAULT_TEMPLATE));
        setDefaultMimeType(configuration.getChildValue("default-mime-type"));

        // configuration JNDI settings
        if (log.isDebugEnabled())
            log.debug("Configuring JNDI");

        configureJNDI(configuration.getChild("jndi"));

        // protect reserved names in the context
        setProtectReservedNames(configuration.getChildValue("protect-reserved-names", "false"));

        // enable or disable parameter actions
        setParameterActionsEnabled(configuration.getChildValue("parameter-actions-enabled", "false"));

        // enable or disable debugging
        setDebug(configuration.getChildValue("debug", "false"));

        // enable or disable debugging
        setEval(configuration.getChildValue("evaluateVelocityTemplates", "false"));

        // load character encoding maps
        if (log.isDebugEnabled())
            log.debug("Loading CharacterEncodingMaps");

        characterEncodingManager.loadConfiguration(configuration);

        // load all actions
        actionManager.loadConfiguration(configuration);

        // load the mime type map
        Iterator mimeTypeMapElements = configuration.getChildren("mime-mapping").iterator();
        while (mimeTypeMapElements.hasNext()) {
            Configuration mimeTypeMapElement = (Configuration) mimeTypeMapElements.next();
            String ext = mimeTypeMapElement.getAttribute("ext");
            String mimeType = mimeTypeMapElement.getAttribute("mimetype");
            mimeTypeMap.put(ext, mimeType);
        }

        // load default error handlers
        Configuration defaultErrorHandlersElement = configuration.getChild("default-error-handlers");
        if (defaultErrorHandlersElement != null) {
            Iterator defaultErrorHandlerElements = defaultErrorHandlersElement.getChildren("error-handler").iterator();
            while (defaultErrorHandlerElements.hasNext()) {
                Configuration defaultErrorHandlerElement = (Configuration) defaultErrorHandlerElements.next();
                String className = defaultErrorHandlerElement.getAttribute("class");
                defaultErrorHandlers.add(ClassUtilities.loadClass(className).newInstance());
            }
        }

        // load error handlers
        Iterator errorHandlerMapElements = configuration.getChildren("error-handler-map").iterator();
        while (errorHandlerMapElements.hasNext()) {
            Configuration errorHandlerMapElement = (Configuration) errorHandlerMapElements.next();
            String path = errorHandlerMapElement.getAttribute("path");
            if (path == null) {
                throw new ConfigurationException("Error handler path must be defined");
            }

            Iterator errorHandlerElements = errorHandlerMapElement.getChildren("error-handler").iterator();
            while (errorHandlerElements.hasNext()) {
                Configuration errorHandlerElement = (Configuration) errorHandlerElements.next();
                String errorHandlerClass = errorHandlerElement.getAttribute("class");
                List errorHandlers = (List) errorHandlerMap.get(path);
                if (errorHandlers == null) {
                    errorHandlers = new ArrayList();
                    errorHandlerMap.put(path, errorHandlers);
                }
                errorHandlers.add(ClassUtilities.loadClass(errorHandlerClass).newInstance());
            }
        }

        // load session disable paths
        Iterator disableSessionElements = configuration.getChildren("disable-session").iterator();
        while (disableSessionElements.hasNext()) {
            Configuration disableSessionElement = (Configuration) disableSessionElements.next();
            String path = disableSessionElement.getAttribute("path");
            disableSessionPaths.add(path);
        }

/*
        // add Spring to JPublish if the applicationContext is defined
        log.info("Checking for JPublish-Spring support ...");
        Configuration springSupportConfig = configuration.getChild("spring-support");
        if (springSupportConfig != null) {
            log.info("Loading Spring support ...");
            String springConfigPath = springSupportConfig.getChildValue("config-path", "WEB-INF/applicationContext.xml");
            String rootDir = contextRoot.getAbsolutePath();
            springConfigPath = rootDir + FILE_SYSTEM_SEPARATOR + springConfigPath;

            try {
                ApplicationContext appContext = new FileSystemXmlApplicationContext("file:" + springConfigPath);
                this.setAttribute(SiteContext.SPRING, appContext);

                //messageSource = (MessageSource) appContext.getBean("messageSource");
                //String[] localizationBaseNames = getLocalizationBaseNames(springSupportConfig);
                //log.info("Localization properties definitions: ");
                //for (int i = 0; i < localizationBaseNames.length; i++) {
                //    localizationBaseNames[i] = "file:" + rootDir + "/" +localizationBaseNames[i];
                //    log.info("   > "+localizationBaseNames[i]);
                //}
                //messageSource.setBasenames( localizationBaseNames);

            } catch (BeansException e) {
                e.printStackTrace();
            }

        } else {
            log.info("Spring support not required ...");
        }
*/


    }


    private void setEval(String evaluateVelocityTemplates) {
        this.evaluateVelocityTemplates = evaluateVelocityTemplates;
    }

    // private methods

    /**
     * Configure the initial JNDI context properties.  If the element is null
     * then the property values will not be modified.
     *
     * @throws Exception
     */

    private void configureJNDI(Configuration configuration) throws Exception {
        if (configuration != null) {
            System.setProperty("java.naming.factory.initial", configuration.getChildValue("initial-factory"));
            System.setProperty("java.naming.provider.url", configuration.getChildValue("provider"));
        }
    }

    /**
     * Load the configuration.  This method opens the stream to the
     * configuration and then calls the <code>load()</code> method.
     *
     * @throws Exception
     */

    private void loadConfiguration() throws Exception {
        InputStream in = null;

        try {
            if (log.isDebugEnabled())
                log.debug("Loading configuration from: " + configurationFile);

            in = new FileInputStream(configurationFile);
            load(in);
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtilities.close(in);
        }
    }


    public PathDispatcherManager getPathDispatcherManager() {
        return pathDispatcherManager;
    }

    public JPublishCacheManager getJPublishCacheManager() {
        return jPublishCacheManager;
    }

    public boolean isEval() {
        return evaluateVelocityTemplates.equalsIgnoreCase("true");
    }
/*
    public String[] getLocalizationBaseNames(Configuration springSupportConfig) {
        String[] baseNames = {""};

        if (springSupportConfig != null) {
            String localizationBaseNames = springSupportConfig.getChildValue("localizationBaseNames", "");
            if (StringUtils.hasText(localizationBaseNames)){
                baseNames = StringUtils.tokenizeToStringArray(localizationBaseNames, " ,;:\n\r");
            }
        }
        return baseNames;
    }

    */

    public HttpServlet getJPublishServlet() {
        return jPublishServlet;
    }

    public void setJPublishServlet(HttpServlet jPublishServlet) {
        this.jPublishServlet = jPublishServlet;
    }

    public void destroy() {
        //todo implement
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }
}
