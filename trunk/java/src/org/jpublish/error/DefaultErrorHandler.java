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

package org.jpublish.error;

import org.jpublish.JPublishError;

/** Default implementation of the error handler.  This implementation prints
    a stack trace and does not consume the error.

    @author Anthony Eden
*/

public class DefaultErrorHandler extends AbstractErrorHandler{
    
    /** Handle the error.  This implementation prints a stack trace and does 
        not consume the error.
        
        @param error The error
    */
    
    public void handleError(JPublishError error){
        error.getError().printStackTrace();
    }
    
}
