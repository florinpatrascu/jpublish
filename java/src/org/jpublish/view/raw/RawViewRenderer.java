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

package org.jpublish.view.raw;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.SiteContext;
import org.jpublish.JPublishContext;
import org.jpublish.view.ViewRenderer;
import org.jpublish.view.ViewRenderException;

/** ViewRenderer which renders the data directly to the output stream with
    no template engine.
    
    @author Anthony Eden
    @since 2.0
*/

public class RawViewRenderer implements ViewRenderer{
    
    private SiteContext siteContext;
    
    /** Set the SiteContext.
    
        @param siteContext The SiteContext
    */
    
    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Initialize the ViewRenderer.
    
        @throws Exception Any Exception
    */
    
    public void init() throws Exception{
        
    }
    
    /** Render the view.
    
        @param context The JPublishContext
        @param path The path to the template
        @param in The Reader to read view template from
        @param out The Writer to write the rendered view
        @throws IOException 
        @throws ViewRenderException
    */
    
    public void render(JPublishContext context, String path, Reader in, 
    Writer out) throws IOException, ViewRenderException{
        int c = -1;
        while((c = in.read()) != -1){
            out.write((char)c);
        }
    }
    
    /** Render the view.
    
        @param context The JPublishContext
        @param path The path to the template
        @param in The InputStream to read view template from
        @param out The OutputStream to write the rendered view
        @throws IOException 
        @throws ViewRenderException
    */
    
    public void render(JPublishContext context, String path, InputStream in, 
    OutputStream out) throws IOException, ViewRenderException{
        int c = -1;
        while((c = in.read()) != -1){
            out.write((char)c);
        }
    }
    
    /** Load the configuration for the view.
    
        @param configuration The configuration object
    */
    
    public void loadConfiguration(Configuration configuration)
    throws ConfigurationException{
        
    }
    
}
