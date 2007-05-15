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

package org.jpublish.view.freemarker;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateCollectionModel;

import org.jpublish.JPublishContext;

/** Implementation of a TemplateModel which wraps the JPublishContext.

    @author Anthony Eden
    @since 2.0
*/

public class FreeMarkerViewContext implements TemplateHashModelEx {
    
    private JPublishContext context;
    
    /** Construct a new FreeMarkerViewContext.
    
        @param context The wrapped JPublishContext
    */

    public FreeMarkerViewContext(JPublishContext context){
        this.context = context;
    }
    
    /** Get the number of elements in the context.
    
        @return The number of elements in the context
    */

    public int size() {
        return context.getKeys().length;
    }
    
    /** Return true if the context is empty.
    
        @return True if the context is empty
    */

    public boolean isEmpty() {
        return size() == 0;
    }
    
    /** Return the TemplateModel for the specified key.
    
        @param key The key
        @return The TemplateModel
        @throws TemplateModelException
    */

    public TemplateModel get(String key) throws TemplateModelException {
        return wrapAsTemplateModel(context.get(key));
    }
    
    /** Return a collection representing all keys in the context.
    
        @return The collection of keys
        @throws TemplateModelException
    */

    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel) wrapAsTemplateModel(context.getKeys());
    }

    /** Return a collection representing all values in the context.
    
        @return The collection of values
        @throws TemplateModelException
    */

    public TemplateCollectionModel values() throws TemplateModelException {
        Object[] arr = context.getKeys();
        arr = (Object[]) arr.clone();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = context.get(arr[i].toString());
        }
        return (TemplateCollectionModel) wrapAsTemplateModel(arr);
    }
    
    /** Wrap the specified object in a TemplateModel implementation.
    
        @param obj The object
        @return The TemplateModel wrapper
        @throws TemplateModelException
    */

    private TemplateModel wrapAsTemplateModel(Object obj) throws TemplateModelException {
        return ObjectWrapper.BEANS_WRAPPER.wrap(obj);
    }
    
}
