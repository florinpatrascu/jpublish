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

import org.jpublish.JPublishContext;

import com.anthonyeden.lib.config.Configuration;

/** The Action interface represents a single programmatic action.  Actions
    can be attached to multiple locations within a site.  Please see the
    User's Guide for complete information on Actions.
    
    @author Anthony Eden
*/

public interface Action{
    
    /** Execute the action using the given context.
    
        @param context The current context
        @param configuration The configuration
        @throws Exception Any error
    */
    
    public void execute(JPublishContext context, Configuration configuration) 
    throws Exception;

}
