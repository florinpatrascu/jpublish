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

/**
 * 
 *
 * @author Florin T.PATRASCU <me@flop.ca>
 * @since $Version$ (created: Feb 18, 2005 9:49:03 PM)
 */
public class JPublishCacheException extends Exception {
    /**
     * Constructor for JPublishCacheException.
     */
    public JPublishCacheException() {
        super();
    }

    /**
     * Constructor for JPublishCacheException.
     *
     * @param message
     */
    public JPublishCacheException(String message) {
        super(message);
    }

    /**
     * Constructor for JPublishCacheException.
     *
     * @param message
     * @param cause
     */
    public JPublishCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for JPublishCacheException.
     *
     * @param cause
     */
    public JPublishCacheException(Throwable cause) {
        super(cause);
    }
}
