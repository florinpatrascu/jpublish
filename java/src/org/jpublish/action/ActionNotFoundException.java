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

package org.jpublish.action;

import org.jpublish.JPublishRuntimeException;

/** Runtime exception indicating the a named action cannot be found.

    @author Anthony Eden
    @since 2.0
*/

public class ActionNotFoundException extends JPublishRuntimeException{
    
    private String actionName;        
    
    /** Construct a new ActionNotFoundException.
    
        @param actionName The action name
    */
    
    public ActionNotFoundException(String actionName){
        this(null, null, actionName);
    }
    
    /** Construct a new ActionNotFoundException.
    
        @param message The message
        @param actionName The action name
    */
    
    public ActionNotFoundException(String message, String actionName){
        this(message, null, actionName);
    }
    
    /** Construct a new ActionNotFoundException.
    
        @param t The error
        @param actionName The action name
    */
    
    public ActionNotFoundException(Throwable t, String actionName){
        this(null, t, actionName);
    }
    
    /** Construct a new ActionNotFoundException.
    
        @param message The message
        @param t The error
        @param actionName The action name
    */
    
    public ActionNotFoundException(String message, Throwable t, String actionName){
        super(message+" action:["+actionName+"]", t);
        this.actionName = actionName;
    }
    
    /** Get the action name.
    
        @return The action name
    */
    
    public String getActionName(){
        return actionName;
    }
    
}
