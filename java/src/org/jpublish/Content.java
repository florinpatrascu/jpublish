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

import java.io.Reader;
import java.io.InputStream;
import java.io.Serializable;

/** Interface which represents a single item of content.  This interface
    is read-only.

    @author Anthony Eden
    @since 2.0
*/

public interface Content extends Serializable {

    /** Get the last-modified time of the content or -1 if it is not known.

        @return The last modified time
    */

    public long getLastModified();

    /**
     * last time this cache was flushed
     * @return long
     */
    public long getLastFlush();

    /**
     * set time when last flush occured
     * @param lastFlush
     */
    public void setLastFlush(long lastFlush);

    /** Get an InputStream for reading the content data.

        @return The content InputStream
    */

    public InputStream getInputStream();

    /** Get a Reader for reading the content data.

        @return The content Reader
    */

    public Reader getReader();

    /** Get a Reader for readint the content data using the specified content
        encoding.

        @param encoding The content encoding
        @return The Reader
    */

    public Reader getReader(String encoding);


}
