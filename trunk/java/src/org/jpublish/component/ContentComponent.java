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

package org.jpublish.component;

import org.jpublish.Repository;
import org.jpublish.JPublishContext;

/** JPublish component which renders content.

    @author Anthony Eden
    @since 2.0
*/

public class ContentComponent extends AbstractComponent{
    
    private static final String NAME = "Content";
    private static final String DESCRIPTION = 
        "Display content.";
        
    protected Repository repository;
    
    /** Construct a new ContentComponent. */
    
    public ContentComponent(){
        this.name = NAME;
        this.description = DESCRIPTION;
    }
    
    /** Render the component for the specified path and return the result.
    
        @param path The request path
        @param context The context
        @return The rendered component
        @throws Exception
    */
    
    public String render(String path, JPublishContext context) throws Exception{
        return renderView(getViewRepository().get(path), path, context);
    }
    
}
