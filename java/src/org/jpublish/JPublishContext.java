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
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.jpublish.util.CharacterEncodingMap;
import org.jpublish.util.DateUtilities;
import org.jpublish.util.NumberUtilities;
import org.jpublish.util.URLUtilities;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The JPublishContext overrides the Velocity context to add name
 * checking as a security measure so that code cannot replace
 * standard variables.
 *
 * @author Anthony Eden
 */

public class JPublishContext extends VelocityContext {
    public static final String JPUBLISH_STOP_PROCESSING = "stop-processing";


    /**
     * names used to store objects in the JPublish context
     */
    public static final String JPUBLISH_REQUEST = "request";
    public static final String JPUBLISH_RESPONSE = "response";
    public static final String JPUBLISH_SESSION = "session";
    public static final String JPUBLISH_APPLICATION = "application";
    public static final String JPUBLISH_CHARACTER_ENCODING_MAP = "characterEncodingMap";
    public static final String JPUBLISH_URL_UTILITIES = "urlUtilities";
    public static final String JPUBLISH_DATE_UTILITIES = "dateUtilities";
    public static final String JPUBLISH_NUMBER_UTILITIES = "numberUtilities";
    public static final String JPUBLISH_SYSLOG = "syslog";
    public static final String JPUBLISH_SITE = "site";
    public static final String JPUBLISH_PAGE = "page";

    /**
     * List of reserved names.
     */
    private static final String[] reservedNames = {
            "request", "response", "session", "application",
            "page", "site", "components"
    };

    private Object owner;
    private boolean checkReservedNames = false;

    /**
     * Construct a new JPublishContext.  The owner object is used
     * to enable and disable modification of protected keys.
     *
     * @param owner The context "owner" object
     */

    public JPublishContext(Object owner) {
        this.owner = owner;
    }

    /**
     * Construct a new JPublishContext.  The owner object is used
     * to enable and disable modification of protected keys.
     *
     * @param inner The inner context
     * @param owner The context "owner" object
     */

    public JPublishContext(Context inner, Object owner) {
        super(inner);
        this.owner = owner;
    }

    /**
     * Get the value for the specified key.  If the value is not
     * in the context then this method returns null.
     *
     * @param key The key
     * @return The value or null
     */

    public Object get(String key) {
        return super.get(key);
    }


    /**
     * Get the stop-processing lag.  This method will return null unless request processing should be stopped.
     *
     * @return The stop processing flag or null
     */
    public String getStopProcessing() {
        return (String) get(JPUBLISH_STOP_PROCESSING);
    }

    /**
     * Set the stop-processing flag.  Any non-null String value will be considered "true" indicating that processing
     * should stop.
     *
     * @param stopProcessing The stop processing flag
     */
    public void setStopProcessing(String stopProcessing) {
        put(JPUBLISH_STOP_PROCESSING, stopProcessing);
    }

    public void setStopProcessing() {
        put(JPUBLISH_STOP_PROCESSING, JPUBLISH_STOP_PROCESSING);
    }


    /**
     * Put the given value into the context.  If the key cannot be
     * inserted into the context because it conflicts with a reserved
     * name then a ReservedNameException will be thrown.
     *
     * @param key   The key
     * @param value The value
     * @return The previous value or null if there was no previous
     *         value
     */

    public Object put(String key, Object value) {
        if (checkReservedNames && (isReservedName(key))) {
            throw new ReservedNameException(key);
        }

        return super.put(key, value);
    }

    /**
     * Remove the given value from the context. If the key cannot be
     * removed from the context because it is a reserved name then a
     * ReservedNameException will be thrown.
     *
     * @param key The key
     * @return The removed value or null if there was no removed
     *         value
     */

    public Object remove(String key) {
        if (checkReservedNames && isReservedName(key)) {
            throw new ReservedNameException(key);
        }

        return super.remove(key);
    }

    /**
     * Enable the reserved name check so that external code cannot
     * overwrite JPublish defined variables.
     *
     * @param owner The owner of the context.  Only the owner can
     *              enable or disable the check.
     */

    public final void enableCheckReservedNames(Object owner) {
        if (owner.equals(this.owner)) {
            checkReservedNames = true;
        }
    }

    /**
     * Disable the reserved name check so that external code can
     * overwrite JPublish defined variables.
     *
     * @param owner The owner of the context.  Only the owner can
     *              enable or disable the check.
     */

    public final void disableCheckReservedNames(Object owner) {
        if (owner.equals(this.owner)) {
            checkReservedNames = false;
        }
    }

    /*  Convenience methods for getting objects stored in the 
        JPublish context
    */

    /**
     * Get the current HTTP-request object
     *
     * @return The HTTP servlet request or null if not available
     */

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) get(JPUBLISH_REQUEST);
    }

    /**
     * Get the current HTTP-response object
     *
     * @return The HTTP servlet response or null if not available
     */

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) get(JPUBLISH_RESPONSE);
    }

    /**
     * Get the current HTTP session object
     *
     * @return The HTTP session or null if not available
     */

    public HttpSession getSession() {
        return (HttpSession) get(JPUBLISH_SESSION);
    }

    /**
     * Get the current servlet context
     *
     * @return The servlet context or null if not available
     */

    public ServletContext getApplication() {
        return (ServletContext) get(JPUBLISH_APPLICATION);
    }

    /**
     * Get the JPublish character encoding map
     *
     * @return The character encoding map or null if not available
     */

    public CharacterEncodingMap getCharacterEncodingMap() {
        return (CharacterEncodingMap) get(JPUBLISH_CHARACTER_ENCODING_MAP);
    }

    /**
     * Get the JPublish URL utilities
     *
     * @return The URL utilities or null if not available
     */

    public URLUtilities getUrlUtilities() {
        return (URLUtilities) get(JPUBLISH_URL_UTILITIES);
    }

    /**
     * Get the JPublish date utilities
     *
     * @return The date utilities or null if not available
     */

    public DateUtilities getDateUtilities() {
        return (DateUtilities) get(JPUBLISH_DATE_UTILITIES);
    }

    /**
     * Get the JPublish number utilities
     *
     * @return The number utilities or null if not available
     */

    public NumberUtilities getNumberUtilities() {
        return (NumberUtilities) get(JPUBLISH_NUMBER_UTILITIES);
    }

    /**
     * Get the JPublish syslog.
     *
     * @return The syslog or null if not available
     */

    public Log getSyslog() {
        return (Log) get(JPUBLISH_SYSLOG);
    }

    /**
     * Get the current JPublish page.
     *
     * @return The current page or null if not available
     */

    public Page getPage() {
        return (Page) get(JPUBLISH_PAGE);
    }

    /**
     * Return true if the given variable is a reserved name
     * used by the JPublish engine. If checkReservedNames is
     * true then this method must be called before inserting
     * a value into the context.
     *
     * @param name The name to check
     * @return True if the name is a reserved name
     */

    private static boolean isReservedName(String name) {
        for (int i = 0; i < reservedNames.length; i++) {
            if (name.equals(reservedNames[i])) {
                return true;
            }
        }
        return false;
    }

}
