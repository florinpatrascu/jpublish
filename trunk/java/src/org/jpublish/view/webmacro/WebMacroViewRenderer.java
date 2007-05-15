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

package org.jpublish.view.webmacro;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.jpublish.JPublishContext;
import org.jpublish.SiteContext;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;
import org.webmacro.Context;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.engine.StreamTemplate;

import java.io.*;

/** ViewRenderer which uses the WebMacro template engine.
    
    @author Anthony Eden
    @since 2.0
*/

public class WebMacroViewRenderer implements ViewRenderer{
    
    private SiteContext siteContext;
    private WebMacro wm;
    
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
        if(siteContext == null){
            throw new Exception("SiteContext must be specified before init()");
        }
        
        wm = new WM();
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
        try{
            Context wmContext = wm.getContext();
            Object[] keys = keys = context.getKeys();
            for(int i = 0; i < keys.length; i++){
                wmContext.put(keys[i], context.get(keys[i].toString()));
            }
            
            // need to specify encoding here?
            StreamTemplate template = new StreamTemplate(wm.getBroker(), in);
            template.setName(path);
            Object result = template.evaluate(wmContext);
            if(result == null){
                throw new Exception("Error evaluating template.");
            } else {
                out.write(result.toString());
            }
        } catch(IOException e){
            throw e;
        } catch(Exception e){
            throw new ViewRenderException(e);
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
        render(context, path, new InputStreamReader(in), 
            new OutputStreamWriter(out));
    }
    
    /** Load the configuration for the view.
    
        @param configuration The configuration object
    */
    
    public void loadConfiguration(Configuration configuration)
    throws ConfigurationException{
        
    }
    
}
