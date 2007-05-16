/*-- 

 Copyright (C) 2000-2003 Anthony Eden.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "EdenLib" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact me@anthonyeden.com.
 
 4. Products derived from this software may not be called "EdenLib", nor
    may "EdenLib" appear in their name, without prior written permission
    from Anthony Eden (me@anthonyeden.com).
 
 In addition, I request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 For more information on EdenLib, please see <http://edenlib.sf.net/>.
 
 */

package com.anthonyeden.lib.config;

import com.anthonyeden.lib.ChainedException;
import com.anthonyeden.lib.util.MessageUtilities;

/** 
 * Exception thrown when there is an error while reading, writing or
 * manipulating Configurations.
 *  
 * @author Anthony Eden
 */

public class ConfigurationException extends ChainedException {
    
    private static final String MESSAGE_PACKAGE = "com.anthonyeden.lib";
    
    private Configuration configuration;
    
    /** 
     * Construct a ConfigurationException with the given message.
     * 
     * @param message The message
     */
    
    public ConfigurationException(String message) {
        this(message, null, null);
    }
    
    /**
     * Construct a ConfigurationException with the given message.
     *
     * @param t The Throwable
     */
    
    public ConfigurationException(Throwable t) {
        this(t.getMessage(), t);
    }
    
    /** 
     * Construct a ConfigurationException with the given message.
     * 
     * @param message The message
     */
    
    public ConfigurationException(String message, Throwable t) {
        this(message, t, null);
    }
    
    /** 
     * Construct a ConfigurationException with the given message.
     * 
     * @param message The message
     * @param configuration The Configuration object
     */
    
    public ConfigurationException(String message, Configuration configuration) {
        this(message, null, configuration);
    }
    
    /** 
     * Construct a ConfigurationException with the given nested
     * error.  The message of the nested error will be used as this
     * exception's message.
     *  
     * @param t The nested error
     * @param configuration The Configuration object
     */
    
    public ConfigurationException(Throwable t, Configuration configuration) {
        this(t.getMessage(), t, configuration);
    }
    
    /** 
     * Construct a ConfigurationException with the given message
     * and nested error.
     *  
     * @param message The message
     * @param t The nested error
     * @param configuration The Configuration
     */
    
    public ConfigurationException(String message, Throwable t, 
    Configuration configuration) {
        super(message, t);
        this.configuration = configuration;
    }
    
    /**
     * Overrides the Throwable.getMessage() method and adds additional 
     * configuration location information if it is available.
     *
     * @return The message
     */
    
    public String getMessage() {
        String message = super.getMessage();
        if (configuration != null) {
            Location location = configuration.getLocation();
            if (location != null) {
                Object[] args = {location.getSourceId(), 
                    String.valueOf(location.getLineNumber()), 
                    String.valueOf(location.getColumnNumber())};
                return message + MessageUtilities.getMessage(getClass(), 
                    MESSAGE_PACKAGE, "location", args);
            }
        }
        return message;
    }

}
