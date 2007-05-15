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

package org.jpublish.page;

import org.jpublish.JPublishException;

/** Exception which is thrown when a Page cannot be found.

    @author Anthony Eden
    @since 1.1
*/

public class PageNotFoundException extends JPublishException{

    /** Construct a PageNotFoundException. */

    public PageNotFoundException(){
        super();
    }
    
    /** Construct a PageNotFoundException.
    
        @param message The error message
    */
    
    public PageNotFoundException(String message){
        super(message);
    }
    
    /** Construct a new PageNotFoundException with the given message
        and nested exception.
        
        @param message The error message
        @param t The nested exception
    */
    
    public PageNotFoundException(String message, Throwable t){
        super(message, t);
    }
    
}
