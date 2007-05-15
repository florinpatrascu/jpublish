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

import java.util.Map;

import com.anthonyeden.lib.config.Configuration;

/** Standard interface implement by pluggable JPublish modules.  Modules have access
    to the SiteContext during the init() method and have access to the JPublishContext
    for every request that they will service.

    @author Anthony Eden
*/

public interface JPublishModule{
    
    /** Initialize the module.  This corresponds with the
        Servlet.init() method, except that it includes a reference
        to the JPublish SiteContext.
    
        @param site The SiteContext
        @param configuration The configuration object
        @throws Exception
    */
    
    public void init(SiteContext site, Configuration configuration) throws 
    Exception;
    
    /** Get a map of defined actions.
    
        @return The defined actions
    */
    
    public Map getDefinedActions();
    
    /** Invoked when the module is destroyed.  This corresponds to the
        Servlet.destroy() method.
    */
    
    public void destroy();
    
}
