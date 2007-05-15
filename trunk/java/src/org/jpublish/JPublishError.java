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

/** Class which encapsulates Throwable errors which are sent to registered
    ErrorHandlers.  ErrorHandlers can consume an error message which will
    indicate that a ServletException should NOT be thrown once all error
    handlers have completed.
    
    @author Anthony Eden
*/

public class JPublishError{
    
    private Throwable error;
    private boolean consumed;
    private JPublishContext context;
    
    /** Construct a JPublishError with the given Throwable.
    
        @param error The Throwable
    */
    
    public JPublishError(Throwable error){
        this(error, null);
    }
    
    /** Construct a JPublishError with the given Throwable and context.
    
        @param error The error
        @param context The current JPublishContext
    */
    
    public JPublishError(Throwable error, JPublishContext context){
        this.error = error;
        this.context = context;
    }
    
    /** Get the encapsulated error.
    
        @return The error
    */
    
    public Throwable getError(){
        return error;
    }
    
    /** Return true if this error is consumed.  If this method returns true then
        a ServletException should NOT be thrown upon completion of all error 
        handlers.
        
        @return True if this error is consumed
    */
    
    public boolean isConsumed(){
        return consumed;
    }
    
    /** Set to true to mark the error as consumed.
    
        @param consumed True if error is consumed
    */
    
    public void setConsumed(boolean consumed){
        this.consumed = consumed;
    }
    
    /** Get a snapshot of the context which was taken when the error occured.
        This method may return null if the context is not available at the time
        the error occurred.
    
        @return The context
    */
    
    public JPublishContext getContext(){
        return context;
    }
    
}
