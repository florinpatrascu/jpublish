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

package org.jpublish.template.filesystem;

import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.Template;
import org.jpublish.template.AbstractTemplateManager;
import org.jpublish.template.TemplateCacheEntry;
import org.jpublish.util.BreadthFirstFileTreeIterator;
import org.jpublish.util.FileCopyUtils;
import org.jpublish.util.JPublishCache;
import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;
import org.jpublish.util.vfs.provider.filesystem.FileSystemProvider;

import java.io.*;
import java.util.Iterator;

/**
 * Implementation of the TemplateManager interface which retrieves templates
 * from the file system.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 1.1
 */

public class FileSystemTemplateManager extends AbstractTemplateManager {
    private static Log log = LogFactory.getLog(FileSystemTemplateManager.class);

    /**
     * The root directory to search for templates.
     */
    protected File root;

    /**
     * A cache of templates.
     */
    protected JPublishCache cache;

    /**
     * Virtual File System provider.
     */
    protected VFSProvider provider;

    /**
     * Get a Template instance from the given path.  If no template can be
     * found then this method will throw a FileNotFoundException.
     *
     * @param path The template path
     * @return The Template
     * @throws Exception Any Exception
     */

    public synchronized Template getTemplate(String path) throws Exception {
        File templateFile = new File(getRoot(), path);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template not found: " + path);
        }

        long lastModified = templateFile.lastModified();
        TemplateCacheEntry cacheEntry = (TemplateCacheEntry) cache.get(path);
        Template template = null;

        if (cacheEntry == null || cacheEntry.getLastModified() != lastModified) {
            template = new Template(siteContext, path);
            loadTemplate(template, path);
            configureTemplate(template, templateFile);
            cache.put(path, new TemplateCacheEntry(template, lastModified));
            template.setLastModified(lastModified);
        }
        if (cacheEntry != null)
            template = cacheEntry.getTemplate();
        return template;
    }

    /**
     * Put the template in the location specified by the given path.
     *
     * @param path     The template path
     * @param template The Template
     * @throws Exception
     */

    public void putTemplate(String path, Template template) throws Exception {
        File templateFile = new File(getRoot(), path);
        saveTemplate(template, templateFile);
    }

    public void removeTemplate(String path) throws Exception {
        File templateFile = new File(getRoot(), path);
        templateFile.delete();
    }

    /**
     * Make the directory for the specified path.  Parent directories
     * will also be created if they do not exist.
     *
     * @param path The directory path
     */

    public void makeDirectory(String path) {
        File file = new File(getRoot(), path);
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
        File file = new File(getRoot(), path);
        if (log.isDebugEnabled())
            log.debug("Deleting file: " + file.getAbsolutePath());
        if (file.isDirectory()) {
            file.delete();
        } else {
            throw new Exception("Path is not a directory: " + path);
        }
    }

    /**
     * Get the root directory for templates.
     *
     * @return The root directory for templates
     */

    public File getRoot() {
        return siteContext.getRealTemplateRoot();
    }

    /**
     * Get an iterator which can be used to iterate through all templates
     * known to the TemplateManager.
     *
     * @return An Iterator of Templates
     * @throws Exception Any Exception
     */

    public Iterator getTemplates() throws Exception {
        return getTemplates(getRoot());
    }

    /**
     * Get an iterator which can be used to iterate through all the
     * templates at or below the specified path.  If the path refers
     * to a file, then the file's parent directory is used.
     *
     * @param path The base path
     * @return An Iterator of Templates
     * @throws Exception Any Exception
     */

    public Iterator getTemplates(String path) throws Exception {
        if (log.isDebugEnabled())
            log.debug("getTemplates(" + path + ")");

        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find path: " + path);
        }

        return getTemplates(file);
    }

    /**
     * Get an iterator which can be used to iterate through all the templates
     * at or below the specified File.
     *
     * @param file The base file
     * @return An Iterator of templates
     * @throws Exception
     */

    protected Iterator getTemplates(File file) throws Exception {
        if (log.isDebugEnabled())
            log.debug("getTemplates(" + file + ")");

        if (!file.isDirectory()) {
            file = file.getParentFile();
        }

        return new FileSystemTemplateIterator(this,
                new BreadthFirstFileTreeIterator(file));
    }

    /**
     * Get the Virtual File System root file.  The Virtual File System
     * provides a datasource-independent way of navigating through all
     * items known to the PageManager.
     *
     * @return The root VFSFile
     * @throws Exception
     */

    public synchronized VFSFile getVFSRoot() throws Exception {
        if (provider == null) {
            provider = new FileSystemProvider(getRoot());
        }
        return provider.getRoot();
    }

    /**
     * Load the specified template's text.
     * <p/>
     * <p><b>Note:</b> Template encoding is currently disabled.  The original
     * encoding mechanism was based on the page path.  However, now that
     * templates will be loaded and cached independent of a request path,
     * this will need to change.  The new encoding system should be based
     * on the template path, not the page path.</p>
     *
     * @param template The Template
     * @param path     to the Template
     * @throws IOException
     */

    protected synchronized void loadTemplate(Template template, String path) throws IOException {
        // construct a new Template
        //String templateEncoding = siteContext.getCharacterEncodingManager().getMap(path).getTemplateEncoding();
        File templateFile = new File(getRoot(), path);
        //Reader reader = new InputStreamReader(new FileInputStream(templateFile), templateEncoding);
        Reader reader = new InputStreamReader(new FileInputStream(templateFile));
        template.setText(FileCopyUtils.copyToString(reader));
    }

    /**
     * Configure the template.
     *
     * @param template The Template
     * @param file     The file for the template
     * @throws Exception Any exception
     * @since 2.0
     */

    protected void configureTemplate(Template template, File file) throws Exception {

        // load additional configuration information

        // note that if there is a template with the ending .xml
        // then this will cause a naming conflict.

        File parentFile = file.getParentFile();
        String name = file.getName();
        String namePart = name.substring(0, name.lastIndexOf("."));
        String configFileName = namePart + ".xml";
        File configFile = new File(parentFile, configFileName);
        if (configFile.exists()) {
            template.loadConfiguration(configFileName, configFile);
        }

    }

    /**
     * Save the template to the specified file location.
     *
     * @param template The Template
     * @param file     The File
     * @throws IOException
     */

    protected synchronized void saveTemplate(Template template, File file)
            throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            writer.print(template.getText());
        } finally {
            IOUtilities.close(writer);
        }
    }


    public void setCache(JPublishCache cache) {
        this.cache = cache;
    }


}
