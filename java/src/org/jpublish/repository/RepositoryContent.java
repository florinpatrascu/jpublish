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

package org.jpublish.repository;

import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.Content;
import org.jpublish.Repository;

import java.io.*;

/**
 * Interface which represents a single item of content.  This interface
 * is read-only.
 *
 * @author Anthony Eden
 * @author Florin T.PATRASCU
 * @since 2.0
 */

public class RepositoryContent implements Content {

    private static final Log log = LogFactory.getLog(RepositoryContent.class);

    private Repository repository = null;
    private String path = null;
    private String data = null;
    private long lastModified = -1;
    private long lastFlush = 0;

    /**
     * Construct a new RepositoryContent object.
     *
     * @param repository The Repository used to load the content
     * @param path       The path to the content in the repository
     */

    public RepositoryContent(Repository repository, String path) {
        this.repository = repository;
        this.path = path;
        this.data = null;
        this.lastModified = -1;
    }

    /**
     * Construct a new RepositoryContent object.
     *
     * @param data         The data
     * @param lastModified The last modified time
     */

    public RepositoryContent(String data, long lastModified) {
        this.data = data;
        this.lastModified = lastModified;
    }

    /**
     * Get the last-modified time of the content or -1 if it is not known.
     *
     * @return The last modified time
     */

    public long getLastModified() {
        this.snapLastModified(false);
        return lastModified;
    }

    /**
     * last time this cache was flushed
     *
     * @return long
     */
    public long getLastFlush() {
        return this.lastFlush;
    }

    /**
     * set time when last flush occured
     *
     * @param lastFlush
     */
    public void setLastFlush(long lastFlush) {
       this.lastFlush=lastFlush;
    }

    private void snapLastModified(boolean forceFromRepository) {
        if ((forceFromRepository || this.lastModified == -1) &&
                this.repository != null) {
            try {
                this.lastModified = this.repository.getLastModified(this.path);
            } catch (Exception e) {
                log.warn("Could not getLastModified time from repository " +
                        "(returning -1): " + e.toString());
            }
        }
    }

    /**
     * Get an InputStream for reading the content data.
     *
     * @return The content InputStream
     */

    public InputStream getInputStream() {
        //doing an actual read, set the lastModified time
        this.snapLastModified(true);

        if (this.repository != null) {
            try {
                return this.repository.getInputStream(this.path);
            } catch (Exception e) {
                log.warn("Could not getInputStream from repository " +
                        "(returning null): " + e.toString());
                return null;
            }
        }
        return new ByteArrayInputStream(data.getBytes());
    }

    /**
     * Get a Reader for reading the content data.
     *
     * @return The content Reader
     */

    public Reader getReader() {
        return getReader(null);
    }

    /**
     * Get a Reader for reading the content data with the specified content
     * encoding.
     *
     * @param encoding The content encoding
     * @return The Reader
     */

    public Reader getReader(String encoding) {
        //doing an actual read, set the lastModified time
        this.snapLastModified(true);

        // this code bothers me because it is not clear about what is happening
        // to the repInputStream instance if an error occurs 
        InputStream repInputStream = null;
        if (repository != null) {
            try {
                repInputStream = repository.getInputStream(path);
            } catch (Exception e) {
                log.warn("Could not getInputStream from repository " +
                        "(returning null): " + e.toString());
                IOUtilities.close(repInputStream);
                return null;
            }
        }

        if (encoding == null) {
            if (repository != null) {
                return new InputStreamReader(repInputStream);
            }
            IOUtilities.close(repInputStream);
            return new StringReader(data);
        } else {
            try {
                if (repository != null) {
                    return new InputStreamReader(repInputStream, encoding);
                } else {
                    IOUtilities.close(repInputStream);
                    return new StringReader(new String(data.getBytes(),
                            encoding));
                }
            } catch (UnsupportedEncodingException e) {
                log.warn("Unsupported encoding " + encoding +
                        "; using default encoding");
                if (repository != null) {
                    return new InputStreamReader(repInputStream);
                } else {
                    IOUtilities.close(repInputStream);
                    return new StringReader(data);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        Content passed = (Content) obj;
        long thisLastModified = this.getLastModified();
        long passedLastModified = passed.getLastModified();
        if (thisLastModified == passedLastModified) {
            return true;
        }
        if (log.isDebugEnabled())
            log.debug("Passed content not equal: thisLastModified=" +
                    thisLastModified + ", passedLastModified=" + passedLastModified);
        return false;
    }
}
