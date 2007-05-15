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

import java.io.PrintWriter;

/** A base class for runtime exceptions.

    @author Anthony Eden
*/

public class JPublishRuntimeException extends RuntimeException{

    private Throwable t;
    
    /** Construct a JPublishRuntimeException. */

    public JPublishRuntimeException(){
        super();
    }
    
    /** Construct a JPublishRuntimeException.
    
        @param message The error message
    */
    
    public JPublishRuntimeException(String message){
        super(message);
    }
    
    /** Construct a new JPublishRuntimeException with the given message
        and nested exception.
        
        @param message The error message
        @param t The nested exception
    */
    
    public JPublishRuntimeException(String message, Throwable t){
        super(message);
        this.t = t;
    }
    
    /** Print the current stack trace.
    
        @param out The PrintWriter to print to
    */
    
    public void printStackTrace(PrintWriter out){
        if(t != null){
            t.printStackTrace(out);
        }
        super.printStackTrace(out);
    }

}
