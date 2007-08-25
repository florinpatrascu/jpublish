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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * A wrapper around any repository.  This wrapper is used to expose the
 * Repository to the view renderer.  The current context is stored when
 * the wrapper is created which means that the context does not have to
 * be passed to the <code>get()</code> methods at request-time. Wrapping it
 * in a Map for making easier the integration with modern template engines
 * such as: StringTemplate. This is the discussion thread that made me decide
 * about the implementation:
 * http://hardlikesoftware.com/weblog/2007/06/25/thoughts-on-stringtemplate-part-2/#comment-1498
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class RepositoryWrapper implements Map {
    public static final String EMPTY_STRING = "";
    private static final Log log = LogFactory.getLog(RepositoryWrapper.class);

    private Repository repository;
    private JPublishContext context;

    /**
     * Construct a new RepositoryWrapper over the given repository using
     * the given context for merging.
     *
     * @param repository The repository
     * @param context    The current context
     */
    public RepositoryWrapper(Repository repository, JPublishContext context) {
        this.repository = repository;
        this.context = context;
    }


    /**
     * Get the content at the given path.  Errors will be caught and the
     * error message will be returned
     * in place of the content.
     *
     * @param path The content path
     * @return The content as a String
     */
    public Object get(Object path) {
        return this.get((String) path);
    }

    /**
     * Get the content at the given path.  The content will be merged
     * with the associated context.
     *
     * @param path The content path
     * @return The content as a String
     */
    public String get(String path) {
        return get(path, true);
    }

    /**
     * Get the content at the given path, optinally merging it with the
     * associated context.  If merge is true then merging will
     * occur.  Errors will be caught and the error message will be returned
     * in place of the content.
     *
     * @param path   The content path
     * @param merged True to merge
     * @return The content as a String
     */
    public String get(String path, boolean merged) {
        try {
            if (merged)
                return repository.get(path, context);
            else
                return repository.get(path);

        } catch (FileNotFoundException e) {
            // we really don't need to print the stack trace here
            log.warn("Page not found: " + path);
            return e.getMessage();

        } catch (Exception e) {
            //e.printStackTrace();
            String err = path + ", error: " + e.getMessage();
            log.error(err);
            return err;
        }
    }

    /**
     * Get the given content as an InputStream.  The InputStream will
     * return the raw content data.  Implementations should not attempt
     * to pass the data through the view renderer.
     *
     * @param path The path to the content
     * @return The InputStream
     * @throws Exception if errors occur during the acquisition of the inputstream
     */
    public InputStream getInputStream(String path) throws Exception {
        return repository.getInputStream(path);
    }

    /**
     * Get an OutputStream for writing content to the given path.
     * Repository implementations are not required to implement this
     * method if they provide read-only access.
     *
     * @param path The path to the content
     * @return The OutputStream
     * @throws Exception if errors occur during the acquisition of the outputstream
     */
    public OutputStream getOutputStream(String path) throws Exception {
        return repository.getOutputStream(path);
    }

    /**
     * @return the number of files/directories available in the repository path
     */
    public int size() {
        File file = repository.pathToFile(EMPTY_STRING);
        int size = 0;

        if (file.isDirectory()) {
            size = file.list().length;
        }
        return size;
    }

    /**
     * @return true if the repository contains no files/directories
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * check if the repository contains the file/directory at the given path
     *
     * @param path the path to the content
     * @return true if the file/directory is found, false otherwise
     */
    public boolean containsKey(Object path) {
        return repository.pathToFile((String) path) != null;
    }

    /**
     * todo: don't know yet the best behavior for this one.
     *
     * @param object ideally the pre-rendered content of a page. Tough one.
     * @return false, for now
     */
    public boolean containsValue(Object object) {
        //can't say :'(
        //NYI
        return false;
    }


    public Object put(Object key, Object value) {
        //NYI
        return null;
    }

    /**
     * if implemented (uncommented), this will delete
     * the content at the specified path!
     *
     * @param path the path to the file or the directory that will be removed
     * @return null
     */
    public Object remove(Object path) {
        //try {
        // are you sure???
        //ok then:
        //todo: check if is file or directory, then
        //repository.remove((String) key);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        log.warn("Are you sure you want to remove: " + path + "?????");
        return null;
    }

    public void putAll(Map map) {
        //NYI
    }

    public void clear() {
        //NYI
    }

    /**
     * Finds all the file/directories names in the repository
     *
     * @return a Set containing the file/directory names in the wrapped repository
     */
    public Set keySet() {
        File file = repository.pathToFile(EMPTY_STRING);
        Set keys = null;
        try {
            String[] filesInRepository = file.list();
            if (filesInRepository != null && filesInRepository.length > 0) {
                keys = new HashSet(Arrays.asList(filesInRepository));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public Collection values() {
        return null;
    }

    public Set entrySet() {
        return null;
    }
}