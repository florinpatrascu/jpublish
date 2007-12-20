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

package org.jpublish.repository.filesystem;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.util.IOUtilities;
import com.atlassian.util.profiling.UtilTimerStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.*;
import org.jpublish.action.ActionManager;
import org.jpublish.action.ActionNotFoundException;
import org.jpublish.util.*;
import org.jpublish.view.ViewRenderer;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * The ExtendedFileSystemRepository allows actions to be bound to content
 * elements through XML configuration files.  Only actions bound to dynamic
 * content elements will be executed.
 * <p/>
 * <p>Note: Actions attached to content elements cannot cause an HTTP redirect.
 * The "redirect" value will be ignored.</p>
 * <p/>
 * Florin - refactoring the code for speed and reimplementeing the cache
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class ExtendedFileSystemRepository extends AbstractFileSystemRepository implements Configurable {
    private static final Log log = LogFactory.getLog(ExtendedFileSystemRepository.class);
    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_CACHE_NAME = "default";
    private static final String XML_TYPE = ".xml";

    private JPublishCache cache = null;
    private String cacheName = DEFAULT_CACHE_NAME;
    private String configurationDirectoryName = "config";
    private static final String DOT = ".";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_LOCALE = "locale";

//private Map configCache = new HashMap();

    /**
     * Get the content from the given path.  Implementations of this method
     * should NOT merge the content using view renderer.
     *
     * @param path The relative content path
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Getting static content element.");

        //log.info("get: "+path);
        return loadContent(path);
    }

    /**
     * Get the content from the given path and merge it with the given
     * context.  Any actions attached to the content will be executed
     * first.
     *
     * @param path    The content path
     * @param context The current context
     * @return The content as a String
     * @throws Exception Any Exception
     */

    public String get(String path, JPublishContext context) throws Exception {
        UtilTimerStack.push( " ==> /"+path);
        executeActions(path, context);

        if (log.isDebugEnabled())
            log.debug("Getting dynamic content element for path " + path);

        StringWriter writer = null;
        BufferedReader reader = null;
        Reader in = null;

        try {

            in = new StringReader(loadContent(path));
            reader = new BufferedReader(in);
            writer = new StringWriter();
            String name = PathUtilities.makeRepositoryURI(getName(), path);
            ViewRenderer renderer = siteContext.getViewRenderer();

            renderer.render(context, name, reader, writer);

            return writer.toString();

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new FileNotFoundException("File not found: " + path);
        } finally {
            IOUtilities.close(in);
            IOUtilities.close(reader);
            IOUtilities.close(writer);
            UtilTimerStack.pop( " ==> /"+path);
        }
    }

    public void remove(String path) throws Exception {
        pathToFile(path).delete();
    }

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path) {
        File file = new File(getRealRoot(), path);
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
        log.info("Remove directory: " + path);
        File file = new File(getRealRoot(), path);

        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());

        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
    }

    /**
     * Get the given content as an InputStream.  The InputStream will
     * return the raw content data.
     *
     * @param path The path to the content
     * @return The InputStream
     * @throws Exception
     */

    public InputStream getInputStream(String path) throws Exception {
        return new FileInputStream(pathToFile(path));
    }

    /**
     * Get an OutputStream for writing content to the given path.
     *
     * @param path The path to the content
     * @return The OutputStream
     * @throws Exception
     */

    public OutputStream getOutputStream(String path) throws Exception {
        if (!isWriteAllowed()) {
            throw new SecurityException("Writing not allowed");
        }

        return new FileOutputStream(pathToFile(path));
    }

    /**
     * Get the last modified time in milliseconds for the given path.
     *
     * @param path The content path
     * @return The last modified time in milliseconds
     * @throws Exception Any Exception
     */

    public long getLastModified(String path) throws Exception {
        return pathToFile(path).lastModified();
    }

    /**
     * Return the configuration directory name.
     *
     * @return The configuration directory name
     */

    public String getConfigurationDirectoryName() {
        return configurationDirectoryName;
    }

    /**
     * Set the name of the configuration directory used to locate XML
     * configuration files for a given path.  The default value is "config".
     * <p/>
     * <p>Example: using the default value "config", a request for
     * <code>/site/test.html</code> would look for the configuration file as
     * <code>/site/config/test.xml</code>.</p>
     *
     * @param configurationDirectoryName The new configuration directory name
     */

    public void setConfigurationDirectoryName(String configurationDirectoryName) {
        if (configurationDirectoryName != null) {
            this.configurationDirectoryName = configurationDirectoryName;
        }
    }

    /**
     * Load the repository's configuration from the given configuration
     * object.
     *
     * @param configuration The configuration object
     * @throws Exception
     */

    public void loadConfiguration(Configuration configuration) throws Exception {
        this.name = configuration.getAttribute(PROPERTY_NAME);
        setRoot(configuration.getChildValue("root"));
        setCache(configuration.getChildValue("cache"));
        setWriteAllowed(configuration.getChildValue("write-allowed"));
        setConfigurationDirectoryName(configuration.getChildValue("config-dir"));
    }

    private synchronized void setCache(String cacheName) {
        this.cacheName = cacheName;
        JPublishCacheManager jPublishCacheManager = siteContext.getJPublishCacheManager();
        cache = jPublishCacheManager.getCache(cacheName);
    }

    /**
     * Get an Iterator of paths which are known to the repository.
     *
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths() throws Exception {
        return getPaths(EMPTY_STRING);
    }

    /**
     * Get an Iterator of paths which are known to the repository, starting
     * from the specified base path.
     *
     * @param base The base path
     * @return An iterator of paths
     * @throws Exception
     */

    public Iterator getPaths(String base) throws Exception {
        return new FileSystemPathIterator(new BreadthFirstFileTreeIterator(pathToFile(base)), this);
    }

    /**
     * Load the content from the given path.
     *
     * @param path The path
     * @return The String
     */

    private String loadContent(String path) throws Exception {
        CacheEntry cacheEntry = (CacheEntry) cache.get(path);
        long fileTimeStamp = pathToFile(path).lastModified();

        if (cacheEntry == null || cacheEntry.getLastModified() != fileTimeStamp) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(path)));// [florin], getInputEncoding(path)));
            cacheEntry = new CacheEntry(FileCopyUtils.copyToString(reader), fileTimeStamp);
            cache.put(path, cacheEntry);
            //log.info(path + " loaded ...");
        }
        return (String) cacheEntry.getObject();
    }

    /**
     * Get the File to the content using the path.
     *
     * @param path The content path
     * @return A File object
     */

    public File pathToFile(String path) {
        return new File(getRealRoot(), path);

        // not sure why I wanted to do this...but for the moment
        // I am going to use the same system as the FileSystemRepository
        // for determining the content File object. -AE

        //return new File(new File(getRealRoot(), "data"), path);
    }

    /**
     * The path to the config is determined by removing the last suffix
     * from the path and adding <code>.xml</code> to the path and then
     * appending the path to <code><i>root</i>/conf</code>.
     *
     * @param path The path
     * @return The File
     */

    private File pathToConfig(String path) {
        int dotIndex = path.lastIndexOf(DOT);
        if (dotIndex > 0) {
            path = path.substring(0, dotIndex);
        }
        return new File(
                new File(getRealRoot(), getConfigurationDirectoryName()),
                path + XML_TYPE);
    }

    /**
     * Execute all actions for the given path.
     * Load the properties file and add the properties to the page definition
     * [florin]
     *
     * @param path    The path
     * @param context The context
     * @throws Exception Any Exception
     */

    private void executeActions(String path, JPublishContext context) throws Exception {

        ActionManager actionManager = siteContext.getActionManager();

        String configFileKey;
        int dotIndex = path.lastIndexOf(DOT);
        if (dotIndex > 0) {
            configFileKey = path.substring(0, dotIndex) + XML_TYPE;
        } else {
            configFileKey = path + XML_TYPE;
        }

        // locate the configuration file
        File configFile = pathToConfig(path);
        if (!configFile.exists()) {
            return;
        }

        // load the configuration object, check the cache first
        Configuration configuration;

        CacheEntry cacheEntry = (CacheEntry) cache.get(configFileKey);
        long fileTimeStamp = configFile.lastModified();

        try {
            if (cacheEntry == null || cacheEntry.getLastModified() != fileTimeStamp) {
                configuration = new XMLConfiguration(configFileKey, configFile);
                configuration.getLocation().setSourceId(configFileKey);
                cacheEntry = new CacheEntry(configuration, fileTimeStamp);
                cache.put(configFileKey, cacheEntry);
                //log.info(configFileKey + " loaded ...");
            }
            configuration = (Configuration) cacheEntry.getObject();

        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new JPublishException("cannot load the Configuration for: " + configFileKey);
        } catch (JPublishCacheException e) {
            e.printStackTrace();
            throw new JPublishCacheException("cache refers to a null config object. Required by: " + configFileKey);
        }


        Page page = context.getPage();

        List properties = configuration.getChildren("property");
        if (properties != null) {
            Iterator propertyElements = properties.iterator();
            while (propertyElements.hasNext()) {
                Configuration propertyElement = (Configuration) propertyElements.next();
                String name = propertyElement.getAttribute(PROPERTY_NAME);
                String v = propertyElement.getAttribute(PROPERTY_VALUE);
                String value = v == null ? propertyElement.getValue() : v;
                String l = propertyElement.getAttribute(PROPERTY_LOCALE);

                if (name != null && name.trim().length() > 0) {
                    if (l != null) {
                        try {
                            page.setProperty(name, value, new Locale(l));
                        } catch (Exception e) {
                            throw new Exception(path + ", invalid locale: " + l + ", for element: " + name + ", value: " + value);
                        }
                    } else {
                        page.setProperty(name, value);
                    }
                } else {
                    throw new Exception("attempt to define a null property name from: " + configFileKey);
                }
            }
        }

        // execute all content actions
        List actions = configuration.getChildren("content-action");
        if (actions != null) {
            Iterator contentActionElements = actions.iterator();
            while (contentActionElements.hasNext()) {
                Configuration contentActionElement = (Configuration) contentActionElements.next();
                String actionName = contentActionElement.getAttribute(PROPERTY_NAME);
                if (actionName != null && actionName.trim().length() > 0) {
                    actionManager.execute(actionName, context, contentActionElement);
                } else {
                    throw new ActionNotFoundException("Action: " + actionName + ", not found. Defined in: " + configFileKey);
                }
            }
        }

    }


    /**
     * interim solution for using a dual cache; Florin
     *
     * @param context
     */
    public synchronized void clearCache(JPublishContext context) {
        try {

            if (cache != null) {
                if (context != null) {
                    context.put("old.cache.size", new Integer(cache.getKeys().size()));
                }
                cache.clear();
            }
        } catch (JPublishCacheException e) {
            e.printStackTrace();
        }
    }

    public String getCacheName() {
        return cacheName;
    }
}
