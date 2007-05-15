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

package org.jpublish.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.Content;
import org.jpublish.Template;

import java.io.*;

/**
 * An implementation of the Content interface which provides the data of
 * a Template object.
 *
 * @author Anthony Eden
 * @since 2.0
 */

public class TemplateContent implements Content {

    private static final Log log = LogFactory.getLog(TemplateContent.class);

    private Template template;
    private long lastFlush = 0;

    /**
     * Construct a new TemplateContent object.
     *
     * @param template The template
     */

    public TemplateContent(Template template) {
        this.template = template;
    }

    /**
     * Get an InputStream for reading the content data.
     *
     * @return The content InputStream
     */

    public InputStream getInputStream() {
        return new ByteArrayInputStream(template.getText().getBytes());
    }

    /**
     * Get the template reader.
     *
     * @return The template reader
     */

    public Reader getReader() {
        return getReader(null);
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
        this.lastFlush = lastFlush;
    }

    /**
     * Get the template reader using the specified encoding.
     *
     * @param The content encoding
     * @return The Reader
     */

    public Reader getReader(String encoding) {
        if (encoding == null) {
            return new StringReader(template.getText());
        } else {
            try {
                return new StringReader(
                        new String(template.getText().getBytes(), encoding));
            } catch (UnsupportedEncodingException e) {
                log.warn("Unsupported encoding " + encoding +
                        "; using default encoding");
                return new StringReader(template.getText());
            }
        }
    }

    /**
     * Get the last modified time of the template.
     *
     * @return The last modified time
     */

    public long getLastModified() {
        return template.getLastModified();
    }

    /**
     * Return true if the specified object equals this object.
     *
     * @param obj The object to compare to
     * @return True if the objects are equal
     */

    public boolean equals(Object obj) {
        Content passed = (Content) obj;
        long thisLastModified = this.getLastModified();
        long passedLastModified = passed.getLastModified();
        if (thisLastModified == passedLastModified) {
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Passed content not equal: thisLastModified=" +
                    thisLastModified + ", passedLastModified=" + passedLastModified);
        }
        return false;
    }
}
