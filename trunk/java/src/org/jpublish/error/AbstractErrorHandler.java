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

import com.anthonyeden.lib.config.Configuration;

import org.jpublish.ErrorHandler;

/** Abstract base implementation of the ErrorHandler interface.  Concrete 
    implementations can override the methods as they need.
    
    @author Anthony Eden
*/

public abstract class AbstractErrorHandler implements ErrorHandler{
    
    /** Configure the error handler.  This implementation is a no-op.
        Subclasses which need configuration should override this method.
    
        @param configuration The configuration object
    */
    
    public void configure(Configuration configuration){
    }
    
}
