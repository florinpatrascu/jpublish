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

/** Exception which is thrown if there is an error mergine a template with
    a page.
    
    @author Anthony Eden
    @since 1.1
*/

public class TemplateMergeException extends JPublishException{

    /** Construct a TemplateMergeException. */

    public TemplateMergeException(){
        super();
    }
    
    /** Construct a TemplateMergeException.
    
        @param message The error message
    */
    
    public TemplateMergeException(String message){
        super(message);
    }
    
    /** Construct a new TemplateMergeException with the given message
        and nested exception.
        
        @param message The error message
        @param t The nested exception
    */
    
    public TemplateMergeException(String message, Throwable t){
        super(message, t);
    }
    
}
