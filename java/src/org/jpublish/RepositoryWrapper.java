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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A wrapper around any repository.  This wrapper is used to expose the
 * Repository to the view renderer.  The current context is stored when
 * the wrapper is created which means that the context does not have to
 * be passed to the <code>get()</code> methods at request-time.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class RepositoryWrapper {
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
     * @throws Exception
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
     * @throws Exception
     */

    public OutputStream getOutputStream(String path) throws Exception {
        return repository.getOutputStream(path);
    }

}
