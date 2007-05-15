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

package org.jpublish.view.velocity;

import org.apache.velocity.context.Context;

import org.jpublish.JPublishContext;

/** Context object which can be passed to the Velocity engine for merging.

    @author Anthony Eden
    @since 2.0
*/

public class VelocityViewContext implements Context{
    
    private JPublishContext context;
    
    /** Construct a new VelocityViewContext which wraps the given
        ViewContext.
        
        @param context The ViewContext
    */
    
    public VelocityViewContext(JPublishContext context){
        this.context = context;
    }
    
    public Object put(String key, Object value){
        return context.put(key, value);
    }
    
    public Object get(String key){
        return context.get(key);
    }
    
    public Object remove(Object key){
        return context.remove(key.toString());
    }
    
    public boolean containsKey(Object key){
        return context.containsKey(key.toString());
    }
    
    public Object[] getKeys(){
        return context.getKeys();
    }
    
}
