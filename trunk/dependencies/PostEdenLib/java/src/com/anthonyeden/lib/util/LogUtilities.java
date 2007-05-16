/*-- 

 Copyright (C) 2001 Anthony Eden.
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

package com.anthonyeden.lib.util;

import org.apache.commons.logging.Log;

import java.util.Iterator;
import java.util.Map;


/**
 * A utility class to assist with extended logging.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class LogUtilities {

    private LogUtilities() {
        // no op
    }

    /**
     * Standard debugging.
     *
     * @param log     The Logger to log to
     * @param message The message
     * @deprecated Use <code>debug(Log log, String message)</code>
     */

    public static void debug(Log log, String message) {
        log.debug(message);
    }


    /**
     * Extended debugging.  The name/value pairs in the data object will
     * be appended to the log message.
     *
     * @param log     The Log to log to
     * @param message The message
     * @param data    Any additional data to log
     */

    public static void debug(Log log, String message, Map data) {
        log.debug(generateMessage(message, data));
    }

    /**
     * Generate a single String which combines the log message with the
     * given data.
     *
     * @param message The message
     * @param data    The data
     * @return The complete message String
     */

    private static String generateMessage(String message, Map data) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(message);

        Iterator keys = data.keySet().iterator();
        if (keys.hasNext()) {
            buffer.append(" [");

            while (keys.hasNext()) {
                Object key = keys.next();
                Object value = data.get(key);

                buffer.append(key.toString());
                buffer.append("=");
                buffer.append(value.toString());

                if (keys.hasNext()) {
                    buffer.append(",");
                }
            }

            buffer.append("]");
        }

        return buffer.toString();
    }

}